/*
 * Created on 20-May-2005
 */
package org.alfresco.web.bean;

import java.io.File;
import java.io.Serializable;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.UserTransaction;

import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.version.Version;
import org.alfresco.repo.version.operations.VersionOperationsService;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.app.servlet.DownloadContentServlet;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.apache.log4j.Logger;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * @author Kevin Roast
 */
public class CheckinCheckoutBean
{
   // ------------------------------------------------------------------------------
   // Bean property getters and setters 
   
   /**
    * @return Returns the BrowseBean.
    */
   public BrowseBean getBrowseBean()
   {
      return this.browseBean;
   }
   
   /**
    * @param browseBean The BrowseBean to set.
    */
   public void setBrowseBean(BrowseBean browseBean)
   {
      this.browseBean = browseBean;
   }
   
   /**
    * @return Returns the NodeService.
    */
   public NodeService getNodeService()
   {
      return this.nodeService;
   }

   /**
    * @param nodeService The NodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * @return Returns the VersionOperationsService.
    */
   public VersionOperationsService getVersionOperationsService()
   {
      return this.versionOperationsService;
   }
   
   /**
    * @param versionOperationsService  The VersionOperationsService to set.
    */
   public void setVersionOperationsService(VersionOperationsService versionOperationsService)
   {
      this.versionOperationsService = versionOperationsService;
   }
   
   /**
    * @return Returns the ContentService.
    */
   public ContentService getContentService()
   {
      return this.contentService;
   }

   /**
    * @param contentService   The ContentService to set.
    */
   public void setContentService(ContentService contentService)
   {
      this.contentService = contentService;
   }
   
   /**
    * @return The document node being used for the current operation
    */
   public Node getDocument()
   {
      return this.document;
   }

   /**
    * @param document The document node to be used for the current operation
    */
   public void setDocument(Node document)
   {
      this.document = document;
   }
   
   /**
    * @return Returns the working copy Document.
    */
   public Node getWorkingDocument()
   {
      return this.workingDocument;
   }
   
   /**
    * @param workingDocument The working copy Document to set.
    */
   public void setWorkingDocument(Node workingDocument)
   {
      this.workingDocument = workingDocument;
   }
   
   /**
    * @param keepCheckedOut   The keepCheckedOut to set.
    */
   public void setKeepCheckedOut(boolean keepCheckedOut)
   {
      this.keepCheckedOut = keepCheckedOut;
   }
   
   /**
    * @return Returns the keepCheckedOut.
    */
   public boolean getKeepCheckedOut()
   {
      return this.keepCheckedOut;
   }
   
   /**
    * @return Returns the version history notes.
    */
   public String getVersionNotes()
   {
      return this.versionNotes;
   }

   /**
    * @param versionNotes  The version history notes to set.
    */
   public void setVersionNotes(String versionNotes)
   {
      this.versionNotes = versionNotes;
   }
   
   /**
    * @return Returns the copy location. Either the current or other space.
    */
   public String getCopyLocation()
   {
      if (this.fileName != null)
      {
         return this.COPYLOCATION_OTHER;
      }
      else
      {
         return this.copyLocation;
      }
   }
   
   /**
    * @param copyLocation The copy location. Either the current or other space.
    */
   public void setCopyLocation(String copyLocation)
   {
      this.copyLocation = copyLocation;
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
   
   
   // ------------------------------------------------------------------------------
   // Navigation action event handlers
   
   /**
    * Action event called by all actions that need to setup a Content Document context on the 
    * CheckinCheckoutBean before an action page/wizard is called. The context will be a Node in
    * setDocument() which can be retrieved on the action page from BrowseBean.getDocument().
    * 
    * @param event   ActionEvent
    */
   public void setupContentAction(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         if (logger.isDebugEnabled())
            logger.debug("Setup for action, setting current document to: " + id);
         
         try
         {
            // create the node ref, then our node representation
            NodeRef ref = new NodeRef(Repository.getStoreRef(), id);
            Node node = new Node(ref, this.nodeService);
            
            // remember the document
            setDocument(node);
         }
         catch (InvalidNodeRefException refErr)
         {
            Utils.addErrorMessage( MessageFormat.format(RepoUtils.ERROR_NODEREF, new Object[] {id}) );
         }
      }
      else
      {
         setDocument(null);
      }
      
      clearUpload();
   }
   
