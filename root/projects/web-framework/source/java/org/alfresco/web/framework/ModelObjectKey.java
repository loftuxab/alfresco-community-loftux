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

import java.io.Serializable;

/**
 * The Class ModelObjectKey.
 * 
 * @author muzquiano
 */
public class ModelObjectKey implements Serializable
{
    private String persisterId;
    private String storagePath;
    private String id;
    private boolean saved;
        
    /**
     * Instantiates a new model object key.
     * 
     * @param persisterId the persister id
     * @param storagePath the storage path
     * @param id the id
     */
    public ModelObjectKey(String persisterId, String storagePath, String id, boolean saved)
    {
        this.persisterId = persisterId;
        this.storagePath = storagePath;
        this.id = id;
        this.saved = saved;
    }
    
    /**
     * Gets the persister id.
     * 
     * @return the persister id
     */
    public String getPersisterId()
    {
        return this.persisterId;
    }
    
    /**
     * Sets the storage path
     * 
     * @param storagePath
     */
    protected void setStoragePath(String storagePath)
    {
        this.storagePath = storagePath;
    }
    
    /**
     * Gets the storage path.
     * 
     * @return the storage path
     */
    public String getStoragePath()
    {
        return this.storagePath;
    }
     
    /**
     * Sets the id
     * 
     * @param id
     */
    protected void setId(String id)
    {
        this.id = id;
    }
    
    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return this.id;
    }    
    
    /**
     * Returns whether the object is currently saved or not
     * 
     * @return whether saved
     */
    public boolean isSaved()
    {
        return this.saved;
    }
    
    /**
     * Marks the saved flag on the key
     * 
     * @param saved
     */
    public void setSaved(boolean saved)
    {
        this.saved = saved;
    }
}
