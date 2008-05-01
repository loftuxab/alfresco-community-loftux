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
package org.alfresco.web.site.cache;

/**
 * The Interface IContentCache.
 * 
 * @author muzquiano
 */
public interface IContentCache
{
    /*
     *  Returns the content object for the given path.  This method checks to make sure
     *  that the timeout for the object hasn't expired.  If the timeout has expired, the
     *  object is cleaned up and null is returned.
     */
    /**
     * Gets the.
     * 
     * @param key the key
     * 
     * @return the object
     */
    public Object get(String key);

    /*
     *  Removes the content object from the cache.
     */
    /**
     * Removes the.
     * 
     * @param key the key
     */
    public void remove(String key);

    /*
     *  Adds the given content object to the cache, keyed from the given path.
     *  If a content item exists at the given path, it is replaced.
     */
    /**
     * Put.
     * 
     * @param key the key
     * @param obj the obj
     * @param timeout the timeout
     */
    public void put(String key, Object obj, long timeout);

    /**
     * Put.
     * 
     * @param key the key
     * @param obj the obj
     */
    public void put(String key, Object obj);

    /**
     * Invalidate all.
     */
    public void invalidateAll();

    /**
     * Sets the reporting.
     * 
     * @param b the new reporting
     */
    public void setReporting(boolean b);

    /**
     * Checks if is reporting.
     * 
     * @return true, if is reporting
     */
    public boolean isReporting();
}
