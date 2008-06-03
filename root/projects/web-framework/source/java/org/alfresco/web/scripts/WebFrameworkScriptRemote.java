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

import org.alfresco.config.ConfigService;
import org.alfresco.connector.Connector;
import org.alfresco.connector.CredentialVault;
import org.alfresco.connector.Response;
import org.alfresco.connector.User;
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.web.config.RemoteConfigElement;
import org.alfresco.web.config.RemoteConfigElement.EndpointDescriptor;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A version of the ScriptRemote object from the Web Script Framework
 * that takes advantage of the credential vault facility.
 * 
 * This version is aware of who the current user is (from the Web Framework)
 * and can appropriately look up Credential objects to bind to the
 * provisioned Connectors.
 * 
 * @author muzquiano
 */
public class WebFrameworkScriptRemote
{
    private static final Log logger = LogFactory.getLog(WebFrameworkScriptRemote.class);
    
    private RequestContext context;
    
    /**
     * Instantiates a new script remote.
     * 
     * @param configService the config service
     */
    public WebFrameworkScriptRemote(RequestContext context)
    {
    	this.context = context;
    }
    
    /**
     * Returns the request context instance
     * 
     * @return
     */
    protected RequestContext getRequestContext()
    {
    	return this.context;
    }
        
    /**
     * Constructs a ScriptRemoteConnector to a default endpoint (if configured)
     * If a default endpoint is not configured, null will be returned.
     * 
     * @return the remote connector
     */
    public ScriptRemoteConnector connect()
    {
    	ScriptRemoteConnector remoteConnector = null;
    	
    	// Check whether a remote configuration has been provided
    	RemoteConfigElement remoteConfig = FrameworkHelper.getRemoteConfig();
        if(remoteConfig != null)
        {
        	// See if we have a default endpoint id
        	String defaultEndpointId = remoteConfig.getDefaultEndpointId();
        	if(defaultEndpointId != null)
        	{
        		// Construct for this endpoint id
                remoteConnector = connect(defaultEndpointId);
        	}
        }
        
        return remoteConnector;
    }

    /**
     * Constructs a ScirptRemoteConnector to a specific endpoint.
     * If the endpoint does not exist, null is returned.
     * 
     * @param endpointId the endpoint id
     * 
     * @return the remote client
     */
    public ScriptRemoteConnector connect(String endpointId)
    {
    	ScriptRemoteConnector remoteConnector = null;
    	
    	// Check whether a remote configuration has been provided    	
    	RemoteConfigElement remoteConfig = FrameworkHelper.getRemoteConfig();
        if (remoteConfig != null)
        {        	
        	// check whether we have a descriptor for this endpoint
    		EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(endpointId);
    		if (descriptor == null)
    		{
    			logger.error("No endpoint descriptor found for endpoint id: " + endpointId);
    		}
    		else
    		{
    			// construct a connector to this endpoint
    			try
    			{
    				// config service
    				ConfigService configService = FrameworkHelper.getConfigService();
    				
    				// the vault
    				CredentialVault vault = getRequestContext().getCredentialVault();
    				
    				if(logger.isDebugEnabled())
    					logger.debug("Found credential vault: " + vault);

    				// check whether we have a current user
    				User user = getRequestContext().getUser();
    				if (user == null || vault == null)
    				{
    					if(logger.isDebugEnabled())
    						logger.debug("No user was found, creating unauthenticated connector");

    					// return the non-credential'ed connector to this endpoint
                        Connector connector = FrameworkHelper.getConnector(endpointId);                       
                        remoteConnector = new ScriptRemoteConnector(connector);
    				}
    				else
    				{
    					if(logger.isDebugEnabled())
    						logger.debug("User '" + user.getId() + "' was found, creating authenticated connector");

    					// return the credential'ed connector to this endpoint
                        Connector connector = FrameworkHelper.getConnector(context, endpointId);                        
                        remoteConnector = new ScriptRemoteConnector(connector);
    				}
    			}
    			catch (RemoteConfigException rce)
    			{
    				logger.error("Unable to open connection to endpoint: " + endpointId, rce);
    			}
    		}
        }
        
        return remoteConnector;
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
}
