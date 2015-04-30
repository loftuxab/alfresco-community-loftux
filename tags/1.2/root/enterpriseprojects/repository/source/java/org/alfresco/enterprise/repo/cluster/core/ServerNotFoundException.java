/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Signifies that a particular server is not registered with the ClusterService.
 * 
 * @author Matt Ward
 */
public class ServerNotFoundException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = 1L;
    private final String ipAddress;
    private final int port;
    
    /**
     * Constructor.
     * 
     * @param ipAddress
     * @param port
     */
    public ServerNotFoundException(String ipAddress, int port)
    {
        super("system.cluster.err.server_not_found", new Object[] { ipAddress + ":" + port });
        this.ipAddress = ipAddress;
        this.port = port; 
    }

    public String getIpAddress()
    {
        return this.ipAddress;
    }

    public int getPort()
    {
        return this.port;
    }
}
