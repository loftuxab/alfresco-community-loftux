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
package org.alfresco.repo.webdav;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Implements the WebDAV COPY method
 * 
 * @author gavinc
 */
public class CopyMethod extends HierarchicalMethod
{
   /**
    * Default constructor
    */
   public CopyMethod()
   {
   }   
   
   /**
    * Exceute the request
    * 
    * @exception WebDAVServerException
    */
   protected void executeImpl() throws WebDAVServerException
   {
       NodeService nodeService = getNodeService();
       int srcSts = WebDAVHelper.NotExist;
       int destSts = WebDAVHelper.NotExist;
       
       try {
           
           // Debug
           
           if ( logger.isDebugEnabled())
               logger.debug("Copy from " + getPath() + " to " + getDestinationPath());
           
           // Check if the source node exists
           
           srcSts = getDAVHelper().getPathStatus(getRootNodeRef(), getPath());
           if ( srcSts == WebDAVHelper.NotExist)
           {
               throw new WebDAVServerException(HttpServletResponse.SC_NOT_FOUND); 
           }

           // Check if the destination exists
           
           destSts = getDAVHelper().getPathStatus(getRootNodeRef(), getDestinationPath());
           
           if ( hasOverWrite() == false && destSts != WebDAVHelper.NotExist)
           {
               throw new WebDAVServerException(HttpServletResponse.SC_PRECONDITION_FAILED);
           }
           
           // Copy the content or folder node
           
           if ( srcSts == WebDAVHelper.FileExists)
           {
               // Perform a single content node copy
               
               copyContentNode( destSts);
           }
           else
           {
               // Copy a folder node and its child nodes
               
               copyFolderNode( destSts);
           }
           
           // Set the response status

           m_response.setStatus(destSts == WebDAVHelper.NotExist ? HttpServletResponse.SC_CREATED :
               HttpServletResponse.SC_NO_CONTENT);
       }
       catch (AlfrescoRuntimeException ex)
       {
           throw new WebDAVServerException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex); 
       }
   }
   
   /**
    * Copy a single content node to a new location
    * 
    * @param destSts int
    * @exception WebDAVServerException
    */
   private final void copyContentNode(int destSts)
       throws WebDAVServerException
   {
       // Split the path into path and file name

       String[] paths = getDAVHelper().splitPath(getDestinationPath());

       if (paths[1] == null)
       {
           // Bad path

           throw new WebDAVServerException(HttpServletResponse.SC_BAD_REQUEST);
       }
       
       // Get the parent folder node

       NodeRef parentNode = getDAVHelper().getNodeForPath(getRootNodeRef(), paths[0], m_request.getServletPath());

       // Create a new node or version

       NodeRef newNode = getDAVHelper().createNode(parentNode, paths[1], true);

       // Get the existing content node
       
       NodeRef curNode = getDAVHelper().getNodeForPath(getRootNodeRef(), getPath(), m_request.getServletPath());
       
       // Access the content

       ContentService contentService = getContentService();
       
       ContentWriter contentWriter = contentService.getWriter(newNode, ContentModel.PROP_CONTENT, false);
       ContentReader contentReader = contentService.getReader(curNode, ContentModel.PROP_CONTENT);
       
       // Copy the content from the existing node to the new node
       
       contentWriter.putContent(contentReader);
   }
   
   /**
    * Copy a folder node and its child nodes to a new location
    *
    * @param destSts int
    * @exception WebDAVServerException
    */
   private final void copyFolderNode(int destSts)
       throws WebDAVServerException
   {
       // We only support tree copy in overwrite mode when the destination exists
       
       if ( hasOverWrite() == false && destSts == WebDAVHelper.FolderExists)
           throw new WebDAVServerException(HttpServletResponse.SC_PRECONDITION_FAILED);
       
       // Create the destination path if it does not exist, create all folders missing from the path
       
       NodeRef destNode = getDAVHelper().makePath(getRootNodeRef(), getDestinationPath());

       if ( destNode == null)
           throw new WebDAVServerException(HttpServletResponse.SC_PRECONDITION_FAILED);

       // Get the source node
       
       NodeRef srcNode = getDAVHelper().getNodeForPath(getRootNodeRef(), getPath(), m_request.getServletPath());

       if ( srcNode == null)
           throw new WebDAVServerException(HttpServletResponse.SC_PRECONDITION_FAILED);
       
       // Copy using the copy service
       
       try {
           
           // Get the destination folder name
           
           String[] paths = getDAVHelper().splitAllPaths(getDestinationPath());
           String fname = paths[paths.length - 1];
           String encodedPath = QName.createValidLocalName(fname);
           
           // Copy the tree
           
           CopyService copyService = getDAVHelper().getCopyService();
           
           NodeRef newNode = copyService.copy(srcNode, destNode, ContentModel.ASSOC_CONTAINS,
                               QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, encodedPath), true);
           
           // Update the name property on the new root node
           
           NodeService nodeService = getDAVHelper().getNodeService();
           nodeService.setProperty(newNode, ContentModel.PROP_NAME, fname);
       }
       catch ( Exception ex)
       {
           throw new WebDAVServerException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
       }
   }
   
   /**
    * Copy items in a folder node to the destination folder node
    * 
    * @param srcNode NodeRef
    * @param destNode NodeRef
    */
   private final void copyAFolder(NodeRef srcNode, NodeRef destNode)
   {
       // Get the list of child nodes for the source node
       
       List<NodeRef> childNodes = getDAVHelper().getChildNodes( srcNode);
       
       // Copy the child nodes
       
       if ( childNodes != null && childNodes.size() > 0)
       {
           // Copy the child nodes
           
           for ( NodeRef curChild : childNodes)
           {
               // Get the properties for the existing file/folder and create a property list for the new node
               
               Map<QName, Serializable> properties = getNodeService().getProperties(curChild);
               Map<QName, Serializable> newProps = new HashMap<QName, Serializable>();
               
               if ( properties.containsKey(ContentModel.PROP_NAME))
                   newProps.put(ContentModel.PROP_NAME, properties.get(ContentModel.PROP_NAME));
               
               if ( properties.containsKey(ContentModel.PROP_CONTENT))
                   newProps.put(ContentModel.PROP_CONTENT, properties.get(ContentModel.PROP_CONTENT));
               
               // Check if the current child node is a folder or content node
               
               if ( getDAVHelper().isFolderNode(curChild))
               {
                   // Create a new folder node on the destination node, clone the properties of the
                   // existing node
                   
                   NodeRef newNode = getDAVHelper().createNodeWithProperties(destNode, newProps, false);
                   
                   // Copy the child folder items
                   
                   copyAFolder(curChild, newNode);
               }
               else
               {
                   // Create a new file node on the destination node, clone the properties of the
                   // existing node
                   
                   NodeRef newNode = getDAVHelper().createNodeWithProperties(destNode, newProps, true);

                   // Access the content

                   ContentService contentService = getContentService();
                   
                   ContentWriter contentWriter = contentService.getWriter(newNode, ContentModel.PROP_CONTENT, true);
                   ContentReader contentReader = contentService.getReader(curChild, ContentModel.PROP_CONTENT);
                   
                   // Copy the content from the existing node to the new node
                   
                   contentWriter.putContent(contentReader);
               }
           }
       }
   }
}
