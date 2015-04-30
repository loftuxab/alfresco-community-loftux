/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.HashMap;
import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.repo.batch.BatchMonitorEvent;
import org.alfresco.repo.security.sync.ChainingUserRegistrySynchronizerStatus;
import org.alfresco.repo.security.sync.SynchronizeStartEvent;
import org.alfresco.service.descriptor.DescriptorService;



/**
 * Exports {@link SyncMonitorMBean} in response to
 * {@link SyncStartEvent
 * 
 * @author mrogers
 */
public class SyncMonitorExporter extends AbstractManagedResourceExporter<SynchronizeStartEvent>
{
    public SyncMonitorExporter()
    {
        super(SynchronizeStartEvent.class);      
    }

    @Override
    public Map<ObjectName, ?> getObjectsToExport(SynchronizeStartEvent event)
            throws MalformedObjectNameException
    {
        Map<ObjectName, Object> objectMap = new HashMap<ObjectName, Object>(7);
        objectMap.put(new ObjectName("Alfresco:Name=BatchJobs,Type=Synchronization,Category=manager"), 
                new SyncMonitorMBean((ChainingUserRegistrySynchronizerStatus)event.getSource()));

        return objectMap;
    }
}
