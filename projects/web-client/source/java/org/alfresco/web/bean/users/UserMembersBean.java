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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.ContextListener;
import org.alfresco.web.app.context.IContextListener;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.bean.NavigationBean;
import org.alfresco.web.bean.repository.MapNode;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.repository.User;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.common.component.UIGenericPicker;
import org.alfresco.web.ui.common.component.data.UIRichList;

/**
 * @author Kevin Roast
 */
public class UserMembersBean implements IContextListener
{
   private static final String ERROR_DELETE = "error_remove_user";

   private static final String OUTCOME_FINISH = "finish";

   /** NodeService bean reference */
   private NodeService nodeService;

   /** SearchService bean reference */
   private SearchService searchService;
   
   /** PermissionService bean reference */
   private PermissionService permissionService;
   
   /** PersonService bean reference */
   private PersonService personService;
   
   /** NavigationBean bean refernce */
   private NavigationBean navigator;
   
   /** Component reference for Users RichList control */
   private UIRichList usersRichList;
   
   /** action context */
   private Node person = null;
   
   /** action context */
   private String personName = null;
   
   /** datamodel for table of roles for current person */
   private DataModel personRolesDataModel = null;
   
   /** roles for current person */
   private List<PermissionWrapper> personRoles = null;
   
   
   // ------------------------------------------------------------------------------
   // Construction

   /**
    * Default Constructor
    */
   public UserMembersBean()
   {
      UIContextService.getInstance(FacesContext.getCurrentInstance()).registerBean(this);
   }
   
   
   // ------------------------------------------------------------------------------
   // Bean property getters and setters

   /**
    * @param nodeService        The NodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }

   /**
    * @param searchService      The search service
    */
   public void setSearchService(SearchService searchService)
   {
      this.searchService = searchService;
   }
   
   /**
    * @param permissionService  The PermissionService to set.
    */
   public void setPermissionService(PermissionService permissionService)
   {
      this.permissionService = permissionService;
   }
   
   /**
    * @param personService             The personService to set.
    */
   public void setPersonService(PersonService personService)
   {
      this.personService = personService;
   }
   
   /**
    * @param navigator           The NavigationBean to set.
    */
   public void setNavigator(NavigationBean navigator)
   {
      this.navigator = navigator;
   }
   
   /**
    * @return Returns the usersRichList.
    */
   public UIRichList getUsersRichList()
   {
      return this.usersRichList;
   }

   /**
    * @param usersRichList  The usersRichList to set.
    */
   public void setUsersRichList(UIRichList usersRichList)
   {
      this.usersRichList = usersRichList;
   }
   
   /**
    * Returns the properties for current Person roles JSF DataModel
    * 
    * @return JSF DataModel representing the current Person roles
    */
   public DataModel getPersonRolesDataModel()
   {
      if (this.personRolesDataModel == null)
      {
         this.personRolesDataModel = new ListDataModel();
      }
      
      this.personRolesDataModel.setWrappedData(this.personRoles);
      
      return this.personRolesDataModel;
   }
   
   /**
    * @return Returns the person context.
    */
   public Node getPerson()
   {
      return this.person;
   }

   /**
    * @param person     The person context to set.
    */
   public void setPerson(Node person)
   {
      this.person = person;
   }
   
   /**
    * @return Returns the personName.
    */
   public String getPersonName()
   {
      return this.personName;
   }

   /**
    * @param personName The personName to set.
    */
   public void setPersonName(String personName)
   {
      this.personName = personName;
   }
   
