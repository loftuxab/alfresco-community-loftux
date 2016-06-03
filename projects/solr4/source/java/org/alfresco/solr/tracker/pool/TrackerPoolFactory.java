package org.alfresco.solr.tracker.pool;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Create a thread pool for the {@link org.alfresco.solr.tracker.Tracker}s.
 * 
 * @author Matt Ward
 */
public interface TrackerPoolFactory
{
    ThreadPoolExecutor create();
}
