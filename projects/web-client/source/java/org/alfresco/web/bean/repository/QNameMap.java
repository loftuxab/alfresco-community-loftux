package org.alfresco.web.bean.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Map that holds as it's key a QName stored in it's internal String representation.
 * Calls to get and put automatically map the key to and from the QName representation.
 * 
 * @author gavinc
 */
public class QNameMap<K,V> implements Map
{
   private Log logger = LogFactory.getLog(QNameMap.class);
   private Map<String, Object> contents = new HashMap<String, Object>(7, 1.0f);
   
   /**
    * @see java.util.Map#size()
    */
   public int size()
   {
      return this.contents.size();
   }

   /**
    * @see java.util.Map#isEmpty()
    */
   public boolean isEmpty()
   {
      return this.contents.isEmpty();
   }

   /**
    * @see java.util.Map#containsKey(java.lang.Object)
    */
   public boolean containsKey(Object key)
   {
      return this.contents.containsKey(Repository.resolveToQNameString((String)key));
   }

   /**
    * @see java.util.Map#containsValue(java.lang.Object)
    */
   public boolean containsValue(Object value)
   {
      return this.contents.containsValue(value);
   }

   /**
    * @see java.util.Map#get(java.lang.Object)
    */
   public Object get(Object key)
   {
      return this.contents.get(Repository.resolveToQNameString((String)key));
   }

   /**
    * @see java.util.Map#put(K, V)
    */
   public Object put(Object key, Object value)
   {
      return this.contents.put(Repository.resolveToQNameString((String)key), value);
   }

   /**
    * @see java.util.Map#remove(java.lang.Object)
    */
   public Object remove(Object key)
   {
      return this.contents.remove(Repository.resolveToQNameString((String)key));
   }

   /**
    * @see java.util.Map#putAll(java.util.Map)
    */
   public void putAll(Map t)
   {
      for (Object key : t.keySet())
      {
         this.put(key, t.get(key));
      }
   }

   /**
    * @see java.util.Map#clear()
    */
   public void clear()
   {
      this.contents.clear();
   }

   /**
    * @see java.util.Map#keySet()
    */
   public Set keySet()
   {
      return this.contents.keySet();
   }

   /**
    * @see java.util.Map#values()
    */
   public Collection values()
   {
      return this.contents.values();
   }

   /**
    * @see java.util.Map#entrySet()
    */
   public Set entrySet()
   {
      return this.contents.entrySet();
   }
}
