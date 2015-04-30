/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.hazelcast.config.TcpIpConfig;

/**
 * Subclass of Hazelcast's {@link com.hazelcast.config.TcpIpConfig} class that
 * configures the list of cluster members by querying the repository's {@link ClusterService}.
 * 
 * @author Matt Ward
 */
public class AlfrescoTcpIpConfig extends TcpIpConfig implements ApplicationContextAware
{
    private static final long serialVersionUID = 1L;
    private ClusterService clusterService;
    private ApplicationContext applicationContext;
    
    @Override
    public List<String> getMembers()
    {
        Set<RegisteredServerInfoImpl> serverSet = getClusterService().getAllRegisteredMembers();
        List<String> memberAddresses = socketAddressList(serverSet);
        return memberAddresses;
    }

    protected List<String> socketAddressList(Set<RegisteredServerInfoImpl> serverSet)
    {
        List<String> addresses = new ArrayList<String>(serverSet.size());
        for (RegisteredServerInfoImpl serverInfo : serverSet)
        {
            addresses.add(serverInfo.getIPAddress() + ":" + serverInfo.getPort());
        }
        return addresses;
    }

    private ClusterService getClusterService()
    {
        if (clusterService == null)
        {
            clusterService = (ClusterService) applicationContext.getBean("ClusterService");
        }
        return clusterService;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}
