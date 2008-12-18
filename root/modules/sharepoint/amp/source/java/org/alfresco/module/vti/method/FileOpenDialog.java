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
package org.alfresco.module.vti.method;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.VtiRequest;
import org.alfresco.module.vti.VtiResponse;
import org.alfresco.module.vti.metadata.dialog.DialogMetaInfo;
import org.alfresco.module.vti.metadata.dialog.DialogMetaInfoComparator;
import org.alfresco.module.vti.metadata.dialog.DialogsMetaInfo;
import org.alfresco.module.vti.metadata.dic.VtiSort;
import org.alfresco.module.vti.metadata.dic.VtiSortField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling FileOpen Dialog
 * 
 * @author AndreyAk
 *
 */
public class FileOpenDialog extends AbstractVtiMethod
{
    private static Log logger = LogFactory.getLog(FileOpenDialog.class);
    
    private static final String METHOD_NAME = "dialogview";
    
    private static final String PAGE_PATH = "/jsp/vti/fileopen/fileOpen.jsp";
        
    public String getName()
    {
        return METHOD_NAME;
    }

    protected void doExecute(VtiRequest request, VtiResponse response) throws VtiException, IOException
    {   
        if (logger.isDebugEnabled())
        {
            logger.debug("Start method execution. Method name: " + getName());
        }
        String location = request.getParameter("location", "");   
        String site = getSiteUrl(request);
        if (site.equals(""))
        {
            response.setStatus(HttpServletResponse.SC_GONE);
            return;
        }
        List<String> fileDialogFilterValue = Arrays.asList(request.getParameter("FileDialogFilterValue").split(";"));
        String rootFolder = request.getParameter("RootFolder", "");
        VtiSortField sortField = request.getParameter("SortField", VtiSortField.TYPE);
        VtiSort sort = request.getParameter("SortDir", VtiSort.ASC);
        String view = request.getParameter("View", "");      
        DialogsMetaInfo dialogInfo = vtiHandler.getFileOpen(site, location, fileDialogFilterValue, rootFolder, sortField, sort, view);
        List<DialogMetaInfo> items = dialogInfo.getDialogMetaInfoList();
        Collections.sort(items, new DialogMetaInfoComparator(sortField, sort));
        request.setAttribute("VTIDialogsMetaInfoList", items);
        try
        {
            request.getRequestDispatcher(PAGE_PATH).include(request, response);
        }
        catch (ServletException e)
        {
            throw new RuntimeException(e);
        }
        
        if (logger.isDebugEnabled())        
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }
    
    private String getSiteUrl(VtiRequest request)
    {
        String siteUrl;
        siteUrl = request.getRequestURI().replaceAll(request.getContextPath(), "");
        int pos = siteUrl.indexOf("/_vti_bin/");
        if (pos != 0)        
        {
            return siteUrl.substring(1, pos);
        }
        else
        {
            return "";
        }
    }
}
