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
import java.util.EnumSet;

import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.VtiRequest;
import org.alfresco.module.vti.VtiResponse;
import org.alfresco.module.vti.metadata.DocMetaInfo;
import org.alfresco.module.vti.metadata.Document;
import org.alfresco.module.vti.metadata.dic.options.PutOption;

/**
 * Class for handling "put document" method
 * 
 * @author PavelYur
 *
 */
public class PutDocumentMethod extends AbstractVtiMethod
{

    /**
     * Default constructor
     */
    public PutDocumentMethod()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * writes a single file to a directory in an existing Web site
     */
    @Override
    protected void doExecute(VtiRequest request, VtiResponse response) throws VtiException, IOException
    {
        String serviceName = request.getParameter("service_name", "");
        Document document = request.getParameter("document", new Document());
        EnumSet<PutOption> putOptionSet = PutOption.getOptions(request.getParameter("put_option"));
        String comment = request.getParameter("comment", "");
        boolean keepCheckedOut = request.getParameter("keep_checked_out", false);
        boolean validateWelcomeNames = request.getParameter("validateWelcomeNames", false);
        
        DocMetaInfo docMetaInfo = vtiHandler.putDocument(serviceName, document, putOptionSet, comment, keepCheckedOut, validateWelcomeNames);

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        response.addParameter("message", "successfully put document '" + docMetaInfo.getPath() + "' as '" + docMetaInfo.getPath() + "'");
        response.beginList("document");
        response.addParameter("document_name", docMetaInfo.getPath());
        response.beginList("meta_info");
        processDocMetaInfo(docMetaInfo, request, response);
        response.endList();
        response.endList();
        response.endVtiAnswer();
    }

    /**
     * @return methods name
     */
    public String getName()
    {
        return "put document";
    }

}