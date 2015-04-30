/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.Collections;
import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.repo.batch.BatchMonitorEvent;

/**
 * Exports {@link BatchMonitorMBean} instances in response to {@link BatchMonitorEvent}s.
 * 
 * @author dward
 */
public class BatchMonitorExporter extends AbstractManagedResourceExporter<BatchMonitorEvent>
{

    /**
     * The Constructor.
     */
    public BatchMonitorExporter()
    {
        super(BatchMonitorEvent.class);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.management.AbstractManagedResourceExporter#getObjectsToExport(org.springframework.context.
     * ApplicationEvent)
     */
    @Override
    public Map<ObjectName, ?> getObjectsToExport(BatchMonitorEvent event) throws MalformedObjectNameException
    {
        // Adapt the source monitor to an MBean
        BatchMonitor batchMonitor = new BatchMonitor(event.getBatchMonitor());
        return Collections.singletonMap(
                new ObjectName("Alfresco:Name=BatchJobs,Type=" + batchMonitor.getProcessName()), batchMonitor);
    }
}
