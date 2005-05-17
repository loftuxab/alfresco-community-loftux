package org.alfresco.web.bean.wizard;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.ResultSetRow;
import org.alfresco.repo.search.Searcher;
import org.alfresco.util.Conversion;
import org.alfresco.web.bean.NavigationBean;
import org.alfresco.web.bean.RepoUtils;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIModeList;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Handler class used by the New Space Wizard 
 * 
 * @author gavinc
 */
public class NewSpaceWizard
{
   private static Logger logger = Logger.getLogger(NewSpaceWizard.class);
   private static final String ERROR_NODEREF = "Unable to find the repository node referenced by Id: {0} - the node has probably been deleted from the database.";
   
   // new space wizard specific properties
   private String createFrom = "scratch";
   private String spaceType = "container";
   private String existingSpaceId;
   private String templateSpaceId;
   private String copyPolicy = "structure";
   private String name;
   private String description;
   private String icon = "space-icon-default";
   private String templateName;
   private boolean saveAsTemplate = false;
   private List spaces;
   private List templates;
   
   // common wizard properties
   private int currentStep = 1;
   private boolean finishDisabled = true;
   private String currentSpaceName;
   private NodeService nodeService;
   private Searcher searchService;
   private NavigationBean navigator;

   /**
    * @return Returns the nodeService.
    */
   public NodeService getNodeService()
   {
      return this.nodeService;
   }

   /**
    * @param nodeService The nodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * @return Returns the searchService.
    */
   public Searcher getSearchService()
   {
      return searchService;
   }

   /**
    * @param searchService The searchService to set.
    */
   public void setSearchService(Searcher searchService)
   {
      this.searchService = searchService;
   }
   
   /**
    * @return Returns the navigation bean instance.
    */
   public NavigationBean getNavigator()
   {
      return navigator;
   }
   
   /**
    * @param navigator The NavigationBean to set.
    */
   public void setNavigator(NavigationBean navigator)
   {
      this.navigator = navigator;
   }
   
   /**
    * @return Returns the currentStep.
    */
   public int getCurrentStep()
   {
      return currentStep;
   }

   /**
    * @param currentStep The currentStep to set.
    */
   public void setCurrentStep(int currentStep)
   {
      this.currentStep = currentStep;
   }
   
   /**
    * @return Returns the currentSpaceName.
    */
   public String getCurrentSpaceName()
   {
      // NOTE: We do this ourself rather than using the browse bean as when the 
      //       wizard potentially gets restored from the shelf the current node
      //       may have changed from when the wizard was launched. That means we
      //       should also store the current id too!
      
      if (this.currentSpaceName == null)
      {
         String spaceName = "Alfresco Home";
         String currentNodeId = this.navigator.getCurrentNodeId();
         if (currentNodeId != null)
         {
            try
            {
               NodeRef ref = new NodeRef(Repository.getStoreRef(), currentNodeId);
               spaceName = RepoUtils.getNameForNode(this.nodeService, ref);
            }
            catch (InvalidNodeRefException refErr)
            {
               Utils.addErrorMessage(MessageFormat.format(ERROR_NODEREF, new Object[] {currentNodeId}));
            }
         }
         
         this.currentSpaceName = spaceName;
      }
      
      return this.currentSpaceName;
   }

   /**
    * @param currentSpaceName The currentSpaceName to set.
    */
   public void setCurrentSpaceName(String currentSpaceName)
   {
      this.currentSpaceName = currentSpaceName;
   }
   
   /**
    * @return Returns the finishDisabled.
    */
   public boolean isFinishDisabled()
   {
      return finishDisabled;
   }

   /**
    * @param finishDisabled The finishDisabled to set.
    */
   public void setFinishDisabled(boolean finishDisabled)
   {
      this.finishDisabled = finishDisabled;
   }

   /**
    * Deals with the next button being pressed
    * 
    * @return
    */
   public String next()
   {
      this.currentStep++;
      
      // determine whether the finish button should be enabled
      evaluateFinishButtonState();
      
      // determine which page to go to next
      String nextPage = determinePageForStep(this.currentStep);
      
      // navigate
      navigate(nextPage);
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Navigating to next page: " + nextPage);
         logger.debug("currentStep: " + this.currentStep);
      }
      
