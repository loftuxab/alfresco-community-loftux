package org.alfresco.web.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.servlet.DownloadContentServlet;
import org.alfresco.web.bean.repository.MapNode;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.apache.log4j.Logger;

/**
 * Backing bean providing access to the details of a document
 * 
 * @author gavinc
 */
public class DocumentDetailsBean
{
   private static Logger logger = Logger.getLogger(DocumentDetailsBean.class);
   
   private BrowseBean browseBean;
   private NodeService nodeService;
   private LockService lockService;
   private CopyService copyService;
   private VersionService versionService;
   
   /**
    * Returns the id of the current document
    * 
    * @return The id
    */
   public String getId()
   {
      return getDocument().getId();
   }
   
   /**
    * Returns the name of the current document
    * 
    * @return Name of the current document
    */
   public String getName()
   {
      return getDocument().getName();
   }
   
   /**
    * Returns the URL to the content for the current document
    * 
    * @return Content url of the current document
    */
   public String getUrl()
   {
      return (String)getDocument().getProperties().get("url");
   }
   
   /**
    * Returns a list of objects representing the versions of the 
    * current document 
    * 
    * @return List of previous versions
    */
   public List getVersionHistory()
   {
      List<MapNode> versions = new ArrayList<MapNode>();
      
      if (getDocument().hasAspect(ContentModel.ASPECT_VERSIONABLE))
      {
         VersionHistory history = this.versionService.getVersionHistory(getDocument().getNodeRef());
   
         if (history != null)
         {
            for (Version version : history.getAllVersions())
            {
               // create a map node representation of the version
               MapNode clientVersion = new MapNode(version.getNodeRef(), this.nodeService);
               clientVersion.put("versionLabel", version.getVersionLabel());
               clientVersion.put("author", clientVersion.get("creator"));
               clientVersion.put("versionDate", version.getCreatedDate());
               clientVersion.put("url", DownloadContentServlet.generateURL(version.getNodeRef(), 
                     clientVersion.getName()));
               
               // add the client side version to the list
               versions.add(clientVersion);
            }
         }
      }
      
      return versions;
   }
   
   /**
    * Returns a list of objects representing the categories applied to the 
    * current document
    *  
    * @return List of categories
    */
   public List getCategories()
   {
      return null;
   }

   /**
    * Returns an overview summary of the current state of the attached
    * workflow (if any)
    * 
    * @return Summary HTML
    */
   public String getWorkflowOverviewHTML()
   {
      String html = "This document is not part of any workflow.";
      
      if (getDocument().hasAspect(ContentModel.ASPECT_SIMPLE_WORKFLOW))
      {
         // get the simple workflow aspect properties
         Map<String, Object> props = getDocument().getProperties();
         
         String approveStepName = (String)props.get("approvestep");
         String rejectStepName = (String)props.get("rejectstep");
         
         Boolean approveMove = (Boolean)props.get("approvemove");
         Boolean rejectMove = (Boolean)props.get("rejectmove");
         
         NodeRef approveFolder = (NodeRef)props.get("approvefolder");
         NodeRef rejectFolder = (NodeRef)props.get("rejectfolder");
         
         String approveFolderName = null;
         String rejectFolderName = null;
         
         // get the approve folder name
         if (approveFolder != null)
         {
            Node node = new Node(approveFolder, this.nodeService);
            approveFolderName = node.getName();
         }
         
         // get the reject folder name
         if (rejectFolder != null)
         {
            Node node = new Node(rejectFolder, this.nodeService);
            rejectFolderName = node.getName();
         }
         
         StringBuilder builder = new StringBuilder();
         builder.append("The document will be ");
         if (approveMove.booleanValue())
         {
            builder.append("moved");
         }
         else
         {
            builder.append("copied");
         }
         builder.append(" to '");
         builder.append(approveFolderName);
         builder.append("' if the '");
         builder.append(approveStepName);
         builder.append("' action is taken.");
         
         // add details of the reject step if there is one
         if (rejectStepName != null && rejectMove != null && rejectFolderName != null)
         {
            builder.append("<p>Alternatively, the document will be ");
            if (rejectMove.booleanValue())
            {
               builder.append("moved");
            }
            else
            {
               builder.append("copied");
            }
            builder.append(" to '");
            builder.append(rejectFolderName);
            builder.append("' if the '");
            builder.append(rejectStepName);
            builder.append("' action is taken.");
         }
         
         html = builder.toString();
      }
         
      return html;
   }
   
   /**
    * Returns the name of the approve step of the attached workflow
    * 
    * @return The name of the approve step or null if there is no workflow
    */
   public String getApproveStepName()
   {
      String approveStepName = null;
      
      if (getDocument().hasAspect(ContentModel.ASPECT_SIMPLE_WORKFLOW))
      {
         approveStepName = (String)getDocument().getProperties().get("approvestep");
      }
      
      return approveStepName; 
   }
   
