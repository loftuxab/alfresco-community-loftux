/*
 * Created on 10-Jun-2005
 */
package org.alfresco.web.bean.wizard;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.RepositoryAuthenticationDao;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.bean.repository.MapNode;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.common.component.data.UIRichList;
import org.apache.log4j.Logger;

/**
 * @author Kevin Roast
 */
public class NewUserWizard extends AbstractWizardBean
{
   private static Logger logger = Logger.getLogger(NewUserWizard.class);
   
   // TODO: retrieve these from the config service
   private static final String WIZARD_TITLE_NEW = "New User Wizard";
   private static final String WIZARD_DESC_NEW = "Use this wizard to add a user to the repository.";
   private static final String WIZARD_TITLE_EDIT = "Edit User Wizard";
   private static final String WIZARD_DESC_EDIT = "Use this wizard to modify a user in the repository.";
   private static final String STEP1_TITLE = "Step One - Person Properties";
   private static final String STEP1_DESCRIPTION = "Enter information about this person.";
   private static final String STEP2_TITLE = "Step Two - User Properties";
   private static final String STEP2_DESCRIPTION = "Enter information about this user.";
   private static final String FINISH_INSTRUCTION = "To add the user to this space click Finish.<br/>" +
                                                    "To review or change your selections click Back.";

  
   private static final String ERROR_GENERIC = "A system error occured during the operation: {0}";
   
   /** form variables */
   private String firstName = null;
   private String lastName = null;
   private String userName = null;
   private String email = null;
   private String companyId = null;
   private String homeSpaceName = null;
   private String homeSpaceLocation = null;
   
   /** Component references */
   private UIRichList usersRichList;
   
   /** action context */
   private Node person = null;
   
   
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
      this.email = "";
      this.companyId = "";
      this.homeSpaceName = "";
      this.homeSpaceLocation = null;
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
      this.email = (String)props.get("email");
      this.companyId = (String)props.get("organizationId");
      // TODO: calculate home space name and parent space Id from homeFolderId
      String homeFolderId = (String)props.get("homeFolder");
      this.homeSpaceName = "";
      this.homeSpaceLocation = null;
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
            
