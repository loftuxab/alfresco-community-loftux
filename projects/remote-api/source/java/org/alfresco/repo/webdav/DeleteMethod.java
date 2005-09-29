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

import javax.servlet.http.HttpServletResponse;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * Implements the WebDAV DELETE method
 * 
 * @author gavinc
 */
public class DeleteMethod extends WebDAVMethod
{
   /**
    * Default constructor
    */
   public DeleteMethod()
   {
   }
   
   /**
    * Parse the request headers
    * 
    * @exception WebDAVServerException
    */
   protected void parseRequestHeaders() throws WebDAVServerException
   {
      // Nothing to do in this method
   }
   
   /**
    * Parse the request body
    * 
    * @exception WebDAVServerException
    */
   protected void parseRequestBody() throws WebDAVServerException
   {
      // Nothing to do in this method
   }   
   
   /**
    * Execute the request
    * 
    * @exception WebDAVServerException
    */
   protected void executeImpl() throws WebDAVServerException
   {
       NodeService nodeService = getNodeService();
       
       try
       {
           // Find the node to be deleted
           
           NodeRef node = getDAVHelper().getNodeForPath(getRootNodeRef(), getPath(), m_request.getServletPath());
           
           if ( node != null)
               nodeService.deleteNode( node);
           else
               throw new WebDAVServerException(HttpServletResponse.SC_NOT_FOUND);
       }
       catch ( AccessDeniedException ex)
       {
       
           // Return a forbidden status
           
           throw new WebDAVServerException(HttpServletResponse.SC_UNAUTHORIZED, ex);
       }
       catch (AlfrescoRuntimeException ex)
       {
           // TODO: Check for locked status and return different status code
           
           // Convert error to a server error
           
           throw new WebDAVServerException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
       }
   }
}
