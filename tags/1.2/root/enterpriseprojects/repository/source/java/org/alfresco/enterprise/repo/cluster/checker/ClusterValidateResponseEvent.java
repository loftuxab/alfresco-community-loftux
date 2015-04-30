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
public class ClusterValidateResponseEvent extends ClusterMessageEvent
{
    private static final long serialVersionUID = -813956714769487998L;
    
    private String ipAddress;
    private String hostName;
    private int port;
    private boolean ticketValid;

    public ClusterValidateResponseEvent(ClusterChecker clusterChecker, String ipAddress, String hostName, int port, String sourceId, String targetId, boolean ticketValid)
    {
        super(clusterChecker, sourceId, targetId);
        this.ipAddress = ipAddress;
        this.hostName = hostName;
        this.port = port;
        this.ticketValid = ticketValid;
    }

    public String getIPAddress()
    {
        return ipAddress;
    }
    
    public String getHostName()
    {
        return this.hostName;
    }

    public int getPort()
    {
        return this.port;
    }

    public boolean isTicketValid()
    {
        return ticketValid;
    }

}
