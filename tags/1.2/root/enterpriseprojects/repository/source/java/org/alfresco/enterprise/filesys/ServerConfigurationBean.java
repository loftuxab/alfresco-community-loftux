/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.filesys;

import org.alfresco.enterprise.repo.cluster.core.HazelcastInstanceFactory;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.filesys.cache.hazelcast.ClusterConfigSection;

import com.hazelcast.core.HazelcastInstance;

/**
 * Enterprise version of {@link org.alfresco.filesys.config.ServerConfigurationBean}.
 * 
 * @author Matt Ward
 */
public class ServerConfigurationBean extends org.alfresco.filesys.config.ServerConfigurationBean
{
    @Override
    protected void processClusterConfig() throws InvalidConfigurationException
    {
        if (clusterConfigBean  == null || !clusterConfigBean.getClusterEnabled())
        {
            removeConfigSection(ClusterConfigSection.SectionName);
            logger.info("Filesystem cluster cache not enabled");
            return;
        }
                
        // Create a ClusterConfigSection and attach it to 'this'.
        ClusterConfigSection clusterConf = new ClusterConfigSection(this);
        HazelcastInstanceFactory hazelcastInstanceFactory = getClusterConfigBean().getHazelcastInstanceFactory();
        // Clustering is enabled, so we can safely request the hazelcast instance.
        HazelcastInstance hazelcast = hazelcastInstanceFactory.getInstance();
        clusterConf.setHazelcastInstance(hazelcast);
    }
    
    protected org.alfresco.enterprise.filesys.ClusterConfigBean getClusterConfigBean()
    {
        return (org.alfresco.enterprise.filesys.ClusterConfigBean) clusterConfigBean;
    }
}
