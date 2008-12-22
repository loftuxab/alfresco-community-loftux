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
import java.util.LinkedList;
import java.util.List;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling "getDocsMetaInfo" method
 *
 * @author PavelYur
 */
public class GetDocsMetaInfoMethod extends AbstractMethod
{
    private static Log logger = LogFactory.getLog(GetDocsMetaInfoMethod.class);
    
    /**
     * Provides the meta-information for the files in the current Web site
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

        String serviceName = request.getParameter("service_name", "");
        boolean listHiddenDocs = request.getParameter("listHiddenDocs", false);
        boolean listLinkInfo = request.getParameter("listLinkInfo", false);
        boolean validateWelcomeNames = request.getParameter("validateWelcomeNames", false);
        List<String> urlList = request.getParameter("url_list", new LinkedList<String>());
        String context = request.getAlfrescoContextName();

        String uri = request.getRequestURI();
        int pos = uri.indexOf("/_vti_bin");
        if (pos != -1 && (context.length() + 1) < pos)
        {
            serviceName = uri.substring(context.length() + 1, pos);
        }
        else
        {
            serviceName = "";
        }

        for (int i = 0; i < urlList.size(); ++i)
        {
            String url = urlList.get(i);
            if (url.startsWith(request.getScheme() + "://" + request.getHeader("Host") + context + "/" + serviceName + "/"))
            {
                urlList.set(i, url.split(request.getScheme() + "://" + request.getHeader("Host") + context + "/" + serviceName + "/")[1]);
            }
        }
        DocsMetaInfo docsMetaInfoList;
        try
        {
            docsMetaInfoList = vtiHandler.getDocsMetaInfo(serviceName, listLinkInfo, validateWelcomeNames, listHiddenDocs, urlList);
        }
        catch (VtiHandlerException e)
        {
            throw new VtiMehtodException(e);
        }

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);

        List<DocMetaInfo> fileMetaInfoList = docsMetaInfoList.getFileMetaInfoList();
        response.beginList("document_list");
        for (DocMetaInfo docMetaInfo : fileMetaInfoList)
        {
            response.beginList();
            response.addParameter("document_name", docMetaInfo.getPath());
            response.beginList("meta_info");
            processDocMetaInfo(docMetaInfo, request, response);
            response.endList();
            response.endList();
        }
        response.endList();

        List<DocMetaInfo> folderMetaInfoList = docsMetaInfoList.getFolderMetaInfoList();
        response.beginList("urldirs");
        for (DocMetaInfo docMetaInfo : folderMetaInfoList)
        {
            response.beginList();
            response.addParameter("url", docMetaInfo.getPath());
            response.beginList("meta_info");
            processDocMetaInfo(docMetaInfo, request, response);
            response.endList();
            response.endList();
        }
        response.endList();

        List<DocMetaInfo> failedUrls = docsMetaInfoList.getFailedUrls();
        if (!failedUrls.isEmpty())
        {
            response.beginList("failedUrls");
            for (DocMetaInfo docMetaInfo : failedUrls)
            {
                response.addParameter(docMetaInfo.getPath());
            }
            response.endList();
        }
        response.endVtiAnswer();

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }

    /**
     * returns methods name
     */
    public String getName()
    {
        return "getDocsMetaInfo";
    }

}
