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

import org.alfresco.connector.AuthenticatingConnector;
import org.alfresco.connector.Connector;
import org.alfresco.connector.CredentialVault;
import org.alfresco.connector.Credentials;
import org.alfresco.connector.Response;
import org.alfresco.connector.User;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.site.exception.UserFactoryException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This factory loads users from Alfresco, fetching their properties
 * and so forth.  The data source is assumed to be a JSON provider.
 *
 * @author muzquiano
 * @author kevinr
 */
public class AlfrescoUserFactory extends UserFactory
{
    private static Log logger = LogFactory.getLog(AlfrescoUserFactory.class);

    public static final String ALFRESCO_ENDPOINT_ID = "alfresco";


    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#authenticate(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.String)
     */
    public boolean authenticate(HttpServletRequest request, String username, String password)
    {
        boolean authenticated = false;
        try
        {
            // make sure our credentials are in the vault
            CredentialVault vault = FrameworkHelper.getCredentialVault(request.getSession(), username);
            Credentials credentials = vault.newCredentials(ALFRESCO_ENDPOINT_ID);
            credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
            credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, password);
            
            // build a connector whose connector session is bound to the current session
            AuthenticatingConnector connector = (AuthenticatingConnector)
                FrameworkHelper.getConnector(request.getSession(), username, ALFRESCO_ENDPOINT_ID);
            authenticated = connector.handshake();
        }
        catch (Throwable ex)
        {
            // many things might have happened
            // an invalid ticket or perhaps a connectivity issue
            // at any rate, we cannot authenticate
            if(logger.isDebugEnabled())
                logger.debug("Exception in AlfrescoUserFactory.authenticate()", ex);
        }
        
        return authenticated;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public User loadUser(RequestContext context, HttpServletRequest request, String userId)
        throws UserFactoryException
    {
        AlfrescoUser user = null;
        try
        {
            // get a connector whose connector session is bound to the current session
            Connector connector = FrameworkHelper.getConnector(
                    request.getSession(), userId, ALFRESCO_ENDPOINT_ID);
            
            // build the REST URL to retrieve user details
            String uri = "/webframework/content/metadata?user=" + userId;
            
            // invoke and check for OK response
            Response response = connector.call(uri);
            if (Status.STATUS_OK != response.getStatus().getCode())
            {
                throw new UserFactoryException("Unable to create user - failed to retrieve user metadata: " + 
                        response.getStatus().getMessage(), (Exception)response.getStatus().getException());
            }
            
            // Load the user properties via the JSON parser
            JSONObject json = new JSONObject(response.getResponse());
            JSONObject properties = json.getJSONObject("properties");
            
            user = new AlfrescoUser(userId);
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
            if (properties.has("{http://www.alfresco.org/model/content/1.0}email"))
            {
                user.setEmail(properties.getString("{http://www.alfresco.org/model/content/1.0}email"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}persondescription"))
            {
                user.setBiography(properties.getString("{http://www.alfresco.org/model/content/1.0}persondescription"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}telephone"))
            {
                user.setTelephone(properties.getString("{http://www.alfresco.org/model/content/1.0}telephone"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}mobile"))
            {
                user.setMobilePhone(properties.getString("{http://www.alfresco.org/model/content/1.0}mobile"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}skype"))
            {
                user.setSkype(properties.getString("{http://www.alfresco.org/model/content/1.0}skype"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}instantmsg"))
            {
                user.setInstantMsg(properties.getString("{http://www.alfresco.org/model/content/1.0}instantmsg"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}companyaddress1"))
            {
                user.setCompanyAddress1(properties.getString("{http://www.alfresco.org/model/content/1.0}companyaddress1"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}companyaddress2"))
            {
                user.setCompanyAddress2(properties.getString("{http://www.alfresco.org/model/content/1.0}companyaddress2"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}companyaddress3"))
            {
                user.setCompanyAddress3(properties.getString("{http://www.alfresco.org/model/content/1.0}companyaddress3"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}companypostcode"))
            {
                user.setCompanyPostcode(properties.getString("{http://www.alfresco.org/model/content/1.0}companypostcode"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}companytelephone"))
            {
                user.setCompanyTelephone(properties.getString("{http://www.alfresco.org/model/content/1.0}companytelephone"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}companyfax"))
            {
                user.setCompanyFax(properties.getString("{http://www.alfresco.org/model/content/1.0}companyfax"));
            }
            if (properties.has("{http://www.alfresco.org/model/content/1.0}companyemail"))
            {
                user.setCompanyEmail(properties.getString("{http://www.alfresco.org/model/content/1.0}companyemail"));
            }
            
            if (json.has("associations"))
            {
                JSONObject assocs = json.getJSONObject("associations");
                JSONArray array = assocs.getJSONArray("{http://www.alfresco.org/model/content/1.0}avatar");
                if (array.length() != 0)
                {
                    user.setAvatarRef(array.getString(0));
                }
            }
        }
        catch (Exception ex)
        {
            // unable to read back the user json object
            throw new UserFactoryException("Unable to retrieve user from repository", ex);
        }

        return user;
    }
}
