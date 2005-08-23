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
package org.alfresco.web.bean.wizard;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.transaction.UserTransaction;

import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.repo.security.permissions.PermissionService;
import org.alfresco.repo.security.permissions.impl.SimplePermissionEntry;
import org.alfresco.repo.security.permissions.impl.SimplePermissionReference;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.repository.User;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.apache.log4j.Logger;

/**
 * @author Kevin Roast
 */
public class NewUserWizard extends AbstractWizardBean
{
   private static Logger logger = Logger.getLogger(NewUserWizard.class);
   
   // TODO: retrieve these from the config service
   private static final String WIZARD_TITLE_NEW = "New User Wizard";
   private static final String WIZARD_DESC_NEW = "This wizard helps you to add a user to the repository.";
   private static final String WIZARD_TITLE_EDIT = "Edit User Wizard";
   private static final String WIZARD_DESC_EDIT = "This wizard helps you modify a user in the repository.";
   private static final String STEP1_TITLE = "Step One - Person Properties";
   private static final String STEP1_DESCRIPTION = "Enter information about this person.";
   private static final String STEP2_TITLE = "Step Two - User Properties";
   private static final String STEP2_DESCRIPTION = "Enter information about this user.";
   private static final String FINISH_INSTRUCTION = "To add the user to this space click Finish.<br/>" +
                                                    "To review or change your selections click Back.";
   
   private static final String ERROR = "error_person";
   
   /** form variables */
   private String firstName = null;
   private String lastName = null;
   private String userName = null;
   private String password = null;
   private String email = null;
   private String companyId = null;
   private String homeSpaceName = null;
   private String homeSpaceLocation = null;
   
   /** AuthenticationService bean reference */
   private AuthenticationService authenticationService;
   
   /** NamespaceService bean reference */
   private NamespaceService namespaceService;
   
   /** PermissionService bean reference */
   private PermissionService permissionService;
   
   /** action context */
   private Node person = null;
   
   /** ref to system people folder */
   private NodeRef peopleRef = null;
   
   /** ref to the company home space folder */
   private NodeRef companyHomeSpaceRef = null;
   
   
   /**
    * @param authenticationService  The AuthenticationService to set.
    */
   public void setAuthenticationService(AuthenticationService authenticationService)
   {
      this.authenticationService = authenticationService;
   }
   
   /**
    * @param namespaceService The namespaceService to set.
    */
   public void setNamespaceService(NamespaceService namespaceService)
   {
      this.namespaceService = namespaceService;
   }
   
   /**
    * @param permissionService  The PermissionService to set.
    */
   public void setPermissionService(PermissionService permissionService)
   {
      this.permissionService = permissionService;
   }
   
