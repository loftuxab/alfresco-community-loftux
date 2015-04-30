/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.Date;

/**
 * Management interface for CachedContentCleaner resources.
 * 
 * @author Matt Ward
 */
public interface CachedContentCleanerMBean
{
    boolean isRunning();
    
    long getNumFilesSeen();
    
    long getNumFilesDeleted();
    
    double getSizeFilesDeletedMB();
    
    long getNumFilesMarked();
    
    Date getTimeStarted();
    
    Date getTimeFinished();
    
    long getDurationMillis();
    
    long getDurationSeconds();
}
