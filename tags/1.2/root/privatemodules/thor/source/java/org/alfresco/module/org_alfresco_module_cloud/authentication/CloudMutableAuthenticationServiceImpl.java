/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.authentication;

import org.alfresco.module.org_alfresco_module_cloud.webscripts.TenantSwitchingRepositoryContainer;
import org.alfresco.repo.security.authentication.MutableAuthenticationServiceImpl;
import org.alfresco.repo.tenant.TenantContextHolder;

public class CloudMutableAuthenticationServiceImpl extends MutableAuthenticationServiceImpl
{
    /**
     * In the cloud deployment, an outer wrapper sets the tenant that should be used before the authentication service
     * {@link #validate(String)} operation is invoked. This method normally always returns null in the base implementation,
     * but is overridden in the cloud to pick up the one that was set by the outer wrapper.
     * In the case of webscripts, for example, the outer wrapper is {@link TenantSwitchingRepositoryContainer}, and the tenant
     * has already been chosen by the time this method is invoked. Therefore, we simply reflect back that choice here.   
     */
    @Override
    protected String getPrevalidationTenantDomain()
    {
        return TenantContextHolder.getTenantDomain();
    }
}
