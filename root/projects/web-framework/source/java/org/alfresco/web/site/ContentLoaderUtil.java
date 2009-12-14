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
package org.alfresco.web.site;

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.surf.util.ReflectionHelper;
import org.alfresco.web.config.WebFrameworkConfigElement.ResourceLoaderDescriptor;
import org.alfresco.web.framework.exception.ContentLoaderException;
import org.alfresco.web.framework.exception.ResourceLoaderException;
import org.alfresco.web.framework.resource.Resource;
import org.alfresco.web.framework.resource.ResourceContent;
import org.alfresco.web.framework.resource.ResourceLoader;

/**
 * Helper class for loading content from Content Loader implementations.
 * 
 * @author muzquiano
 */
public class ContentLoaderUtil
{
    protected static Map<String, ResourceLoader> loaderCache;

    /**
     * Considers a piece of content and walks through all of the available
     * Content Loaders to find one that can load this content.
     * 
     * The Content Loaders are cached to help speed up performance.
     * 
     * With this method, the endpoint is assumed to be the default endpoint.
     * 
     * @param context the context
     * @param objectId the object id
     * 
     * @return the content
     * 
     * @throws ContentLoaderException the content loader exception
     */
    public synchronized static ResourceContent loadContent(
            RequestContext context, String objectId)
            throws ContentLoaderException
    {
        String endpointId = context.getRequest().getParameter("endpoint");
        if (endpointId == null)
        {
            endpointId = FrameworkHelper.getRemoteConfig().getDefaultEndpointId();
        }
        return _loadContent(context, objectId, endpointId);
    }

    public synchronized static ResourceContent loadContent(
            RequestContext context, String objectId, String endpointId)
            throws ContentLoaderException
    {
        return _loadContent(context, objectId, endpointId);
    }

    private synchronized static ResourceContent _loadContent(
            RequestContext context, String objectId, String endpointId)
            throws ContentLoaderException
    {
        if (loaderCache == null)
        {
            loaderCache = new HashMap<String, ResourceLoader>(10, 1.0f);
        }

        ResourceContent content = null;

        String[] ids = FrameworkHelper.getConfig().getResourceLoaderIds();
        int i = 0;
        while ((i < ids.length) && (content == null))
        {
            ResourceLoaderDescriptor descriptor = (ResourceLoaderDescriptor)FrameworkHelper.getConfig().getResourceLoaderDescriptor(
                    ids[i]);
            if (descriptor != null)
            {
                String loaderClassName = (String) descriptor.getImplementationClass();
                ResourceLoader loader = (ResourceLoader) loaderCache.get(loaderClassName);
                if (loader == null)
                {
                    Class[] argTypes = new Class[] { String.class };
                    Object[] args = new Object[] { endpointId };
                    loader = (ResourceLoader) ReflectionHelper.newObject(
                            loaderClassName, argTypes, args);
                    if (loader == null)
                    {
                        throw new ContentLoaderException(
                                "Unable to instantiate resource loader for class: " + loaderClassName);
                    }

                    loaderCache.put(loaderClassName, loader);
                }

                // first check whether this loader is working against the same
                // endpoint
                if (endpointId != null && endpointId.equals(loader.getEndpointId()))
                {
                    // check whether the loader can handle this object
                    if (loader.canHandle(objectId))
                    {
                        Resource resource = null;
                        try
                        {
                            resource = loader.load(context, objectId);

                            if (resource != null)
                            {
                                content = resource.getContent(context);
                            }
                        }
                        catch (ResourceLoaderException rle)
                        {
                            throw new ContentLoaderException(
                                    "Unable to retrieve resource for object: " + objectId,
                                    rle);
                        }
                    }
                }
            }

            // iterate through loop
            i++;
        }

        return content;
    }
}
