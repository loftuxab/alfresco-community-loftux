/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.scheduler;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.management.JMException;
import javax.management.ObjectName;

import org.alfresco.repo.management.DynamicMBeanExporter;
import org.alfresco.repo.scheduler.SchedulerAware;
import org.quartz.Calendar;
import org.quartz.CronTrigger;
import org.quartz.JobPersistenceException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.core.SchedulingContext;
import org.quartz.simpl.RAMJobStore;
import org.springframework.jmx.support.JmxUtils;

/**
 * A quartz scheduler job store that exports all its triggers through JMX and exposes an <code>executeNow</code>
 * operation on each.
 * 
 * @author dward
 */
public class MonitoredRAMJobStore extends RAMJobStore implements SchedulerAware
{
    /** The JMX exporter. */
    private final DynamicMBeanExporter exporter;

    /** The monitored triggers. */
    private final Map<String, Map<String, ObjectName>> monitoredTriggers = new TreeMap<String, Map<String, ObjectName>>();

    /** The owning scheduler. */
    private Scheduler scheduler;

    public MonitoredRAMJobStore()
    {
        // We have no access to the spring container, so have to self-contain the MBean stuff we need
        this.exporter = new DynamicMBeanExporter();

        this.exporter.afterPropertiesSet();

    }

    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }

    /**
     * <p>
     * Store the given <code>{@link org.quartz.Trigger}</code>.
     * </p>
     * 
     * @param newTrigger
     *            The <code>Trigger</code> to be stored.
     * @param replaceExisting
     *            If <code>true</code>, any <code>Trigger</code> existing in the <code>JobStore</code> with the same
     *            name & group should be over-written.
     * @param ctxt
     *            the ctxt
     * @throws ObjectAlreadyExistsException
     *             if a <code>Trigger</code> with the same name/group already exists, and replaceExisting is set to
     *             false.
     * @throws JobPersistenceException
     *             the job persistence exception
     * @see #pauseTriggerGroup(SchedulingContext, String)
     */
    @Override
    public void storeTrigger(SchedulingContext ctxt, Trigger newTrigger, boolean replaceExisting)
            throws JobPersistenceException
    {
        synchronized (this.lock)
        {
            super.storeTrigger(ctxt, newTrigger, replaceExisting);

            Map<String, ObjectName> groupMonitoredTriggers = this.monitoredTriggers.get(newTrigger.getGroup());
            if (groupMonitoredTriggers == null)
            {
                groupMonitoredTriggers = new TreeMap<String, ObjectName>();
                this.monitoredTriggers.put(newTrigger.getGroup(), groupMonitoredTriggers);
            }
            MonitoredTrigger mt = newMonitoredTrigger(ctxt, newTrigger.getName(), newTrigger.getGroup());
            ObjectName objectName = null;
            try
            {
                StringBuilder objName = new StringBuilder();
                objName.append("Alfresco:Name=Schedule,Group=")
                       .append(newTrigger.getGroup())
                       .append(",Type=")
                       .append(mt.getClass().getSimpleName())
                       .append(",Trigger=")
                       .append(newTrigger.getName().replace(':', '|')); // note: replace ':' (eg. in case of nodeRef value - see also ContentStoreExporter)
                
                objectName = new ObjectName(objName.toString());
                this.exporter.registerMBean(mt, objectName);
            }
            catch (JMException e)
            {
                throw new RuntimeException(e);
            }
            groupMonitoredTriggers.put(newTrigger.getName(), objectName);
        }
    }

    /**
     * <p>
     * Remove (delete) the <code>{@link org.quartz.Trigger}</code> with the given name.
     * </p>
     * 
     * @param triggerName
     *            The name of the <code>Trigger</code> to be removed.
     * @param groupName
     *            The group name of the <code>Trigger</code> to be removed.
     * @param ctxt
     *            the ctxt
     * @return <code>true</code> if a <code>Trigger</code> with the given name & group was found and removed from the
     *         store.
     */
    @Override
    public boolean removeTrigger(SchedulingContext ctxt, String triggerName, String groupName)
    {
        synchronized (this.lock)
        {
            boolean retVal = super.removeTrigger(ctxt, triggerName, groupName);
            Map<String, ObjectName> groupMonitoredTriggers = this.monitoredTriggers.get(groupName);
            if (groupMonitoredTriggers == null)
            {
                return retVal;
            }
            ObjectName objectName = groupMonitoredTriggers.remove(triggerName);
            if (objectName == null)
            {
                return retVal;
            }
            this.exporter.unregisterMBean(objectName);
            return retVal;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.simpl.RAMJobStore#shutdown()
     */
    @Override
    public void shutdown()
    {
        super.shutdown();
        this.exporter.destroy();
    }

    /** Textual names for state constants. */
    private static final String[] STATE_NAMES =
    {
        "NORMAL", "PAUSED", "COMPLETE", "ERROR", "BLOCKED"
    };

    /**
     * Constructs an appropriate subtype of monitored trigger that can be exported as an MBean.
     * 
     * @param ctxt
     *            the ctxt
     * @param triggerName
     *            the trigger name
     * @param groupName
     *            the group name
     * @return the monitored trigger
     */
    public MonitoredTrigger newMonitoredTrigger(SchedulingContext ctxt, String triggerName, String groupName)
    {
        Trigger trigger = retrieveTrigger(ctxt, triggerName, groupName);
        if (trigger instanceof CronTrigger)
        {
            return new MonitoredCronTrigger(ctxt, triggerName, groupName);
        }
        else if (trigger instanceof SimpleTrigger)
        {
            return new MonitoredSimpleTrigger(ctxt, triggerName, groupName);
        }
        else
        {
            return new MonitoredTrigger(ctxt, triggerName, groupName);
        }
    }

    /**
     * A Bean that exposes generic trigger properties for monitoring.
     */
    public class MonitoredTrigger
    {
        /** The ctxt. */
        private final SchedulingContext ctxt;

        /** The trigger name. */
        private final String triggerName;

        /** The group name. */
        private final String groupName;

        /**
         * The Constructor.
         * 
         * @param ctxt
         *            the ctxt
         * @param triggerName
         *            the trigger name
         * @param groupName
         *            the group name
         */
        protected MonitoredTrigger(SchedulingContext ctxt, String triggerName, String groupName)
        {
            this.ctxt = ctxt;
            this.triggerName = triggerName;
            this.groupName = groupName;
        }

        /**
         * Gets the trigger.
         * 
         * @return the trigger
         */
        protected Trigger getTrigger()
        {
            return retrieveTrigger(this.ctxt, this.triggerName, this.groupName);
        }

        /**
         * Manually invokes the trigger (designed to be called via JMX)
         * 
         * @throws SchedulerException
         */
        public void executeNow() throws SchedulerException
        {
            Trigger trigger = getTrigger();
            MonitoredRAMJobStore.this.scheduler.triggerJobWithVolatileTrigger(trigger.getJobName(), trigger
                    .getJobGroup(), trigger.getJobDataMap());
        }

        /**
         * <p>
         * Get the name of this <code>Trigger</code>.
         * </p>
         * 
         * @return the name
         */
        public String getName()
        {
            return this.triggerName;
        }

        /**
         * <p>
         * Get the group of this <code>Trigger</code>.
         * </p>
         * 
         * @return the group
         */
        public String getGroup()
        {
            return this.groupName;
        }

        /**
         * Gets the state.
         * 
         * @return the state
         */
        public String getState()
        {
            try
            {
                int state = getTriggerState(this.ctxt, this.triggerName, this.groupName);
                return state == Trigger.STATE_NONE ? "NONE" : MonitoredRAMJobStore.STATE_NAMES[state];
            }
            catch (JobPersistenceException e)
            {
                throw new RuntimeException(e);
            }
        }

        /**
         * <p>
         * Get the name of the associated <code>{@link org.quartz.JobDetail}</code>.
         * </p>
         * 
         * @return the job name
         */
        public String getJobName()
        {
            return getTrigger().getJobName();
        }

        /**
         * <p>
         * Get the name of the associated <code>{@link org.quartz.JobDetail}</code>'s group.
         * </p>
         * 
         * @return the job group
         */
        public String getJobGroup()
        {
            return getTrigger().getJobGroup();
        }

        /**
         * <p>
         * Return the description given to the <code>Trigger</code> instance by its creator (if any).
         * </p>
         * 
         * @return null if no description was set.
         */
        public String getDescription()
        {
            return getTrigger().getDescription();
        }

        /**
         * <p>
         * Get the name of the <code>{@link Calendar}</code> associated with this Trigger.
         * </p>
         * 
         * @return <code>null</code> if there is no associated Calendar.
         */
        public String getCalendarName()
        {
            return getTrigger().getCalendarName();
        }

        /**
         * <p>
         * Whether or not the <code>Trigger</code> should be persisted in the
         * <code>{@link org.quartz.spi.JobStore}</code> for re-use after program restarts.
         * </p>
         * <p>
         * If not explicitly set, the default value is <code>false</code>.
         * </p>
         * 
         * @return <code>true</code> if the <code>Trigger</code> should be garbage collected along with the
         *         <code>{@link Scheduler}</code>.
         */
        public boolean isVolatile()
        {
            return getTrigger().isVolatile();
        }

        /**
         * The priority of a <code>Trigger</code> acts as a tiebreaker such that if two <code>Trigger</code>s have the
         * same scheduled fire time, then the one with the higher priority will get first access to a worker thread.
         * <p>
         * If not explicitly set, the default value is <code>5</code>.
         * </p>
         * 
         * @return the priority
         * @see #DEFAULT_PRIORITY
         */
        public int getPriority()
        {
            return getTrigger().getPriority();
        }

        /**
         * <p>
         * Used by the <code>{@link Scheduler}</code> to determine whether or not it is possible for this
         * <code>Trigger</code> to fire again.
         * </p>
         * <p>
         * If the returned value is <code>false</code> then the <code>Scheduler</code> may remove the
         * <code>Trigger</code> from the <code>{@link org.quartz.spi.JobStore}</code>.
         * </p>
         * 
         * @return the may fire again
         */
        public boolean getMayFireAgain()
        {
            return getTrigger().mayFireAgain();
        }

        /**
         * <p>
         * Get the time at which the <code>Trigger</code> should occur.
         * </p>
         * 
         * @return the start time
         */
        public Date getStartTime()
        {
            return getTrigger().getStartTime();
        }

        /**
         * <p>
         * Get the time at which the <code>Trigger</code> should quit repeating - even if an assigned 'repeatCount'
         * isn't yet satisfied.
         * </p>
         * 
         * @return the end time
         * @see #getFinalFireTime()
         */
        public Date getEndTime()
        {
            return getTrigger().getEndTime();
        }

        /**
         * <p>
         * Returns the next time at which the <code>Trigger</code> will fire. If the trigger will not fire again,
         * <code>null</code> will be returned. The value returned is not guaranteed to be valid until after the
         * <code>Trigger</code> has been added to the scheduler.
         * </p>
         * 
         * @return the next fire time
         */
        public Date getNextFireTime()
        {
            return getTrigger().getNextFireTime();
        }

        /**
         * <p>
         * Returns the previous time at which the <code>Trigger</code> will fire. If the trigger has not yet fired,
         * <code>null</code> will be returned.
         * 
         * @return the previous fire time
         */
        public Date getPreviousFireTime()
        {
            return getTrigger().getPreviousFireTime();
        }

        /**
         * <p>
         * Returns the last time at which the <code>Trigger</code> will fire, if the Trigger will repeat indefinitely,
         * null will be returned.
         * </p>
         * <p>
         * Note that the return time *may* be in the past.
         * </p>
         * 
         * @return the final fire time
         */
        public Date getFinalFireTime()
        {
            return getTrigger().getFinalFireTime();
        }
    }

    /**
     * Exposes CronTrigger properties for monitoring
     */
    public class MonitoredCronTrigger extends MonitoredTrigger
    {

        /**
         * The Constructor.
         * 
         * @param ctxt
         *            the ctxt
         * @param triggerName
         *            the trigger name
         * @param groupName
         *            the group name
         */
        public MonitoredCronTrigger(SchedulingContext ctxt, String triggerName, String groupName)
        {
            super(ctxt, triggerName, groupName);
        }

        /**
         * Gets the time zone.
         * 
         * @return the time zone
         */
        public String getTimeZone()
        {
            return ((CronTrigger) getTrigger()).getTimeZone().getDisplayName();
        }

        /**
         * Gets the cron expression.
         * 
         * @return the cron expression
         */
        public String getCronExpression()
        {
            return ((CronTrigger) getTrigger()).getCronExpression();
        }

    }

    /**
     * Exposes SimpleTrigger properties for monitoring.
     */
    public class MonitoredSimpleTrigger extends MonitoredTrigger
    {

        /**
         * The Constructor.
         * 
         * @param ctxt
         *            the ctxt
         * @param triggerName
         *            the trigger name
         * @param groupName
         *            the group name
         */
        public MonitoredSimpleTrigger(SchedulingContext ctxt, String triggerName, String groupName)
        {
            super(ctxt, triggerName, groupName);
        }

        /**
         * Gets the repeat count.
         * 
         * @return the repeat count
         */
        public int getRepeatCount()
        {
            return ((SimpleTrigger) getTrigger()).getRepeatCount();
        }

        /**
         * Gets the repeat interval.
         * 
         * @return the repeat interval
         */
        public long getRepeatInterval()
        {
            return ((SimpleTrigger) getTrigger()).getRepeatInterval();
        }

        /**
         * Gets the times triggered.
         * 
         * @return the times triggered
         */
        public long getTimesTriggered()
        {
            return ((SimpleTrigger) getTrigger()).getTimesTriggered();
        }

    }
}
