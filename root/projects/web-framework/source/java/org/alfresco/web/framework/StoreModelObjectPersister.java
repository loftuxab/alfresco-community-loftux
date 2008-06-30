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
import org.alfresco.web.framework.cache.ModelObjectCache.ModelObjectSentinel;
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
    private static final Class[] MODELOBJECT_CLASSES = new Class[] {
        String.class, ModelPersisterInfo.class, Document.class };

    private static Log logger = LogFactory.getLog(StoreModelObjectPersister.class);
    
    protected String id;
    protected final long delay;
    protected final Store store;
    protected final Map<String, ModelObjectCache> objectCaches;


    /**
     * Instantiates a new store model object persister.
     * 
     * @param store             the store
     * @param objectTypeId      the object type id
     * @param cacheCheckDelay   delay in seconds between checking last modified date of cached items 
     */
    public StoreModelObjectPersister(String objectTypeId, Store store, int cacheCheckDelay)
    {
        super(objectTypeId);
        this.delay = (cacheCheckDelay * 1000L);
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
                // check to see if the requested object is present in the store
                if (store.hasDocument(path))
                {
                    // parse XML to a Document DOM
                    Document document = XMLUtil.parse(store.getDocument(path));
                    
                    if (logger.isDebugEnabled())
                        logger.debug("Parsed document: " + document);
                    
                    String persisterId = this.getId();
                    String storagePath = path;
                    String id = pathToId(storagePath);
                    if (id != null)
                    {                
                        ModelPersisterInfo info = new ModelPersisterInfo(persisterId, storagePath, true);                
                        String implClassName = FrameworkHelper.getConfig().getTypeDescriptor(objectTypeId).getImplementationClass();
                        obj = (ModelObject)ReflectionHelper.newObject(
                                implClassName, MODELOBJECT_CLASSES,
                                new Object[] { id, info, document });
                        
                        // if found, place the object into the cache
                        if (obj != null)
                        {
                            obj.touch();
                            
                            cachePut(context, path, obj);
                        }
                        else
                        {
                            throw new ModelObjectPersisterException("Unable to create object of type '" + implClassName + "' via reflection");
                        }
                    }
                }
                else    
                {
                    // document does not exist - add sentinel object, this will timeout like other cached values
                    cachePut(context, path, ModelObjectSentinel.getInstance());
                }
            }
            catch (Exception ex)
            {
                throw new ModelObjectPersisterException("Failure to load model object for path: " + path, ex);
            }
        }
        
        // handle cached sentinel case - we return null but the cache keeps the sentinel object reference
        if (obj == ModelObjectSentinel.getInstance())
        {
            obj = null;
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
        
        // the path to which we expect to save, this is essentially the path against
        // which we were instantiated or from which we were loaded - this may change
        // if the ID of the object has changed since creation - as objects are named
        // by convention based on the ID
        String path = modelObject.getStoragePath();
        
        // now figure out what path we want to save to
        String _path = idToPath(modelObject.getId());
        try
        {
            // if the object hasn't been saved yet
            if (!modelObject.isSaved())
            {
                // create the document
                this.store.createDocument(_path, content);
                
                // adjust the persister information to reflect new storage state
                ModelPersisterInfo info = modelObject.getKey();
                info.setStoragePath(_path);
                info.setSaved(true);
                
                // put object into cache
                cachePut(context, _path, modelObject);
                
                // flag that the save was successful
                saved = true;
            }
            else
            {
                // object was already saved
                // what we do in this case depends on whether the path changed
                if (!path.equals(_path))
                {
                    // path has changed, so first create the new object
                    this.store.createDocument(_path, content);
                    
                    // adjust the persister information to reflect new storage state
                    ModelPersisterInfo info = modelObject.getKey();
                    info.setStoragePath(_path);
                    info.setSaved(true);
                    
                    // lock against the cache so we can atomically perform put+remove
                    synchronized (getCache(context))
                    {
                        // put object into new cache location
                        cachePut(context, _path, modelObject);
                        
                        // remove old object from old cache location
                        cacheRemove(context, path);
                    }
                    
                    // remove the old object from the store
                    this.store.removeDocument(path);
                    
                    // flag that the save was successful
                    saved = true;
                }
                else
                {
                    // file not moved, so just do an update
                    this.store.updateDocument(path, content);
                    
                    // make sure it is marked as saved
                    modelObject.getKey().setSaved(true);
                    
                    // put object into cache
                    cachePut(context, path, modelObject);
                    
                    // flag that the save was succesful
                    saved = true;
                }
            }
        }
        catch (IOException ex)
        {
            throw new ModelObjectPersisterException("Unable to save object: " + path + " (" + _path + ")", ex);
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
                
                // remove from cache
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
            
            String persisterId = getId();
            String path = idToPath(objectId);
            
            if (objectId != null)
            {            
                ModelPersisterInfo info = new ModelPersisterInfo(persisterId, path, false);                
                String implClassName = FrameworkHelper.getConfig().getTypeDescriptor(objectTypeId).getImplementationClass();
                obj = (ModelObject)ReflectionHelper.newObject(
                        implClassName, MODELOBJECT_CLASSES,
                        new Object[] { objectId, info, document });
                
                // if constructed ok, place the object into the cache
                if (obj != null)
                {
                    obj.touch();
                    
                    cachePut(context, path, obj);
                }
                else
                {
                    throw new ModelObjectPersisterException("Unable to create new object for path: " + path);
                }
            }
        }
        catch (DocumentException de)
        {
            // something failed while trying to load the xml object
            throw new ModelObjectPersisterException("Failed to load objectId: " + objectId, de);
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
        synchronized (objectCaches)
        {
            this.objectCaches.clear();
        }
    }
    
    
    /**
     * Gets the cache for a particular model persistence context
     * 
     * @return the cache
     */
    protected ModelObjectCache getCache(ModelPersistenceContext context)
    {
        String key = getId();
        
        ModelObjectCache cache;
        synchronized (objectCaches)
        {
            cache = objectCaches.get(key);
            if (cache == null)
            {
                cache = new ModelObjectCache(this.store, this.delay);
                objectCaches.put(key, cache);
            }
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
        return getCache(context).get(path);
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

    @Override
    public String toString()
    {
        return this.id;
    }
}
