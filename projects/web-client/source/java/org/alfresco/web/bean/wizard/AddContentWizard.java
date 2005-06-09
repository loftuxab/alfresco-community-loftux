package org.alfresco.web.bean.wizard;

import java.io.File;
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
import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.web.bean.FileUploadBean;
import org.alfresco.web.bean.repository.Node;
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
   private static final String STEP2_TITLE = "Step Two - Properties";
   private static final String STEP2_DESCRIPTION = "Enter information about this content.";
   private static final String FINISH_INSTRUCTION = "To add the content to this space click Finish.<br/>" +
                                                    "To review or change your selections click Back.";
   
   // add content wizard specific properties
   private File file;
   private String fileName;
   private String author;
   private String title;
   private String description;
   private String contentType;
   private List<SelectItem> contentTypes;
   private ContentService contentService;
   
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
      
      UserTransaction tx = null;
      
      try
      {
         FacesContext context = FacesContext.getCurrentInstance();
         tx = Repository.getUserTransaction(context);
         tx.begin();
         
         if (this.editMode)
         {
            // update the existing node in the repository
            Node currentDocument = this.browseBean.getDocument();
            NodeRef nodeRef = currentDocument.getNodeRef();
            Date now = new Date( Calendar.getInstance().getTimeInMillis() );
            
            // update the modified timestamp
            this.nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_MODIFIED, now);
            this.nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_NAME, this.fileName);
            this.nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_TITLE, this.title);
            this.nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_DESCRIPTION, this.description);
            this.nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_MIME_TYPE, this.contentType);
            this.nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_CREATOR, this.author);
         }
         else
         {
            // get the node ref of the node that will contain the content
            NodeRef containerNodeRef;
            String nodeId = getNavigator().getCurrentNodeId();
            if (nodeId == null)
            {
               containerNodeRef = this.nodeService.getRootNode(Repository.getStoreRef(context));
            }
            else
            {
               containerNodeRef = new NodeRef(Repository.getStoreRef(context), nodeId);
            }

            // create properties for content type
            Map<QName, Serializable> contentProps = new HashMap<QName, Serializable>(3);
            contentProps.put(DictionaryBootstrap.PROP_QNAME_NAME, this.fileName);
            contentProps.put(DictionaryBootstrap.PROP_QNAME_ENCODING, "UTF-8");
            contentProps.put(DictionaryBootstrap.PROP_QNAME_MIME_TYPE, this.contentType);
            
            // create the node to represent the node
            String assocName = Repository.createValidQName(this.fileName);
            ChildAssocRef assocRef = this.nodeService.createNode(
                  containerNodeRef,
                  DictionaryBootstrap.CHILD_ASSOC_QNAME_CONTAINS,
                  QName.createQName(NamespaceService.ALFRESCO_URI, assocName),
                  DictionaryBootstrap.TYPE_QNAME_CONTENT,
                  contentProps);
            
            NodeRef fileNodeRef = assocRef.getChildRef();
            
            if (logger.isDebugEnabled())
               logger.debug("Created file node for file: " + this.fileName);
            
            // apply the titled aspect - title and description
            Map<QName, Serializable> titledProps = new HashMap<QName, Serializable>(5);
            titledProps.put(DictionaryBootstrap.PROP_QNAME_TITLE, this.title);
            titledProps.put(DictionaryBootstrap.PROP_QNAME_DESCRIPTION, this.description);
            this.nodeService.addAspect(fileNodeRef, DictionaryBootstrap.ASPECT_QNAME_TITLED, titledProps);
            
            if (logger.isDebugEnabled())
               logger.debug("Added titled aspect with properties: " + titledProps);
            
            // apply the auditable aspect - created and modified date
            Map<QName, Serializable> auditProps = new HashMap<QName, Serializable>(5);
            Date now = new Date( Calendar.getInstance().getTimeInMillis() );
            auditProps.put(DictionaryBootstrap.PROP_QNAME_CREATED, now);
            auditProps.put(DictionaryBootstrap.PROP_QNAME_MODIFIED, now);
            auditProps.put(DictionaryBootstrap.PROP_QNAME_CREATOR, this.author);
            this.nodeService.addAspect(fileNodeRef, DictionaryBootstrap.ASPECT_QNAME_AUDITABLE, auditProps);

            if (logger.isDebugEnabled())
               logger.debug("Added auditable aspect with properties: " + auditProps);
            
            // get a writer for the content and put the file
            ContentWriter writer = contentService.getUpdatingWriter(fileNodeRef);
            writer.putContent(this.file);
         }
         
         // commit the transaction
         tx.commit();
         
         // now we know the new details are in the repository, reset the
         // client side node representation so the new details are retrieved
         if (this.editMode)
         {
            this.browseBean.getDocument().reset();
         }
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         throw new AlfrescoRuntimeException("Failed to add content", e);
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
    * Initialises the wizard
    */
   public void init()
   {
      super.init();
      
      clearUpload();
      
      this.fileName = null;
      this.author = null;
      this.file = null;
      this.title = null;
      this.description = null;
      this.contentType = null;
      
      if (this.contentTypes != null)
      {
         this.contentTypes.clear();
         this.contentTypes = null;
      }
   }
   
   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#populate()
    */
   public void populate()
   {
      // get hold of the current document and populate the appropriate values
      Node currentDocument = this.browseBean.getDocument();
      Map<String, Object> props = currentDocument.getProperties();
      
      this.author = (String)props.get("creator");
      this.contentType = (String)props.get("mimetype");
      this.description = (String)props.get("description");
      this.fileName = currentDocument.getName();
      this.title = (String)props.get("title");
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
    * @return Returns the author
    */
   public String getAuthor()
   {
      return this.author;
   }

   /**
    * @param author Sets the author
    */
   public void setAuthor(String author)
   {
      this.author = author;
   }

   /**
    * @return Returns the content type currenty selected
    */
   public String getContentType()
   {
      return this.contentType;
   }

   /**
    * @param contentType Sets the currently selected content type
    */
   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }

   /**
    * @return Returns the description
    */
   public String getDescription()
   {
      return this.description;
   }

   /**
    * @param description Sets the description
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * @return Returns the title
    */
   public String getTitle()
   {
      return this.title;
   }

   /**
    * @param title Sets the title
    */
   public void setTitle(String title)
   {
      this.title = title;
   }

   /**
    * @return Returns a list of content types to allow the user to select from
    */
   public List<SelectItem> getContentTypes()
   {
      if (this.contentTypes == null)
      {
         this.contentTypes = new ArrayList<SelectItem>(80);
         MimetypeMap mimetypeMap = (MimetypeMap)FacesContextUtils.
            getRequiredWebApplicationContext(FacesContext.getCurrentInstance()).
            getBean(Repository.BEAN_MIMETYPE_MAP);
         
         // get the mime type display names
         Map<String, String> mimeTypes = mimetypeMap.getDisplaysByMimetype();
         for (String mimeType : mimeTypes.keySet())
         {
            this.contentTypes.add(new SelectItem(mimeType, mimeTypes.get(mimeType)));
         }
      }
      
      return this.contentTypes;
   }
   
   /**
    * @return Determines whether the next and finish button should be enabled 
    */
   public boolean getNextFinishDisabled()
   {
      boolean disabled = false;
      
      if (this.fileName == null || this.fileName.length() == 0 ||
          this.title == null || this.title.length() == 0 ||
          this.contentType == null)
      {
         disabled = true;
      }
      
      return disabled;
   }

   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      return buildSummary(
            new String[] {"File Name", "Content Type", "Title", "Description", "Author"},
            new String[] {this.fileName, this.contentType, this.title, this.description, this.author});
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
