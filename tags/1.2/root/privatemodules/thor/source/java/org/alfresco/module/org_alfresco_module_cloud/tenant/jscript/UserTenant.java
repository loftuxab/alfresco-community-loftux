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
package org.alfresco.module.org_alfresco_module_cloud.tenant.jscript;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountClass;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;


/**
 * Script to get user's tenant domain(s) 
 * 
 * @author janv
 * @since Thor
 */
public class UserTenant extends BaseScopableProcessorExtension
{
    private AccountService      accountService;
    private DirectoryService    directoryService;
    
    public void setDirectoryService(DirectoryService service)
    {
        this.directoryService = service;
    }
    
    public void setAccountService(AccountService service)
    {
        this.accountService = service;
    }
    
    public String getHomeTenant(String email)
    {
        return getHomeTenant(directoryService, accountService, email);
    }
    
    public static String getHomeTenant(DirectoryService directoryService, AccountService accountService, String email)
    {
        String tenantDomain = null;
        
        Long accountId = directoryService.getHomeAccount(email);
        if (accountId != null)
        {
            Account account = accountService.getAccount(accountId);
            AccountType accountType = account.getType();
            
            // note: by definition public users have no home tenant from remote API perspective
            //       check that email does not belong to a network/account that is typed as a public email domain
            if (! ((accountType != null) && (AccountClass.Name.PUBLIC_EMAIL_DOMAIN.equals(accountType.getAccountClass().getName()))))
            {
                tenantDomain = account.getTenantId();
            }
        }
        
        return tenantDomain;
    }
    
    public Scriptable getSecondaryTenants(String email)
    {
        List<String> tenantDomains = getSecondaryTenantsAsList(email);
        return Context.getCurrentContext().newArray(getScope(), tenantDomains.toArray());
    }
    public List<String> getSecondaryTenantsAsList(String email)
    {
        List<Long> accountIds = directoryService.getSecondaryAccounts(email);
        List<String> tenantDomains = new ArrayList<String>(accountIds.size());
        for (Long accountId : accountIds)
        {
            tenantDomains.add(accountService.getAccountTenant(accountId));
        }
        return tenantDomains;
    }
    
    public String getDefaultTenant(String email)
    {
        Long accountId = directoryService.getDefaultAccount(email);
        if (accountId != null)
        {
            return accountService.getAccountTenant(accountId);
        }
        return getHomeTenant(email);
    }
    
    /**
     * This method returns the {@link AccountType} for the user's account.
     * @return the account type
     */
    public AccountType getAccountType(String email)
    {
        AccountType result = null;
        Long homeAccountId = directoryService.getHomeAccount(email);
        if (homeAccountId != null)
        {
            Account homeAccount = accountService.getAccount(homeAccountId);
            result = homeAccount.getType();
        }
        
        return result;
    }
}
