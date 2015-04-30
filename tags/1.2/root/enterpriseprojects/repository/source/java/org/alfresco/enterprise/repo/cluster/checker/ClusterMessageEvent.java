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
public class ClusterMessageEvent extends ClusterEvent
{
    private static final long serialVersionUID = -8677530378696271077L;

    private String sourceId;
    private String targetId;
    
    public ClusterMessageEvent(ClusterChecker clusterChecker, String sourceId, String targetId)
    {
        super(clusterChecker);
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    public String getSourceId()
    {
        return sourceId;
    }

    public String getTargetId()
    {
        return targetId;
    }
    
}
