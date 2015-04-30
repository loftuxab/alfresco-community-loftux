/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.webscripts;

import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.tenant.TenantAdminService;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.repo.web.auth.TenantAuthentication;

/**
 * Authenticate current user against specified tenant
 */
public class CloudTenantAuthentication implements TenantAuthentication
{
    private DirectoryService directoryService;
    private AccountService accountService;
    private TenantAdminService tenantAdminService;
    private AuthorityService authorityService;
    private EmailAddressService emailAddressService;
    
    public void setDirectoryService(DirectoryService service)
    {
        this.directoryService = service;
    }
    
    public void setAccountService(AccountService service)
    {
        this.accountService = service;
    }
    
    public void setTenantAdminService(TenantAdminService service)
    {
        this.tenantAdminService = service;
    }
    
    public void setAuthorityService(AuthorityService service)
    {
        this.authorityService = service;
    }

    public void setEmailAddressService(EmailAddressService service)
    {
        this.emailAddressService = service;
    }

    /**
     * Determine whether tenant for email address exists and enabled
     * 
     * @param email
     * @return  true => email tenant exists
     */
    public boolean emailTenantExists(String email)
    {
        if (email == null)
        {
            return false;
        }
        
        final String tenant = emailAddressService.getDomain(email);
        return tenantExists(tenant);
    }
    
    /**
     * Determine whether tenant exists and enabled
     * 
     * @param tenant
     * @return  true => it exists, no it doesn't
     */
    public boolean tenantExists(final String tenant)
    {
        if (tenant == null || TenantService.DEFAULT_DOMAIN.equals(tenant))
        {
            return true;
        }
        
        return AuthenticationUtil.runAsSystem(new RunAsWork<Boolean>()
        {
            public Boolean doWork() throws Exception
            {
                return tenantAdminService.existsTenant(tenant) && tenantAdminService.isEnabled();
            }
        });
    }
    
    /**
     * Authenticate user against tenant
     * 
     * @param email
     * @param tenant
     * @return  true => authenticated, false => not authenticated
     */
    public boolean authenticateTenant(String email, String tenant)
    {
        // test super tenant case
        if (tenant.equalsIgnoreCase(TenantUtil.SYSTEM_TENANT))
        {
            List<Long> accounts = directoryService.getAllAccounts(email);
            if (accounts.size() == 0)
            {
                return true;
            }
        }
        
        // test user's default tenant case
        else if (tenant.equalsIgnoreCase(TenantUtil.DEFAULT_TENANT))
        {
            // test for tenant applicability
            if (authorityService.isAdminAuthority(email))
            {
                // admins allowed into any tenant ignoring whether tenant is disabled
                return true;
            }
            else
            {
                Long defaultAccount = directoryService.getDefaultAccount(email);
                if (defaultAccount != null)
                {
                    if (tenantAdminService.isEnabledTenant(accountService.getAccountTenant(defaultAccount)))
                    {
                        return true;
                    }
                }
            }
        }
        
        else
        {
            
            // test for tenant applicability
            if (authorityService.isAdminAuthority(email))
            {
                // admins allowed into any tenant ignoring whether tenant is disabled
                return true;
            }
            else
            {
                if (tenantAdminService.isEnabledTenant(tenant))
                {
                    // other users only allowed into tenants they belong to
                    List<Long> accounts = directoryService.getAllAccounts(email);
                    for (Long accountId : accounts)
                    {
                        if (tenant.equalsIgnoreCase(accountService.getAccountTenant(accountId)))
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false; 
    }
    
}
