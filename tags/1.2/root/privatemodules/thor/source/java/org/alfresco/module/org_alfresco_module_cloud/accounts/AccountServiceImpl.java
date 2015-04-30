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
package org.alfresco.module.org_alfresco_module_cloud.accounts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountDAO;
import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountEntity;
import org.alfresco.module.org_alfresco_module_cloud.tenant.CloudTenantAdminService;
import org.alfresco.module.org_alfresco_module_cloud.usage.TenantQuotaService;
import org.alfresco.query.CannedQuery;
import org.alfresco.query.CannedQueryResults;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.util.Pair;


/**
 * Account Service implementation to create and manage {@link Account accounts}.
 * 
 * @author David Gildeh
 * @author Neil Mc Erlean
 * @author janv
 * @since Cloud
 */
public class AccountServiceImpl implements AccountService
{
    private AccountDAO accountDAO;
    private AccountRegistry accountRegistry;
    private AccountsCannedQueryFactory accountsCannedQueryFactory;
    
    private CloudTenantAdminService tenantAdminService;
    
    
    public void setAccountDAO(AccountDAO accountDAO)
    {
        this.accountDAO = accountDAO;
    }
    
    public void setAccountRegistry(AccountRegistry accountRegistry)
    {
        this.accountRegistry = accountRegistry;
    }
    
    public void setTenantAdminService(CloudTenantAdminService service)
    {
        this.tenantAdminService = service;
    }

    public void setAccountsCannedQueryFactory(AccountsCannedQueryFactory service)
    {
        this.accountsCannedQueryFactory = service;
    }
    
    public Account createAccount(final String domain, int type, boolean enabled)
    {
        AccountType accountType = accountRegistry.getType(type);
        if (accountType == null)
        {
            throw new IllegalArgumentException("Illegal account typeID: " + type);
        }
        
        AccountEntity entity = accountDAO.getAccount(domain);
        if (entity == null)
        {
            // create account
            entity = new AccountEntity(domain, type);
            entity.setCreationDate(new Date());
            // TODO: allow setting of account name on create?
            entity.setName(domain);
            entity = accountDAO.createAccount(entity);
            
            // create tenant for account
            // TODO: remove hard-oded tenant admin password
            tenantAdminService.createTenant(domain, "admin".toCharArray());

            // kick-off usages
//            tenantAdminService.clearQuota(domain, TenantQuotaService.FILE_STORAGE);
            tenantAdminService.clearQuota(domain, TenantQuotaService.SITE_COUNT);
            tenantAdminService.clearQuota(domain, TenantQuotaService.PERSON_COUNT);
            tenantAdminService.clearQuota(domain, TenantQuotaService.INTERNAL_PERSON_COUNT);
            tenantAdminService.clearQuota(domain, TenantQuotaService.NETWORK_ADMIN_COUNT);
            
            if (!enabled)
            {
                tenantAdminService.disableTenant(domain);
            }
        }
        
        return getAccount(entity);
    }
    
    private Account getAccount(AccountEntity entity)
    {
        // note: currently 1st domain maps to tenant (see also Account.getTenantId())
        String tenant = entity.getDomains().get(0);
        
        boolean enabled = tenantAdminService.getTenant(tenant).isEnabled();
        
        AccountUsages usages = new AccountUsages();
        usages.setFileUploadQuota(tenantAdminService.getQuota(tenant, TenantQuotaService.FILE_UPLOAD_SIZE));
        usages.setFileQuota(tenantAdminService.getQuota(tenant, TenantQuotaService.FILE_STORAGE));
        usages.setFileUsage(tenantAdminService.getUsage(tenant, TenantQuotaService.FILE_STORAGE));
        usages.setSiteCountQuota(tenantAdminService.getQuota(tenant, TenantQuotaService.SITE_COUNT));
        usages.setSiteCountUsage(tenantAdminService.getUsage(tenant, TenantQuotaService.SITE_COUNT));
        usages.setPersonCountQuota(tenantAdminService.getQuota(tenant, TenantQuotaService.PERSON_COUNT));
        usages.setPersonCountUsage(tenantAdminService.getUsage(tenant, TenantQuotaService.PERSON_COUNT));
        usages.setPersonIntOnlyCountQuota(tenantAdminService.getQuota(tenant, TenantQuotaService.INTERNAL_PERSON_COUNT));
        usages.setPersonIntOnlyCountUsage(tenantAdminService.getUsage(tenant, TenantQuotaService.INTERNAL_PERSON_COUNT));
        usages.setPersonNetworkAdminCountQuota(tenantAdminService.getQuota(tenant, TenantQuotaService.NETWORK_ADMIN_COUNT));
        usages.setPersonNetworkAdminCountUsage(tenantAdminService.getUsage(tenant, TenantQuotaService.NETWORK_ADMIN_COUNT));
      
        return new AccountImpl(accountRegistry, entity, usages, enabled);
    }
    
    public Account getAccount(long id)
    {
        AccountEntity entity = accountDAO.getAccount(id);
        if (entity == null)
        {
            return null;
        }
        return getAccount(entity);
    }
    
    public String getAccountTenant(long id)
    {
        AccountEntity entity = accountDAO.getAccount(id);
        if (entity == null)
        {
            return null;
        }
        
        // note: currently 1st domain maps to tenant (see also Account.getTenantId())
        return entity.getDomains().get(0);
    }
    
