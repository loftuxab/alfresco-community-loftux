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
 * Resource Loader implementation which knows how to load resources
 * from the Alfresco Spaces repository.
 * 
 * These resources represent nodeRefs in the SpacesStore.
 * 
 * The object id's can be of the following format:
 * 
 * workspace://SpacesStore/<nodeId> workspace/SpacesStore/<nodeId>
 * 
 * @author muzquiano
 */
public class AlfrescoSpaceResourceLoader extends AbstractResourceLoader
{
    public AlfrescoSpaceResourceLoader(String endpointId)
    {
        super(endpointId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceLoader#canHandle(java.lang.String)
     */
    public boolean canHandle(String objectId)
    {
        boolean canHandle = false;

        if (objectId != null)
        {
            if (objectId.startsWith("workspace://"))
            {
                canHandle = true;
            }
            if (objectId.startsWith("workspace/"))
            {
                canHandle = true;
            }
        }

        return canHandle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceLoader#load(org.alfresco.web.site.RequestContext,
     *      java.lang.String)
     */
    public Resource load(RequestContext context, String objectId)
            throws ResourceLoaderException
    {
        // construct a temporary, transient resource to an Alfresco
        // Space
        TransientResourceImpl resource = new TransientResourceImpl(objectId,
                TransientResourceImpl.TYPE_SPACE);

        // set the endpoint
        resource.setEndpoint(endpointId);

        // set the value
        resource.setValue(objectId);

        // set the resource onto the context
        return resource;
    }
}
