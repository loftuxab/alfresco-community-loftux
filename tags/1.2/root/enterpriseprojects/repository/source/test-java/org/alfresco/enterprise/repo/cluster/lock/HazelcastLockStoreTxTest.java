/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.lock;

import org.alfresco.repo.lock.mem.AbstractLockStoreTxTest;
import org.alfresco.repo.lock.mem.LockState;
import org.alfresco.service.cmr.repository.NodeRef;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Tests for transaction related {@link HazelcastLockStore} functionality.
 * 
 * @author Matt Ward
 */
public class HazelcastLockStoreTxTest extends AbstractLockStoreTxTest<HazelcastLockStore>
{
    private static HazelcastInstance hz;
    
    @BeforeClass
    public static void setUpClass()
    {
        hz = Hazelcast.getDefaultInstance();
    }
    
    @AfterClass
    public static void tearDownClass()
    {
        hz.getLifecycleService().shutdown();
    }
    
    @Override
    protected HazelcastLockStore createLockStore()
    {
        IMap<NodeRef, LockState> map = hz.getMap(HazelcastLockStoreTest.class.getName()+".backingMap");
        // clear the backing map, to ensure test case isolation.
        map.clear();
        return new HazelcastLockStore(map);
    }
//
//    @Test
//    public void testReadsWhenNoTransaction() throws NotSupportedException, SystemException
//    {
//    	super.testReadsWhenNoTransaction();
//    }
}

