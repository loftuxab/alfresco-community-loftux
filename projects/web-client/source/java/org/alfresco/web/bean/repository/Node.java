package org.alfresco.web.bean.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import org.alfresco.repo.ref.NodeRef;

/**
 * Lighweight client side representation of a node held in the repository. 
 * 
 * TODO: This object should be retrieved via Spring (singleton = false)
 *       and then the nodeService etc. can be injected in here and be
 *       used to find metadata etc.
 * 
 * 
 * @author gavinc
 */
public class Node implements Serializable, Map<String, Object>
{
   private static final long serialVersionUID = 3544390322739034169L;

   private static Logger logger = Logger.getLogger(Node.class);
   
   private NodeRef nodeRef;
   private String type;
   private Map<String, Object> properties = new HashMap(7, 1.0f);
   
   /**
    * Constructor
    * 
    * @param nodeRef    The NodeRef this Node wrapper represents
    * @param type       Type of the Node this represents
    */
   public Node(NodeRef nodeRef, String type)
   {
      if (nodeRef == null)
      {
         throw new IllegalArgumentException("NodeRef must be specified during creation of a Node.");
      }
      if (type == null || type.length() == 0)
      {
         throw new IllegalArgumentException("Node Type must be specified during the creation of a Node.");
      }
      
      this.type = type;
      this.nodeRef = nodeRef;
      
      // also add the type to the properties so it can be retrieved
      // that way too (for value binding expressions)
      // TODO: this needs to reviewed though as we don't want this appearing as a property!
      //       we should have several types of Node (e.g. Node as as interface) Map type used for datagrids
      // IMPORTANT: these props will be lost if you call setProperties() later!
      this.properties.put("type", this.type);
      this.properties.put("nodeRef", this.nodeRef);
   }

   /**
    * @return Returns the NodeRef this Node object represents
    */
   public NodeRef getNodeRef()
   {
      return this.nodeRef;
   }
   
   /**
    * @return Returns the type.
    */
   public String getType()
   {
      // TODO: Use the node service to retrieve the type and
      //       remove the type from the constructor
      
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
    * @return The list of aspects applied to this node
    */
   public List getAspects()
   {
      // TODO: Use the node service to retrieve the aspects
      
      return null;
   }

   /**
    * @return The GUID for the node
    */
   public String getId()
   {
      return this.properties.get("id").toString();
   }

   public String getPath()
   {
      // TODO: Use the node service to retrieve the path
      
      return null;
   }
   
   /**
    * @param properties The properties to set.
    */
   public void setProperties(Map<String, Object> properties)
   {
      // TODO: Use the node service to retrieve the properties,
      //       this will probably be done in the constructor so
      //       this method can be removed.
      
      this.properties = properties;
      
      // also add the type to the properties so it can be retrieved
      // that way too (for value binding expressions), this needs to
      // reviewed though as we don't want this appearing as a property!
      this.properties.put("type", this.type);
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
