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
import java.util.List;
import java.util.Map;

import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.VtiRequest;
import org.alfresco.module.vti.VtiResponse;
import org.alfresco.module.vti.metadata.DocMetaInfo;
import org.alfresco.module.vti.metadata.DocsMetaInfo;

/**
 * Class for handling ListDocuments Method
 *
 * @author andreyak
 */
public class ListDocumentsMethod extends AbstractVtiMethod
{
    private static final String METHOD_NAME = "list documents";


    public String getName()
    {
        return METHOD_NAME;
    }

    public ListDocumentsMethod()
    {
    }


    /**
     * Provides a list of the files, folders, and subsites complete with meta-information
     * for each file contained in the initialUrl parameter of the specified Web site.
     */
    protected void doExecute(VtiRequest request, VtiResponse response) throws VtiException, IOException
    {
        String serviceName  = request.getParameter("service_name", "");
        boolean listHiddenDocs = request.getParameter("listHiddenDocs", false);
        boolean listExplorerDocs = request.getParameter("listExplorerDocs", false);
        String platform = request.getParameter("platform", "");
        String initialURL = request.getParameter("initialUrl", "");
        boolean listRecurse = request.getParameter("listRecurse", false);
        boolean listLinkInfo = request.getParameter("listLinkInfo", false);
        boolean listFolders = request.getParameter("listFolders", true);
        boolean listFiles = request.getParameter("listFiles", true);
        boolean listIncludeParent = request.getParameter("listIncludeParent", true);
        boolean listDerived = request.getParameter("listDerived", false);
        boolean listBorders = request.getParameter("listBorders", false);
        boolean validateWelcomeNames = request.getParameter("validateWelcomeNames", false);
        Map<String, Object> folderList = request.getMetaDictionary("folderList");
        boolean listChildWebs = request.getParameter("listChildWebs", false);

        DocsMetaInfo documents = vtiHandler.getListDocuments(serviceName, listHiddenDocs, listExplorerDocs, platform, initialURL, listRecurse, listLinkInfo,
                listFolders, listFiles, listIncludeParent, listDerived, listBorders, validateWelcomeNames, folderList, listChildWebs);
        
        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        
        response.beginList("document_list");
        List<DocMetaInfo> fileMetaInfoList = documents.getFileMetaInfoList();
        for (DocMetaInfo docMetaInfo: fileMetaInfoList)
        {
            response.beginList();
                response.addParameter("document_name", docMetaInfo.getPath());
                response.beginList("meta_info");
                    processDocMetaInfo(docMetaInfo, request, response);
                response.endList();
            response.endList();
        }
        response.endList();
        
        response.beginList("urldirs");
        List<DocMetaInfo> folderMetaInfoList = documents.getFolderMetaInfoList();
        for (DocMetaInfo docMetaInfo: folderMetaInfoList)
        {
            response.beginList();
            response.addParameter("url", docMetaInfo.getPath());
            response.beginList("meta_info");
            processDocMetaInfo(docMetaInfo, request, response);
            response.endList();
            response.endList();
        }
        response.endList();
        
        response.endVtiAnswer();
    }
}
