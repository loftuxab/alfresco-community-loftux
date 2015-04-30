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
public class ClusterCheckEvent extends ClusterMessageEvent
{
    private static final long serialVersionUID = -4633842466757526069L;

    public ClusterCheckEvent(ClusterChecker clusterChecker, String sourceId, String targetId)
    {
        super(clusterChecker, sourceId, targetId);
    }
    
}
