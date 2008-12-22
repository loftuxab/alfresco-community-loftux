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
package org.alfresco.module.vti.web.ws;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.module.vti.handler.UserGroupServiceHandler;
import org.alfresco.module.vti.metadata.model.UserBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling GetUserLoginFromEmail soap method
 * 
 * @author AndreyAk
 *
 */
public class GetUserLoginFromEmailEndpoint extends AbstractEndpoint
{

    // handler that provides methods for operating with documents and folders
    private UserGroupServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "usergroup";

    private static Log logger = LogFactory.getLog(GetUserLoginFromEmailEndpoint.class);

    public GetUserLoginFromEmailEndpoint(UserGroupServiceHandler handler)
    {
        super();
        this.handler = handler;
    }

    /**
     * Retrieves information about the user
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    @SuppressWarnings("unchecked")
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("Soap Method with name " + getName() + " is started.");
        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        // getting document parameter from request
        XPath usersPath = new Dom4jXPath(buildXPath(prefix, "/GetUserLoginFromEmail/emailXml/Users/User"));
        usersPath.setNamespaceContext(nc);
        List<Element> usersList = (List<Element>) usersPath.selectNodes(soapRequest.getDocument().getRootElement());
        if (logger.isDebugEnabled())
            logger.debug("Getting users emails from request.");
        List<String> emailList = new ArrayList<String>();
        for (Element userElement : usersList)
        {
            emailList.add(userElement.attribute("Email").getText());
        }

        if (logger.isDebugEnabled()) {
            String emails = "";
            for(String email : emailList) {
                emails += email + " ";
            }
            logger.debug("Getting users from email list [ " + emails + "]");
        }
        
        List<UserBean> users = handler.getUserLoginFromEmail(null, emailList);
        
        // creating soap response
        Element root = soapResponse.getDocument().addElement("GetUserLoginFromEmailResponse", namespace);
        Element result = root.addElement("GetUserLoginFromEmailResult");
        Element getUserLoginFromEmail = result.addElement("GetUserLoginFromEmail");
        
        if (logger.isDebugEnabled()) {
            String usernames = "";
            for(UserBean user : users) {
                usernames += user.getDisplayName() + " ";
            }
            logger.debug("Retreived users [ " + usernames + "]");
        }

        for (int i = 0; i < users.size(); i++)
        {
            UserBean userBean = users.get(i);
            Element user = getUserLoginFromEmail.addElement("User");
            user.addAttribute("Login", userBean.getLoginName());
            user.addAttribute("DisplayName", userBean.getDisplayName());
            user.addAttribute("Email", userBean.getEmail());
            user.addAttribute("SiteUser", "1");
        }
        
        if (logger.isDebugEnabled()) 
        {
            logger.debug("Soap Method with name " + getName() + " is finished.");
        }
               
    }

}
