/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.messenger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;

import org.alfresco.enterprise.repo.cluster.core.ClusterService;
import org.alfresco.enterprise.repo.cluster.core.HazelcastInstanceFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;


/**
 * Tests for the HazelcastMessengerFactory class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class HazelcastMessengerFactoryTest
{
    private HazelcastMessengerFactory factory;
    private @Mock HazelcastInstance hazelcast;
    private @Mock Member member;
    private @Mock Cluster cluster;
    private @Mock ITopic<String> topic;
    private @Mock HazelcastInstanceFactory hazelcastInstanceFactory;
    private @Mock ClusterService clusterService;
    
    @Before
    public void setUp()
    {
        factory = new HazelcastMessengerFactory();
        factory.setHazelcastInstanceFactory(hazelcastInstanceFactory);
        factory.setClusterService(clusterService);
        
        when(hazelcastInstanceFactory.isClusteringEnabled()).thenReturn(true);
        when(hazelcastInstanceFactory.getInstance()).thenReturn(hazelcast);
        when(clusterService.isInitialised()).thenReturn(true);
    }
    
    @Test
    public void topicWrappedInMessenger()
    {
        when(hazelcast.<String>getTopic("app-region")).thenReturn(topic);
        when(hazelcast.getCluster()).thenReturn(cluster);
        when(cluster.getLocalMember()).thenReturn(member);
        when(member.getInetSocketAddress()).thenReturn(InetSocketAddress.createUnresolved("a-host-name", 1234));
        
        Messenger<String> messenger = factory.createMessenger("app-region");
        
        assertSame(topic, ((HazelcastMessenger<String>) messenger).getTopic());
        assertEquals("a-host-name:1234", messenger.getAddress());
    }
    
    @Test
    public void canCheckClusterIsActive()
    {
        assertEquals(true, factory.isClusterActive());
        
        when(hazelcastInstanceFactory.isClusteringEnabled()).thenReturn(false);
        assertEquals(false, factory.isClusterActive());        
    }
}
