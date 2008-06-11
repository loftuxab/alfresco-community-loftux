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

import java.util.HashMap;
import java.util.Map;

import org.alfresco.web.framework.exception.ModelObjectPersisterException;
import org.alfresco.web.framework.exception.ModelObjectManagerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides core services for loading, retrieving and persisting
 * model objects to one or more back-end Stores.
 * <p>
 * A model object type has an associated search path id.  The search
 * path defines the set of stores from which objects of a given model
 * type can be loaded.
 * <p>
 * Loaded model objects are bound to a particular store and will be
 * persisted back to that store unless copied or moved.
 * <p>
 * New model objects are created within a given store or they are
 * created within the default store for the particular model object
 * type (this is defined in configuration).
 * 
 * @author muzquiano
 */
public final class ModelObjectManager
{
    private static final Log logger = LogFactory.getLog(ModelObjectManager.class);

    private WebFrameworkService service;
    private ModelPersistenceContext context;
    
    
    /**
     * Constrcuts a new ModelObjectService which is scoped to the given
     * persister context
     * 
     * This constructor is private as all construction should be performed
     * through static method
     * 
     * @param service
     * @param context
     */
    private ModelObjectManager(WebFrameworkService service, ModelPersistenceContext context)
    {
        this.service = service;
        this.context = context;
    }
    
    /**
     * Returns the model persistence context
     * 
     * @return context
     */
    public ModelPersistenceContext getContext()
    {
        return this.context;
    }
    
    /**
     * Static instantiator of ModelObjectServices
     * 
     * @param service a valid WebFrameworkService instance
     * @param context a valid PersisterContext instance
     * @return
     * @throws ModelObjectManagerException
     */
    static ModelObjectManager newInstance(WebFrameworkService service, ModelPersistenceContext context)
        throws ModelObjectManagerException, IllegalArgumentException
    {
        if(service == null)
        {
            throw new IllegalArgumentException("WebFrameworkService is null");
        }
        if(context == null)
        {
            throw new IllegalArgumentException("PersisterContext is null");
        }
        
        return new ModelObjectManager(service, context);
    }
        
    /**
     * Retrieves an object from the framework.
     * 
     * If the object is not in cache, it is loaded from storage.
     * 
     * @param objectTypeId the object type id
     * @param objectId the object id
     * 
     * @return the object
     */
    public ModelObject getObject(String objectTypeId, String objectId)
    {
        ModelObject obj = null;
        
        ModelObjectPersister persister = this.service.getPersister(objectTypeId);
        if(persister != null)
        {
            if(logger.isDebugEnabled())
                logger.debug("getObject loading: " + objectId);
            
            try
            {
                obj = persister.getObject(this.context, objectId);
            }
            catch(ModelObjectPersisterException mope)
            {
                if(logger.isDebugEnabled())
                    logger.debug("Unable to retrieve object: " + objectId + " of type " + objectTypeId, mope);
            }
        }
       
        return obj;
    }
    
    /**
     * New object.
     * 
     * @param objectTypeId the object type id
     * @param objectId the object id
     * 
     * @return the model object
     */
    public ModelObject newObject(String objectTypeId, String objectId)
    {
        ModelObject obj = null;
        
        // get the default persister for this object type
        ModelObjectPersister persister = this.service.getDefaultPersister(objectTypeId);
        if(persister != null)
        {
            try
            {
                obj = persister.newObject(this.context, objectId);
            }
            catch(ModelObjectPersisterException mope)
            {
                if(logger.isDebugEnabled())
                    logger.debug("Unable to create object: " + objectId + " of type " + objectTypeId, mope);
                
                // allow null to be returned
            }                
        }
       
        return obj;
    }

    /**
     * New object.
     * 
     * @param objectTypeId the object type id
     * 
     * @return the model object
     */
    public ModelObject newObject(String objectTypeId)
    {
        ModelObject obj = null;
        
        // get the default persister for this object type
        ModelObjectPersister persister = this.service.getDefaultPersister(objectTypeId);        
        if(persister != null)
        {
            String objectId = newGUID();
            try
            {
                obj = persister.newObject(this.context, objectId);
            }
            catch(ModelObjectPersisterException mope)
            {
                if(logger.isDebugEnabled())
                    logger.debug("Unable to create object: " + objectId + " of type " + objectTypeId, mope);
            }                
        }
       
        return obj;
    }
    
    /**
     * Saves the object to its persister
     * 
     * @param object the object
     */
    public boolean saveObject(ModelObject object)
    {
        boolean saved = false;
        
        ModelObjectPersister persister = this.service.getPersisterById(object.getPersisterId());
        if(persister != null)
        {
            try
            {
                saved = persister.saveObject(this.context, object);
            }
            catch(ModelObjectPersisterException mope)
            {
                if(logger.isDebugEnabled())
                    logger.debug("Unable to save object: " + object.getId() + " of type " + object.getTypeId() + " to storage: " + persister.getId()); 
            }                                
        }
        
        return saved;
    }
    
    /**
     * Removes the object.
     * 
     * @param object the object
     * 
     * @return true, if successful
     */
    public boolean removeObject(ModelObject object)
    {
        return removeObject(object.getTypeId(), object.getPersisterId());
    }
    
    /**
     * Removes the object.
     * 
     * @param objectTypeId the object type id
     * @param objectId the object id
     * 
     * @return true, if successful
     */
    public boolean removeObject(String objectTypeId, String objectId)
    {
        boolean removed = false;
        
        ModelObjectPersister persister = this.service.getPersister(objectTypeId);
        if(persister != null)
        {
            boolean x = false;
            try
            {
                x = persister.removeObject(this.context, objectId);
            }
            catch(ModelObjectPersisterException mope)
            {
                if(logger.isDebugEnabled())
                    logger.debug("Unable to remove object: " + objectId + " of type " + objectTypeId, mope);
            }                

            if(x)
            {
                removed = true;
            }
        }
        
        return removed;
    }
    
    /**
     * Retrieves all objects of a given type id
     * 
     * @param objectTypeId
     * 
     * @return a map of model objects (keyed by object id)
     */
    public Map<String, ModelObject> getAllObjects(String objectTypeId)
    {
        Map<String, ModelObject> objects = new HashMap<String, ModelObject>(256, 1.0f);

        ModelObjectPersister persister = this.service.getPersister(objectTypeId);
        if(persister != null)
        {
            try
            {
                Map<String, ModelObject> persisterObjects = persister.getAllObjects(this.context);
                objects.putAll(persisterObjects);
            }
            catch(ModelObjectPersisterException mope)
            {
                if(logger.isDebugEnabled())
                    logger.debug("ModelObjectService unable to retrieve all objects", mope);                
            }
        }
        
        return objects;
    }

    /**
     * New guid.
     * 
     * @return the string
     */
    protected String newGUID()
    {
        return ModelHelper.newGUID();
    }
    
    /**
     * Invalidates the cache for all persisters in this persistence context 
     */
    public void invalidateCache()
    {
        ModelObjectPersister[] persisters = this.service.getPersisters();
        for(int i = 0; i < persisters.length; i++)
        {
            persisters[i].invalidateCache();
        }       
    }
}
