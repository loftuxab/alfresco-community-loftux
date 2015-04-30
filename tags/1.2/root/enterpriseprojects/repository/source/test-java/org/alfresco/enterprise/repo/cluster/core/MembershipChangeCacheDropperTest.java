package org.alfresco.enterprise.repo.cluster.core;

import static org.junit.Assert.assertEquals;

import org.alfresco.repo.cache.DefaultSimpleCache;
import org.alfresco.repo.cache.SimpleCache;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link MembershipChangeCacheDropper} class.
 * 
 * @author Matt Ward
 */
public class MembershipChangeCacheDropperTest
{
    MembershipChangeCacheDropper cacheDropper;
    private SimpleCache<String, String> invalidating1;
    private SimpleCache<String, String> invalidating2;
    private SimpleCache<String, String> invalidating3;
    private SimpleCache<String, String> fullydist1;
    private SimpleCache<String, String> fullydist2;
    private SimpleCache<String, String> fullydist3;
    
    @Before
    public void setUp() throws Exception
    {
        cacheDropper = new MembershipChangeCacheDropper();
        
        ClusteredObjectProxyFactory.clear();
        
        // Create some caches...
        invalidating1 = createInvalidatingCache("invalidating1");
        invalidating2 = createInvalidatingCache("invalidating2");
        invalidating3 = createInvalidatingCache("invalidating3");
        fullydist1 = createDistributedCache("fullydist1");
        fullydist2 = createDistributedCache("fullydist2");
        fullydist3 = createDistributedCache("fullydist3");
        
        // Add some values
        invalidating1.put("1", "value1");
        invalidating2.put("2", "value2");
        invalidating3.put("3", "value3");
        
        fullydist1.put("1", "value1");
        fullydist2.put("2", "value2");
        fullydist3.put("3", "value3");
        
        // Enable dropping
        cacheDropper.setEnabled(true);
    }
    

    @Test
    public void cachesAreDroppedOnMemberLeaving()
    {
        cacheDropper.memberRemoved(null);
        
        assertEquals(null, invalidating1.get("1"));
        assertEquals(null, invalidating2.get("2"));
        assertEquals(null, invalidating3.get("3"));
        assertEquals("value1", fullydist1.get("1"));
        assertEquals("value2", fullydist2.get("2"));
        assertEquals("value3", fullydist3.get("3"));
    }
    
    @Test
    public void cachesAreDroppedOnMemberJoining()
    {   
        cacheDropper.memberAdded(null);
        
        assertEquals(null, invalidating1.get("1"));
        assertEquals(null, invalidating2.get("2"));
        assertEquals(null, invalidating3.get("3"));
        assertEquals("value1", fullydist1.get("1"));
        assertEquals("value2", fullydist2.get("2"));
        assertEquals("value3", fullydist3.get("3"));
    }
    
    @Test
    public void cachesAreNotDroppedOnMemberLeavingWhenBehaviourIsDisabled()
    {
        cacheDropper.setEnabled(false);
        
        cacheDropper.memberRemoved(null);
        
        assertEquals("value1", invalidating1.get("1"));
        assertEquals("value2", invalidating2.get("2"));
        assertEquals("value3", invalidating3.get("3"));
        assertEquals("value1", fullydist1.get("1"));
        assertEquals("value2", fullydist2.get("2"));
        assertEquals("value3", fullydist3.get("3"));
    }
    
    @Test
    public void cachesAreNotDroppedOnMemberJoiningWhenBehaviourIsDisabled()
    {
        cacheDropper.setEnabled(false);
        
        cacheDropper.memberAdded(null);
        
        assertEquals("value1", invalidating1.get("1"));
        assertEquals("value2", invalidating2.get("2"));
        assertEquals("value3", invalidating3.get("3"));
        assertEquals("value1", fullydist1.get("1"));
        assertEquals("value2", fullydist2.get("2"));
        assertEquals("value3", fullydist3.get("3"));
    }
    
    private SimpleCache<String, String> createInvalidatingCache(String name)
    {
        SimpleCache<String, String> cache = ClusteredObjectProxyFactory.
                    createInvalidateRemovalCacheProxy(name, new DefaultSimpleCache<String, String>());
        return cache;
    }
    
    private SimpleCache<String, String> createDistributedCache(String name)
    {
        SimpleCache<String, String> cache = ClusteredObjectProxyFactory.
                    createCacheProxy(name, new DefaultSimpleCache<String, String>());
        return cache;
    }
}
