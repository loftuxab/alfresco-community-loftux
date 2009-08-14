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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * @author Nick Smith
 */
public class OneToManyHashMap<K, V> implements Map<K, Set<V>>, OneToManyMap<K, V>
{
    //Delegate map.
    private final Map<K, Set<V>> map = new HashMap<K, Set<V>>();

    public void clear()
    {
        map.clear();
    }

    public boolean containsKey(Object key)
    {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }

    /*
     * @see org.alfresco.util.OneToManyMap#containsSingleValue(V)
     */
    public boolean containsSingleValue(V value)
    {
        Collection<Set<V>> values = map.values();
        for (Set<V> set : values)
        {
            if (set.contains(value)) return true;

        }
        return false;
    }

    public Set<Entry<K, Set<V>>> entrySet()
    {
        return map.entrySet();
    }

    /*
     * @see org.alfresco.util.OneToManyMap#entries()
     */
    public Set<Entry<K, V>> entries()
    {
        Set<Entry<K, V>> entries = new HashSet<Entry<K, V>>();
        for (Entry<K, Set<V>> entry : map.entrySet())
        {
            final K key = entry.getKey();
            final Set<V> values = entry.getValue();
            for (final V value : values)
            {
                entries.add(new Entry<K, V>()
                {

                    public K getKey()
                    {
                        return key;
                    }

                    public V getValue()
                    {
                        return value;
                    }

                    // Not Thread-safe!
                    public V setValue(V newValue)
                    {
                        throw new UnsupportedOperationException(
                                    "Cannot modify the entries returned by "
                                                + OneToManyHashMap.class.getName() + ".entries()!");
                    }
                });
            }
        }
        return entries;
    }

    public Set<V> get(Object key)
    {
        Set<V> set = map.get(key);
        if (set == null) set = new HashSet<V>();
        return Collections.unmodifiableSet(set);
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public Set<K> keySet()
    {
        return map.keySet();
    }

    public Set<V> put(K key, Set<V> value)
    {
        return map.put(key, value);
    }

    /*
     * @see org.alfresco.util.OneToManyMap#putSingleValue(K, V)
     */
    public V putSingleValue(K key, V value)
    {
        Set<V> values = map.get(key);
        if (values == null)
        {
            values = new HashSet<V>();
            map.put(key, values);
        }
        values.add(value);
        return value;
    }

    public void putAll(Map<? extends K, ? extends Set<V>> m)
    {
        map.putAll(m);
    }

    /*
     * @see org.alfresco.util.OneToManyMap#putAllSingleValues(java.util.Map)
     */
    public void putAllSingleValues(Map<? extends K, ? extends V> m)
    {
        for (Entry<? extends K, ? extends V> entry : m.entrySet())
        {
            putSingleValue(entry.getKey(), entry.getValue());
        }
    }

    public Set<V> remove(Object key)
    {
        return map.remove(key);
    }

    public int size()
    {
        return map.size();
    }

    public Collection<Set<V>> values()
    {
        return map.values();
    }

    /*
     * @see org.alfresco.util.OneToManyMap#flatValues()
     */
    public Collection<V> flatValues()
    {
        LinkedList<V> flatValues = new LinkedList<V>();
        for (Set<V> values : map.values())
        {
            flatValues.addAll(values);
        }
        return flatValues;
    }
}
