package org.alfresco.solr.tracker;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreTrackerJob implements Job
{
    protected final static Logger log = LoggerFactory.getLogger(CoreTrackerJob.class);

    public CoreTrackerJob()
    {
        super();

    }

    /*
     * (non-Javadoc)
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException
    {
        CoreTracker coreTracker = (CoreTracker) jec.getJobDetail().getJobDataMap().get("TRACKER");
        coreTracker.updateIndex();
    }
}