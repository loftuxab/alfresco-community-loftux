/*
 * Copyright 2013-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.Date;

import org.alfresco.repo.security.sync.ChainingUserRegistrySynchronizerStatus;

public class SyncMonitorMBean implements SyncMonitor
{
    
    private ChainingUserRegistrySynchronizerStatus monitor;
    SyncMonitorMBean(ChainingUserRegistrySynchronizerStatus monitor)
    {
        this.monitor = monitor;
    }    
        
    @Override
    public Date getSyncStartTime()
    {
        return monitor.getSyncStartTime();
    }

    @Override
    public Date getSyncEndTime()
    {
        return monitor.getSyncEndTime();
    }

    @Override
    public String getLastErrorMessage()
    {
        return monitor.getLastErrorMessage();
    }

    @Override
    public String getLastRunOnServer()
    {
       return monitor.getLastRunOnServer();
    }
    
    @Override
    public String getSynchronizationStatus()
    {
        return monitor.getSynchronizationStatus();
    }

}
