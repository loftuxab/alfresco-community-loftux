/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.connector;

import java.util.HashMap;

import org.alfresco.config.ConfigService;
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.util.ReflectionHelper;
import org.alfresco.web.config.RemoteConfigElement;
import org.alfresco.web.config.RemoteConfigElement.AuthenticatorDescriptor;
import org.alfresco.web.config.RemoteConfigElement.ConnectorDescriptor;
import org.alfresco.web.config.RemoteConfigElement.EndpointDescriptor;
import org.alfresco.web.config.RemoteConfigElement.IdentityType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides a single place in the API where developers can go to instantiate
 * connectors to endpoints for given users.
 * 
 * Connectors are wrappers around Clients that provision credentials from the
 * credential vaults to the Client objects.
 * 
 * @author muzquiano
 */
public class ConnectorFactory
{
    protected static Log logger = LogFactory.getLog(ConnectorFactory.class);

    // well known connectors
    public final static String CONNECTOR_ALFRESCO = "alfresco";
    public final static String CONNECTOR_HTTP = "http";

    private static ConnectorFactory factory = null;

    private ConfigService configService;

    private static HashMap<String, Object> cache = new HashMap<String, Object>(
            8, 1.0f);

    /**
     * Returns an instance of the ConnectorFactory.
     * 
     * This method exists so that a ConnectorFactory can be created from within
     * Java application code. You simple need to pass in a valid Alfresco
     * ConfigService instance.
     * 
     * You should not need more than one instance of a ConnectorFactory per
     * instance of ConfigService.
     * 
     * @param configService the config service
     * 
     * @return the Connector factory
     */
    public synchronized static ConnectorFactory getInstance(
            ConfigService configService)
    {
        if (factory == null)
        {
            factory = new ConnectorFactory();
            factory.setConfigService(configService);
        }
        return factory;
    }

    /**
     * Instantiates a new connector factory.
     * 
     * This constructor is provided so as to facilitate placement of
     * ConnectorFactory definitions within Spring configuration files.
     */
    public ConnectorFactory()
    {
    }

    /**
     * Instantiates a new connector factory.
     * 
     * This constructor is provided so as to facilitate placement of
     * ConnectorFactory definitions within Spring configuration files.
     * 
     * @param configService
     */
    public ConnectorFactory(ConfigService configService)
    {
        setConfigService(configService);
    }

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

    /**
     * Constructs a Connector to the given endpoint id
     * 
     * No user context is provided No authenticator override is provided. The
     * Connector's default authenticator will be used.
     * 
     * This will construct a basic Connector that will either impersonate a
     * specified user (as provided by the endpoint definition) or it will act as
     * an anonymous, unauthenticated user.
     * 
     * @param endpointId the endpoint id
     * 
     * @return the connector
     * 
     * @throws Exception *
     * @throws RemoteConfigException the remote config exception
     */
    public Connector connector(String endpointId) throws RemoteConfigException
    {
        return connector(endpointId, null, null);
    }

    /**
     * Constructs a Connector to the given endpoint id.
     * 
     * User context is provided so that if the endpoint requires the current
     * user's credentials to be passed in, they can be
     * 
     * No authenticator override is provided. The Connector's default
     * authenticator will be used.
     * 
     * @param endpointId the endpoint id
     * @param credentialId the credential id
     * @param credentialVault the credential vault
     * 
     * @return the connector
     * 
     * @throws Exception *
     * @throws RemoteConfigException the remote config exception
     */
    public Connector connector(String endpointId, String credentialId,
            CredentialVault credentialVault) throws RemoteConfigException
    {
        return connector(endpointId, credentialId, credentialVault, null);
    }

