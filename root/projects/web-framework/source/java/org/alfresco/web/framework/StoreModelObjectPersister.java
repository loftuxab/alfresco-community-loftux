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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.tools.XMLUtil;
import org.alfresco.util.ReflectionHelper;
import org.alfresco.web.framework.cache.ModelObjectCache;
import org.alfresco.web.framework.exception.ModelObjectPersisterException;
import org.alfresco.web.scripts.Store;
import org.alfresco.web.site.FrameworkHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;

/**
 * This persister is used for stores which are mounted against a fixed
 * or static location.  Examples are the Classpath Store and the Repository
 * store, both of which have fixed root paths or mounts.
 * 
 * Caching is implemented using a simple object key cache and timestamps
 * are checked by querying the underlying store implementation.
 * 
 * The cache key generation is intentionally split out so that it may
 * be overridden by subobjects that wish to cache on alternative
 * context state.
 * 
 * @author muzquiano
 */
public class StoreModelObjectPersister extends AbstractModelObjectPersister
{
    private static Log logger = LogFactory.getLog(StoreModelObjectPersister.class);
    protected static long DEFAULT_CACHE_TIMEOUT = 30*60*1000; // 30 minutes

    protected String id;
    protected Store store;
    protected Map<String, ModelObjectCache> objectCaches;


