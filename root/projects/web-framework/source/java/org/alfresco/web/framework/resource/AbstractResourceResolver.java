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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework.resource;

import java.util.StringTokenizer;

import org.alfresco.connector.Response;
import org.alfresco.web.framework.exception.ResourceMetadataException;
import org.alfresco.web.scripts.ScriptRemoteConnector;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;

/**
 * Abstract implementation of a resource resolver
 * 
 * @author muzquiano
 */
public abstract class AbstractResourceResolver implements ResourceResolver
{
    protected Resource resource;

    public AbstractResourceResolver(Resource resource)
    {
        this.resource = resource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceResolver#getProxiedDownloadURI(org.alfresco.web.site.RequestContext)
     */
    public String getProxiedDownloadURI(RequestContext context)
    {
        String url = "/proxy/{endpoint}" + getDownloadURI(context);

        String ep = this.resource.getEndpoint();
        if (ep == null)
        {
            ep = "alfresco";
        }
        url = url.replace("{endpoint}", ep);

        return url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceResolver#getProxiedMetadataURI(org.alfresco.web.site.RequestContext)
     */
    public String getProxiedMetadataURI(RequestContext context)
    {
        String url = "/proxy/{endpoint}" + getMetadataURI(context);

        String ep = this.resource.getEndpoint();
        if (ep == null)
        {
            ep = "alfresco";
        }
        url = url.replace("{endpoint}", ep);

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
