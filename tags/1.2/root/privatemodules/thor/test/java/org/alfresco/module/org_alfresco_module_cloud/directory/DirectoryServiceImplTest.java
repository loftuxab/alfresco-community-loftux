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
package org.alfresco.module.org_alfresco_module_cloud.directory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;


public class DirectoryServiceImplTest
{
    private static ApplicationContext testContext;
    
    // Services
    private static DirectoryService directoryService;
    private static MutableAuthenticationService authenticationService;
    private static RetryingTransactionHelper transactionHelper;

    private CloudTestContext cloudContext;
    private String U1;
    
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        testContext = ApplicationContextHelper.getApplicationContext();
        transactionHelper = (RetryingTransactionHelper)testContext.getBean("retryingTransactionHelper");
        directoryService = (DirectoryService)testContext.getBean("directoryService");
        authenticationService = (MutableAuthenticationService)testContext.getBean("authenticationService");
    }
    
    @Before public void init() throws Exception
    {
        cloudContext = new CloudTestContext(testContext);
        U1 = cloudContext.createUserName("dave", cloudContext.createTenantName("acme"));
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
    }

    @After public void cleanup()
    {
        cloudContext.cleanup();
    }
    
    @Test
    public void createUser() throws Exception
    {
        createUserImpl();
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    @Test public void createAndDestroyUser() throws Exception
    {
        final String user = createUserImpl();
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                directoryService.deleteUser(user);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertFalse(authenticationService.authenticationExists(user));
                return null;
            }
        });
    }
    
    private String createUserImpl()
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertFalse(authenticationService.authenticationExists(U1));
                
                directoryService.createUser(U1, "David", "Smith", "smithy");
                cloudContext.addUser(U1);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertTrue(authenticationService.authenticationExists(U1));
                return null;
            }
        });
        
        return U1;
    }
    
    @Test
    public void homeAccount() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                directoryService.createUser(U1, "David", "Smith", "smithy");
                directoryService.setHomeAccount(U1, 1L);
                cloudContext.addUser(U1);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertEquals((Long)1L, directoryService.getHomeAccount(U1));
                return null;
            }
        });
    }

    @Test
    public void secondaryAccounts() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                directoryService.createUser(U1, "David", "Smith", "smithy");
                cloudContext.addUser(U1);
                directoryService.addSecondaryAccount(U1, 1L);
                directoryService.addSecondaryAccount(U1, 2L);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                List<Long> secondaryAccounts = directoryService.getSecondaryAccounts(U1);
                assertNotNull(secondaryAccounts);
                assertEquals(2, secondaryAccounts.size());
                assertTrue(secondaryAccounts.contains(1L));
                assertTrue(secondaryAccounts.contains(2L));
                assertFalse(secondaryAccounts.contains(3L));
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                directoryService.removeSecondaryAccount(U1, 1L);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                List<Long> secondaryAccounts = directoryService.getSecondaryAccounts(U1);
                assertNotNull(secondaryAccounts);
                assertEquals(1, secondaryAccounts.size());
                assertFalse(secondaryAccounts.contains(1L));
                assertTrue(secondaryAccounts.contains(2L));
                assertFalse(secondaryAccounts.contains(3L));
                return null;
            }
        });
    }

    @Test
    public void promoteSecondaryToHome() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                directoryService.createUser(U1, "David", "Smith", "smithy");
                cloudContext.addUser(U1);
                directoryService.setHomeAccount(U1, 1L);
                directoryService.addSecondaryAccount(U1, 2L);
                directoryService.addSecondaryAccount(U1, 3L);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertEquals((Long)1L, directoryService.getHomeAccount(U1));
                List<Long> secondaryAccounts = directoryService.getSecondaryAccounts(U1);
                assertNotNull(secondaryAccounts);
                assertEquals(2, secondaryAccounts.size());
                assertTrue(secondaryAccounts.contains(2L));
                assertTrue(secondaryAccounts.contains(3L));
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                directoryService.setHomeAccount(U1, 2L);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertEquals((Long)2L, directoryService.getHomeAccount(U1));
                List<Long> secondaryAccounts = directoryService.getSecondaryAccounts(U1);
                assertNotNull(secondaryAccounts);
                assertEquals(1, secondaryAccounts.size());
                assertFalse(secondaryAccounts.contains(2L));
                assertTrue(secondaryAccounts.contains(3L));
                return null;
            }
        });
    }
    
    @Test(expected=DirectoryServiceException.class)
    public void secondaryAlreadyHome() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                directoryService.createUser(U1, "David", "Smith", "smithy");
                directoryService.setHomeAccount(U1, 1L);
                directoryService.addSecondaryAccount(U1, 1L);
                return null;
            }
        });
    }

    @Test
    public void defaultAccount() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                directoryService.createUser(U1, "David", "Smith", "smithy");
                directoryService.setHomeAccount(U1, 1L);
                directoryService.setDefaultAccount(U1, 1L);
                cloudContext.addUser(U1);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertEquals((Long)1L, directoryService.getHomeAccount(U1));
                assertEquals((Long)1L, directoryService.getDefaultAccount(U1));
                return null;
            }
        });
    }

    /**
     * @since Thor Phase 2 Sprint 1
     */
    @Test public void defaultAccountChangesDuringUserRemovalFromAccounts() throws Exception
    {
        final List<Long> secondaryAccountIds = Arrays.asList(new Long[]{2L, 3L, 4L});
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                directoryService.createUser(U1, "David", "Smith", "smithy");
                directoryService.setHomeAccount(U1, 1L);
                for (Long secondaryAccountId : secondaryAccountIds)
                {
                    directoryService.addSecondaryAccount(U1, secondaryAccountId);
                }
                directoryService.setDefaultAccount(U1, secondaryAccountIds.get(0));
                cloudContext.addUser(U1);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                for (final Long secondaryAccountId : secondaryAccountIds)
                {
                    directoryService.removeSecondaryAccount(U1, secondaryAccountId);
                    List<Long> remainingSecondaryAccounts = directoryService.getSecondaryAccounts(U1);
                    Long defaultAccount = directoryService.getDefaultAccount(U1);
                    if ( !remainingSecondaryAccounts.isEmpty())
                    {
                        assertTrue("Current implementation should have default account falling back to various secondary accounts",
                                remainingSecondaryAccounts.contains(defaultAccount));
                    }
                }
                return null;
            }
        });
    }
    
    @Test(expected=DirectoryServiceException.class)
    public void defaultAccountInvalidForUser() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                directoryService.createUser(U1, "David", "Smith", "smithy");
                directoryService.setDefaultAccount(U1, 1L);
                cloudContext.addUser(U1);
                return null;
            }
        });
    }
}
