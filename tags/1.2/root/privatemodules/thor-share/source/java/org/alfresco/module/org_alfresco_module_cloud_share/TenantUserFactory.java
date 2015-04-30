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

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.SlingshotUser;
import org.alfresco.web.site.SlingshotUserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.site.AlfrescoUser;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.connector.*;

/**
 * <p>A factory for instantiating {@link TenantUser}s.</p>
 *  
 * @author David Draper
 */
public class TenantUserFactory extends SlingshotUserFactory implements ApplicationContextAware
{
    private static Log logger = LogFactory.getLog(TenantUserFactory.class);
    
    public static final String LOGIN_URL_SUFFIX = "/api/login";
    public static final String HOME_TENANT = "homeTenant";
    public static final String DEFAULT_TENANT = "defaultTenant";
    public static final String SECONDARY_TENANTS = "secondaryTenants";
    public static final String ACCOUNT_CLASS_NAME = "accountClassName";
    public static final String IS_EXTERNAL = "isExternal";
    public static final String IS_NETWORK_ADMIN = "isNetworkAdmin";
    public static final String SESSION_ATTRIBUTE_KEY_IDP_SESSION_INDEX = "_alf_IDP_SESSION_INDEX";

    private ApplicationContext applicationContext = null;
    
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }
    
    /**
     * <p>Authenticates a tenant user.</p>
     */
    @Override
    public boolean authenticate(HttpServletRequest request, String username, String password)
    {
        boolean authenticated = false;
        try
        {
            // make sure our credentials are in the vault
            CredentialVault vault = frameworkUtils.getCredentialVault(request.getSession(), username);
            Credentials credentials = vault.newCredentials(ALFRESCO_ENDPOINT_ID);
            credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
            credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, password);
            
            // build a connector whose connector session is bound to the current session
            Connector connector = frameworkUtils.getConnector(request.getSession(), username, ALFRESCO_ENDPOINT_ID);
            TenantAlfrescoAuthenticator taa = new TenantAlfrescoAuthenticator();
            taa.setApplicationContext(applicationContext);
            AuthenticatingConnector authenticatingConnector = new AuthenticatingConnector(connector, taa);
            authenticated = authenticatingConnector.handshake();
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

    /**
     *  TODO Move this to SlingshotUserFactory and use SAMLAlfrescoAuthenticator.
     *
     *  Then override it here and use TenantSAMLAlfrescoAuthenticator
     */
    public SAMLAuthnResponseVerification authenticateSAML(HttpServletRequest request, Map<String, String> authnResponse) throws PlatformRuntimeException
    {
        boolean authenticated = false;
        String ticket = null;
        String username = null;
        String sessionIndex = null;
        SAMLAuthnResponseVerificationRegistration registration = null;
        try
        {
            // Create a credential vault for the "alfresco" endpoint what will contain the SAML AuthnResponse properties.
            // The vault will be passed into the SAMLAlfrescoAuthenticator's authenticate method.
            // Currently we don't know the username but since the ConnectorService's getCredentialVault method requires a userid
            // we pass in a string to avoid throwing an error.
            String tmpSAMLUsername = "tmpSAMLUsername";
            CredentialVault vault = frameworkUtils.getCredentialVault(request.getSession(), tmpSAMLUsername);
            Credentials credentials = vault.newCredentials(ALFRESCO_ENDPOINT_ID);
            
            for (Map.Entry<String, String> param : authnResponse.entrySet())
            {
                credentials.setProperty(param.getKey(), param.getValue());
            }
            
            // Build a connector and make sure we use a SAMLAlfrescoAuthenticator.
            // The SAMLAlfrescoAuthenticator will get the value passed in and then ask the repository to verify the SAML AuthnResponse properties
            Connector connector = frameworkUtils.getConnector(request.getSession(), tmpSAMLUsername, ALFRESCO_ENDPOINT_ID);
            TenantSAMLAlfrescoAuthenticator tsaa = new TenantSAMLAlfrescoAuthenticator();
            tsaa.setApplicationContext(applicationContext);
            AuthenticatingConnector authenticatingConnector = new AuthenticatingConnector(connector, tsaa);
            
            // PercentFormatter the handshake and see if it was a successful login (a ticket was returned)
            authenticated = authenticatingConnector.handshake();

            // Get the Connector session so we can get more details about the AuthnResponse verification
            ConnectorSession cs = authenticatingConnector.getConnectorSession();

            // Pick out the username, ticket & sessionIndex so we can pass it on to the callee
            ticket = cs.getParameter(SAMLAlfrescoAuthenticator.CS_PARAM_ALF_TICKET); // Will be null if login failed
            username = cs.getParameter(SAMLAlfrescoAuthenticator.CS_PARAM_ALF_USERID);
            sessionIndex = cs.getParameter(SAMLAlfrescoAuthenticator.CS_PARAM_ALF_IDP_SESSION_INDEX);

            String registrationId = cs.getParameter(SAMLAlfrescoAuthenticator.CS_PARAM_ALF_IDP_REGISTRATION_ID);
            if (registrationId != null)
            {
                // User didn't exist but there is a workflow in place for the user to complete his/hers profile
                registration = new SAMLAuthnResponseVerificationRegistration(registrationId,
                    cs.getParameter(SAMLAlfrescoAuthenticator.CS_PARAM_ALF_IDP_REGISTRATION_KEY),
                    cs.getParameter(SAMLAlfrescoAuthenticator.CS_PARAM_ALF_IDP_REGISTRATION_TYPE));
            }

            
            // TODO check "cs" status code and/or whether username is empty/null - this should throw exception/401 ... example, IdP has not email address for user !!

            //authenticated = ((ticket != null) && (! ticket.isEmpty()) && (! ticket.equals("null")));
        }
        catch (Throwable ex)
        {
            // many things might have happened
            // an invalid ticket or perhaps a connectivity issue
            // at any rate, we cannot authenticate
            if (logger.isDebugEnabled())
            {
                logger.debug("Exception in AlfrescoUserFactory.authenticate()", ex);
            }
            if (ex instanceof PlatformRuntimeException)
            {
                throw (PlatformRuntimeException) ex;
            }
            // TODO: Throw something else?
        }
        return new SAMLAuthnResponseVerification(authenticated, username, ticket, sessionIndex, registration);
    }

    /**
     * <p>Overrides the default implementation to ensure that tenant information is included in requests
     * to retrieve user information.</p>
     */
    @Override
    protected String buildUserMetadataRestUrl(RequestContext context, String userId, String endpointId)
    {        
        String tenantName = TenantUtil.getTenantName();
        
        // TODO: This is temporary and may or may not need to be removed...
        if (userId.equals("admin"))
        {
            tenantName = TenantUtil.SYSTEM_TENANT_NAME;
            TenantUtil.setTenantName(tenantName);
        }
        return "/" + tenantName + DEFAULT_USER_URL_PREFIX + URLEncoder.encode(userId);
    }

    /**
     * <p>Overrides the super class implementation to ensure that a {@link TenantUser} rather than {@link SlingshotUser}
     * is instantiated.</p>
     */
    @Override
    protected AlfrescoUser constructUser(JSONObject properties, 
                                         Map<String, Boolean> capabilities,
                                         Map<String, Boolean> immutability) throws JSONException
    {
        TenantUser user = new TenantUser(properties.getString(CM_USERNAME), capabilities, immutability);
        user.setProperty(PROP_USERSTATUS, properties.has(CM_USERSTATUS) ? properties.getString(CM_USERSTATUS) : null);
        user.setProperty(PROP_USERSTATUSTIME, properties.has(CM_USERSTATUSTIME) ? properties.getString(CM_USERSTATUSTIME) : null);
        return user;
    }

    /**
     * <p>Overrides the super class implementation to set the home and default tenants on the user. This is known to
     * be a {@link TenantUser} because it will have been instantiated in this class (or a sub-class).</p>
     */
    @Override
    protected AlfrescoUser constructAlfrescoUser(JSONObject jsonData, 
                                                 JSONObject properties,
                                                 Map<String, Boolean> capabilities, 
                                                 Map<String, Boolean> immutability) throws JSONException
    {
        TenantUser user = (TenantUser) super.constructAlfrescoUser(jsonData, properties, capabilities, immutability);
        String homeTenant = "";
        String defaultTenant = "";
        String[] secondaryTenantArray;
        String accountClassName = "";
        boolean isExternal = false;
        boolean isNetworkAdmin = false;
        if (jsonData.has(HOME_TENANT))
        {
            homeTenant = jsonData.getString(HOME_TENANT);
        }
        if (jsonData.has(DEFAULT_TENANT))
        {
            defaultTenant = jsonData.getString(DEFAULT_TENANT);
        }
        
        if (jsonData.has(SECONDARY_TENANTS))
        {
            JSONArray secondaryTenants = jsonData.getJSONArray(SECONDARY_TENANTS);
            secondaryTenantArray = new String[secondaryTenants.length()];
            for (int i=0; i<secondaryTenants.length(); i++)
            {
                secondaryTenantArray[i] = secondaryTenants.getString(i);
            }
        }
        else
        {
            secondaryTenantArray = new String[]{};
        }
        
        if (jsonData.has(ACCOUNT_CLASS_NAME))
        {
            accountClassName = jsonData.getString(ACCOUNT_CLASS_NAME);
        }
        if (jsonData.has(IS_EXTERNAL))
        {
            isExternal = jsonData.getBoolean(IS_EXTERNAL);
        }
        if (jsonData.has(IS_NETWORK_ADMIN))
        {
            isNetworkAdmin = jsonData.getBoolean(IS_NETWORK_ADMIN);
        }
        
        user.setHomeTenant(homeTenant);
        user.setDefaultTenant(defaultTenant);
        user.setSecondaryTenants(secondaryTenantArray);
        user.setProperty(HOME_TENANT, homeTenant);
        user.setProperty(DEFAULT_TENANT, defaultTenant);
        user.setProperty(SECONDARY_TENANTS, secondaryTenantArray);
        user.setProperty(ACCOUNT_CLASS_NAME, accountClassName);
        user.setProperty(IS_EXTERNAL, isExternal);
        user.setProperty(IS_NETWORK_ADMIN, isNetworkAdmin);
        
        return user;
    }

    @Override
    public User loadUser(RequestContext context, String userId, String endpointId) throws UserFactoryException
    {
        User user = super.loadUser(context, userId.toLowerCase(), endpointId);

        // set a value indicating time the user was constructed
        user.setProperty(ALF_USER_LOADED, new Date().getTime());

        return user;
    }
}
