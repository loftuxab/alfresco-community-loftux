/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.Date;

/**
 * Header bean for the Sync Job
 * 
 * @author mrogers
 */
public interface SyncMonitor
{
    /**
     * Get the date/time of the last sync
     * @return date/time of last sync or null
     */
    Date getSyncStartTime();
    
    /**
     * Get the date/time of the last sync
     * @return date/time of last sync or null
     */
    Date getSyncEndTime();
    
    /**
     * Get the last error message
     * @return the last error message - blank if no error
     */
    String getLastErrorMessage();
    
    /**
     * Last run on server
     * @return last run on server
     */
    String getLastRunOnServer();
    
    /**
     * get the synchronization status
     * @return the synchronization status
     */
    String getSynchronizationStatus();

}
