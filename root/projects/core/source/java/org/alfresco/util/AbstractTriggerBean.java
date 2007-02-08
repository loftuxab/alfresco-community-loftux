/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.alfresco.util;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.quartz.JobDetailAwareTrigger;

/**
 * A utility bean to wrap sceduling a job with a scheduler.
 * 
 * @author Andy Hind
 */
public abstract class AbstractTriggerBean implements InitializingBean, JobDetailAwareTrigger, BeanNameAware
{

    private static Log s_logger = LogFactory.getLog(AbstractTriggerBean.class);

    private JobDetail jobDetail;

    private Scheduler scheduler;

    private String beanName;

    public AbstractTriggerBean()
    {
        super();
    }

    /**
     * Get the definition of the job to run.
     */
    public JobDetail getJobDetail()
    {
        return jobDetail;
    }

    /**
     * Set the definition of the job to run.
     * 
     * @param jobDetail
     */
    public void setJobDetail(JobDetail jobDetail)
    {
        this.jobDetail = jobDetail;
    }

    /**
     * Get the scheduler with which the job and trigger are scheduled.
     * 
     * @return
     */
    public Scheduler getScheduler()
    {
        return scheduler;
    }

    /**
     * Set the scheduler.
     * 
     * @param scheduler
     */
    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }

    /**
     * Set the scheduler
     */
    public void afterPropertiesSet() throws Exception
    {
        // Check properties are set
        if (jobDetail == null)
        {
            throw new AlfrescoRuntimeException("Job detail has not been set");
        }
        if (scheduler == null)
        {
            s_logger.warn("Job " + getBeanName() + " is not active");
        }
        else
        {
            s_logger.info("Job " + getBeanName() + " is active");
            // Register the job with the scheduler
            Trigger trigger = getTrigger();
            scheduler.scheduleJob(jobDetail, trigger);
        }

    }

    /**
     * Abstract method for implementations to build their trigger.
     * 
     * @return
     * @throws Exception
     */
    public abstract Trigger getTrigger() throws Exception;

    /**
     * Get the bean name as this trigger is created
     */
    public void setBeanName(String name)
    {
        this.beanName = name;
    }

    /**
     * Get the bean/trigger name.
     * 
     * @return
     */
    public String getBeanName()
    {
        return beanName;
    }

}