   /**
    * Action called upon completion of the Check Out file page
    */
   public String checkoutFile()
   {
      String outcome = null;
      
      UserTransaction tx = null;
      
      Node node = getDocument();
      if (node != null)
      {
         try
         {
            tx = (UserTransaction)FacesContextUtils.getRequiredWebApplicationContext(
                  FacesContext.getCurrentInstance()).getBean(Repository.USER_TRANSACTION);
            tx.begin();
            
            if (logger.isDebugEnabled())
               logger.debug("Trying to checkout content node Id: " + node.getId());
            
            // checkout the node content to create a working copy
            // TODO: impl checkout to a arbituary parent Space 
            NodeRef workingCopyRef = this.versionOperationsService.checkout(node.getNodeRef());
            
            // modify the name to include an additional string
            // save the original name so we can set it back later
            String originalName = node.getName();
            String workingCopyName;
            int extIndex = originalName.lastIndexOf('.');
            if (extIndex != -1)
            {
               workingCopyName = originalName.substring(0, extIndex) + WORKING_COPY + originalName.substring(extIndex);  
            }
            else
            {
               workingCopyName = originalName + WORKING_COPY;
            }
            this.nodeService.setProperty(workingCopyRef, QNAME_NAME, workingCopyName);
            this.nodeService.setProperty(workingCopyRef, QNAME_ORIGINALNAME, originalName);
            
            // set the working copy Node instance
            Node workingCopy = new Node(workingCopyRef, this.nodeService);
            setWorkingDocument(workingCopy);
            
            // create content URL to the content download servlet with ID and expected filename
            // the myfile part will be ignored by the servlet but gives the browser a hint
            String url = DownloadContentServlet.generateURL(workingCopyRef, workingCopy.getName());
            
            workingCopy.getProperties().put("url", url);
            
            // commit the transaction
            tx.commit();
            
            // show the page that display the checkout link
            outcome = "checkoutFileLink";
         }
         catch (Throwable err)
         {
            // rollback the transaction
            try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
            Utils.addErrorMessage("Unable to checkout Content Node due to system error: " + err.getMessage());
         }
      }
      else
      {
         logger.warn("WARNING: checkoutFile called without a current Document!");
      }
      
      return outcome;
   }
   
   /**
    * Action called upon completion of the Check Out file Link download page
    */
   public String checkoutFileOK()
   {
      String outcome = null;
      
      Node node = getWorkingDocument();
      if (node != null)
      {
         // clean up and clear action context
         clearUpload();
         setDocument(null);
         setWorkingDocument(null);
         
         // refresh the UI, and setting the outcome will show the browse view
         UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
         
         outcome = "browse";
      }
      else
      {
         logger.warn("WARNING: checkoutFileOK called without a current WorkingDocument!");
      }
      
      return outcome;
   }
   
   /**
    * Action to undo the checkout of a document and return to the browse screen.
    */
   public String undoCheckout()
   {
      String outcome = null;
      
      Node node = getWorkingDocument();
      if (node != null)
      {
         try
         {
            // try to cancel checkout of the working copy
            this.versionOperationsService.cancelCheckout(node.getNodeRef());
            
            clearUpload();
            
            // refresh the UI, and setting the outcome will show the browse view
            UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
            
            outcome = "browse";
         }
         catch (Throwable err)
         {
            Utils.addErrorMessage("Unable to cancel checkout of Content Node due to system error: " + err.getMessage(), err);
         }
      }
      else
      {
         logger.warn("WARNING: undoCheckout called without a current WorkingDocument!");
      }
      
      return outcome;
   }
   
   /**
    * Action called upon completion of the Check In file page
    */
   public String checkinFileOK()
   {
      String outcome = null;
      
      UserTransaction tx = null;
      
      // NOTE: for checkin the document node _is_ the working document!
      Node node = getDocument();
      if (node != null && (getCopyLocation().equals(COPYLOCATION_CURRENT) || this.getFileName() != null))
      {
         try
         {
            tx = (UserTransaction)FacesContextUtils.getRequiredWebApplicationContext(
                  FacesContext.getCurrentInstance()).getBean(Repository.USER_TRANSACTION);
            tx.begin();
            
            if (logger.isDebugEnabled())
               logger.debug("Trying to checkin content node Id: " + node.getId());
            
            // get the original name and the working copy name
            String origNameProp = (String)node.getProperties().get("originalName");
            String nameProp = node.getName();
            
            // switch the name back to the original name before checkin
            // otherwise the working copy name will get set on the original doc!
            this.nodeService.setProperty(node.getNodeRef(), QNAME_NAME, origNameProp);
            
            // we can either checkin the content from the current working copy node
            // which would have been previously updated by the user
            String contentUrl;
            if (getCopyLocation().equals(COPYLOCATION_CURRENT))
            {
               contentUrl = (String)node.getProperties().get("contentUrl");
            }
            // or specify a specific file as the content instead
            else
            {
               // add the content to a repo temp writer location
               // we can then retrieve the URL to the content to to be set on the node during checkin
               ContentWriter tempWriter = this.contentService.getTempWriter();
               tempWriter.putContent(this.file);
               contentUrl = tempWriter.getContentUrl();
            }
            
            if (contentUrl == null || contentUrl.length() == 0)
            {
               throw new IllegalStateException("Content URL is empty for specified working copy content node!");
            }
            
            // TODO: what props should we add here? - e.g. version history text
            Map<String, Serializable> props = new HashMap<String, Serializable>(1, 1.0f);
            props.put(Version.PROP_DESCRIPTION, this.versionNotes);
            NodeRef originalDoc = this.versionOperationsService.checkin(
                  node.getNodeRef(),
                  props,
                  contentUrl, 
                  this.keepCheckedOut);
            
            // restore working copy name after checkin copy opp
            if (this.keepCheckedOut == true)
            {
               this.nodeService.setProperty(node.getNodeRef(), QNAME_NAME, nameProp);
            }
            
            // commit the transaction
            tx.commit();
            
            // clear action context
            setDocument(null);
            clearUpload();
            
            // refresh the UI, setting the outcome will show the browse view
            UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
            
            outcome = "browse";
         }
         catch (Throwable err)
         {
            // rollback the transaction
            try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
            Utils.addErrorMessage("Unable to check-in Content Node due to system error: " + err.getMessage(), err);
         }
      }
      else
      {
         logger.warn("WARNING: checkinFileOK called without a current Document!");
      }
      
      return outcome;
   }
   
