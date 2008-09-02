/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.vti;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.method.VtiPropfindMethod;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;

/**
 * @author PavelYur
 *
 */
public class VtiIfHeaderServlet extends HttpServlet
{
    
    private static final long serialVersionUID = 3119971805600532320L;
    
    private static SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
    
    static
    {
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    
    private NodeService nodeService;
    
    private FileFolderService fileFolderService;
    
    private CheckOutCheckInService checkOutCheckInService;
    
    private AuthenticationService authenticationService;
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }
    
    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }
    
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String if_header_value = req.getHeader("If");
        String guid = null;       
        
        if (if_header_value != null && if_header_value.length() > 0)
        {
            int begin = if_header_value.indexOf(":");
            int end = if_header_value.indexOf("@");
            if (begin != -1 && end != -1)
            {
                guid = if_header_value.substring(begin + 1, end);
            }
        }
        
        if (guid == null)
        {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return; 
        }
        
        NodeRef nodeRef = new NodeRef("workspace", "SpacesStore", guid.toLowerCase());
        
        NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(nodeRef);
        
        // original node props
        Map<QName, Serializable> props = nodeService.getProperties(nodeRef);
        // original node reader
        ContentReader contentReader = fileFolderService.getReader(nodeRef);
        
        if (workingCopyNodeRef != null)
        {        
            String workingCopyOwner = nodeService.getProperty(workingCopyNodeRef, ContentModel.PROP_WORKING_COPY_OWNER).toString();
            if (workingCopyOwner.equals(authenticationService.getCurrentUserName()))
            {
                // allow to see changes in document after it was checked out (only for checked out owner)
                contentReader = fileFolderService.getReader(workingCopyNodeRef);

                // working copy props
                props = nodeService.getProperties(workingCopyNodeRef);
            }            
        }
        
        Date lastModified = (Date) props.get(ContentModel.PROP_MODIFIED);
        
        resp.setHeader("Last-Modified", format.format(lastModified));
        resp.setHeader("ETag", "\"{" + guid.toUpperCase() + "}," + VtiPropfindMethod.convertDateToVersion(lastModified) + "\"");
        resp.setHeader("ResourceTag", "rt:" + guid.toUpperCase() + "@" + VtiPropfindMethod.convertDateToVersion(lastModified));
        
        ContentData content = (ContentData) props.get(ContentModel.PROP_CONTENT);     
        
        resp.setContentType(content.getMimetype());
        
        InputStream is = contentReader.getContentInputStream();
        OutputStream os = resp.getOutputStream();
        int len = 0;
        byte[] buf = new byte[4096];
        
        while ((len = is.read(buf)) != -1)
        {
            os.write(buf, 0, len);
        }        
        
        is.close();
        os.close();
    }
}
