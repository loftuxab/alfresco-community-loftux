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

import org.alfresco.web.site.ClusterAwarePathStoreObjectPersister;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelPersistenceContext;
import org.springframework.extensions.surf.cache.ContentCache;

/**
 * Tenant specific version of the ClusterAwarePathStoreObjectPersister
 * 
 * @author Kevin Roast
 */
public class TenantClusterAwarePathStoreObjectPersister extends ClusterAwarePathStoreObjectPersister
{
    @Override
    protected ContentCache<ModelObject> getCache(ModelPersistenceContext context, String bucket)
    {
        // extract the current tenant name from the request context
        String tenant = TenantUtil.getTenantName();
        context.setStoreId(tenant);
        // delegate to super class impl to provide the actual cache
        return super.getCache(context, bucket);
    }
}