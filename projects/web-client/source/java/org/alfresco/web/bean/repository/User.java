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

import java.util.List;

import javax.faces.context.FacesContext;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.configuration.ConfigurableService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;

/**
 * Bean that represents the currently logged in user
 * 
 * @author gavinc
 */
public final class User
{
   private String homeSpaceId;
   private String userName;
   private String ticket;
   private NodeRef user;
   private NodeRef person;
   
   /** cached ref to our user preferences node */
   private NodeRef preferencesFolderRef = null;
   
   /**
    * Constructor
    * 
    * @param userName constructor for the user
    */
   public User(NodeRef user, String userName, String ticket, NodeRef person)
   {
      if (user == null || userName == null || ticket == null || person == null)
      {
         throw new IllegalArgumentException("All user details are mandatory!");
      }
      
      this.user = user;
      this.userName = userName;  
      this.ticket = ticket;
      this.person = person;
   }
   
   /**
    * @return The user name
    */
   public String getUserName()
   {
      return this.userName;
   }
   
   /**
    * @return Retrieves the user's home space (this may be the id of the company home space)
    */
   public String getHomeSpaceId()
   {
      return this.homeSpaceId;
   }

   /**
    * @param homeSpaceId Sets the id of the users home space
    */
   public void setHomeSpaceId(String homeSpaceId)
   {
      this.homeSpaceId = homeSpaceId;
   }

   /**
    * @return Returns the ticket.
    */
   public String getTicket()
   {
      return this.ticket;
   }

   /**
    * @return The NodeRef representing this User
    */
   public NodeRef getUserNodeRef()
   {
      return this.user;
   }
   
   /**
    * @return Returns the person.
    */
   public NodeRef getPerson()
   {
      return this.person;
   }
   
   /**
    * Get or create the node used to store user preferences.
    * Utilises the 'configurable' aspect on the Person linked to this user.
    */
   public synchronized NodeRef getUserPreferencesRef()
   {
      if (this.preferencesFolderRef == null)
      {
         FacesContext fc = FacesContext.getCurrentInstance();
         ServiceRegistry registry = Repository.getServiceRegistry(fc);
         NodeService nodeService = registry.getNodeService();
         NamespaceService namespaceService = registry.getNamespaceService();
         ConfigurableService configurableService = registry.getConfigurableService();
         
         NodeRef person = Application.getCurrentUser(fc).getPerson();
         if (nodeService.hasAspect(person, ContentModel.ASPECT_CONFIGURABLE) == false)
         {
            // create the configuration folder for this Person node
        	 configurableService.makeConfigurable(person);
         }
         
         // target of the assoc is the configurations folder ref
         NodeRef configRef = configurableService.getConfigurationFolder(person);
         if (configRef == null)
         {
            throw new IllegalStateException("Unable to find associated 'configurations' folder for node: " + person);
         }
         
         String xpath = NamespaceService.ALFRESCO_PREFIX + ":" + "preferences";
         List<NodeRef> nodes = nodeService.selectNodes(
               configRef,
               xpath,
               null,
               namespaceService,
               false);
         
         NodeRef prefRef;
         if (nodes.size() == 1)
         {
            prefRef = nodes.get(0);
         }
         else
         {
            // create the preferences Node for this user
            ChildAssociationRef childRef = nodeService.createNode(
                  configRef,
                  ContentModel.ASSOC_CONTAINS,
                  QName.createQName(NamespaceService.ALFRESCO_URI, "preferences"),
                  ContentModel.TYPE_CMOBJECT);
            
            prefRef = childRef.getChildRef();
         }
         
         this.preferencesFolderRef = prefRef;
      }
      
      return this.preferencesFolderRef;
   }
}
