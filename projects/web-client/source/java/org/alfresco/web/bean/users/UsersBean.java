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
package org.alfresco.web.bean.users;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.transaction.UserTransaction;

import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.context.IContextListener;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.common.component.data.UIRichList;
import org.apache.log4j.Logger;

/**
 * @author Kevin Roast
 */
public class UsersBean implements IContextListener
{
   private static final String DEFAULT_OUTCOME = "manageUsers";

   private static Logger logger = Logger.getLogger(UsersBean.class);
   
   private static final String ERROR_DELETE = "error_delete_user";
   
   /** NodeService bean reference */
   private NodeService nodeService;
   
   /** SearchService bean reference */
   private SearchService searchService;
   
   /** AuthenticationService bean reference */
   private AuthenticationService authenticationService;
   
   /** Component reference for Users RichList control */
   private UIRichList usersRichList;
   
   /** action context */
   private Node person = null;


   // ------------------------------------------------------------------------------
   // Construction 
   
   /**
    * Default Constructor
    */
   public UsersBean()
   {
      UIContextService.getInstance(FacesContext.getCurrentInstance()).registerBean(this);
   }
   
   
   // ------------------------------------------------------------------------------
   // Bean property getters and setters 
   
   /**
    * @param nodeService The NodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * @param searchService the search service
    */
   public void setSearchService(SearchService searchService)
   {
      this.searchService = searchService;
   }
   
   /**
    * @param authenticationService  The AuthenticationService to set.
    */
   public void setAuthenticationService(AuthenticationService authenticationService)
   {
      this.authenticationService = authenticationService;
   }
   
   /**
    * @return Returns the usersRichList.
    */
   public UIRichList getUsersRichList()
   {
      return this.usersRichList;
   }
   
   /**
    * @param usersRichList The usersRichList to set.
    */
   public void setUsersRichList(UIRichList usersRichList)
   {
      this.usersRichList = usersRichList;
   }
   
   /**
    * @return the list of user Nodes to display
    */
   public List<Node> getUsers()
   {
      return Repository.getUsers(FacesContext.getCurrentInstance(), this.nodeService, this.searchService);
   }
   
   /**
    * @return Returns the person context.
    */
   public Node getPerson()
   {
      return this.person;
   }

   /**
    * @param person The person context to set.
    */
   public void setPerson(Node person)
   {
      this.person = person;
   }
   
   /**
    * Action event called by all actions that need to setup a Person context on the 
    * Users bean before an action page is called. The context will be a Person Node in
    * setPerson() which can be retrieved on the action page from UsersBean.getPerson().
    */
   public void setupUserAction(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         if (logger.isDebugEnabled())
            logger.debug("Setup for action, setting current Person to: " + id);
         
         try
         {
            // create the node ref, then our node representation
            NodeRef ref = new NodeRef(Repository.getStoreRef(), id);
            Node node = new Node(ref, this.nodeService);
            
            // remember the Person node
            setPerson(node);
            
            // clear the UI state in preparation for finishing the action and returning to the main page
            contextUpdated();
         }
         catch (InvalidNodeRefException refErr)
         {
            Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
                  FacesContext.getCurrentInstance(), Repository.ERROR_NODEREF), new Object[] {id}) );
         }
      }
      else
      {
         setPerson(null);
      }
   }
   
   /**
    * Action handler called when the OK button is clicked on the Delete User page 
    */
   public String deleteOK()
   {
      UserTransaction tx = null;
      
      try
      {
         FacesContext context = FacesContext.getCurrentInstance();
         tx = Repository.getUserTransaction(context);
         tx.begin();
         
         // delete the User authentication
         authenticationService.deleteAuthentication((String)getPerson().getProperties().get("userName"));
         
         // delete the associated Person
         this.nodeService.deleteNode(getPerson().getNodeRef());
         
         // commit the transaction
         tx.commit();
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), ERROR_DELETE), e.getMessage()), e);
      }
      
      return DEFAULT_OUTCOME;
   }
   
   
   // ------------------------------------------------------------------------------
   // IContextListener implementation 
   
   /**
    * @see org.alfresco.web.app.context.IContextListener#contextUpdated()
    */
   public void contextUpdated()
   {
      if (this.usersRichList != null)
      {
         this.usersRichList.setValue(null);
      }
   }
}
