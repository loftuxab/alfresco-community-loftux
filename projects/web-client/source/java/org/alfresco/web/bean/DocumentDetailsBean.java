/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.bean;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.app.servlet.DownloadContentServlet;
import org.alfresco.web.bean.repository.MapNode;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.wizard.NewRuleWizard;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.repo.component.template.TemplateNode;
import org.apache.log4j.Logger;

/**
 * Backing bean providing access to the details of a document
 * 
 * @author gavinc
 */
public class DocumentDetailsBean
{
   private static Logger logger = Logger.getLogger(DocumentDetailsBean.class);
   
   private String category;
   private BrowseBean browseBean;
   private NodeService nodeService;
   private LockService lockService;
   private CopyService copyService;
   private VersionService versionService;
   private NavigationBean navigator;
   
   private Map<String, String> workflowProperties;

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
    * Returns the URL to download content for the current document
    * 
    * @return Content url to download the current document
    */
   public String getUrl()
   {
      return (String)getDocument().getProperties().get("url");
   }
   
   /**
    * Returns the URL to the content for the current document
    *  
    * @return Content url to the current document
    */
   public String getBrowserUrl()
   {
      return DownloadContentServlet.generateBrowserURL(getDocument().getNodeRef(), getDocument().getName());
   }
   
   /**
    * Returns the current category for this document
    * 
    * @return The current category as an id
    */
   public String getCategory()
   {
      return this.category;
   }
   
   /**
    * Sets the category to be used by the current document
    * 
    * @param category The new category 
    */
   public void setCategory(String category)
   {
      this.category = category;
   }
   
   /**
    * Determines whether the current document is versionable
    * 
    * @return true if the document has the versionable aspect
    */
   public boolean isVersionable()
   {
      return getDocument().hasAspect(ContentModel.ASPECT_VERSIONABLE);
   }
   
