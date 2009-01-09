/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.module.vti.web.fp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.dialog.DialogMetaInfo;
import org.alfresco.module.vti.metadata.dialog.DialogMetaInfoComparator;
import org.alfresco.module.vti.metadata.dialog.DialogUtils;
import org.alfresco.module.vti.metadata.dialog.DialogsMetaInfo;
import org.alfresco.module.vti.metadata.dic.VtiSort;
import org.alfresco.module.vti.metadata.dic.VtiSortField;
import org.alfresco.module.vti.web.VtiRequestDispatcher;
import org.alfresco.util.URLEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Class for handling FileOpen Dialog
 * 
 * @author Mike Shavnev
 *
 */
public class FileOpenDialog extends AbstractMethod
{
    private static Log logger = LogFactory.getLog(FileOpenDialog.class);
    
    private static final String METHOD_NAME = "dialogview";
 
	private Template template = null;    
    
    public String getName()
    {
        return METHOD_NAME;
    }
    
    /**
     * Returns web-view for 'File Open' window.
     * 
     * @param request Vti Frontpage request ({@link VtiFpRequest})
     * @param response Vti Frontpage response ({@link VtiFpResponse})      
     */
    protected void doExecute(VtiFpRequest request, VtiFpResponse response) throws VtiMehtodException, IOException
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
        DialogsMetaInfo dialogInfo;
        try
        {
            dialogInfo = vtiHandler.getFileOpen(site, location, fileDialogFilterValue, rootFolder, sortField, sort, view);
        }
        catch (VtiHandlerException e)
        {
            throw new VtiMehtodException(e);
        }

        List<DialogMetaInfo> items = dialogInfo.getDialogMetaInfoList();
        Collections.sort(items, new DialogMetaInfoComparator(sortField, sort));

        Map<String, Object> freeMarkerMap = new HashMap<String, Object>();
        freeMarkerMap.put("sortField", sortField);
        freeMarkerMap.put("sort", sort);
        freeMarkerMap.put("context", request.getAlfrescoContextName());
        freeMarkerMap.put("host", request.getHeader("Host"));
        freeMarkerMap.put("items", items);
        freeMarkerMap.put("alfContext", (String) request.getAttribute(VtiRequestDispatcher.VTI_ALFRESCO_CONTEXT));
        freeMarkerMap.put("location", URLEncoder.encode(location));
        freeMarkerMap.put("request", request);
        freeMarkerMap.put("DialogUtils", new DialogUtils());

        try
        {
            if (template == null)
            {
                template = new Template("FileOpenDialog", new InputStreamReader(getClass().getResourceAsStream("FileOpenDialog.ftl")), null);
            }
            template.process(freeMarkerMap, response.getWriter());
            response.getWriter().flush();
        }
        catch (TemplateException e)
        {
            throw new RuntimeException(e);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }

    private String getSiteUrl(VtiFpRequest request)
    {
        String siteUrl;
        siteUrl = request.getRequestURI().replaceAll(request.getAlfrescoContextName(), "");
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
