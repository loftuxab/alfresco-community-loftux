/*
 * Created on 20-May-2005
 */
package org.alfresco.web.bean;

import java.io.File;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.version.operations.VersionOperationsService;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.apache.log4j.Logger;

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
      
      Node node = getDocument();
      if (node != null)
      {
         try
         {
            if (logger.isDebugEnabled())
               logger.debug("Trying to checkout content node Id: " + node.getId());
            
            // checkout the node content to create a working copy
            // TODO: impl checkout to a arbituary parent Space 
            NodeRef workingCopyRef = this.versionOperationsService.checkout(node.getNodeRef());
            Node workingCopy = new Node(workingCopyRef, this.nodeService);
            
            // set the working copy Node instance
            setWorkingDocument(workingCopy);
            
            // TODO: need to get the content URL to the content service
            //       see Gavs work in Add Content wizard
            
            // TODO: show the page that display the checkout link
            
            outcome = "checkoutFileLink";
         }
         catch (Throwable err)
         {
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
         // TODO: clean up etc.
         
         // clear action context
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
            
            // refresh the UI, and setting the outcome will show the browse view
            UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
            
            outcome = "browse";
         }
         catch (Throwable err)
         {
            Utils.addErrorMessage("Unable to cancel checkout of Content Node due to system error: " + err.getMessage());
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
      
      Node node = getDocument();
      if (node != null)
      {
         try
         {
            if (logger.isDebugEnabled())
               logger.debug("Trying to unlock content node Id: " + node.getId());
            
            // checkin the node content
            // TODO: get this boolean from form input
            boolean keepCheckedOut = false;
            // TODO: where does this come from?
            String contentURL = null;
            this.versionOperationsService.checkin(
                  node.getNodeRef(),
                  Collections.<String, Serializable>emptyMap(),   // TODO: do we need to add stuff here?
                  contentURL, 
                  keepCheckedOut);
            
            // clear action context
            setDocument(null);
            setWorkingDocument(null);
            
            clearUpload();
            
            // refresh the UI, setting the outcome will show the browse view
            UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
            
            outcome = "browse";
         }
         catch (Throwable err)
         {
            Utils.addErrorMessage("Unable to checkout Content Node due to system error: " + err.getMessage());
         }
      }
      else
      {
         logger.warn("WARNING: checkinFileOK called without a current Document!");
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
   
   private void clearUpload()
   {
      // delete the temporary file we uploaded earlier
      if (this.file != null)
      {
         this.file.delete();
      }
      
      this.file = null;
      this.fileName = null;
      
      // remove the file upload bean from the session
      FacesContext ctx = FacesContext.getCurrentInstance();
      FileUploadBean fileBean = (FileUploadBean)ctx.getExternalContext().getSessionMap().remove(FileUploadBean.FILE_UPLOAD_BEAN_NAME);
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   private static Logger logger = Logger.getLogger(CheckinCheckoutBean.class);
   
   /** The current document */
   private Node document;
   
   /** The working copy of the document we are checking out */
   private Node workingDocument;
   
   private File file;
   
   private String fileName;
   
   /** The BrowseBean to be used by the bean */
   private BrowseBean browseBean;
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** The VersionOperationsService to be used by the bean */
   private VersionOperationsService versionOperationsService;
}
