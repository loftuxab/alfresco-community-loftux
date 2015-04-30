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
 * Exception thrown when an unacceptable IP address has been
 * selected by Alfresco for clustering.
 * 
 * @author Matt Ward
 */
public class ClusterAddressException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = 1L;
    private final String ipAddress;
    
    /**
     * Create a cluster address exception.
     * 
     * @param message
     * @param ipAddress
     */
    public ClusterAddressException(String ipAddress)
    {
        super("system.cluster.err.bad_ip", new Object[] { ipAddress });
        this.ipAddress = ipAddress;
    }
    
    public String getIpAddress()
    {
        return this.ipAddress;
    }
}
