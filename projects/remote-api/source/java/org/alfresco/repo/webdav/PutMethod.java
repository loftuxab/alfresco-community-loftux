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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * Implements the WebDAV PUT method
 * 
 * @author gavinc
 */
public class PutMethod extends WebDAVMethod
{
    // Request parameters
    
    private String m_strLockToken = null;
    private String m_strContentType = null;
    private boolean m_expectHeaderPresent = false;

    /**
     * Default constructor
     */
    public PutMethod()
    {
    }

    /**
     * Parse the request headers
     * 
     * @exception WebDAVServerException
     */
    protected void parseRequestHeaders() throws WebDAVServerException
    {
        m_strContentType = m_request.getHeader(WebDAV.HEADER_CONTENT_TYPE);
        String strExpect = m_request.getHeader(WebDAV.HEADER_EXPECT);

        if (strExpect != null && strExpect.equals(WebDAV.HEADER_EXPECT_CONTENT))
        {
            m_expectHeaderPresent = true;
        }

        // Get the lock token, if any

        m_strLockToken = parseIfHeader();
    }

    /**
     * Parse the request body
     * 
     * @exception WebDAVServerException
     */
    protected void parseRequestBody() throws WebDAVServerException
    {
        // Nothing to do in this method, the body contains
        // the content it will be dealt with later
    }

    /**
     * Exceute the WebDAV request
     * 
     * @exception WebDAVServerException
     */
    protected void executeImpl() throws WebDAVServerException
    {
        NodeService nodeService = getNodeService();
        ContentService contentService = getContentService();

        int fsts = WebDAVHelper.NotExist;

        try
        {

            // Get the status for the request path

            fsts = getDAVHelper().getPathStatus(getRootNodeRef(), getPath());
            NodeRef contentNode = null;

            // Check that we are not trying to do this on a collection

            if (fsts == WebDAVHelper.FolderExists)
            {
                // Return an error status

                throw new WebDAVServerException(HttpServletResponse.SC_BAD_REQUEST);
            }
            else if ( fsts == WebDAVHelper.FileExists)
            {
                // Get an existing node, if available
                
                contentNode = getDAVHelper().getNodeForPath(getRootNodeRef(), getPath(), m_request.getServletPath());
            }
            else
            {
                // Split the path into path and file name
    
                String[] paths = getDAVHelper().splitPath(getPath());
    
                if (paths[1] == null)
                {
                    // Bad path
    
                    throw new WebDAVServerException(HttpServletResponse.SC_BAD_REQUEST);
                }
    
                // Get the parent folder node
    
                NodeRef parentNode = getDAVHelper().getNodeForPath(getRootNodeRef(), paths[0], m_request.getServletPath());
    
                // Create a new node or version
    
                contentNode = getDAVHelper().createNode(parentNode, paths[1], true);
            }

            // Access the content

            ContentWriter contentWriter = contentService.getWriter(contentNode, ContentModel.PROP_CONTENT, true);
            
            // Get the input stream from the request data
            
            InputStream input = m_request.getInputStream();

            // Write the new data to the content node, close the stream
            
            contentWriter.putContent(input);
            input.close();

            // Update the content type, if specified

            if (m_strContentType != null)
            {
                nodeService.setProperty(contentNode, ContentModel.PROP_CONTENT, new ContentData(null, m_strContentType, 0L, "UTF-8"));
            }

            // Set the response status, depending if the node existed or not

            m_response.setStatus(fsts == WebDAVHelper.FolderExists ? HttpServletResponse.SC_NO_CONTENT
                    : HttpServletResponse.SC_CREATED);
        }
        catch (IOException ex)
        {
            // Convert the error to a server error

            throw new WebDAVServerException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
        }
        catch (AccessDeniedException ex)
        {
            // Return an access denied status
            
            throw new WebDAVServerException(HttpServletResponse.SC_UNAUTHORIZED, ex);
        }
        catch (AlfrescoRuntimeException ex)
        {
            // TODO: Check for locking errors

            // Convert the error to a server error

            throw new WebDAVServerException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
        }
    }
}
