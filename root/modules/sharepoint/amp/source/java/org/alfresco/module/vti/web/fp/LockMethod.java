/*
* Copyright (C) 2005-2010 Alfresco Software Limited.
*
* This file is part of Alfresco
*
* Alfresco is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Alfresco is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
*/

package org.alfresco.module.vti.web.fp;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileFolderUtil;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.extensions.surf.util.URLDecoder;

/**
 * Implements the WebDAV LOCK method with VTI specific
 * 
 * @author DmitryVas
 */
public class LockMethod extends org.alfresco.repo.webdav.LockMethod
{
    private String alfrescoContext;

    public LockMethod(String alfrescoContext)
    {
        this.alfrescoContext = alfrescoContext;
    }
    
    /**
     * Execute the request
     * 
     * @exception WebDAVServerException
     */
    protected void executeImpl() throws WebDAVServerException, Exception
    {
        FileFolderService fileFolderService = getFileFolderService();
        String path = URLDecoder.decode(m_request.getRequestURI());

        // remove the servlet path from the path
        if (alfrescoContext != null && alfrescoContext.length() > 0 && path.startsWith(alfrescoContext))
        {
            // Strip the servlet path from the relative path
            path = path.substring(alfrescoContext.length());
        }

        NodeRef rootNodeRef = getRootNodeRef();
        // Get the active user
        String userName = getDAVHelper().getAuthenticationService().getCurrentUserName();

        if (logger.isDebugEnabled())
        {
            logger.debug("Locking node: \n" + "   user: " + userName + "\n" + "   path: " + path);
        }

        FileInfo lockNodeInfo = null;
        try
        {
            // Check if the path exists
            lockNodeInfo = getDAVHelper().getNodeForPath(getRootNodeRef(), URLDecoder.decode(m_request.getRequestURI()), alfrescoContext);
            NodeRef workingCopy = getServiceRegistry().getCheckOutCheckInService().getWorkingCopy(lockNodeInfo.getNodeRef());
            if (workingCopy != null)
            {
                String workingCopyOwner = getNodeService().getProperty(workingCopy, ContentModel.PROP_WORKING_COPY_OWNER).toString();
                if (workingCopyOwner.equals(getAuthenticationService().getCurrentUserName()))
                {
                    lockNodeInfo = getFileFolderService().getFileInfo(workingCopy);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            // need to create it
            String[] splitPath = getDAVHelper().splitPath(path);
            // check
            if (splitPath[1].length() == 0)
            {
                throw new WebDAVServerException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            FileInfo dirInfo = null;
            List<String> dirPathElements = getDAVHelper().splitAllPaths(splitPath[0]);
            if (dirPathElements.size() == 0)
            {
                // if there are no path elements we are at the root so get the root node
                dirInfo = fileFolderService.getFileInfo(getRootNodeRef());
            }
            else
            {
                // make sure folder structure is present
                dirInfo = FileFolderUtil.makeFolders(fileFolderService, rootNodeRef, dirPathElements, ContentModel.TYPE_FOLDER);
            }

            if (dirInfo == null)
            {
                throw new WebDAVServerException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            // create the file
            lockNodeInfo = fileFolderService.create(dirInfo.getNodeRef(), splitPath[1], ContentModel.TYPE_CONTENT);
            ContentWriter writer = fileFolderService.getWriter(lockNodeInfo.getNodeRef());
            writer.putContent("");

            if (getNodeService().hasAspect(lockNodeInfo.getNodeRef(), ContentModel.ASPECT_VERSIONABLE) == false)
            {
                getNodeService().addAspect(lockNodeInfo.getNodeRef(), ContentModel.ASPECT_VERSIONABLE, null);
            }

            if (getNodeService().hasAspect(lockNodeInfo.getNodeRef(), ContentModel.ASPECT_AUTHOR) == false)
            {
                getNodeService().addAspect(lockNodeInfo.getNodeRef(), ContentModel.ASPECT_AUTHOR, null);
            }
            getNodeService().setProperty(lockNodeInfo.getNodeRef(), ContentModel.PROP_AUTHOR, getAuthenticationService().getCurrentUserName());

            if (logger.isDebugEnabled())
            {
                logger.debug("Created new node for lock: \n" + "   path: " + path + "\n" + "   node: " + lockNodeInfo);
            }

            m_response.setStatus(HttpServletResponse.SC_CREATED);
        }

        // Check if this is a new lock or a lock refresh
        if (hasLockToken())
        {
            this.lockInfo = checkNode(lockNodeInfo);
            // Refresh an existing lock
            refreshLock(lockNodeInfo, userName);
        }
        else
        {
            this.lockInfo = checkNode(lockNodeInfo, true, createExclusive);
            // Create a new lock
            createLock(lockNodeInfo, userName);
        }

        m_response.setHeader(WebDAV.HEADER_LOCK_TOKEN, "<" + WebDAV.makeLockToken(lockNodeInfo.getNodeRef(), userName) + ">");
        m_response.setContentType(WebDAV.XML_CONTENT_TYPE);

        // We either created a new lock or refreshed an existing lock, send back the lock details
        generateResponse(lockNodeInfo.getNodeRef(), userName);

    }
    
    /**
     * Generates the XML lock discovery response body
     */
    protected void generateResponse(NodeRef lockNode, String userName) throws Exception
    {
        String scope;
        String lt;
        if (lockToken != null)
        {
            // In case of lock creation take the scope from request header
            scope = this.createExclusive ? WebDAV.XML_EXCLUSIVE : WebDAV.XML_SHARED;
            // Output created lock
            lt = lockToken;
        }
        else
        {
            // In case of lock refreshing take the scope from previously stored lock
            scope = this.lockInfo.getScope();
            // Output refreshed lock
            lt = this.lockInfo.getToken();
        }
        String owner = (String) getNodeService().getProperty(lockNode, ContentModel.PROP_LOCK_OWNER);
        
        XMLWriter xml = createMSWebDavXmlWriter();

        xml.startDocument();

        String nsdec = generateNamespaceDeclarations(null);
        xml.startElement(EMPTY_NS, WebDAV.XML_PROP + nsdec, WebDAV.XML_PROP + nsdec, getDAVHelper().getNullAttributes());

        // Output the lock details with empty namespace
        generateLockDiscoveryXML(xml, lockNode, true, scope, WebDAV.getDepthName(m_depth), lt, owner);

        // Close off the XML
        xml.endElement(EMPTY_NS, WebDAV.XML_PROP, WebDAV.XML_PROP);
    }

    private XMLWriter createMSWebDavXmlWriter() throws IOException
    {
        OutputFormat outputFormat = new OutputFormat();
        outputFormat.setNewLineAfterDeclaration(false);
        outputFormat.setNewlines(false);
        outputFormat.setIndent(false);
        return new XMLWriter(m_response.getWriter(), outputFormat);
    }

}
