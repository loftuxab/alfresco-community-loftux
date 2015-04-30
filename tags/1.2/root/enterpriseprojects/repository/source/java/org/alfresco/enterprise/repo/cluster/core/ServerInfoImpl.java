/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;


/**
 * Provides basic server instance information.
 * 
 * @author Matt Ward
 */
public class ServerInfoImpl implements ServerInfo
{
    private static final long serialVersionUID = 1L;
    private String hostName;
    private String ipAddress;
    private int port;
    private boolean clusteringEnabled;
    private String serverType;
    
    @Override
    public String getHostName()
    {
        return hostName;
    }

    @Override
    public String getIPAddress()
    {
        return ipAddress;
    }

    @Override
    public int getPort()
    {
        return port;
    }

    @Override
    public boolean isClusteringEnabled()
    {
        return clusteringEnabled;
    }
    
    @Override
    public String getServerType()
    {
        return this.serverType;
    }

    
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    public void setIPAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public void setClusteringEnabled(boolean clusteringEnabled)
    {
        this.clusteringEnabled = clusteringEnabled;
    }

    public void setServerType(String serverType)
    {
        this.serverType = serverType;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.clusteringEnabled ? 1231 : 1237);
        result = prime * result + ((this.hostName == null) ? 0 : this.hostName.hashCode());
        result = prime * result + ((this.ipAddress == null) ? 0 : this.ipAddress.hashCode());
        result = prime * result + this.port;
        result = prime * result + ((this.serverType == null) ? 0 : this.serverType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ServerInfoImpl other = (ServerInfoImpl) obj;
        if (this.clusteringEnabled != other.clusteringEnabled) return false;
        if (this.hostName == null)
        {
            if (other.hostName != null) return false;
        }
        else if (!this.hostName.equals(other.hostName)) return false;
        if (this.ipAddress == null)
        {
            if (other.ipAddress != null) return false;
        }
        else if (!this.ipAddress.equals(other.ipAddress)) return false;
        if (this.port != other.port) return false;
        if (this.serverType == null)
        {
            if (other.serverType != null) return false;
        }
        else if (!this.serverType.equals(other.serverType)) return false;
        return true;
    }
}
