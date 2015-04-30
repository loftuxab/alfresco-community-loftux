/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * This class is the trigger for the Premise Sync Job which polls for local 
 * unsynced changes.
 * 
 * @author mrogers
 * @since CloudSync
 */
public class OnPremiseSyncPushJob implements Job
{
    private static final Log log = LogFactory.getLog(OnPremiseSyncPushJob.class);
    
    @Override public void execute(JobExecutionContext context) throws JobExecutionException
    {
        if (log.isTraceEnabled()) 
        { 
            log.trace("PUSH: Starting " + OnPremiseSyncPushJob.class.getSimpleName());
        }
        
        final SyncTrackerComponent syncTrackerComponent = getRequiredQuartzJobParameter(context, "syncTrackerComponent", SyncTrackerComponent.class);
        
        syncTrackerComponent.push();
    }
    
    private <T> T getRequiredQuartzJobParameter(JobExecutionContext context, String dataKey, Class<T> requiredClass) throws JobExecutionException
    {
        @SuppressWarnings("unchecked")
        final T result = (T) context.getJobDetail().getJobDataMap().get(dataKey);
        if (result == null)
        {
            if (log.isErrorEnabled())
            {
                log.error("PUSH: Did not retrieve required service for quartz job: " + dataKey);
            }
            throw new JobExecutionException("Missing job data: " + dataKey);
        }
        return result;
    }
}
