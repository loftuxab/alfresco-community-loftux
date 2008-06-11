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
package org.alfresco.web.framework.cache;

import java.io.IOException;

import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.scripts.Store;

/**
 * An enhancement to the in-memory basic cache that considers the in-memory
 * cache to be secondary to the status of content within a persisted
 * store.  The persisted store is a Store implementation that is checked
 * in order to verify that the content is still valid in the cache.
 * 
 * @author muzquiano
 */
public class ModelObjectCache extends BasicCache<ModelObject>
{
    protected Store store = null;
    
    /**
     * Instantiates a new model object cache.
     * 
     * @param store the store
     * @param default_timeout the default_timeout
     */
    public ModelObjectCache(Store store, long default_timeout)
    {
        super(default_timeout);
        
        this.store = store;
    }
 
    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.BasicCache#get(java.lang.String)
     */
    public synchronized ModelObject get(String key)
    {
        ModelObject obj = super.get(key);
        if (obj != null)
        {
            // modification time of our model objec
            long objectTimestamp = obj.getModificationTime();
            
            // check the modification time in the store
            long storeTimestamp = -1;
            try
            {
                storeTimestamp = store.lastModified(key);
            }
            catch (IOException ex)
            {
                // unable to access the timestamp in the store
                // could be many reasons but lets assume the worst case
                // the file may have been deleted
                // thus, remove from cache
                remove(key);
                obj = null;
            }
            
            if (storeTimestamp > -1)
            {
                if (storeTimestamp > objectTimestamp)
                {
                    // the in-memory copy is stale
                    // thus, remove from cache
                    remove(key);
                    obj = null;
                }
            }
        }
        return obj;
    }
}
