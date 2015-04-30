/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.cache;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.enterprise.repo.cluster.core.ClusterService;
import org.alfresco.enterprise.repo.cluster.core.ClusteredObjectProxyFactory;
import org.alfresco.enterprise.repo.cluster.core.HazelcastInstanceFactory;
import org.alfresco.enterprise.repo.cluster.messenger.Messenger;
import org.alfresco.enterprise.repo.cluster.messenger.MessengerFactory;
import org.alfresco.repo.cache.AbstractCacheFactory;
import org.alfresco.repo.cache.CacheFactory;
import org.alfresco.repo.cache.DefaultSimpleCache;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * {@link CacheFactory} capable of creating both non-clustered and clustered caches depending
 * on whether clustering is enabled.
 * 
 * @author Matt Ward
 * @since 4.2
 */
public class ClusterAwareCacheFactory<K extends Serializable, V> extends AbstractCacheFactory<K, V>
{
    private static final String CACHE_TYPE_LOCAL = "local";
    private static final String CACHE_TYPE_DISTRIBUTED = "fully-distributed";
    private static final String CACHE_TYPE_INVALIDATING = "invalidating";
    private static final Log log = LogFactory.getLog(ClusterAwareCacheFactory.class);
    private HazelcastInstanceFactory hzInstanceFactory;
    private CacheFactory<K, V> nonClusteredCacheFactory;
    private ClusterService clusterService;
    private MessengerFactory messengerFactory;
    
    @Override
    public synchronized SimpleCache<K, V> createCache(String cacheName)
    {
        SimpleCache<K, V> cache;
        String cacheType = getProperty(cacheName, "cluster.type", "<not set>");
        
        if (cacheType.equals(CACHE_TYPE_INVALIDATING))
        {
            cache = createInvalidateRemovalCache(cacheName);
        }
        else if (cacheType.equals(CACHE_TYPE_DISTRIBUTED))
        {
            cache = createDistributedCache(cacheName);
        }
        else if (cacheType.equals(CACHE_TYPE_LOCAL))
        {
            cache = createLocalCache(cacheName);
        }
        else
        {
            throw new IllegalArgumentException("Invalid cache type '" + cacheType + "' for cache " + cacheName);
        }
        return cache;
    }

    private SimpleCache<K, V> createDistributedCache(String cacheName)
    {
        SimpleCache<K, V> cache;
        if (hzInstanceFactory.isClusteringEnabled())
        {
            if (clusterService != null && clusterService.isInitialised())
            {
                cache = createClusteredCache(cacheName);
            }
            else
            {
                cache = createProxiedCache(cacheName);
            }
        }
        else
        {
            cache = createNonClusteredCache(cacheName);
        }
        return cache;
    }
    
    private SimpleCache<K, V> createLocalCache(String cacheName)
    {
        return createNonClusteredCache(cacheName);
    }
    
    private SimpleCache<K, V> createInvalidateRemovalCache(String cacheName)
    {
        return createInvalidatingCache(cacheName, true);
    }
    
    private SimpleCache<K, V> createInvalidatingCache(String cacheName, boolean removeOnly)
    {
        if (hzInstanceFactory.isClusteringEnabled())
        {
            if (clusterService != null && clusterService.isInitialised())
            {
                return createClusteredInvalidatingCache(cacheName);
            }
            else
            {
                return createProxiedInvalidatingCache(cacheName);
            }
        }
        else
        {
            return createNonClusteredCache(cacheName);
        }
    }


    private SimpleCache<K, V> createClusteredInvalidatingCache(String cacheName)
    {
        Messenger<InvalidationMessage> messenger = messengerFactory.createMessenger(cacheName + ".invalidation");
        DefaultSimpleCache<K, V> localCache = (DefaultSimpleCache<K, V>) createNonClusteredCache(cacheName);
        InvalidatingCache<K, V> cache = new InvalidatingCache<K, V>(localCache, messenger);
        cache.init();
        if (log.isDebugEnabled())
        {
            log.debug("Creating clustered invalidating cache " + cacheName);
        }
        return cache;
    }
    
