/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import org.alfresco.enterprise.repo.cluster.messenger.MessageReceiver;
import org.alfresco.enterprise.repo.cluster.messenger.Messenger;
import org.alfresco.repo.cache.DefaultSimpleCache;
import org.alfresco.repo.cache.SimpleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A {@link SimpleCache} implementation that may be used in a cluster and uses a {@link Messenger} to
 * send/receive invalidation messages between peers on different cluster members. All removal operations,
 * e.g. remove(k), clear() etc., will cause an invalidation message to be sent to the other cluster members
 * as well as put(k,v) when contains(k), i.e. puts result in an invalidation message when a value
 * exists for key k.
 * 
 * @author Matt Ward
 */
public class InvalidatingCache<K extends Serializable, V>
        implements SimpleCache<K, V>, MessageReceiver<InvalidationMessage>
{
    private static final Log log = LogFactory.getLog(InvalidatingCache.class);
    
    private DefaultSimpleCache<K, V> cache;
    private Messenger<InvalidationMessage> messenger;
    
    /**
     * Constructor.
     * 
     * @param cache
     * @param messenger
     */
    public InvalidatingCache(DefaultSimpleCache<K, V> cache, Messenger<InvalidationMessage> messenger)
    {
        this.cache = cache;
        this.messenger = messenger;
    }

    /**
     * Call post-construction to enable reception of invalidation messages.
     */
    public void init()
    {
        messenger.setReceiver(this);        
    }
    
    @Override
    public boolean contains(K key)
    {
        return cache.contains(key);
    }

    @Override
    public Collection<K> getKeys()
    {
        return cache.getKeys();
    }

    @Override
    public V get(K key)
    {
        return cache.get(key);
    }

    @Override
    public void put(K key, V value)
    {
        boolean isUpdate = cache.putAndCheckUpdate(key, value);
        
        if (isUpdate)
        {
            invalidateKey(key);
        }
    }

    @Override
    public void remove(K key)
    {
        cache.remove(key);
        invalidateKey(key);
    }

    @Override
    public void clear()
    {
        cache.clear();
        invalidateEntireCache();
    }
    
    /**
     * Handle a cache invalidation message.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onReceive(InvalidationMessage message)
    {
        if (message.invalidatesAllKeys())
        {
            if (log.isTraceEnabled())
            {
                log.trace(
                        "RECEIVING: Invalidate entire cache: \n" +
                        "   Messenger: " + messenger);
            }
            cache.clear();
        }
        else if (message.invalidatesSingleKey())
        {
            if (log.isTraceEnabled())
            {
                log.trace(
                        "RECEIVING: Invalidate cache entry: \n" +
                        "   Messenger: " + messenger + "\n" +
                        "   Key:       " + message.staleKey());
            }
            Serializable key = message.staleKey();
            cache.remove((K) key);
        }
        else
        {
            throw new RuntimeException("Invalidation message has no appropriate handler: " + message);
        }
    }
    
    /**
     * Invalidate the entire cache, i.e. all the keys are now invalid.
     */
    private void invalidateEntireCache()
    {
        if (log.isTraceEnabled())
        {
            log.trace(
                    "SENDING: Invalidate entire cache: \n" +
                    "   Messenger: " + messenger);
        }
        messenger.send(InvalidationMessage.forAllKeys());
    }

    /**
     * Mark a key invalid.
     */
    private void invalidateKey(K key)
    {
        if (log.isTraceEnabled())
        {
            log.trace(
                    "SENDING: Invalidate cache entry: \n" +
                    "   Messenger: " + messenger + "\n" +
                    "   Key:       " + key);
        }
        messenger.send(InvalidationMessage.forKey(key));
    }

}
