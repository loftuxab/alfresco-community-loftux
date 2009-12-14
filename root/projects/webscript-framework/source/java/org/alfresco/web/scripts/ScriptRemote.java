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
package org.alfresco.web.scripts;

import org.springframework.extensions.config.ConfigService;
import org.alfresco.connector.Connector;
import org.alfresco.connector.ConnectorProvider;
import org.alfresco.connector.ConnectorProviderImpl;
import org.alfresco.connector.Response;
import org.alfresco.connector.exception.ConnectorProviderException;
import org.alfresco.web.config.RemoteConfigElement;
import org.alfresco.web.config.RemoteConfigElement.EndpointDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Root-scope class that provides useful functions for working with endpoints,
 * connectors and credentials.
 * 
 * This class also implements methods from the Connector interface so as to
 * allow application developers to use it straight away against the configured
 * default endpoint.
 * 
 * @author muzquiano
 */
public class ScriptRemote
{
    private static final Log logger = LogFactory.getLog(ScriptRemote.class);

    private ConfigService configService;
    private ConnectorProvider connectorProvider;

    /**
     * Sets the configuration service.
     * 
     * @param configService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
        
    /**
     * Sets the connector provider.
     * 
     * @param connectorProvider
     */
    public void setConnectorProvider(ConnectorProvider connectorProvider)
    {
        this.connectorProvider = connectorProvider;
    }

    /**
     * Constructs a RemoteClient to a default endpoint (if configured) If a
     * default endpoint is not configured, null will be returned.
     * 
     * @return the remote client
     */
    public ScriptRemoteConnector connect()
    {
        ScriptRemoteConnector remoteConnector = null;

        // Check whether a remote configuration has been provided
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if (remoteConfig != null)
        {
            // See if we have a default endpoint id
            String defaultEndpointId = remoteConfig.getDefaultEndpointId();
            if (defaultEndpointId != null)
            {
                // Construct for this endpoint id
                remoteConnector = connect(defaultEndpointId);
            }
        }

        return remoteConnector;
    }

    /**
     * Constructs a RemoteClient to a specific endpoint. If the endpoint does
     * not exist, null is returned.
     * 
     * @param endpointId the endpoint id
     * 
     * @return the remote client
     */
    public ScriptRemoteConnector connect(String endpointId)
    {
        ScriptRemoteConnector remoteConnector = null;

        RemoteConfigElement remoteConfig = getRemoteConfig();
        if (remoteConfig != null)
        {
            // check whether we have a descriptor for this endpoint
            EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(endpointId);
            if (descriptor == null)
            {
                logger.error("No EndPoint descriptor configuration found for ID: " + endpointId);
            }
            else
            {
                // if a connector provider has not been assigned, we can use a
                // default provider which provides simple stateless access
                if (connectorProvider == null)
                {
                    connectorProvider = new ConnectorProviderImpl();                    
                }
                
                try
                {
                    // construct a connector to this endpoint
                    Connector connector = connectorProvider.provide(endpointId);
                    remoteConnector = new ScriptRemoteConnector(connector, descriptor);
                }
                catch (ConnectorProviderException cpe)
                {
                    logger.error("Unable to provision connector for endpoint: " + endpointId);
                }
            }
        }

        return remoteConnector;
    }

    // //////////////////////////////////////////////////////////////
    //
    // Connector pass-thru methods to work with default Connector
    //
    // //////////////////////////////////////////////////////////////

    /**
     * Invoke a specific URI on the default endpoint
     * 
     * @param uri the uri
     * 
     * @return the response
     */
    public Response call(String uri)
    {
        return this.connect().call(uri);
    }

    /**
     * Returns a list of the application endpoint ids
     * 
     * @return
     */
    public String[] getEndpointIds()
    {
        String[] endpointIds = null;
        
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if(remoteConfig != null)
        {
            endpointIds = remoteConfig.getEndpointIds();
        }
        
        return endpointIds;
    }
    
    /**
     * Returns the name of an endpoint
     * 
     * @param id
     * @return
     */
    public String getEndpointName(String id)
    {
        String name = null;
        
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if(remoteConfig != null)
        {
            EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(id);
            if(descriptor != null)
            {
                name = descriptor.getName();
            }
        }

        return name;
    }

    /**
     * Returns the description of an endpoint
     * 
     * @param id
     * @return
     */
    public String getEndpointDescription(String id)
    {
        String description = null;
        
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if(remoteConfig != null)
        {
            EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(id);
            if(descriptor != null)
            {
                description = descriptor.getDescription();
            }
        }

        return description;
    }    

    public boolean isEndpointPersistent(String id)
    {
        boolean persistent = false;
        
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if(remoteConfig != null)
        {
            EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(id);
            if(descriptor != null)
            {
                persistent = descriptor.getPersistent();
            }
        }

        return persistent;
    }    

    /**
     * Returns the configured URL for the given endpoint
     * 
     * @param id the id
     * 
     * @return the endpoint url
     */
    public String getEndpointURL(String id)
    {
        String url = null;
        
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if (remoteConfig != null)
        {
            EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(id);
            if (descriptor != null)
            {
                url = descriptor.getEndpointUrl();
            }
        }

        return url;
    }
    
    /**
     * @return RemoteConfigElement
     */
    private RemoteConfigElement getRemoteConfig()
    {
        RemoteConfigElement remoteConfig = (RemoteConfigElement)configService.getConfig(
                "Remote").getConfigElement("remote");
        return remoteConfig;
    }
}
