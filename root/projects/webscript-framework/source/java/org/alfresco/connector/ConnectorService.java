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
package org.alfresco.connector;

import javax.servlet.http.HttpSession;

import org.alfresco.config.ConfigService;
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.ReflectionHelper;
import org.alfresco.web.config.RemoteConfigElement;
import org.alfresco.web.config.RemoteConfigElement.AuthenticatorDescriptor;
import org.alfresco.web.config.RemoteConfigElement.ConnectorDescriptor;
import org.alfresco.web.config.RemoteConfigElement.CredentialVaultDescriptor;
import org.alfresco.web.config.RemoteConfigElement.EndpointDescriptor;
import org.alfresco.web.config.RemoteConfigElement.IdentityType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * The ConnectorService acts as a singleton that can be used to
 * build any of the objects utilized by the Connector layer.
 * <p>
 * This class is mounted as a Spring Bean within the
 * Web Script Framework so that developers can access it from the
 * application context.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class ConnectorService implements ApplicationListener
{
    private static final String PREFIX_CONNECTOR_SESSION = "_alfwsf_consession_";
    private static final String PREFIX_VAULT_SESSION     = "_alfwsf_vaults_";
    
    private static Log logger = LogFactory.getLog(ConnectorService.class);
    
    private ConfigService configService;
    private RemoteConfigElement remoteConfig;

    
    /**
     * Sets the config service.
     * 
     * @param configService the new config service
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    /**
     * Gets the config service.
     * 
     * @return the config service
     */
    public ConfigService getConfigService()
    {
        return this.configService;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof ContextRefreshedEvent)
        {
            ContextRefreshedEvent refreshEvent = (ContextRefreshedEvent)event;
            ApplicationContext refreshContext = refreshEvent.getApplicationContext();
            if (refreshContext != null)
            {
                // cache the remote configuration block
                this.remoteConfig = (RemoteConfigElement) getConfigService().getConfig("Remote").getConfigElement("remote");
                if (this.remoteConfig == null)
                {
                    throw new AlfrescoRuntimeException(
                            "The 'Remote' configuration was not found.");
                }
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////
    // Connectors

    /**
     * Retrieves a Connector to a given endpoint.
     * <p>
     * This Connector has no given user context and will not pass any
     * authentication credentials. Therefore only endpoints that do not
     * require authentication or have "declared" authentication as part
     * of the endpoint config should be used.  
     * 
     * @param endpointId the endpoint id
     * 
     * @return the connector
     */
    public Connector getConnector(String endpointId)
        throws RemoteConfigException
    {
        if (endpointId == null)
        {
            throw new IllegalArgumentException("EndpointId cannot be null.");
        }
        
        return getConnector(endpointId, (UserContext)null, (HttpSession)null);
    }
    
    /**
     * Retrieves a Connector for the given endpoint that is scoped
     * to the given user.
     * <p>
     * If the provided endpoint is configured to use an Authenticator,
     * then the Connector instance returned will be wrapped as an
     * AuthenticatingConnector.
     * <p>
     * Cookie and token state will be session bound and reusable on
     * subsequent invocations. 
     * 
     * @param endpointId    the endpoint id
     * @param userId        the user id
     * @param session       the session
     * 
     * @return the connector
     */
    public Connector getConnector(String endpointId, String userId, HttpSession session)
        throws RemoteConfigException
    {
        if (endpointId == null)
        {
            throw new IllegalArgumentException("EndpointId cannot be null.");
        }
        if (userId == null)
        {
            throw new IllegalArgumentException("UserId cannot be null.");
        }
        if (session == null)
        {
            throw new IllegalArgumentException("HttpSession cannot be null.");
        }
        
        // set credentials
        Credentials credentials = this.getCredentialVault(session, userId).retrieve(endpointId);
        
        // get connector session and build user context
        ConnectorSession connectorSession = this.getConnectorSession(session, endpointId);
        UserContext userContext = new UserContext(userId, credentials, connectorSession);
        
        return getConnector(endpointId, userContext, session);
    }

    /**
     * Retrieves a Connector for the given endpoint that is scoped
     * to the given user context.
     * <p>
     * A user context is a means of wrapping the Credentials and
     * ConnectorSession objects for a given user.  If they are provided,
     * then context will be drawn from them and stored back.
     * 
     * @param endpointId the endpoint id
     * @param userContext the user context
     * @param session the http session
     * 
     * @return the connector
     * 
     * @throws RemoteConfigException the remote config exception
     */
    public Connector getConnector(String endpointId, UserContext userContext, HttpSession session)
        throws RemoteConfigException
    {
        if (endpointId == null)
        {
            throw new IllegalArgumentException("EndpointId cannot be null.");
        }
        
        // load the endpoint
        EndpointDescriptor endpointDescriptor = remoteConfig.getEndpointDescriptor(endpointId);
        if (endpointDescriptor == null)
        {
            throw new RemoteConfigException(
                    "Unable to find endpoint definition for endpoint id: " + endpointId);
        }

        // load the connector
        String connectorId = (String)endpointDescriptor.getConnectorId();
        if (connectorId == null)
        {
            throw new RemoteConfigException(
                    "The connector id property on the endpoint definition '" + endpointId + "' was empty");
        }
        ConnectorDescriptor connectorDescriptor = remoteConfig.getConnectorDescriptor(connectorId);
        if (connectorDescriptor == null)
        {
            throw new RemoteConfigException(
                    "Unable to find connector definition for connector id: " + connectorId + " on endpoint id: " + endpointId);
        }

        // get the endpoint url
        String url = endpointDescriptor.getEndpointUrl();

        // build the connector
        Connector connector = buildConnector(connectorDescriptor, url);
        if (connector == null)
        {
            throw new RemoteConfigException(
                    "Unable to construct Connector for class: " + connectorDescriptor.getImplementationClass() + ", connector id: " + connectorId);
        }

        // if an authenticator is configured for the connector, then we
        // will wrap the connector with an AuthenticatingConnector type
        // which will do a re-attempt if our credential fails
        String authId = connectorDescriptor.getAuthenticatorId();
        if (authId != null)
        {
            AuthenticatorDescriptor authDescriptor = remoteConfig.getAuthenticatorDescriptor(authId);
            if (authDescriptor == null)
            {
                throw new RemoteConfigException(
                        "Unable to find authenticator definition for authenticator id: " + authId + " on connector id: " + connectorId);
            }
            String authClass = authDescriptor.getImplementationClass();
            Authenticator authenticator = buildAuthenticator(authClass);
            
            // wrap the connector
            connector = new AuthenticatingConnector(connector, authenticator);
        }
        
        // set credentials onto the connector
        // credentials are either "declared", "user", or "none":
        //  "declared" indicates that pre-set fixed declarative user credentials are to be used
        //  "user" indicates that the current user's credentials should be drawn from the vault and used
        //  "none" means that we don't include any credentials
        IdentityType identity = endpointDescriptor.getIdentity();
        switch (identity)
        {
            case DECLARED:
            {
                Credentials credentials = null;
                if (userContext != null && userContext.getCredentials() != null)
                {
                    // reuse previously vaulted credentials
                    credentials = userContext.getCredentials();
                }                
                if (credentials == null)
                {
                    // create new credentials for this declared user
                    String username = (String) endpointDescriptor.getUsername();
                    String password = (String) endpointDescriptor.getPassword();
                    
                    credentials = new SimpleCredentials(endpointId);
                    credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
                    credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, password);
                    
                    // store credentials in vault if we persisting against a user session
                    if (session != null)
                    {
                        getCredentialVault(session, username).store(credentials);
                    }
                }
                connector.setCredentials(credentials);
                
                break;
            }

            case USER:
            {
                Credentials credentials = null;
                
                if (userContext != null && userContext.getCredentials() != null)
                {
                    // reuse previously vaulted credentials
                    credentials = userContext.getCredentials();
                }
                
                if (credentials != null)
                {
                    connector.setCredentials(credentials);
                }
                else if (logger.isDebugEnabled())
                {
                    if (userContext != null)
                    {
                        logger.debug("Unable to find credentials for user: " + userContext.getUserId() + " and endpoint: " + endpointId);
                    }
                    else
                    {
                        logger.debug("Unable to find credentials for endpoint: " + endpointId);
                    }
                }
            }
        }
        
        // Establish Connector Session
        ConnectorSession connectorSession = null;
        if (userContext != null && userContext.getConnectorSession() != null)
        {
            // reuse previously session-bound connector session
            connectorSession = userContext.getConnectorSession();
        }
        if (connectorSession == null)
        {
            // create a new "temporary" connector session
            // this will not get bound back into the session
            connectorSession = new ConnectorSession(endpointId);
        }
        connector.setConnectorSession(connectorSession);
        
        return connector;
    }

    
    /////////////////////////////////////////////////////////////////
    // Authenticators
    
    /**
     * Returns the implementation of an Authenticator with a given id
     * 
     * @param id the id
     * 
     * @return the authenticator
     * 
     * @throws RemoteConfigException the remote config exception
     */
    public Authenticator getAuthenticator(String id) throws RemoteConfigException
    {
        if (id == null)
        {
            throw new IllegalArgumentException("Authenticator ID cannot be null.");
        }
        
        AuthenticatorDescriptor descriptor = remoteConfig.getAuthenticatorDescriptor(id);
        if (descriptor == null)
        {
            throw new RemoteConfigException(
                    "Unable to find authenticator for id: " + id);
        }
        
        return buildAuthenticator(descriptor.getImplementationClass());
    }

    
    /////////////////////////////////////////////////////////////////
    // Connector Sessions

    /**
     * Returns the ConnectorSession bound to the current HttpSession for the given endpoint
     * 
     * @param session the session
     * @param endpointId the endpoint id
     * 
     * @return the connector session
     */
    public ConnectorSession getConnectorSession(HttpSession session, String endpointId)
    {
        String key = getSessionEndpointKey(endpointId);
        ConnectorSession cs = (ConnectorSession)session.getAttribute(key);
        if (cs == null)
        {
            cs = new ConnectorSession(key);
            session.setAttribute(key, cs);
        }
        
        return cs;
    }

    /**
     * Removes the ConnectorSession from the HttpSession for the given endpoint 
     * 
     * @param session the session
     * @param endpointId the endpoint id
     */
    public void removeConnectorSession(HttpSession session, String endpointId)
    {
        String key = getSessionEndpointKey(endpointId);
        session.removeAttribute(key);
    }
    
    
    /////////////////////////////////////////////////////////////////
    // CredentialVaults
    
    /**
     * Retrieves the user-scoped CredentialVault for the given user
     * 
     * If a vault doesn't yet exist, a vault of the default type
     * will be instantiated
     * 
     * @param userId the user id
     * 
     * @return the credential vault
     * 
     * @throws RemoteConfigException the remote config exception
     */
    public CredentialVault getCredentialVault(HttpSession session, String userId) 
        throws RemoteConfigException
    {
        return getCredentialVault(session, userId, null);
    }

    /**
     * Retrieves the user-scoped CredentialVault for the given user id
     * and with the given vault id
     * 
     * @param userId the user id
     * @param vaultId the vault id
     * 
     * @return the credential vault
     * 
     * @throws RemoteConfigException the remote config exception
     */
    public CredentialVault getCredentialVault(HttpSession session, String userId, String vaultId)
        throws RemoteConfigException
    {
        if (userId == null)
        {
            throw new IllegalArgumentException("UserId is mandatory.");
        }
        if (vaultId == null)
        {
            vaultId = remoteConfig.getDefaultCredentialVaultId();
        }
        
        // session binding key
        String cacheKey = PREFIX_VAULT_SESSION + userId + "_" + vaultId;
        
        // pull the credential vault from session
        CredentialVault vault = (CredentialVault)session.getAttribute(cacheKey);
        
        // if no vault, build a new one
        if (vault == null)
        {
            // load the vault descriptor
            CredentialVaultDescriptor descriptor = remoteConfig.getCredentialVaultDescriptor(vaultId);
            if (descriptor == null)
            {
                throw new RemoteConfigException(
                        "Unable to find credential vault definition for id: " + vaultId);
            }
            
            // build the vault instance - it should always succeed
            vault = buildCredentialVault(userId, descriptor);
            if (vault == null)
            {
                throw new RemoteConfigException("Unable to instantiate configured class: " + descriptor.getImplementationClass());
            }
            
            // load the vault
            vault.load();
            
            // place onto session
            session.setAttribute(cacheKey, vault);
        }
        
        return vault;
    }
    
    
    /**
     * Internal method for building an Authenticator.
     * 
     * @param className the class name
     * 
     * @return the authenticator
     */
    private static Authenticator buildAuthenticator(String className)
        throws RemoteConfigException
    {
        Authenticator auth = (Authenticator)ReflectionHelper.newObject(className);
        if (auth == null)
        {
            throw new RemoteConfigException("Unable to instantiate Authenticator: " + className);
        }
        return auth;
    }

    /**
     * Internal method for building a Connector.
     * 
     * Connectors are not cached.  A new Connector will be constructed each time.
     * 
     * @param descriptor the descriptor
     * @param url the url
     * 
     * @return the connector
     */
    private static Connector buildConnector(ConnectorDescriptor descriptor, String url)
    {
        Class[] argTypes = new Class[] { descriptor.getClass(), url.getClass() };
        Object[] args = new Object[] { descriptor, url };
        return (Connector) ReflectionHelper.newObject(
                descriptor.getImplementationClass(), argTypes, args);
    }

    /**
     * Internal method for building a CredentialVault.
     * 
     * CredentialVaults built here are not cached.  They are purely
     * instantiated and handed back.
     * 
     * @param id the id of the vault
     * @param descriptor the descriptor
     * 
     * @return the credential vault
     */
    private static CredentialVault buildCredentialVault(String id, CredentialVaultDescriptor descriptor)
    {
        Class[] argTypes = new Class[] { id.getClass(), descriptor.getClass() };
        Object[] args = new Object[] { id, descriptor };
        return (CredentialVault) ReflectionHelper.newObject(
                descriptor.getImplementationClass(), argTypes, args);
    }
    
    /**
     * Internal method for building a endpoint key for storage within the session
     * 
     * @param endpointId the endpoint id
     * 
     * @return the session endpoint key
     */
    private static String getSessionEndpointKey(String endpointId)
    {
        return PREFIX_CONNECTOR_SESSION + endpointId;        
    }
}