   /**
    * @return the list of user nodes for list data binding
    */
   public List<Node> getUsers()
   {
      FacesContext context = FacesContext.getCurrentInstance();
      
      List<Node> personNodes = null;
      
      UserTransaction tx = null;
      try
      {
         tx = Repository.getUserTransaction(context);
         tx.begin();
         
         // Return all the permissions set against the current node
         // for any authentication instance (user).
         // Then combine them into a single list for each authentication found. 
         User user = Application.getCurrentUser(context);
         Map<String, List<String>> permissionMap = new HashMap<String, List<String>>(13, 1.0f);
         Set<AccessPermission> permissions = permissionService.getAllSetPermissions(navigator.getCurrentNode().getNodeRef());
         for (AccessPermission permission : permissions)
         {
            // we are only interested in Allow and not groups/owner etc.
            if (permission.getAccessStatus() == AccessStatus.ALLOWED &&
                permission.getAuthorityType() == AuthorityType.USER)
            {
               String authority = permission.getAuthority();
               
               List<String> userPermissions = permissionMap.get(authority);
               if (userPermissions == null)
               {
                  // create for first time
                  userPermissions = new ArrayList<String>(4);
                  permissionMap.put(authority, userPermissions);
               }
               // add the display label for the permission name
               userPermissions.add(Application.getMessage(context, permission.getPermission()));
            }
         }
         
         // filter invalid users e.g. Admin and current user
         permissionMap.remove(ContextListener.ADMIN);
         permissionMap.remove(Application.getCurrentUser(context).getUserName());
         
         // for each authentication (username key) found we get the Person
         // node represented by it and use that for our list databinding object
         personNodes = new ArrayList<Node>(permissionMap.size());
         for (String username : permissionMap.keySet())
         {
            NodeRef nodeRef = personService.getPerson(username);
            if (nodeRef != null)
            {
               // create our Node representation
               MapNode node = new MapNode(nodeRef, nodeService);
               
               // set data binding properties
               // this will also force initialisation of the props now during the UserTransaction
               // it is much better for performance to do this now rather than during page bind
               Map<String, Object> props = node.getProperties(); 
               props.put("fullName", ((String)props.get("firstName")) + ' ' + ((String)props.get("lastName")));
               
               String userName = (String)props.get("userName");
               props.put("roles", listToString(permissionMap.get(userName)));
               
               personNodes.add(node);
            }
         }
         
         // commit the transaction
         tx.commit();
      }
      catch (InvalidNodeRefException refErr)
      {
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               context, Repository.ERROR_NODEREF), new Object[] {"root"}) );
         personNodes = Collections.<Node>emptyList();
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
      }
      catch (Exception err)
      {
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               context, Repository.ERROR_GENERIC), err.getMessage()), err );
         personNodes = Collections.<Node>emptyList();
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
      }
      
      return personNodes;
   }
   
   private static String listToString(List<String> list)
   {
      StringBuilder buf = new StringBuilder();
      
      if (list != null)
      {
         for (int i=0; i<list.size(); i++)
         {
            if (buf.length() != 0)
            {
               buf.append(", ");
            }
            buf.append(list.get(i));
         }
      }
      
      return buf.toString();
   }
   
   /**
    * Action event called by all actions that need to setup a Person context on
    * the UserMembers bean before an action page is called. The context will be a
    * Person Node in setPerson() which can be retrieved on the action page from
    * UserMembers.getPerson().
    */
   public void setupUserAction(ActionEvent event)
   {
      FacesContext context = FacesContext.getCurrentInstance();
      
      UIActionLink link = (UIActionLink) event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         try
         {
            // create the node ref, then our node representation
            NodeRef ref = new NodeRef(Repository.getStoreRef(), id);
            Node node = new Node(ref, this.nodeService);
            
            // remember the Person node
            setPerson(node);
            
            // get username authentication key
            String userName = (String)node.getProperties().get(ContentModel.PROP_USERNAME);
            
            // setup convience function for current user full name
            setPersonName((String)node.getProperties().get(ContentModel.PROP_FIRSTNAME) + ' ' +
                  (String)node.getProperties().get(ContentModel.PROP_LASTNAME));
            
            // setup roles for this person
            List<PermissionWrapper> userPermissions = new ArrayList<PermissionWrapper>(4);
            Set<AccessPermission> permissions = permissionService.getAllSetPermissions(navigator.getCurrentNode().getNodeRef());
            for (AccessPermission permission : permissions)
            {
               // we are only interested in Allow and not groups/owner etc.
               if (permission.getAccessStatus() == AccessStatus.ALLOWED &&
                   permission.getAuthorityType() == AuthorityType.USER)
               {
                  if (userName.equals(permission.getAuthority()))
                  {
                     // found a permission for this user authentiaction
                     PermissionWrapper wrapper = new PermissionWrapper(
                           permission.getPermission(),
                           Application.getMessage(context, permission.getPermission()));
                     userPermissions.add(wrapper);
                  }
               }
            }
            this.personRoles = userPermissions;
            
            // clear the UI state in preparation for finishing the action
            // and returning to the main page
            contextUpdated();
         }
         catch (InvalidNodeRefException refErr)
         {
            Utils.addErrorMessage(MessageFormat.format(Application.getMessage(FacesContext
                  .getCurrentInstance(), Repository.ERROR_NODEREF), new Object[] { id }));
         }
      }
      else
      {
         setPerson(null);
      }
   }
   
   /**
    * Action handler called when the Add Role button is pressed to process the current selection
    */
   public void addRole(ActionEvent event)
   {
      UISelectOne rolePicker = (UISelectOne)event.getComponent().findComponent("roles");
      
      String role = (String)rolePicker.getValue();
      if (role != null)
      {
         FacesContext context = FacesContext.getCurrentInstance();
         PermissionWrapper wrapper = new PermissionWrapper(role, Application.getMessage(context, role));
         this.personRoles.add(wrapper);
      }
   }
   
   /**
    * Action handler called when the Remove button is pressed to remove a role from current user
    */
   public void removeRole(ActionEvent event)
   {
      PermissionWrapper wrapper = (PermissionWrapper)this.personRolesDataModel.getRowData();
      if (wrapper != null)
      {
         this.personRoles.remove(wrapper);
      }
   }
   
   /**
    * Action handler called when the Finish button is clicked on the Edit User Roles page
    */
   public String finishOK()
   {
      String outcome = OUTCOME_FINISH;
      
      FacesContext context = FacesContext.getCurrentInstance();
      
      // persist new user permissions
      if (this.personRoles != null && getPerson() != null)
      {
         UserTransaction tx = null;
         try
         {
            tx = Repository.getUserTransaction(context);
            tx.begin();
            
            // clear the currently set permissions for this user
            // and add each of the new permissions in turn
            NodeRef nodeRef = navigator.getCurrentNode().getNodeRef();
            String username = (String)getPerson().getProperties().get(ContentModel.PROP_USERNAME);
            this.permissionService.clearPermission(nodeRef, username);
            for (PermissionWrapper wrapper : personRoles)
            {
               this.permissionService.setPermission(
                     nodeRef,
                     username,
                     wrapper.getPermission(),
                     true);
            }
            
            tx.commit();
         }
         catch (Exception err)
         {
            Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
                  context, Repository.ERROR_GENERIC), err.getMessage()), err );
            try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
            
            outcome = null;
         }
      }
      
      return outcome;
   }
   
   /**
    * Action handler called when the OK button is clicked on the Remove User page
    */
   public String removeOK()
   {
      UserTransaction tx = null;

      try
      {
         FacesContext context = FacesContext.getCurrentInstance();
         tx = Repository.getUserTransaction(context);
         tx.begin();
         
         // remove the invited User
         if (getPerson() != null)
         {
            // clear permissions for the specified user
            String username = (String)getPerson().getProperties().get(ContentModel.PROP_USERNAME);
            this.permissionService.clearPermission(this.navigator.getCurrentNode().getNodeRef(), username);
         }
         
         // commit the transaction
         tx.commit();
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(FacesContext
               .getCurrentInstance(), ERROR_DELETE), e.getMessage()), e);
      }
      
      return OUTCOME_FINISH;
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
   
   
   /**
    * Wrapper class for list data model to display current roles for user
    */
   public static class PermissionWrapper
   {
      public PermissionWrapper(String permission, String label)
      {
         this.permission = permission;
         this.label = label;
      }
      
      public String getRole()
      {
         return this.label;
      }
      
      public String getPermission()
      {
         return this.permission;
      }
      
      private String label;
      private String permission;
   }
}
