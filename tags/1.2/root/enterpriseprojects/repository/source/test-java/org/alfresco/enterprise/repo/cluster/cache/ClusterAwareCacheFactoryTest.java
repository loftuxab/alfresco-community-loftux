/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.alfresco.enterprise.repo.cluster.core.ClusterService;
import org.alfresco.enterprise.repo.cluster.core.ClusteredObjectProxyFactory;
import org.alfresco.enterprise.repo.cluster.core.ClusteredObjectProxyFactory.CacheProxyInvoker;
import org.alfresco.enterprise.repo.cluster.core.ClusteredObjectProxyFactory.ClusteredObjectProxyInvoker;
import org.alfresco.enterprise.repo.cluster.core.ClusteredObjectProxyFactory.InvalidateRemovalCacheProxyInvoker;
import org.alfresco.enterprise.repo.cluster.core.HazelcastInstanceFactory;
import org.alfresco.enterprise.repo.cluster.messenger.Messenger;
import org.alfresco.enterprise.repo.cluster.messenger.MessengerFactory;
import org.alfresco.repo.cache.CacheFactory;
import org.alfresco.repo.cache.DefaultSimpleCache;
import org.alfresco.repo.cache.SimpleCache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Tests for the {@link ClusterAwareCacheFactoryBean} class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class ClusterAwareCacheFactoryTest
{
    private ClusterAwareCacheFactory<String, String> factory;
    private @Mock CacheFactory<String, String> defaultCacheFactory;
    private @Mock HazelcastInstanceFactory hzInstanceFactory;
    private @Mock HazelcastInstance hzInstance;
    private @Mock IMap hzMap;
    private @Mock ClusterService clusterService;
    private @Mock MessengerFactory messengerFactory;
    private @Mock Messenger<InvalidationMessage> messenger;
    private @Mock Config config;
    private @Mock MapConfig mapConfig;
    private Properties props;
    
    @Before
    public void setUp() throws Exception
    {
        factory = new ClusterAwareCacheFactory<String, String>();
        factory.setHazelcastInstanceFactory(hzInstanceFactory);
        factory.setNonClusteredCacheFactory(defaultCacheFactory);
        factory.setMessengerFactory(messengerFactory);
        
        props = new Properties();
        factory.setProperties(props);
        
        Mockito.when(hzInstanceFactory.getInstance()).thenReturn(hzInstance);
        Mockito.when(hzInstance.getMap("org.alfresco.cache.propertyValueCache")).thenReturn(hzMap);
    }

    @Test
    public void canCreateCacheWhenClusteringDisabled() throws Exception
    {
        Mockito.when(hzInstanceFactory.isClusteringEnabled()).thenReturn(false);
        
        // Even though a fully distributed cache is requested, if clustering is
        // disabled then a local cache is returned.
        props.setProperty("caches.nonClusteredCache.cluster.type", "fully-distributed");
        
        DefaultSimpleCache<String, String> cache =
                    (DefaultSimpleCache<String, String>) factory.createCache("caches.nonClusteredCache");
        
        Mockito.verify(defaultCacheFactory).createCache("caches.nonClusteredCache");
    }


    @Test
    public void canCreateCacheWhenClusteringEnabledAndInitialised()
    {
        Mockito.when(clusterService.isInitialised()).thenReturn(true);
        factory.setClusterService(clusterService);
        
        Mockito.when(hzInstanceFactory.isClusteringEnabled()).thenReturn(true);
        Mockito.when(hzInstance.getConfig()).thenReturn(config);
        Mockito.when(config.getMapConfig("org.alfresco.cache.propertyValueCache")).thenReturn(mapConfig);
        
        props.setProperty("org.alfresco.cache.propertyValueCache.cluster.type", "fully-distributed");
        
        HazelcastSimpleCache<String, String> cache =
                    (HazelcastSimpleCache<String, String>) factory.createCache("org.alfresco.cache.propertyValueCache");
        
        Mockito.verify(hzInstance).getMap("org.alfresco.cache.propertyValueCache");
        assertEquals("org.alfresco.cache.propertyValueCache", cache.getCacheName());
    }
    
    @Test
    public void canOverrideHazelcastMapConfigWithProperties()
    {
        Mockito.when(clusterService.isInitialised()).thenReturn(true);
        factory.setClusterService(clusterService);
        
        Mockito.when(hzInstanceFactory.isClusteringEnabled()).thenReturn(true);
        Mockito.when(hzInstance.getConfig()).thenReturn(config);
        // TODO: temp
        mapConfig = new MapConfig();
        Mockito.when(config.getMapConfig("org.alfresco.cache.propertyValueCache")).thenReturn(mapConfig);
        
        // Simulate the existing config values (either defaults or from XML file, for example)
        mapConfig.setBackupCount(2);
        mapConfig.setEvictionPolicy("NONE");
        mapConfig.setEvictionPercentage(30);
        mapConfig.setMergePolicy("hz.HIGHER_BITS");
        mapConfig.setTimeToLiveSeconds(100);
        mapConfig.setMaxIdleSeconds(200);
        MaxSizeConfig maxSizeConfig = new MaxSizeConfig();
        // Test with non-default, as the policy should be kept - only the size will be changed.
        maxSizeConfig.setMaxSizePolicy(MaxSizeConfig.POLICY_MAP_SIZE_PER_JVM);
        maxSizeConfig.setSize(900);
        mapConfig.setMaxSizeConfig(maxSizeConfig);
        
        props.setProperty("org.alfresco.cache.propertyValueCache.cluster.type", "fully-distributed");
        // These are the properties that should be used to override the existing config values.
        props.setProperty("org.alfresco.cache.propertyValueCache.backup-count", "3");
        props.setProperty("org.alfresco.cache.propertyValueCache.async-backup-count", "2");
        props.setProperty("org.alfresco.cache.propertyValueCache.eviction-policy", "LFU");
        props.setProperty("org.alfresco.cache.propertyValueCache.eviction-percentage", "50");
        props.setProperty("org.alfresco.cache.propertyValueCache.merge-policy", "hz.LATEST_UPDATE");
        props.setProperty("org.alfresco.cache.propertyValueCache.timeToLiveSeconds", "500");
        props.setProperty("org.alfresco.cache.propertyValueCache.maxIdleSeconds", "600");
        props.setProperty("org.alfresco.cache.propertyValueCache.maxItems", "1800");
        props.setProperty("org.alfresco.cache.propertyValueCache.readBackupData", "true");
        
        HazelcastSimpleCache<String, String> cache =
                    (HazelcastSimpleCache<String, String>) factory.createCache("org.alfresco.cache.propertyValueCache");
        
        Mockito.verify(hzInstance).getMap("org.alfresco.cache.propertyValueCache");
        
        // Verify new settings are made
        assertEquals(3, mapConfig.getBackupCount());
        assertEquals(2, mapConfig.getAsyncBackupCount());
        assertEquals("LFU", mapConfig.getEvictionPolicy());
        assertEquals(50, mapConfig.getEvictionPercentage());
        assertEquals("hz.LATEST_UPDATE", mapConfig.getMergePolicy());
        assertEquals(500, mapConfig.getTimeToLiveSeconds());
        assertEquals(600, mapConfig.getMaxIdleSeconds());
        assertEquals(1800, mapConfig.getMaxSizeConfig().getSize());
        assertEquals(MaxSizeConfig.POLICY_MAP_SIZE_PER_JVM, mapConfig.getMaxSizeConfig().getMaxSizePolicy());
        assertEquals(true, mapConfig.isReadBackupData());
        
        assertEquals("org.alfresco.cache.propertyValueCache", cache.getCacheName());
    }
    
    @Test
    public void canCreateProxyCacheWhenClusteringEnabledButNotInitialised()
    {
        factory.setClusterService(null);
        SimpleCache<String, String> backingCache = new DefaultSimpleCache<String, String>(100, "cache.propertyValueCache");
        Mockito.when(defaultCacheFactory.createCache("cache.propertyValueCache")).thenReturn(backingCache);
        Mockito.when(hzInstanceFactory.isClusteringEnabled()).thenReturn(true);
        
        props.setProperty("cache.propertyValueCache.cluster.type", "fully-distributed");
        
        @SuppressWarnings("rawtypes")
        SimpleCache cache = (SimpleCache) factory.createCache("cache.propertyValueCache");
        
        // Not a Hazelcast Map yet
        Mockito.verify(hzInstance, Mockito.never()).getMap("cache.propertyValueCache");
        // Check proxied cache was stored in ClusteredObjectProxyFactory.
        boolean foundProxy = false;
        for (ClusteredObjectProxyInvoker<?> invoker : ClusteredObjectProxyFactory.invokers)
        {
            if (invoker instanceof CacheProxyInvoker)
            {
                @SuppressWarnings("rawtypes")
                CacheProxyInvoker cacheInvoker = (CacheProxyInvoker) invoker;
                if (cacheInvoker.cacheName.equals("cache.propertyValueCache"))
                {
                    assertSame(backingCache, cacheInvoker.getBackingObject());
                    foundProxy = true;
                    break;
                }
            }
        }
        if (!foundProxy)
        {
            fail("Did not find proxy for cache.propertyValueCache");
        }
    }
    
    @Test
    public void canCreateLocalCacheWhenClusteringDisabled() throws Exception
    {
        Mockito.when(hzInstanceFactory.isClusteringEnabled()).thenReturn(false);
        
        props.setProperty("caches.localCache.cluster.type", "local");
        
        DefaultSimpleCache<String, String> cache =
                    (DefaultSimpleCache<String, String>) factory.createCache("caches.localCache");
        
        Mockito.verify(defaultCacheFactory).createCache("caches.localCache");
    }
    
    @Test
    public void canCreateLocalCacheWhenClusteringEnabled()
    {
        Mockito.when(hzInstanceFactory.isClusteringEnabled()).thenReturn(true);

        props.setProperty("caches.localCache.cluster.type", "local");
        
        DefaultSimpleCache<String, String> cache =
                    (DefaultSimpleCache<String, String>) factory.createCache("caches.localCache");
        
        Mockito.verify(defaultCacheFactory).createCache("caches.localCache");
    }
    
    @Test
    public void canCreateInvalidateRemovalCacheWhenClusteringDisabled()
    {
        Mockito.when(hzInstanceFactory.isClusteringEnabled()).thenReturn(false);
        
        props.setProperty("myCache.cluster.type", "invalidating");
        
        DefaultSimpleCache<String, String> cache =
                    (DefaultSimpleCache<String, String>) factory.createCache("myCache");
        
        // A localCache is created
        Mockito.verify(defaultCacheFactory).createCache("myCache");
    }
    
    @Test
    public void canCreateInvalidateRemovalCacheWhenClusteringEnabledAndInitialised()
    {
        Mockito.when(clusterService.isInitialised()).thenReturn(true);
        factory.setClusterService(clusterService);
        
        Mockito.when(hzInstanceFactory.isClusteringEnabled()).thenReturn(true);
        
        Mockito.when(messengerFactory.<InvalidationMessage>createMessenger("myCache.invalidation")).thenReturn(messenger);

        props.setProperty("myCache.cluster.type", "invalidating");
        
        InvalidatingCache<String, String> cache =
                    (InvalidatingCache<String, String>) factory.createCache("myCache");
        
        // A localCache is created
        Mockito.verify(defaultCacheFactory).createCache("myCache");
        // TODO: introduce a NamedCache interface (extends SimpleCache)?
        //assertEquals("myCache", cache.getCacheName());
        
        // An invalidation channel was created
        Mockito.verify(messengerFactory).createMessenger("myCache.invalidation");
        // The messenger was initialised
        Mockito.verify(messenger).setReceiver(cache);
    }
    
    @Test
    public void canCreateInvalidateRemovalCacheWhenClusteringEnabledButNotInitialised()
    {
        factory.setClusterService(null);
        SimpleCache<String, String> backingCache = new DefaultSimpleCache<String, String>(100, "myCache");
        Mockito.when(defaultCacheFactory.createCache("myCache")).thenReturn(backingCache);
        Mockito.when(hzInstanceFactory.isClusteringEnabled()).thenReturn(true);
        
        props.setProperty("myCache.cluster.type", "invalidating");
        
        @SuppressWarnings("rawtypes")
        SimpleCache cache = (SimpleCache) factory.createCache("myCache");
        
        // No invalidation channel yet
        Mockito.verify(messengerFactory, Mockito.never()).createMessenger("myCache.invalidation");
        // Check proxied cache was stored in ClusteredObjectProxyFactory.
        boolean foundProxy = false;
        for (ClusteredObjectProxyInvoker<?> invoker : ClusteredObjectProxyFactory.invokers)
        {
            if (invoker instanceof InvalidateRemovalCacheProxyInvoker)
            {
                @SuppressWarnings("rawtypes")
                InvalidateRemovalCacheProxyInvoker cacheInvoker = (InvalidateRemovalCacheProxyInvoker) invoker;
                if (cacheInvoker.cacheName.equals("myCache"))
                {
                    assertSame(backingCache, cacheInvoker.getBackingObject());
                    foundProxy = true;
                    break;
                }
            }
        }
        if (!foundProxy)
        {
            fail("Did not find proxy for myCache");
        }
    }
}
