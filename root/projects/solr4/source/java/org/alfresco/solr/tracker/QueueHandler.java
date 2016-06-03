package org.alfresco.solr.tracker;

public interface QueueHandler
{
    void removeFromQueueAndProdHead(AbstractWorkerRunnable job);
}