   /**
    * @return true if the current document has the 'inlineeditable' aspect applied
    */
   public boolean isInlineEditable()
   {
      return getDocument().hasAspect(ContentModel.ASPECT_INLINEEDITABLE);
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
               clientVersion.put("url", DownloadContentServlet.generateDownloadURL(version.getNodeRef(), 
                     clientVersion.getName()));
               
               // add the client side version to the list
               versions.add(clientVersion);
            }
         }
      }
      
      return versions;
   }
   
   /**
    * Determines whether the current document has any categories applied
    * 
    * @return true if the document has categories attached
    */
   public boolean isCategorised()
   {
      return getDocument().hasAspect(ContentModel.ASPECT_GEN_CLASSIFIABLE);
   }
   
   /**
    * Returns a list of objects representing the categories applied to the 
    * current document
    *  
    * @return List of categories
    */
   public String getCategoriesOverviewHTML()
   {
      String html = null;
      
      if (isCategorised())
      {
         // we know for now that the general classifiable aspect only will be
         // applied so we can retrive the categories property direclty
         NodeRef catNode = (NodeRef)this.nodeService.getProperty(this.browseBean.getDocument().getNodeRef(), 
               ContentModel.PROP_CATEGORIES);
         
          if (catNode == null)
          {
             html = "This document does not yet have a category applied.";
          }
          else
          {
             html = "This document has the following category applied: " + 
                   Repository.getNameForNode(this.nodeService, catNode);
          }
      }
      
      return html;
   }

   /**
    * Event handler called to setup the category for editing
    * 
    * @param event The event
    */
   public void setupCategoryForEdit(ActionEvent event)
   {
      NodeRef catNode = (NodeRef)this.nodeService.getProperty(this.browseBean.getDocument().getNodeRef(), 
               ContentModel.PROP_CATEGORIES);
      
      if (catNode != null)
      {
         this.category = catNode.getId();
      }
      else
      {
         this.category = null;
      }
   }
   
   /**
    * Updates the category for the current document to the category stored
    * by this.newCategory
    *  
    * @return The outcome
    */
   public String saveCategory()
   {
      String outcome = "cancel";
      
      UserTransaction tx = null;
      
      try
      {
         FacesContext context = FacesContext.getCurrentInstance();
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         // firstly retrieve all the properties for the current node
         Map<QName, Serializable> updateProps = this.nodeService.getProperties(
               getDocument().getNodeRef());
         
         // create a node ref representation of the selected id and set the new properties
         updateProps.put(ContentModel.PROP_CATEGORIES, new NodeRef(Repository.getStoreRef(), this.category));
         
         // set the properties on the node
         this.nodeService.setProperties(getDocument().getNodeRef(), updateProps);
         
         // commit the transaction
         tx.commit();
         
         // reset the state of the current document so it reflects the changes just made
         getDocument().reset();
         
         outcome = "finish";
      }
      catch (Throwable e)
      {
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), "error_update_category"), e.getMessage()), e);
      }
      
      return outcome;
   }
   
   /**
    * Returns an overview summary of the current state of the attached
    * workflow (if any)
    * 
    * @return Summary HTML
    */
   public String getWorkflowOverviewHTML()
   {
      String html = null;
      
      if (getDocument().hasAspect(ContentModel.ASPECT_SIMPLE_WORKFLOW))
      {
         // get the simple workflow aspect properties
         Map<String, Object> props = getDocument().getProperties();

         String approveStepName = (String)props.get(
               ContentModel.PROP_APPROVE_STEP.toString());
         String rejectStepName = (String)props.get(
               ContentModel.PROP_REJECT_STEP.toString());
         
         Boolean approveMove = (Boolean)props.get(
               ContentModel.PROP_APPROVE_MOVE.toString());
         Boolean rejectMove = (Boolean)props.get(
               ContentModel.PROP_REJECT_MOVE.toString());
         
         NodeRef approveFolder = (NodeRef)props.get(
               ContentModel.PROP_APPROVE_FOLDER.toString());
         NodeRef rejectFolder = (NodeRef)props.get(
               ContentModel.PROP_REJECT_FOLDER.toString());
         
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
    * Returns the properties for the attached workflow as a map
    * 
    * @return Properties of the attached workflow, null if there is no workflow
    */
   public Map<String, String> getWorkflowProperties()
   {
      if (this.workflowProperties == null && 
          getDocument().hasAspect(ContentModel.ASPECT_SIMPLE_WORKFLOW))
      {
         // get the exisiting properties for the document
         Map<String, Object> props = getDocument().getProperties();
         
         String approveStepName = (String)props.get(
               ContentModel.PROP_APPROVE_STEP.toString());
         String rejectStepName = (String)props.get(
               ContentModel.PROP_REJECT_STEP.toString());
         
         Boolean approveMove = (Boolean)props.get(
               ContentModel.PROP_APPROVE_MOVE.toString());
         Boolean rejectMove = (Boolean)props.get(
               ContentModel.PROP_REJECT_MOVE.toString());
         
         NodeRef approveFolder = (NodeRef)props.get(
               ContentModel.PROP_APPROVE_FOLDER.toString());
         NodeRef rejectFolder = (NodeRef)props.get(
               ContentModel.PROP_REJECT_FOLDER.toString());

         // put the workflow properties in a separate map for use by the JSP
         this.workflowProperties = new HashMap<String, String>(6);
         this.workflowProperties.put(NewRuleWizard.PROP_APPROVE_STEP_NAME, 
               approveStepName);
         this.workflowProperties.put(NewRuleWizard.PROP_APPROVE_ACTION, 
               approveMove ? "move" : "copy");
         this.workflowProperties.put(NewRuleWizard.PROP_APPROVE_FOLDER, 
               approveFolder.getId());
         
         if (rejectStepName == null || rejectMove == null || rejectFolder == null)
         {
            this.workflowProperties.put(NewRuleWizard.PROP_REJECT_STEP_PRESENT, 
                  "no");
         }
         else
         {
            this.workflowProperties.put(NewRuleWizard.PROP_REJECT_STEP_PRESENT, 
                  "yes");
            this.workflowProperties.put(NewRuleWizard.PROP_REJECT_STEP_NAME, 
                  rejectStepName);
            this.workflowProperties.put(NewRuleWizard.PROP_REJECT_ACTION, 
                  rejectMove ? "move" : "copy");
            this.workflowProperties.put(NewRuleWizard.PROP_REJECT_FOLDER, 
                  rejectFolder.getId());
         }
      }
      
      return this.workflowProperties;
   }
   
   /**
    * Saves the details of the workflow stored in workflowProperties
    * to the current document
    *  
    * @return The outcome string
    */
   public String saveWorkflow()
   {
      String outcome = "cancel";
      
      UserTransaction tx = null;
      
      try
      {
         FacesContext context = FacesContext.getCurrentInstance();
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         // firstly retrieve all the properties for the current node
         Map<QName, Serializable> updateProps = this.nodeService.getProperties(
               getDocument().getNodeRef());
         
         // update the simple workflow properties
         
         // set the approve step name
         updateProps.put(ContentModel.PROP_APPROVE_STEP,
               this.workflowProperties.get(NewRuleWizard.PROP_APPROVE_STEP_NAME));
         
         // specify whether the approve step will copy or move the content
         boolean approveMove = true;
         String approveAction = this.workflowProperties.get(NewRuleWizard.PROP_APPROVE_ACTION);
         if (approveAction != null && approveAction.equals("copy"))
         {
            approveMove = false;
         }
         updateProps.put(ContentModel.PROP_APPROVE_MOVE, new Boolean(approveMove));
         
         // create node ref representation of the destination folder
         NodeRef approveDestNodeRef = new NodeRef(Repository.getStoreRef(), 
                  this.workflowProperties.get(NewRuleWizard.PROP_APPROVE_FOLDER));
         updateProps.put(ContentModel.PROP_APPROVE_FOLDER, approveDestNodeRef);
         
         // determine whether there should be a reject step
         boolean requireReject = true;
         String rejectStepPresent = this.workflowProperties.get(
               NewRuleWizard.PROP_REJECT_STEP_PRESENT);
         if (rejectStepPresent != null && rejectStepPresent.equals("no"))
         {
            requireReject = false;
         }
         
         if (requireReject)
         {
            // set the reject step name
            updateProps.put(ContentModel.PROP_REJECT_STEP,
                  this.workflowProperties.get(NewRuleWizard.PROP_REJECT_STEP_NAME));
         
            // specify whether the reject step will copy or move the content
            boolean rejectMove = true;
            String rejectAction = this.workflowProperties.get(
                  NewRuleWizard.PROP_REJECT_ACTION);
            if (rejectAction != null && rejectAction.equals("copy"))
            {
               rejectMove = false;
            }
            updateProps.put(ContentModel.PROP_REJECT_MOVE, new Boolean(rejectMove));

            // create node ref representation of the destination folder
            NodeRef rejectDestNodeRef = new NodeRef(Repository.getStoreRef(), 
                  this.workflowProperties.get(NewRuleWizard.PROP_REJECT_FOLDER));
            updateProps.put(ContentModel.PROP_REJECT_FOLDER, rejectDestNodeRef);
         }
         else
         {
            // set all the reject properties to null to signify there should
            // be no reject step
            updateProps.put(ContentModel.PROP_REJECT_STEP, null);
            updateProps.put(ContentModel.PROP_REJECT_MOVE, null);
            updateProps.put(ContentModel.PROP_REJECT_FOLDER, null);
         }
         
         // set the properties on the node
         this.nodeService.setProperties(getDocument().getNodeRef(), updateProps);
         
         // commit the transaction
         tx.commit();
         
         // reset the state of the current document so it reflects the changes just made
         getDocument().reset();
         
         outcome = "finish";
      }
      catch (Throwable e)
      {
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), "error_update_simpleworkflow"), e.getMessage()), e);
      }
      
      return outcome;
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
         approveStepName = (String)getDocument().getProperties().get(
               ContentModel.PROP_APPROVE_STEP.toString());
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
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id == null || id.length() == 0)
      {
         throw new AlfrescoRuntimeException("approve called without an id");
      }
      
      NodeRef docNodeRef = new NodeRef(Repository.getStoreRef(), id);
      Node docNode = new Node(docNodeRef, this.nodeService);
      
      if (docNode.hasAspect(ContentModel.ASPECT_SIMPLE_WORKFLOW) == false)
      {
         throw new AlfrescoRuntimeException("You can not approve a document that is not part of a workflow");
      }
      
      // get the simple workflow aspect properties
      Map<String, Object> props = docNode.getProperties();
      
      Boolean approveMove = (Boolean)props.get(ContentModel.PROP_APPROVE_MOVE.toString());
      NodeRef approveFolder = (NodeRef)props.get(ContentModel.PROP_APPROVE_FOLDER.toString());
      
      UserTransaction tx = null;
      try
      {
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         // first we need to take off the simpleworkflow aspect
         this.nodeService.removeAspect(docNodeRef, ContentModel.ASPECT_SIMPLE_WORKFLOW);
         
         if (approveMove.booleanValue())
         {
            // move the document to the specified folder
            String qname = QName.createValidLocalName(docNode.getName());
            this.nodeService.moveNode(docNodeRef, approveFolder, ContentModel.ASSOC_CONTAINS,
                  QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, qname));
         }
         else
         {
            // copy the document to the specified folder
            String qname = QName.createValidLocalName(docNode.getName());
            this.copyService.copy(docNodeRef, approveFolder, ContentModel.ASSOC_CONTAINS,
                  QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, qname));
         }
         
         // commit the transaction
         tx.commit();
         
         // if this was called via the document details dialog we need to reset the document node
         if (getDocument() != null)
         {
            getDocument().reset();
         }
         
         // also make sure the UI will get refreshed
         UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
         
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
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), "error_workflow_approve"), e.getMessage()), e);
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
         approveStepName = (String)getDocument().getProperties().get(
               ContentModel.PROP_REJECT_STEP.toString());
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
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id == null || id.length() == 0)
      {
         throw new AlfrescoRuntimeException("reject called without an id");
      }
      
      NodeRef docNodeRef = new NodeRef(Repository.getStoreRef(), id);
      Node docNode = new Node(docNodeRef, this.nodeService);
      
      if (docNode.hasAspect(ContentModel.ASPECT_SIMPLE_WORKFLOW) == false)
      {
         throw new AlfrescoRuntimeException("You can not reject a document that is not part of a workflow");
      }
      
      // get the simple workflow aspect properties
      Map<String, Object> props = docNode.getProperties();
      
      String rejectStep = (String)props.get(ContentModel.PROP_REJECT_STEP.toString());
      Boolean rejectMove = (Boolean)props.get(ContentModel.PROP_REJECT_MOVE.toString());
      NodeRef rejectFolder = (NodeRef)props.get(ContentModel.PROP_REJECT_FOLDER.toString());
      
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
         this.nodeService.removeAspect(docNodeRef, ContentModel.ASPECT_SIMPLE_WORKFLOW);
         
         if (rejectMove.booleanValue())
         {
            // move the document to the specified folder
            String qname = QName.createValidLocalName(docNode.getName());
            this.nodeService.moveNode(docNodeRef, rejectFolder, ContentModel.ASSOC_CONTAINS,
                  QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, qname));
         }
         else
         {
            // copy the document to the specified folder
            String qname = QName.createValidLocalName(docNode.getName());
            this.copyService.copy(docNodeRef, rejectFolder, ContentModel.ASSOC_CONTAINS,
                  QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, qname));
         }
         
         // commit the transaction
         tx.commit();
         
         // if this was called via the document details dialog we need to reset the document node
         if (getDocument() != null)
         {
            getDocument().reset();
         }
         
         // also make sure the UI will get refreshed
         UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
         
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
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), "error_workflow_reject"), e.getMessage()), e);
      }
   }
   
   /**
    * Applies the classifiable aspect to the current document
    */
   public void applyClassifiable()
   {
      UserTransaction tx = null;
      
      try
      {
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         // add the general classifiable aspect to the node
         this.nodeService.addAspect(getDocument().getNodeRef(), ContentModel.ASPECT_GEN_CLASSIFIABLE, null);
         
         // commit the transaction
         tx.commit();
         
         // reset the state of the current document
         getDocument().reset();
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), "error_aspect_classify"), e.getMessage()), e);
      }
   }
   
   /**
    * Applies the versionable aspect to the current document
    */
   public void applyVersionable()
   {
      UserTransaction tx = null;
      
      try
      {
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         // add the versionable aspect to the node
         this.nodeService.addAspect(getDocument().getNodeRef(), ContentModel.ASPECT_VERSIONABLE, null);
         
         // commit the transaction
         tx.commit();
         
         // reset the state of the current document
         getDocument().reset();
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), "error_aspect_versioning"), e.getMessage()), e);
      }
   }
   
   /**
    * Applies the inlineeditable aspect to the current document
    */
   public String applyInlineEditable()
   {
      UserTransaction tx = null;
      
      try
      {
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         // add the inlineeditable aspect to the node
         Map<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
         String contentType = (String)getDocument().getProperties().get("mimetype");
         
         // set the property to true by default if the filetype is text/HTML content
         if (MimetypeMap.MIMETYPE_HTML.equals(contentType) ||
             MimetypeMap.MIMETYPE_TEXT_PLAIN.equals(contentType) ||
             MimetypeMap.MIMETYPE_XML.equals(contentType) ||
             MimetypeMap.MIMETYPE_TEXT_CSS.equals(contentType))
         {
            props.put(ContentModel.PROP_EDITINLINE, true);
         }
         this.nodeService.addAspect(getDocument().getNodeRef(), ContentModel.ASPECT_INLINEEDITABLE, props);
         
         // commit the transaction
         tx.commit();
         
         // reset the state of the current document
         getDocument().reset();
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), "error_aspect_inlineeditable"), e.getMessage()), e);
      }
      
      // force recreation of the details view - this means the properties sheet component will reinit
      return "showDocDetails";
   }
   
   /**
    * Navigates to next item in the list of content for the current Space
    */
   public void nextItem(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         List<Node> nodes = this.browseBean.getContent();
         if (nodes.size() > 1)
         {
            // perform a linear search - this is slow but stateless
            // otherwise we would have to manage state of last selected node
            // this gets very tricky as this bean is instantiated once and never
            // reset - it does not know when the document has changed etc.
            for (int i=0; i<nodes.size(); i++)
            {
               if (id.equals(nodes.get(i).getId()) == true)
               {
                  Node next;
                  // found our item - navigate to next
                  if (i != nodes.size() - 1)
                  {
                     next = nodes.get(i + 1);
                  }
                  else
                  {
                     // handle wrapping case
                     next = nodes.get(0);
                  }
                  
                  // prepare for showing details for this node
                  this.browseBean.setupContentAction(next.getId(), false);
                  break;
               }
            }
         }
      }
   }
   
   /**
    * Navigates to the previous item in the list of content for the current Space
    */
   public void previousItem(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         List<Node> nodes = this.browseBean.getContent();
         if (nodes.size() > 1)
         {
            // see above
            for (int i=0; i<nodes.size(); i++)
            {
               if (id.equals(nodes.get(i).getId()) == true)
               {
                  Node previous;
                  // found our item - navigate to previous
                  if (i != 0)
                  {
                     previous = nodes.get(i - 1);
                  }
                  else
                  {
                     // handle wrapping case
                     previous = nodes.get(nodes.size() - 1);
                  }
                  
                  // prepare for showing details for this node
                  this.browseBean.setupContentAction(previous.getId(), false);
                  break;
               }
            }
         }
      }
   }
   
   /**
    * Returns a model for use by a template on the Document Details page.
    * 
    * @return model containing current document and current space info.
    */
   public Map getTemplateModel()
   {
      HashMap model = new HashMap(3, 1.0f);
      
      TemplateNode documentNode = new TemplateNode(getDocument().getNodeRef(), this.nodeService);
      model.put("document", documentNode);
      TemplateNode spaceNode = new TemplateNode(this.navigator.getCurrentNode().getNodeRef(), this.nodeService);
      model.put("space", spaceNode);
      
      return model;
   }
   
   /**
    * Returns whether the current document is locked
    * 
    * @return true if the document is checked out
    */
   public boolean isLocked()
   {
      return Repository.isNodeLocked(getDocument(), this.lockService);
   }
   
   /**
    * Returns whether the current document is a working copy
    * 
    * @return true if the document is a working copy
    */
   public boolean isWorkingCopy()
   {
      return getDocument().hasAspect(ContentModel.ASPECT_WORKING_COPY);
   }
   
   /**
    * Returns whether the current document is a working copy owned by the current User
    * 
    * @return true if the document is a working copy owner by the current User
    */
   public boolean isOwner()
   {
      return Repository.isNodeOwner(getDocument(), this.lockService);
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
   
   /**
    * @param navigator The NavigationBean to set.
    */
   public void setNavigator(NavigationBean navigator)
   {
      this.navigator = navigator;
   }
}
