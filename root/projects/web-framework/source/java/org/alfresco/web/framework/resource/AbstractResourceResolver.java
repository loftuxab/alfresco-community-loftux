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

import javax.servlet.http.HttpServletRequest;

import org.alfresco.connector.Response;
import org.alfresco.web.scripts.ScriptRemoteConnector;
import org.alfresco.web.scripts.WebFrameworkScriptRemote;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.exception.RequestContextException;

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

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceResolver#getProxiedDownloadURI(javax.servlet.http.HttpServletRequest)
     */
    public String getProxiedDownloadURI(HttpServletRequest request)
    {
        String url = "/proxy/{endpoint}" + getDownloadURI(request);         

        String ep = this.resource.getEndpoint();
        if (ep == null)
        {
            ep = "alfresco";
        }
        url = url.replace("{endpoint}", ep);
        
        return url;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceResolver#getProxiedMetadataURI(javax.servlet.http.HttpServletRequest)
     */
    public String getProxiedMetadataURI(HttpServletRequest request)
    {
        String url = "/proxy/{endpoint}" + getMetadataURI(request);

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
        // nodestrings look like
        // workspace/SpacesStore/abcdefah-123123 etc
        
        String storeType = null;
        String storeId = null;
        String nodeId = null;
        
        StringTokenizer tokenizer = new StringTokenizer(nodeString, "/");
        storeType = tokenizer.nextToken();
        storeId = tokenizer.nextToken();
        nodeId = tokenizer.nextToken();
        
        return storeType + "://" + storeId + "/" + nodeId;        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceResolver#getRawMetadata(javax.servlet.http.HttpServletRequest)
     */
    public String getRawMetadata(HttpServletRequest request)
    {
        String metadata = null;
        
        RequestContext requestContext = null;
        try
        {
            requestContext = RequestUtil.getRequestContext(request);
            
            WebFrameworkScriptRemote remote = new WebFrameworkScriptRemote(requestContext);            
            ScriptRemoteConnector connector = remote.connect(this.resource.getEndpoint());
            
            Response response = connector.get(this.getMetadataURI(request));
            
            return response.getResponse();
            
        }
        catch (RequestContextException rce)
        {
            // TODO: handle
        }

        return metadata;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceResolver#getMetadata(javax.servlet.http.HttpServletRequest)
     */
    public abstract String getMetadata(HttpServletRequest request);
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceResolver#getDownloadURI(javax.servlet.http.HttpServletRequest)
     */
    public abstract String getDownloadURI(HttpServletRequest request);

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceResolver#getMetadataURI(javax.servlet.http.HttpServletRequest)
     */
    public abstract String getMetadataURI(HttpServletRequest request);
    
}
