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
 * @author kevinr
 */
public abstract class AbstractModelObjectPersister implements ModelObjectPersister
{
    protected final String objectTypeId;

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
    public boolean removeObject(ModelPersistenceContext context, ModelObject object)
        throws ModelObjectPersisterException    
    {
        return removeObject(context, object.getId());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#hasObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
     */
    public boolean hasObject(ModelPersistenceContext context, ModelObject object) throws ModelObjectPersisterException
    {
        return hasObject(context, object.getId());
    }
    
    /**
     * Default way to convert a storage path into an object id is to
     * strips the extension (if any) from the file name.
     * <p>
     * This method should never return a null value.
     * 
     * @param path  the storage path
     * 
     * @return the id for this path
     */
    public String pathToId(String path)
    {
        String id = path;
        
        int i = path.lastIndexOf('.');
        if (i != -1)
        {
            id = path.substring(0, i);
        }
        
        return id;
    }
    
    /**
     * Default way to convert an object id into a storage path
     * is to add the .xml extension to the object id.
     * <p>
     * This method should never return a null value.
     * 
     * @param objectId the object id
     * 
     * @return the storage path for this id
     */
    public String idToPath(String id)
    {
        return new StringBuilder(id.length() + 4).append(id).append(".xml").toString();
    }
}