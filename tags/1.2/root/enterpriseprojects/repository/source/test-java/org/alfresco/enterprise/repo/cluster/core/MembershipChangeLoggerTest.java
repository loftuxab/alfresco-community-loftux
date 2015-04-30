/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.enterprise.repo.cluster.core;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.extensions.surf.util.I18NUtil;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;

/**
 * Tests for the {@link MembershipChangeLogger} class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class MembershipChangeLoggerTest
{
    private MembershipChangeLogger membershipChangeLogger;
    private @Mock Log log;
    private @Mock Cluster cluster;
    private @Mock Member member1;
    private @Mock Member member2;
    private @Mock Member member3;

    @Before
    public void setUp() throws Exception
    {
        I18NUtil.registerResourceBundle("alfresco/messages/system-messages");
        
        membershipChangeLogger = new MembershipChangeLogger(log);
        
        when(member1.getInetSocketAddress()).thenReturn(makeInetSocketAddr(1));
        when(member2.getInetSocketAddress()).thenReturn(makeInetSocketAddr(2));
        when(member3.getInetSocketAddress()).thenReturn(makeInetSocketAddr(3));
                
        Set<Member> members = new LinkedHashSet<Member>();
        members.add(member1);
        members.add(member2);
        members.add(member3);
        when(cluster.getMembers()).thenReturn(members);
    }

    private InetSocketAddress makeInetSocketAddr(int i) throws UnknownHostException
    {
        return new InetSocketAddress(InetAddress.getByAddress("host"+i, new byte[] { 127, 0, 0, (byte)i }), 5700+i);
    }

    @Test
    public void testMemberAdded()
    {
        MembershipEvent event = new MembershipEvent(cluster, member3, MembershipEvent.MEMBER_ADDED);
        membershipChangeLogger.memberAdded(event);
        
        verify(log).info("Member joined: 127.0.0.3:5703 (hostname: host3)");
        verify(log).info("Current cluster members:\n" +
                         "  127.0.0.1:5701 (hostname: host1)\n" +
                         "  127.0.0.2:5702 (hostname: host2)\n" +
                         "  127.0.0.3:5703 (hostname: host3)\n");
    }

    @Test
    public void testMemberRemoved()
    {
        MembershipEvent event = new MembershipEvent(cluster, member2, MembershipEvent.MEMBER_REMOVED);
        membershipChangeLogger.memberRemoved(event);
        
        verify(log).info("Member left: 127.0.0.2:5702 (hostname: host2)");
        verify(log).info("Current cluster members:\n" +
                         "  127.0.0.1:5701 (hostname: host1)\n" +
                         "  127.0.0.2:5702 (hostname: host2)\n" +
                         "  127.0.0.3:5703 (hostname: host3)\n");
    }
}
