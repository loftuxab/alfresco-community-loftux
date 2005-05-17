package org.alfresco.web.bean.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.util.Conversion;

/**
 * Lighweight client side representation of a node held in the repository, which
 * is modelled as a map for use in the data tables. 
 * 
 * @author gavinc
 */
public class MapNode extends Node implements Map<String, Object>
{
   private static final long serialVersionUID = 4051322327734433079L;

   /**
    * Constructor
    * 
    * @param nodeRef The NodeRef this Node wrapper represents
    * @param nodeService The node service to use to retrieve data for this node 
    */
   public MapNode(NodeRef nodeRef, NodeService nodeService)
   {
      super(nodeRef, nodeService);
   }
   
   
   // ------------------------------------------------------------------------------
   // Map implementation - allows the Node bean to be accessed using JSF expression syntax 
   
   /**
    * @see java.util.Map#clear()
    */
   public void clear()
   {
      getProperties().clear();
   }

   /**
    * @see java.util.Map#containsKey(java.lang.Object)
    */
   public boolean containsKey(Object key)
   {
      return getProperties().containsKey(key);
   }

   /**
    * @see java.util.Map#containsValue(java.lang.Object)
    */
   public boolean containsValue(Object value)
   {
      return getProperties().containsKey(value);
   }

   /**
    * @see java.util.Map#entrySet()
    */
   public Set entrySet()
   {
      return getProperties().entrySet();
   }

   /**
    * @see java.util.Map#get(java.lang.Object)
    */
   public Object get(Object key)
   {
      Object obj = null;
      
      // there are some properties that aren't available as properties
      // but from method calls, so for these handle them individually
      Map<String, Object> props = getProperties();
      if (propsInitialised == false)
      {
         // well known properties required as publically accessable map attributes
         props.put("id", this.getId());
         props.put("type", this.getType());
         props.put("name", this.getName());
         props.put("nodeRef", this.getNodeRef());
         
         propsInitialised = true;
      }
      
      obj = props.get(key);
      
      // temp HACK
      // TODO: should use data dictionary to decide on conversion here?!
      if (obj == null && key.equals("description"))
      {
         obj = "";
      }
      
      if (obj != null && (key.equals("createddate") || key.equals("modifieddate")))
      {
         String date = obj.toString();
         obj = Conversion.dateFromXmlDate(date);
      }
      
      return obj; 
   }

   /**
    * @see java.util.Map#isEmpty()
    */
   public boolean isEmpty()
   {
      return getProperties().isEmpty();
   }

   /**
    * @see java.util.Map#keySet()
    */
   public Set keySet()
   {
      return getProperties().keySet();
   }

   /**
    * @see java.util.Map#put(K, V)
    */
   public Object put(String key, Object value)
   {
      return getProperties().put(key, value);
   }

   /**
    * @see java.util.Map#putAll(java.util.Map)
    */
   public void putAll(Map t)
   {
      getProperties().putAll(t);
   }

   /**
    * @see java.util.Map#remove(java.lang.Object)
    */
   public Object remove(Object key)
   {
      return getProperties().remove(key);
   }

   /**
    * @see java.util.Map#size()
    */
   public int size()
   {
      return getProperties().size();
   }

   /**
    * @see java.util.Map#values()
    */
   public Collection values()
   {
      return getProperties().values();
   }
   
   private boolean propsInitialised = false;
}
