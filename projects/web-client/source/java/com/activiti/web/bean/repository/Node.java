package com.activiti.web.bean.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Lighweight client side representation of a node held in the repository. 
 * 
 * @author gavinc
 */
public class Node implements Serializable, Map<String, Object>
{
   private static final long serialVersionUID = 3544390322739034169L;

   private static Logger logger = Logger.getLogger(Node.class);
   
   private String type;
   private Map<String, Object> properties = new HashMap(7, 1.0f);
   
   public Node(String type)
   {
      this.type = type;
   }

   /**
    * @return Returns the type.
    */
   public String getType()
   {
      return type;
   }
   
   /**
    * @return The display name for the node
    */
   public String getName()
   {
      return this.properties.get("name").toString();
   }
   
   /**
    * @return The GUID for the node
    */
   public String getId()
   {
      return this.properties.get("id").toString();
   }

   /**
    * @param properties The properties to set.
    */
   public void setProperties(Map<String, Object> properties)
   {
      this.properties = properties;
   }
   
   /**
    * Used to save the properties edited by the user
    * 
    * @return The outcome string
    */
   public String persist()
   {
      logger.debug("Updating properties for: " + this + "; properties = " + this.properties);
      
      // TODO: Use whatever service to persist the Node back to the repository
      
      return "success";
   }

   
   // ------------------------------------------------------------------------------
   // Map implementation - allows the Node bean to be accessed using JSF expression syntax 
   
   /**
    * @see java.util.Map#clear()
    */
   public void clear()
   {
      this.properties.clear();
   }

   /**
    * @see java.util.Map#containsKey(java.lang.Object)
    */
   public boolean containsKey(Object key)
   {
      return this.properties.containsKey(key);
   }

   /**
    * @see java.util.Map#containsValue(java.lang.Object)
    */
   public boolean containsValue(Object value)
   {
      return this.properties.containsKey(value);
   }

   /**
    * @see java.util.Map#entrySet()
    */
   public Set entrySet()
   {
      return this.properties.entrySet();
   }

   /**
    * @see java.util.Map#get(java.lang.Object)
    */
   public Object get(Object key)
   {
      return this.properties.get(key);
   }

   /**
    * @see java.util.Map#isEmpty()
    */
   public boolean isEmpty()
   {
      return this.properties.isEmpty();
   }

   /**
    * @see java.util.Map#keySet()
    */
   public Set keySet()
   {
      return this.properties.keySet();
   }

   /**
    * @see java.util.Map#put(K, V)
    */
   public Object put(String key, Object value)
   {
      return this.properties.put(key, value);
   }

   /**
    * @see java.util.Map#putAll(java.util.Map)
    */
   public void putAll(Map t)
   {
      this.properties.putAll(t);
   }

   /**
    * @see java.util.Map#remove(java.lang.Object)
    */
   public Object remove(Object key)
   {
      return this.properties.remove(key);
   }

   /**
    * @see java.util.Map#size()
    */
   public int size()
   {
      return this.properties.size();
   }

   /**
    * @see java.util.Map#values()
    */
   public Collection values()
   {
      return this.properties.values();
   }

   /**
    * @see java.lang.Object#toString()
    */
//   public String toString()
//   {
//      StringBuilder buffer = new StringBuilder();
//      buffer.append(super.toString());
//      buffer.append(" (type=").append(this.type);
//      buffer.append(" properties=").append(this.properties).append(")");
//      return buffer.toString();
//   }
   
   
}
