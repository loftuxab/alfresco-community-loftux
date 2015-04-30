/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.Date;

/**
 * Adapts a batch monitor into a full-blown MBean.
 */
public class BatchMonitor implements BatchMonitorMBean
{

    /** The wrapped monitor. */
    private final org.alfresco.repo.batch.BatchMonitor wrapped;

    /**
     * Instantiates a new batch monitor.
     * 
     * @param wrapped
     *            the wrapped monitor
     */
    public BatchMonitor(org.alfresco.repo.batch.BatchMonitor wrapped)
    {
        this.wrapped = wrapped;
    }

    public String getCurrentEntryId()
    {
        return wrapped.getCurrentEntryId();
    }

    public Date getEndTime()
    {
        return wrapped.getEndTime();
    }

    public String getLastError()
    {
        return wrapped.getLastError();
    }

    public String getLastErrorEntryId()
    {
        return wrapped.getLastErrorEntryId();
    }

    public String getPercentComplete()
    {
        return wrapped.getPercentComplete();
    }

    public String getProcessName()
    {
        return wrapped.getProcessName();
    }

    public Date getStartTime()
    {
        return wrapped.getStartTime();
    }

    public int getSuccessfullyProcessedEntries()
    {
        return wrapped.getSuccessfullyProcessedEntries();
    }

    public int getTotalErrors()
    {
        return wrapped.getTotalErrors();
    }

    public int getTotalResults()
    {
        return wrapped.getTotalResults();
    }
}
