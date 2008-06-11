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
public class CacheItem implements java.io.Serializable
{
    private static final long serialVersionUID = 4526472295622776147L;

    protected String m_key;
    protected Object m_object;
    protected long m_timeout;
    protected long m_stamp;
    
    /**
     * Instantiates a new cache item.
     * 
     * @param key the key
     * @param obj the obj
     * @param timeout the timeout
     */
    public CacheItem(String key, Object obj, long timeout)
    {
        m_timeout = timeout;
        m_key = key;
        m_object = obj;
        m_stamp = System.currentTimeMillis();
    }

    /**
     * Checks if is expired.
     * 
     * @return true, if is expired
     */
    public boolean isExpired()
    {
        // never timeout for -1
        if (m_timeout == -1)
        {
            return false;
        }

        return (m_timeout < (System.currentTimeMillis() - m_stamp));
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
        out.writeObject(this.m_key);
        out.writeObject(new Long(this.m_timeout));
        out.writeObject(new Long(this.m_stamp));
        out.writeObject(this.m_object);
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
        this.m_key = (String) in.readObject();
        this.m_timeout = ((Long) in.readObject()).longValue();
        this.m_stamp = ((Long) in.readObject()).longValue();
        this.m_object = ((Object) in.readObject());
    }
}
