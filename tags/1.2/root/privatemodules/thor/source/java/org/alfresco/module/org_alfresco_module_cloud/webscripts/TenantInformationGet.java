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
package org.alfresco.module.org_alfresco_module_cloud.webscripts;

import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.tenant.jscript.UserTenant;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Cloud Specific override of the Tenancy Information Get WebScript,
 *  which also reports on Secondary Domains 
 *  
 * @author Nick Burch
 * @since Thor.
 */
public class TenantInformationGet extends org.alfresco.enterprise.repo.web.scripts.tenant.TenantInformationGet
{
    private SyncAdminService syncAdminService;
    private DirectoryService directoryService;
    private AccountService accountService;

    public void setSyncAdminService(SyncAdminService syncAdminService)
    {
        this.syncAdminService = syncAdminService;
    }
    public void setDirectoryService(DirectoryService directoryService)
    {
        this.directoryService = directoryService;
    }
    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void buildAdditionalDetails(JSONObject json)
    {
        // Use the JScript helper to get our details
        UserTenant userTenant = new UserTenant();
        userTenant.setAccountService(accountService);
        userTenant.setDirectoryService(directoryService);
        
        // Fetch and store the details on their home and default tenants
        String email = AuthenticationUtil.getFullyAuthenticatedUser();
        json.put("email", email);
        json.put("homeTenant",    buildTenantDetails(userTenant.getHomeTenant(email)));
        json.put("defaultTenant", buildTenantDetails(userTenant.getDefaultTenant(email)));
        
        // Get the list of secondary tenants, and if they're sync enabled or not
        JSONArray secondaryTenants = new JSONArray();
        for (String tenant : userTenant.getSecondaryTenantsAsList(email))
        {
            secondaryTenants.add(buildTenantDetails(tenant));
        }
        json.put("secondaryTenants", secondaryTenants);
        
        // All done
    }
    
    @SuppressWarnings("unchecked")
    protected Object buildTenantDetails(String tenant)
    {
        if (tenant == null) return null;
        
        // Grab the details of the tenant
        JSONObject t = new JSONObject();
        t.put("name", tenant);
        t.put("isSyncEnabled", syncAdminService.isTenantEnabledForSync(tenant));
        return t;
    }
}