    private SimpleCache<K, V> createProxiedInvalidatingCache(String cacheName)
    {
        SimpleCache<K, V> cache = createNonClusteredCache(cacheName);
        SimpleCache<K, V> proxiedCache;
        proxiedCache = ClusteredObjectProxyFactory.createInvalidateRemovalCacheProxy(cacheName, cache);
        return proxiedCache;
    }

    private SimpleCache<K, V> createProxiedCache(String cacheName)
    {
        SimpleCache<K, V> cache = createNonClusteredCache(cacheName);
        SimpleCache<K, V> proxiedCache = ClusteredObjectProxyFactory.createCacheProxy(cacheName, cache);
        return proxiedCache;
    }

    private SimpleCache<K, V> createNonClusteredCache(String cacheName)
    {
        // Logging is done by peer object, e.g. "Creating cache: ..."
        return nonClusteredCacheFactory.createCache(cacheName);
    }

    private SimpleCache<K, V> createClusteredCache(String cacheName)
    {
        HazelcastInstance hz = hzInstanceFactory.getInstance();
        applyPropertyConfig(cacheName);
        IMap<K, AbstractMap.SimpleImmutableEntry<K, V>> map = hz.getMap(cacheName);
        HazelcastSimpleCache<K, V> cache = new HazelcastSimpleCache<K, V>(map);
        cache.setCacheName(cacheName);
        if (log.isDebugEnabled())
        {
            log.debug("Creating clustered cache " + cacheName);
        }
        return cache;
    }
    
