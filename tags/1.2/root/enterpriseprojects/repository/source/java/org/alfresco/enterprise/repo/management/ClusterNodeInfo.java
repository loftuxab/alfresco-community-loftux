/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.alfresco.enterprise.repo.cluster.checker.ClusterChecker;
import org.alfresco.enterprise.repo.cluster.checker.ClusterChecker.NodeInfo;
import org.alfresco.enterprise.repo.cluster.checker.ClusterChecker.PeerNodeInfo;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @since Odin
 *
 */
@ManagedResource
public class ClusterNodeInfo implements ClusterNodeInfoMBean
{
    private static final String PeerNodeId = "Peer Node Id";
    private static final String PeerNodeAddress = "Peer Node Address";
    private static final String PeerNodeStatus = "Peer Node Status";

    private static String[] keyAttributes = {PeerNodeId};
    private static String[] attributeKeys = {PeerNodeId, PeerNodeAddress, PeerNodeStatus};
    private static String[] attributeDescriptions = {PeerNodeId, PeerNodeAddress, PeerNodeStatus};
    private static OpenType<?>[] attributeTypes = {SimpleType.STRING, SimpleType.STRING, SimpleType.STRING};

    private static CompositeType compositeType;
    private static TabularType tabularType;
    
    private static CompositeType getCompositeType() throws OpenDataException
    {
        if(compositeType == null)
        {
            compositeType = new CompositeType("Cluster Nodes", "Cluster Nodes", attributeKeys, attributeDescriptions, attributeTypes);
        }
        return compositeType;
    }

    private static TabularType getTabularType() throws OpenDataException
    {
        if(tabularType == null)
        {
            tabularType = new TabularType("Cluster Nodes", "Cluster Nodes", getCompositeType(), keyAttributes);
        }
        return tabularType;
    }
    
    private ClusterChecker clusterChecker;
    private String nodeId;
    
    public ClusterNodeInfo(ClusterChecker clusterChecker, String nodeId)
    {
        this.clusterChecker = clusterChecker;
        this.nodeId = nodeId;
    }

    @ManagedAttribute(description = "Cluster node id")
    public String getId()
    {
        return nodeId;
    }

    @ManagedAttribute(description = "Cluster node address")
    public String getAddress()
    {
        NodeInfo nodeInfo = clusterChecker.getNodeInfo(nodeId);
        return (nodeInfo != null ? nodeInfo.getIPAddress() : "Unknown");
    }
    
    @ManagedAttribute(description = "Cluster nodes this node knows about")
    public TabularData getPeers() throws OpenDataException
    {
        List<PeerNodeInfo> peerNodes = clusterChecker.getPeers(nodeId);

        CompositeType compositeType = ClusterNodeInfo.getCompositeType();
        TabularType tType = ClusterNodeInfo.getTabularType();
        TabularDataSupport table = new TabularDataSupport(tType);

        for(PeerNodeInfo peerNodeInfo : peerNodes)
        {
            Map<String, String> values = new HashMap<String, String>();
            values.put(PeerNodeId, peerNodeInfo.getPeerId());
            values.put(PeerNodeAddress, peerNodeInfo.getPeerAddress());
            values.put(PeerNodeStatus, peerNodeInfo.getPeerStatus().toString());
            CompositeDataSupport row = new CompositeDataSupport(compositeType, values);
            table.put(row);
        }

        return table;
    }
}
