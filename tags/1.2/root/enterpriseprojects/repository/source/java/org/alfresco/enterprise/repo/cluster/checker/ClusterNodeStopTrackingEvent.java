/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.checker;


/**
 * 
 * @since Odin
 *
 */
public class ClusterNodeStopTrackingEvent extends ClusterEvent
{
    private static final long serialVersionUID = -116885933025872510L;

    public static final String NOTIFICATION_TYPE = "Cluster Node Stop Tracking";

    private String nodeId;

    public ClusterNodeStopTrackingEvent(ClusterChecker clusterChecker, String nodeId)
    {
        super(clusterChecker);
        this.nodeId = nodeId;

    }

    public String getNodeId()
    {
        return nodeId;
    }

}
