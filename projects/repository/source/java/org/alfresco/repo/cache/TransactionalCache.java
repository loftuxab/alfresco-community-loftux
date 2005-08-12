/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.util.EqualsHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * A 2-level cache that mainains a both a transaction-local cache and
 * wraps a non-transactional (global) cache.
 * <p>
 * It uses the <b>Ehcache</b> <tt>Cache</tt> for it's per-transaction
 * caches as these provide automatic size limitations, etc.
 * <p>
 * Instances of this class <b>do not require a transaction</b>.  They will work
 * directly with the global cache when no transaction is present.
 * 
 * @author Derek Hulley
 */
public class TransactionalCache implements SimpleCache, TransactionListener, InitializingBean
{
    private static final String RESOURCE_KEY_UPDATED_CACHE = "TransactionalCache.Updated";
    private static final String RESOURCE_KEY_REMOVED_CACHE = "TransactionalCache.Removed";
    
    private static Log logger = LogFactory.getLog(TransactionalCache.class);

    /** a name for convenience in logging */
    private String name = "transactionalCache";
    
    /** the global cache that will get updated after commits */
    private SimpleCache globalCache;

    /** the manager to control Ehcache caches */
    private CacheManager cacheManager;
    
    /** the maximum number of elements to be contained in the cache */
    private int maxCacheSize = 500;

