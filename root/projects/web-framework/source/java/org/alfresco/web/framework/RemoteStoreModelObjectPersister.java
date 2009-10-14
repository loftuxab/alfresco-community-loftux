/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.web.framework.cache.ModelObjectCache;
import org.alfresco.web.scripts.RemoteStore;
import org.alfresco.web.scripts.Store;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This persister works for a RemoteStore implementation of a Store.
 * 
 * A RemoteStore is a store whose storage is located somewhere over
 * an HTTP connection.  The REST service allows for a Repository store
 * to function as the means of storage.
 * 
 * A Repository store identifier is therefore required.  When using this
 * persister implementation, one should first bind a repository store id
 * as a thread local variable (methods are available on the RemoteStore).
 * 
 * @author muzquiano
 * @author kevinr
 */
public class RemoteStoreModelObjectPersister extends StoreModelObjectPersister
{
    private static Log logger = LogFactory.getLog(RemoteStoreModelObjectPersister.class);
    
    protected final Map<String, ModelObjectCache> objectCaches = new HashMap<String, ModelObjectCache>();
    
    
    /**
     * Instantiates a new store model object persister.
     * 
     * @param typeId the type id
     * @param store the store
     * @param cache             true to cache model objects, false to look them up fresh every time
     * @param cacheCheckDelay   delay in seconds between checking last modified date of cached items 
     */
    public RemoteStoreModelObjectPersister(String typeId, Store store, boolean cache, int delay)
    {
        super(typeId, store, cache, delay);
        
        if (store instanceof RemoteStore == false)
        {
            throw new IllegalArgumentException("Store must be a RemoteStore instance.");
        }
        this.id = "RemoteStore_" + store.getBasePath() + "_" + this.objectTypeId; 
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#invalidateCache(org.alfresco.web.framework.ModelPersistenceContext)
     */
    @Override
    public void invalidateCache()
    {
        super.invalidateCache();
        synchronized (this.objectCaches)
        {
            this.objectCaches.clear();
        }
    }


    /**
     * Gets the cache for a particular model persistence context
     * 
     * @param context the context
     * 
     * @return the cache
     */
    @Override
    protected ModelObjectCache getCache(ModelPersistenceContext context)
    {
        ModelObjectCache cache = this.objectCache;
        
        String storeId = (String)context.getValue(ModelPersistenceContext.REPO_STOREID);

        if (storeId == null)
        {
            String userId = context.getUserId();
            int idx = userId.indexOf('@');
            if (idx != -1)
            {
                // assume MT Share so partition by user domain
                // TODO alternatively Share could pass "alfStoreId" in each request
                storeId = userId.substring(idx);
            }
        }
        
        if (storeId != null)
        {
            // need to lookup a store specific object cache
            String key = new StringBuilder(100).append(storeId).append(':').append(getId()).toString();
            synchronized (this.objectCaches)
            {
                cache = this.objectCaches.get(key);
                if (cache == null)
                {
                    cache = new ModelObjectCache(this.store, this.delay);
                    this.objectCaches.put(key, cache);
                }
            }
        }
        
        return cache;
    }
}
