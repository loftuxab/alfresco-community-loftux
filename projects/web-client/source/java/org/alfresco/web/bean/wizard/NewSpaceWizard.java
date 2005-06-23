package org.alfresco.web.bean.wizard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.apache.log4j.Logger;

/**
 * Handler class used by the New Space Wizard 
 * 
 * @author gavinc
 */
public class NewSpaceWizard extends AbstractWizardBean
{
   private static Logger logger = Logger.getLogger(NewSpaceWizard.class);
   
   // TODO: retrieve these from the config service
   private static final String WIZARD_TITLE = "New Space Wizard";
   private static final String WIZARD_DESC = "This wizard helps you to create a new space.";
   private static final String STEP1_TITLE = "Step One - Starting Space";
   private static final String STEP1_DESCRIPTION = "Choose how you want to create your space.";
   private static final String STEP2_TITLE = "Step Two - Space Options";
   private static final String STEP2_DESCRIPTION = "Select space options.";
   private static final String STEP3_TITLE = "Step Three - Space Details";
   private static final String STEP3_DESCRIPTION = "Enter information about the space.";
   private static final String FINISH_INSTRUCTION = "To close this wizard and create your space click Finish.";
   
   // new space wizard specific properties
   private SearchService searchService;
   private CopyService nodeOperationsService;
   private String createFrom;
   private String spaceType;
   private String existingSpaceId;
   private String templateSpaceId;
   private String copyPolicy;
   private String name;
   private String description;
   private String icon;
   private String templateName;
   private boolean saveAsTemplate;
   private List<SelectItem> templates;
   
