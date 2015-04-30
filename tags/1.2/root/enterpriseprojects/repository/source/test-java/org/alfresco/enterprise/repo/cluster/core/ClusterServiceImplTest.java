/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.alfresco.enterprise.repo.cluster.core.ClusterServiceImpl.ServerInfoCallback;
import org.alfresco.enterprise.repo.cluster.core.ClusterServiceImpl.TimestampProvider;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.attributes.AttributeService.AttributeQueryCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.impl.MemberImpl;
import com.hazelcast.nio.Address;

/**
 * Tests for the {@link ClusterServiceImpl} class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class ClusterServiceImplTest
{
    private ClusterServiceImpl clusterService;
    private @Mock AttributeService attributeService;
    private TimestampProvider timeStamper;
    private Date now;
    private @Mock HazelcastInstanceFactory hzInstanceFactory;
    private @Mock HazelcastInstance hzInstance;
    private @Mock Cluster hzCluster;
    private @Mock ApplicationContext appCtx;
    private @Mock JobLockService jobLockService;
    private @Mock SysAdminParams sysAdminParams;
    private @Mock NonMemberIPAddrPicker nonMemberIPAddrPicker;
    
    @Before
    public void setUp() throws UnknownHostException
    {
        clusterService = new ClusterServiceImpl();
        clusterService.setAttributeService(attributeService);
        clusterService.setJobLockService(jobLockService);
        clusterService.setSysAdminParams(sysAdminParams);
        clusterService.setNonMemberAddrPicker(nonMemberIPAddrPicker);
        
        now = new Date();
        
        // Override timeStamp method to return a reference timestamp for use in testing.
        timeStamper = new TimestampProvider()
        {
            @Override
            public Date timeStamp()
            {
                return now;
            }
        };
        clusterService.timeStampProvider = timeStamper;
        
        when(hzInstance.getCluster()).thenReturn(hzCluster);
        
        MemberImpl localMember = new MemberImpl(new Address("192.168.1.200", 9876), true);
        when(hzCluster.getLocalMember()).thenReturn(localMember);
        
        when(sysAdminParams.subsituteHost("raw.host.property")).thenReturn("this.host.example.com");
        clusterService.setMemberHostName("raw.host.property");
        clusterService.setServerType("repo server");
        when(hzInstanceFactory.isClusteringEnabled()).thenReturn(true);
        when(hzInstanceFactory.getInstance()).thenReturn(hzInstance);
        clusterService.setHazelcastInstanceFactory(hzInstanceFactory);
    }
    
    @Test
    public void serverRegistersSelfWhenClusteringDisabled()
    {
        // Clustering is disabled
        when(hzInstanceFactory.isClusteringEnabled()).thenReturn(false);
        when(nonMemberIPAddrPicker.pick()).thenReturn("192.168.1.200");
        
        // Bootstrap initialises this server as a non-cluster-member
        clusterService.initNonMember();
        
        // Check the correct attributes are written when this non-member registers itself
        verify(attributeService).setAttribute("this.host.example.com", ".clusterMembers", "192.168.1.200:0", ".host_name");
        verify(attributeService).setAttribute("192.168.1.200", ".clusterMembers", "192.168.1.200:0", ".ip_address");
        verify(attributeService).setAttribute(0, ".clusterMembers", "192.168.1.200:0", ".port");
        verify(attributeService).setAttribute(false, ".clusterMembers", "192.168.1.200:0", ".clustering_enabled");
        verify(attributeService).setAttribute(now, ".clusterMembers", "192.168.1.200:0", ".last_registered");
        verify(attributeService).setAttribute("repo server", ".clusterMembers", "192.168.1.200:0", ".cluster_node_type");
    }
    
    @Test
    public void canRegisterSelf()
    {
        InetSocketAddress self = new InetSocketAddress("192.168.1.200", 9876);
        clusterService.registerSelf(self);
        
        // Check the correct attributes are written when the server is registered
        verify(attributeService).setAttribute(true, ".clusterMembers", "192.168.1.200:9876", ".clustering_enabled");
        verify(attributeService).setAttribute("raw.host.property", ".clusterMembers", "192.168.1.200:9876", ".host_name");
        verify(attributeService).setAttribute("192.168.1.200", ".clusterMembers", "192.168.1.200:9876", ".ip_address");
        verify(attributeService).setAttribute(9876, ".clusterMembers", "192.168.1.200:9876", ".port");
        verify(attributeService).setAttribute(now, ".clusterMembers", "192.168.1.200:9876", ".last_registered");
        verify(attributeService).setAttribute("repo server", ".clusterMembers", "192.168.1.200:9876", ".cluster_node_type");
    }
    
    @Test
    public void canRegisterMember()
    {
        clusterService.registerMember("h1.example.com", "192.168.1.1", 9701, "Alf repo");
        
        // Check the correct attributes are written when a member is registered
        verify(attributeService).setAttribute(true, ".clusterMembers", "192.168.1.1:9701", ".clustering_enabled");
        verify(attributeService).setAttribute("h1.example.com", ".clusterMembers", "192.168.1.1:9701", ".host_name");
        verify(attributeService).setAttribute("192.168.1.1", ".clusterMembers", "192.168.1.1:9701", ".ip_address");
        verify(attributeService).setAttribute(9701, ".clusterMembers", "192.168.1.1:9701", ".port");
        verify(attributeService).setAttribute(now, ".clusterMembers", "192.168.1.1:9701", ".last_registered");
        verify(attributeService).setAttribute("Alf repo", ".clusterMembers", "192.168.1.1:9701", ".cluster_node_type");
    }
    
    @Test
    public void canRegisterNonMember()
    {
        clusterService.registerNonMember("h1.example.com", "192.168.1.1", 9702, "Transformation server");
        
        // Check the correct attributes are written when a non-member (e.g. transformation server) is registered
        verify(attributeService).setAttribute("h1.example.com", ".clusterMembers", "192.168.1.1:9702", ".host_name");
        verify(attributeService).setAttribute("192.168.1.1", ".clusterMembers", "192.168.1.1:9702", ".ip_address");
        verify(attributeService).setAttribute(9702, ".clusterMembers", "192.168.1.1:9702", ".port");
        verify(attributeService).setAttribute(false, ".clusterMembers", "192.168.1.1:9702", ".clustering_enabled");
        verify(attributeService).setAttribute(now, ".clusterMembers", "192.168.1.1:9702", ".last_registered");
        verify(attributeService).setAttribute("Transformation server", ".clusterMembers", "192.168.1.1:9702", ".cluster_node_type");
    }
    
    @Test
    public void canGetLocalServerInfo()
    {
        clusterService.initClusterService();
        
        assertEquals("this.host.example.com", clusterService.getMemberHostName());
        assertEquals("192.168.1.200", clusterService.getMemberIP());
        assertEquals(true, clusterService.isClusteringEnabled());
        assertEquals("repo server", clusterService.getServerType());
    }
    
    @Test
    public void canGetActiveMembers() throws UnknownHostException
    {
        // Members reported by Hazelcast
        Set<Member> members = new HashSet<Member>();
        members.add(new MemberImpl(new Address("192.168.1.1", 5701), false));
        members.add(new MemberImpl(new Address("192.168.1.200", 9876), true));
        members.add(new MemberImpl(new Address("192.168.1.2", 5701), false));
        when(hzCluster.getMembers()).thenReturn(members);
        
        // Members which are also registered with the cluster service.
        doAnswer(new Answer<Object>()
        {
            long id = 0;
            ServerInfoCallback callback;
            void handle(Serializable value, String... keys)
            {
                callback.handleAttribute(id++, value, keys);
            }
            
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                callback = (ServerInfoCallback) invocation.getArguments()[0];
                handle(true, ".clusterMembers", "192.168.1.200:9876", ".clustering_enabled");
                handle("this.host.example.com", ".clusterMembers", "192.168.1.200:9876", ".host_name");
                handle("192.168.1.200", ".clusterMembers", "192.168.1.200:9876", ".ip_address");
                handle(9876, ".clusterMembers", "192.168.1.200:9876", ".port");
                handle(now, ".clusterMembers", "192.168.1.200:9876", ".last_registered");
                handle("repo server", ".clusterMembers", "192.168.1.200:9876", ".cluster_node_type");
                return null;
            }
        }).when(attributeService).getAttributes(any(AttributeQueryCallback.class), eq(".clusterMembers"));

        
        clusterService.initClusterService();
        
        // Exercise the method under test.
        Set<RegisteredServerInfoImpl> actives = clusterService.getActiveMembers();
        
        
        // In the cluster, there are two servers that are 'unknown' and one that is registered.
        assertEquals(3, actives.size());
        assertTrue(actives.contains(new RegisteredServerInfoImpl(
                    "192.168.1.1","192.168.1.1", 5701, false, null, "Unknown server type")));
        assertTrue(actives.contains(new RegisteredServerInfoImpl(
                    "this.host.example.com", "192.168.1.200", 9876, true, now, "repo server")));
        assertTrue(actives.contains(new RegisteredServerInfoImpl(
                    "192.168.1.2", "192.168.1.2", 5701, false, null, "Unknown server type")));
    }
    
    @Test
    public void canGetOfflineMembers() throws UnknownHostException
    {
        // Members reported by Hazelcast (active)
        Set<Member> members = new HashSet<Member>();
        members.add(new MemberImpl(new Address("192.168.1.1", 5701), true));
        when(hzCluster.getMembers()).thenReturn(members);
        
        // Members which are also registered with the cluster service.
        doAnswer(new Answer<Object>()
        {
            long id = 0;
            ServerInfoCallback callback;
            void handle(Serializable value, String... keys)
            {
                callback.handleAttribute(id++, value, keys);
            }
            
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                callback = (ServerInfoCallback) invocation.getArguments()[0];
                // Registered member 1 - should not be reported offline as it is in hazelcast member list
                handle(true, ".clusterMembers", "192.168.1.1:5701", ".clustering_enabled");
                handle("member1.example.com", ".clusterMembers", "192.168.1.1:5701", ".host_name");
                handle("192.168.1.1", ".clusterMembers", "192.168.1.1:5701", ".ip_address");
                handle(5701, ".clusterMembers", "192.168.1.1:5701", ".port");
                handle(now, ".clusterMembers", "192.168.1.1:5701", ".last_registered");
                handle("repo server", ".clusterMembers", "192.168.1.1:5701", ".cluster_node_type");
                // Registered member 2 - should be reported as offline, as it isn't in hazelcast member list
                handle(true, ".clusterMembers", "192.168.1.2:5701", ".clustering_enabled");
                handle("member2.example.com", ".clusterMembers", "192.168.1.2:5701", ".host_name");
                handle("192.168.1.2", ".clusterMembers", "192.168.1.2:5701", ".ip_address");
                handle(5701, ".clusterMembers", "192.168.1.2:5701", ".port");
                handle(now, ".clusterMembers", "192.168.1.2:5701", ".last_registered");
                handle("repo server", ".clusterMembers", "192.168.1.2:5701", ".cluster_node_type");
                // Registered NON-member 1 - should not be reported as offline.
                handle(false, ".clusterMembers", "192.168.1.3:5701", ".clustering_enabled");
                handle("non-member1.example.com", ".clusterMembers", "192.168.1.3:5701", ".host_name");
                handle("192.168.1.3", ".clusterMembers", "192.168.1.3:5701", ".ip_address");
                handle(5701, ".clusterMembers", "192.168.1.3:5701", ".port");
                handle(now, ".clusterMembers", "192.168.1.3:5701", ".last_registered");
                handle("other server", ".clusterMembers", "192.168.1.3:5701", ".cluster_node_type");
                
                return null;
            }
        }).when(attributeService).getAttributes(any(AttributeQueryCallback.class), eq(".clusterMembers"));

        
        clusterService.initClusterService();
        
        // Exercise the method under test.
        Set<RegisteredServerInfoImpl> actives = clusterService.getOfflineMembers();
        
        
        // In the cluster, there are two servers that are 'unknown' and one that is registered.
        assertEquals(1, actives.size());
        assertTrue(actives.contains(new RegisteredServerInfoImpl(
                    "member2.example.com","192.168.1.2", 5701, true, now, "repo server")));
    }
    
    @Test
    public void canGetNumClusterMembersBeforeInitialized()
    {
        assertFalse(clusterService.isInitialised());
        assertEquals(0, clusterService.getNumActiveClusterMembers());

        // The service shouldn't retrieve the cluster information.
        verify(hzInstance, Mockito.never()).getCluster();
    }
    
    @Test
    public void canGetNumClusterMembersAfterInitialized() throws UnknownHostException
    {
        Set<Member> members = new HashSet<Member>();
        members.add(new MemberImpl(new Address("192.168.1.1", 5701), true));
        members.add(new MemberImpl(new Address("192.168.1.2", 5701), false));
        when(hzCluster.getMembers()).thenReturn(members);
        
        clusterService.initClusterService();
        assertTrue(clusterService.isInitialised());
        
        assertEquals(2, clusterService.getNumActiveClusterMembers());
    }
    
    @Test
    public void canDeregisterServer()
    {
        // The attribute service must confirm that the server exists
        when(attributeService.exists(".clusterMembers", "10.150.10.1:4321", ".ip_address")).thenReturn(true);
        
        clusterService.deregisterServer("10.150.10.1", 4321);
        
        verify(attributeService).removeAttributes(".clusterMembers", "10.150.10.1:4321");
    }
    
    @Test
    public void canDeregisterNonClusteredServer() throws UnknownHostException
    {
        // Members reported by Hazelcast
        Set<Member> members = new HashSet<Member>();
        members.add(new MemberImpl(new Address("192.168.1.1", 5702), false));
        members.add(new MemberImpl(new Address("192.168.1.3", 5701), true));
        when(hzCluster.getMembers()).thenReturn(members);
        
        // The attribute service must confirm that the servers exists
        when(attributeService.exists(".clusterMembers", "192.168.1.1:5701", ".ip_address")).thenReturn(true);
        when(attributeService.exists(".clusterMembers", "192.168.1.2:5701", ".ip_address")).thenReturn(true);
        when(attributeService.exists(".clusterMembers", "192.168.1.3:5701", ".ip_address")).thenReturn(true);
        
        
        clusterService.initClusterService();
        
        // Attempt to delete some server info...
        
        clusterService.deregisterNonClusteredServer("192.168.1.1", 5701);
        
        try
        {
            clusterService.deregisterNonClusteredServer("192.168.1.1", 5702);
            fail("Should not have been able to remove server info.");
        }
        catch (IllegalArgumentException e)
        {
            // Good, this is an active member - so don't delete it.
        }
        
        clusterService.deregisterNonClusteredServer("192.168.1.2", 5701);
        
        try
        {
            clusterService.deregisterNonClusteredServer("192.168.1.3", 5701);
            fail("Should not have been able to remove server info.");
        }
        catch (IllegalArgumentException e)
        {
            // Good, this is an active member - so don't delete it.
        }
     
        // Check that the allowed deletions have taken place
        verify(attributeService).removeAttributes(".clusterMembers", "192.168.1.1:5701");
        verify(attributeService).removeAttributes(".clusterMembers", "192.168.1.2:5701");
        // Check that the disallowed deletions didn't happen
        verify(attributeService, never()).removeAttributes(".clusterMembers", "192.168.1.1:5702");
        verify(attributeService, never()).removeAttributes(".clusterMembers", "192.168.1.3:5701");
    }
    
    @Test
    public void loopbackAddressIsInvalid() throws UnknownHostException
    {
        clusterService.setInterfaceSpec("");
        
        // Override the behaviour from setUp()
        MemberImpl localMember = new MemberImpl(new Address("127.0.0.1", 9876), true);
        when(hzCluster.getLocalMember()).thenReturn(localMember);
        
        try
        {
            clusterService.initClusterService();
            fail("Address 127.0.0.1 should not be allowed for clustering without explicitly being set.");
        }
        catch (ClusterAddressException e)
        {
            // Got here, good.
        }
        
        assertNull("Member IP address should not be accessible.", clusterService.getMemberIP());
        assertNull("Member port number should not be accessible.", clusterService.getMemberPort());
        assertNull("Host name should not be accesible", clusterService.getMemberHostName());
        assertEquals(true, clusterService.isClusteringEnabled());
        assertEquals(false, clusterService.isInitialised());        
    }
    
    @Test
    public void loopbackAddressIsValidIfSpecified() throws UnknownHostException
    {
        // User requests the loopback...
        clusterService.setInterfaceSpec("127.0.0.1");
        
        // Override the behaviour from setUp()
        MemberImpl localMember = new MemberImpl(new Address("127.0.0.1", 9876), true);
        when(hzCluster.getLocalMember()).thenReturn(localMember);
        
        clusterService.initClusterService();
        
        assertEquals("127.0.0.1", clusterService.getMemberIP());
        assertEquals(9876, clusterService.getMemberPort().intValue());
        assertEquals(true, clusterService.isClusteringEnabled());
        assertEquals(true, clusterService.isInitialised());        
        assertEquals("this.host.example.com", clusterService.getMemberHostName());
    }
}
