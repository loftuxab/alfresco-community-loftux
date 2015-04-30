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
package org.alfresco.module.org_alfresco_module_cloud.accounts.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Test code for {@link AccountDAOImpl}.
 * 
 * @author Neil Mc Erlean
 * @since Thor
 */
public class AccountDAOImplTest
{
    //TODO Move this (and similar codes) to utility class.
    protected static final int FREE_PUBLIC_EMAIL_DOMAIN_ACCOUNT_ID = -1;
    protected static final int FREE_PRIVATE_EMAIL_DOMAIN_ACCOUNT_ID = 0;
    protected static final int PAID_BUSINESS_TEST_DOMAIN_ACCOUNT_ID = 100;
    
    protected static ApplicationContext TEST_CONTEXT;
    
    // Services
    protected static AccountDAO ACCOUNT_DAO;
    protected static RetryingTransactionHelper TRANSACTION_HELPER;
    
    private CloudTestContext cloudContext;
    private String T1;
    
    
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        TEST_CONTEXT          = ApplicationContextHelper.getApplicationContext();
        TRANSACTION_HELPER    = (RetryingTransactionHelper)TEST_CONTEXT.getBean("retryingTransactionHelper");
        ACCOUNT_DAO           = (AccountDAOImpl) TEST_CONTEXT.getBean("accountDAO");
    }
    
    @Before public void init() throws Exception
    {
        cloudContext = new CloudTestContext(TEST_CONTEXT);
        T1 = cloudContext.createTenantName("acme");
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
    }
    
    @After public void cleanup()
    {
        cloudContext.cleanup();
    }
    
    @Test public void checkUsingCorrectDAO() throws Exception
    {
        // This check will only work for un-intercepted beans.
        assertEquals("Loaded the wrong AccountDAO impl class", AccountDAOImpl.class, ACCOUNT_DAO.getClass());
    }
    
    @Test public void createAccount() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    AccountEntity account = createAccountEntity(T1, FREE_PRIVATE_EMAIL_DOMAIN_ACCOUNT_ID);
                    cloudContext.addAccountEntity(account);
                    return null;
                }
            });
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    @Test public void createPublicAccount() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    AccountEntity account = createAccountEntity(T1, FREE_PUBLIC_EMAIL_DOMAIN_ACCOUNT_ID);
                    cloudContext.addAccountEntity(account);
                    return null;
                }
            });
    }
    
    @Test public void createAccountAndUpdateType() throws Exception
    {
        final AccountEntity newAccount = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<AccountEntity>()
        {
            @Override
            public AccountEntity execute() throws Throwable
            {
                AccountEntity account = createAccountEntity(T1, FREE_PRIVATE_EMAIL_DOMAIN_ACCOUNT_ID);
                cloudContext.addAccountEntity(account);
                return account;
            }
        });
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                updateAccountType(newAccount, PAID_BUSINESS_TEST_DOMAIN_ACCOUNT_ID);
                return null;
            }
        });
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                AccountEntity updatedAccount = ACCOUNT_DAO.getAccount(newAccount.getId());
                assertNotNull(updatedAccount);
                assertEquals(PAID_BUSINESS_TEST_DOMAIN_ACCOUNT_ID, updatedAccount.getType());
                return null;
            }
        });
    }
    
    @Test public void createDuplicateAccounts() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    AccountEntity account = createAccountEntity(T1, FREE_PRIVATE_EMAIL_DOMAIN_ACCOUNT_ID);
                    cloudContext.addAccountEntity(account);
                    
                    AccountEntity account2 = createAccountEntity(T1, FREE_PRIVATE_EMAIL_DOMAIN_ACCOUNT_ID, account.getCreationDate());
                    assertEquals(account, account2);
                    
                    return null;
                }
            });
    }
    
    @Test public void getAccountById() throws Exception
    {
        final AccountEntity account = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<AccountEntity>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public AccountEntity execute() throws Throwable
                {
                    AccountEntity account = createAccountEntity(T1, FREE_PRIVATE_EMAIL_DOMAIN_ACCOUNT_ID);
                    cloudContext.addAccountEntity(account);
                    return account;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    AccountEntity existingAccount = ACCOUNT_DAO.getAccount(account.getId());
                    assertNotNull(existingAccount);
                    AccountEntity nonExistingAccount = ACCOUNT_DAO.getAccount(-1L);
                    assertNull(nonExistingAccount);
                    return null;
                }
            });
    }

    @Test public void getAccountByDomain() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<AccountEntity>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public AccountEntity execute() throws Throwable
                {
                    AccountEntity account = createAccountEntity(T1, FREE_PRIVATE_EMAIL_DOMAIN_ACCOUNT_ID);
                    cloudContext.addAccountEntity(account);
                    return account;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    AccountEntity existingAccount = ACCOUNT_DAO.getAccount(T1);
                    assertNotNull(existingAccount);
                    AccountEntity nonExistingAccount = ACCOUNT_DAO.getAccount("doesnotexist.com");
                    assertNull(nonExistingAccount);
                    return null;
                }
            });
    }

    @Test public void deleteAccount() throws Exception
    {
        final AccountEntity account = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<AccountEntity>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public AccountEntity execute() throws Throwable
                {
                    AccountEntity account = createAccountEntity(T1, FREE_PRIVATE_EMAIL_DOMAIN_ACCOUNT_ID);
                    cloudContext.addAccountEntity(account);
                    return account;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    AccountEntity existingAccount = ACCOUNT_DAO.getAccount(account.getId());
                    assertNotNull(existingAccount);
                    ACCOUNT_DAO.deleteAccount(account.getId());
                    cloudContext.removeAccountEntity(account);
                    return null;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    AccountEntity nonExistingAccount = ACCOUNT_DAO.getAccount(account.getId());
                    assertNull(nonExistingAccount);
                    nonExistingAccount = ACCOUNT_DAO.getAccount(account.getDomains().get(0));
                    assertNull(nonExistingAccount);
                    return null;
                }
            });
    }
    
    private AccountEntity createAccountEntity(String domain, int accountTypeId) throws Exception
    {
        return createAccountEntity(domain, accountTypeId, new Date());
    }
    
    /**
     * This method creates a new account with the specifies parameters.
     * There is no transaction handling in this method.
     * 
     * @param domain
     * @param accountName
     * @param accountTypeId
     * @return
     * @throws Exception
     */
    private AccountEntity createAccountEntity(String domain, int accountTypeId, Date creationDate) throws Exception
    {
        AccountEntity account = new AccountEntity(domain, accountTypeId);
        account.setName(domain);
        account.setCreationDate(creationDate);
        account = ACCOUNT_DAO.createAccount(account);
        
        assertNotNull("account id is not null", account.getId());
        assertNotNull("account domains not null", account.getDomains());
        assertNotNull("account domains is not empty", account.getDomains().size() == 1);
        assertEquals("account domain was wrong.", domain, account.getDomains().get(0));
        assertEquals("account type was wrong.", accountTypeId, account.getType());
        assertEquals("account name was wrong", domain, account.getName());
        assertEquals("account creation date was wrong", creationDate, account.getCreationDate());
        
        return account;
    }
    
    private void updateAccountType(AccountEntity existingAccountEntity, int newAccountTypeId)
    {
        ACCOUNT_DAO.updateAccountType(existingAccountEntity.getId(), newAccountTypeId);
    }
}
