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
import java.io.InputStream;
import java.util.EnumSet;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.dic.GetOption;
import org.alfresco.module.vti.metadata.model.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling GetDocument Method
 * 
 * @author andreyak
 *
 */
public class GetDocumentMethod extends AbstractMethod
{
    private static Log logger = LogFactory.getLog(GetDocumentMethod.class);
            
    private static final String METHOD_NAME = "get document"; 
    
    /**
     * Method for document retreiving
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
        String documentName = request.getParameter("document_name", "");
        boolean force = request.getParameter("force", true);
        String docVersion = request.getParameter("doc_version", "");
        EnumSet<GetOption> getOptionSet = GetOption.getOptions(request.getParameter("get_option"));
        int timeout = request.getParameter("timeout", 10);

        serviceName = VtiPathHelper.removeSlashes(serviceName.replaceFirst(request.getAlfrescoContextName(), ""));
        Document document;
        try
        {
            document = vtiHandler.getDocument(serviceName, documentName, force, docVersion, getOptionSet, timeout);
        }
        catch (VtiHandlerException e)
        {
            throw new VtiMehtodException(e);
        }

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);

        response.addParameter("message=successfully retrieved document '" + documentName + "' from '" + documentName + "'");
        response.beginList("document");

        response.addParameter("document_name", document.getPath());
        response.beginList("meta_info");
        processDocMetaInfo(document, request, response);
        response.endList();

        response.endList();

        response.endVtiAnswer();

        // 4K buffer
        byte[] bytearray = new byte[4096];
        InputStream is = document.getInputStream();
        int bytesread = 0;
        while ((bytesread = is.read(bytearray)) != -1)
        {
            response.getOutputStream().write(bytearray, 0, bytesread);
        }
        is.close();

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }


    /**
     * @see org.alfresco.module.vti.method.VtiMethod#getName()
     */
    public String getName()
    {
        return METHOD_NAME;
    }

}
