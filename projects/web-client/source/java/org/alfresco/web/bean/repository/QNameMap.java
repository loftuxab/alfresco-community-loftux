/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
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
public final class QNameMap<K,V> implements Map, Cloneable
{
   private static Log logger = LogFactory.getLog(QNameMap.class);
   private Map<String, Object> contents = new HashMap<String, Object>(11, 1.0f);
   private Node parent = null;
   private Map<String, NodePropertyResolver> resolvers = new HashMap<String, NodePropertyResolver>(11, 1.0f);
   
   /**
    * Constructor
    * 
    * @param parent     Parent Node of the QNameMap
    */
   public QNameMap(Node parent)
   {
      if (parent == null)
      {
         throw new IllegalArgumentException("Parent Node cannot be null!");
      }
      this.parent = parent;
   }
   
   /**
    * Register a property resolver for the named property.
    * 
    * @param name       Name of the property this resolver is for
    * @param resolver   Property resolver to register
    */
   public void addPropertyResolver(String name, NodePropertyResolver resolver)
   {
      this.resolvers.put(name, resolver);
   }
   
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
      String qnameKey = Repository.resolveToQNameString(key.toString());
      Object obj = this.contents.get(qnameKey);
      if (obj == null)
      {
         // if a property resolver exists for this property name then invoke it
         NodePropertyResolver resolver = this.resolvers.get(key.toString());
         if (resolver != null)
         {
            obj = resolver.get(this.parent);
            // cache the result
            // obviously the cache is useless if the result is null, in most cases it shouldn't be
            this.contents.put(qnameKey, obj);
         }
      }
      
      return obj;
   }
   
   /**
    * Perform a get without using property resolvers
    * 
    * @param key    item key
    * @return object
    */
   public Object getRaw(Object key)
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
   
   /**
    * Override Object.toString() to provide useful debug output
    */
   public String toString()
   {
      return this.contents.toString();
   }
   
   /**
    * Shallow copy the map by copying keys and values into a new QNameMap
    */
   public Object clone()
   {
      QNameMap map = new QNameMap(this.parent);
      map.putAll(this);
      if (this.resolvers.size() != 0)
      {
         map.resolvers = (Map)((HashMap)this.resolvers).clone();
      }
      return map;
   }
}
