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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.VtiRequest;
import org.alfresco.module.vti.VtiResponse;
import org.alfresco.module.vti.metadata.DocMetaInfo;
import org.alfresco.module.vti.metadata.DocsMetaInfo;
import org.alfresco.module.vti.metadata.dic.options.PutOption;
import org.alfresco.module.vti.metadata.dic.options.RenameOption;

/**
 * Class for handling MoveDocument Method
 * 
 * @author AndreyAk
 */
public class MoveDocumentMethod extends AbstractVtiMethod
{
    
    private static final String METHOD_NAME = "move document";
    
    /** 
     * Changes the name of the selected document to the new name provided by the user
     */
    @Override
    protected void doExecute(VtiRequest request, VtiResponse response) throws VtiException, IOException
    {
        String serviceName  = request.getParameter("service_name", "");
        String oldUrl  = request.getParameter("oldUrl", "");
        String newUrl  = request.getParameter("newUrl", "");
        List<String> urlList  = request.getParameter("url_list", new ArrayList<String>());
        EnumSet<RenameOption> renameOptionSet  = RenameOption.getOptions(request.getParameter("rename_option"));
        EnumSet<PutOption> putOptionSet  = PutOption.getOptions(request.getParameter("put_option"));
        boolean docopy  = request.getParameter("docopy", false);
        boolean validateWelcomeNames  = request.getParameter("validateWelcomeNames", false);
        
        DocsMetaInfo docs = vtiHandler.moveDocument(serviceName, oldUrl, newUrl, urlList, renameOptionSet, putOptionSet, docopy, validateWelcomeNames);

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);

        response.addParameter("message=successfully renamed URL '" + oldUrl + "' as '" + newUrl + "'");
        response.addParameter("oldUrl", oldUrl);
        response.addParameter("newUrl", newUrl);
        response.beginList("document_list");
        response.endList();
        
        response.beginList("moved_docs");
        for (DocMetaInfo document : docs.getFileMetaInfoList())
        {
            response.beginList();
            response.addParameter("document_name", document.getPath());
            response.beginList("meta_info");
            processDocMetaInfo(document, request, response);
            response.endList();
            response.endList();
        }
        response.endList();
        
        response.beginList("moved_dirs");
        for (DocMetaInfo url : docs.getFolderMetaInfoList())
        {
            response.beginList();
            response.addParameter("url", url.getPath());
            response.beginList("meta_info");
            processDocMetaInfo(url, request, response);
            response.endList();
            response.endList();
        }
        response.endList();
        
        response.endVtiAnswer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.module.vti.method.VtiMethod#getName()
     */
    public String getName()
    {
        return METHOD_NAME;
    }

}
