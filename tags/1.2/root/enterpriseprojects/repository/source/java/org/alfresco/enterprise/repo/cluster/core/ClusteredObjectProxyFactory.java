/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.enterprise.repo.cluster.cache.ClusterAwareCacheFactory;
import org.alfresco.enterprise.repo.cluster.lock.ClusterAwareLockStoreFactory;
import org.alfresco.enterprise.repo.cluster.messenger.HazelcastMessengerFactory;
import org.alfresco.enterprise.repo.cluster.messenger.MessageReceiver;
import org.alfresco.enterprise.repo.cluster.messenger.Messenger;
import org.alfresco.enterprise.repo.cluster.messenger.MessengerFactory;
import org.alfresco.repo.cache.CacheFactory;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.lock.mem.LockState;
import org.alfresco.repo.lock.mem.LockStore;
import org.alfresco.repo.lock.mem.LockStoreFactory;
import org.alfresco.repo.webdav.LockInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Creates and manages proxied clustered data structures.
 * 
 * @author Matt Ward
 */
public abstract class ClusteredObjectProxyFactory
{
    private static final Log log = LogFactory.getLog(ClusteredObjectProxyFactory.class);
    public final static List<ClusteredObjectProxyInvoker<?>> invokers = new ArrayList<ClusteredObjectProxyInvoker<?>>(100);
    
    
    public synchronized static <T extends Serializable> Messenger<T> createMessengerProxy(
                Messenger<T> messenger,
                String appRegion,
                boolean acceptLocalMessages)
    {
        MessengerProxyInvoker<T> invoker = new MessengerProxyInvoker<T>(messenger, appRegion, acceptLocalMessages);
        Messenger<T> proxy = createProxy(invoker);
        return proxy;
    }
    
    public synchronized static <K extends Serializable, V> SimpleCache<K, V> createCacheProxy(
                String cacheName,
                SimpleCache<K, V> cache)
    {
        CacheProxyInvoker<K, V> invoker = new CacheProxyInvoker<K, V>(cache, cacheName);
        SimpleCache<K, V> proxy = createProxy(invoker);
        return proxy;
    }
    
    public synchronized static <K extends Serializable, V> SimpleCache<K, V> createInvalidateRemovalCacheProxy(
                String cacheName,
                SimpleCache<K, V> cache)
    {
        InvalidateRemovalCacheProxyInvoker<K, V> invoker = new InvalidateRemovalCacheProxyInvoker<K, V>(cache, cacheName);
        SimpleCache<K, V> proxy = createProxy(invoker);
        return proxy;
    }
    
    public synchronized static LockStore createLockStoreProxy(LockStore lockStore)
    {
        LockStoreProxyInvoker invoker = new LockStoreProxyInvoker(lockStore);
        LockStore proxy = createProxy(invoker);
        return proxy;
    }
    
    @SuppressWarnings("unchecked")
    public synchronized static <T> T createProxy(ClusteredObjectProxyInvoker<T> invoker)
    {
        invokers.add(invoker);
        T proxy = (T) Proxy.newProxyInstance(
                    ClusteredObjectProxyFactory.class.getClassLoader(),
                    invoker.getInterfaces(),
                    invoker);
        
        return proxy;
    }
    
