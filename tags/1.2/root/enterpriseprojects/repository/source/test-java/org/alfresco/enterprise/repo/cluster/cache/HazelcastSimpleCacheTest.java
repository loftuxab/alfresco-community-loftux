/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.AbstractMap;

import org.alfresco.repo.cache.SimpleCacheTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Tests for the HazelcastSimpleCache class.
 * 
 * @author Matt Ward
 */
public class HazelcastSimpleCacheTest extends SimpleCacheTestBase<HazelcastSimpleCache<Integer, String>>
{
    private static HazelcastInstance hz;

    @BeforeClass
    public static void setUpClass()
    {
        hz = Hazelcast.getDefaultInstance();        
    }
    
    @Override
    protected HazelcastSimpleCache<Integer, String> createCache()
    {
        IMap<Integer, AbstractMap.SimpleImmutableEntry<Integer, String>> map = hz.getMap("default");
        map.clear();
        return new HazelcastSimpleCache<Integer, String>(map);
    }

    @Test
    public void canActAsNullCacheWhenHazelcastInactive()
    {        
        Integer key = 20583; // random key 
        cache.put(key, "A cache value.");
        
        hz.getLifecycleService().shutdown();
        
        // Check sensible defaults are returned when cluster is inactive.
        try
        {            
            // Although this value is set, it can't be accessed.
            assertFalse(cache.contains(key));
            assertEquals(0, cache.getKeys().size());
            assertNull(cache.get(key));
            // No return value to check, just check we can invoke without exceptions.
            cache.put(key+1, "Another Value");
            cache.remove(key);
            cache.clear();
        }
        finally
        {
            // Restore for benefit of other tests
            hz = Hazelcast.getDefaultInstance();
        }
    }

}
