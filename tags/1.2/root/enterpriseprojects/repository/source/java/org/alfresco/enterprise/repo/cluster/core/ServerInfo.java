/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import java.io.Serializable;

/**
 * Provides information about the local repository server.
 * 
 * @author Matt Ward
 */
public interface ServerInfo extends Serializable
{
    /**
     * What is the host name of this machine?
     * 
     * @return Host name
     */
    String getHostName();
    
    /**
     * What is the IP address of this machine?
     * 
     * @return IP address
     */
    String getIPAddress();
    
    /**
     * What port is clustering configured to run on?
     * 
     * @return Port
     */
    int getPort();
    
    /**
     * Does this host have clustering enabled?
     * 
     * @return boolean
     */
    boolean isClusteringEnabled();
    
    /**
     * What type of server is this? Human readable label that
     * describes the type of server, e.g. "repository server" or
     * "transformation server".
     * 
     * @return String
     */
    String getServerType();
}
