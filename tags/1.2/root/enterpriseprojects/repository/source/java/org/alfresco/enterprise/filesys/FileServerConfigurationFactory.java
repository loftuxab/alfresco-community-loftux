/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.filesys;

import org.alfresco.enterprise.repo.cluster.core.HazelcastInstanceFactory;
import org.alfresco.filesys.config.ClusterConfigBean;
import org.alfresco.filesys.config.ServerConfigurationBean;

/**
 * Factory to create enterprise versions of key fileserver configuration beans.
 * 
 * @author Matt Ward
 */
public class FileServerConfigurationFactory extends
            org.alfresco.filesys.config.FileServerConfigurationFactory
{
    private HazelcastInstanceFactory hazelcastInstanceFactory;
    private String mapName;
    
    @Override
    public ServerConfigurationBean createFileServerConfiguration()
    {
        return new org.alfresco.enterprise.filesys.ServerConfigurationBean();
    }

    @Override
    public ClusterConfigBean createClusterConfigBean()
    {
        // Create an Enterprise version of the ClusterConfigBean
        org.alfresco.enterprise.filesys.ClusterConfigBean ccb =
                    new org.alfresco.enterprise.filesys.ClusterConfigBean();
        // Configure with the Enterprise-only Hazelcast factory 
        ccb.setHazelcastInstanceFactory(hazelcastInstanceFactory);
        ccb.setMapName(mapName);
        return ccb;
    }

    public void setMapName(String mapName)
    {
        this.mapName = mapName;
    }

    public void setHazelcastInstanceFactory(HazelcastInstanceFactory hazelcastInstanceFactory)
    {
        this.hazelcastInstanceFactory = hazelcastInstanceFactory;
    }
}
