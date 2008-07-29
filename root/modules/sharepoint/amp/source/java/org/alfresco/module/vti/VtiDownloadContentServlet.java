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

package org.alfresco.module.vti;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.servlet.BaseDownloadContentServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is temp servlet for downloading versions for documents
 *
 * @author Dmitry Lazurkin
 *
 */
public class VtiDownloadContentServlet extends BaseDownloadContentServlet
{
    private static final long serialVersionUID = -4317029858934814804L;

    private static Log logger = LogFactory.getLog(VtiDownloadContentServlet.class);

    protected void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        if (logger.isDebugEnabled())
        {
            String queryString = req.getQueryString();
            logger.debug("Authenticating request to URL: " + req.getRequestURI() +
                    ((queryString != null && queryString.length() > 0) ? ("?" + queryString) : ""));
        }

        processDownloadRequest(req, res, false);
    }

    public final static String generateDownloadURL(NodeRef ref, String name)
    {
        return generateUrl("/history/" + URL_ATTACH + "/{0}/{1}/{2}/{3}", ref, name);
    }

    @Override
    protected Log getLogger()
    {
        return logger;
    }

}
