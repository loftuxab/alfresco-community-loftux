package org.alfresco.repo.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A cache backed by a simple <code>ConcurrentHashMap</code>.
 * <p>
 * <b>Note:</b> This cache is not transaction-safe.  Use it for tests or wrap it appropriately.
 * 
 * @author Derek Hulley
 * @since 3.2
 */
public class MemoryCache<K extends Serializable, V extends Object> implements SimpleCache<K, V>
{
    private Map<K, V> map;
    
    public MemoryCache()
    {
        map = new ConcurrentHashMap<K, V>(15);
    }

    public boolean contains(K key)
    {
        return map.containsKey(key);
    }

    public Collection<K> getKeys()
    {
        return map.keySet();
    }

    public V get(K key)
    {
        return map.get(key);
    }

    public void put(K key, V value)
    {
        map.put(key, value);
    }

    public void remove(K key)
    {
        map.remove(key);
    }

    public void clear()
    {
        map.clear();
    }
}
