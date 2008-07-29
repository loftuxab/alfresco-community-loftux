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
package org.alfresco.module.vti.endpoints.usergroup;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.module.vti.endpoints.EndpointUtils;
import org.alfresco.module.vti.endpoints.VtiEndpoint;
import org.alfresco.module.vti.handler.soap.UserGroupServiceHandler;
import org.alfresco.module.vti.metadata.soap.usergroup.UserBean;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * @author AndreyAk
 *
 */
public class AddUserCollectionToRoleEndpoint extends VtiEndpoint
{

    // handler that provides methods for operating with documents and folders
    private UserGroupServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "usergroup";

    /**
     * @param handler
     */
    public AddUserCollectionToRoleEndpoint(UserGroupServiceHandler handler)
    {
        super();
        this.handler = handler;
    }

    /* (non-Javadoc)
     * @see org.springframework.ws.server.endpoint.AbstractDom4jPayloadEndpoint#invokeInternal(org.dom4j.Element, org.dom4j.Document)
     */
    
    @SuppressWarnings("unchecked")
    @Override
    protected Element invokeInternal(Element requestElement, Document responseDocument) throws Exception
    {
        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);

        // getting document parameter from request
        XPath roleNamePath = new Dom4jXPath(EndpointUtils.buildXPath(prefix, "/AddUserCollectionToRole/roleName"));
        roleNamePath.setNamespaceContext(nc);
        Element roleName = (Element) roleNamePath.selectSingleNode(requestElement);

        // getting document parameter from request
        XPath usersPath = new Dom4jXPath(EndpointUtils.buildXPath(prefix, "/AddUserCollectionToRole/usersInfoXml/Users/User"));
        usersPath.setNamespaceContext(nc);
        List<Element> usersElementList = (List<Element>) usersPath.selectNodes(requestElement);

        List<UserBean> usersList = new ArrayList<UserBean>();
        for (Element userElement : usersElementList)
        {
            usersList.add(new UserBean(userElement.attributeValue("Name"), userElement.attributeValue("LoginName"), userElement.attributeValue("Email"), userElement.attributeValue("Notes")));
        }
        
        String role = roleName.getTextTrim();
        if (role != null)
        {
            String dws = sessionManager.getSession(EndpointUtils.getRequest()).get(VtiEndpoint.DWS).toString();
            handler.addUserCollectionToRole(dws, role, usersList);
            // creating soap response
            Element root = responseDocument.addElement("AddUserCollectionToRoleResponse", namespace);
            return root;
        }
        else
        {
            // TODO: error
            return null;
        }


    }

}
