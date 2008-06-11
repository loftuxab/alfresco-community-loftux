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
import java.util.Iterator;
import java.util.Map;

import org.alfresco.web.framework.exception.ModelObjectPersisterException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Class MultiModelObjectPersister.
 * 
 * @author muzquiano
 */
public class MultiModelObjectPersister implements ModelObjectPersister
{
    private static Log logger = LogFactory.getLog(MultiModelObjectPersister.class);
    private String persisterId;
    private String objectTypeId;
    private WebFrameworkService service;    
    private Map<String, ModelObjectPersister> persisters = null;
    
    /**
     * Instantiates a new multi model object persister.
     * 
     * @param objectTypeId the object type id
     * @param service the service
     * @param persisters the persisters
     */
    public MultiModelObjectPersister(String objectTypeId, WebFrameworkService service, Map<String, ModelObjectPersister> persisters)
    {
        this.persisterId = "MultiModelObjectPersister_" + objectTypeId;
        this.objectTypeId = objectTypeId;
        this.service = service;
        this.persisters = persisters;
        
        if(logger.isDebugEnabled())
            logger.debug("MultiModelObjectPersister loaded with persisters: " + persisters);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getId()
     */
    public String getId()
    {
        return this.persisterId;
    }
    
    /**
     * Gets the object type id.
     * 
     * @return the object type id
     */
    public String getObjectTypeId()
    {
        return this.objectTypeId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public ModelObject getObject(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException
    {
        ModelObject modelObject = null;
        
        // for each persister, see if we can load the object from its
        // underlying storage
        Iterator<String> it = this.persisters.keySet().iterator();
        while (it.hasNext())
        {
            String persisterId = it.next();
            ModelObjectPersister persister = persisters.get(persisterId);
            
            // try to load the object
            try
            {
                modelObject = persister.getObject(context, objectId);
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isDebugEnabled())
                    logger.debug(mope);
            }
            if (modelObject != null)
            {
                if (logger.isDebugEnabled())
                    logger.debug("getObject loaded '" + objectId + "' from persister: " + persisterId); 

                // if we have the object, jump out
                break;
            }
        }
        
        if (modelObject == null && logger.isDebugEnabled())
        {
            logger.debug("getObject() unable to get object from any persisters");
        }
        
        return modelObject;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#saveObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
     */
    public boolean saveObject(ModelPersistenceContext context, ModelObject object)
        throws ModelObjectPersisterException    
    {
        boolean saved = false;
        
        // get the persister to use for this object
        ModelObjectPersister persister = this.service.getPersisterById(object.getPersisterId());
        if (persister != null)
        {
            saved = persister.saveObject(context, object);
            if (logger.isDebugEnabled())
                logger.debug("saveObject save to persister '" + persisterId + "' returned: " + saved);
        }
        
        return saved;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#removeObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
     */
    public boolean removeObject(ModelPersistenceContext context, ModelObject object)
        throws ModelObjectPersisterException    
    {
        return removeObject(context, object.getPersisterId());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#removeObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public boolean removeObject(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException    
    {
        boolean removed = false;
        
        // for each persister, see if we can load the object from its underlying storage
        Iterator<String> it = this.persisters.keySet().iterator();
        while (it.hasNext())
        {
            String persisterId = it.next();
            ModelObjectPersister persister = persisters.get(persisterId);
            
            if (persister.hasObject(context, objectId))
            {
                removed = persister.removeObject(context, objectId);
                
                if(logger.isDebugEnabled())
                    logger.debug("removeObject remove from persister '" + persisterId + "' returned: " + removed);
                
                break;
            }
        }
        
        return removed;        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#hasObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
     */
    public boolean hasObject(ModelPersistenceContext context, ModelObject object)
    {
        return hasObject(context, object.getStoragePath());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#hasObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public boolean hasObject(ModelPersistenceContext context, String objectId)  
    {
        boolean hasObject = false;
        
        // for each persister, see if we can load the object from its underlying storage
        Iterator<String> it = this.persisters.keySet().iterator();
        while (it.hasNext())
        {
            String persisterId = it.next();
            ModelObjectPersister persister = persisters.get(persisterId);
            
            if (persister.hasObject(context, objectId))
            {
                hasObject = true;
            }
        }
        
        return hasObject;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#newObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public ModelObject newObject(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException    
    {
        ModelObject obj = null;
        
        // get the default persister for this object type
        ModelObjectPersister persister = this.service.getDefaultPersister(this.objectTypeId);
        if (persister != null)
        {
           obj = persister.newObject(context, objectId);
           
           if (logger.isDebugEnabled())
               logger.debug("newObject created on persister '" + persister.getId() + "' returned: " + obj);
        }
        else
        {
            throw new ModelObjectPersisterException("Unable to create new object - no default persister found for object type id: " + this.objectTypeId);
        }
        
        return obj;
    }  
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getAllObjects(org.alfresco.web.framework.ModelPersistenceContext)
     */
    public Map<String, ModelObject> getAllObjects(ModelPersistenceContext context)
        throws ModelObjectPersisterException
    {
        Map<String, ModelObject> objects = new HashMap<String, ModelObject>(512, 1.0f);
        
        // for each persister, see if we can load the object from its underlying storage
        Iterator<String> it = this.persisters.keySet().iterator();
        while (it.hasNext())
        {
            String persisterId = it.next();
            ModelObjectPersister persister = persisters.get(persisterId);
            
            Map<String, ModelObject> map = persister.getAllObjects(context);
            objects.putAll(map);
        }
        
        if (logger.isDebugEnabled())
            logger.debug("getAllObjects for type: " + this.objectTypeId + " return set of size: " + objects.size());
        
        return objects;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getTimestamp(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public long getTimestamp(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException
    {
        long timestamp = -1;
        
        // find the persister that has the object
        Iterator<String> it = this.persisters.keySet().iterator();
        while (it.hasNext())
        {
            String persisterId = it.next();
            ModelObjectPersister persister = persisters.get(persisterId);
            
            if (persister.hasObject(context, objectId))
            {
                timestamp = persister.getTimestamp(context, objectId);
            }
        }
        
        if (timestamp == -1)
        {
            throw new ModelObjectPersisterException("Unable to find object: " + objectId + " in any persister, unable to return timestamp");
        }
        
        return timestamp;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#invalidateCache()
     */
    public void invalidateCache()
    {
        Iterator<ModelObjectPersister> it = this.persisters.values().iterator();
        while (it.hasNext())
        {
            ModelObjectPersister persister = it.next();            
            persister.invalidateCache();
        }
    }    
}
