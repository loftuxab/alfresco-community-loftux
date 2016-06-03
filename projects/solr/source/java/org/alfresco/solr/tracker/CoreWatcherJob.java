package org.alfresco.solr.tracker;

import org.alfresco.solr.AlfrescoCoreAdminHandler;
import org.alfresco.solr.LegacySolrInformationServer;
import org.apache.solr.core.SolrCore;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreWatcherJob implements Job
{
    protected final static Logger log = LoggerFactory.getLogger(CoreWatcherJob.class);
    
    
    public CoreWatcherJob()
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
        AlfrescoCoreAdminHandler adminHandler = (AlfrescoCoreAdminHandler) jec.getJobDetail().getJobDataMap().get("ADMIN_HANDLER");

        for (SolrCore core : adminHandler.getCoreContainer().getCores())
        {

            if (!adminHandler.getTrackers().containsKey(core.getName()))
            {
                if (core.getSolrConfig().getBool("alfresco/track", false))
                {
                    log.info("Starting to track " + core.getName());
                    
                    // Create information server and wire it up.  This will be done by a registry
                    LegacySolrInformationServer srv = new LegacySolrInformationServer(adminHandler, core);
                    adminHandler.getInformationServers().put(core.getName(), srv);
                    adminHandler.getTrackers().put(core.getName(), srv.getTracker());
                }
            }
        }
    }
    

}