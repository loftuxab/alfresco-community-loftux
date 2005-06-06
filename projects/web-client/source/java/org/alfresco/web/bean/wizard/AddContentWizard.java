package org.alfresco.web.bean.wizard;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.util.Conversion;
import org.alfresco.web.bean.FileUploadBean;
import org.alfresco.web.bean.RepoUtils;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.apache.log4j.Logger;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Handler class used by the Add Content Wizard 
 * 
 * @author gavinc
 */
public class AddContentWizard extends AbstractWizardBean
{
   private static Logger logger = Logger.getLogger(AddContentWizard.class);

   // TODO: retrieve these from the config service
   private static final String WIZARD_TITLE = "Add Content Wizard";
   private static final String WIZARD_DESC = "Use this wizard to add a document to a space.";
   private static final String STEP1_TITLE = "Step One - Upload Document";
   private static final String STEP1_DESCRIPTION = "Locate and upload your document to the repository.";
   private static final String FINISH_INSTRUCTION = "To add the content to this space click Finish.<br/>" +
                                                    "To review or change your selections click Back.";
   
   // add content wizard specific properties
   private File file;
   private String fileName;
   private String owner;
   private boolean overwrite;
   private ContentService contentService;
   
   /**
    * Deals with the finish button being pressed
    * 
    * @return outcome
    */
   public String finish()
   {
      String outcome = FINISH_OUTCOME;
      
      if (this.fileName == null || this.fileName.length() == 0)
      {
         // create error and send wizard back to upload page
         Utils.addErrorMessage("You must upload a file before you can complete the wizard.");
         outcome = determineOutcomeForStep(1);
         this.currentStep = 1;
      }
      else
      {
         UserTransaction tx = null;
         
         try
         {
            tx = RepoUtils.getUserTransaction(FacesContext.getCurrentInstance());
            tx.begin();
            
            // get the node ref of the node that will contain the content
            NodeRef containerNodeRef;
            String nodeId = getNavigator().getCurrentNodeId();
            if (nodeId == null)
            {
               containerNodeRef = this.nodeService.getRootNode(Repository.getStoreRef());
            }
            else
            {
               containerNodeRef = new NodeRef(Repository.getStoreRef(), nodeId);
            }

            // create properties for content type
            Map<QName, Serializable> contentProps = new HashMap<QName, Serializable>(3);
            contentProps.put(DictionaryBootstrap.PROP_QNAME_NAME, this.fileName);
            contentProps.put(DictionaryBootstrap.PROP_QNAME_ENCODING, "UTF-8");
            contentProps.put(DictionaryBootstrap.PROP_QNAME_MIME_TYPE, 
                  RepoUtils.getMimeTypeForFileName(FacesContext.getCurrentInstance(), this.fileName));
            
            // create the node to represent the node
            String assocName = this.fileName.replace('.', '-');
            if (assocName.length() > QName.MAX_LENGTH)
            {
               assocName = assocName.substring(0, QName.MAX_LENGTH);
            }
            ChildAssocRef assocRef = this.nodeService.createNode(containerNodeRef,
                   RepoUtils.QNAME_CONTAINS,
                   QName.createQName(NamespaceService.ALFRESCO_URI, assocName),
                   DictionaryBootstrap.TYPE_QNAME_CONTENT,
                   contentProps);
            NodeRef fileNodeRef = assocRef.getChildRef();
            
            if (logger.isDebugEnabled())
               logger.debug("Created file node for file: " + this.fileName);
            
            // apply the auditable aspect - created and modified date
            Map<QName, Serializable> auditProps = new HashMap<QName, Serializable>(5);
            Date now = new Date( Calendar.getInstance().getTimeInMillis() );
            auditProps.put(DictionaryBootstrap.PROP_QNAME_CREATED, now);
            auditProps.put(DictionaryBootstrap.PROP_QNAME_MODIFIED, now);
            this.nodeService.addAspect(fileNodeRef, DictionaryBootstrap.ASPECT_QNAME_AUDITABLE, auditProps);

            if (logger.isDebugEnabled())
               logger.debug("Added auditable aspect with properties: " + auditProps);
            
            // get a writer for the content and put the file
            ContentWriter writer = contentService.getUpdatingWriter(fileNodeRef);
            writer.putContent(this.file);
            
            // commit the transaction
            tx.commit();
         }
         catch (Exception e)
         {
            // rollback the transaction
            try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
            throw new AlfrescoRuntimeException("Failed to add content", e);
         }
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
         case 2:
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
      
      clearUpload();
      
      this.fileName = null;
      this.owner = null;
      this.file = null;
      this.overwrite = false;
   }
   
   /**
    * @return Returns the contentService.
    */
   public ContentService getContentService()
   {
      return contentService;
   }

   /**
    * @param contentService The contentService to set.
    */
   public void setContentService(ContentService contentService)
   {
      this.contentService = contentService;
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
    * @return Returns the overwrite.
    */
   public boolean isOverwrite()
   {
      return overwrite;
   }

   /**
    * @param overwrite The overwrite to set.
    */
   public void setOverwrite(boolean overwrite)
   {
      this.overwrite = overwrite;
   }

   /**
    * @return Returns the owner.
    */
   public String getOwner()
   {
      return owner;
   }

   /**
    * @param owner The owner to set.
    */
   public void setOwner(String owner)
   {
      this.owner = owner;
   }
   
   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      StringBuilder builder = new StringBuilder();
      builder.append("File Name: ").append(this.fileName).append("<br/>");
      //builder.append("Overwrite: ").append(this.overwrite).append("<br/>");
      
      return builder.toString();
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
      FileUploadBean fileBean = (FileUploadBean)ctx.getExternalContext().getSessionMap().
         remove(FileUploadBean.FILE_UPLOAD_BEAN_NAME);
   }
}
