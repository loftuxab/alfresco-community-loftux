/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
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
   
   private NodeRef nodeRef;
   private String name;
   private QName type;
   private String path;
   private String id;
   private Set<QName> aspects = null;
   private QNameMap<String, Object> properties = new QNameMap<String, Object>();
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
      this.properties = new QNameMap<String, Object>();
      this.propsRetrieved = false;
      this.aspects = null;

      if (this.propertyNames != null)
      {
         this.propertyNames.clear();
      }
      this.propertyNames = null;
   }
}