   /**
    * Action called upon completion of the Update File page
    */
   public String updateFileOK()
   {
      String outcome = null;
      
      UserTransaction tx = null;
      
      // NOTE: for update the document node _is_ the working document!
      Node node = getDocument();
      if (node != null && this.getFileName() != null)
      {
         try
         {
            tx = (UserTransaction)FacesContextUtils.getRequiredWebApplicationContext(
                  FacesContext.getCurrentInstance()).getBean(Repository.USER_TRANSACTION);
            tx.begin();
            
            if (logger.isDebugEnabled())
               logger.debug("Trying to update content node Id: " + node.getId());
            
            // get an updating writer that we can use to modify the content on the current node
            ContentWriter writer = this.contentService.getUpdatingWriter(node.getNodeRef());
            writer.putContent(this.file);
            
            // commit the transaction
            tx.commit();
            
            // clear action context
            setDocument(null);
            clearUpload();
            
            // refresh the UI, setting the outcome will show the browse view
            UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
            
            outcome = "browse";
         }
         catch (Throwable err)
         {
            // rollback the transaction
            try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
            Utils.addErrorMessage("Unable to update Content Node due to system error: " + err.getMessage(), err);
         }
      }
      else
      {
         logger.warn("WARNING: updateFileOK called without a current Document!");
      }
      
      return outcome;
   }
   
   /**
    * Deals with the cancel button being pressed on the check in file page
    */
   public String cancel()
   {
      // reset the state
      clearUpload();
      
      return "browse";
   }
   
   /**
    * Clear form state and upload file bean
    */
   private void clearUpload()
   {
      // delete the temporary file we uploaded earlier
      if (this.file != null)
      {
         this.file.delete();
      }
      
      this.file = null;
      this.fileName = null;
      this.keepCheckedOut = false;
      this.copyLocation = COPYLOCATION_CURRENT;
      this.versionNotes = "";
      
      // remove the file upload bean from the session
      FacesContext ctx = FacesContext.getCurrentInstance();
      FileUploadBean fileBean = (FileUploadBean)ctx.getExternalContext().getSessionMap().remove(FileUploadBean.FILE_UPLOAD_BEAN_NAME);
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   private static Logger logger = Logger.getLogger(CheckinCheckoutBean.class);
   
   private static final String WORKING_COPY = " (working copy)";
   
   /** constants for copy location selection */
   private static final String COPYLOCATION_CURRENT = "current";
   private static final String COPYLOCATION_OTHER   = "other";
   
   /** well known QName values */
   private static final QName QNAME_NAME = QName.createQName(NamespaceService.ALFRESCO_URI, "name");
   private static final QName QNAME_ORIGINALNAME = QName.createQName(NamespaceService.ALFRESCO_URI, "originalName");
   
   /** The current document */
   private Node document;
   
   /** The working copy of the document we are checking out */
   private Node workingDocument;
   
   /** transient form and upload properties */
   private File file;
   private String fileName;
   private boolean keepCheckedOut = false;
   private String copyLocation = COPYLOCATION_CURRENT;
   private String versionNotes = "";
   
   /** The BrowseBean to be used by the bean */
   private BrowseBean browseBean;
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** The VersionOperationsService to be used by the bean */
   private VersionOperationsService versionOperationsService;
   
   /** The ContentService to be used by the bean */
   private ContentService contentService;
}
