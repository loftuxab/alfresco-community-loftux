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

import java.io.Serializable;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.cache.lookup.EntityLookupCache;
import org.alfresco.repo.cache.lookup.EntityLookupCache.EntityLookupCallbackDAOAdaptor;
import org.alfresco.util.Pair;
import org.springframework.dao.ConcurrencyFailureException;


/**
 * Abstract implementation for Account DAO.
 * <p>
 * This provides basic services such as caching, but defers to the underlying implementation
 * for CRUD operations.
 * 
 * @author janv
 * @since Thor
 */
public abstract class AbstractAccountDAOImpl implements AccountDAO
{
    private static final String CACHE_REGION_LOCALE = "Account";
    
    /*
     * Cache for the Account values:<br/>
     * KEY: ID<br/>
     * VALUE: AccountEntity<br/>
     * VALUE KEY: Domain (String)<br/>
     */
    private EntityLookupCache<Long, AccountEntity, String> accountEntityCache;
    
    /**
     * Set the cache that maintains the ID-Account mappings and vice-versa (bi-directional)
     * 
     * @param accountEntityCache        the cache
     */
    public void setAccountEntityCache(SimpleCache<Serializable, AccountEntity> accountEntityCache)
    {
        this.accountEntityCache = new EntityLookupCache<Long, AccountEntity, String>(
                accountEntityCache,
                CACHE_REGION_LOCALE,
                new AccountEntityCallbackDAO());
    }
    
    /**
     * Default constructor.
     * <p>
     * This sets up the DAO accessors to bypass any caching to handle the case where the caches are not
     * supplied in the setters.
     */
    protected AbstractAccountDAOImpl()
    {
        this.accountEntityCache = new EntityLookupCache<Long, AccountEntity, String>(new AccountEntityCallbackDAO());
    }
    
    /**
     * {@inheritDoc}
     */
    public AccountEntity createAccount(AccountEntity account)
    {
        if (account == null)
        {
            throw new IllegalArgumentException("Account value cannot be null");
        }
        
        if ((account.getDomains() == null) || (account.getDomains().size() != 1))
        {
            throw new IllegalArgumentException("Account must have one domain");
        }
        
        Pair<Long, AccountEntity> entityPair = accountEntityCache.getOrCreateByValue(account);
        
        AccountEntity accountOut = entityPair.getSecond();
        if (accountOut.getId() == null)
        {
            // account already exists - force retrieval of existing
            accountOut = accountEntityCache.getByKey(entityPair.getFirst()).getSecond();
        }
        
        return accountOut;
    }
    
    /**
     * {@inheritDoc}
     */
    public AccountEntity getAccount(Long id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("Cannot look up entity by null ID.");
        }
        
        Pair<Long, AccountEntity> entityPair = accountEntityCache.getByKey(id);
        return (entityPair == null ? null : entityPair.getSecond());
    }
    
    /**
     * {@inheritDoc}
     */
    public AccountEntity getAccount(String domain)
    {
        AccountEntity value = new AccountEntity(domain, -1);
        Pair<Long, AccountEntity> entityPair = accountEntityCache.getByValue(value);
        return (entityPair == null ? null : entityPair.getSecond());
    }
    
    /**
     * {@inheritDoc}
     */
    public void deleteAccount(Long id)
    {
        Pair<Long, AccountEntity> entityPair = accountEntityCache.getByKey(id);
        if (entityPair == null)
        {
            return;
        }
        
        int deleted = accountEntityCache.deleteByKey(id);
        if (deleted < 1)
        {
            throw new ConcurrencyFailureException("Account with ID " + id + " no longer exists");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void updateAccountType(Long accountId, Integer newAccountTypeId)
    {
        if (accountId == null || newAccountTypeId == null)
        {
            throw new IllegalArgumentException("Cannot look up entity by null ID.");
        }
        
        Pair<Long, AccountEntity> entityPair = accountEntityCache.getByKey(accountId);
        if (entityPair == null)
        {
            throw new IllegalArgumentException("Account entity not found.");
        }
        
        AccountEntity updatedAccount = new AccountEntity(entityPair.getSecond());
        updatedAccount.setType(newAccountTypeId);
        
        accountEntityCache.updateValue(accountId, updatedAccount);
    }
    
    /**
     * Callback for <b>alf_account</b> DAO
     */
    private class AccountEntityCallbackDAO extends EntityLookupCallbackDAOAdaptor<Long, AccountEntity, String>
    {
        private final Pair<Long, AccountEntity> convertEntityToPair(AccountEntity entity)
        {
            if (entity == null)
            {
                return null;
            }
            else
            {
                return new Pair<Long, AccountEntity>(entity.getId(), entity);
            }
        }
        
        public String getValueKey(AccountEntity value)
        {
            return value.getDomains().get(0);
        }
        
        public Pair<Long, AccountEntity> createValue(AccountEntity account)
        {
            AccountEntity entity = createAccountEntity(account);
            return convertEntityToPair(entity);
        }
        
        public Pair<Long, AccountEntity> findByKey(Long id)
        {
            AccountEntity entity = getAccountEntity(id);
            return convertEntityToPair(entity);
        }
        
        @Override
        public Pair<Long, AccountEntity> findByValue(AccountEntity value)
        {
            if ((value == null) || (value.getDomains() == null) || (value.getDomains().size() != 1))
            {
                throw new AlfrescoRuntimeException("Unexpected: Account must have one domain");
            }
            AccountEntity entity = getAccountEntity(value.getDomains().get(0));
            return convertEntityToPair(entity);
        }
        
        @Override
        public int updateValue(Long accountId, AccountEntity value)
        {
            return updateAccountEntity(value);
        }
        
        @Override
        public int deleteByKey(Long id)
        {
            return deleteAccountEntity(id);
        }
    }
    
    protected abstract AccountEntity createAccountEntity(AccountEntity account);
    protected abstract AccountEntity getAccountEntity(Long id);
    protected abstract AccountEntity getAccountEntity(String domain);
    protected abstract int updateAccountEntity(AccountEntity account);
    protected abstract int deleteAccountEntity(Long id);
}
