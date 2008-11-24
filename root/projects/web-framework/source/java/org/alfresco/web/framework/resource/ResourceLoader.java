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

import org.alfresco.web.framework.exception.ResourceLoaderException;
import org.alfresco.web.site.RequestContext;

/**
 * Loads a resource with a given object and endpoint id
 * 
 * @author muzquiano
 */
public interface ResourceLoader
{
    /**
     * Determines whether the loader can handle a resource load for
     * the given object from the given endpoint
     * 
     * @param objectId
     * @param endpointId
     * @return
     */
    public boolean canHandle(String objectId);

    /**
     * Returns the endpoint id for this resource loader
     * 
     * @return
     */
    public String getEndpointId();

    /**
     * Loads the resource with the given object id
     * 
     * @param context
     * @param objectId
     * @return
     * @throws ResourceLoaderException
     */
    public Resource load(RequestContext context, String objectId)
            throws ResourceLoaderException;
}
