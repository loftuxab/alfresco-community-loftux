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
package org.alfresco.module.org_alfresco_module_cloud.usage;

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountRegistry;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountDAO;
import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountEntity;
import org.alfresco.repo.tenant.TenantUtil;


/**
 * Tenant Service implementation which defaults to Account Type settings.
 * 
 * @author dcaruana
 * @author janv
 * 
 * @since Thor
 */
public class CloudTenantQuotaServiceImpl extends TenantQuotaServiceImpl
{
    private AccountRegistry accountRegistry;
    private AccountDAO accountDAO;

    public void setAccountRegistry(AccountRegistry service)
    {
        this.accountRegistry = service;
    }
    
    public void setAccountDAO(AccountDAO service)
    {
        this.accountDAO = service;
    }
    
    @Override
    public long getQuota(String quotaName)
    {
        Long quota = super.getQuota(quotaName);
        if ((quota == null || quota.equals(UNKNOWN)))
        {
            quota = getDefaultQuota(quotaName);
        }
        return (quota == null ? UNKNOWN : quota);
    }
    
    private long getDefaultQuota(String quotaName)
    {
        AccountType accountType = getAccountTypeByDomain();
        if (accountType == null)
        {
            return UNKNOWN;
        }
        if (quotaName.equals(FILE_UPLOAD_SIZE))
        {
            return accountType.getFileUploadQuota();
        }
        else if (quotaName.equals(FILE_STORAGE))
        {
            return accountType.getFileQuota();
        }
        else if (quotaName.equals(SITE_COUNT))
        {
            return accountType.getSiteCountQuota();
        }
        else if (quotaName.equals(INTERNAL_PERSON_COUNT))
        {
            return accountType.getPersonIntOnlyCountQuota();
        }
        else if (quotaName.equals(NETWORK_ADMIN_COUNT))
        {
            return accountType.getPersonNetworkAdminCountQuota();
        }
        else if (quotaName.equals(PERSON_COUNT))
        {
            return accountType.getPersonCountQuota();
        }
        return UNKNOWN;
    }

    private AccountType getAccountTypeByDomain()
    {
        AccountEntity account = accountDAO.getAccount(TenantUtil.getCurrentDomain());
        return account == null ? null : accountRegistry.getType(account.getType()); 
    }
}