    /**
     * @see #setName(String)
     */
    public String toString()
    {
        return name;
    }
    
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof TransactionalCache))
        {
            return false;
        }
        TransactionalCache that = (TransactionalCache) obj;
        return EqualsHelper.nullSafeEquals(this.name, that.name);
    }
    
    public int hashCode()
    {
        return name.hashCode();
    }
    
    /**
     * Set the global cache to use during transaction synchronization or when no transaction
     * is present.
     * 
     * @param globalCache
     */
    public void setGlobalCache(SimpleCache globalCache)
    {
        this.globalCache = globalCache;
    }

    /**
     * Set the manager to activate and control the cache instances
     * 
     * @param cacheManager
     */
    public void setCacheManager(CacheManager cacheManager)
    {
        this.cacheManager = cacheManager;
    }

    /**
     * Set the maximum number of elements to store in the update and remove caches.
     * The maximum number of elements stored in the transaction will be twice the
     * value given.
     * <p>
     * The removed list will overflow to disk in order to ensure that deletions are
     * not lost.
     * 
     * @param maxCacheSize
     */
    public void setMaxCacheSize(int maxCacheSize)
    {
        this.maxCacheSize = maxCacheSize;
    }

    /**
     * Set the name that identifies this cache from other instances.  This is optional.
     * 
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Ensures that all properties have been set
     */
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(name, "name property not set");
        Assert.notNull(cacheManager, "cacheManager property not set");
    }

    /**
     * To be used in a transaction only.
     */
    private Cache getRemovedCache(boolean create)
    {
        Cache cache = (Cache) AlfrescoTransactionSupport.getResource(RESOURCE_KEY_REMOVED_CACHE);
        if (create && cache == null)
        {
            // make a cache name
            String cacheName = name + "_" + AlfrescoTransactionSupport.getTransactionId() + "_removes";
            cache = new Cache(cacheName, maxCacheSize, false, true, 0, 0);
            try
            {
                cacheManager.addCache(cache);
            }
            catch (CacheException e)
            {
                throw new AlfrescoRuntimeException("Failed to add txn updates cache to manager", e);
            }
            AlfrescoTransactionSupport.bindResource(RESOURCE_KEY_REMOVED_CACHE, cache);
        }
        return cache;
    }
    
    /**
     * To be used in a transaction only.
     */
    private Cache getUpdatedCache(boolean create)
    {
        Cache cache = (Cache) AlfrescoTransactionSupport.getResource(RESOURCE_KEY_UPDATED_CACHE);
        if (create && cache == null)
        {
            // make a cache name
            String cacheName = name + "_" + AlfrescoTransactionSupport.getTransactionId() + "_updates";
            cache = new Cache(cacheName, maxCacheSize, true, true, 0, 0);
            try
            {
                cacheManager.addCache(cache);
            }
            catch (CacheException e)
            {
                throw new AlfrescoRuntimeException("Failed to add txn updates cache to manager", e);
            }
            AlfrescoTransactionSupport.bindResource(RESOURCE_KEY_UPDATED_CACHE, cache);
        }
        return cache;
    }

    /**
     * Checks the transactional removed and updated caches before checking the global cache.
     */
    public boolean contains(Serializable key)
    {
        Object value = get(key);
        if (value == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Checks the per-transaction caches for the object before going to the global cache.
     * If the thread is not in a transaction, then the global cache is accessed directly.
     */
    public Serializable get(Serializable key)
    {
        // are we in a transaction?
        if (AlfrescoTransactionSupport.getTransactionId() != null)
        {
            try
            {
                // check to see if the key is present in the transaction's removed items
                Cache removedCache = getRemovedCache(false);
                if (removedCache != null && removedCache.get(key) != null)
                {
                    // it has been removed in this transaction
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("get returning null - item has been removed from transactional cache: \n" +
                                "   cache: " + this + "\n" +
                                "   key: " + key);
                    }
                    return null;
                }
                // check for the item in the transaction's new/updated items
                Cache updatedCache = getUpdatedCache(false);
                if (updatedCache != null)
                {
                    Element element = updatedCache.get(key);
                    if (element != null)
                    {
                        // element was found in transaction-specific updates/additions
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Found item in transactional cache: \n" +
                                    "   cache: " + this + "\n" +
                                    "   key: " + key + "\n" +
                                    "   value: " + element.getValue());
                        }
                        return element.getValue();
                    }
                }
            }
            catch (CacheException e)
            {
                throw new AlfrescoRuntimeException("Cache failure", e);
            }
        }
        // it has not been handled in a transaction - go to the global cache
        if (logger.isDebugEnabled())
        {
            logger.debug("Fetching instance direct from global cache: \n" +
                    "   cache: " + this + "\n" +
                    "   key: " + key + "\n" +
                    "   value: " + globalCache.get(key));
        }
        return globalCache.get(key);
    }

    /**
     * Goes direct to the global cache in the absence of a transaction.
     * <p>
     * Where a transaction is present, a cache of updated items is lazily added to the
     * thread and the <tt>Object</tt> put onto that. 
     */
    public void put(Serializable key, Serializable value)
    {
        // are we in a transaction?
        if (AlfrescoTransactionSupport.getTransactionId() == null)  // not in transaction
        {
            // no transaction
            globalCache.put(key, value);
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("No transaction - adding item direct to global cache: \n" +
                        "   cache: " + this + "\n" +
                        "   key: " + key + "\n" +
                        "   value: " + value);
            }
        }
        else  // transaction present
        {
            // register for callbacks
            AlfrescoTransactionSupport.bindListener(this);
            // we have a transaction - add the item into the updated cache for this transaction
            Cache updatedCache = getUpdatedCache(true);
            Element element = new Element(key, value);
            updatedCache.put(element);
            // remove the item from the removed cache, if present
            Cache removedCache = getRemovedCache(false);
            if (removedCache != null)
            {
                removedCache.remove(key);
            }
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("In transaction - adding item direct to transactional update cache: \n" +
                        "   cache: " + this + "\n" +
                        "   key: " + key + "\n" +
                        "   value: " + value);
            }
        }
    }

    /**
     * Goes direct to the global cache in the absence of a transaction.
     * <p>
     * Where a transaction is present, a cache of removed items is lazily added to the
     * thread and the <tt>Object</tt> put onto that. 
     */
    public void remove(Serializable key)
    {
        // are we in a transaction?
        if (AlfrescoTransactionSupport.getTransactionId() == null)  // not in transaction
        {
            // no transaction
            globalCache.remove(key);
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("No transaction - removing item from global cache: \n" +
                        "   cache: " + this + "\n" +
                        "   key: " + key);
            }
        }
        else  // transaction present
        {
            // register for callbacks
            AlfrescoTransactionSupport.bindListener(this);
            // if the item is not present in the global cache, then we need only ensure that
            // we remove it from the transactional update cache
            Serializable globalValue = globalCache.get(key);
            if (globalValue != null)
            {
                // it is present in the global cache - add it from the removed cache for this txn
                Element element = new Element(key, globalValue);
                Cache removedCache = getRemovedCache(true);
                removedCache.put(element);
            }
            // remove the item from the udpated cache, if present
            Cache updatedCache = getUpdatedCache(false);
            if (updatedCache != null)
            {
                updatedCache.remove(key);
            }
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("In transaction - adding item direct to transactional removed cache: \n" +
                        "   cache: " + this + "\n" +
                        "   key: " + key);
            }
        }
    }

    /**
     * Just clears the global and transaction-local caches alike
     */
    public void clear()
    {
        // clear global cache
        globalCache.clear();
        // clear local caches
        if (AlfrescoTransactionSupport.getTransactionId() != null)
        {
            try
            {
                Cache updatedCache = getUpdatedCache(false);
                if (updatedCache != null)
                {
                    updatedCache.removeAll();
                }
                Cache removedCache = getRemovedCache(false);
                if (removedCache != null)
                {
                    removedCache.removeAll();
                }
            }
            catch (IOException e)
            {
                throw new AlfrescoRuntimeException("Failed to clear caches", e);
            }
        }
    }

    public void flush()
    {
        /*
         * Handle the removed items if they prove to be an issue
         */
    }

    public void beforeCommit(boolean readOnly)
    {
    }

    public void beforeCompletion()
    {
    }

    /**
     * Merge the transactional caches into the global cache
     */
    @SuppressWarnings("unchecked")
    public void afterCommit()
    {
        // transfer removed items
        Cache removedCache = getRemovedCache(false);
        if (removedCache != null)
        {
            // any removed items will have also been removed from the in-transaction updates
            // propogate the deletes to the global cache
            List<Serializable> keys = removedCache.getKeys();
            for (Serializable key : keys)
            {
                globalCache.remove(key);
            }
        }
        // transfer updates
        Cache updatedCache = getUpdatedCache(false);
        if (updatedCache != null)
        {
            try
            {
                List<Serializable> keys = updatedCache.getKeys();
                for (Serializable key : keys)
                {
                    Element element = updatedCache.get(key);
                    globalCache.put(key, element.getValue());
                }
            }
            catch (CacheException e)
            {
                throw new AlfrescoRuntimeException("Failed to transfer updates to global cache", e);
            }
        }
    }

    /**
     * Just allow the transactional caches to be thrown away
     */
    public void afterRollback()
    {
    }
}