   /**
    * Initialises the wizard
    */
   public void init()
   {
      super.init();
      
      // reset all variables
      this.firstName = "";
      this.lastName = "";
      this.userName = "";
      this.password = "";
      this.email = "";
      this.companyId = "";
      this.homeSpaceName = "";
      this.homeSpaceLocation = getCompanyHomeSpace().getId();
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#populate()
    */
   public void populate()
   {
      // set values for edit mode
      Map<String, Object> props = getPerson().getProperties();
      
      this.firstName = (String)props.get("firstName");
      this.lastName = (String)props.get("lastName");
      this.userName = (String)props.get("userName");
      this.password = ""; 
      this.email = (String)props.get("email");
      this.companyId = (String)props.get("organizationId");
      
      // calculate home space name and parent space Id from homeFolderId
      this.homeSpaceLocation = null;        // default to Company root space
      this.homeSpaceName = "";              // default to none set below root
      String homeFolderId = (String)props.get("homeFolder");
      NodeRef homeFolderRef = new NodeRef(Repository.getStoreRef(), homeFolderId);
      if (this.nodeService.exists(homeFolderRef) == true)
      {
         ChildAssociationRef childAssocRef = this.nodeService.getPrimaryParent(homeFolderRef);
         NodeRef parentRef = childAssocRef.getParentRef();
         if (this.nodeService.getRootNode(Repository.getStoreRef()).equals(parentRef) == false)
         {
            this.homeSpaceLocation = parentRef.getId();
            this.homeSpaceName = Repository.getNameForNode(nodeService, homeFolderRef);
         }
         else
         {
            this.homeSpaceLocation = homeFolderRef.getId();
         }
      }
   }
   
   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getWizardDescription()
    */
   public String getWizardDescription()
   {
      if (this.editMode)
      {
         return WIZARD_DESC_EDIT;
      }
      else
      {
         return WIZARD_DESC_NEW;
      }
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getWizardTitle()
    */
   public String getWizardTitle()
   {
      if (this.editMode)
      {
         return WIZARD_TITLE_EDIT;
      }
      else
      {
         return WIZARD_TITLE_NEW;
      }
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getStepTitle()
    */
   public String getStepTitle()
   {
      String stepTitle = null;
      
      switch (this.currentStep)
      {
         case 1:
         {
            stepTitle = STEP1_TITLE;
            break;
         }
         case 2:
         {
            stepTitle = STEP2_TITLE;
            break;
         }
         case 3:
         {
            stepTitle = SUMMARY_TITLE;
            break;
         }
         default:
         {
            stepTitle = "";
         }
      }
      
      return stepTitle;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getStepDescription()
    */
   public String getStepDescription()
   {
      String stepDesc = null;
      
      switch (this.currentStep)
      {
         case 1:
         {
            stepDesc = STEP1_DESCRIPTION;
            break;
         }
         case 2:
         {
            stepDesc = STEP2_DESCRIPTION;
            break;
         }
         case 3:
         {
            stepDesc = SUMMARY_DESCRIPTION;
            break;
         }
         default:
         {
            stepDesc = "";
         }
      }
      
      return stepDesc;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getStepInstructions()
    */
   public String getStepInstructions()
   {
      String stepInstruction = null;
      
      switch (this.currentStep)
      {
         case 3:
         {
            stepInstruction = FINISH_INSTRUCTION;
            break;
         }
         default:
         {
            stepInstruction = DEFAULT_INSTRUCTION;
         }
      }
      
      return stepInstruction;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#determineOutcomeForStep(int)
    */
   protected String determineOutcomeForStep(int step)
   {
      String outcome = null;
      
      switch(step)
      {
         case 1:
         {
            outcome = "person-properties";
            break;
         }
         case 2:
         {
            outcome = "user-properties";
            break;
         }
         case 3:
         {
            outcome = "summary";
            break;
         }
         default:
         {
            outcome = CANCEL_OUTCOME;
         }
      }
      
      return outcome;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#finish()
    */
   public String finish()
   {
      String outcome = FINISH_OUTCOME;
      
      // TODO: implement create new Person object from specified details
      UserTransaction tx = null;
      
      try
      {
         FacesContext context = FacesContext.getCurrentInstance();
         tx = Repository.getUserTransaction(context);
         tx.begin();
         
         if (this.editMode)
         {
            // update the existing node in the repository
            NodeRef nodeRef = getPerson().getNodeRef();
            
            Map<QName, Serializable> props = this.nodeService.getProperties(nodeRef);
            props.put(ContentModel.PROP_USERNAME, this.userName);
            props.put(ContentModel.PROP_FIRSTNAME, this.firstName);
            props.put(ContentModel.PROP_LASTNAME, this.lastName);
            String homeSpaceId;
            if (this.homeSpaceLocation != null)
            {
               homeSpaceId = createHomeSpace(this.homeSpaceLocation, this.homeSpaceName, false);
            }
            else
            {
               homeSpaceId = getCompanyHomeSpace().getId();
            }
            props.put(ContentModel.PROP_HOMEFOLDER, homeSpaceId);
            props.put(ContentModel.PROP_EMAIL, this.email);
            props.put(ContentModel.PROP_ORGID, this.companyId);
            this.nodeService.setProperties(nodeRef, props);
            
            // TODO: allow change password - separate screen for this?
         }
         else
         {
            // get the node ref of the node that will contain the content
            NodeRef peopleNode = Repository.getSystemPeopleFolderRef(context, this.nodeService, this.searchService);
            
            // create properties for Person type from submitted Form data
            Map<QName, Serializable> props = new HashMap<QName, Serializable>(7, 1.0f);
            props.put(ContentModel.PROP_USERNAME, this.userName);
            props.put(ContentModel.PROP_FIRSTNAME, this.firstName);
            props.put(ContentModel.PROP_LASTNAME, this.lastName);
            String homeSpaceId;
            if (this.homeSpaceLocation != null)
            {
               homeSpaceId = createHomeSpace(this.homeSpaceLocation, this.homeSpaceName, true);
            }
            else
            {
               homeSpaceId = getCompanyHomeSpace().getId();
            }
            props.put(ContentModel.PROP_HOMEFOLDER, homeSpaceId);
            props.put(ContentModel.PROP_EMAIL, this.email);
            props.put(ContentModel.PROP_ORGID, this.companyId);
            
            // create the node to represent the Person
            String assocName = QName.createValidLocalName(this.userName);
            this.nodeService.createNode(
                  peopleNode,
                  ContentModel.ASSOC_CHILDREN,
                  ContentModel.TYPE_PERSON,
                  ContentModel.TYPE_PERSON,
                  props);
            
            if (logger.isDebugEnabled())
               logger.debug("Created Person node for username: " + this.userName);
            
            // create the ACEGI Authentication instance for this user
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(this.userName, this.password);
            this.authenticationService.createAuthentication(Repository.getStoreRef(), token);
            
            if (logger.isDebugEnabled())
               logger.debug("Created User Authentication instance for username: " + this.userName);
         }
         
         // commit the transaction
         tx.commit();
         
         // reset the richlist component so it rebinds to the users list
         invalidateUserList();
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), ERROR), e.getMessage()), e);
         outcome = null;
      }
      
      return outcome;
   }
   
   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      String homeSpaceLabel = this.homeSpaceName;
      if ((this.homeSpaceName == null || this.homeSpaceName.length() == 0) && this.homeSpaceLocation != null)
      {
         homeSpaceLabel = Repository.getNameForNode(this.nodeService, new NodeRef(Repository.getStoreRef(), this.homeSpaceLocation));
      }
      
      return buildSummary(
            new String[] {"Name", "User Name", "Password", "Home Space"},
            new String[] {this.firstName + " " + this.lastName, this.userName, "********", homeSpaceLabel});
   }
   
   /**
    * Init the users screen
    */
   public void setupUsers(ActionEvent event)
   {
      invalidateUserList();
   }
   
   /**
    * Action event called by all actions that need to setup a Person context on the 
    * NewUserWizard bean before an action page is called. The context will be a Person Node in
    * setPerson() which can be retrieved on the action page from NewUserWizard.getPerson().
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
            invalidateUserList();
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
    * Action listener called when the wizard is being launched for 
    * editing an existing node.
    */
   public void startWizardForEdit(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
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
            
            // set the wizard in edit mode
            this.editMode = true;
            
            // populate the wizard's default values with the current value
            // from the node being edited
            init();
            populate();
            
            // clear the UI state in preparation for finishing the action and returning to the main page
            invalidateUserList();
            
            if (logger.isDebugEnabled())
               logger.debug("Started wizard : " + getWizardTitle() + " for editing");
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
    * @return Returns the companyId.
    */
   public String getCompanyId()
   {
      return this.companyId;
   }
   
   /**
    * @param companyId The companyId to set.
    */
   public void setCompanyId(String companyId)
   {
      this.companyId = companyId;
   }
   
   /**
    * @return Returns the email.
    */
   public String getEmail()
   {
      return this.email;
   }
   
   /**
    * @param email The email to set.
    */
   public void setEmail(String email)
   {
      this.email = email;
   }
   
   /**
    * @return Returns the firstName.
    */
   public String getFirstName()
   {
      return this.firstName;
   }
   
   /**
    * @param firstName The firstName to set.
    */
   public void setFirstName(String firstName)
   {
      this.firstName = firstName;
   }
   
   /**
    * @return Returns the homeSpaceLocation.
    */
   public String getHomeSpaceLocation()
   {
      return this.homeSpaceLocation;
   }
   
   /**
    * @param homeSpaceLocation The homeSpaceLocation to set.
    */
   public void setHomeSpaceLocation(String homeSpaceLocation)
   {
      this.homeSpaceLocation = homeSpaceLocation;
   }
   
   /**
    * @return Returns the homeSpaceName.
    */
   public String getHomeSpaceName()
   {
      return this.homeSpaceName;
   }
   
   /**
    * @param homeSpaceName The homeSpaceName to set.
    */
   public void setHomeSpaceName(String homeSpaceName)
   {
      this.homeSpaceName = homeSpaceName;
   }
   
   /**
    * @return Returns the lastName.
    */
   public String getLastName()
   {
      return this.lastName;
   }
   
   /**
    * @param lastName The lastName to set.
    */
   public void setLastName(String lastName)
   {
      this.lastName = lastName;
   }
   
   /**
    * @return Returns the userName.
    */
   public String getUserName()
   {
      return this.userName;
   }
   
   /**
    * @param userName The userName to set.
    */
   public void setUserName(String userName)
   {
      this.userName = userName;
   }
   
   /**
    * @return Returns the password.
    */
   public String getPassword()
   {
      return this.password;
   }
   
   /**
    * @param password The password to set.
    */
   public void setPassword(String password)
   {
      this.password = password;
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
   
   public boolean getEditMode()
   {
      return this.editMode;
   }
   
   
   // ------------------------------------------------------------------------------
   // Validator methods 
   
   /**
    * Validate password field data is acceptable 
    */
   public void validatePassword(FacesContext context, UIComponent component, Object value)
      throws ValidatorException
   {
      String pass = (String)value;
      if (pass.length() < 5 || pass.length() > 12)
      {
         String err = "Password must be between 5 and 12 characters in length.";
         throw new ValidatorException(new FacesMessage(err));
      }
      
      for (int i=0; i<pass.length(); i++)
      {
         if (Character.isLetterOrDigit( pass.charAt(i) ) == false)
         {
            String err = "Password can only contain characters or digits.";
            throw new ValidatorException(new FacesMessage(err));
         }
      }
   }
   
   /**
    * Validate Username field data is acceptable 
    */
   public void validateUsername(FacesContext context, UIComponent component, Object value)
      throws ValidatorException
   {
      String pass = (String)value;
      if (pass.length() < 5 || pass.length() > 12)
      {
         String err = "Username must be between 5 and 12 characters in length.";
         throw new ValidatorException(new FacesMessage(err));
      }
      
      for (int i=0; i<pass.length(); i++)
      {
         if (Character.isLetterOrDigit( pass.charAt(i) ) == false)
         {
            String err = "Username can only contain characters or digits.";
            throw new ValidatorException(new FacesMessage(err));
         }
      }
   }
   
   
   // ------------------------------------------------------------------------------
   // Helper methods
   
   /**
    * Helper to return the company home space
    * 
    * @return company home space NodeRef
    */
   private NodeRef getCompanyHomeSpace()
   {
      if (this.companyHomeSpaceRef == null)
      {
         String companySpaceName = Application.getRootPath(FacesContext.getCurrentInstance());
         String companyXPath = NamespaceService.APP_MODEL_PREFIX + ":" + QName.createValidLocalName(companySpaceName);
         
         NodeRef rootNodeRef = this.nodeService.getRootNode(Repository.getStoreRef());
         List<NodeRef> nodes = this.searchService.selectNodes(
               rootNodeRef,
               companyXPath, null, this.namespaceService, false);
         
         if (nodes.size() == 0)
         {
            throw new IllegalStateException("Unable to find company home space path: " + companySpaceName);
         }
         
         this.companyHomeSpaceRef = nodes.get(0);
      }
      
      return this.companyHomeSpaceRef;
   }
   
   /**
    * Create the specified home space if it does not exist, and return the ID
    * 
    * @param locationId    Parent location
    * @param spaceName     Home space to create, can be null to simply return the parent
    * @param error         True to throw an error if the space already exists, else ignore and return
    * 
    * @return ID of the home space
    */
   private String createHomeSpace(String locationId, String spaceName, boolean error)
   {
      String homeSpaceId = locationId;
      if (spaceName != null && spaceName.length() != 0)
      {
         StoreRef storeRef = Repository.getStoreRef();
         
         NodeRef parentRef = new NodeRef(Repository.getStoreRef(), locationId);
         
         // check for existance of home space with same name - return immediately
         // if it exists or throw an exception an give user chance to enter another name
         // TODO: this might be better replaced with an XPath query!
         List<ChildAssociationRef> children = this.nodeService.getChildAssocs(parentRef);
         for (ChildAssociationRef ref : children)
         {
            String childNodeName = (String)this.nodeService.getProperty(ref.getChildRef(), ContentModel.PROP_NAME);
            if (spaceName.equals(childNodeName))
            {
               if (error)
               {
                  throw new AlfrescoRuntimeException("A Home Space with the same name already exists.");
               }
               else
               {
                  return ref.getChildRef().getId();
               }
            }
         }
         
         // space does not exist already, create a new Space under it with the specified name
         String qname = QName.createValidLocalName(spaceName);
         ChildAssociationRef assocRef = this.nodeService.createNode(
               parentRef,
               ContentModel.ASSOC_CONTAINS,
               QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, qname),
               ContentModel.TYPE_FOLDER);
         
         NodeRef nodeRef = assocRef.getChildRef();
         
         // set the name property on the node
         this.nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, spaceName);
         
         if (logger.isDebugEnabled())
            logger.debug("Created Home Space for with name: " + spaceName);
         
         // apply the uifacets aspect - icon, title and description props
         Map<QName, Serializable> uiFacetsProps = new HashMap<QName, Serializable>(3);
         uiFacetsProps.put(ContentModel.PROP_ICON, NewSpaceWizard.SPACE_ICON_DEFAULT);
         uiFacetsProps.put(ContentModel.PROP_TITLE, spaceName);
         this.nodeService.addAspect(nodeRef, ContentModel.ASPECT_UIFACETS, uiFacetsProps);
         
         // apply the default permissions model
         // first set no inheritance so we can setup new permissions
         this.permissionService.setInheritParentPermissions(nodeRef, false);
         // then we set maximium permissions to the owner of the space
         // so by default other users will NOT have access to the space
         this.permissionService.setPermission(nodeRef, this.userName, SimplePermissionEntry.ALL_PERMISSIONS, true);
         this.permissionService.setPermission(nodeRef, SimplePermissionEntry.OWNER_AUTHORITY, SimplePermissionEntry.ALL_PERMISSIONS, true);
         
         // return the ID of the created space
         homeSpaceId = nodeRef.getId();
      }
      
      return homeSpaceId;
   }
   
   private void invalidateUserList()
   {
      UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
   }
}
