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
 * CachedContentCleanerMBean implementation.
 * 
 * @author Matt Ward
 */
public class CachedContentCleaner implements CachedContentCleanerMBean
{
    private org.alfresco.repo.content.caching.cleanup.CachedContentCleaner cleaner;
    
    
    /**
     * @param cleaner
     */
    public CachedContentCleaner(org.alfresco.repo.content.caching.cleanup.CachedContentCleaner cleaner)
    {
        this.cleaner = cleaner;
    }

    @Override
    public boolean isRunning()
    {
        return cleaner.isRunning();
    }

    @Override
    public long getNumFilesSeen()
    {
        return cleaner.getNumFilesSeen();
    }

    @Override
    public long getNumFilesDeleted()
    {
        return cleaner.getNumFilesDeleted();
    }

    @Override
    public double getSizeFilesDeletedMB()
    {
        return cleaner.getSizeFilesDeletedMB();
    }

    @Override
    public long getNumFilesMarked()
    {
        return cleaner.getNumFilesMarked();
    }

    @Override
    public Date getTimeStarted()
    {
        return cleaner.getTimeStarted();
    }

    @Override
    public Date getTimeFinished()
    {
        return cleaner.getTimeFinished();
    }

    @Override
    public long getDurationMillis()
    {
        return cleaner.getDurationMillis();
    }

    @Override
    public long getDurationSeconds()
    {
        return cleaner.getDurationSeconds();
    }
}
