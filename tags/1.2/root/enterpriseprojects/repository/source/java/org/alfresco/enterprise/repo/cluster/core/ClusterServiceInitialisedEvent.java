/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import org.springframework.context.ApplicationEvent;

/**
 * Application event that is fired upon initialisation of the ClusterService.
 * 
 * @author Matt Ward
 */
public class ClusterServiceInitialisedEvent extends ApplicationEvent
{
    private static final long serialVersionUID = 1L;

    public ClusterServiceInitialisedEvent(ClusterService source)
    {
        super(source);
    }
    
    public ClusterService getClusterService()
    {
        return (ClusterService) getSource();
    }
}
