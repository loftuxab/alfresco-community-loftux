/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

import org.springframework.extensions.webscripts.RemoteStore;

/**
 * <p>Tenant specific implementation of the {@link RemoteStore} that ensures that the tenant name
 * is included on remote requests.</p>
 * 
 * @author David Draper
 */
public class TenantRemoteStore extends RemoteStore
{
    /**
     * <p>Builds a URI that includes the tenant name.</p>
     * 
     * @param uri
     * @return
     */
    @Override
    protected String buildUri(String uri)
    {
        return "/" + TenantUtil.getTenantName() + uri;
    }
}
