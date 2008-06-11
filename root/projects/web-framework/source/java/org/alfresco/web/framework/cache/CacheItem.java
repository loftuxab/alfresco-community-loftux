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

/**
 * Wraps the cached item with metadata
 * 
 * @author muzquiano
 */
public final class CacheItem<K> implements java.io.Serializable
{
    private static final long serialVersionUID = 4526472295622776147L;

    private String key;
    K object;
    private long timeout;
    private long stamp;
    long lastChecked;
    
    /**
     * Instantiates a new cache item.
     * 
     * @param key the key
     * @param obj the obj
     * @param timeout the timeout
     */
    public CacheItem(String key, K obj, long timeout)
    {
        this.timeout = timeout;
        this.key = key;
        this.object = obj;
        this.lastChecked = this.stamp = System.currentTimeMillis();
    }

    /**
     * Checks if is expired.
     * 
     * @return true, if is expired
     */
    public boolean isExpired()
    {
        // never timeout for -1
        if (timeout == -1)
        {
            return false;
        }

        return (timeout < (System.currentTimeMillis() - stamp));
    }

    /**
     * Serializes the object to an output stream
     * 
     * @param out the out
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.writeObject(this.key);
        out.writeObject(new Long(this.timeout));
        out.writeObject(new Long(this.stamp));
        out.writeObject(this.object);
    }

    /**
     * Deserializes the object from an input stream
     * 
     * @param in the in
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException the class not found exception
     */
    public void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException
    {
        this.key = (String) in.readObject();
        this.timeout = ((Long) in.readObject()).longValue();
        this.stamp = ((Long) in.readObject()).longValue();
        this.object = ((K) in.readObject());
    }
}