    public Account getAccountByDomain(String domain)
    {
        // special case: THOR-1245
        if ((domain != null) && (domain.equals(TenantUtil.SYSTEM_TENANT)))
        {
            AccountEntity dummySystemAccount = new AccountEntity(TenantUtil.SYSTEM_TENANT, AccountType.FREE_NETWORK_ACCOUNT_TYPE);
            dummySystemAccount.setCreationDate(new Date());
            dummySystemAccount.setId(Long.valueOf(-1));
            dummySystemAccount.setName(TenantUtil.SYSTEM_TENANT);
           
            return new AccountImpl(accountRegistry, dummySystemAccount, new AccountUsages(), true);
        }
        
        AccountEntity entity = accountDAO.getAccount(domain);
        if (entity == null)
        {
            return null;
        }
        return getAccount(entity);
    }
    
    public PagingResults<Account> getAccounts(Integer filterByAccountTypeId, PagingRequest pagingReq)
    {
        return getAccounts(filterByAccountTypeId, null, pagingReq);
    }
    
    public PagingResults<Account> getAccounts(Integer filterByAccountTypeId, Pair<String, Boolean> sortByPair, PagingRequest pagingReq)
    {
        CannedQuery<AccountEntity> query = accountsCannedQueryFactory.getCannedQuery(pagingReq, filterByAccountTypeId, sortByPair);
        CannedQueryResults<AccountEntity> results = query.execute();
        
        // convert entities to service level accounts
        // note: gets usage/quotas for each tenant in this (final) page of results
        return getPagingResults(pagingReq, results);
    }
    
    private PagingResults<Account> getPagingResults(PagingRequest pagingRequest, final CannedQueryResults<AccountEntity> results)
    {
        List<AccountEntity> entities = null;
        if (results.getPageCount() > 0)
        {
            entities = results.getPages().get(0);
        }
        else
        {
            entities = Collections.emptyList();
        }
        
        // set total count
        final Pair<Integer, Integer> totalCount;
        if (pagingRequest.getRequestTotalCountMax() > 0)
        {
            totalCount = results.getTotalResultCount();
        }
        else
        {
            totalCount = null;
        }
        
        final List<Account> accounts = new ArrayList<Account>(entities.size());
        for (AccountEntity entity : entities)
        {
            accounts.add(getAccount(entity));
        }
        
        return new PagingResults<Account>()
        {
            @Override
            public String getQueryExecutionId()
            {
                return results.getQueryExecutionId();
            }
            @Override
            public List<Account> getPage()
            {
                return accounts;
            }
            @Override
            public boolean hasMoreItems()
            {
                return results.hasMoreItems();
            }
            @Override
            public Pair<Integer, Integer> getTotalResultCount()
            {
                return totalCount;
            }
        };
    }
    
    // TODO - also update other account details, eg. name, enabled
    public Account updateAccount(Account account)
    {
        String tenantDomain = account.getTenantId();
        if (tenantDomain == null)
        {
            throw new IllegalArgumentException("Illegal - Account Domain is required");
        }
        
        long fileUploadQuota = account.getUsageQuota().getFileUploadQuota();
        if (fileUploadQuota >= -1)
        {
            tenantAdminService.setQuota(tenantDomain, TenantQuotaService.FILE_UPLOAD_SIZE, fileUploadQuota);
        }

        long fileQuota = account.getUsageQuota().getFileQuota();
        if (fileQuota >= -1)
        {
            tenantAdminService.setQuota(tenantDomain, TenantQuotaService.FILE_STORAGE, fileQuota);
        }
        
        long siteCountQuota = account.getUsageQuota().getSiteCountQuota();
        if (siteCountQuota >= -1)
        {
            tenantAdminService.setQuota(tenantDomain, TenantQuotaService.SITE_COUNT, siteCountQuota);
        }
        
        long personCountQuota = account.getUsageQuota().getPersonCountQuota();
        if (personCountQuota >= -1)
        {
            tenantAdminService.setQuota(tenantDomain, TenantQuotaService.PERSON_COUNT, personCountQuota);
        }
        
        long personIntOnlyCountQuota = account.getUsageQuota().getPersonIntOnlyCountQuota();
        if (personIntOnlyCountQuota >= -1)
        {
            tenantAdminService.setQuota(tenantDomain, TenantQuotaService.INTERNAL_PERSON_COUNT, personIntOnlyCountQuota);
        }
        
        long personNetworkAdminCountQuota = account.getUsageQuota().getPersonNetworkAdminCountQuota();
        if (personNetworkAdminCountQuota >= -1)
        {
            tenantAdminService.setQuota(tenantDomain, TenantQuotaService.NETWORK_ADMIN_COUNT, personNetworkAdminCountQuota);
        }
        
        // TODO only update if *input* has changed (rather than rely on isEnabledTenant)
        boolean enabled = tenantAdminService.isEnabledTenant(tenantDomain);
        if ((!enabled) && account.isEnabled())
        {
            tenantAdminService.enableTenant(tenantDomain);
        }
        else if (enabled && (!account.isEnabled()))
        {
            tenantAdminService.disableTenant(tenantDomain);
        }
        
        // This Service is no longer intended to be used to update an Account's Type.
        int existingAccountType = this.getAccount(account.getId()).getType().getId();
        if (account.getType().getId() != existingAccountType)
        {
            throw new AlfrescoRuntimeException("Incorrect usage of AccountService. Cannot change Account Type.");
        }
        
        return getAccountByDomain(tenantDomain);
    }
    
    public void removeAccount(long id)
    {
        Account account = getAccount(id);
        if (account != null)
        {
            String tenantId = account.getTenantId();
            if (tenantId != null)
            {
                tenantAdminService.deleteTenant(tenantId);
            }
            accountDAO.deleteAccount(id);
        }
    }
    
    public long getNumberOfAccounts()
    {
    	return accountDAO.getNumberOfAccounts();
    }
}
