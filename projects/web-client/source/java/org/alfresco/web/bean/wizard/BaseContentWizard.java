/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.bean.wizard;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.data.IDataContainer;
import org.alfresco.web.data.QuickSort;
import org.apache.log4j.Logger;

/**
 * Base Handler class used by the Content Wizards 
 * 
 * @author gavinc kevinr
 */
public abstract class BaseContentWizard extends AbstractWizardBean
{
   private static Logger logger = Logger.getLogger(BaseContentWizard.class);

   protected static final String FINISH_INSTRUCTION = "To add the content to this space click Finish.<br/>" +
                                                      "To review or change your selections click Back.";
   
   // content wizard specific attributes
   protected String fileName;
   protected String author;
   protected String title;
   protected String description;
   protected String contentType;
   protected List<SelectItem> contentTypes;
   protected ContentService contentService;
   
   
   /**
    * Save the specified content using the currently set wizard attributes
    * 
    * @param fileContent      File content to save
    * @param strContent       String content to save
    */
   protected void saveContent(File fileContent, String strContent)
   {
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
            
            // update the modified timestamp and other content props
            Map<QName, Serializable> contentProps = this.nodeService.getProperties(nodeRef);
            contentProps.put(ContentModel.PROP_NAME, this.fileName);
            contentProps.put(ContentModel.PROP_TITLE, this.title);
            contentProps.put(ContentModel.PROP_DESCRIPTION, this.description);
            contentProps.put(ContentModel.PROP_MIME_TYPE, this.contentType);
            contentProps.put(ContentModel.PROP_CREATOR, this.author);
            this.nodeService.setProperties(nodeRef, contentProps);
         }
         else
         {
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
            Map<QName, Serializable> contentProps = new HashMap<QName, Serializable>(3, 1.0f);
            contentProps.put(ContentModel.PROP_NAME, this.fileName);
            contentProps.put(ContentModel.PROP_ENCODING, "UTF-8");
            contentProps.put(ContentModel.PROP_MIME_TYPE, this.contentType);
            contentProps.put(ContentModel.PROP_CREATOR, this.author);
            
            // create the node to represent the node
            String assocName = QName.createValidLocalName(this.fileName);
            ChildAssociationRef assocRef = this.nodeService.createNode(
                  containerNodeRef,
                  ContentModel.ASSOC_CONTAINS,
                  QName.createQName(NamespaceService.ALFRESCO_URI, assocName),
                  ContentModel.TYPE_CONTENT,
                  contentProps);
            
            NodeRef fileNodeRef = assocRef.getChildRef();
            
            if (logger.isDebugEnabled())
               logger.debug("Created file node for file: " + this.fileName);
            
            // apply the titled aspect - title and description
            Map<QName, Serializable> titledProps = new HashMap<QName, Serializable>(5);
            titledProps.put(ContentModel.PROP_TITLE, this.title);
            titledProps.put(ContentModel.PROP_DESCRIPTION, this.description);
            this.nodeService.addAspect(fileNodeRef, ContentModel.ASPECT_TITLED, titledProps);
            
            if (logger.isDebugEnabled())
               logger.debug("Added titled aspect with properties: " + titledProps);
            
            // get a writer for the content and put the file
            ContentWriter writer = contentService.getUpdatingWriter(fileNodeRef);
            if (fileContent != null)
            {
               writer.putContent(fileContent);
            }
            else if (strContent != null)
            {
               writer.putContent(strContent);
            }
         }
         
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
      
      this.fileName = null;
      this.author = null;
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
      return this.fileName;
   }

   /**
    * @param fileName The name of the file
    */
   public void setFileName(String fileName)
   {
      this.fileName = fileName;
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
         ServiceRegistry registry = Repository.getServiceRegistry(FacesContext.getCurrentInstance());
         MimetypeService mimetypeService = registry.getMimetypeService();
         
         // get the mime type display names
         Map<String, String> mimeTypes = mimetypeService.getDisplaysByMimetype();
         for (String mimeType : mimeTypes.keySet())
         {
            this.contentTypes.add(new SelectItem(mimeType, mimeTypes.get(mimeType)));
         }
         
         // make sure the list is sorted by the values
         QuickSort sorter = new QuickSort(this.contentTypes, "label", true, IDataContainer.SORT_CASEINSENSITIVE);
         sorter.sort();
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
    * Returns the display label for the content type currently chosen
    * 
    * @return The human readable version of the content type
    */
   protected String getSummaryContentType()
   {
      ServiceRegistry registry = Repository.getServiceRegistry(FacesContext.getCurrentInstance());
      MimetypeService mimetypeService = registry.getMimetypeService();
         
      // get the mime type display name
      Map<String, String> mimeTypes = mimetypeService.getDisplaysByMimetype();
      return mimeTypes.get(this.contentType);
   }
}
