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

import org.alfresco.connector.AlfrescoConnector;
import org.alfresco.connector.Connector;
import org.alfresco.connector.CredentialVaultFactory;
import org.alfresco.connector.Credentials;
import org.alfresco.connector.Response;
import org.alfresco.connector.SimpleCredentials;
import org.alfresco.connector.User;
import org.alfresco.web.scripts.WebFrameworkScriptRemote;
import org.alfresco.web.site.exception.UserFactoryException;
import org.dom4j.Document;
import org.json.JSONObject;

/**
 * This factory loads users from Alfresco, fetching their properties
 * and so forth.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class AlfrescoUserFactory extends UserFactory
{
    public static final String ALFRESCO_ENDPOINT_ID = "alfresco";
    public static final String ALFRESCO_SYSTEM_ENDPOINT_ID = "alfresco-system";


    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#authenticate(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.String)
     */
    public Credentials authenticate(HttpServletRequest request, String username, String password)
    {
        Credentials credentials = null;
        try
        {
            // request context
            RequestContext context = RequestUtil.getRequestContext(request);

            // create a connector for the current user
            WebFrameworkScriptRemote remote = new WebFrameworkScriptRemote(context);
            Connector connector = remote.connect(ALFRESCO_ENDPOINT_ID);

            // call the authentication ticket provider
            String uri = "/api/login?u=" + username + "&pw=" + password;
            Response response = connector.call(uri);

            // parse out the ticket
            Document document = org.dom4j.DocumentHelper.parseText(response.getResponse());
            String ticket = document.getRootElement().getText();

            // build the credentials to represent the successful login
            String id = CredentialVaultFactory.buildBindingKey(ALFRESCO_ENDPOINT_ID, username);
            credentials = new SimpleCredentials(id);
            credentials.setProperty(Credentials.CREDENTIAL_ALF_USERNAME, username);
            credentials.setProperty(Credentials.CREDENTIAL_ALF_PASSWORD, password);
            credentials.setProperty(Credentials.CREDENTIAL_ALF_TICKET, ticket);
        }
        catch (Throwable ex) 
        {
            // many things might have happened
            // an invalid ticket or perhaps a connectivity issue
            // at any rate, we cannot authenticate
        }

        return credentials;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public User loadUser(RequestContext context, HttpServletRequest request, String userId)
        throws UserFactoryException
    {
        User user = null;
        try
        {
            // create a connector for the current user
            WebFrameworkScriptRemote remote = new WebFrameworkScriptRemote(context);
            AlfrescoConnector connector = (AlfrescoConnector)remote.connect(ALFRESCO_SYSTEM_ENDPOINT_ID);
            
            // call the authentication ticket provider
            String uri = "/webframework/content/metadata?user=" + userId;
            Response response = connector.call(uri);
            
            String responseString = response.getResponse();
            
            // Load the user from the JSON parser
            JSONObject jsonObject = new JSONObject(responseString);
            
            JSONObject properties = jsonObject.getJSONObject("properties");
            
            user = new User(userId);
            user.setFirstName(properties.getString("{http://www.alfresco.org/model/content/1.0}firstName"));
            user.setLastName(properties.getString("{http://www.alfresco.org/model/content/1.0}lastName"));
            if (properties.has("{http://www.alfresco.org/model/content/1.0}jobtitle"))
            {
                user.setJobTitle(properties.getString("{http://www.alfresco.org/model/content/1.0}jobtitle"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}organization"))
            {
                user.setOrganization(properties.getString("{http://www.alfresco.org/model/content/1.0}organization"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}location"))
            {
                user.setLocation(properties.getString("{http://www.alfresco.org/model/content/1.0}location"));
            }
            
            // TODO: apply other user properties
        }
        catch (Exception ex)
        {
            // unable to read back the user json object
            throw new UserFactoryException("Unable to retrieve user from repository", ex);
        }
        
        return user;
    }
}
