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
public class ClusterValidateEvent extends ClusterMessageEvent
{
    private static final long serialVersionUID = -8091460189522981871L;

    private String ticket;

    public ClusterValidateEvent(ClusterChecker clusterChecker, String ticket, String sourceId, String targetId)
    {
        super(clusterChecker, sourceId, targetId);
        this.ticket = ticket;
    }
    
    public String getTicket()
    {
        return ticket;
    }
}
