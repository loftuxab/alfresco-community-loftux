/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.messenger;

import java.io.Serializable;
import java.util.Set;

import org.alfresco.enterprise.repo.cluster.core.ClusterMembershipListener;
import org.alfresco.enterprise.repo.cluster.core.ClusterService;
import org.alfresco.enterprise.repo.cluster.core.ClusteredObjectProxyFactory;
import org.alfresco.enterprise.repo.cluster.core.ClusteringBootstrap;
import org.alfresco.enterprise.repo.cluster.core.HazelcastInstanceFactory;
import org.alfresco.util.Pair;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

/**
 * Hazelcast-based implementation of the {@link MessengerFactory} interface.
 * The factory must be configured with a {@link HazelcastInstance} - which
 * is the underlying factory for {@link ITopic} creation.
 * 
 * @author Matt Ward
 */
public class HazelcastMessengerFactory implements MessengerFactory
{
    private HazelcastInstanceFactory hazelcastInstanceFactory;
    private ClusterService clusterService;
    
    @Override
    public <T extends Serializable> Messenger<T> createMessenger(String appRegion)
    {
        return createMessenger(appRegion, false);
    }

    @Override
    public <T extends Serializable> Messenger<T> createMessenger(String appRegion, boolean acceptLocalMessages)
    {
        Messenger<T> messenger = null;
        if (hazelcastInstanceFactory.isClusteringEnabled())
        {
            if (clusterService != null && clusterService.isInitialised())
            {
                messenger = createClusteredMessenger(appRegion, acceptLocalMessages);
            }
            else
            {
                messenger = createProxiedMessenger(appRegion, acceptLocalMessages);
            }
        }
        else
        {
            messenger = createNonClusteredMessenger(appRegion, acceptLocalMessages);
        }
        
        return messenger;
    }
    

    private <T extends Serializable> Messenger<T> createNonClusteredMessenger(String appRegion, boolean acceptLocalMessages)
    {
        return new NullMessenger<T>();
    }

    private <T extends Serializable> Messenger<T> createProxiedMessenger(String appRegion, boolean acceptLocalMessages)
    {
        Messenger<T> messenger = createNonClusteredMessenger(appRegion, acceptLocalMessages);
        Messenger<T> proxy = ClusteredObjectProxyFactory.createMessengerProxy(messenger, appRegion, acceptLocalMessages);
        return proxy;
    }

    private <T extends Serializable> Messenger<T> createClusteredMessenger(String appRegion, boolean acceptLocalMessages)
    {
        HazelcastInstance hazelcast = hazelcastInstanceFactory.getInstance();
        ITopic<Pair<String, T>> topic = hazelcast.getTopic(appRegion);
        String address = hazelcast.getCluster().getLocalMember().getInetSocketAddress().toString();
        return new HazelcastMessenger<T>(topic, address, acceptLocalMessages);
    }

    /**
     * Provide the messenger factory with a means to obtain a HazelcastInstance.
     * 
     * @param hazelcastInstanceFactory
     */
    public void setHazelcastInstanceFactory(HazelcastInstanceFactory hazelcastInstanceFactory)
    {
        this.hazelcastInstanceFactory = hazelcastInstanceFactory;
    }

    /**
     * Do NOT set using Spring, see {@link ClusteringBootstrap}
     * 
     * @param clusterService
     */
    public void setClusterService(ClusterService clusterService)
    {
        this.clusterService = clusterService;
    }

    @Override
    public boolean isClusterActive()
    {
        return hazelcastInstanceFactory.isClusteringEnabled();
    }

    @Override
    public void addMembershipListener(final ClusterMembershipListener listener)
    {
        if (isClusterActive())
        {
            HazelcastInstance hazelcast = hazelcastInstanceFactory.getInstance();
            hazelcast.getCluster().addMembershipListener(new MembershipListener()
            {
                @Override
                public void memberRemoved(MembershipEvent e)
                {
                    listener.memberLeft(member(e), cluster(e));
                }
                
                @Override
                public void memberAdded(MembershipEvent e)
                {
                    listener.memberJoined(member(e), cluster(e));
                }
                
                private String member(MembershipEvent e)
                {
                    return e.getMember().getInetSocketAddress().toString();
                }
                
                private String[] cluster(MembershipEvent e)
                {
                    Set<Member> members = e.getCluster().getMembers();
                    String[] cluster = new String[members.size()];
                    int i = 0;
                    for (Member m : members)
                    {
                        cluster[i++] = m.getInetSocketAddress().toString();
                    }
                    return cluster;
                }
            });
        }
    }
}
