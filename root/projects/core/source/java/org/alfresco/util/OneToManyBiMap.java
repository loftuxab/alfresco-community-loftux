/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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

package org.alfresco.util;

/**
 * An extension of <code>org.alfresco.util.OneToManyMap</code> that stores the
 * inverse mapping from a value to its key.
 * 
 * @author Nick Smith
 */
public interface OneToManyBiMap<K, V> extends OneToManyMap<K, V>
{

    /**
     * Returns the key, if any, for the specified <code>value</code>. If the
     * specified value does not exist within the map then this method returns
     * <code>null</code>.
     * 
     * @param value
     * @return The key to the specified <code>value</code> or <code>null</code>.
     */
    public abstract K getKey(V value);

    /**
     * Removes the specified <code>value</code> from the <code>OneToManyBiMap</code>. If this was the only value associated with the key to this value, then the key is also removed.
     * 
     * @param value The value to be removed.
     * @return The key that is associated with the value to be removed.
     */
    public abstract K removeValue(V value);

}