   /**
    * Deals with the finish button being pressed
    * 
    * @return outcome
    */
   public String finish()
   {
      String outcome = FINISH_OUTCOME;
      
      UserTransaction tx = null;
   
      try
      {
         FacesContext context = FacesContext.getCurrentInstance();
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         if (this.editMode)
         {
            // update the existing node in the repository
            Node currentSpace = this.browseBean.getActionSpace();
            NodeRef nodeRef = currentSpace.getNodeRef();
            Date now = new Date( Calendar.getInstance().getTimeInMillis() );
            
            // update the modified timestamp
            this.nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, this.name);
            this.nodeService.setProperty(nodeRef, ContentModel.PROP_ICON, this.icon);
            this.nodeService.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, this.description);
         }
         else
         {
            String newSpaceId = null;
            
            if (this.createFrom.equals("scratch"))
            {
               // create the space (just create a folder for now)
               NodeRef parentNodeRef;
               String nodeId = getNavigator().getCurrentNodeId();
               if (nodeId == null)
               {
                  parentNodeRef = this.nodeService.getRootNode(Repository.getStoreRef());
               }
               else
               {
                  parentNodeRef = new NodeRef(Repository.getStoreRef(), nodeId);
               }
               
               String qname = QName.createValidLocalName(this.name);
               ChildAssociationRef assocRef = this.nodeService.createNode(
                     parentNodeRef,
                     ContentModel.ASSOC_CONTAINS,
                     QName.createQName(NamespaceService.ALFRESCO_URI, qname),
                     ContentModel.TYPE_FOLDER);
               
               NodeRef nodeRef = assocRef.getChildRef();
               newSpaceId = nodeRef.getId();
               
               // set the name property on the node
               this.nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, this.name);
               
               if (logger.isDebugEnabled())
                  logger.debug("Created folder node with name: " + this.name);

               // apply the uifacets aspect - icon, title and description props
               Map<QName, Serializable> uiFacetsProps = new HashMap<QName, Serializable>(5);
               uiFacetsProps.put(ContentModel.PROP_ICON, this.icon);
               uiFacetsProps.put(ContentModel.PROP_TITLE, this.name);
               uiFacetsProps.put(ContentModel.PROP_DESCRIPTION, this.description);
               this.nodeService.addAspect(nodeRef, ContentModel.ASPECT_UIFACETS, uiFacetsProps);
               
               if (logger.isDebugEnabled())
                  logger.debug("Added uifacets aspect with properties: " + uiFacetsProps);
            }
            else if (this.createFrom.equals("existing"))
            {
               // copy the selected space and update the name, description and icon
               NodeRef sourceNode = new NodeRef(Repository.getStoreRef(), this.existingSpaceId);
               NodeRef parentSpace = new NodeRef(Repository.getStoreRef(), getNavigator().getCurrentNodeId());
               NodeRef copiedNode = this.nodeOperationsService.copy(sourceNode, parentSpace, 
                     ContentModel.ASSOC_CONTAINS,
                     QName.createQName(NamespaceService.ALFRESCO_URI, QName.createValidLocalName(this.name)),
                     true);
               // also need to set the new description and icon properties
               // TODO: remove this when the copy also copies the name
               this.nodeService.setProperty(copiedNode, ContentModel.PROP_NAME, this.name);
               this.nodeService.setProperty(copiedNode, ContentModel.PROP_DESCRIPTION, this.description);
               this.nodeService.setProperty(copiedNode, ContentModel.PROP_ICON, this.icon);
               
               newSpaceId = copiedNode.getId();
                  
               if (logger.isDebugEnabled())
                  logger.debug("Copied space with id of " + sourceNode.getId() + " to " + this.name);
            }
            else if (this.createFrom.equals("template"))
            {
               // copy the selected space and update the name, description and icon
               NodeRef sourceNode = new NodeRef(Repository.getStoreRef(), this.templateSpaceId);
               NodeRef parentSpace = new NodeRef(Repository.getStoreRef(), getNavigator().getCurrentNodeId());
               NodeRef copiedNode = this.nodeOperationsService.copy(sourceNode, parentSpace, 
                     ContentModel.ASSOC_CONTAINS,
                     QName.createQName(NamespaceService.ALFRESCO_URI, QName.createValidLocalName(this.name)), 
                     true);
               // also need to set the new description and icon properties
               // TODO: remove this when the copy also copies the name
               this.nodeService.setProperty(copiedNode, ContentModel.PROP_NAME, this.name);
               this.nodeService.setProperty(copiedNode, ContentModel.PROP_DESCRIPTION, this.description);
               this.nodeService.setProperty(copiedNode, ContentModel.PROP_ICON, this.icon);
               
               newSpaceId = copiedNode.getId();
               
               if (logger.isDebugEnabled())
                  logger.debug("Copied template space with id of " + sourceNode.getId() + " to " + this.name);
            }
            
