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

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.connector.Connector;
import org.alfresco.connector.Response;
import org.alfresco.web.config.WebFrameworkConfigElement.ResourceResolverDescriptor;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.resource.TransientResourceImpl;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.site.servlet.VirtualizedContentRetrievalServlet;

/**
 * Helper object for dealing with the web application's environment.
 * 
 * This object can be used on both the production and preview tiers to gain access to the correct
 * web application mount points and more.
 * 
 * @author muzquiano
 */
public final class ScriptWebApplication extends ScriptBase
{
    /**
     * Constructs a new ScriptWebApplication object.
     * 
     * @param context   The RenderContext instance for the current request
     */
    public ScriptWebApplication(RenderContext context)
    {
        super(context);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptBase#buildProperties()
     */
    protected ScriptableMap<String, Serializable> buildProperties()
    {
        return null;
    }


    // --------------------------------------------------------------
    // JavaScript Properties
    
    /**
     * Returns the root web application context
     */
    public String getContext()
    {        
        StringBuilder builder = new StringBuilder(512);
        
        HttpServletRequest request = this.context.getRequest();
        
        // on the preview tier, we'll plug in a passthru to the AVM remote store
        if (FrameworkHelper.getConfig().isPreviewEnabled())
        {
            // path to web application
            builder.append(request.getContextPath());
            
            // virtualized content retrieval proxy
            builder.append("/v");            
        }
        else
        {
            ResourceResolverDescriptor descriptor = FrameworkHelper.getConfig().getResourceResolverDescriptor("webapp");
            if (descriptor != null)
            {
                String aliasUri = descriptor.getStringProperty("alias-uri");
                
                // if the alias uri is empty, we'll just use the context path
                if (aliasUri == null)
                {
                    builder.append(request.getContextPath());
                }
                else
                {
                    if (aliasUri.startsWith("/"))
                    {
                        // if the alias uri starts with "/", then
                        // we'll assume it is root-relative
                        builder.append(aliasUri);
                    }
                    else
                    {
                        // if the alias uri doesn't start with "/", then
                        // we'll assume it is relative to the context path
                        builder.append(request.getContextPath());
                        builder.append("/");
                        builder.append(aliasUri);
                    }
                }
            }
        }
        
        return builder.toString();
    }  

    /**
     * Performs a server-side include of a web asset
     * 
     * This uses the default endpoint
     * 
     * @param path
     * 
     * @return
     */    
    public String include(String path)
    {
        return include(path, null);
    }
    
    /**
     * Performs a server-side include of a web asset
     * 
     * If this is running in a preview tier, this turns into a remote call
     * over to the avmstore look up.
     * 
     * Otherwise, it turns into a wrapped server-side include.
     * 
     * The result string is returned.
     * 
     * Value paths are:
     * 
     *    /a/b/c.gif
     *    /images/test.jpg
     * 
     * @param path
     * @param endpointId
     * 
     * @return
     */
    public String include(String path, String endpointId)
    {
        String buffer = null;
        
        HttpServletRequest request = context.getRequest();
                
        try
        {            
            if (FrameworkHelper.getConfig().isPreviewEnabled())
            {
                String storeId = (String) context.getValue(WebFrameworkConstants.STORE_ID_REQUEST_CONTEXT_NAME);
                String webappId = (String) context.getValue(WebFrameworkConstants.WEBAPP_ID_REQUEST_CONTEXT_NAME);
                
                // virtualized content retrieval
                buffer = VirtualizedContentRetrievalServlet.retrieveAsString(context, path, endpointId, storeId, webappId);
            }
            else
            {
                // construct a transient resource to represent this asset
                TransientResourceImpl res = new TransientResourceImpl(path, "webapp");
                res.setValue(path);
                res.setEndpoint(endpointId);
                String uri = res.getBrowserDownloadURI(context);
                
                // create a connector to the resource
                Connector connector = FrameworkHelper.getConnector(context, "http");

                // pull back the data
                Response response = connector.call(uri);
                buffer = response.getResponse();    
            }
            
            // some post treatment of the buffer
            buffer = buffer.replace("${app.context}", this.getContext());
        }
        catch (Exception ex)
        {
            FrameworkHelper.getLogger().warn("Unable to include '" + path + "', " + ex.getMessage());
        }
        
        return buffer;
    }
}
