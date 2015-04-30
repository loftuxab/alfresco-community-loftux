/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

/**
 * Tests for the {@link AlfescoTcpIpConfig} class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class AlfrescoTcpIpConfigTest
{
    private AlfrescoTcpIpConfig config;
    private @Mock ClusterService clusterService;
    private @Mock ApplicationContext appCtx;
    
    @Before
    public void setUp() throws Exception
    {
        when(appCtx.getBean("ClusterService")).thenReturn(clusterService);
        
        config = new AlfrescoTcpIpConfig();
        config.setApplicationContext(appCtx);
    }

    @Test
    public void testGetMembers()
    {
        Set<RegisteredServerInfoImpl> allMembers = makeFakeMemberSet();
        when(clusterService.getAllRegisteredMembers()).thenReturn(allMembers);
        
        List<String> members = config.getMembers();

        assertEquals(3, members.size());
        assertEquals("192.168.1.111:5701", members.get(0));
        assertEquals("192.168.1.112:5702", members.get(1));
        assertEquals("192.168.1.113:5703", members.get(2));
    }

    @Test
    public void testConvertServerInfoSetToAddressList()
    {
        Set<RegisteredServerInfoImpl> otherMembers = makeFakeMemberSet();
        
        List<String> members = config.socketAddressList(otherMembers);

        assertEquals(3, members.size());
        assertEquals("192.168.1.111:5701", members.get(0));
        assertEquals("192.168.1.112:5702", members.get(1));
        assertEquals("192.168.1.113:5703", members.get(2));
    }
    
    /**
     * The fake set of other servers, so that we can verify that the info has been
     * consumed and reported to Hazelcast correctly.
     * 
     * @return Set of ServerInfo objects.
     */
    private Set<RegisteredServerInfoImpl> makeFakeMemberSet()
    {
        Set<RegisteredServerInfoImpl> members = new LinkedHashSet<RegisteredServerInfoImpl>();
        members.add(new RegisteredServerInfoImpl("a", "192.168.1.111", 5701, true, new Date(), "Alfresco repository"));
        members.add(new RegisteredServerInfoImpl("b", "192.168.1.112", 5702, true, new Date(), "Alfresco repository"));
        members.add(new RegisteredServerInfoImpl("c", "192.168.1.113", 5703, true, new Date(), "Alfresco repository"));
        return members;
    }
}
