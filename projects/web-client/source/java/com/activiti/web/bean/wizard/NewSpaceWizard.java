package com.activiti.web.bean.wizard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.dictionary.bootstrap.DictionaryBootstrap;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.QName;
import com.activiti.repo.search.ResultSet;
import com.activiti.repo.search.ResultSetRow;
import com.activiti.repo.search.Searcher;
import com.activiti.util.Conversion;
import com.activiti.web.bean.NavigationBean;
import com.activiti.web.bean.repository.Repository;
import com.activiti.web.jsf.component.UIModeList;

/**
 * Handler class used by the New Space Wizard 
 * 
 * @author gavinc
 */
public class NewSpaceWizard
{
   private static Logger logger = Logger.getLogger(NewSpaceWizard.class);
   
   private String createFrom = "scratch";
   private String spaceType = "container";
   private String existingSpaceId;
   private String templateSpaceId;
   private String copyPolicy = "structure";
   private String name;
   private String description;
   private String icon = "icon1";
   private String templateName;
   private boolean saveAsTemplate = false;
   private boolean finishDisabled = true;
   private int currentStep = 1;
   private NodeService nodeService;
   private Searcher searchService;
   
   /** The NavigationBean reference */
   private NavigationBean navigator;
   
   private List spaces;
   private List templates;
   
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
    * Deals with the next button being pressed
    * 
    * @return
    */
   public String next()
   {
      this.currentStep++;
      
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
                QName.createQName(NamespaceService.ACTIVITI_URI, this.name),
                DictionaryBootstrap.TYPE_FOLDER);
      NodeRef nodeRef = assocRef.getChildRef();
      
      // set the properties
      if (this.description != null)
      {
         QName propDesc = QName.createQName(NamespaceService.ACTIVITI_URI, "description");
         this.nodeService.setProperty(nodeRef, propDesc, this.description);
      }
      
      QName propIcon = QName.createQName(NamespaceService.ACTIVITI_URI, "icon");
      this.nodeService.setProperty(nodeRef, propIcon, this.icon);
      
      QName propCreatedDate = QName.createQName(NamespaceService.ACTIVITI_URI, "createddate");
      Date now = new Date( Calendar.getInstance().getTimeInMillis() );
      this.nodeService.setProperty(nodeRef, propCreatedDate, Conversion.dateToXmlDate(now));
      
      QName propModifiedDate = QName.createQName(NamespaceService.ACTIVITI_URI, "modifieddate");
      this.nodeService.setProperty(nodeRef, propModifiedDate, Conversion.dateToXmlDate(now));
      
      // reset the state
      reset();
      
      // navigate
      navigate("/jsp/browse/browse.jsp");
      
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
            page = "/jsp/jump.jsp";
         }
      }
      
      return page;
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
      
      // get the searcher object and perform the search of the root node
      String s = "PATH:\"/" + NamespaceService.ACTIVITI_PREFIX + ":*\"";
      ResultSet results = this.searchService.query(rootNodeRef.getStoreRef(), "lucene", s, null, null);
      
      // create a list of items from the results
      ArrayList<SelectItem> items = new ArrayList<SelectItem>();
      if (results.length() > 0)
      {
         for (ResultSetRow row: results)
         {
            String name = row.getQName().getLocalName();
            SelectItem item = new SelectItem(name, name);
            items.add(item);
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
