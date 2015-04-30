/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import java.util.Date;

/**
 * Provides information about a repository server instance.
 * 
 * @author Matt Ward
 */
public class RegisteredServerInfoImpl extends ServerInfoImpl implements RegisteredServerInfo
{
    private static final long serialVersionUID = 1L;
    private Date lastRegistered;
    
    
    /**
     * Default constructor. 
     */
    public RegisteredServerInfoImpl()
    {
    }

    /**
     * Construct a {@link RegisteredServerInfoImpl} object specifying all the field values.
     * 
     * @param hostName
     * @param ipAddress
     * @param port
     * @param clusteringEnabled
     * @param lastRegistered
     * @param serverType
     */
    public RegisteredServerInfoImpl(String hostName, String ipAddress, int port, boolean clusteringEnabled,
                Date lastRegistered, String serverType)
    {
        setHostName(hostName);
        setIPAddress(ipAddress);
        setPort(port);
        setClusteringEnabled(clusteringEnabled);
        setLastRegistered(lastRegistered);
        setServerType(serverType);
    }

    @Override
    public Date getLastRegistered()
    {
        return this.lastRegistered;
    }

    public void setLastRegistered(Date lastRegistered)
    {
        this.lastRegistered = lastRegistered;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                    + ((this.lastRegistered == null) ? 0 : this.lastRegistered.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        RegisteredServerInfoImpl other = (RegisteredServerInfoImpl) obj;
        if (this.lastRegistered == null)
        {
            if (other.lastRegistered != null) return false;
        }
        else if (!this.lastRegistered.equals(other.lastRegistered)) return false;
        return true;
    }
}
