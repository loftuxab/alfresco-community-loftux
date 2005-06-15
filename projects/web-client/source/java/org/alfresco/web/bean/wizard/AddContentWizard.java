package org.alfresco.web.bean.wizard;

import java.io.File;

import javax.faces.context.FacesContext;

import org.alfresco.web.bean.FileUploadBean;
import org.alfresco.web.bean.repository.Repository;
import org.apache.log4j.Logger;

/**
 * Handler class used by the Add Content Wizard 
 * 
 * @author gavinc
 */
public class AddContentWizard extends BaseContentWizard
{
   private static Logger logger = Logger.getLogger(AddContentWizard.class);

   // TODO: retrieve these from the config service
   private static final String WIZARD_TITLE = "Add Content Wizard";
   private static final String WIZARD_DESC = "Use this wizard to add a document to a space.";
   private static final String STEP1_TITLE = "Step One - Upload Document";
   private static final String STEP1_DESCRIPTION = "Locate and upload your document to the repository.";
   private static final String STEP2_TITLE = "Step Two - Properties";
   private static final String STEP2_DESCRIPTION = "Enter information about this content.";
   
   // add content wizard specific properties
   private File file;
   
   
   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#next()
    */
   public String next()
   {
      String outcome = super.next();
      
      // if the outcome is "properties" we pre-set the content type and title
      // fields accordingly
      if (outcome.equals("properties"))
      {
         if (this.contentType == null)
         {
            this.contentType = Repository.getMimeTypeForFileName(
                  FacesContext.getCurrentInstance(), this.fileName);
         }
         
         if (this.title == null)
         {
            this.title = this.fileName;
         }
      }
      
      return outcome;
   }
   
   /**
    * Deals with the finish button being pressed
    * 
    * @return outcome
    */
   public String finish()
   {
      String outcome = FINISH_OUTCOME;
      
      saveContent(this.file, null);
      
      // now we know the new details are in the repository, reset the
      // client side node representation so the new details are retrieved
      if (this.editMode)
      {
         this.browseBean.getDocument().reset();
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
    * Initialises the wizard
    */
   public void init()
   {
      super.init();
      
      clearUpload();
      
      this.file = null;
   }

   /**
    * @return Returns the name of the file
    */
   public String getFileName()
   {
      // try and retrieve the file and filename from the file upload bean
      // representing the file we previously uploaded.
      FacesContext ctx = FacesContext.getCurrentInstance();
      FileUploadBean fileBean = (FileUploadBean)ctx.getExternalContext().getSessionMap().
         get(FileUploadBean.FILE_UPLOAD_BEAN_NAME);
      if (fileBean != null)
      {
         this.file = fileBean.getFile();
         this.fileName = fileBean.getFileName();
      }
      
      return this.fileName;
   }

   /**
    * @param fileName The name of the file
    */
   public void setFileName(String fileName)
   {
      this.fileName = fileName;
      
      // we also need to keep the file upload bean in sync
      FacesContext ctx = FacesContext.getCurrentInstance();
      FileUploadBean fileBean = (FileUploadBean)ctx.getExternalContext().getSessionMap().
         get(FileUploadBean.FILE_UPLOAD_BEAN_NAME);
      if (fileBean != null)
      {
         fileBean.setFileName(this.fileName);
      }
   }
   
   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      return buildSummary(
            new String[] {"File Name", "Content Type", "Title", "Description", "Author"},
            new String[] {this.fileName, getSummaryContentType(), this.title, this.description, this.author});
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
            outcome = "upload";
            break;
         }
         case 2:
         {
            outcome = "properties";
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
    * Deletes the uploaded file and removes the FileUploadBean from the session
    */
   private void clearUpload()
   {
      // delete the temporary file we uploaded earlier
      if (this.file != null)
      {
         this.file.delete();
      }
      
      // remove the file upload bean from the session
      FacesContext ctx = FacesContext.getCurrentInstance();
      ctx.getExternalContext().getSessionMap().remove(FileUploadBean.FILE_UPLOAD_BEAN_NAME);
   }
}