    public synchronized static <K extends Serializable, V> void upgradeCaches(
                ClusterAwareCacheFactory<?,?> cacheFactory,
                ClusterAwareLockStoreFactory lockStoreFactory,
                HazelcastMessengerFactory messengerFactory)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Upgrading caches, lock stores and messengers...");
        }
        for (ClusteredObjectProxyInvoker<?> invoker : invokers)
        {
            invoker.upgradeBackingObject(cacheFactory, lockStoreFactory, messengerFactory);
        }
    }
    
    public synchronized static void dropInvalidatingCaches()
    {
        for (ClusteredObjectProxyInvoker<?> invoker : invokers)
        {
            if (invoker instanceof InvalidateRemovalCacheProxyInvoker)
            {
                InvalidateRemovalCacheProxyInvoker<?, ?> invalidatingCacheInvoker =
                            (InvalidateRemovalCacheProxyInvoker<?, ?>) invoker;
                SimpleCache<?, ?> backingCache = invalidatingCacheInvoker.getBackingObject();
                backingCache.clear();
            }
        }
    }

    public static void clear()
    {
        invokers.clear();
    }

    protected static class MessengerProxyInvoker<T extends Serializable> extends AbstractClusteredObjectProxyInvoker<Messenger<T>>
    {
        protected String appRegion;
        protected boolean acceptLocalMessages;
        
        public MessengerProxyInvoker(Messenger<T> nonClusteredMessenger, String appRegion, boolean acceptLocalMessages)
        {
            super(nonClusteredMessenger);
            this.appRegion = appRegion;
            this.acceptLocalMessages = acceptLocalMessages;
        }
        
        @Override
        protected Messenger<T> createNewObject(CacheFactory<?, ?> cacheFactory,
                    LockStoreFactory lockStoreFactory,
                    MessengerFactory messengerFactory)
        {
            return messengerFactory.createMessenger(appRegion, acceptLocalMessages);
        }
        
        @Override
        protected void transferCollectedItems(Messenger<T> oldMessenger, Messenger<T> newMessenger)
        {
            MessageReceiver<T> receiver = oldMessenger.getReceiver();
            newMessenger.setReceiver(receiver);
        }
        
        @Override
        public Class<?>[] getInterfaces()
        {
            return new Class[] { Messenger.class };
        }
    }
    
    protected static class LockStoreProxyInvoker extends AbstractClusteredObjectProxyInvoker<LockStore>
    {   
        public LockStoreProxyInvoker(LockStore nonClusteredLockStore)
        {
            super(nonClusteredLockStore);
        }
        
        @Override
        protected LockStore createNewObject(CacheFactory<?, ?> cacheFactory,
                    LockStoreFactory lockStoreFactory, MessengerFactory messengerFactory)
        {
            return lockStoreFactory.createLockStore();
        }
        
        @Override
        protected void transferCollectedItems(LockStore oldLockStore, LockStore newLockStore)
        {
            if (log.isTraceEnabled())
            {
                log.trace("Transferring values for LockStore");
            }
            for (NodeRef node : oldLockStore.getNodes())
            {
                LockState lockState = oldLockStore.get(node);
                if (log.isTraceEnabled())
                {
                    log.trace("    key:[" + node + "] => value:[" + lockState + "]");
                }
                newLockStore.set(node, lockState);
            }
        }
        
        @Override
        public Class<?>[] getInterfaces()
        {
            return new Class[] { LockStore.class };
        }
    }
    
    public static class CacheProxyInvoker<K extends Serializable, V>
            extends AbstractClusteredObjectProxyInvoker<SimpleCache<K, V>>
    {
        public String cacheName;
        
        /**
         * @param nonClusteredCache
         * @param cacheName
         */
        public CacheProxyInvoker(SimpleCache<K, V> nonClusteredCache, String cacheName)
        {
            super(nonClusteredCache);
            this.cacheName = cacheName;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        protected SimpleCache<K, V> createNewObject(CacheFactory<?, ?> cacheFactory,
                    LockStoreFactory lockStoreFactory, MessengerFactory messengerFactory)
        {
            return (SimpleCache<K, V>) cacheFactory.createCache(cacheName);
        }
        
        @Override
        protected void transferCollectedItems(SimpleCache<K, V> oldCache, SimpleCache<K, V> newCache)
        {
            if (log.isTraceEnabled())
            {
                log.trace("Transferring values for cache: " + cacheName);
            }
            for (K key : oldCache.getKeys())
            {
                V value = oldCache.get(key);
                if (log.isTraceEnabled())
                {
                    log.trace("    key:[" + key + "] => value:[" + value + "]");
                }
                newCache.put(key, value);
            }
        }
        
        @Override
        public Class<?>[] getInterfaces()
        {
            return new Class[] { SimpleCache.class };
        }
    }
    
    public static class InvalidateRemovalCacheProxyInvoker<K extends Serializable, V>
        extends CacheProxyInvoker<K, V>
    {
        /**
         * @param nonClusteredCache
         * @param cacheName
         */
        public InvalidateRemovalCacheProxyInvoker(SimpleCache<K, V> nonClusteredCache, String cacheName)
        {
            super(nonClusteredCache, cacheName);
        }
        
        @SuppressWarnings("unchecked")
        @Override
        protected SimpleCache<K, V> createNewObject(CacheFactory<?, ?> cacheFactory,
                    LockStoreFactory lockStoreFactory, MessengerFactory messengerFactory)
        {
            return (SimpleCache<K, V>) cacheFactory.createCache(cacheName);
        }
    }
    
    protected static abstract class AbstractClusteredObjectProxyInvoker<T> implements ClusteredObjectProxyInvoker<T>
    {
        protected T backingObject;
        
        /**
         * Create a clustered object proxy invoker passing in the item to be proxied.
         * 
         * @param backingObject
         */
        public AbstractClusteredObjectProxyInvoker(T backingObject)
        {
            super();
            this.backingObject = backingObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            try
            {
                return method.invoke(backingObject, args);
            }
            catch (InvocationTargetException e)
            {
                // To provide transparent proxying, we should unwrap this exception, in particular
                // avoiding UndeclaredThrowableException when LockTryException should be propagated.
                throw e.getCause();
            }
        }
        
        protected T swapWithNew(T newBackingObject)
        {
            T oldObj = backingObject;
            backingObject = newBackingObject;
            return oldObj;
        }
        
        @Override
        public T getBackingObject()
        {
            return backingObject;
        }
        
        @Override
        public void upgradeBackingObject(
                    ClusterAwareCacheFactory<?, ?> cacheFactory,
                    LockStoreFactory lockStoreFactory,
                    MessengerFactory messengerFactory)
        {
            T newObj = createNewObject(cacheFactory, lockStoreFactory, messengerFactory);
            // Disengage the old proxied object.
            T oldObj = swapWithNew(newObj);
            // Copy any collected items into the new store
            transferCollectedItems(oldObj, newObj);
        }

        protected abstract T createNewObject(
                    CacheFactory<?, ?> cacheFactory,
                    LockStoreFactory lockStoreFactory,
                    MessengerFactory messengerFactory);
        
        protected abstract void transferCollectedItems(T oldObj, T newObj);
    }
    
    public interface ClusteredObjectProxyInvoker<T> extends InvocationHandler
    {
        /**
         * Swaps a basic non-clustered implementation of the backing object
         * for a clustered implementation.
         */
        void upgradeBackingObject(ClusterAwareCacheFactory<?, ?> cacheFactory, LockStoreFactory lockStoreFactory, MessengerFactory messengerFactory);
        
        /**
         * Return the current object being proxied.
         * 
         * @return T
         */
        T getBackingObject();
        
        /**
         * Returns the interfaces that this invoker will proxy.
         * 
         * @return Class array
         */
        Class<?>[] getInterfaces();
    }
}
