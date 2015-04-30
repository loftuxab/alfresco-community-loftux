/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud_share;

import java.net.MalformedURLException;
import java.net.URL;

import org.alfresco.web.scripts.SlingshotRemoteClient;
import org.springframework.extensions.webscripts.connector.RemoteClient;

/**
 * <p>A tenant specific implementation of the {@link RemoteClient} that ensures that requests made to 
 * the remote endpoint include the tenant for the current request.</p> 
 * 
 * @author David Draper
 */
public class TenantRemoteClient extends SlingshotRemoteClient
{
    /**
     * <p>Overrides the default implementation to ensure that the current tenant name is included on 
     * remote requests.</p>
     */
    @Override
    protected URL buildURL(String uri) throws MalformedURLException
    {
        URL url;
        String resolvedUri = uri;
        String tenant = "/" + TenantUtil.getTenantName();
        if (uri.startsWith(endpoint))
        {
            // Remove the endpoint from the URI if included...
            resolvedUri = uri.substring(endpoint.length());
        }
        if (resolvedUri.startsWith(tenant))
        {
            // Remove the tenant from the URI if included...
            resolvedUri = resolvedUri.substring(tenant.length());
        }
        else if (resolvedUri.startsWith("/" + TenantUtil.DEFAULT_TENANT_NAME))
        {
            resolvedUri = resolvedUri.substring(TenantUtil.DEFAULT_TENANT_NAME.length() + 1);
            tenant = "/" + TenantUtil.DEFAULT_TENANT_NAME;
        }
        else if (resolvedUri.startsWith("/" + TenantUtil.SYSTEM_TENANT_NAME))
        {
            resolvedUri = resolvedUri.substring(TenantUtil.SYSTEM_TENANT_NAME.length() + 1);
            tenant = "/" + TenantUtil.SYSTEM_TENANT_NAME;
        }
        
        // Re-build the URI (to ensure that BOTH the endpoint and tenant are present)...
        resolvedUri = endpoint + tenant + resolvedUri;
        if (getTicket() == null)
        {
            url = new URL(resolvedUri);
        }
        else
        {
            url = new URL(resolvedUri +
                    (uri.lastIndexOf('?') == -1 ? ("?"+getTicketName()+"="+getTicket()) : ("&"+getTicketName()+"="+getTicket())));
        }
        return url;
    }
    
}
