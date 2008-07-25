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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.connector.AuthenticatingConnector;
import org.alfresco.connector.Connector;
import org.alfresco.connector.ConnectorContext;
import org.alfresco.connector.CredentialVault;
import org.alfresco.connector.Credentials;
import org.alfresco.connector.HttpMethod;
import org.alfresco.connector.Response;
import org.alfresco.connector.User;
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.util.StringBuilderWriter;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.json.JSONWriter;
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
    
    private static final String CM_AVATAR = "{http://www.alfresco.org/model/content/1.0}avatar";
    private static final String CM_COMPANYEMAIL = "{http://www.alfresco.org/model/content/1.0}companyemail";
    private static final String CM_COMPANYFAX = "{http://www.alfresco.org/model/content/1.0}companyfax";
    private static final String CM_COMPANYTELEPHONE = "{http://www.alfresco.org/model/content/1.0}companytelephone";
    private static final String CM_COMPANYPOSTCODE = "{http://www.alfresco.org/model/content/1.0}companypostcode";
    private static final String CM_COMPANYADDRESS3 = "{http://www.alfresco.org/model/content/1.0}companyaddress3";
    private static final String CM_COMPANYADDRESS2 = "{http://www.alfresco.org/model/content/1.0}companyaddress2";
    private static final String CM_COMPANYADDRESS1 = "{http://www.alfresco.org/model/content/1.0}companyaddress1";
    private static final String CM_INSTANTMSG = "{http://www.alfresco.org/model/content/1.0}instantmsg";
    private static final String CM_SKYPE = "{http://www.alfresco.org/model/content/1.0}skype";
    private static final String CM_MOBILE = "{http://www.alfresco.org/model/content/1.0}mobile";
    private static final String CM_TELEPHONE = "{http://www.alfresco.org/model/content/1.0}telephone";
    private static final String CM_PERSONDESCRIPTION = "{http://www.alfresco.org/model/content/1.0}persondescription";
    private static final String CM_EMAIL = "{http://www.alfresco.org/model/content/1.0}email";
    private static final String CM_LOCATION = "{http://www.alfresco.org/model/content/1.0}location";
    private static final String CM_ORGANIZATION = "{http://www.alfresco.org/model/content/1.0}organization";
    private static final String CM_JOBTITLE = "{http://www.alfresco.org/model/content/1.0}jobtitle";
    private static final String CM_LASTNAME = "{http://www.alfresco.org/model/content/1.0}lastName";
    private static final String CM_FIRSTNAME = "{http://www.alfresco.org/model/content/1.0}firstName";

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
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, java.lang.String)
     */
    public User loadUser(RequestContext context, String userId)
        throws UserFactoryException
    {
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
                    ((HttpRequestContext)context).getRequest().getSession(), currentUserId, ALFRESCO_ENDPOINT_ID);
            
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
            
            // Construct the Alfresco User object based on the cm:person properties
            user = new AlfrescoUser(userId);
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
     * Persist the user back to the Alfresco repository
     * 
     * @param user  to persist
     * 
     * @throws IOException
     */
    public void saveUser(AlfrescoUser user) throws UserFactoryException
    {
        HttpRequestContext context = (HttpRequestContext)ThreadLocalRequestContext.getRequestContext();
        if (!context.getUserId().equals(user.getId()))
        {
            throw new UserFactoryException("Unable to persist user with different Id that current Id.");
        }
        
        StringBuilderWriter buf = new StringBuilderWriter(512);
        JSONWriter writer = new JSONWriter(buf);
        
        try
        {
            writer.startObject();
            
            writer.writeValue("username", user.getId());
            
            writer.startValue("properties");
            writer.startObject();
            writer.writeValue(CM_FIRSTNAME, user.getFirstName());
            writer.writeValue(CM_LASTNAME, user.getLastName());
            writer.writeValue(CM_JOBTITLE, user.getJobTitle());
            writer.writeValue(CM_ORGANIZATION, user.getOrganization());
            writer.writeValue(CM_LOCATION, user.getLocation());
            writer.writeValue(CM_EMAIL, user.getEmail());
            writer.writeValue(CM_TELEPHONE, user.getTelephone());
            writer.writeValue(CM_MOBILE, user.getMobilePhone());
            writer.writeValue(CM_SKYPE, user.getSkype());
            writer.writeValue(CM_INSTANTMSG, user.getInstantMsg());
            writer.writeValue(CM_COMPANYADDRESS1, user.getCompanyAddress1());
            writer.writeValue(CM_COMPANYADDRESS2, user.getCompanyAddress2());
            writer.writeValue(CM_COMPANYADDRESS3, user.getCompanyAddress3());
            writer.writeValue(CM_COMPANYPOSTCODE, user.getCompanyPostcode());
            writer.writeValue(CM_COMPANYFAX, user.getCompanyFax());
            writer.writeValue(CM_COMPANYEMAIL, user.getCompanyEmail());
            writer.writeValue(CM_COMPANYTELEPHONE, user.getCompanyTelephone());
            writer.endObject();
            writer.endValue();
            
            writer.startValue("content");
            writer.startObject();
            writer.writeValue(CM_PERSONDESCRIPTION, user.getBiography());
            writer.endObject();
            writer.endValue();
            
            writer.endObject();
            
            Connector conn = FrameworkHelper.getConnector(context, ALFRESCO_ENDPOINT_ID);
            ConnectorContext c = new ConnectorContext(HttpMethod.POST);
            c.setContentType("application/json");
            Response res = conn.call("/slingshot/profile/userprofile", c,
                    new ByteArrayInputStream(buf.toString().getBytes()));
            if (Status.STATUS_OK != res.getStatus().getCode())
            {
                throw new UserFactoryException("Remote error during User save: " + res.getStatus().getMessage());
            }
        }
        catch (IOException ioErr)
        {
            throw new UserFactoryException("IO error during User save: " + ioErr.getMessage(), ioErr);
        }
        catch (RemoteConfigException err)
        {
            throw new UserFactoryException("Configuration error during User save: " + err.getMessage(), err);
        }
    }
}