   /**
    * Event handler called to handle the approve step of the simple workflow
    * 
    * @param event The event that was triggered
    */
   public void approve(ActionEvent event)
   {
      if (getDocument().hasAspect(ContentModel.ASPECT_SIMPLE_WORKFLOW) == false)
      {
         throw new AlfrescoRuntimeException("You can not approve a document that is not part of a workflow");
      }
      
      // get the simple workflow aspect properties
      Map<String, Object> props = getDocument().getProperties();
      
      Boolean approveMove = (Boolean)props.get("approvemove");
      NodeRef approveFolder = (NodeRef)props.get("approvefolder");
      
      UserTransaction tx = null;
      try
      {
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         // first we need to take off the simpleworkflow aspect
         this.nodeService.removeAspect(getDocument().getNodeRef(), ContentModel.ASPECT_SIMPLE_WORKFLOW);
         
         if (approveMove.booleanValue())
         {
            // move the document to the specified folder
            String qname = QName.createValidLocalName(getDocument().getName());
            this.nodeService.moveNode(getDocument().getNodeRef(), approveFolder, ContentModel.ASSOC_CONTAINS,
                  QName.createQName(NamespaceService.ALFRESCO_URI, qname));
         }
         else
         {
            // copy the document to the specified folder
            String qname = QName.createValidLocalName(getDocument().getName());
            this.copyService.copy(getDocument().getNodeRef(), approveFolder, ContentModel.ASSOC_CONTAINS,
                  QName.createQName(NamespaceService.ALFRESCO_URI, qname));
         }
         
         // commit the transaction
         tx.commit();
         
         // reset the document node
         getDocument().reset();
         
         if (logger.isDebugEnabled())
         {
            String movedCopied = approveMove ? "moved" : "copied";
            logger.debug("Document has been approved and " + movedCopied + " to folder with id of " + 
                  approveFolder.getId());
         }
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         throw new AlfrescoRuntimeException("Failed to approve the document", e);
      }
   }
   
   /**
    * Returns the name of the reject step of the attached workflow
    * 
    * @return The name of the reject step or null if there is no workflow
    */
   public String getRejectStepName()
   {
      String approveStepName = null;
      
      if (getDocument().hasAspect(ContentModel.ASPECT_SIMPLE_WORKFLOW))
      {
         approveStepName = (String)getDocument().getProperties().get("rejectstep");
      }
      
      return approveStepName;
   }
   
   /**
    * Event handler called to handle the approve step of the simple workflow
    * 
    * @param event The event that was triggered
    */
   public void reject(ActionEvent event)
   {
      if (getDocument().hasAspect(ContentModel.ASPECT_SIMPLE_WORKFLOW) == false)
      {
         throw new AlfrescoRuntimeException("You can not reject a document that is not part of a workflow");
      }
      
      // get the simple workflow aspect properties
      Map<String, Object> props = getDocument().getProperties();
      
      String rejectStep = (String)props.get("rejectstep");
      Boolean rejectMove = (Boolean)props.get("rejectmove");
      NodeRef rejectFolder = (NodeRef)props.get("rejectfolder");
      
      if (rejectStep == null && rejectMove == null && rejectFolder == null)
      {
         throw new AlfrescoRuntimeException("The workflow does not have a reject step defined");
      }
      
      UserTransaction tx = null;
      try
      {
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         // first we need to take off the simpleworkflow aspect
         this.nodeService.removeAspect(getDocument().getNodeRef(), ContentModel.ASPECT_SIMPLE_WORKFLOW);
         
         if (rejectMove.booleanValue())
         {
            // move the document to the specified folder
            String qname = QName.createValidLocalName(getDocument().getName());
            this.nodeService.moveNode(getDocument().getNodeRef(), rejectFolder, ContentModel.ASSOC_CONTAINS,
                  QName.createQName(NamespaceService.ALFRESCO_URI, qname));
         }
         else
         {
            // copy the document to the specified folder
            String qname = QName.createValidLocalName(getDocument().getName());
            this.copyService.copy(getDocument().getNodeRef(), rejectFolder, ContentModel.ASSOC_CONTAINS,
                  QName.createQName(NamespaceService.ALFRESCO_URI, qname));
         }
         
         // commit the transaction
         tx.commit();
         
         // reset the document node
         getDocument().reset();
         
         if (logger.isDebugEnabled())
         {
            String movedCopied = rejectMove ? "moved" : "copied";
            logger.debug("Document has been rejected and " + movedCopied + " to folder with id of " + 
                  rejectFolder.getId());
         }
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         throw new AlfrescoRuntimeException("Failed to approve the document", e);
      }
   }
   
   /**
    * Returns whether the current document is locked
    * 
    * @return true if the document is checked out
    */
   public boolean getLocked()
   {
      return Repository.isNodeLocked(getDocument(), this.lockService);
   }
   
   /**
    * Returns whether the current document is a working copy
    * 
    * @return true if the document is a working copy
    */
   public boolean getWorkingCopy()
   {
      return getDocument().hasAspect(ContentModel.ASPECT_WORKING_COPY);
   }
   
   /**
    * Returns the document this bean is currently representing
    * 
    * @return The document Node
    */
   public Node getDocument()
   {
      return this.browseBean.getDocument();
   }
   
   /**
    * Sets the BrowseBean instance to use to retrieve the current document
    * 
    * @param browseBean BrowseBean instance
    */
   public void setBrowseBean(BrowseBean browseBean)
   {
      this.browseBean = browseBean;
   }

   /**
    * Sets the node service instance the bean should use
    * 
    * @param nodeService The NodeService
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }

   /**
    * Sets the lock service instance the bean should use
    * 
    * @param lockService The LockService
    */
   public void setLockService(LockService lockService)
   {
      this.lockService = lockService;
   }

   /**
    * Sets the version service instance the bean should use
    * 
    * @param versionService The VersionService
    */
   public void setVersionService(VersionService versionService)
   {
      this.versionService = versionService;
   }
   
   /**
    * Sets the copy service instance the bean should use
    * 
    * @param copyService The CopyService
    */
   public void setCopyService(CopyService copyService)
   {
      this.copyService = copyService;
   }
}
