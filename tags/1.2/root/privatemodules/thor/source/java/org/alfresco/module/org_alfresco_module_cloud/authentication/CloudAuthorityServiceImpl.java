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

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authority.AuthorityServiceImpl;
import org.alfresco.repo.tenant.TenantService;

/**
 * THOR-927: perf tweak - treat "admin" (including re-map of "admin@tenant") as an implicit admin authority
 */
public class CloudAuthorityServiceImpl extends AuthorityServiceImpl
{
    private TenantService mtTenantService;
    
    public void setMTTenantService(TenantService service)
    {
        mtTenantService = service;
    }
    
    @Override
    public boolean hasAdminAuthority()
    {
        return isAdminAuthority(AuthenticationUtil.getRunAsUser());
    }
    
    @Override
    public boolean isAdminAuthority(String authorityName)
    {
        if (authorityName == null)
        {
            return false;
        }
        // remap tenant admin to system admin
        String admin = mtTenantService.getBaseNameUser(AuthenticationUtil.getAdminUserName());
        String user = mtTenantService.getBaseNameUser(authorityName);
        return (user.equalsIgnoreCase(admin));
    }
}
