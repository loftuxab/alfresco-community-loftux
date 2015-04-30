/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cache;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.alfresco.enterprise.repo.cluster.core.ClusteredObjectProxyFactory;
import org.alfresco.enterprise.repo.cluster.core.ClusteringBootstrap;
import org.alfresco.repo.cache.CacheFactory;
import org.alfresco.repo.cache.DefaultSimpleCache;
import org.alfresco.repo.cache.HibernateSimpleCacheAdapter;
import org.alfresco.repo.cache.SimpleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.hibernate.cache.Timestamper;

/**
 * Hibernate cache provider: creates proxied {@link DefaultSimpleCache} when spring not
 * fully initialized and creates caches using the supplied {@link CacheFactory} otherwise.
 * <p>
 * Proxied DefaultSimpleCache objects are swapped for clustered caches by {@link ClusteringBootstrap}.
 * 
 * @author Matt Ward
 */
public class HibernateCacheProvider implements CacheProvider
{
    private static final Log log = LogFactory.getLog(HibernateCacheProvider.class); 
    private static CacheFactory<Serializable, Object> clusteredCacheFactory;
    private static boolean initialized;
    private static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    
    @Override
    public Cache buildCache(String regionName, Properties properties) throws CacheException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Building hibernate cache, region: " + regionName + ", properties: " + properties);
        }
        ReadLock readLock = rwLock.readLock();
        try
        {
            readLock.lock();
            SimpleCache<Serializable, Object> simpleCache;
            if (initialized)
            {
                // Use the spring-supplied cache factory
                simpleCache = clusteredCacheFactory.createCache(regionName);
            }
            else
            {
                // Not yet initialised, so create proxied cache.
                // First create the underlying DefaultSimpleCache, this is not ideal, as we are not
                // able to use the proper configuration due to spring being unavailable (e.g. maxSize).
                DefaultSimpleCache<Serializable, Object> cache = new DefaultSimpleCache<Serializable, Object>();
                cache.setCacheName(regionName);
                simpleCache = ClusteredObjectProxyFactory.createCacheProxy(regionName, cache);
            }
            HibernateSimpleCacheAdapter hibernateCache = new HibernateSimpleCacheAdapter(simpleCache, regionName);
            return hibernateCache;
        }
        finally
        {
            readLock.unlock();
        }
    }

    @Override
    public long nextTimestamp()
    {
        return Timestamper.next();
    }

    @Override
    public void start(Properties properties) throws CacheException
    {
        log.debug("Starting CacheProvider");
    }

    @Override
    public void stop()
    {
        log.debug("Stopping CacheProvider");
    }

    @Override
    public boolean isMinimalPutsEnabledByDefault()
    {
        return false;
    }

    /**
     * Provide this class with a clustered cache factory.
     * 
     * @param clusteredCacheFactory
     */
    public void setClusteredCacheFactory(CacheFactory<Serializable, Object> cacheFactory)
    {
        HibernateCacheProvider.clusteredCacheFactory = cacheFactory;
    }
    
    public void initCacheProvider()
    {
        if (clusteredCacheFactory == null)
        {
            throw new IllegalStateException("cacheFactory property cannot be null.");
        }
        WriteLock writeLock = rwLock.writeLock();
        try
        {
            writeLock.lock();
            initialized = true;
        }
        finally
        {
            writeLock.unlock();
        }
    }
}
