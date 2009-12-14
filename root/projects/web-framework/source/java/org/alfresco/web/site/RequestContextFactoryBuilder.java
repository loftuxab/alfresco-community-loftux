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
package org.alfresco.web.site;

import org.springframework.extensions.surf.util.ReflectionHelper;
import org.alfresco.web.config.WebFrameworkConfigElement.RequestContextDescriptor;
import org.alfresco.web.site.exception.RequestContextException;

/**
 * Manages the construction of RequestContextFactory objects.
 * <p>
 * The Web Framework configuration could specify one or more
 * RequestContextFactory implementations.
 * 
 * @author muzquiano
 */
public class RequestContextFactoryBuilder
{
    private static final String HTTP_REQUEST_CONTEXT_FACTORY = "org.alfresco.web.site.HttpRequestContextFactory";

    /**
     * Private Constructor - ensure singleton pattern
     */
    private RequestContextFactoryBuilder()
    {
    }

    /**
     * Produces the default RequestContextFactory as identified by the
     * Web Framework configuration
     * 
     * @return the request context factory
     * 
     * @throws RequestContextException if factory cannot be instantiated
     */
    public static RequestContextFactory newFactory() throws RequestContextException
    {
        // the default class name that we will use
        String className = HTTP_REQUEST_CONTEXT_FACTORY;
        
        // default request context id
        String requestContextId = FrameworkHelper.getConfig().getDefaultRequestContextId();
        if (requestContextId != null)
        {
            // see if another class name was configured
            RequestContextDescriptor desc = FrameworkHelper.getConfig().getRequestContextDescriptor(requestContextId);
            if (desc != null)
            {
                String _className = desc.getImplementationClass();
                if (_className != null)
                {
                    className = _className;
                }
            }
        }
        
        // instantiate the object
        RequestContextFactory factory = (RequestContextFactory)ReflectionHelper.newObject(className);
        if (factory == null)
        {
            throw new RequestContextException("Unable to load RequestContextFactory: " + className);
        }
        
        if (FrameworkHelper.getLogger().isDebugEnabled())
            FrameworkHelper.getLogger().debug("New request context factory: " + className);
        
        return factory;
    }    
}
