/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.Notification;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.alfresco.enterprise.repo.cluster.checker.ClusterChecker;
import org.alfresco.enterprise.repo.cluster.checker.ClusterChecker.MemberPair;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;

/**
 * 
 * @since Odin
 *
 */
@ManagedResource
public class ClusterInfo implements ClusterInfoMBean, NotificationPublisherAware
{
    private static final String Node1 = "Node 1";
    private static final String Node2 = "Node 2";
    private static final String PairWorking = "Node Pair Working?";

    private static String[] keyAttributes = {Node1, Node2, PairWorking};
    private static String[] attributeKeys = {Node1, Node2, PairWorking};
    private static String[] attributeDescriptions = {Node1, Node2, PairWorking};
    private static OpenType<?>[] attributeTypes = {SimpleType.STRING, SimpleType.STRING, SimpleType.BOOLEAN};

    private static CompositeType compositeType;
    private static TabularType tabularType;
    
    private static CompositeType getCompositeType() throws OpenDataException
    {
        if(compositeType == null)
        {
            compositeType = new CompositeType("Cluster Pairs", "Cluster Pairs", attributeKeys, attributeDescriptions, attributeTypes);
        }
        return compositeType;
    }

    private static TabularType getTabularType() throws OpenDataException
    {
        if(tabularType == null)
        {
            tabularType = new TabularType("Cluster Pairs", "Cluster Pairs", getCompositeType(), keyAttributes);
        }
        return tabularType;
    }

    private ClusterChecker clusterChecker;
    private AtomicLong seq = new AtomicLong(0);

    private NotificationPublisher notificationPublisher;
    
    public ClusterInfo(ClusterChecker clusterChecker)
    {
        this.clusterChecker = clusterChecker;
    }

    @ManagedAttribute(description = "Number of nodes found in the cluster by the cluster check tool")
    public int getNumNodesFoundInCluster()
    {
        return clusterChecker.getNodeInfo().size();
    }
    
    @ManagedOperation(description = "Stop checking the cluster node")
    @ManagedOperationParameters({ @ManagedOperationParameter(name = "nodeId", description = "The node id of the cluster node to stop tracking")
    })
    public void stopChecking(String nodeId)
    {
        clusterChecker.stopChecking(nodeId);
    }

    @ManagedAttribute(description = "Cluster member information in pairs with their working status")
    public TabularData getClusterMemberPairs() throws OpenDataException
    {
        Set<MemberPair> nodePairs = clusterChecker.getPeerDetails();
        
        CompositeType compositeType = ClusterInfo.getCompositeType();
        TabularType tType = ClusterInfo.getTabularType();
        TabularDataSupport table = new TabularDataSupport(tType);
        
        for(MemberPair pair : nodePairs)
        {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put(Node1, pair.getFirst());
            values.put(Node2, pair.getSecond());
            values.put(PairWorking, pair.isWorking());
            CompositeDataSupport row = new CompositeDataSupport(compositeType, values);
            table.put(row);
        }

        return table;
    }

    @ManagedOperation(description = "Initiate cluster check")
    @ManagedOperationParameters({})
    public void checkCluster()
    {
    	ClassLoader backup = Thread.currentThread().getContextClassLoader();
    	try
    	{
			// Hack: override context class loader (which seems to be set to system class loader for JMX threads,
	    	// causing resource lookup to fail)
	    	Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

	    	clusterChecker.check();
    	}
    	finally
    	{
    		Thread.currentThread().setContextClassLoader(backup);
    	}
    }

    public void sendNotification(String type, String message, String address)
    {
        Notification notification = new Notification(type, this, seq.longValue(), ((new Date()).getTime()), message);
        seq.incrementAndGet();
        notificationPublisher.sendNotification(notification);
    }
    
    @Override
    public void setNotificationPublisher(NotificationPublisher notificationPublisher)
    {
        this.notificationPublisher = notificationPublisher;
    }
}
