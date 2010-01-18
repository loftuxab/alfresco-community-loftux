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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site.portlet;

import java.io.IOException;
import java.net.URLEncoder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author dward
 */
public class DispatcherPortlet extends GenericPortlet
{
    private static final String PATH_PARAMETER = "_path";

    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException,
            PortletSecurityException, IOException
    {
        String path = request.getParameter(PATH_PARAMETER);
        // If the path parameter has not been provided, forward to the user specific dashboard page
        if (path == null)
        {
            String userId = request.getRemoteUser();
            if (userId != null)
            {
                path = "/page/user/" + URLEncoder.encode(userId, "UTF-8") + "/dashboard";
            }
        }

        if (path != null)
        {
            this.getPortletContext().getRequestDispatcher(path).include(request, response);
        }
    }

    @Override
    protected String getTitle(RenderRequest request)
    {
        // TODO Auto-generated method stub
        return super.getTitle(request);
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException,
            PortletSecurityException, IOException
    {
        // TODO Auto-generated method stub
        super.processAction(request, response);
    }

}
