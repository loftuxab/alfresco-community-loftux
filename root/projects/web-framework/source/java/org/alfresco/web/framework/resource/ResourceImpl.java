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

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.Resource#getProxiedDownloadURI(org.alfresco.web.site.RequestContext)
     */
    public String getProxiedDownloadURI(RequestContext context)
    {
        return resolver.getProxiedDownloadURI(context);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.Resource#getProxiedMetadataURI(org.alfresco.web.site.RequestContext)
     */
    public String getProxiedMetadataURI(RequestContext context)
    {
        return resolver.getProxiedMetadataURI(context);
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
}
