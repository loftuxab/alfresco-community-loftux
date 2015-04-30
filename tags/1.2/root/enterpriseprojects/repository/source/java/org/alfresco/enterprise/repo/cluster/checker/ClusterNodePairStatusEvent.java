/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.checker;

import org.alfresco.enterprise.repo.cluster.checker.ClusterChecker.NodeStatus;


/**
 * 
 * @Odin
 *
 */
public class ClusterNodePairStatusEvent extends ClusterEvent
{
    private static final long serialVersionUID = -4045195741687097066L;
    public static final String NOTIFICATION_TYPE = "Cluster Node Pair Status";

    private String sourceNodeId;
    private String targetNodeId;
    private NodeStatus status;

    public ClusterNodePairStatusEvent(ClusterChecker clusterChecker, String sourceNodeId, String targetNodeId, NodeStatus status)
    {
        super(clusterChecker);
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.status = status;

    }

    public String getSourceNodeId()
    {
        return sourceNodeId;
    }
    
    public String getTargetNodeId()
    {
        return targetNodeId;
    }

    public NodeStatus getStatus()
    {
        return status;
    }
    
}