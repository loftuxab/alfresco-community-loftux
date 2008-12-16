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

import java.util.Collections;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.framework.exception.ModelObjectManagerException;
import org.alfresco.web.framework.exception.ModelObjectPersisterException;
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
 * @author kevinr
 */
public final class ModelObjectManager
{
    private static final Log logger = LogFactory.getLog(ModelObjectManager.class);

    private final WebFrameworkManager service;
    private final ModelPersistenceContext context;
    
    
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
    private ModelObjectManager(WebFrameworkManager service, ModelPersistenceContext context)
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
     * Static factory method for ModelObjectManager instances. Each instance is tied to
     * a particular ModelPersistenceContext object.
     * 
     * @param service a valid WebFrameworkManager instance
     * @param context a valid PersisterContext instance
     * 
     * @return ModelObjectManager for the given context
     * 
     * @throws ModelObjectManagerException
     */
    static ModelObjectManager newInstance(WebFrameworkManager service, ModelPersistenceContext context)
        throws ModelObjectManagerException, IllegalArgumentException
    {
        if (service == null)
        {
            throw new IllegalArgumentException("WebFrameworkManager is null");
        }
        if (context == null)
        {
            throw new IllegalArgumentException("ModelPersistenceContext is null");
        }
        
        return new ModelObjectManager(service, context);
    }
        
    /**
     * Retrieves an object from the persister that manages the given object type.
     * <p>
     * If the object is not available in cache, it is loaded from storage.
     * 
     * @param objectTypeId  the object type id
     * @param objectId      the object id
     * 
     * @return the ModelObject or null if not found
     */
    public ModelObject getObject(String objectTypeId, String objectId)
    {
        ModelObject obj = null;
        
        ModelObjectPersister persister = this.service.getPersister(objectTypeId);
        if (persister != null)
        {
            if (logger.isDebugEnabled())
                logger.debug("getObject loading: " + objectId);
            
            try
            {
                obj = persister.getObject(this.context, objectId);
            }
            catch (ModelObjectPersisterException mope)
            {
                throw new AlfrescoRuntimeException("Unable to retrieve object: " + objectId + " of type: " +
                        objectTypeId, mope);
            }
        }
        
        return obj;
    }
    
    /**
     * Create a new object.
     * 
     * @param objectTypeId  the object type id
     * @param objectId      the object id
     * 
     * @return the ModelObject or null if no persister for the given type can be found.
     */
    public ModelObject newObject(String objectTypeId, String objectId)
    {
        ModelObject obj = null;
        
        // get the default persister for this object type
        ModelObjectPersister persister = this.service.getDefaultPersister(objectTypeId);
        if (persister != null)
        {
            try
            {
                obj = persister.newObject(this.context, objectId);
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isInfoEnabled())
                    logger.info("Unable to create object: " + objectId + " of type: " + objectTypeId, mope);
                
                // allow null to be returned
            }                
        }
       
        return obj;
    }

    /**
     * Create a new object.
     * 
     * @param objectTypeId  the object type id
     * 
     * @return the ModelObject or null if not found
     * 
     * @return the model object
     */
    public ModelObject newObject(String objectTypeId)
    {
        ModelObject obj = null;
        
        // get the default persister for this object type
        ModelObjectPersister persister = this.service.getDefaultPersister(objectTypeId);        
        if (persister != null)
        {
            String objectId = newGUID();
            try
            {
                obj = persister.newObject(this.context, objectId);
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isInfoEnabled())
                    logger.info("Unable to create object: " + objectId + " of type: " + objectTypeId, mope);
            }                
        }
       
        return obj;
    }
    
    /**
     * Saves the object to its persister.
     * 
     * @param object    the ModelObject to save
     */
    public boolean saveObject(ModelObject object)
    {
        boolean saved = false;
        
        ModelObjectPersister persister = this.service.getPersisterById(object.getPersisterId());
        if (persister != null)
        {
            try
            {
                if (logger.isDebugEnabled())
                    logger.debug("Attempting to save object '" + object.getId() + "' to persister: " + persister.getId()); 
                
                saved = persister.saveObject(this.context, object);
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isInfoEnabled())
                    logger.info("Unable to save object: " + object.getId() + " of type: " + object.getTypeId() +
                                " to persister: " + persister.getId() + " due to error: " + mope.getMessage()); 
            }                                
        }
        
        return saved;
    }
    
    /**
     * Removes the object.
     * 
     * @param object    the ModelObject to remove
     * 
     * @return true if successful, false otherwise
     */
    public boolean removeObject(ModelObject object)
    {
        return removeObject(object.getTypeId(), object.getId());
    }
    
    /**
     * Removes the object.
     * 
     * @param objectTypeId  the object type id
     * @param objectId      the object id
     * 
     * @return true if successful, false otherwise
     */
    public boolean removeObject(String objectTypeId, String objectId)
    {
        boolean removed = false;
        
        ModelObjectPersister persister = this.service.getPersister(objectTypeId);
        if (persister != null)
        {
            try
            {
                if (logger.isDebugEnabled())
                    logger.debug("Attempting to remove object '" + objectId + "' from persister: " + persister.getId()); 
                
                removed = persister.removeObject(this.context, objectId);
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isInfoEnabled())
                    logger.info("Unable to remove object: " + objectId + " of type: " + objectTypeId, mope);
            }                
        }
        
        return removed;
    }
    
    /**
     * Retrieves all objects of a given type id.
     * 
     * @param objectTypeId      Type ID
     * 
     * @return a map of model objects (keyed by object id)
     */
    public Map<String, ModelObject> getAllObjects(String objectTypeId)
    {
        Map<String, ModelObject> objects = Collections.<String, ModelObject>emptyMap();
        
        ModelObjectPersister persister = this.service.getPersister(objectTypeId);
        if (persister != null)
        {
            try
            {
                objects = persister.getAllObjects(this.context);
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isInfoEnabled())
                    logger.info("ModelObjectManager unable to retrieve all objects", mope);                
            }
        }
        
        return objects;
    }
    
    /**
     * Retrieves all objects of a given type id with the given object ID filter
     * 
     * @param objectTypeId  the object type id
     * 
     * @return a map of model objects (keyed by object id)
     */
    public Map<String, ModelObject> getAllObjects(String objectTypeId, String filter)
    {
        Map<String, ModelObject> objects = Collections.<String, ModelObject>emptyMap();
        
        ModelObjectPersister persister = this.service.getPersister(objectTypeId);
        if (persister != null)
        {
            try
            {
                objects = persister.getAllObjectsByFilter(this.context, filter);
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isInfoEnabled())
                    logger.info("ModelObjectManager unable to retrieve all objects by filter: " + filter, mope);                
            }
        }
        
        return objects;
    }

    /**
     * New guid.
     * 
     * @return the string
     */
    private static String newGUID()
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
