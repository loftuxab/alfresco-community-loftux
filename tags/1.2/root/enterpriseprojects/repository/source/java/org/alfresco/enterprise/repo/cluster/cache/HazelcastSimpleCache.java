/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.cache;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;

import org.alfresco.repo.cache.SimpleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;

import com.hazelcast.core.IMap;
import com.hazelcast.core.OperationTimeoutException;

/**
 * {@link SimpleCache} implementation backed by a Hazelcast {@link IMap}
 * 
 * @author Matt Ward
 */
public final class HazelcastSimpleCache<K extends Serializable, V> implements SimpleCache<K, V>, BeanNameAware
{
    private static Log log = LogFactory.getLog(HazelcastSimpleCache.class);
    private IMap<K, AbstractMap.SimpleImmutableEntry<K, V>> map;
    private String cacheName;
    
    /**
     * Create a {@link HazelcastSimpleCache} backed by the injected {@link IMap}.
     * 
     * @param map
     */
    public HazelcastSimpleCache(IMap<K, AbstractMap.SimpleImmutableEntry<K, V>> map)
    {
        this.map = map;
    }

    @Override
    public boolean contains(K key)
    {
        try
        {            
            return map.containsKey(key);
        }
        catch (IllegalStateException e)
        {
            log.warn("Cluster is inactive but contains(key) was called for cache " + toString() + ", key=" + key);
            return false;
        }
    }

    @Override
    public Collection<K> getKeys()
    {
        try
        {            
            return map.keySet();
        }
        catch (IllegalStateException e)
        {
            log.warn("Cluster is inactive but getKeys() was called for cache " + toString());
            return Collections.emptyList();
        }
    }

    @Override
    public V get(K key)
    {
        AbstractMap.SimpleImmutableEntry<K, V> kvp;
        try
        {
            kvp = map.get(key);
        }
        catch (IllegalStateException e)
        {
            log.warn("Cluster is inactive but get(key) was called for cache " + toString() + ", key=" + key);
            kvp = null;
        }
        catch (OperationTimeoutException toe)
        {
            log.warn("Can't get value from cluster for cache " + toString() + ", key=" + key);
            kvp = null;
        }
        
        if (kvp == null)
        {
            return null;
        }
        return kvp.getValue();
    }

    @Override
    public void put(K key, V value)
    {
        AbstractMap.SimpleImmutableEntry<K, V> kvp = new AbstractMap.SimpleImmutableEntry<K, V>(key, value);
        try
        {
            map.put(key, kvp);
        }
        catch (IllegalStateException e)
        {
            log.warn("Cluster is inactive but put(k,v) was called for cache " + toString());
        }
    }

    @Override
    public void remove(K key)
    {
        try
        {
            map.remove(key);
        }
        catch (IllegalStateException e)
        {
            log.warn("Cluster is inactive remove(key) was called for cache " + toString());
        }
    }

    @Override
    public void clear()
    {
        try
        {
            map.clear();
        }
        catch (IllegalStateException e)
        {
            log.warn("Cluster is inactive but clear() was called for cache " + toString());
        }
    }
    
    /**
     * Retrieve the name of this cache.
     * 
     * @see #setCacheName(String)
     * @return the cacheName
     */
    public String getCacheName()
    {
        return this.cacheName;
    }

    /**
     * Since there are many cache instances, it is useful to be able to associate
     * a name with each one.
     * 
     * @see #setBeanName(String)
     * @param cacheName
     */
    public void setCacheName(String cacheName)
    {
        this.cacheName = cacheName;
    }
    
    /**
     * Since there are many cache instances, it is useful to be able to associate
     * a name with each one.
     * 
     * @param cacheName Set automatically by Spring, but can be set manually if required.
     */
    @Override
    public void setBeanName(String cacheName)
    {
        this.cacheName = cacheName;
    }
    
    @Override
    public String toString()
    {
        return "HazelcastSimpleCache[cacheName=" + cacheName + "]";
    }
}
