/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.web.site;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.connector.remote.Connector;
import org.alfresco.connector.remote.Response;
import org.alfresco.web.site.model.Endpoint;
import org.alfresco.web.site.remote.ConnectorFactory;

/**
 * This factory loads users from Alfresco, fetching their properties
 * and so forth.
 * 
 * @author muzquiano
 */
public class ExtranetUserFactory extends UserFactory
{
    protected User loadUser(RequestContext context, HttpServletRequest request,
            String user_id) throws Exception
    {
        // TODO: Somehow get the endpoint id...
        String endpointId = "";

        // Load the endpoint
        Endpoint endpoint = ModelUtil.getEndpoint(context, endpointId);

        // Webscript to use
        String webscriptUri = "/service/user";

        // Use the remote
        Connector conn = ConnectorFactory.newInstance(context, endpoint);
        Response response = conn.call(webscriptUri);
        String responseString = response.getResponse();

        // This is a JSON response...
        // TODO: Load into JSON parser and work with it
        User user = new User(user_id);

        return user;
    }
}
