/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.filesys;

import org.alfresco.enterprise.repo.cluster.core.HazelcastInstanceFactory;

/**
 * Enterprise version of {@link org.alfresco.filesys.config.ClusterConfigBean}
 * 
 * @author Matt Ward
 */
public class ClusterConfigBean extends org.alfresco.filesys.config.ClusterConfigBean
{
    private HazelcastInstanceFactory hazelcastInstanceFactory;
    private String mapName;
    
    public void setHazelcastInstanceFactory(HazelcastInstanceFactory hazelcastInstanceFactory)
    {
        this.hazelcastInstanceFactory = hazelcastInstanceFactory;
    }
    
    public HazelcastInstanceFactory getHazelcastInstanceFactory()
    {
        return this.hazelcastInstanceFactory;
    }

    @Override
    public boolean getClusterEnabled()
    {
       return hazelcastInstanceFactory.isClusteringEnabled();
    }
    
    /**
     * The cluster name, with respect to JLAN is really the filestate map name. See hazelcast-tcp.xml
     * in the enterprise project and also jlanConfigCluster.xml in JLAN library.
     */
    @Override
    public String getClusterName()
    {
        return mapName;
    }
    
    public void setMapName(String mapName)
    {
        this.mapName = mapName;
    }
}
