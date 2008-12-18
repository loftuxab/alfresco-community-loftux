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

import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.VtiRequest;
import org.alfresco.module.vti.VtiResponse;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.DocMetaInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Michael Shavnev
 *
 */
public class UncheckoutDocumentMethod extends AbstractVtiMethod
{

    private static Log logger = LogFactory.getLog(UncheckoutDocumentMethod.class);

    public String getName()
    {
        return "uncheckout document";
    }

    protected void doExecute(VtiRequest request, VtiResponse response) throws VtiException, IOException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Start method execution. Method name: " + getName());
        }
        String serviceName  = request.getParameter("service_name", "");
        String documentName  = request.getParameter("document_name", "");
        boolean force  = request.getParameter("force", false);
        Date timeCheckedOut  = request.getParameter("time_checked_out", new Date());
        boolean rlsshortterm  = request.getParameter("rlsshortterm", false);
        boolean validateWelcomeNames  = request.getParameter("validateWelcomeNames", false);        
        
        serviceName = VtiPathHelper.removeSlashes(serviceName.replaceFirst(request.getAlfrescoContextName(), ""));   

        DocMetaInfo docMetaInfo = vtiHandler.uncheckOutDocument(serviceName, documentName, force, timeCheckedOut, rlsshortterm, validateWelcomeNames);

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        response.beginList("meta_info");
        
        processDocMetaInfo(docMetaInfo, request, response);
        
        response.endList();
        response.endVtiAnswer();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }

}
