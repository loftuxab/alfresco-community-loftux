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

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MembershipListener;

/**
 * Tests for the {@link HazelcastInstanceFactory} class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class HazelcastInstanceFactoryTest
{
    private HazelcastInstanceFactory hazelcastInstanceFactory;
    private @Mock HazelcastInstance hzInstance;
    private @Mock Cluster cluster;
    private @Mock MembershipListener membershipListener1;
    private @Mock MembershipListener membershipListener2;
    private @Mock MembershipListener membershipListener3;
    private List<MembershipListener> membershipListeners;
    
    @Before
    public void setUp() throws Exception
    {
        hazelcastInstanceFactory = new HazelcastInstanceFactory();
        membershipListeners = Arrays.asList(
                    membershipListener1,
                    membershipListener2,
                    membershipListener3
        );
        
        Mockito.when(hzInstance.getCluster()).thenReturn(cluster);
        
        // Set the private hazelcastInstance field with a mock HazelcastInstance.
        Field hzInstanceField = hazelcastInstanceFactory.getClass().getDeclaredField("hazelcastInstance");
        hzInstanceField.setAccessible(true);
        hzInstanceField.set(hazelcastInstanceFactory, hzInstance);
        
        hazelcastInstanceFactory.setMembershipListeners(membershipListeners);
    }
    

    @Test
    public void testAddMembershipListeners()
    {
        hazelcastInstanceFactory.addMembershipListeners();

        Mockito.verify(cluster).addMembershipListener(membershipListener1);
        Mockito.verify(cluster).addMembershipListener(membershipListener2);
        Mockito.verify(cluster).addMembershipListener(membershipListener3);
    }
}
