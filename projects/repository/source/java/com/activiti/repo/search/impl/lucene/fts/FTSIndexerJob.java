/*
 * Created on 18-Apr-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl.lucene.fts;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public class FTSIndexerJob implements Job
{
    public FTSIndexerJob()
    {
        super();
    }

    public void execute(JobExecutionContext executionContext) throws JobExecutionException
    {

        FullTextSearchIndexer indexer = (FullTextSearchIndexer)executionContext.getJobDetail().getJobDataMap().get("bean");
        if(indexer != null)
        {
           indexer.index();
        }

    }

   

}
