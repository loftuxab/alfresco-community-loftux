/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.checker;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * @since Odin
 *
 */
public class ClusterEvent extends ApplicationEvent
{
    private static final long serialVersionUID = 7481373845772903712L;

    public ClusterEvent(ClusterChecker clusterChecker)
    {
        super(clusterChecker);
    }

}