    /**
     * Constructs a Connector to the given endpoint id.
     * 
     * User context is provided so that if the endpoint requires the current
     * user's credentials to be passed in, they can be
     * 
     * The authenticator with the given id will be used instead of the default
     * authenticator. In general, you will not need to override the
     * authenticator though you may wish to if you wish for a particular
     * handshake sequence to occur.
     * 
     * @param endpointId the endpoint id
     * @param authId the auth id
     * @param credentialId the credential id
     * @param credentialVault the credential vault
     * 
     * @return the connector
     * 
     * @throws RemoteConfigException the remote config exception
     */
    public Connector connector(String endpointId, String credentialId,
            CredentialVault credentialVault, String authId)
            throws RemoteConfigException
    {
        // get the remote configuration block
        RemoteConfigElement remoteConfig = (RemoteConfigElement) getConfigService().getConfig(
                "Remote").getConfigElement("remote");
        if (remoteConfig == null)
        {
            throw new RemoteConfigException(
                    "The 'Remote' configuration was not found, unable to lookup the endpoint definition.");
        }

        // load the endpoint
        EndpointDescriptor endpointDescriptor = remoteConfig.getEndpointDescriptor(endpointId);
        if (endpointDescriptor == null)
        {
            throw new RemoteConfigException(
                    "Unable to find endpoint definition for endpoint id: " + endpointId);
        }

        // load the connector
        String connectorId = (String) endpointDescriptor.getConnectorId();
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

        // load the authenticator onto the connector
        if (authId == null)
        {
            authId = connectorDescriptor.getAuthenticatorId();
        }
        if (authId != null)
        {
            AuthenticatorDescriptor authDescriptor = remoteConfig.getAuthenticatorDescriptor(authId);
            if (authDescriptor == null)
            {
                throw new RemoteConfigException(
                        "Unable to find authenticator definition for authenticator id: " + authId + " on connector id: " + connectorId);
            }
            String authClass = authDescriptor.getImplementationClass();
            Authenticator authenticator = _getAuthenticator(authClass);
            if (authenticator == null)
            {
                throw new RemoteConfigException(
                        "Unable to construct Authenticator for class: " + authClass);
            }

            // place authenticator onto the connector
            connector.setAuthenticator(authenticator);
        }

        // set credentials onto the connector
        // credentials are either "declared", "user", or "none":
        // "declared" indicates that pre-set fixed declarative user credentials
        // are to be used
        // "user" indicates that the current user's credentials should
        // be drawn from the vault and used
        // "none" means that we don't include any credentials
        IdentityType identity = endpointDescriptor.getIdentity();
        switch (identity)
        {
            case DECLARED:
            {
                String username = (String) endpointDescriptor.getUsername();
                String password = (String) endpointDescriptor.getPassword();
                String bindingKey = endpointId + "_" + username;
                Credentials credentials = null;

                if (credentialVault != null)
                {
                    credentials = (Credentials) credentialVault.retrieve(bindingKey);
                    if (credentials == null)
                    {
                        credentials = new SimpleCredentials(bindingKey);
                        credentials.setProperty(
                                Credentials.CREDENTIAL_ALF_USERNAME, username);
                        credentials.setProperty(
                                Credentials.CREDENTIAL_ALF_PASSWORD, password);
                        credentialVault.store(bindingKey, credentials);
                    }
                }
                else
                {
                    credentials = new SimpleCredentials(bindingKey);
                    credentials.setProperty(Credentials.CREDENTIAL_ALF_USERNAME,
                            username);
                    credentials.setProperty(Credentials.CREDENTIAL_ALF_PASSWORD,
                            password);
                }
                connector.setCredentials(credentials);
                break;
            }

            case USER:
            {
                String bindingKey = endpointId + "_" + credentialId;

                Credentials credentials = null;
                if (credentialVault != null)
                {
                    credentials = credentialVault.retrieve(bindingKey);
                }

                if (credentials != null)
                {
                    connector.setCredentials(credentials);
                }
                else
                {
                    logger.warn("Unable to find credentials for binding key: " + bindingKey);
                }
            }
        }

        return connector;
    }

    /**
     * Authenticator.
     * 
     * @param id the id
     * 
     * @return the authenticator
     * 
     * @throws RemoteConfigException the remote config exception
     */
    public Authenticator authenticator(String id) throws RemoteConfigException
    {
        RemoteConfigElement remoteConfig = (RemoteConfigElement) getConfigService().getConfig(
                "Remote").getConfigElement("remote");
        if (remoteConfig == null)
        {
            throw new RemoteConfigException(
                    "Unable to find remote configuration, cannot load authenticator settings");
        }

        AuthenticatorDescriptor descriptor = remoteConfig.getAuthenticatorDescriptor(id);
        if (descriptor == null)
        {
            throw new RemoteConfigException(
                    "Unable to find authenticator for id: " + id);
        }

        String className = descriptor.getImplementationClass();
        return _getAuthenticator(className);
    }

    /**
     * _get authenticator.
     * 
     * @param className the class name
     * 
     * @return the authenticator
     */
    protected static synchronized Authenticator _getAuthenticator(
            String className)
    {
        Authenticator auth = (Authenticator) cache.get(className);
        if (auth == null)
        {
            auth = (Authenticator) ReflectionHelper.newObject(className);

            cache.put(className, auth);
        }

        return auth;
    }

    /**
     * Builds the connector.
     * 
     * @param descriptor the descriptor
     * @param url the url
     * 
     * @return the connector
     */
    protected static Connector buildConnector(ConnectorDescriptor descriptor,
            String url)
    {
        Class[] argTypes = new Class[] { descriptor.getClass(), url.getClass() };
        Object[] args = new Object[] { descriptor, url };
        return (Connector) ReflectionHelper.newObject(
                descriptor.getImplementationClass(), argTypes, args);
    }
}
