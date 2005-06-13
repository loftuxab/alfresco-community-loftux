package org.alfresco.repo.content;

import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.debug.CodeMonkey;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Removes all content form the store that is not referenced
 * by any content node.
 * 
 * @author Derek Hulley
 */
public class ContentStoreCleanupJob implements Job
{
    public ContentStoreCleanupJob()
    {
    }

    /**
     * Gets all content URLs from the store, checks if it is in use by any node
     * and deletes those that aren't.
     */
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        // extract the content store to use
        Object contentStoreObj = context.getJobDetail().getJobDataMap().get("contentStore");
        if (contentStoreObj == null || !(contentStoreObj instanceof ContentStore))
        {
            throw new AlfrescoRuntimeException("Job data must contain valid 'contentStore' reference");
        }
        ContentStore contentStore = (ContentStore) contentStoreObj;
        // extract the search to use
        Object searcherObj = context.getJobDetail().getJobDataMap().get("searcher");
        if (searcherObj == null || !(searcherObj instanceof SearchService))
        {
            throw new AlfrescoRuntimeException("Job data must contain valid 'searcher' reference");
        }
        SearchService searcher = (SearchService) searcherObj;
        
        // get all URLs in the store
        List<String> contentUrls = contentStore.listUrls();
        for (String contentUrl : contentUrls)
        {
            // search for it
            CodeMonkey.issue("Sort out where the content URLs will be stored in the new model");  // TODO
            // perform the search
            // delete if necessary
            // continue;
        }
        throw new UnsupportedOperationException();
    }
}
