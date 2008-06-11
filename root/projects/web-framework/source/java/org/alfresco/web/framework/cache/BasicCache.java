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

import java.util.WeakHashMap;

/**
 * This is an implementation of a purely in-memory cache that uses a
 * WeakHashMap to provide a basic form of caching.
 * 
 * @author muzquiano
 */
public class BasicCache<K> implements ContentCache<K>
{
    protected WeakHashMap<String, CacheItem<K>> m_cache;
    protected long m_default_timeout;
    
    /**
     * Instantiates a new basic cache.
     * 
     * @param default_timeout the default_timeout
     */
    public BasicCache(long default_timeout)
    {
        m_default_timeout = default_timeout;
        m_cache = new WeakHashMap<String, CacheItem<K>>(256);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.ContentCache#get(java.lang.String)
     */
    public synchronized K get(String key)
    {
        // get the content item from the cache
        CacheItem<K> item = m_cache.get(key);
        
        // if the cache item is null, return right away
        if (item == null)
        {
            return null;
        }
        else
        {
            // ask the cache item if it's still valid
            if (item.isExpired())
            {
                // it's not valid, throw it away
                remove(key);
                return null;
            }
            
            return item.m_object;
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.ContentCache#remove(java.lang.String)
     */
    public synchronized void remove(String key)
    {
        if (key == null)
        {
            return;
        }
        m_cache.remove(key);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.ContentCache#put(java.lang.String, java.lang.Object)
     */
    public synchronized void put(String key, K obj)
    {
        put(key, obj, m_default_timeout);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.ContentCache#put(java.lang.String, java.lang.Object, long)
     */
    public synchronized void put(String key, K obj, long timeout)
    {
        // create the cache item
        CacheItem<K> item = new CacheItem<K>(key, obj, timeout);
        m_cache.put(key, item);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.ContentCache#invalidate()
     */
    public void invalidate()
    {
        m_cache.clear();
    }
}
