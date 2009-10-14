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
package org.alfresco.web.scripts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.connector.Connector;
import org.alfresco.connector.ConnectorService;
import org.alfresco.connector.CredentialVault;
import org.alfresco.connector.Credentials;
import org.alfresco.connector.Response;
import org.alfresco.connector.CredentialsImpl;
import org.alfresco.util.Base64;
import org.alfresco.web.scripts.Description.RequiredAuthentication;
import org.alfresco.web.scripts.servlet.ServletAuthenticatorFactory;
import org.alfresco.web.scripts.servlet.WebScriptServletRequest;
import org.alfresco.web.scripts.servlet.WebScriptServletResponse;
import org.alfresco.web.site.UserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * HTTP Basic Authentication for web-tier.
 * 
 * Provides either delegated or direct HTTP authentication to the designated endpoint.
 * If delegation is used, the endpoint supplied must perform the handshake when called.
 * 
 * @author Kevin Roast
 */
public class BasicHttpAuthenticatorFactory implements ServletAuthenticatorFactory
{
    private static Log logger = LogFactory.getLog(BasicHttpAuthenticatorFactory.class);
    
    private ConnectorService connectorService;
    private String endpointId;
    private boolean delegate = false;
    
    
    /**
     * @param connectorService      the ConnectorService to use
     */
    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }
    
    /**
     * @param endpointId            EndPoint Id to use
     */
    public void setEndpointId(String endpointId)
    {
        this.endpointId = endpointId;
    }
    
    /**
     * @param delegate              True to delegate actual auth to the connector framework
     *                              False to perform the authentication directly
     */
    public void setDelegate(boolean delegate)
    {
        this.delegate = delegate;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.servlet.ServletAuthenticatorFactory#create(org.alfresco.web.scripts.servlet.WebScriptServletRequest, org.alfresco.web.scripts.servlet.WebScriptServletResponse)
     */
    public Authenticator create(WebScriptServletRequest req, WebScriptServletResponse res)
    {
        return new BasicHttpAuthenticator(req, res);
    }
    
    
    /**
     * HTTP Basic Authentication
     */
    public class BasicHttpAuthenticator implements Authenticator
    {
        // dependencies
        private WebScriptServletRequest servletReq;
        private WebScriptServletResponse servletRes;
        
        private String authorization;
        private String ticket;
        
        /**
         * Construct
         * 
         * @param authenticationService
         * @param req
         * @param res
         */
        public BasicHttpAuthenticator(WebScriptServletRequest req, WebScriptServletResponse res)
        {
            this.servletReq = req;
            this.servletRes = res;
            
            HttpServletRequest httpReq = servletReq.getHttpServletRequest();
            
            this.authorization = httpReq.getHeader("Authorization");
            this.ticket = httpReq.getParameter("alf_ticket");
        }
    
        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.Authenticator#authenticate(org.alfresco.web.scripts.Description.RequiredAuthentication, boolean)
         */
        public boolean authenticate(RequiredAuthentication required, boolean isGuest)
        {
            boolean authorized = false;
    
            // validate credentials
            HttpServletRequest req = servletReq.getHttpServletRequest();
            HttpServletResponse res = servletRes.getHttpServletResponse();
            
            if (logger.isDebugEnabled())
                logger.debug("HTTP Authorization provided: " + (authorization != null && authorization.length() != 0));
            
            // authenticate as specified by HTTP Basic Authentication
            if (authorization != null && authorization.length() != 0)
            {
                String[] authorizationParts = authorization.split(" ");
                if (!authorizationParts[0].equalsIgnoreCase("basic"))
                {
                    throw new WebScriptException("Authorization '" + authorizationParts[0] + "' not supported.");
                }
                String decodedAuthorisation = new String(Base64.decode(authorizationParts[1]));
                String[] parts = decodedAuthorisation.split(":");
                
                if (parts.length == 2)
                {
                    // assume username and password passed as the parts
                    String username = parts[0];
                    if (logger.isDebugEnabled())
                        logger.debug("Authenticating (BASIC HTTP) user " + parts[0]);
                    
                    try
                    {
                        // generate the credentials based on the auth details provided
                        HttpSession session = req.getSession();
                        Credentials credentials = new CredentialsImpl(endpointId);
                        credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
                        credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, parts[1]);
                        CredentialVault vault = connectorService.getCredentialVault(session, username);
                        vault.store(credentials);
                        
                        if (delegate)
                        {
                            // the handshake will be performed by the Connector when a remote call is first made
                            session.setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, username);
                            authorized = true;
                        }
                        else
                        {
                            // perform the authentication test directly using the connector
                            Connector connector = connectorService.getConnector(endpointId, username, session);
                            Response response = connector.call("/touch");
                            authorized = (response.getStatus().getCode() != Status.STATUS_UNAUTHORIZED);
                        }
                    }
                    catch (Throwable err)
                    {
                        logger.warn("Failed during authorization: " + err.getMessage(), err);
                    }
                }
            }
            
            // request credentials if not authorized
            if (!authorized)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Requesting authorization credentials");
                
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setHeader("WWW-Authenticate", "Basic realm=\"Alfresco\"");
            }
            
            return authorized;
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.Authenticator#emptyCredentials()
         */
        public boolean emptyCredentials()
        {
            return ((ticket == null || ticket.length() == 0) && (authorization == null || authorization.length() == 0));
        }
    }
}