    /**
     * Instantiates a new store model object persister.
     * 
     * @param store the store
     * @param objectTypeId the object type id
     */
    public StoreModelObjectPersister(String objectTypeId, Store store)
    {
        super(objectTypeId);
        this.store = store;
        this.id = "Store_" + this.store.getBasePath() + "_" + this.objectTypeId;
        this.objectCaches = new HashMap<String, ModelObjectCache>(16, 1.0f);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getId()
     */
    public String getId()
    {
        return this.id;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public ModelObject getObject(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException    
    {
        String path = idToPath(objectId);
        return getObjectByPath(context, path);
    }
    
    /**
     * Gets the object by path.
     * 
     * @param path the path
     * @param context the context
     * 
     * @return the object by path
     * 
     * @throws ModelObjectPersisterException the model object persister exception
     */
    protected synchronized ModelObject getObjectByPath(ModelPersistenceContext context, String path)
        throws ModelObjectPersisterException
    {
        // get the object from the cache if possible
        ModelObject obj = cacheGet(context, path);
        if (obj == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Loading object for path: " + path);
            
            try
            {
                // parse to Document
                Document document = XMLUtil.parse(store.getDocument(path));

                if (logger.isDebugEnabled())
                    logger.debug("Parsed document: " + document);
                
                String persisterId = this.getId();
                String storagePath = path;
                String id = pathToId(storagePath);
                if (id != null)
                {                
                    ModelObjectKey key = new ModelObjectKey(persisterId, storagePath, id);                
                    String implClassName = FrameworkHelper.getConfig().getTypeDescriptor(objectTypeId).getImplementationClass();
                    obj = (ModelObject) ReflectionHelper.newObject(
                            implClassName, new Class[] { ModelObjectKey.class, Document.class },
                            new Object[] { key, document });
                    
                    if (obj != null)
                    {
                        obj.touch();
                        
                        // CACHE: put the object into the cache
                        cachePut(context, path, obj);
                    }
                    else
                    {
                        throw new ModelObjectPersisterException("Unable to create object of type '" + implClassName + "' via reflection");
                    }
                }
            }
            catch (Exception ex)
            {
                throw new ModelObjectPersisterException("Unable to load model object for path: " + path, ex);
            }
        }
        
        return obj;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#saveObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
     */
    public synchronized boolean saveObject(ModelPersistenceContext context, ModelObject modelObject)
        throws ModelObjectPersisterException    
    {
        boolean saved = false;
        
        String content = modelObject.toXML();
        
        // this is the path that we expect to save the object
        // either the object was originally stored at this location
        // or it is the path that was initialized when the object was created
        String storagePath = modelObject.getStoragePath();
        
        // now get the storage id and the theoretical path where it would store
        // the end user might have changed the id
        String storageId = modelObject.getPersisterId();
        String _storagePath = storageId + ".xml";
        
        boolean fileMoved = !(storagePath.equals(_storagePath));
        
        // if the storage path and id are the same, then the file hasn't been
        // renamed, so we can just save it to the same place
        if (this.store.hasDocument(storagePath))
        {
            try
            {
                if (!fileMoved)
                {
                    // the file wasn't moved, so just update
                    this.store.updateDocument(storagePath, content);
                    
                    // CACHE: remove object from cache
                    // this will force it to reload the next time around
                    cacheRemove(context, storagePath);
                    
                    saved = true;
                }
                else
                {
                    // the file was moved and it already exists in the store
                    
                    // TODO: move this down into the store so that we can
                    // provide some transactional support
                    
                    // create the new object
                    this.store.createDocument(_storagePath, content);
                    
                    // adjust the key to reflect new storage state
                    String _id = pathToId(_storagePath);                        
                    modelObject.getKey().setId(_id);
                    modelObject.getKey().setStoragePath(_storagePath);
                    
                    // remove the old object
                    this.store.removeDocument(storagePath);
                    
                    // CACHE: remove object from cache
                    // this will force it to reload the next time around
                    cacheRemove(context, storagePath);
                    
                    saved = true;
                }
            }
            catch (IOException ex)
            {
                throw new ModelObjectPersisterException("Unable to update model object: " + storagePath, ex);
            }
        }
        else
        {
            try
            {
                if (fileMoved)
                {
                    // check whether a file already exists at the intended path
                    if (!this.store.hasDocument(_storagePath))
                    {
                        // create the document
                        this.store.createDocument(_storagePath, content);
                        
                        // adjust the key to reflect new storage state
                        String _id = pathToId(_storagePath);                        
                        modelObject.getKey().setId(_id);
                        modelObject.getKey().setStoragePath(_storagePath);
                        
                        // CACHE: remove object from cache
                        // this will force it to reload the next time around
                        cacheRemove(context, storagePath);
                        
                        saved = true;
                    }
                    else
                    {
                        throw new ModelObjectPersisterException("A file already exists at the path: " + _storagePath);
                    }
                }
            }
            catch(IOException ex)
            {
                throw new ModelObjectPersisterException("Unable to create new model object: " + storagePath, ex);
            }                
        }
        
        return saved;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#removeObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public boolean removeObject(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException    
    {
        String path = this.idToPath(objectId);
        return removeObjectByPath(context, path);
    }
    
    /**
     * Removes the object by path.
     * 
     * @param context the context
     * @param path the path
     * 
     * @return true, if successful
     * 
     * @throws ModelObjectPersisterException the model object persister exception
     */
    protected synchronized boolean removeObjectByPath(ModelPersistenceContext context, String path)
        throws ModelObjectPersisterException
    {    
        boolean removed = false;
        
        if (this.store.hasDocument(path))
        {
            try
            {
                removed = this.store.removeDocument(path);
                
                // CACHE: remove from cache
                cacheRemove(context, path);
            }
            catch(IOException ex)
            {
                throw new ModelObjectPersisterException("Unable to remove object for path: " + path);
            }
        }
        
        return removed;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#newObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public ModelObject newObject(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException
    {
        // create the minimum XML - nodes will be added using DOM methods to it
        String xml = "<" + this.objectTypeId + "></" + this.objectTypeId + ">";
        
        // build the object
        ModelObject obj = null;
        try
        {
            Document document = XMLUtil.parse(xml);
            XMLUtil.addChildValue(document.getRootElement(), "version", FrameworkHelper.getConfig().getTypeDescriptor(this.objectTypeId).getVersion());
            
            String persisterId = this.getId();
            String storagePath = this.idToPath(objectId);
            
            if (objectId != null)
            {            
                ModelObjectKey key = new ModelObjectKey(persisterId, storagePath, objectId);                
                String implClassName = FrameworkHelper.getConfig().getTypeDescriptor(objectTypeId).getImplementationClass();
                obj = (ModelObject) ReflectionHelper.newObject(
                        implClassName, new Class[] { ModelObjectKey.class, Document.class },
                        new Object[] { key, document });
                
                if (obj != null)
                {
                    obj.touch();

                    // NOTE: do not add to cache at this point
                    // We want the cache to only represent persisted objects
                    // Objects are not persisted until they are saved
                }
                else
                {
                    throw new ModelObjectPersisterException("Unable to create new object for path: " + storagePath);
                }
            }
        }
        catch (DocumentException de)
        {
            // something failed while trying to load the xml object
            if (logger.isWarnEnabled())
                logger.warn(de);
        }
        
        return obj;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#hasObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public boolean hasObject(ModelPersistenceContext context, String objectId)
    {
        String path = this.idToPath(objectId);
        return hasObjectByPath(context, path);
    }  

    /**
     * Checks for object by path.
     * 
     * @param context the context
     * @param path the path
     * 
     * @return true, if successful
     */
    protected boolean hasObjectByPath(ModelPersistenceContext context, String path)
    {
        return this.store.hasDocument(path);
    }  
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getAllObjects(org.alfresco.web.framework.ModelPersistenceContext)
     */
    public Map<String, ModelObject> getAllObjects(ModelPersistenceContext context)
        throws ModelObjectPersisterException
    {
        String[] docPaths = this.store.getAllDocumentPaths();
        Map<String, ModelObject> objects = new HashMap<String, ModelObject>(docPaths.length, 1.0f);
        for (int i = 0; i < docPaths.length; i++)
        {
            // load object from path
            // this will retrieve from cache, if possible
            ModelObject object = getObjectByPath(context, docPaths[i]);
            
            // place into collected map
            objects.put(object.getId(), object);
        }
        
        return objects;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getTimestamp(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public long getTimestamp(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException
    {
        String path = this.idToPath(objectId);
        return getTimestampByPath(context, path);
    }

    /**
     * Gets the timestamp by path.
     * 
     * @param context the context
     * @param path the path
     * 
     * @return the timestamp by path
     * 
     * @throws ModelObjectPersisterException the model object persister exception
     */
    public long getTimestampByPath(ModelPersistenceContext context, String path)
        throws ModelObjectPersisterException
    {
        try
        {
            return this.store.lastModified(path);
        }
        catch(IOException ioe)
        {
            throw new ModelObjectPersisterException("Unable to check timestamp for object path: " + path, ioe);
        }    
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#invalidateCache(org.alfresco.web.framework.ModelPersistenceContext)
     */
    public void invalidateCache()
    {
        this.objectCaches.clear();
    }
    
    
    /**
     * Gets the cache for a particular model persistence context
     * 
     * @return the cache
     */
    protected ModelObjectCache getCache(ModelPersistenceContext context)
    {
        String key = getId();
        
        ModelObjectCache cache = objectCaches.get(key);
        if (cache == null)
        {
            cache = new ModelObjectCache(this.store, DEFAULT_CACHE_TIMEOUT);
            objectCaches.put(key, cache);
        }
        
        return cache;
    }

    /**
     * Returns an object from the cache
     * 
     * @param context the context
     * @param path the path
     * 
     * @return the model object
     */
    protected ModelObject cacheGet(ModelPersistenceContext context, String path)
    {
        ModelObject obj = getCache(context).get(path);
        if(logger.isDebugEnabled())
        {
            if(obj != null)
            {
                logger.debug("Cache hit: " + path);
            }
            else
            {
                logger.debug("Cache miss: " + path);
            }
        }
        return obj;
    }   
    
    /**
     * Places an object into this persister's cache.
     * 
     * @param context the context
     * @param obj the obj
     */
    protected void cachePut(ModelPersistenceContext context, ModelObject obj)
    {
        String path = idToPath(obj.getId());
        cachePut(context, path, obj);
    }
    
    /**
     * Places an object into this persister's cache.
     * 
     * @param context the context
     * @param path the path
     * @param obj the obj
     */
    protected void cachePut(ModelPersistenceContext context, String path, ModelObject obj)
    {
        if(logger.isDebugEnabled())
            logger.debug("Put into cache: " + path);
        
        getCache(context).put(path, obj);
    }

    /**
     * Removes an object from the cache
     * 
     * @param context the context
     * @param path the path
     */
    protected void cacheRemove(ModelPersistenceContext context, String path)
    {
        if(logger.isDebugEnabled())
            logger.debug("Remove from cache: " + path);
        
        getCache(context).remove(path);
    }
}
