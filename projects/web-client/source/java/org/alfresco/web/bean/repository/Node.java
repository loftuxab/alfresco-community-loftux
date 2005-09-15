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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Lighweight client side representation of a node held in the repository. 
 * 
 * @author gavinc
 */
public class Node implements Serializable
{
   private static final long serialVersionUID = 3544390322739034169L;

   private static Log logger = LogFactory.getLog(Node.class);
   
   protected NodeRef nodeRef;
   private String name;
   private QName type;
   private String path;
   private String id;
   private Set<QName> aspects = null;
   private QNameMap<String, Object> properties;
   private boolean propsRetrieved = false;
   private Map<String, Boolean> permissions;
   protected NodeService nodeService;
   
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
      
      this.properties = new QNameMap<String, Object>(this);
   }

   /**
    * @return All the properties known about this node.
    */
   public final Map<String, Object> getProperties()
   {
      if (this.propsRetrieved == false)
      {
         Map<QName, Serializable> props = this.nodeService.getProperties(this.nodeRef);
         
         for (QName qname: props.keySet())
         {
            this.properties.put(qname.toString(), props.get(qname));
         }
         
         this.propsRetrieved = true;
      }
      
      return properties;
   }
   
   /**
    * Register a property resolver for the named property.
    * 
    * @param name       Name of the property this resolver is for
    * @param resolver   Property resolver to register
    */
   public final void addPropertyResolver(String name, NodePropertyResolver resolver)
   {
      this.properties.addPropertyResolver(name, resolver);
   }
   
   /**
    * @param propertyName Property to test existence of
    * @return true if property exists, false otherwise
    */
   public final boolean hasProperty(String propertyName)
   {
      return getProperties().containsKey(propertyName);
   }

   /**
    * @return Returns the NodeRef this Node object represents
    */
   public final NodeRef getNodeRef()
   {
      return this.nodeRef;
   }
   
   /**
    * @return Returns the type.
    */
   public final QName getType()
   {
      if (this.type == null)
      {
         this.type = this.nodeService.getType(this.nodeRef);
      }
      
      return type;
   }
   
   /**
    * @return The display name for the node
    */
   public final String getName()
   {
      if (this.name == null)
      {
         // try and get the name from the properties first
         this.name = (String)getProperties().get("cm:name");
         
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
   public final Set<QName> getAspects()
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
   public final boolean hasAspect(QName aspect)
   {
      Set aspects = getAspects();
      return aspects.contains(aspect);
   }
   
   /**
    * Return whether the current user has the specified access permission on this Node
    * 
    * @param permission     Permission to validate against
    * 
    * @return true if the permission is applied to the node for this user, false otherwise
    */
   public final boolean hasPermission(String permission)
   {
      Boolean valid = null;
      if (permissions != null)
      {
         valid = permissions.get(permission);
      }
      else
      {
         permissions = new HashMap<String, Boolean>(5, 1.0f);
      }
      
      if (valid == null)
      {
         PermissionService service = Repository.getServiceRegistry(FacesContext.getCurrentInstance()).getPermissionService();
         valid = Boolean.valueOf(/*service.hasPermission(getNodeRef() ...)*/true);
         permissions.put(permission, valid);
      }
      
      return valid.booleanValue();
   }

   /**
    * @return The GUID for the node
    */
   public final String getId()
   {
      return this.id;
   }

   /**
    * @return The path for the node
    */
   public final String getPath()
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
      this.properties.clear();
      this.propsRetrieved = false;
      this.aspects = null;
      this.permissions = null;
   }
   
   /**
    * Override Object.toString() to provide useful debug output
    */
   public String toString()
   {
      if (this.nodeService != null)
      {
         return "Node Type: " + getType() + 
                "\nNode Properties: " + this.getProperties().toString() + 
                "\nNode Aspects: " + this.getAspects().toString();
      }
      else
      {
         return super.toString();
      }
   }
}
