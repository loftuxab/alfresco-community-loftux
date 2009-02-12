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
package org.alfresco.web.framework.resource;

import org.alfresco.web.framework.exception.ResourceLoaderException;
import org.alfresco.web.site.RequestContext;

/**
 * Resource Loader implementation which knows how to load resources from the web
 * application
 * 
 * This can be either a live WCM application or a runtime local-disk web
 * application
 * 
 * The object id's can be of the following format:
 *  - avm://storeId/<version>;<comma-delimited-path>
 *    avm://storeId/-1;www;avm_webapps;ROOT;products;product.xml
 *    
 *  - /www/avm_webapps/ROOT/<relativePath>
 *    /www/avm_webapps/ROOT/products/product.xml
 * 
 * @author muzquiano
 */
public class AlfrescoWebProjectResourceLoader extends AbstractResourceLoader
{
    public AlfrescoWebProjectResourceLoader(String endpointId)
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
            if (objectId.startsWith("avm://"))
            {
                canHandle = true;
            }
            if (objectId.startsWith("/"))
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
        // construct a temporary, transient resource
        TransientResourceImpl resource = new TransientResourceImpl(objectId,
                TransientResourceImpl.TYPE_WEBAPP);

        // set the endpoint
        resource.setEndpoint(endpointId);

        // set the value (convert to path)
        resource.setValue(objectId);

        // set the resource onto the context
        return resource;
    }
}
