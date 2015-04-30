/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import javax.management.openmbean.TabularData;


/**
 * Defines cluster admin management interface.
 * 
 * @author Matt Ward
 */
public interface ClusterAdminMBean
{
    String getClusterName();
    String getHostName();
    String getIPAddress();
    boolean isClusteringEnabled();
    int getNumClusterMembers();
    TabularData getClusterMembers();
    TabularData getOfflineMembers();
    TabularData getNonClusteredServers();
    void deregisterNonClusteredServer(String ipAddress, int port);
}
