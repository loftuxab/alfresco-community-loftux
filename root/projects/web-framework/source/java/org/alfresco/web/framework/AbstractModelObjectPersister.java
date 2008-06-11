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

import org.alfresco.web.framework.exception.ModelObjectPersisterException;

/**
 * The Class AbstractModelObjectPersister.
 * 
 * @author muzquiano
 */
public abstract class AbstractModelObjectPersister implements ModelObjectPersister
{
    protected String objectTypeId;

    /**
     * Instantiates a new abstract model object persister.
     * 
     * @param objectTypeId the type id
     */
    public AbstractModelObjectPersister(String objectTypeId)
    {
        this.objectTypeId = objectTypeId;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#removeObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
     */
    public boolean removeObject(ModelPersistenceContext context, ModelObject modelObject)
        throws ModelObjectPersisterException    
    {
        return removeObject(context, modelObject.getPersisterId());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#hasObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
     */
    public boolean hasObject(ModelPersistenceContext context, ModelObject object)
    {
        return hasObject(context, object.getPersisterId());
    }
    
    /**
     * Default way to convert a storage path into an object id
     * This essentially just strips the extension from the file name.
     * 
     * @param storagePath the storage path
     * 
     * @return the string
     */
    public String pathToId(String storagePath)
    {
        String id = null;
        
        int x = storagePath.lastIndexOf(".");
        if (x != -1)
        {
            id = storagePath.substring(0, x);
        }
        
        return id;
    }
    
    /**
     * Default way to convert an object id into a storage path
     * This just adds the .xml extension to the id
     * 
     * @param objectId the object id
     * 
     * @return the string
     */
    public String idToPath(String objectId)
    {
        return objectId + ".xml";
    }
}
