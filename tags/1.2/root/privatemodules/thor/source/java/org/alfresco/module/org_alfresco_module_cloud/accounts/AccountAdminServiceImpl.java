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

import java.util.HashSet;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountDAO;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck.FailureReason;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.domain.InvalidDomainDAO;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.domain.InvalidDomainEntity;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService.TYPE;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.tenant.TenantAdminService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.web.util.paging.PagedResults;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Neil Mc Erlean
 * @since Thor Phase 2 Sprint 2
 */
public class AccountAdminServiceImpl implements AccountAdminService
{
    private static final Log log = LogFactory.getLog(AccountAdminServiceImpl.class);
    
    AccountDAO               accountDAO;
    AccountService           accountService;
    CloudPersonService       cloudPersonService;
    DirectoryService         directoryService;
    InvalidDomainDAO         invalidDomainDAO;
    NodeService              nodeService;
    RegistrationService      registrationService;
    TenantAdminService       tenantAdminService;
    
    public void setAccountDAO(AccountDAO dao)
    {
        this.accountDAO = dao;
    }
    
    public void setAccountService(AccountService service)
    {
        this.accountService = service;
    }
    
    public void setCloudPersonService(CloudPersonService service)
    {
        this.cloudPersonService = service;
    }
    
    public void setDirectoryService(DirectoryService service)
    {
        this.directoryService = service;
    }
    
    public void setInvalidDomainDAO(InvalidDomainDAO dao)
    {
        this.invalidDomainDAO = dao;
    }
    
    public void setNodeService(NodeService service)
    {
        this.nodeService = service;
    }
    
    public void setRegistrationService(RegistrationService service)
    {
        this.registrationService = service;
    }

    public void setTenantAdminService(TenantAdminService service)
    {
        this.tenantAdminService = service;
    }
    
    @Override
    public void changeAccountType(Account account, AccountType newAccountType)
    {
        this.changeAccountType(account, newAccountType.getId());
    }

    @Override
    public void changeAccountType(final Account account, final int newAccountTypeId)
    {
        //TODO If/when this gets any more complicated (in terms of to/from type combinations), we might
        //     do something a bit more clever. But for now, we'll keep it simple.
        
        
        // Get the latest persisted account data.
        Account existingAccount = accountService.getAccount(account.getId());
        
        final int existingAccountTypeId = existingAccount.getType().getId();
        
        if (log.isDebugEnabled())
        {
            log.debug("Attempting to change account type from " + existingAccountTypeId + " to " + newAccountTypeId + " for account " + account.getId());
        }
        
        if (existingAccountTypeId != newAccountTypeId)
        {
            // For now we only support changing an account type to public, but not back again.
            if (newAccountTypeId == AccountType.PUBLIC_DOMAIN_ACCOUNT_TYPE)
            {
                changeAccountToPublic(account);
            }
            else if (existingAccountTypeId == AccountType.PUBLIC_DOMAIN_ACCOUNT_TYPE &&
                        newAccountTypeId == AccountType.FREE_NETWORK_ACCOUNT_TYPE)
            {
                // This we explicitly disallow.
                throw new AccountAdminServiceException("Changing an account from public to private is not currently supported.");
            }
            else
            {
                // We'll let the rest through, but we'll not perform any business logic as part of the change.
                changeAccountTypeImpl(account, newAccountTypeId);
            }
            
            if (existingAccountTypeId >= AccountType.STANDARD_NETWORK_ACCOUNT_TYPE && 
                newAccountTypeId <= AccountType.FREE_NETWORK_ACCOUNT_TYPE) 
            {
                removeNetworkAdmins(existingAccount);
            }
        }
    }
    
    private void removeNetworkAdmins(final Account account)
    {
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                Set<NodeRef> networkAdmins = new HashSet<NodeRef>();
                
                final int pageSize = 128;
                int offset = 0;
                
                PagingResults<NodeRef> pageResults;
        
                do 
                {
                    pageResults = cloudPersonService.getPeople("*", null, offset, pageSize, TYPE.INTERNAL, true);
                    networkAdmins.addAll(pageResults.getPage());
                } 
                while (pageResults.hasMoreItems());
                
                for (NodeRef networkAdmin : networkAdmins) {
                    String email = (String) nodeService.getProperty(networkAdmin, ContentModel.PROP_USERNAME);
        
                    registrationService.demoteUserFromNetworkAdmin(account.getId(), email);
                }
                return null;
            }
        }, account.getTenantId());
    }

    /**
     * This method just dumbly sets the account type without performing any business side-effects that might be required.
     */
    private void changeAccountTypeImpl(final Account account, int newAccountTypeId)
    {
        accountDAO.updateAccountType(account.getId(), newAccountTypeId);
    }
    
    /**
     * This method changes the specified account to a PUBLIC account and does all the necessary
     * business logic associated with that.
     */
    private void changeAccountToPublic(final Account account)
    {
        // Remove all external users from network - this has to be run in the account tenant.
        final String tenantId = account.getTenantId();
        
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                // We have to retrieve all the external people in this tenant, which could be a long list.
                // Rather than ask for a very large page of data (Integer.MAX_VALUE would cause an OutOfMemoryError),
                // we'll get a series of pages of data and concatenate them.
                Set<NodeRef> allExternalPeople = new HashSet<NodeRef>();
                
                final int pageSize = 128;
                int currentSkipCount = 0;
                
                PagingResults<NodeRef> pageOfData;
                do
                {
                    // get next page of data
                    pageOfData = cloudPersonService.getPeople("*", null, currentSkipCount, pageSize, TYPE.EXTERNAL, false);
                    
                    currentSkipCount += pageSize;
                    
                    // Unfortunately we can't remove them as we go, as this leads to inconsistencies in the results from the
                    // getPeople call.
                    allExternalPeople.addAll(pageOfData.getPage());
                }
                while(pageOfData.hasMoreItems());
                
                // Now remove them from this account
                for (NodeRef externalPerson : allExternalPeople)
                {
                    String username = (String) nodeService.getProperty(externalPerson, ContentModel.PROP_USERNAME);
                    directoryService.removeSecondaryAccount(username, account.getId());
                }
                
                return null;
            }
        }, tenantId);
        
        
        // Disable the account
        tenantAdminService.disableTenant(account.getTenantId());
        
        // Update the account data.
        accountDAO.updateAccountType(account.getId(), AccountType.PUBLIC_DOMAIN_ACCOUNT_TYPE);
        
        // Mark the domain as PUBLIC in the blacklist so that we'll prevent further signups immediately, rather than at activation.
        InvalidDomainEntity entity = invalidDomainDAO.getInvalidDomain(account.getDomains().get(0));
        if (entity != null)
        {
            entity.setType(FailureReason.PUBLIC.name());
            invalidDomainDAO.updateInvalidDomain(entity);
        }
        else
        {
            entity = new InvalidDomainEntity();
            entity.setDomain(account.getDomains().get(0));
            entity.setType(FailureReason.PUBLIC.name());
            entity.setNote("Marked as PUBLIC as part of account type conversion.");
            
            invalidDomainDAO.createInvalidDomain(entity);
        }
    }
}
