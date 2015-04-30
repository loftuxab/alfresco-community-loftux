/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
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

import org.alfresco.enterprise.repo.cluster.core.ClusterService;
import org.alfresco.enterprise.repo.cluster.core.ClusterServiceInitialisedEvent;
import org.alfresco.service.transaction.TransactionService;

/**
 * Responds to {@link ClusterServiceInitialisedEvent} events, exporting {@link ClusterAdmin} MBeans.
 * 
 * @author Matt Ward
 */
public class ClusterAdminExporter extends AbstractManagedResourceExporter<ClusterServiceInitialisedEvent>
{
    private TransactionService transactionService;
    
    public ClusterAdminExporter()
    {
        super(ClusterServiceInitialisedEvent.class);
    }

    @Override
    public Map<ObjectName, ?> getObjectsToExport(ClusterServiceInitialisedEvent event)
                throws MalformedObjectNameException
    {
        ClusterAdmin clusterAdmin = new ClusterAdmin();
        ClusterService clusterService = event.getClusterService();
        clusterAdmin.setClusterService(clusterService);
        clusterAdmin.setTransactionService(transactionService);
        return Collections.singletonMap(new ObjectName("Alfresco:Name=Cluster,Tool=Admin"), clusterAdmin);
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
}
