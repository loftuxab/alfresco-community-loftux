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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountDAOImpl;
import org.alfresco.query.CannedQueryPageDetails;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Test code for {@link AccountDAOImpl}.
 * 
 * @author Neil Mc Erlean
 * @since Cloud
 */
public class AccountServiceImplTest
{
    // Services
    protected static ApplicationContext TEST_CONTEXT;
    protected static AccountService ACCOUNT_SERVICE;
    protected static RetryingTransactionHelper TRANSACTION_HELPER;
    
    protected static AccountRegistry accountRegistry;
    
    private CloudTestContext cloudContext;
    private String T1;
    
    
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        TEST_CONTEXT = ApplicationContextHelper.getApplicationContext();
        TRANSACTION_HELPER = (RetryingTransactionHelper)TEST_CONTEXT.getBean("retryingTransactionHelper");
        ACCOUNT_SERVICE = (AccountService) TEST_CONTEXT.getBean("accountService");
        accountRegistry = (AccountRegistry) TEST_CONTEXT.getBean("accountRegistry");
    }
    
    @Before public void init() throws Exception
    {
        cloudContext = new CloudTestContext(TEST_CONTEXT);
        T1 = cloudContext.createTenantName("acme");
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
    }
    
    /** Delete any temporary accounts created during the previous test */
    @After public void cleanup()
    {
        cloudContext.cleanup();
    }
    
    @Test public void createAccountEnabled() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    Account account = createAccount(T1, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                    cloudContext.addAccount(account);
                    return null;
                }
            });
    }

    @Test public void createAccountDisabled() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    Account account = createAccount(T1, AccountType.FREE_NETWORK_ACCOUNT_TYPE, false);
                    cloudContext.addAccount(account);
                    return null;
                }
            });
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
    private Account createAccount(String domain, int type, boolean enabled) throws Exception
    {
        // This will create an account with the metadata values defined for the specified accountId.
        // See account-service-context.xml
        Account account = ACCOUNT_SERVICE.createAccount(domain, type, enabled);
        assertNotNull("Account was null", account);
        
        // Check some metadata values to make sure they're set.
        assertEquals("account name was wrong", domain, account.getName());
        assertEquals("account type was wrong", type, account.getType().getId());
        assertEquals("account domains were wrong", Arrays.asList(new String[]{domain}), account.getDomains());
        assertEquals("account enabled was wrong", enabled, account.isEnabled());
        assertNotNull("account creation date is not null", account.getCreationDate());
        
        assertEquals("unexpected account quota", accountRegistry.getType(type).getFileQuota(), account.getUsageQuota().getFileQuota());
        
        return account;
    }

    @Test public void createDuplicateAccounts() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    Account account = createAccount(T1, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                    cloudContext.addAccount(account);
                    
                    Account account2 = ACCOUNT_SERVICE.createAccount(T1, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                    assertEquals(account, account2);
                    
                    return null;
                }
            });
    }
    
    @Test public void createAccountWithInvalidType() throws Exception
    {
        final int illegalAccountTypeId = -1000000;
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    try
                    {
                        ACCOUNT_SERVICE.createAccount(T1, illegalAccountTypeId, true);
                    }
                    catch (IllegalArgumentException expected)
                    {
                        return null;
                    }
                    
                    fail("Expected exception was not thrown: " + IllegalArgumentException.class.getSimpleName());
                    return null;
                }
            });
    }

    @Test public void getAccountById() throws Exception
    {
        final Account account = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Account execute() throws Throwable
                {
                    Account account = createAccount(T1, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                    cloudContext.addAccount(account);
                    return account;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    Account existingAccount = ACCOUNT_SERVICE.getAccount(account.getId());
                    assertNotNull(existingAccount);
                    Account nonExistingAccount = ACCOUNT_SERVICE.getAccount(-1L);
                    assertNull(nonExistingAccount);
                    return null;
                }
            });
    }

    @Test public void getAccountByDomain() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Account execute() throws Throwable
                {
                    Account account = createAccount(T1, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                    cloudContext.addAccount(account);
                    return account;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    Account existingAccount = ACCOUNT_SERVICE.getAccountByDomain(T1);
                    assertNotNull(existingAccount);
                    Account nonExistingAccount = ACCOUNT_SERVICE.getAccountByDomain("doesnotexist.com");
                    assertNull(nonExistingAccount);
                    return null;
                }
            });
    }

    @Test public void getAccounts() throws Exception
    {
        final List<Account> createdAccounts = new ArrayList<Account>();
        
        final List<Account> createdAccountsPublic     = new ArrayList<Account>();
        final List<Account> createdAccountsFree       = new ArrayList<Account>();
        final List<Account> createdAccountsStandard   = new ArrayList<Account>();
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        // delete any existing accounts
                        PagingRequest all = new PagingRequest(CannedQueryPageDetails.DEFAULT_PAGE_SIZE);
                        PagingResults<Account> allResults = ACCOUNT_SERVICE.getAccounts(null, all);
                        assertNotNull(allResults);
                        List<Account> allPage = allResults.getPage();
                        
                        if (allPage.size() > 0)
                        {
                            for (Account account : allPage)
                            {
                                ACCOUNT_SERVICE.removeAccount(account.getId());
                            }
                            
                            allResults = ACCOUNT_SERVICE.getAccounts(null, all);
                            assertEquals(0, allResults.getPage().size());
                        }
                        
                        return null;
                    }
                });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    Account account1 = createAccount(cloudContext.createTenantName("acme1"), AccountType.PUBLIC_DOMAIN_ACCOUNT_TYPE, true);
                    cloudContext.addAccount(account1);
                    createdAccounts.add(account1);
                    createdAccountsPublic.add(account1);
                    
                    Account account2 = createAccount(cloudContext.createTenantName("acme2"), AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                    cloudContext.addAccount(account2);
                    createdAccounts.add(account2);
                    createdAccountsFree.add(account2);
                    
                    Account account3 = createAccount(cloudContext.createTenantName("acme3"), AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                    cloudContext.addAccount(account3);
                    createdAccounts.add(account3);
                    createdAccountsFree.add(account3);
                    
                    Account account4 = createAccount(cloudContext.createTenantName("acme4"), AccountType.STANDARD_NETWORK_ACCOUNT_TYPE, true);
                    cloudContext.addAccount(account4);
                    createdAccounts.add(account4);
                    createdAccountsStandard.add(account4);
                    
                    Account account5 = createAccount(cloudContext.createTenantName("acme5"), AccountType.STANDARD_NETWORK_ACCOUNT_TYPE, true);
                    cloudContext.addAccount(account5);
                    createdAccounts.add(account5);
                    createdAccountsStandard.add(account5);
                    
                    Account account6 = createAccount(cloudContext.createTenantName("acme6"), AccountType.STANDARD_NETWORK_ACCOUNT_TYPE, true);
                    cloudContext.addAccount(account6);
                    createdAccounts.add(account6);
                    createdAccountsStandard.add(account6);
                    
                    return null;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    // get all accounts
                    PagingRequest all = new PagingRequest(CannedQueryPageDetails.DEFAULT_PAGE_SIZE);
                    PagingResults<Account> allResults = ACCOUNT_SERVICE.getAccounts(null, all);
                    assertNotNull(allResults);
                    List<Account> allPage = allResults.getPage();
                    assertEquals(createdAccounts.size(), allPage.size());
                    
                    for (Account account : createdAccounts)
                    {
                        assertTrue(allPage.contains(account));
                    }
                    
                    return null;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                     final int PAGE_SIZE = 2;
                     int createdAccountsSize = createdAccounts.size();

                     List<Account> all = new ArrayList<Account>();

                     // get pages of accounts
                     PagingRequest page = new PagingRequest(PAGE_SIZE);
                     PagingResults<Account> pagedResults = ACCOUNT_SERVICE.getAccounts(null, page);

                     // 1st page
                     List<Account> accounts = pagedResults.getPage();
                     assertNotNull(accounts);
                     assertEquals(accounts.size(), PAGE_SIZE);
                     all.addAll(accounts);
                        
                     // 2nd page
                     page = new PagingRequest(page.getSkipCount() + PAGE_SIZE, PAGE_SIZE, null);
                     page.setRequestTotalCountMax(createdAccountsSize);
                     pagedResults = ACCOUNT_SERVICE.getAccounts(null, page);
                     accounts = pagedResults.getPage();
                     assertNotNull(accounts);
                     assertEquals(accounts.size(), PAGE_SIZE);
                     all.addAll(accounts);
                     
                     // 3rd page
                     page = new PagingRequest(page.getSkipCount() + PAGE_SIZE, PAGE_SIZE, null);
                     page.setRequestTotalCountMax(createdAccountsSize);
                     pagedResults = ACCOUNT_SERVICE.getAccounts(null, page);
                     accounts = pagedResults.getPage();
                     assertNotNull(accounts);
                     assertEquals(accounts.size(), PAGE_SIZE);
                     all.addAll(accounts);
                     
                    assertEquals(createdAccountsSize, all.size());
                    
                    for (Account account : createdAccounts)
                    {
                        assertTrue(all.contains(account));
                    }
                    
                    return null;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    PagingRequest all = new PagingRequest(CannedQueryPageDetails.DEFAULT_PAGE_SIZE);
                    
                    // get filtered (by account type id) lists 
                    
                    PagingResults<Account> pagedResults = ACCOUNT_SERVICE.getAccounts(AccountType.PUBLIC_DOMAIN_ACCOUNT_TYPE, all);
                    List<Account> page = pagedResults.getPage();
                    assertEquals(createdAccountsPublic.size(), page.size());
                    for (Account account : createdAccountsPublic)
                    {
                        assertTrue(page.contains(account));
                    }
                    
                    pagedResults = ACCOUNT_SERVICE.getAccounts(AccountType.FREE_NETWORK_ACCOUNT_TYPE, all);
                    page = pagedResults.getPage();
                    assertEquals(createdAccountsFree.size(), page.size());
                    for (Account account : createdAccountsFree)
                    {
                        assertTrue(page.contains(account));
                    }
                    
                    pagedResults = ACCOUNT_SERVICE.getAccounts(AccountType.STANDARD_NETWORK_ACCOUNT_TYPE, all);
                    page = pagedResults.getPage();
                    assertEquals(createdAccountsStandard.size(), page.size());
                    for (Account account : createdAccountsStandard)
                    {
                        assertTrue(page.contains(account));
                    }
                    
                    return null;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    PagingRequest all = new PagingRequest(CannedQueryPageDetails.DEFAULT_PAGE_SIZE);
                    
                    // get sorted (by account type id) lists
                    
                    Pair<String,Boolean> sortByPair = new Pair<String,Boolean>("typeId", null);
                    
                    // ascending
                    sortByPair.setSecond(true);
                    PagingResults<Account> pagedResults = ACCOUNT_SERVICE.getAccounts(null, sortByPair, all);
                    List<Account> page = pagedResults.getPage();
                    
                    int previousAccountTypeId = Integer.MIN_VALUE;
                    for (Account account : page)
                    {
                       int currentAccountTypeId = account.getType().getId();
                       assertTrue(currentAccountTypeId >= previousAccountTypeId);
                       previousAccountTypeId = currentAccountTypeId;
                    }
                    
                    // descending
                    sortByPair.setSecond(false);
                    pagedResults = ACCOUNT_SERVICE.getAccounts(null, sortByPair, all);
                    page = pagedResults.getPage();
                    
                    previousAccountTypeId = Integer.MAX_VALUE;
                    for (Account account : page)
                    {
                       int currentAccountTypeId = account.getType().getId();
                       assertTrue(currentAccountTypeId <= previousAccountTypeId);
                       previousAccountTypeId = currentAccountTypeId;
                    }
                    
                    return null;
                }
            });
    }
    
    @Test public void updateAccount() throws Exception
    {
        final Account account = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Account execute() throws Throwable
                {
                    Account account = createAccount(T1, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                    cloudContext.addAccount(account);
                    return account;
                }
            });
        
        final long newQuota = 1234567890L;
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    Account existingAccount = ACCOUNT_SERVICE.getAccount(account.getId());
                    assertNotNull(existingAccount);
                    
                    assertEquals(account.getUsageQuota().getFileQuota(), existingAccount.getUsageQuota().getFileQuota());
                    
                    existingAccount.getUsageQuota().setFileQuota(newQuota);
                    
                    ACCOUNT_SERVICE.updateAccount(existingAccount);
                    return null;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    Account existingAccount = ACCOUNT_SERVICE.getAccount(account.getId());
                    assertNotNull(existingAccount);
                    
                    assertEquals(newQuota, existingAccount.getUsageQuota().getFileQuota());
                    
                    return null;
                }
            });
    }
    
    @Test public void deleteAccount() throws Exception
    {
        final Account account = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Account execute() throws Throwable
                {
                    Account account = createAccount(T1, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                    cloudContext.addAccount(account);
                    return account;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    Account existingAccount = ACCOUNT_SERVICE.getAccount(account.getId());
                    assertNotNull(existingAccount);
                    ACCOUNT_SERVICE.removeAccount(account.getId());
                    return null;
                }
            });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    Account nonExistingAccount = ACCOUNT_SERVICE.getAccount(account.getId());
                    assertNull(nonExistingAccount);
                    nonExistingAccount = ACCOUNT_SERVICE.getAccountByDomain(account.getDomains().get(0));
                    assertNull(nonExistingAccount);
                    return null;
                }
            });
    }
    
}
