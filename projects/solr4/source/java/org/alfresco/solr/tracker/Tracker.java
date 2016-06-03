package org.alfresco.solr.tracker;

import org.alfresco.solr.TrackerState;

public interface Tracker
{
    void track();

    String getAlfrescoVersion();
    
    void setShutdown(boolean shutdown);
    void close();

    TrackerState getTrackerState();
}
