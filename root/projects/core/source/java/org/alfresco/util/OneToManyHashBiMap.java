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

package org.alfresco.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Nick Smith
 */
public class OneToManyHashBiMap<K, V> implements Map<K, Set<V>>, OneToManyBiMap<K, V>
{
    // The 'forward' map.
    private OneToManyHashMap<K, V> map = new OneToManyHashMap<K, V>();

    // The inverse map.
    private Map<V, K> inverse = new HashMap<V, K>();

    public void clear()
    {
        map.clear();
        inverse.clear();
    }

    public boolean containsKey(Object key)
    {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }

    public boolean containsSingleValue(V value)
    {
        return inverse.containsKey(value);
    }

    public Set<Entry<K, Set<V>>> entrySet()
    {
        return map.entrySet();
    }

    public Set<Entry<K, V>> entries()
    {
        return map.entries();
    }

    public Set<V> get(Object key)
    {
        return map.get(key);
    }

    /*
     * @see org.alfresco.util.OneToManyBiMap#getKey(V)
     */
    public K getKey(V value)
    {
        return inverse.get(value);
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public Set<K> keySet()
    {
        return map.keySet();
    }

    public Set<V> put(K key, Set<V> values)
    {
        map.put(key, values);
        for (V value : values)
        {
            inverse.put(value, key);
        }
        return null;
    }

    public V putSingleValue(K key, V value)
    {
        inverse.put(value, key);
        return map.putSingleValue(key, value);
    }

    public void putAll(Map<? extends K, ? extends Set<V>> m)
    {
        map.putAll(m);
        for (Entry<? extends K, ? extends Set<V>> entry : m.entrySet())
        {
            K key = entry.getKey();
            for (V value : entry.getValue())
            {
                inverse.put(value, key);
            }
        }
    }

    public void putAllSingleValues(Map<? extends K, ? extends V> m)
    {
        map.putAllSingleValues(m);
        for (Entry<? extends K, ? extends V> entry : m.entrySet())
        {
            inverse.put(entry.getValue(), entry.getKey());
        }
    }

    public Set<V> remove(Object key)
    {
        Set<V> values = map.remove(key);
        for (V value : values)
        {
            inverse.remove(value);
        }
        return values;
    }

    /*
     * @see org.alfresco.util.OneToManyBiMap#removeValue(V)
     */
    public K removeValue(V value)
    {
        K key = inverse.remove(value);
        Set<V> values = map.get(key);
        values.remove(value);
        if (values.size() == 0) map.remove(key);
        return key;
    }

    public int size()
    {
        return map.size();
    }

    public Collection<Set<V>> values()
    {
        return map.values();
    }

    public Collection<V> flatValues()
    {
        return Collections.unmodifiableCollection(inverse.keySet());
    }

}
