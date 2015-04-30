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
package org.alfresco.module.org_alfresco_module_cloud.accounts.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * In memory implementation of {@link AccountDAO} - for test only.
 * 
 * @author Neil Mc Erlean
 * @since Thor
 */
public class AccountDAOMemoryImpl implements AccountDAO
{
    /** A counter to give unique IDs to each account */
    private static long nextFreeAccountId = 1L;
    
    /** The in-memory 'persistence' of accounts */
    Map<Long, AccountEntity> accountsById = new HashMap<Long, AccountEntity>();
    Map<String, AccountEntity> accountsByDomain = new HashMap<String, AccountEntity>();
    
    @Override
    public void deleteAccount(Long id)
    {
        AccountEntity account = getAccount(id);
        if (account != null)
        {
            this.accountsByDomain.remove(account.getDomains().get(0));
            this.accountsById.remove(id);
        }
    }

    @Override
    public AccountEntity getAccount(Long id)
    {
        return accountsById.get(id);
    }

    @Override
    public AccountEntity getAccount(String domain)
    {
        return accountsByDomain.get(domain);
    }
    
    @Override
    public AccountEntity createAccount(AccountEntity account)
    {
        AccountEntity exists = getAccount(account.getDomains().get(0));
        if (exists != null)
        {
            return exists;
        }
        
        account.setId(nextFreeAccountId++);
        accountsById.put(account.getId(), account);
        accountsByDomain.put(account.getDomains().get(0), account);
        return account;
    }
    
    @Override
    public void updateAccountType(Long accountId, Integer newAccountTypeId)
    {
        AccountEntity existingEntity = getAccount(accountId);
        existingEntity.setType(newAccountTypeId);
    }
    
    @Override
    public long getNumberOfAccounts()
    {
    	return accountsById.size();
    }
}
