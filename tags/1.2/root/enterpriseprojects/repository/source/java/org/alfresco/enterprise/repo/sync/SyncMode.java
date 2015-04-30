package org.alfresco.enterprise.repo.sync;

public enum SyncMode 
{
    /**
     * Sync is enabled in CLOUD MODE
     */
    CLOUD,
    
    /**
     * Sync is enabled in ON_PREMISE mode
     */
    ON_PREMISE,
    
    /**
     * Sync is not enabled
     */
    OFF
}
