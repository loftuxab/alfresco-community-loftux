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
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Implements the WebDAV MOVE method
 * 
 * @author gavinc
 */
public class MoveMethod extends HierarchicalMethod
{
   /**
    * Default constructor
    */
   public MoveMethod()
   {
   }
   
   /**
    * Execute the request
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
               logger.debug("Move from " + getPath() + " to " + getDestinationPath());
           
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
           
           // Move the content or folder node
           
           if ( srcSts == WebDAVHelper.FileExists)
           {
               // Perform a single content node move
               
               moveContentNode( destSts);
           }
           else
           {
               // Move a folder node and its child nodes
               
               moveFolderNode( destSts);
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
    * Move a single content node to a new location
    * 
    * @param destSts int
    * @exception WebDAVServerException
    */
   private final void moveContentNode(int destSts)
       throws WebDAVServerException
   {
       // If the destination exists then delete the existing node

       NodeService nodeService = getNodeService();
       
       if ( destSts == WebDAVHelper.FileExists)
       {
           // Get the existing destination node
           
           NodeRef curDestNode = getDAVHelper().getNodeForPath(getRootNodeRef(), getDestinationPath(), m_request.getServletPath());
           
           // Delete the existing node
           
           getNodeService().deleteNode(curDestNode);
       }
       
       // Get the existing content node
       
       NodeRef curNode = getDAVHelper().getNodeForPath(getRootNodeRef(), getPath(), m_request.getServletPath());
       
       // Split the destination path into path and file name

       String[] paths = getDAVHelper().splitPath(getDestinationPath());

       if (paths[1] == null)
       {
           // Bad path

           throw new WebDAVServerException(HttpServletResponse.SC_BAD_REQUEST);
       }
       
       // Get the destination parent folder node

       NodeRef destParentNode = getDAVHelper().getNodeForPath(getRootNodeRef(), paths[0], m_request.getServletPath());
       
       
       // Move the existing node to the new location
       
       ChildAssociationRef nodeToMoveAssoc = nodeService.getPrimaryParent(curNode);
       QName newAssocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
               QName.createValidLocalName(paths[1]));
       
       nodeService.moveNode(curNode, destParentNode, nodeToMoveAssoc.getTypeQName(), newAssocQName);
       
       Map<QName, Serializable> properties = nodeService.getProperties(curNode);
       properties.put(ContentModel.PROP_NAME, paths[1]);
       
       // Reguess the mimetype in case the extension has changed
       
       String mimeType = getMimetypeService().guessMimetype(paths[1]);
       properties.put(ContentModel.PROP_CONTENT, new ContentData(null, mimeType, 0L, "UTF-8"));
       
       nodeService.setProperties(curNode, properties);
       
   }
   
   /**
    * Move a folder node and its child nodes to a new location
    *
    * @param destSts int
    * @exception WebDAVServerException
    */
   private final void moveFolderNode(int destSts)
       throws WebDAVServerException
   {
       // We only support tree move in overwrite mode when the destination exists
       
       if ( hasOverWrite() == false && destSts == WebDAVHelper.FolderExists)
           throw new WebDAVServerException(HttpServletResponse.SC_PRECONDITION_FAILED);
       
       // Get the source node
       
       NodeRef srcNode = getDAVHelper().getNodeForPath(getRootNodeRef(), getPath(), m_request.getServletPath());

       if ( srcNode == null)
           throw new WebDAVServerException(HttpServletResponse.SC_PRECONDITION_FAILED);
       
       // Strip the destination path back to the parent folder
       
       String destPath = getDestinationPath();
       if ( destPath.endsWith(WebDAV.PathSeperator))
           destPath = destPath.substring(0, destPath.length() - 1);
       
       int pos = destPath.lastIndexOf(WebDAV.PathSeperator, destPath.length() - 1);
       if ( pos == -1)
           throw new WebDAVServerException(HttpServletResponse.SC_PRECONDITION_FAILED);
       
       String parentPath = destPath.substring(0, pos);
       
       // Create the destination path if it does not exist, create all folders missing from the path
       
       NodeRef destParentNode = getDAVHelper().makePath(getRootNodeRef(), parentPath);

       if ( destParentNode == null)
           throw new WebDAVServerException(HttpServletResponse.SC_PRECONDITION_FAILED);

       // Move the node

       NodeService nodeService = getNodeService();
       String destName = destPath.substring(pos + 1);
       
       ChildAssociationRef nodeToMoveAssoc = nodeService.getPrimaryParent(srcNode);
       QName newAssocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(destName));
       
       nodeService.moveNode(srcNode, destParentNode, nodeToMoveAssoc.getTypeQName(), newAssocQName);
       
       // Update the name property
       
       Map<QName, Serializable> properties = nodeService.getProperties(srcNode);
       properties.put(ContentModel.PROP_NAME, destName);
       
       nodeService.setProperties(srcNode, properties);
   }
}
