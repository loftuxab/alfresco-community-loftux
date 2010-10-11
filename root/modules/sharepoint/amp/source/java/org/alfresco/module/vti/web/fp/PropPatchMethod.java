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

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.repo.webdav.WebDAVProperty;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.dom4j.io.XMLWriter;
import org.springframework.extensions.surf.util.URLDecoder;

/**
 * Implements the WebDAV PROPPATCH method with VTI specific
 * 
 * @author DmitryVas
 */
public class PropPatchMethod extends org.alfresco.repo.webdav.PropPatchMethod
{

    private String alfrescoContext;

    public PropPatchMethod(String alfrescoContext)
    {
        this.alfrescoContext = alfrescoContext;
    }

    protected void executeImpl() throws WebDAVServerException, Exception
    {

        m_response.setStatus(WebDAV.WEBDAV_SC_MULTI_STATUS);

        FileInfo pathNodeInfo = null;
        try
        {
            // Check that the path exists
            pathNodeInfo = getDAVHelper().getNodeForPath(getRootNodeRef(), URLDecoder.decode(m_request.getRequestURI()), alfrescoContext);

        }
        catch (FileNotFoundException e)
        {
            // The path is not valid - send a 404 error back to the client
            throw new WebDAVServerException(HttpServletResponse.SC_NOT_FOUND);
        }
        
        NodeRef workingCopy = getServiceRegistry().getCheckOutCheckInService().getWorkingCopy(pathNodeInfo.getNodeRef());
        if (workingCopy != null)
        {
            String workingCopyOwner = getNodeService().getProperty(workingCopy, ContentModel.PROP_WORKING_COPY_OWNER).toString();
            if (workingCopyOwner.equals(getAuthenticationService().getCurrentUserName()))
            {
                pathNodeInfo = getFileFolderService().getFileInfo(workingCopy);
            }
        }
        checkNode(pathNodeInfo);

        // Set the response content type
        m_response.setContentType(WebDAV.XML_CONTENT_TYPE);

        // Create multistatus response
        XMLWriter xml = createXMLWriter();

        xml.startDocument();

        String nsdec = generateNamespaceDeclarations(m_namespaces);
        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_MULTI_STATUS + nsdec, WebDAV.XML_NS_MULTI_STATUS + nsdec, getDAVHelper().getNullAttributes());

        // Create the path for the current location in the tree
        StringBuilder baseBuild = new StringBuilder(256);
        baseBuild.append(getPath());
        if (baseBuild.length() == 0 || baseBuild.charAt(baseBuild.length() - 1) != WebDAVHelper.PathSeperatorChar)
        {
            baseBuild.append(WebDAVHelper.PathSeperatorChar);
        }
        String basePath = baseBuild.toString();

        // Output the response for the root node, depth zero
        generateResponse(xml, pathNodeInfo, basePath);

        // Close the outer XML element
        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_MULTI_STATUS, WebDAV.XML_NS_MULTI_STATUS);

    }

    /**
     * Generates the required response XML
     * 
     * @param xml XMLWriter
     * @param node NodeRef
     * @param path String
     */
    protected void generateResponse(XMLWriter xml, FileInfo nodeInfo, String path) throws Exception
    {
        boolean isFolder = nodeInfo.isFolder();

        // Output the response block for the current node
        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_RESPONSE, WebDAV.XML_NS_RESPONSE, getDAVHelper().getNullAttributes());

        // Build the href string for the current node
        String strHRef = WebDAV.getURLForPath(new HttpServletRequestWrapper(m_request)
        {
            public String getServletPath()
            {
                return alfrescoContext.equals("") ? "/" : alfrescoContext;
            }

        }, path, isFolder);

        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_HREF, WebDAV.XML_NS_HREF, getDAVHelper().getNullAttributes());
        xml.write(strHRef);
        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_HREF, WebDAV.XML_NS_HREF);

        boolean failed = false;
        WebDAVProperty failedProperty = null;
        for (PropertyAction action : m_propertyActions)
        {
            if (action.getProperty().isProtected())
            {
                generateError(xml);
                failed = true;
                failedProperty = action.getProperty();
                break;
            }
        }

        for (PropertyAction propertyAction : m_propertyActions)
        {
            int statusCode;
            String statusCodeDescription;
            WebDAVProperty property = propertyAction.getProperty();

            if (!failed)
            {
                if (PropertyAction.SET == propertyAction.getAction())
                {
                    getNodeService().setProperty(nodeInfo.getNodeRef(), property.createQName(), property.getValue());
                }
                else if (PropertyAction.REMOVE == propertyAction.getAction())
                {
                    getNodeService().removeProperty(nodeInfo.getNodeRef(), property.createQName());
                }
                else
                {
                    throw new WebDAVServerException(HttpServletResponse.SC_BAD_REQUEST);
                }
                statusCode = HttpServletResponse.SC_OK;
                statusCodeDescription = WebDAV.SC_OK_DESC;
            }
            else if (failedProperty == property)
            {
                statusCode = HttpServletResponse.SC_FORBIDDEN;
                statusCodeDescription = WebDAV.SC_FORBIDDEN_DESC;
            }
            else
            {
                statusCode = WebDAV.WEBDAV_SC_FAILED_DEPENDENCY;
                statusCodeDescription = WebDAV.WEBDAV_SC_FAILED_DEPENDENCY_DESC;
            }

            generatePropertyResponse(xml, property, statusCode, statusCodeDescription);
        }

        // Close off the response element
        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_RESPONSE, WebDAV.XML_NS_RESPONSE);
    }

    @Override
    protected void parseRequestHeaders() throws WebDAVServerException
    {
        parseIfHeader();
    }
}
