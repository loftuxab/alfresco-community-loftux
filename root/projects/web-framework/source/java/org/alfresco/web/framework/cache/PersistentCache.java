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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This is a more advanced version of a cache that passivates objects to
 * disk.  If objects are cleaned out of the WeakHashMap, they are still
 * retained on disk and can be reloaded from disk.
 * 
 * Thus, the disk provides the true "persistent" cache and the
 * in-memory cache still exists for performance purposes.
 * 
 * @author muzquiano
 */
public class PersistentCache extends BasicCache
{
    private String m_cacheLocation;
    
    /**
     * Instantiates a new persistent cache.
     * 
     * @param default_timeout the default_timeout
     * @param cacheDir the cache dir
     */
    public PersistentCache(long default_timeout, String cacheDir)
    {
        super(default_timeout);
        m_cacheLocation = cacheDir;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.BasicCache#get(java.lang.String)
     */
    public synchronized Object get(String key)
    {
        boolean bGottenFromMemory = false;
        boolean bReloadedFromDisk = false;

        // get the content item from the cache
        CacheItem item = (CacheItem) cache.get(key);

        // if the cache item is null, then we should try to reload it
        // from disk.  it might have been cached and cleared from memory.
        if (item == null)
        {
            item = (CacheItem) loadFromDisk(key);

            // if the item is still null, then just return null;
            if (item == null)
                return null;

            // otherwise, let's indicate that we reloaded it
            bReloadedFromDisk = true;
        }
        else
            bGottenFromMemory = true;

        // at this point, we have a content item
        // is it still valid?
        if (item.isExpired())
        {
            // it's not valid, throw it away
            remove(key);

            return null;
        }

        // if it was gotten from memory, make sure it's also on disk
        if (bGottenFromMemory)
        {
            if (!existsOnDisk(key))
            {
                remove(key);

                return null;
            }
        }

        // if it was reloaded, make sure its stored in memory
        if (bReloadedFromDisk)
            cache.put(key, item);

        // return this
        return item.object;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.BasicCache#remove(java.lang.String)
     */
    public synchronized void remove(String key)
    {
        if (key == null)
            return;

        cache.remove(key);
        removeFromDisk(key);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.BasicCache#put(java.lang.String, java.lang.Object, long)
     */
    public synchronized void put(String key, Object obj, long timeout)
    {
        if (key == null || obj == null)
            return;

        // create the cache item and write to memory and disk
        CacheItem item = new CacheItem(key, obj, timeout);
        if (writeToDisk(key, item))
        {
            cache.put(key, item);
        }
    }

    /**
     * Returns whether the disk-cached object for the given pathID exists.
     * 
     * @param pathID the path id
     * 
     * @return true, if exists on disk
     */
    protected boolean existsOnDisk(String pathID)
    {
        File file = getCacheFile(pathID);
        if (file == null)
            return false;
        return file.exists();
    }

    /**
     * Loads a content item from disk if it exists.
     * Returns null if the item doesn't exist on disk.
     * If the file on disk is corrupt, the file is cleaned up.
     * 
     * @param pathID the path id
     * 
     * @return the cache item
     */
    protected CacheItem loadFromDisk(String pathID)
    {
        File file = getCacheFile(pathID);
        if (file == null)
            return null;

        CacheItem item = null;
        try
        {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            item = (CacheItem) ois.readObject();
            ois.close();
            fis.close();
        }
        catch (Exception e)
        { /* Ignore */
        }

        return item;
    }

    /**
     * Serializes the given content item to disk for the given path string.
     * 
     * @param pathID the path id
     * @param item the item
     * 
     * @return true, if write to disk
     */
    protected synchronized boolean writeToDisk(String pathID, CacheItem item)
    {
        File file = getCacheFile(pathID);
        if (file == null)
        {
            return false;
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(item);
            oos.flush();
            oos.close();
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    /**
     * Removes a content item cached file from disk for the given path id.
     * 
     * @param pathID the path id
     */
    protected void removeFromDisk(String pathID)
    {
        File file = getCacheFile(pathID);
        if (file == null)
            return;
        if (file.exists())
            file.delete();
    }

    /**
     * Returns the literal java.io.File path for the given path ID.
     * 
     * @param pathID the path id
     * 
     * @return the cache file
     */
    protected File getCacheFile(String pathID)
    {
        if (pathID == null || pathID.length() == 0)
        {
            return null;
        }

        // build the literal file path
        StringBuilder buffer = new StringBuilder(m_cacheLocation);
        buffer.append(pathID);

        // make sure the directory structure exists
        File file = new File(buffer.toString());
        File parent = new File(file.getParent());
        parent.mkdirs();

        return file;
    }
}
