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

import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.VtiRequest;
import org.alfresco.module.vti.VtiResponse;
import org.alfresco.module.vti.metadata.DocMetaInfo;
import org.alfresco.module.vti.metadata.DocsMetaInfo;

/**
 * Class for handling CreateUrlDirectories Method
 * 
 * @author AndreyAk
 *
 */
public class CreateUrlDirectories extends AbstractVtiMethod
{
    
    private static final String METHOD_NAME = "create url-directories";
    
    /** 
     * Allows the client to create one or more directories (folders) on the Web site. 
     * This operation is not atomic. If the bulk operation fails, some of the earlier 
     * directories might have been created. In the case of a failure, the client SHOULD 
     * query the server with list documents or if it needs to determine what folders were 
     * created.Note Clients SHOULD use this method rather than create url-directory.
     */
    protected void doExecute(VtiRequest request, VtiResponse response) throws VtiException, IOException
    {
        String serviceName  = request.getParameter("service_name", "");
        DocsMetaInfo urldirs = request.getParameter("urldirs", new DocsMetaInfo());  
        
        for (DocMetaInfo dir : urldirs.getFolderMetaInfoList())
        {
            vtiHandler.createDirectory(serviceName, dir);
        }
        
        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        response.addParameter("message=successfully created URL-directories");
        response.endVtiAnswer();
    }

    /* (non-Javadoc)
     * @see org.alfresco.module.vti.method.VtiMethod#getName()
     */
    public String getName()
    {
        return METHOD_NAME;
    }

}
