package com.activiti.web.wizard;

import org.apache.log4j.Logger;


/**
 * Handler class used by the New Space Wizard 
 * 
 * @author gavinc
 */
public class NewSpace
{
   private static Logger logger = Logger.getLogger(NewSpace.class);
   
   private String createType = "scratch";
   private String spaceType = "container";
   private String existingSpaceId;
   private String templateSpaceId;
   private boolean copyContents = false;
   private String name;
   private String description;
   private String icon;
   private boolean saveAsTemplate = false;
   private String templateName;
   private int currentStep = 1;
   
   /**
    * Deals with the next button being pressed
    * 
    * @return
    */
   public String next()
   {
      String nextPage = "next";
      
      switch (currentStep)
      {
         case 1:
         {
            if (createType.equalsIgnoreCase("scratch"))
            {
               nextPage = "next-scratch";
            }
            else if (createType.equalsIgnoreCase("existing"))
            {
               nextPage = "next-existing";
            }
            else if (createType.equalsIgnoreCase("template"))
            {
               nextPage = "next-template";
            }
            
            this.currentStep = 2;
            break;
         }
         case 2:
         {
            this.currentStep = 3;
            break;
         }
         case 3:
         {
            this.currentStep = 4;
            break;
         }
      }
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Navigating to next page outcome: " + nextPage);
         logger.debug("currentStep: " + this.currentStep);
      }
      
      return nextPage;
   }
   
   /**
    * Deals with the back button being pressed
    * 
    * @return
    */
   public String back()
   {
      this.currentStep--;
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Back called");
         logger.debug("currentStep: " + this.currentStep);
      }
      
      return "back";
   }
   
   /**
    * Deals with the finish button being pressed
    * 
    * @return
    */
   public String finish()
   {
      // TODO: gather up all the data entered by the user and create the space using the NodeService
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Finish called");
         logger.debug(getSummary());
      }
      
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
    * @param summary The summary to set.
    */
   public void setSummary(String summary)
   {
      // do nothing
   }

   /**
    * @return Returns the copyContents.
    */
   public boolean isCopyContents()
   {
      return copyContents;
   }
   
   /**
    * @param copyContents The copyContents to set.
    */
   public void setCopyContents(boolean copyContents)
   {
      this.copyContents = copyContents;
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
}
