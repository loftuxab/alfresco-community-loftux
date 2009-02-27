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

import org.alfresco.connector.Connector;
import org.alfresco.connector.Response;
import org.alfresco.connector.ResponseStatus;
import org.alfresco.connector.exception.ConnectorServiceException;
import org.alfresco.web.framework.exception.ResourceMetadataException;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generic implementation of a resource
 * 
 * @author muzquiano
 */
public class ResourceImpl extends AbstractResource
{
    private static Log logger = LogFactory.getLog(ResourceImpl.class);
    
    protected ResourceResolver resolver = null;
    protected String metadata = null;
    protected String rawMetadata = null;
    protected byte[] bytes = null;

    public ResourceImpl(ResourceStore store, String id)
    {
        super(store, id);

        this.init(getType());
    }

    public ResourceImpl(ResourceStore store, String id, String type)
    {
        super(store, id);

        this.init(type);
    }

    protected void init(String type)
    {
        resolver = FrameworkHelper.getResourceResolver(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.Resource#getDownloadURI(org.alfresco.web.site.RequestContext)
     */
    public String getDownloadURI(RequestContext context)
    {
        return resolver.getDownloadURI(context);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getBrowserDownloadURI(org.alfresco.web.site.RequestContext)
     */
    public String getBrowserDownloadURI(RequestContext context)
    {
        return resolver.getBrowserDownloadURI(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.Resource#getMetadataURI(org.alfresco.web.site.RequestContext)
     */
    public String getMetadataURI(RequestContext context)
    {
        return resolver.getMetadataURI(context);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getBrowserMetadataURI(org.alfresco.web.site.RequestContext)
     */
    public String getBrowserMetadataURI(RequestContext context)
    {
        return resolver.getBrowserMetadataURI(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.Resource#getRawMetadata(org.alfresco.web.site.RequestContext)
     */
    public String getRawMetadata(RequestContext context)
            throws ResourceMetadataException
    {
        if (this.rawMetadata == null)
        {
            this.rawMetadata = resolver.getRawMetadata(context);
        }

        return this.rawMetadata;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.Resource#getMetadata(org.alfresco.web.site.RequestContext)
     */
    public String getMetadata(RequestContext context)
            throws ResourceMetadataException
    {
        if (this.metadata == null)
        {
            this.metadata = resolver.getMetadata(context);
        }

        return this.metadata;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getBytes(org.alfresco.web.site.RequestContext)
     */
    public byte[] getBytes(RequestContext context)
    {
        if (bytes == null)
        {
            reload(context);
        }
        
        return bytes;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#reload(org.alfresco.web.site.RequestContext)
     */
    public void reload(RequestContext context)
    {
        bytes = null;
        
        String browserDownloadUri = this.getBrowserDownloadURI(context);
        
        // escape the string
        String uri = browserDownloadUri.replace(" ", "%20");
        
        // use the http endpoint
        // TODO: use the specific endpoint defined by the resource
        String endpointId = "http";
        
        // open a connector
        Connector connector = null;
        try
        {
            connector = FrameworkHelper.getConnector(context, endpointId);

            // fetch the result
            Response response = connector.call(uri);
            if (response.getStatus().getCode() == ResponseStatus.STATUS_OK)
            {
                bytes = response.getResponse().getBytes();
            }            
        }
        catch (ConnectorServiceException cse)
        {
            if (logger.isDebugEnabled())
                logger.debug("Unable to establish connector for endpoint: " + endpointId + " while loading resource with uri: " + uri, cse);            
        }
    }    
}
