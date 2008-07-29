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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.VtiRequest;
import org.alfresco.module.vti.VtiResponse;
import org.alfresco.module.vti.metadata.DocMetaInfo;
import org.alfresco.module.vti.metadata.DocsMetaInfo;

/**
 * Class for handling "remove documents" method
 * 
 * @author PavelYur
 *
 */
public class RemoveDocumentsMethod extends AbstractVtiMethod
{

    /**
     * Default constructor 
     */
    public RemoveDocumentsMethod()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     *  Deletes the specified documents or folders from the Web site specified by the service_name parameter
     */
    @Override
    protected void doExecute(VtiRequest request, VtiResponse response) throws VtiException, IOException
    {
        String serviceName = request.getParameter("service_name", "");
        List<String> urlList = request.getParameter("url_list", new LinkedList<String>());
        // parameter time_tokens has been deprecated in Microsoft 
        // Windows SharePoint Services and the method does not act upon its value
        //List<Date> timeTokens = request.getParameter("time_tokens", new LinkedList<Date>());
        boolean validateWelcomeNames = request.getParameter("validateWelcomeNames", false);
        
        /*if (timeTokens.isEmpty() || urlList.size() != timeTokens.size())
        {
            // TODO: throw exception or may be move this check to protocol handler
        }*/

        DocsMetaInfo list = vtiHandler.removeDocuments(serviceName, urlList, new LinkedList<Date>(), validateWelcomeNames);
        List<DocMetaInfo> files = list.getFileMetaInfoList();
        List<DocMetaInfo> folders = list.getFolderMetaInfoList();

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        response.addParameter("message", "successfully removed documents");

        response.beginList("removed_docs");
        for (DocMetaInfo docMetaInfo : files)
        {
            response.beginList();
            response.addParameter("document_name", docMetaInfo.getPath());
            response.beginList("meta_info");
            processDocMetaInfo(docMetaInfo, request, response);
            response.endList();
            response.endList();
        }
        response.endList();

        response.beginList("removed_dirs");
        for (DocMetaInfo docMetaInfo : folders)
        {
            response.beginList();
            response.addParameter("url", docMetaInfo.getPath());
            response.beginList("meta_info");
            processDocMetaInfo(docMetaInfo, request, response);
            response.endList();
            response.endList();
        }
        response.endList();
        // TODO list failed urls (how to identificate is it file or folder?) 
        response.beginList("failed_docs");
        response.endList();
        response.beginList("failed_dirs");
        response.endList();
        response.endVtiAnswer();
    }

    /** 
     * returns methods name
     */
    public String getName()
    {
        return "remove documents";
    }

}
