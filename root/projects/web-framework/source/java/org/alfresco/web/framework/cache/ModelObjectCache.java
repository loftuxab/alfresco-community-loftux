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
    protected final Store store;
    private final static long timeout = 24L*60L*60L*1000L;   // 24 hours
    private final long delay;
    
    /**
     * Instantiates a new model object cache.
     * 
     * @param store     the store
     * @param delay     the delay to check modified dates for items in the cache
     */
    public ModelObjectCache(Store store, long delay)
    {
        super(timeout);
        this.delay = delay;
        this.store = store;
    }
 
    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.BasicCache#get(java.lang.String)
     */
    @Override
    public synchronized ModelObject get(String key)
    {
        ModelObject obj = null;
        CacheItem<ModelObject> item = cache.get(key);
        if (item != null)
        {
            // get the content item from the cache
            long now = System.currentTimeMillis();
            
            obj = item.object;
            
            if (this.delay < now - item.lastChecked)
            {
                // delay hit - check cached item
                // modification time of our model object
                // check the modification time in the store
                item.lastChecked = now;
                try
                {
                    if (store.lastModified(key) > obj.getModificationTime())
                    {
                        // the in-memory copy is stale, remove from cache
                        remove(key);
                        obj = null;
                    }
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
            }
        }
        
        return obj;
    }
}
