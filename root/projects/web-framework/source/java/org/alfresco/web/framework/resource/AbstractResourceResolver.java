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
package org.alfresco.web.framework.resource;

import java.util.StringTokenizer;

import org.alfresco.connector.Response;
import org.alfresco.tools.WebUtil;
import org.alfresco.web.framework.exception.ResourceMetadataException;
import org.alfresco.web.scripts.ScriptRemoteConnector;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract implementation of a resource resolver
 * 
 * This provides default methods for retrieving the browser-friendly URIs
 * of resources by assuming that they are located behind something
 * accessible via the endpoint proxy servlet.
 * 
 * @author muzquiano
 */
public abstract class AbstractResourceResolver implements ResourceResolver
{
    private static final String DEFAULT_ALFRESCO_ENDPOINT_ID = "alfresco";

    private static Log logger = LogFactory.getLog(AbstractResourceResolver.class);
    
    protected Resource resource;

    public AbstractResourceResolver(Resource resource)
    {
        this.resource = resource;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceResolver#getBrowserDownloadURI(org.alfresco.web.site.RequestContext)
     */
    public String getBrowserDownloadURI(RequestContext context)
    {
        String url = context.getRequest().getContextPath() + "/proxy/{endpoint}" + getDownloadURI(context);

        String ep = this.resource.getEndpoint();
        if (ep == null)
        {
            ep = FrameworkHelper.getRemoteConfig().getDefaultEndpointId();
        }
        if (ep == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Unable to determine endpoint binding, resorting to fixed: " + DEFAULT_ALFRESCO_ENDPOINT_ID);
            
            ep = DEFAULT_ALFRESCO_ENDPOINT_ID;
        }
        url = url.replace("{endpoint}", ep);

        // if the URL starts with "/", then make it absolute
        url = WebUtil.toFullyQualifiedURL(context, url);
        
        return url;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceResolver#getBrowserMetadataURI(org.alfresco.web.site.RequestContext)
     */
    public String getBrowserMetadataURI(RequestContext context)
    {
        String url = context.getRequest().getContextPath() + "/proxy/{endpoint}" + getMetadataURI(context);

        String ep = this.resource.getEndpoint();
        if (ep == null)
        {
            ep = FrameworkHelper.getRemoteConfig().getDefaultEndpointId();
        }        
        if (ep == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Unable to determine endpoint binding, resorting to fixed: " + DEFAULT_ALFRESCO_ENDPOINT_ID);

            ep = DEFAULT_ALFRESCO_ENDPOINT_ID;
        }
        url = url.replace("{endpoint}", ep);
        
        // if the URL starts with "/", then make it absolute
        url = WebUtil.toFullyQualifiedURL(context, url);        

        return url;
    }

    // helper method
    protected static String toNodeRefString(String nodeString)
    {
        // the incoming nodeString could be either of these formats
        //
        // workspace/SpacesStore/nodeId
        // workspace://SpacesStore/nodeId

        // we must convert it to workspace://SpacesStore/nodeId

        // a quick test
        if (nodeString.indexOf("://") > -1)
        {
            // assume that it is already in the correct format
            return nodeString;
        }

        String storeType = null;
        String storeId = null;
        String nodeId = null;

        StringTokenizer tokenizer = new StringTokenizer(nodeString, "/");
        storeType = tokenizer.nextToken();
        storeId = tokenizer.nextToken();
        nodeId = tokenizer.nextToken();

        return storeType + "://" + storeId + "/" + nodeId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceResolver#getRawMetadata(org.alfresco.web.site.RequestContext)
     */
    public String getRawMetadata(RequestContext context)
            throws ResourceMetadataException
    {
        String metadata = null;
        
        ScriptRemoteConnector connector = FrameworkHelper.getScriptRemote().connect(this.resource.getEndpoint());

        Response response = connector.get(this.getMetadataURI(context));
        if (response.getStatus().getCode() != 200)
        {
            throw new ResourceMetadataException(
                    "Unable to load raw metadata for resource: "
                            + this.resource.getId());
        }

        metadata = response.getResponse();

        return metadata;
    }
}
