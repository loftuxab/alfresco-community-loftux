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
import org.springframework.extensions.surf.util.URLEncoder;
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
 * By implementing this class, User derived objects are available to
 * all downstream components and templates.  These components and
 * templates can then consult the user profile as they execute.
 * 
 * The user is stored on the request context and can be fetched
 * using context.getUser(). The user is also available in the root
 * of the a script component context as 'user'. 
 * 
 * @author muzquiano
 * @author kevinr
 */
public class AlfrescoUserFactory extends UserFactory
{
    private static Log logger = LogFactory.getLog(AlfrescoUserFactory.class);
    
    public static final String CM_AVATAR = "{http://www.alfresco.org/model/content/1.0}avatar";
    public static final String CM_COMPANYEMAIL = "{http://www.alfresco.org/model/content/1.0}companyemail";
    public static final String CM_COMPANYFAX = "{http://www.alfresco.org/model/content/1.0}companyfax";
    public static final String CM_COMPANYTELEPHONE = "{http://www.alfresco.org/model/content/1.0}companytelephone";
    public static final String CM_COMPANYPOSTCODE = "{http://www.alfresco.org/model/content/1.0}companypostcode";
    public static final String CM_COMPANYADDRESS3 = "{http://www.alfresco.org/model/content/1.0}companyaddress3";
    public static final String CM_COMPANYADDRESS2 = "{http://www.alfresco.org/model/content/1.0}companyaddress2";
    public static final String CM_COMPANYADDRESS1 = "{http://www.alfresco.org/model/content/1.0}companyaddress1";
    public static final String CM_INSTANTMSG = "{http://www.alfresco.org/model/content/1.0}instantmsg";
    public static final String CM_SKYPE = "{http://www.alfresco.org/model/content/1.0}skype";
    public static final String CM_MOBILE = "{http://www.alfresco.org/model/content/1.0}mobile";
    public static final String CM_TELEPHONE = "{http://www.alfresco.org/model/content/1.0}telephone";
    public static final String CM_PERSONDESCRIPTION = "{http://www.alfresco.org/model/content/1.0}persondescription";
    public static final String CM_EMAIL = "{http://www.alfresco.org/model/content/1.0}email";
    public static final String CM_LOCATION = "{http://www.alfresco.org/model/content/1.0}location";
    public static final String CM_ORGANIZATION = "{http://www.alfresco.org/model/content/1.0}organization";
    public static final String CM_JOBTITLE = "{http://www.alfresco.org/model/content/1.0}jobtitle";
    public static final String CM_LASTNAME = "{http://www.alfresco.org/model/content/1.0}lastName";
    public static final String CM_FIRSTNAME = "{http://www.alfresco.org/model/content/1.0}firstName";
    public static final String CM_USERNAME = "{http://www.alfresco.org/model/content/1.0}userName";
    private static final String ISADMIN = "isAdmin";
    private static final String ISGUEST = "isGuest";
    
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
            if (logger.isDebugEnabled())
                logger.debug("Exception in AlfrescoUserFactory.authenticate()", ex);
        }
        
        return authenticated;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, java.lang.String)
     */
    public User loadUser(RequestContext context, String userId)
        throws UserFactoryException
    {
        return loadUser(context, userId, null);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, java.lang.String, java.lang.String)
     */
    public User loadUser(RequestContext context, String userId, String endpointId)
        throws UserFactoryException
    {
        if (endpointId == null)
        {
            endpointId = ALFRESCO_ENDPOINT_ID;
        }
        
        AlfrescoUser user = null;
        try
        {
            // ensure we bind the connector to the current user name - if this is the first load
            // of a user we will use the userId as passed into the method 
            String currentUserId = context.getUserId();
            if (currentUserId == null)
            {
                currentUserId = userId;
            }
            
            // get a connector whose connector session is bound to the current session
            Connector connector = FrameworkHelper.getConnector(
                    context.getRequest().getSession(), currentUserId, endpointId);
            
            // build the REST URL to retrieve user details
            String uri = "/webframework/content/metadata?user=" + URLEncoder.encode(userId);
            
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
            
            // Construct the Alfresco User object based on the cm:person properties
            // ensure we have the correct username case
            user = constructUser(properties.getString(CM_USERNAME),
                    properties.has(ISADMIN) && Boolean.parseBoolean(properties.getString(ISADMIN)),
                    properties.has(ISGUEST) && Boolean.parseBoolean(properties.getString(ISGUEST)));
            user.setFirstName(properties.getString(CM_FIRSTNAME));
            user.setLastName(properties.getString(CM_LASTNAME));
            if (properties.has(CM_JOBTITLE))
            {
                user.setJobTitle(properties.getString(CM_JOBTITLE));
            }
            if (properties.has(CM_ORGANIZATION))
            {
                user.setOrganization(properties.getString(CM_ORGANIZATION));
            }
            if (properties.has(CM_LOCATION))
            {
                user.setLocation(properties.getString(CM_LOCATION));
            }
            if (properties.has(CM_EMAIL))
            {
                user.setEmail(properties.getString(CM_EMAIL));
            }
            if (properties.has(CM_PERSONDESCRIPTION))
            {
                user.setBiography(properties.getString(CM_PERSONDESCRIPTION));
            }
            if (properties.has(CM_TELEPHONE))
            {
                user.setTelephone(properties.getString(CM_TELEPHONE));
            }
            if (properties.has(CM_MOBILE))
            {
                user.setMobilePhone(properties.getString(CM_MOBILE));
            }
            if (properties.has(CM_SKYPE))
            {
                user.setSkype(properties.getString(CM_SKYPE));
            }
            if (properties.has(CM_INSTANTMSG))
            {
                user.setInstantMsg(properties.getString(CM_INSTANTMSG));
            }
            if (properties.has(CM_COMPANYADDRESS1))
            {
                user.setCompanyAddress1(properties.getString(CM_COMPANYADDRESS1));
            }
            if (properties.has(CM_COMPANYADDRESS2))
            {
                user.setCompanyAddress2(properties.getString(CM_COMPANYADDRESS2));
            }
            if (properties.has(CM_COMPANYADDRESS3))
            {
                user.setCompanyAddress3(properties.getString(CM_COMPANYADDRESS3));
            }
            if (properties.has(CM_COMPANYPOSTCODE))
            {
                user.setCompanyPostcode(properties.getString(CM_COMPANYPOSTCODE));
            }
            if (properties.has(CM_COMPANYTELEPHONE))
            {
                user.setCompanyTelephone(properties.getString(CM_COMPANYTELEPHONE));
            }
            if (properties.has(CM_COMPANYFAX))
            {
                user.setCompanyFax(properties.getString(CM_COMPANYFAX));
            }
            if (properties.has(CM_COMPANYEMAIL))
            {
                user.setCompanyEmail(properties.getString(CM_COMPANYEMAIL));
            }
            
            if (json.has("associations"))
            {
                JSONObject assocs = json.getJSONObject("associations");
                JSONArray array = assocs.getJSONArray(CM_AVATAR);
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

    /**
     * @param userId
     * 
     * @return the AlfrescoUser object
     */
    protected AlfrescoUser constructUser(String userId, boolean isAdmin, boolean isGuest)
    {
        return new AlfrescoUser(userId, isAdmin, isGuest);
    }
}
