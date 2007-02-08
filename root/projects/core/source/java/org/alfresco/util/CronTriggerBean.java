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
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.Trigger;

/**
 * A utility bean to wrap scheduling a cron job with a given scheduler.
 * 
 * @author Andy Hind
 */
public class CronTriggerBean extends AbstractTriggerBean 
       
{
    /*
     * The cron expression to trigger execution.
     */
    String cronExpression;

    /**
     * Default constructor
     * 
     */
    public CronTriggerBean()
    {
        super();
    }

    /**
     * Get the cron expression that determines when this job is run.
     * 
     * @return
     */
    public String getCronExpression()
    {
        return cronExpression;
    }

    /**
     * Set the cron expression that determines when this job is run.
     * 
     * @param cronExpression
     */
    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    /**
     * Build the cron trigger
     * 
     * @return
     * @throws Exception
     */
    public Trigger getTrigger() throws Exception
    {
        Trigger trigger = new CronTrigger(getBeanName(), Scheduler.DEFAULT_GROUP, getCronExpression());
        return trigger;
    }

    public void afterPropertiesSet() throws Exception
    {
        if ((cronExpression == null) || (cronExpression.trim().length() == 0))
        {
            throw new AlfrescoRuntimeException(
                    "The cron expression has not been set, is zero length, or is all white space");
        }
        super.afterPropertiesSet();
    }
}