            Map<QName, Serializable> props = new HashMap<QName, Serializable>(7, 1.0f);
            props.put(ContentModel.PROP_USERNAME, this.userName);
            props.put(ContentModel.PROP_FIRSTNAME, this.firstName);
            props.put(ContentModel.PROP_LASTNAME, this.lastName);
            // TODO: should be an ID!
            props.put(ContentModel.PROP_HOMEFOLDER, this.homeSpaceLocation + '/' + this.homeSpaceName);
            props.put(ContentModel.PROP_EMAIL, this.email);
            props.put(ContentModel.PROP_ORGID, this.companyId);
            this.nodeService.setProperties(nodeRef, props);
         }
         else
         {
            // get the node ref of the node that will contain the content
            NodeRef peopleNode = getSystemPeopleFolderRef(context);
            
            // create properties for Person type from submitted Form data
            Map<QName, Serializable> props = new HashMap<QName, Serializable>(7, 1.0f);
            props.put(ContentModel.PROP_USERNAME, this.userName);
            props.put(ContentModel.PROP_FIRSTNAME, this.firstName);
            props.put(ContentModel.PROP_LASTNAME, this.lastName);
             //TODO: should be an ID!
            // TODO: create this Space if it does not already exist
            props.put(ContentModel.PROP_HOMEFOLDER, this.homeSpaceLocation + '/' + this.homeSpaceName);
            props.put(ContentModel.PROP_EMAIL, this.email);
            props.put(ContentModel.PROP_ORGID, this.companyId);
            
            // create the node to represent the Person
            String assocName = QName.createValidLocalName(this.userName);
            this.nodeService.createNode(
                  peopleNode,
                  ContentModel.ASSOC_CHILDREN,
                  QName.createQName(NamespaceService.ALFRESCO_URI, assocName),
                  ContentModel.TYPE_PERSON,
                  props);
            
            if (logger.isDebugEnabled())
               logger.debug("Created Person node for username: " + this.userName);
            
            // TODO: apply appropriate aspects
            // TODO: hook in security API - set password etc.
         }
         
         // commit the transaction
         tx.commit();
         
         // reset the richlist component so it rebinds to the users list
         this.usersRichList.setValue(null);
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         throw new AlfrescoRuntimeException("Failed to create Person", e);
      }
      
      return outcome;
   }
   
   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      return buildSummary(
            new String[] {"Name", "User Name", "Password", "Home Space"},
            new String[] {this.firstName + " " + this.lastName, this.userName, "********", this.homeSpaceName});
   }
   
   /**
    * Init the users screen
    */
   public void setupUsers(ActionEvent event)
   {
      if (this.usersRichList != null)
      {
         this.usersRichList.setValue(null);
      }
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
            NodeRef ref = new NodeRef(Repository.getStoreRef(FacesContext.getCurrentInstance()), id);
            Node node = new Node(ref, this.nodeService);
            
            // remember the Person node
            setPerson(node);
            
            // clear the UI state in preparation for finishing the action and returning to the main page
            this.usersRichList.setValue(null);
         }
         catch (InvalidNodeRefException refErr)
         {
            Utils.addErrorMessage( MessageFormat.format(Repository.ERROR_NODEREF, new Object[] {id}) );
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
            NodeRef ref = new NodeRef(Repository.getStoreRef(FacesContext.getCurrentInstance()), id);
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
            this.usersRichList.setValue(null);
            
            if (logger.isDebugEnabled())
               logger.debug("Started wizard : " + getWizardTitle() + " for editing");
         }
         catch (InvalidNodeRefException refErr)
         {
            Utils.addErrorMessage( MessageFormat.format(Repository.ERROR_NODEREF, new Object[] {id}) );
         }
      }
      else
      {
         setPerson(null);
      }
   }
   
   /**
    * @return the list of user Nodes to display
    */
   public List<Node> getUsers()
   {
      return queryPersonNodes();
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
   
   
   // ------------------------------------------------------------------------------
   // Helper methods
   
   /**
    * Query a list of Person type nodes from the repo
    * It is currently assumed that all Person nodes exist below the Repository root node
    * 
    * @return List of Person node objects
    */
   private List<Node> queryPersonNodes()
   {
      List<Node> personNodes = null;
      
      UserTransaction tx = null;
      try
      {
         FacesContext context = FacesContext.getCurrentInstance();
         tx = Repository.getUserTransaction(context);
         tx.begin();
         
         NodeRef peopleRef = getSystemPeopleFolderRef(context);
         
         // TODO: better to perform an XPath search or a get for a specific child type here?
         List<ChildAssociationRef> childRefs = this.nodeService.getChildAssocs(peopleRef);
         personNodes = new ArrayList<Node>(childRefs.size());
         for (ChildAssociationRef ref: childRefs)
         {
            // create our Node representation from the NodeRef
            NodeRef nodeRef = ref.getChildRef();
            
            if (this.nodeService.getType(nodeRef).equals(ContentModel.TYPE_PERSON))
            {
               // create our Node representation
               MapNode node = new MapNode(nodeRef, this.nodeService);
               
               // set data binding properties
               // this will also force initialisation of the props now during the UserTransaction
               // it is much better for performance to do this now rather than during page bind
               Map<String, Object> props = node.getProperties(); 
               props.put("fullName", ((String)props.get("firstName")) + ' ' + ((String)props.get("lastName")));
               
               personNodes.add(node);
            }
         }
         
         // commit the transaction
         tx.commit();
      }
      catch (InvalidNodeRefException refErr)
      {
         Utils.addErrorMessage( MessageFormat.format(Repository.ERROR_NODEREF, new Object[] {"root"}) );
         personNodes = Collections.<Node>emptyList();
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
      }
      catch (Exception err)
      {
         Utils.addErrorMessage( MessageFormat.format(ERROR_GENERIC, err.getMessage()), err );
         personNodes = Collections.<Node>emptyList();
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
      }
      
      return personNodes;
   }
   
   /**
    * Return a reference to the special system folder containing Person instances
    * 
    * @param context
    * 
    * @return NodeRef to Person folder
    */
   private NodeRef getSystemPeopleFolderRef(FacesContext context)
   {
      if (peopleRef == null)
      {
         // get a reference to the system types folder node
         DynamicNamespacePrefixResolver resolver = new DynamicNamespacePrefixResolver(null);
         resolver.addDynamicNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
         
         List<ChildAssociationRef> results = nodeService.selectNodes(
               this.nodeService.getRootNode(Repository.getStoreRef(context)),
               RepositoryAuthenticationDao.PEOPLE_FOLDER,
               null,
               resolver,
               false);
         
         if (results.size() != 1)
         {
            throw new AlfrescoRuntimeException("Unable to find system types folder: " + RepositoryAuthenticationDao.PEOPLE_FOLDER);
         }
         
         peopleRef = results.get(0).getChildRef();
      }
      
      return peopleRef;
   }
   
   private NodeRef peopleRef = null;
}
