package org.alfresco.enterprise.repo.cluster.lock;

import static org.junit.Assert.*;

import java.util.Date;

import org.alfresco.repo.lock.mem.AbstractLockStoreTestBase;
import org.alfresco.repo.lock.mem.Lifetime;
import org.alfresco.repo.lock.mem.LockState;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.repository.NodeRef;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class HazelcastLockStoreTest extends AbstractLockStoreTestBase<HazelcastLockStore>
{
    private static HazelcastInstance hz;
    private static IMap<NodeRef, LockState> map;
    
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
        map = hz.getMap(HazelcastLockStoreTest.class.getName()+".backingMap");
        // clear the backing map, to ensure test case isolation.
        map.clear();
        return new HazelcastLockStore(map);
    }
    
    @Test
    public void lockStoreCanFunctionWhenHazelcastInactive()
    {
        NodeRef nodeRef = new NodeRef("workspace://SpacesStore/f8cc8568-1f5c-45c8-bee8-2c1be31983f2");
        lockStore.set(nodeRef, LockState.createLock(nodeRef, LockType.WRITE_LOCK, "jbloggs",
                    new Date(), Lifetime.EPHEMERAL, null));
        
        hz.getLifecycleService().shutdown();
        
        // Exercise all the cluster-related methods with the cluster shutdown and check sensible
        // defaults are returned, and that unexpected exceptions aren't thrown. 
        try
        {            
            // Although this value is set, it can't be accessed.
            assertNull(lockStore.get(nodeRef));
            assertEquals(0, lockStore.getNodes().size());
            assertNull(lockStore.get(nodeRef));
            // No return value to check, just check we can invoke without exceptions.
            lockStore.set(nodeRef, LockState.createUnlocked(nodeRef));
            lockStore.clear();
        }
        finally
        {
            // Restore for benefit of other tests
            hz = Hazelcast.getDefaultInstance();
        }
    }
}