            // if the user selected to save the space as a template space copy the new
            // space to the templates folder
            if (this.saveAsTemplate)
            {
               // get hold of the Templates node
               DynamicNamespacePrefixResolver namespacePrefixResolver = new DynamicNamespacePrefixResolver(null);
               namespacePrefixResolver.addDynamicNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
               
               String actNs = NamespaceService.ALFRESCO_PREFIX;
               String xpath = actNs + ":" + 
                     QName.createValidLocalName(Application.getCompanyRootName(FacesContext.getCurrentInstance())) + 
                     "/" + actNs + ":" + 
                     QName.createValidLocalName(Application.getGlossaryFolderName(FacesContext.getCurrentInstance())) +
                     "/" + actNs + ":" + 
                     QName.createValidLocalName(Application.getTemplatesFolderName(FacesContext.getCurrentInstance()));
               
               List<NodeRef> templateNodeList = this.nodeService.selectNodes(
                     this.nodeService.getRootNode(Repository.getStoreRef()),
                     xpath, null, namespacePrefixResolver, false);
               if (templateNodeList.size() == 1)
               {
                  // get the first item in the list as we from test above there is only one!
                  NodeRef templateNode = templateNodeList.get(0);
                  NodeRef sourceNode = new NodeRef(Repository.getStoreRef(), newSpaceId);
                  NodeRef templateCopyNode = this.nodeOperationsService.copy(sourceNode, templateNode, 
                        ContentModel.ASSOC_CONTAINS, 
                        QName.createQName(NamespaceService.ALFRESCO_URI, QName.createValidLocalName(this.templateName)));
                  this.nodeService.setProperty(templateCopyNode, ContentModel.PROP_NAME, this.templateName);
               }
            }
         }
         
         // commit the transaction
         tx.commit();
         
         // now we know the new details are in the repository, reset the
         // client side node representation so the new details are retrieved
         if (this.editMode)
         {
            this.browseBean.getActionSpace().reset();
         }
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         throw new AlfrescoRuntimeException("Failed to create new space", e);
      }
      
      return outcome;
   }
   
   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getWizardDescription()
    */
   public String getWizardDescription()
   {
      return WIZARD_DESC;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getWizardTitle()
    */
   public String getWizardTitle()
   {
      return WIZARD_TITLE;
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
            stepDesc = STEP3_DESCRIPTION;
            break;
         }
         case 4:
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
            stepTitle = STEP3_TITLE;
            break;
         }
         case 4:
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
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getStepInstructions()
    */
   public String getStepInstructions()
   {
      String stepInstruction = null;
      
      switch (this.currentStep)
      {
         case 4:
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
    * Initialises the wizard
    */
   public void init()
   {
      super.init();
      
      // clear the cached query results
      if (this.templates != null)
      {
         this.templates.clear();
         this.templates = null;
      }
      
      // reset all variables
      this.createFrom = "scratch";
      this.spaceType = "container";
      this.icon = "space-icon-default";
      this.copyPolicy = "structure";
      this.existingSpaceId = null;
      this.templateSpaceId = null;
      this.name = null;
      this.description = "";
      this.templateName = null;
      this.saveAsTemplate = false;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#populate()
    */
   public void populate()
   {
      // get hold of the current node and populate the appropriate values
      Node currentSpace = browseBean.getActionSpace();
      Map<String, Object> props = currentSpace.getProperties();
      
      this.name = (String)props.get("name");
      this.description = (String)props.get("description");
      this.icon = (String)props.get("icon");
   }

   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      String summaryCreateType = null;
      
      if (this.createFrom.equals("scratch"))
      {
         summaryCreateType = "Scratch";
      }
      else if (this.createFrom.equals("existing"))
      {
         summaryCreateType = "An existing space";
      }
      else if (this.createFrom.equals("template"))
      {
         summaryCreateType = "A template";
      }
      
      String summarySaveAsTemplate = this.saveAsTemplate ? "Yes" : "No";
      
      return buildSummary(
            new String[] {"Name", "Description", "Creating From", "Save As Template", "Template Name"},
            new String[] {this.name, this.description, summaryCreateType, summarySaveAsTemplate, this.templateName});
   }
   
   /**
    * @return Returns a list of template spaces currently in the system
    */
   public List getTemplateSpaces()
   {      
      if (this.templates == null)
      {
         this.templates = new ArrayList<SelectItem>();
         
         // add an entry to instruct the user to select a template
         this.templates.add(new SelectItem("none", "Select a template..."));
         
         FacesContext context = FacesContext.getCurrentInstance();
         String actNs = NamespaceService.ALFRESCO_PREFIX;
         String xpath = actNs + ":" + 
               QName.createValidLocalName(Application.getCompanyRootName(context)) + 
               "/" + actNs + ":" + 
               QName.createValidLocalName(Application.getGlossaryFolderName(context)) +
               "/" + actNs + ":" + 
               QName.createValidLocalName(Application.getTemplatesFolderName(context)) +
               "/*";
         
         NodeRef rootNodeRef = this.nodeService.getRootNode(Repository.getStoreRef());
         DynamicNamespacePrefixResolver resolver = new DynamicNamespacePrefixResolver(null);
         resolver.addDynamicNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
         List<NodeRef> results = this.nodeService.selectNodes(rootNodeRef, xpath, null, resolver, false);
         
         if (results.size() > 0)
         {
            for (NodeRef assocRef : results)
            {
               Node childNode = new Node(assocRef, this.nodeService);
               this.templates.add(new SelectItem(childNode.getId(), childNode.getName()));
            }
         }
      }
      
      return this.templates;
   }

   /**
    * @return Returns the searchService.
    */
   public SearchService getSearchService()
   {
      return searchService;
   }

   /**
    * @param searchService The searchService to set.
    */
   public void setSearchService(SearchService searchService)
   {
      this.searchService = searchService;
   }
   
   /**
    * @return Returns the NodeOperationsService.
    */
   public CopyService getNodeOperationsService()
   {
      return this.nodeOperationsService;
   }

   /**
    * @param nodeOperationsService   The NodeOperationsService to set.
    */
   public void setNodeOperationsService(CopyService nodeOperationsService)
   {
      this.nodeOperationsService = nodeOperationsService;
   }
   
   /**
    * @return Returns the copyPolicy.
    */
   public String getCopyPolicy()
   {
      return copyPolicy;
   }

   /**
    * @param copyPolicy The copyPolicy to set.
    */
   public void setCopyPolicy(String copyPolicy)
   {
      this.copyPolicy = copyPolicy;
   }
   
   /**
    * @return Returns the createFrom.
    */
   public String getCreateFrom()
   {
      return createFrom;
   }

   /**
    * @param createFrom The createFrom to set.
    */
   public void setCreateFrom(String createFrom)
   {
      this.createFrom = createFrom;
   }

   /**
    * @return Returns the description.
    */
   public String getDescription()
   {
      return description;
   }
   
   /**
    * @param description The description to set.
    */
   public void setDescription(String description)
   {
      this.description = description;
   } 

   /**
    * @return Returns the existingSpaceId.
    */
   public String getExistingSpaceId()
   {
      return existingSpaceId;
   }
   
   /**
    * @param existingSpaceId The existingSpaceId to set.
    */
   public void setExistingSpaceId(String existingSpaceId)
   {
      this.existingSpaceId = existingSpaceId;
   }
   
   /**
    * @return Returns the icon.
    */
   public String getIcon()
   {
      return icon;
   }
   
   /**
    * @param icon The icon to set.
    */
   public void setIcon(String icon)
   {
      this.icon = icon;
   }
   
   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return name;
   }
   
   /**
    * @param name The name to set.
    */
   public void setName(String name)
   {
      this.name = name;
   }
   
   /**
    * @return Returns the saveAsTemplate.
    */
   public boolean isSaveAsTemplate()
   {
      return saveAsTemplate;
   }
   
   /**
    * @param saveAsTemplate The saveAsTemplate to set.
    */
   public void setSaveAsTemplate(boolean saveAsTemplate)
   {
      this.saveAsTemplate = saveAsTemplate;
   }

   /**
    * @return Returns the spaceType.
    */
   public String getSpaceType()
   {
      return spaceType;
   }
   
   /**
    * @param spaceType The spaceType to set.
    */
   public void setSpaceType(String spaceType)
   {
      this.spaceType = spaceType;
   }
   
   /**
    * @return Returns the templateName.
    */
   public String getTemplateName()
   {
      return templateName;
   }
   
   /**
    * @param templateName The templateName to set.
    */
   public void setTemplateName(String templateName)
   {
      this.templateName = templateName;
   }
   
   /**
    * @return Returns the templateSpaceId.
    */
   public String getTemplateSpaceId()
   {
      return templateSpaceId;
   }
   
   /**
    * @param templateSpaceId The templateSpaceId to set.
    */
   public void setTemplateSpaceId(String templateSpaceId)
   {
      this.templateSpaceId = templateSpaceId;
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
            outcome = "create-from";
            break;
         }
         case 2:
         {
            if (createFrom.equalsIgnoreCase("scratch"))
            {
               outcome = "from-scratch";
            }
            else if (createFrom.equalsIgnoreCase("existing"))
            {
               outcome = "from-existing";
            }
            else if (createFrom.equalsIgnoreCase("template"))
            {
               outcome = "from-template";
            }
            
            break;
         }
         case 3:
         {
            outcome = "details";
            break;
         }
         case 4:
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
}
