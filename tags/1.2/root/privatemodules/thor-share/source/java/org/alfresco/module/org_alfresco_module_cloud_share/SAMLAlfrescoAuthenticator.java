/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud_share;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.exception.AuthenticationException;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.webscripts.connector.AlfrescoAuthenticator;
import org.springframework.extensions.webscripts.connector.ConnectorSession;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.RemoteClient;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.json.JSONWriter;

/**
 *
 *
 * @author Erik Winlof
 */

public class SAMLAlfrescoAuthenticator extends AlfrescoAuthenticator
{
    private static Log logger = LogFactory.getLog(AlfrescoAuthenticator.class);

    public final static String CS_PARAM_ALF_USERID = "alfUserId";
    
    // SAML IdP session index (for SSO -> SLO)
    public final static String CS_PARAM_ALF_IDP_SESSION_INDEX = "alfSessionIndex";
    public final static String CS_PARAM_ALF_IDP_REGISTRATION_ID = "alfRegistrationId";
    public final static String CS_PARAM_ALF_IDP_REGISTRATION_KEY = "alfRegistrationKey";
    public final static String CS_PARAM_ALF_IDP_REGISTRATION_TYPE = "alfRegistrationType";

    protected static final String SAML_LOGIN = "/internal/saml/acs";
    protected static final String MIMETYPE_APPLICATION_JSON = "application/json";

    /* (non-Javadoc)
    * @see org.alfresco.connector.AbstractAuthenticator#authenticate(java.lang.String, org.alfresco.connector.Credentials, org.alfresco.connector.ConnectorSession)
    */
    public ConnectorSession authenticate(String endpoint, Credentials credentials, ConnectorSession connectorSession)
            throws AuthenticationException
    {
        ConnectorSession cs = null;

        if (credentials != null && credentials.getPropertyKeys() != null && credentials.getPropertyKeys().length > 0)
        {
            // build a new remote client
            RemoteClient remoteClient = buildRemoteClient(endpoint);

            if (logger.isDebugEnabled())
                logger.debug("Verifying SAML AuthnReponse");

            // POST to the Alfresco login WebScript
            remoteClient.setRequestContentType(MIMETYPE_APPLICATION_JSON);

            StringBuilder body = new StringBuilder("{");

            String[] keys = credentials.getPropertyKeys();
            for (int i = 0, size = keys.length; i < size; i++)
            {
                body.append('"').append(JSONWriter.encodeJSONString(keys[i])).append("\":\"").append(JSONWriter.encodeJSONString((String) credentials.getProperty(keys[i]))).append('"');
                if (i != size - 1)
                {
                    body.append(',');
                }
            }
            body.append('}');

            Response response = remoteClient.call(getLoginURL(), body.toString());

            // read back the ticket
            if (response.getStatus().getCode() == 200)
            {
                String ticket;
                String userId;
                String idpSessionIndex;
                
                try
                {
                    JSONObject json = new JSONObject(response.getResponse());
                    JSONObject data = json.getJSONObject("data");

                    // Authentication
                    ticket = data.isNull("ticket") ? null : data.getString("ticket");
                    userId = data.isNull("userId") ? null : data.getString("userId");
                    idpSessionIndex = data.getString("idpSessionIndex");
                    if (connectorSession != null)
                    {
                        connectorSession.setParameter(CS_PARAM_ALF_TICKET, ticket);
                        connectorSession.setParameter(CS_PARAM_ALF_USERID, userId);
                        connectorSession.setParameter(CS_PARAM_ALF_IDP_SESSION_INDEX, idpSessionIndex);

                        // Registration
                        if (data.has("registration"))
                        {
                            JSONObject registration = data.getJSONObject("registration");
                            if (registration != null)
                            {
                                connectorSession.setParameter(CS_PARAM_ALF_IDP_REGISTRATION_ID, registration.getString("id"));
                                connectorSession.setParameter(CS_PARAM_ALF_IDP_REGISTRATION_KEY, registration.getString("key"));
                                connectorSession.setParameter(CS_PARAM_ALF_IDP_REGISTRATION_TYPE, registration.getString("type"));
                            }
                        }
                    }
                }
                catch (JSONException jErr)
                {
                    // the ticket that came back could not be parsed
                    // this will cause the entire handshake to fail
                    throw new AuthenticationException(
                            "Unable to retrieve login ticket from Alfresco", jErr);
                }

                if (logger.isDebugEnabled())
                    logger.debug("Parsed ticket: " + ticket);

                if (ticket != null)
                {
                    // place the ticket back into the connector session
                    if (connectorSession != null)
                    {
                        // signal that this succeeded
                        cs = connectorSession;
                    }
                }
                else
                {
                    // A ticket set to null means the AuthnResponse was ok (the user had logged in at the idp)
                    // but that the user didn't exist in Alfresco
                    logger.debug("Authentication failed, received a null ticket - User exist in the IDP but not in Alfresco.");
                }
            }
            else
            {
                // Throw an exception so we can catch it later and redirect the user to an error page.
                if (logger.isDebugEnabled())
                {
                    logger.debug("Authentication failed, received response code: " + response.getStatus().getCode());
                }
                throw new PlatformRuntimeException("Authentication failed, received response code: " + response.getStatus().getCode());
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("No user credentials available - cannot authenticate.");
        }

        return cs;
    }

    /**
     * @return the REST URL to be used for login requests
     */
    protected String getLoginURL()
    {
        return SAML_LOGIN;
    }
}



