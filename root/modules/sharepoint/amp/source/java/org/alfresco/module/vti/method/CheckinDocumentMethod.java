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
import org.alfresco.module.vti.metadata.DocMetaInfo;

/**
 * Class for handling "checkin document"
 *
 * @author Dmitry Lazurkin
 *
 */
public class CheckinDocumentMethod extends AbstractVtiMethod
{
    public String getName()
    {
        return "checkin document";
    }

    /**
     * Enables the currently authenticated user to make changes to a document under source control
     */
    protected void doExecute(VtiRequest request, VtiResponse response) throws VtiException, IOException
    {
        String documentName  = request.getParameter("document_name", "");
        String comment  = request.getParameter("comment", "");
        boolean keepCheckedOut = request.getParameter("keep_checked_out", false);
        Date timeCheckedout = request.getParameter("time_checked_out", (Date) null);

        DocMetaInfo docMetaInfo = vtiHandler.checkInDocument("", documentName, comment, keepCheckedOut, timeCheckedout, false);

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        response.beginList("meta_info");

        processDocMetaInfo(docMetaInfo, request, response);

        response.endList();
        response.endVtiAnswer();
    }
}
