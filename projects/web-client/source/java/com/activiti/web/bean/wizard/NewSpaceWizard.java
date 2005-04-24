package com.activiti.web.bean.wizard;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.dictionary.bootstrap.DictionaryBootstrap;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.ResultSet;
import com.activiti.repo.search.ResultSetRow;
import com.activiti.repo.search.Searcher;
import com.activiti.web.bean.repository.Repository;

/**
 * Handler class used by the New Space Wizard 
 * 
 * @author gavinc
 */
public class NewSpaceWizard
{
   private static Logger logger = Logger.getLogger(NewSpaceWizard.class);
   
   private String createType = "scratch";
   private String spaceType = "container";
   private String existingSpaceId;
   private String templateSpaceId;
   private String copyPolicy = "structure";
   private String name;
   private String description;
   private String icon = "icon1";
   private boolean saveAsTemplate = false;
   private String templateName;
   private int currentStep = 1;
   
   private List spaces;
   private List templates;
   
   private NodeService nodeService;
   private Searcher searchService;
   
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
    * Deals with the next button being pressed
    * 
    * @return
    */
   public String next()
   {
      String nextOutcome = "next";      
      
//      String page = "/jsp/wizard/new-space-scratch.jsp";
//      UIViewRoot newRoot = FacesContext.getCurrentInstance().getApplication().getViewHandler().createView(FacesContext.getCurrentInstance(), page);
//      FacesContext.getCurrentInstance().setViewRoot(newRoot);
//      FacesContext.getCurrentInstance().renderResponse();
//      logger.debug("set view root to: " + page);
      
      if (this.currentStep == 1)
      {
         if (createType.equalsIgnoreCase("scratch"))
         {
            nextOutcome = "next-scratch";
         }
         else if (createType.equalsIgnoreCase("existing"))
         {
            nextOutcome = "next-existing";
         }
         else if (createType.equalsIgnoreCase("template"))
         {
            nextOutcome = "next-template";
         }
      }
       
      this.currentStep++;
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Navigating to next outcome: " + nextOutcome);
         logger.debug("currentStep: " + this.currentStep);
      }
      
      return nextOutcome;
   }
   
   /**
    * Deals with the back button being pressed
    * 
    * @return
    */
   public String back()
   {
      String backOutcome = "back";
      
      if (this.currentStep == 3)
      {
         if (createType.equalsIgnoreCase("scratch"))
         {
            backOutcome = "back-scratch";
         }
         else if (createType.equalsIgnoreCase("existing"))
         {
            backOutcome = "back-existing";
         }
         else if (createType.equalsIgnoreCase("template"))
         {
            backOutcome = "back-template";
         }
      }
      
      this.currentStep--;
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Navigating to back outcome: " + backOutcome);
         logger.debug("currentStep: " + this.currentStep);
      } 
      
      return backOutcome;
   }
   
   /**
    * Deals with the finish button being pressed
    * 
    * @return
    */
   public String finish()
   {
      if (logger.isDebugEnabled())
         logger.debug(getSummary());
      
      // get the node service and create the space (just create a folder for now)
      NodeRef rootNodeRef = this.nodeService.getRootNode(Repository.getStoreRef());
      ChildAssocRef assocRef = this.nodeService.createNode(rootNodeRef,
                QName.createQName(NamespaceService.ACTIVITI_URI, this.name),
                DictionaryBootstrap.TYPE_FOLDER);
      NodeRef nodeRef = assocRef.getChildRef();
      
      // set the properties
      if (this.description != null)
      {
         QName propDesc = QName.createQName("description");
         this.nodeService.setProperty(nodeRef, propDesc, this.description);
      }
      
      QName propIcon = QName.createQName("icon");
      this.nodeService.setProperty(nodeRef, propIcon, this.icon);
      
      this.currentStep = 1;
      
      return "finish";
   }
   
   /**
    * Deals with the cancel button being pressed
    * 
    * @return
    */
   public String cancel()
   {
      if (logger.isDebugEnabled())
         logger.debug("Cancel called");
      
      this.currentStep = 1;
      
      return "cancel";
   }
   
   /**
    * Deals with the minimise button being pressed
    * 
    * @return
    */
   public String minimise()
   {
      if (logger.isDebugEnabled())
         logger.debug("Minimise called");
      
      this.currentStep = 1;
      
      return "minimise";
   }
   
   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      StringBuilder builder = new StringBuilder();
      builder.append("Name: ").append(this.name).append("<br/>");
      builder.append("Description: ").append(this.description).append("<br/>");
      builder.append("Create Type: ").append(this.createType).append("<br/>");
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
    * @return Returns the createType.
    */
   public String getCreateType()
   {
      return createType;
   }
   
   /**
    * @param createType The createType to set.
    */
   public void setCreateType(String createType)
   {
      this.createType = createType;
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
   
   private List querySpaces(boolean templates)
   {
      // get the node service and root node
      NodeRef rootNodeRef = this.nodeService.getRootNode(Repository.getStoreRef());
      
      // get the searcher object and perform the search of the root node
      String s = "PATH:\"/" + NamespaceService.ACTIVITI_PREFIX + ":*\"";
      ResultSet results = this.searchService.query(rootNodeRef.getStoreRef(), "lucene", s, null, null);
      
      // create a list of items from the results
      ArrayList items = new ArrayList();
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
}
