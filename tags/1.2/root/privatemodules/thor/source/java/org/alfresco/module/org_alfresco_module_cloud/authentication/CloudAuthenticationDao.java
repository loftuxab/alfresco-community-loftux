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

import net.sf.acegisecurity.UserDetails;
import net.sf.acegisecurity.providers.dao.UsernameNotFoundException;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.RepositoryAuthenticationDao;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.dao.DataAccessException;

/**
 * Component to provide authentication using native Alfresco authentication
 */
public class CloudAuthenticationDao extends RepositoryAuthenticationDao
{
    private TenantService mtTenantService;
    
    public CloudAuthenticationDao()
    {
        super();
    }
    
    public void setMTTenantService(TenantService service)
    {
        mtTenantService = service;
    }
    
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException
    {
        return super.loadUserByUsername(remapUserName(userName));
    }
    
    @Override
    public NodeRef getUserOrNull(String userName)
    {
        return super.getUserOrNull(remapUserName(userName));
    }
    
    private String remapUserName(String userName)
    {
        // remap tenant admin to system admin
        String admin = mtTenantService.getBaseNameUser(AuthenticationUtil.getAdminUserName());
        String user = mtTenantService.getBaseNameUser(userName);
        if (user.equalsIgnoreCase(admin))
        {
            userName = user;
        }
        return userName;
    }
}