      // return null to prevent the naviagtion handler looking up the next page
      return null;
   }
   
   /**
    * Deals with the back button being pressed
    * 
    * @return
    */
   public String back()
   {       
      this.currentStep--;
      
      // determine whether the finish button should be enabled
      evaluateFinishButtonState();
      
      // determine which page to go to next
      String previousPage = determinePageForStep(this.currentStep);
      
      // navigate
      navigate(previousPage);
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Navigating to previous page: " + previousPage);
         logger.debug("currentStep: " + this.currentStep);
      }
      
      // return null to prevent the naviagtion handler looking up the next page
      return null;
   }
   
   /**
    * Deals with the finish button being pressed
    * 
    * @return outcome
    */
   public String finish()
   {
      if (logger.isDebugEnabled())
         logger.debug(getSummary());
      
      // *******************************************************************************
      // TODO: The user may have selected to create the space from an existing space
      //       or a template space, if so we need to copy rather than create, but there
      //       are no repository services available yet to do this!
      //       We also need to be aware of copying structure and/or content.
      //       For now we always create the space from scratch.
      // *******************************************************************************
      
      if (this.name == null || this.name.length() == 0)
      {
         // create error and send wizard back to details page
         Utils.addErrorMessage("You must supply a name for the space");
         navigate(determinePageForStep(3));
      }
      else
      {
         UserTransaction tx = null;
      
         try
         {
            tx = (UserTransaction)FacesContextUtils.getRequiredWebApplicationContext(
                    FacesContext.getCurrentInstance()).getBean(Repository.USER_TRANSACTION);
            tx.begin();
            
            // get the node service and create the space (just create a folder for now)
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
            
            ChildAssocRef assocRef = this.nodeService.createNode(parentNodeRef,
                      null,
                      QName.createQName(NamespaceService.ALFRESCO_URI, this.name),
                      DictionaryBootstrap.TYPE_QNAME_FOLDER);
            
            NodeRef nodeRef = assocRef.getChildRef();
            
            // set the properties
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
            Date now = new Date( Calendar.getInstance().getTimeInMillis() );
            
            QName propName = QName.createQName(NamespaceService.ALFRESCO_URI, "name");
            properties.put(propName, this.name);
            
            QName propCreatedDate = QName.createQName(NamespaceService.ALFRESCO_URI, "createddate");
            properties.put(propCreatedDate, Conversion.dateToXmlDate(now));
           
            QName propModifiedDate = QName.createQName(NamespaceService.ALFRESCO_URI, "modifieddate");
            properties.put(propModifiedDate, Conversion.dateToXmlDate(now));
           
            QName propIcon = QName.createQName(NamespaceService.ALFRESCO_URI, "icon");
            properties.put(propIcon, this.icon);
           
            QName propSpaceType = QName.createQName(NamespaceService.ALFRESCO_URI, "spacetype");
            properties.put(propSpaceType, this.spaceType);
           
            if (this.description != null)
            {
               QName propDescription = QName.createQName(NamespaceService.ALFRESCO_URI, "description");
               properties.put(propDescription, this.description);
            }
            
            // add the space aspect to the folder
            this.nodeService.addAspect(nodeRef, DictionaryBootstrap.ASPECT_SPACE, properties);

            // commit the transaction
            tx.commit();
         }
         catch (Exception e)
         {
            // rollback the transaction
            try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
            throw new RuntimeException(e);
         }
        
         // reset the state
         reset();
         
         // navigate
         navigate("/jsp/browse/browse.jsp");
      }
      
      return null;
   }
   
   /**
    * Deals with the cancel button being pressed
    * 
    * @return
    */
   public String cancel()
   {
      // reset the state
      reset();
      
      // navigate
      navigate("/jsp/browse/browse.jsp");
      
      return null;
   }
   
   /**
    * Deals with the minimise button being pressed
    * 
    * @return
    */
   public String minimise()
   {
      // navigate
      navigate("/jsp/browse/browse.jsp");
      
      return null;
   }
   
   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      StringBuilder builder = new StringBuilder();
      builder.append("Name: ").append(this.name).append("<br/>");
      builder.append("Description: ").append(this.description).append("<br/>");
      builder.append("Create Type: ").append(this.createFrom).append("<br/>");
      builder.append("Space Type: ").append(this.spaceType).append("<br/>");
      builder.append("icon: ").append(this.icon).append("<br/>");
      builder.append("Save As Template: ").append(this.saveAsTemplate).append("<br/>");
      builder.append("Template Name: ").append(this.templateName).append("<br/>");
      
      return builder.toString();
   }

   /**
    * @return Returns a list of template spaces currently in the system
    */
   public List getTemplateSpaces()
   {
      if (this.templates == null)
      {
         this.templates = querySpaces(true);
      }
      
      return this.templates;
   }
   
   /**
    * @return Returns a list of spaces currently in the system
    */
   public List getSpaces()
   {
      if (this.spaces == null)
      {
         this.spaces = querySpaces(false);
      }
      
      return this.spaces;
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
    * Called when the step is changed by the left panel
    * 
    * @param event The event representing the step change
    */
   public void stepChanged(ActionEvent event)
   {
      UIModeList viewList = (UIModeList)event.getComponent();
      int step = Integer.parseInt(viewList.getValue().toString());
      
      String page = determinePageForStep(step);
      navigate(page);
      
      if (logger.isDebugEnabled())
         logger.debug("Navigating directly to: " + page);
   }
   
   /**
    * Sets the page to navigate to
    * 
    * @param page Page to navigate to
    */
   private void navigate(String page)
   {
      FacesContext ctx = FacesContext.getCurrentInstance();
      UIViewRoot newRoot = ctx.getApplication().getViewHandler().createView(ctx, page);
      ctx.setViewRoot(newRoot);
      ctx.renderResponse();
   }
   
   /**
    * Returns the page to be navigated to for the given step
    * 
    * @param step
    * @return
    */
   private String determinePageForStep(int step)
   {
      // TODO: in the wizard framework make this abstract and represent step
      //       0 as the page the wizard was launched from and step -1 as the
      //       cancel page
      
      String page = null;
      String dir = "/jsp/wizard/new-space/";
      
      switch (step)
      {
         case 1:
         {
            page = dir + "create-from.jsp";
            break;
         }
         case 2:
         {
            if (createFrom.equalsIgnoreCase("scratch"))
            {
               page = dir + "from-scratch.jsp";
            }
            else if (createFrom.equalsIgnoreCase("existing"))
            {
               page = dir + "from-existing.jsp";
            }
            else if (createFrom.equalsIgnoreCase("template"))
            {
               page = dir + "from-template.jsp";
            }
            
            break;
         }
         case 3:
         {
            page = dir + "details.jsp";
            break;
         }
         case 4:
         {
            page = dir + "summary.jsp";
            break;
         }
         default:
         {
            page = "/jsp/browse/browse.jsp";
         }
      }
      
      return page;
   }
   
   /**
    * Determines whether the finish button should be enabled and sets
    * the finishDisabled flag appropriately
    */
   private void evaluateFinishButtonState()
   {
      if (this.createFrom.equals("scratch") && this.currentStep > 2)
      {
         this.finishDisabled = false;
      }
      else
      {
         this.finishDisabled = true;
      }
   }
   
   /**
    * Returns a list of spaces in the system 
    * 
    * @param templates Determines whether to return template spaces or not 
    * @return List of spaces
    */
   private List querySpaces(boolean templates)
   {
      // get the node service and root node
      NodeRef rootNodeRef = this.nodeService.getRootNode(Repository.getStoreRef());
      
      List<SelectItem> items = new ArrayList<SelectItem>();
      
      if (templates)
      {
         String actNs = NamespaceService.ALFRESCO_PREFIX;
         String s = "PATH:\"/" + actNs + ":Glossary/" + actNs + ":Templates/" + actNs + ":*\"";
         ResultSet results = this.searchService.query(rootNodeRef.getStoreRef(), "lucene", s, null, null);
         if (results.length() > 0)
         {
            for (ResultSetRow row : results)
            {
               NodeRef node = row.getNodeRef();
               if (this.nodeService.hasAspect(node, DictionaryBootstrap.ASPECT_SPACE))
               {
                  String name = row.getQName().getLocalName();
                  String id = node.getId();
                  items.add(new SelectItem(id, name));
               }
            }
         }
      }
      else
      {
         // get all the child nodes from the root
         Collection<ChildAssocRef> childRefs = this.nodeService.getChildAssocs(rootNodeRef);
         for (ChildAssocRef ref: childRefs)
         {
            NodeRef child = ref.getChildRef();
            // if the node has the space aspect applied add it to the list
            if (this.nodeService.hasAspect(child, DictionaryBootstrap.ASPECT_SPACE))
            {
               String id = child.getId();
               String name = ref.getQName().getLocalName();
               // also filter out the Glossary space  TODO: make this a system type or space
               if (name.equals("Glossary") == false)
               {
                  items.add(new SelectItem(id, name));
               }
            }
         }
      }
      
      return items;
   }
   
   /**
    * Resets the state of the wizard
    */
   private void reset()
   {
      // clear the cached query results
      if (this.spaces != null)
      {
         this.spaces.clear();
         this.spaces = null;
      }
      if (this.templates != null)
      {
         this.templates.clear();
         this.templates = null;
      }
      
      // reset all variables
      this.currentStep = 1;
      this.createFrom = "scratch";
      this.spaceType = "container";
      this.currentSpaceName = null;
      this.existingSpaceId = null;
      this.templateSpaceId = null;
      this.copyPolicy = "structure";
      this.name = null;
      this.description = null;
      this.icon = "icon1";
      this.templateName = null;
      this.saveAsTemplate = false;
      this.finishDisabled = true;
   }
}
