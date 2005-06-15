package org.alfresco.web.bean;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.web.bean.repository.Node;

/**
 * Backing bean providing access to the details of a document
 * 
 * @author gavinc
 */
public class DocumentDetailsBean
{
   private BrowseBean browseBean;
   private NodeService nodeService;
   
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
      return null;
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
}
