/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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

import java.util.Map;

import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.framework.cache.ModelObjectCache;
import org.alfresco.web.framework.exception.ModelObjectPersisterException;
import org.alfresco.web.scripts.RemoteStore;
import org.alfresco.web.scripts.Store;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.HttpRequestContext;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.ThreadLocalRequestContext;
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
 */
public class RemoteStoreModelObjectPersister extends StoreModelObjectPersister
{
    private static Log logger = LogFactory.getLog(RemoteStoreModelObjectPersister.class);
    
    protected final RemoteStore remoteStore;
    
    
    /**
     * Instantiates a new store model object persister.
     * 
     * @param typeId the type id
     * @param store the store
     */
    public RemoteStoreModelObjectPersister(String typeId, Store store, int delay)
    {
        super(typeId, store, delay);
        
        if (store instanceof RemoteStore == false)
        {
            throw new IllegalArgumentException("Store must be a RemoteStore instance.");
        }
        this.remoteStore = (RemoteStore)store;
        this.id = "RemoteStore_" + store.getBasePath() + "_" + this.objectTypeId; 
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.StoreModelObjectPersister#getObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    @Override
    public ModelObject getObject(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException    
    {
        ModelObject obj = null;
        try
        {
            bindConnector();
            bindRepositoryStoreId(context);
            obj = super.getObject(context, objectId);
        }
        finally
        {
            remoteStore.unbind();
        }
        
        return obj;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.StoreModelObjectPersister#saveObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
     */
    @Override
    public synchronized boolean saveObject(ModelPersistenceContext context, ModelObject modelObject)
        throws ModelObjectPersisterException    
    {
        boolean saved = false;
        try
        {
            bindConnector();
            bindRepositoryStoreId(context);
            saved = super.saveObject(context, modelObject);
        }
        finally
        {
            remoteStore.unbind();
        }
        
        return saved;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.StoreModelObjectPersister#removeObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    @Override
    public boolean removeObject(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException    
    {
        boolean removed = false;
        try
        {
            bindConnector();
            bindRepositoryStoreId(context);
            removed = super.removeObject(context, objectId);
        }
        finally
        {
            remoteStore.unbind();
        }
        
        return removed;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.StoreModelObjectPersister#newObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    @Override
    public ModelObject newObject(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException
    {
        ModelObject obj = null;
        try
        {
            bindConnector();
            bindRepositoryStoreId(context);
            obj = super.newObject(context, objectId);
        }
        finally
        {
            remoteStore.unbind();
        }
        
        return obj;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.StoreModelObjectPersister#hasObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    @Override
    public boolean hasObject(ModelPersistenceContext context, String objectId)
    {
        boolean hasObject = false;
        try
        {
            bindConnector();
            bindRepositoryStoreId(context);
            hasObject = super.hasObject(context, objectId);
        }
        finally
        {
            remoteStore.unbind();
        }
        
        return hasObject;
    }  
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.StoreModelObjectPersister#getAllObjects(org.alfresco.web.framework.ModelPersistenceContext)
     */
    @Override
    public Map<String, ModelObject> getAllObjects(ModelPersistenceContext context)
        throws ModelObjectPersisterException
    {
        Map<String, ModelObject> objects;
        try
        {
            bindConnector();
            bindRepositoryStoreId(context);
            objects = super.getAllObjects(context);
        }
        finally
        {
            remoteStore.unbind();
        }
        
        return objects;     
    }

    /**
     * Bind Repository Store ID override to the current thread
     */
    private void bindRepositoryStoreId(ModelPersistenceContext context)
    {
        remoteStore.bindRepositoryStoreId((String)context.getValue(ModelPersistenceContext.REPO_STOREID));
    }

    /**
     * Bind Connector to the remote store for the current thread
     */
    private void bindConnector()
    {
        RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        if (rc instanceof HttpRequestContext)
        {
            try
            {
                remoteStore.bindConnector(FrameworkHelper.getConnector(rc, remoteStore.getEndpoint()));
            }
            catch (RemoteConfigException rce)
            {
                throw new AlfrescoRuntimeException("Failed to bind connector to remote store.", rce);
            }
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
        String storeId = (String)context.getValue(ModelPersistenceContext.REPO_STOREID);
        String key = new StringBuilder(100).append(storeId).append(':').append(getId()).toString();
        
        ModelObjectCache cache = objectCaches.get(key);
        if (cache == null)
        {
            cache = new ModelObjectCache(this.store, this.delay);
            objectCaches.put(key, cache);
        }
        
        return cache;
    }
}
