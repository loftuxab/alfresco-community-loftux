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
package org.alfresco.web.scripts;

import java.util.Map;

import org.alfresco.config.ConfigService;
import org.alfresco.connector.Connector;
import org.alfresco.connector.ConnectorFactory;
import org.alfresco.connector.Response;
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.web.config.RemoteConfigElement;
import org.alfresco.web.config.RemoteConfigElement.EndpointDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Root-scope class that provides useful functions for working with
 * endpoints, connectors and credentials.
 * 
 * This class also implements methods from the Connector interface
 * so as to allow application developers to use it straight away
 * against the configured default endpoint.
 * 
 * @author muzquiano
 */
public class ScriptRemote
{
    private static final Log logger = LogFactory.getLog(ScriptRemote.class);

    private ConfigService configService;

    /**
     * Instantiates a new script remote.
     * 
     * @param configService the config service
     */
    protected ScriptRemote(ConfigService configService)
    {
        this.configService = configService;
    }

    /**
     * Constructs a RemoteClient to a default endpoint (if configured)
     * If a default endpoint is not configured, null will be returned.
     * 
     * @return the remote client
     */
    public Connector connect()
    {
        Connector connector = null;

        // Check whether a remote configuration has been provided
        RemoteConfigElement remoteConfig = (RemoteConfigElement) configService.getConfig("Remote").getConfigElement("remote");
        if(remoteConfig != null)
        {
            // See if we have a default endpoint id
            String defaultEndpointId = remoteConfig.getDefaultEndpointId();
            if(defaultEndpointId != null)
            {
                // Construct for this endpoint id
                connector = connect(defaultEndpointId);
            }
        }

        return connector;
    }

    /**
     * Constructs a RemoteClient to a specific endpoint.
     * If the endpoint does not exist, null is returned.
     * 
     * @param endpointId the endpoint id
     * 
     * @return the remote client
     */
    public Connector connect(String endpointId)
    {
        Connector connector = null;

        // Check whether a remote configuration has been provided    	
        RemoteConfigElement remoteConfig = (RemoteConfigElement) configService.getConfig("Remote").getConfigElement("remote");
        if(remoteConfig != null)
        {        	
            // check whether we have a descriptor for this endpoint
            EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(endpointId);
            if(descriptor == null)
            {
                logger.error("No endpoint descriptor found for endpoint id: " + endpointId);
            }
            else
            {
                // construct a connector to this endpoint
                try
                {
                    // TODO:  Load current user credentials from the vault
                    // At present, this does not seem possible to do since
                    // the web script framework does not maintain any
                    // notion of the current user
                    //
                    // The best we can do is construct anonymous connections
                    // or connections to endpoints that have "declared" user
                    // settings (which is to say, forced usernames and
                    // passwords within the configuration file)
                    connector = ConnectorFactory.getInstance(configService).connector(endpointId);
                }
                catch (RemoteConfigException rce)
                {
                    logger.error("Unable to open connection to endpoint: " + endpointId, rce);
                }
            }
        }

        return connector;
    }


    ////////////////////////////////////////////////////////////////
    //
    // Connector pass-thru methods to work with default Connector
    //
    ////////////////////////////////////////////////////////////////


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
     * Invoke a specific URI on the default endpoint
     * Pass in the given parameters
     * 
     * @param uri
     * @param parameters
     * @return
     */
    public Response call(String uri, Map parameters)
    {
        return this.connect().call(uri, parameters);
    }

    /**
     * Invoke a specific URI on the default endpoint
     * Pass in the given parameters
     * Apply the provided headers
     * 
     * @param uri
     * @param parameters
     * @param headers
     * @return
     */
    public Response call(String uri, Map parameters, Map headers)
    {
        return this.connect().call(uri, parameters, headers);
    }


    // query and interrogation

    public String[] getEndpointIds()
    {
        RemoteConfigElement remoteConfig = (RemoteConfigElement) configService.getConfig("Remote").getConfigElement("remote");
        if(remoteConfig == null)
        {
            return new String[] { };
        }

        return remoteConfig.getEndpointIds();
    }

    public String getEndpointName(String id)
    {
        RemoteConfigElement remoteConfig = (RemoteConfigElement) configService.getConfig("Remote").getConfigElement("remote");
        if(remoteConfig == null)
        {
            return null;
        }

        EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(id);
        if(descriptor == null)
        {
            return null;
        }

        return descriptor.getName();    	
    }

    public String getEndpointDescription(String id)
    {
        RemoteConfigElement remoteConfig = (RemoteConfigElement) configService.getConfig("Remote").getConfigElement("remote");
        if(remoteConfig == null)
        {
            return null;
        }

        EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(id);
        if(descriptor == null)
        {
            return null;
        }

        return descriptor.getDescription();    	
    }

}
