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

import javax.servlet.http.HttpServletRequest;

/**
 * @author muzquiano
 */
public class ResourceImpl extends AbstractResource 
{
    protected ResourceResolver resolver = null;

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
        if ("site".equals(type))
        {
            resolver = new AlfrescoSiteResourceResolver(this);
        }
        else if ("space".equals(type))
        {
            resolver = new AlfrescoSpaceResourceResolver(this);
        }
        else if ("webapp".equals(type))
        {
            resolver = new AlfrescoWebProjectResourceResolver(this);
        }
        else
        {
            resolver = new URIResourceResolver(this);
        }
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getDownloadURI(javax.servlet.http.HttpServletRequest)
     */
    public String getDownloadURI(HttpServletRequest request)
    {
        return resolver.getDownloadURI(request);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getProxiedDownloadURI(javax.servlet.http.HttpServletRequest)
     */
    public String getProxiedDownloadURI(HttpServletRequest request)
    {
        return resolver.getProxiedDownloadURI(request);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getMetadataURI(javax.servlet.http.HttpServletRequest)
     */
    public String getMetadataURI(HttpServletRequest request)
    {
        return resolver.getMetadataURI(request);
    }        

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getProxiedMetadataURI(javax.servlet.http.HttpServletRequest)
     */
    public String getProxiedMetadataURI(HttpServletRequest request)
    {
        return resolver.getProxiedMetadataURI(request);
    }        
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#rawMetadata(javax.servlet.http.HttpServletRequest)
     */
    public String getRawMetadata(HttpServletRequest request)
    {
        return resolver.getRawMetadata(request);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#metadata(javax.servlet.http.HttpServletRequest)
     */
    public String getMetadata(HttpServletRequest request)
    {
        return resolver.getMetadata(request);
    }
    
}