    /**
     * Override (supported) elements of the IMap's configuration as specified
     * by properties, e.g. the &lt;backup-count&gt; element configured
     * in XML may be overridden using the property <code>root.cache.name.backup-count</code>
     * where <code>root.cache.name</code> is the name of the cache.
     * 
     * @param cacheName
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void applyPropertyConfig(String cacheName)
    {
        HazelcastInstance hz = hzInstanceFactory.getInstance();
        MapConfig mapConfig = hz.getConfig().getMapConfig(cacheName);
        
        // Map cache property name (as appears in caches.properties for example) to
        // corresponding MapConfig bean property. For example, if:
        //     cache.ticketsCache.backup-count=2
        // appears in a property file, then
        //     mapConfig.setBackupCount(2)
        // will be used to override the base configuration.
        Map<String, Pair<String, Class>> supportedConfig = new HashMap<String, Pair<String, Class>>();

        supportedConfig.put("backup-count", new Pair<String, Class>("setBackupCount", int.class));
        supportedConfig.put("async-backup-count", new Pair<String, Class>("setAsyncBackupCount", int.class));
        supportedConfig.put("eviction-policy", new Pair<String, Class>("setEvictionPolicy", String.class));
        supportedConfig.put("eviction-percentage", new Pair<String, Class>("setEvictionPercentage", int.class));
        supportedConfig.put("merge-policy", new Pair<String, Class>("setMergePolicy", String.class));
        supportedConfig.put("timeToLiveSeconds", new Pair<String, Class>("setTimeToLiveSeconds", int.class));
        supportedConfig.put("maxIdleSeconds", new Pair<String, Class>("setMaxIdleSeconds", int.class));
        supportedConfig.put("maxItems", new Pair<String, Class>("", int.class));
        supportedConfig.put("readBackupData", new Pair<String, Class>("setReadBackupData", boolean.class));
        supportedConfig.put("nearCache.maxSize", new Pair<String, Class>("setMaxSize", int.class));
        supportedConfig.put("nearCache.maxIdleSeconds", new Pair<String, Class>("setMaxIdleSeconds", int.class));
        supportedConfig.put("nearCache.timeToLiveSeconds", new Pair<String, Class>("setTimeToLiveSeconds", int.class));

        for (String cachePropName : supportedConfig.keySet())
        {
            String propValue = getProperty(cacheName, cachePropName, null);
            if (propValue != null)
            {
                String configMethodName = supportedConfig.get(cachePropName).getFirst();
                Class configPropType = supportedConfig.get(cachePropName).getSecond();
                try
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Overriding cache config property: cache name=" + cacheName +
                                    ", property=" + cachePropName +
                                    ", value=" + propValue);
                    }
                    Object convertedPropVal;
                    if (configPropType.equals(int.class))
                    {
                        convertedPropVal = Integer.valueOf(propValue);
                    }
                    else if (configPropType.equals(boolean.class))
                    {
                        convertedPropVal = Boolean.valueOf(propValue);
                    }
                    else
                    {
                        convertedPropVal = DefaultTypeConverter.INSTANCE.convert(configPropType, propValue);
                    }
                	if (cachePropName.startsWith("nearCache"))
                    {
                        // Unless we set an entirely new NearCache object, the maxItems change
                        // will have no effect.
                        NearCacheConfig nearCacheConfig = mapConfig.getNearCacheConfig();
                        if(nearCacheConfig == null)
                        {
                        	nearCacheConfig = new NearCacheConfig();
                        	mapConfig.setNearCacheConfig(nearCacheConfig);
                        }

                        Method setterMethod = nearCacheConfig.getClass().getDeclaredMethod(configMethodName, configPropType);
                        if(setterMethod == null)
                        {
                        	throw new IllegalArgumentException("Misconfigured cache.properties, unknown setter " + configMethodName);
                        }
                        setterMethod.invoke(nearCacheConfig, convertedPropVal);
                    }
                	else
                	{
	                    if (cachePropName.equals("maxItems"))
	                    {
	                        // Unless we set an entirely new MaxSizeConfig object, the maxItems change
	                        // will have no effect.
	                        MaxSizeConfig oldMaxSizeConfig = mapConfig.getMaxSizeConfig();
	                        MaxSizeConfig maxSizeConfig = new MaxSizeConfig();
	                        // Alter the size, but retain whatever policy was put in place.
	                        maxSizeConfig.setMaxSizePolicy(oldMaxSizeConfig.getMaxSizePolicy());
	                        maxSizeConfig.setSize((Integer)convertedPropVal);
	                        mapConfig.setMaxSizeConfig(maxSizeConfig);
	                	}
	                    else
	                    {
	                        // Unfortunately, we can't use Apache BeanUtils or similar here, since the MapConfig
	                        // doesn't entirely conform to the JavaBean rules.
	                        Method setterMethod = mapConfig.getClass().getDeclaredMethod(configMethodName, configPropType);
	                        setterMethod.invoke(mapConfig, convertedPropVal);
	                    }
                	}
                }
                catch (Exception e)
                {
                    if (log.isErrorEnabled())
                    {
                        log.error("Unable to set cache config property: cache name=" + cacheName +
                                    ", property=" + cachePropName +
                                    ", value=" + propValue,
                                    e);
                    }
                }
            }
        }
    }

    public void setHazelcastInstanceFactory(HazelcastInstanceFactory hzInstanceFactory)
    {
        this.hzInstanceFactory = hzInstanceFactory;
    }

    public void setNonClusteredCacheFactory(CacheFactory<K, V> nonClusteredCacheFactory)
    {
        this.nonClusteredCacheFactory = nonClusteredCacheFactory;
    }

    /**
     * Do not set this in Spring configuration. This should only be set during cluster initialisation.
     */
    public synchronized void setClusterService(ClusterService clusterService)
    {
        this.clusterService = clusterService;
    }

    /**
     * Used to create communications channels for cache invalidation (invalidating caches only).
     */
    public void setMessengerFactory(MessengerFactory messengerFactory)
    {
        this.messengerFactory = messengerFactory;
    }
}
