package org.alfresco.web.bean.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

/**
 * Lighweight client side representation of a node held in the repository. 
 * 
 * @author gavinc
 */
public class Node implements Serializable
{
   private static final long serialVersionUID = 3544390322739034169L;

   private static Logger logger = Logger.getLogger(Node.class);
   
   private NodeRef nodeRef;
   private String name;
   private QName type;
   private String path;
   private String id;
   private Set<QName> aspects = null;
   private Map<String, Object> properties = new HashMap<String, Object>(7, 1.0f);
   private List<String> propertyNames = null;
   
   private boolean propsRetrieved = false;
   private NodeService nodeService;
   
   /**
    * Constructor
    * 
    * @param nodeRef The NodeRef this Node wrapper represents
    * @param nodeService The node service to use to retrieve data for this node 
    */
   public Node(NodeRef nodeRef, NodeService nodeService)
   {
      if (nodeRef == null)
      {
         throw new IllegalArgumentException("NodeRef must be supplied for creation of a Node.");
      }
      
      if (nodeService == null)
      {
         throw new IllegalArgumentException("The NodeService must be supplied for creation of a Node.");
      }
      
      this.nodeRef = nodeRef;
      this.id = nodeRef.getId();
      this.nodeService = nodeService;
      
      if (this.id == null || this.id.length() == 0)
      {
         throw new IllegalArgumentException("The NodeRef id must not be null to create a Node.");
      }
   }

   /**
    * @return All the properties known about this node.
    */
   public Map<String, Object> getProperties()
   {
      if (this.propsRetrieved == false)
      {
         // TODO: How are we going to deal with namespaces, JSF won't understand so
         //       we will need some sort of mechanism to deal with it????
         //       For now just get the local name of each property.
         
         Map<QName, Serializable> props = this.nodeService.getProperties(this.nodeRef);
         
         for (QName qname: props.keySet())
         {
            String localName = qname.getLocalName();
            this.properties.put(localName, props.get(qname));
         }
         
         this.propsRetrieved = true;
      }
      
      return properties;
   }
   
   /**
    * @return A list of the property names currently held by this node
    */
   public List<String> getPropertyNames()
   {
      if (this.propertyNames == null)
      {
         // make sure the properties are available
         this.getProperties();
         // retrieve the list of property names
         this.propertyNames = new ArrayList(this.properties.size());
         for (String propName : this.properties.keySet())
         {
            this.propertyNames.add(propName);
         }
      }
      
      return this.propertyNames;
   }
   
   /**
    * @param propertyName Property to test existence of
    * @return true if property exists, false otherwise
    */
   public boolean hasProperty(String propertyName)
   {
      return getProperties().containsKey(propertyName);
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
   public QName getType()
   {
      if (this.type == null)
      {
         this.type = this.nodeService.getType(this.nodeRef);
      }
      
      return type;
   }
   
   /**
    * @return Returns the type name as a string.
    */
   public String getTypeName()
   {
      return getType().getLocalName();
   }
   
   /**
    * @return The display name for the node
    */
   public String getName()
   {
      if (this.name == null)
      {
         // try and get the name from the properties first
         this.name = (String)getProperties().get("name");
         
         // if we didn't find it as a property get the name from the association name
         if (this.name == null)
         {
            this.name = this.nodeService.getPrimaryParent(this.nodeRef).getQName().getLocalName(); 
         }
      }
      
      return this.name;
   }

   /**
    * @return The list of aspects applied to this node
    */
   public Set<QName> getAspects()
   {
      if (this.aspects == null)
      {
         this.aspects = this.nodeService.getAspects(this.nodeRef);
      }
      
      return this.aspects;
   }
   
   /**
    * @param aspect The aspect to test for
    * @return true if the node has the aspect false otherwise
    */
   public boolean hasAspect(QName aspect)
   {
      Set aspects = getAspects();
      return aspects.contains(aspect);
   }

   /**
    * @return The GUID for the node
    */
   public String getId()
   {
      return this.id;
   }

   /**
    * @return The path for the node
    */
   public String getPath()
   {
      if (this.path == null)
      {
         this.path = this.nodeService.getPath(this.nodeRef).toString();
      }
      
      return this.path;
   }
   
   /**
    * Resets the state of the node to force re-retrieval of the data
    */
   public void reset()
   {
      this.name = null;
      this.type = null;
      this.path = null;
      this.properties = new HashMap<String, Object>(7, 1.0f);
      this.propsRetrieved = false;
      this.aspects = null;

      if (this.propertyNames != null)
      {
         this.propertyNames.clear();
      }
      this.propertyNames = null;
   }
}
