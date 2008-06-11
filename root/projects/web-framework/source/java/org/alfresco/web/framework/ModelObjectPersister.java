/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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

import org.alfresco.web.framework.exception.ModelObjectPersisterException;

/**
 * @author muzquiano
 */
public interface ModelObjectPersister
{
    /**
     * Returns a unique id for this persister
     * 
     * If this persister is wrapped around a ClassPath store,
     * a LocalFileSystem store or a Repository store, this will return
     * the value provided getBasePath()
     * 
     * If this is wrapped around a RemoteStore, this will return the
     * AVM Store ID to which this persister is bound
     * 
     * @return
     */
    public String getId();
    
    /**
     * Gets an object from persisted storage by path
     * 
     * @param objectId
     * @return
     */
    public ModelObject getObject(ModelPersistenceContext context, String objectId) 
        throws ModelObjectPersisterException;
    
    /**
     * Saves an object to persisted storage
     * 
     * @param object
     */
    public boolean saveObject(ModelPersistenceContext context, ModelObject object) 
        throws ModelObjectPersisterException;
    
    /**
     * Removes an object from persisted storage
     * 
     * @param object
     * @return whether the object was removed
     */
    public boolean removeObject(ModelPersistenceContext context, ModelObject object) 
        throws ModelObjectPersisterException;
    
    /**
     * Removes an object from persisted storage
     * 
     * @param objectId
     * @return whether the object was removed
     */
    public boolean removeObject(ModelPersistenceContext context, String objectId) 
        throws ModelObjectPersisterException;
    
    /**
     * Checks whether the given object is persisted
     * 
     * @param object
     * @return
     */
    public boolean hasObject(ModelPersistenceContext context, ModelObject object)
        throws ModelObjectPersisterException;
    
    /**
     * Checks whether an object with the given path is persisted
     * 
     * @param objectId
     * @return
     */
    public boolean hasObject(ModelPersistenceContext context, String objectId);
    
    /**
     * Creates a new object
     * 
     * @param objectId
     * 
     * @return the object
     */
    public ModelObject newObject(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException;
    
    /**
     * Returns a map of all of the objects
     * 
     * In general, this is a very expensive call
     * 
     * @return
     * @throws ModelObjectException
     */
    public Map<String, ModelObject> getAllObjects(ModelPersistenceContext context)
        throws ModelObjectPersisterException;

    /**
     * Returns the timestamp of the given object in the underlying store
     * 
     * @param context
     * @param objectId
     * @return
     * @throws ModelObjectPersisterException
     */
    public long getTimestamp(ModelPersistenceContext context, String objectId)
        throws ModelObjectPersisterException;
    
    /**
     * Invalidates the entire cache for this persister
     */
    public void invalidateCache();
    
}
