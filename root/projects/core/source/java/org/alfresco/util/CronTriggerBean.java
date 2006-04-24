/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
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
