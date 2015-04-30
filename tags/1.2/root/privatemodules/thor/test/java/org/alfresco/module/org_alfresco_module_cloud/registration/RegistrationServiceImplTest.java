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
package org.alfresco.module.org_alfresco_module_cloud.registration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.Clock;
import org.activiti.engine.runtime.Execution;
import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.directory.InvalidEmailAddressException;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceImpl.INVALID_EMAIL_TYPE;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceImpl.InvalidEmail;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.workflow.BPMEngineRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.test_category.SharedJVMTestsCategory;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.ApplicationContext;

import com.ibm.icu.text.MessageFormat;

@Category(SharedJVMTestsCategory.class)
public class RegistrationServiceImplTest
{
    private static ApplicationContext testContext;
    
    // Services
    private static RegistrationService registrationService;
    private static MutableAuthenticationService authenticationService;
    private static NodeService nodeService;
    private static PersonService personService;
    private static AccountService accountService;
    private static AuthorityService authorityService;
    private static SiteService siteService;
    private static RetryingTransactionHelper transactionHelper;
    private static ProcessEngine processEngine;
    
    private CloudTestContext cloudContext;
    private String T1;
    private String T2;
    private String DAVE_T1;
    private String FRED_T1;
    private String JOHN_T1;
    private String FRED_T2;
    private String APOSTROPHE_T1;
    
    private String SITE1_T2 = "site1-T2";
    private String SITE2_T2 = "site2-T2";
    
    
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        testContext = ApplicationContextHelper.getApplicationContext();
        
        transactionHelper = (RetryingTransactionHelper)testContext.getBean("retryingTransactionHelper");
        registrationService = (RegistrationService)testContext.getBean("registrationService");
        accountService = (AccountService)testContext.getBean("AccountService");
        nodeService = (NodeService)testContext.getBean("nodeService");
        personService = (PersonService)testContext.getBean("personService");
        authorityService = (AuthorityService)testContext.getBean("authorityService");
        authenticationService = (MutableAuthenticationService)testContext.getBean("authenticationService");
        siteService = (SiteService)testContext.getBean("siteService");
        processEngine = (ProcessEngine)testContext.getBean("activitiProcessEngine");
    }
    
    @Before public void init() throws Exception
    {
        cloudContext = new CloudTestContext(testContext);
        T1 = cloudContext.createTenantName("acme");
        T2 = cloudContext.createTenantName("rush");
        DAVE_T1 = cloudContext.createUserName("dave", T1);
        FRED_T1 = cloudContext.createUserName("fred", T1);
        JOHN_T1 = cloudContext.createUserName("john", T1);
        FRED_T2 = cloudContext.createUserName("fred", T2);
        APOSTROPHE_T1 = cloudContext.createUserName("a'b", T1);
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
    }
    
    /** Delete any temporary accounts created during the previous test */
    @After public void cleanup()
    {
        cloudContext.cleanup();
    }

    @Test(expected=InvalidEmailAddressException.class)
    public void invalidEmail() throws Exception
    {
        registrationService.createUser("dave", "David", "Smith", "smithy");
    }
    
    @Test
    public void createUser() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertFalse(authenticationService.authenticationExists(DAVE_T1));
                assertNull(accountService.getAccountByDomain(T1));
                assertFalse(personService.personExists(DAVE_T1));
                
                registrationService.createUser(DAVE_T1, "David", "Smith", "smithy");
                cloudContext.addUser(DAVE_T1);
                cloudContext.addAccountDomain(T1);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertTrue(authenticationService.authenticationExists(DAVE_T1));
                assertNotNull(accountService.getAccountByDomain(T1));

                // ensure person created in tenant only
                assertFalse(personService.personExists(DAVE_T1));
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        assertTrue(personService.personExists(DAVE_T1));
                        assertTrue(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(DAVE_T1));
                        return null;
                    }
                }, T1);
                
                return null;
            }
        });
    }

    @Test
    public void createUserWithApostrophe() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertFalse(authenticationService.authenticationExists(APOSTROPHE_T1));
                assertNull(accountService.getAccountByDomain(T1));
                assertFalse(personService.personExists(APOSTROPHE_T1));
                
                registrationService.createUser(APOSTROPHE_T1, "Apostrophe", "Ap", "Apos");
                cloudContext.addUser(APOSTROPHE_T1);
                cloudContext.addAccountDomain(T1);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertTrue(authenticationService.authenticationExists(APOSTROPHE_T1));
                assertNotNull(accountService.getAccountByDomain(T1));

                // ensure person created in tenant only
                assertFalse(personService.personExists(APOSTROPHE_T1));
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        assertTrue(personService.personExists(APOSTROPHE_T1));
                        assertTrue(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(APOSTROPHE_T1));
                        return null;
                    }
                }, T1);
                
                return null;
            }
        });
    }

    /**
     * Public users have no home account.
     * 
     * @since Thor Phase 2 Sprint 1
     */
    @Test public void createPublicUser() throws Exception
    {
        final String publicEmailDomain = "public.test";
        final String publicUserEmail = "person.persson@" + publicEmailDomain;
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override public Void execute() throws Throwable
            {
                assertFalse(authenticationService.authenticationExists(publicUserEmail));
                assertNull(accountService.getAccountByDomain(publicEmailDomain));
                assertFalse(personService.personExists(publicUserEmail));
                
                registrationService.createUser(publicUserEmail, "Person", "Persson", "password");
                cloudContext.addUser(publicUserEmail);
                cloudContext.addAccountDomain(publicEmailDomain);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override public Void execute() throws Throwable
            {
                assertTrue(authenticationService.authenticationExists(publicUserEmail));
                assertNotNull(accountService.getAccountByDomain(publicEmailDomain));
                
                // ensure person created in tenant only
                assertFalse(personService.personExists(publicUserEmail));
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    @Override public Object doWork() throws Exception
                    {
                        assertTrue(personService.personExists(publicUserEmail));
                        assertTrue(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(publicUserEmail));
                        return null;
                    }
                }, publicEmailDomain);
                
                // ensure public users have no auto-created private site.
                TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                {
                    @Override public Object doWork() throws Exception
                    {
                        assertTrue("Public domain users should not automatically have home sites",
                                   siteService.listSites(publicUserEmail).isEmpty());
                        return null;
                    }
                }, publicUserEmail, publicEmailDomain);
                
                return null;
            }
        });
    }
    
    @Test public void promoteUserToNetworkAdminAndDemoteThemAgain() throws Exception
    {
        // Create two users & promote them to NetworkAdmin.
        // We need to have two NetworkAdmins because the system prevents the demotion of the last NetworkAdmin in any tenant.
        final Long accountID = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Long>()
        {
            @Override
            public Long execute() throws Throwable
            {
                
                Long accountId = null;
                
                for (String username : new String[]{DAVE_T1, FRED_T1})
                {
                    assertFalse(authenticationService.authenticationExists(username));
                    assertFalse(personService.personExists(username));
                    
                    registrationService.createUser(username, "fff", "lll", "password");
                    cloudContext.addUser(username);
                    
                    // 1st user will create the account
                    if (accountId == null)
                    {
                        Account account = accountService.getAccountByDomain(T1);
                        accountId = account.getId();
                    
                        // override quota for number of network admins (default is 0 for free account)
                        account.getUsageQuota().setPersonNetworkAdminCountQuota(5);
                        accountService.updateAccount(account);
                    }
                    
                    registrationService.promoteUserToNetworkAdmin(accountId, username);
                }
                cloudContext.addAccountDomain(T1);
                return accountId;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                // ensure person has the correct authority and the marker aspect for a network admin
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        assertTrue(personService.personExists(DAVE_T1));
                        final NodeRef personNode = personService.getPerson(DAVE_T1);
                        assertTrue(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_NETWORK_ADMINS, true).contains(DAVE_T1));
                        assertTrue(nodeService.hasAspect(personNode, CloudModel.ASPECT_NETWORK_ADMIN));
                        return null;
                    }
                }, T1);
                
                return null;
            }
        });
        
        // Demote the user.
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                registrationService.demoteUserFromNetworkAdmin(accountID, DAVE_T1);
                return null;
            }
        });
        
        // Ensure the authority and marker aspect are correctly modified again.
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        assertTrue(personService.personExists(DAVE_T1));
                        final NodeRef personNode = personService.getPerson(DAVE_T1);
                        assertFalse("User was unexpectedly still a network admin",
                                    authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_NETWORK_ADMINS, true).contains(DAVE_T1));
                        assertFalse("User's person unexpectedly had the networkAdmin marker asepect.",
                                    nodeService.hasAspect(personNode, CloudModel.ASPECT_NETWORK_ADMIN));
                        return null;
                    }
                }, T1);
                
                return null;
            }
        });
    }
    
    @Test
    public void createTwoUsersInSameDomain() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertFalse(authenticationService.authenticationExists(DAVE_T1));
                assertFalse(authenticationService.authenticationExists(FRED_T1));
                assertNull(accountService.getAccountByDomain(T1));
                assertFalse(personService.personExists(DAVE_T1));
                assertFalse(personService.personExists(FRED_T1));
                
                registrationService.createUser(DAVE_T1, "David", "Smith", "smithy");
                cloudContext.addUser(DAVE_T1);
                cloudContext.addAccountDomain(T1);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertTrue(authenticationService.authenticationExists(DAVE_T1));
                assertFalse(authenticationService.authenticationExists(FRED_T1));
                assertNotNull(accountService.getAccountByDomain(T1));

                // ensure person created in tenant only
                assertFalse(personService.personExists(DAVE_T1));
                assertFalse(personService.personExists(FRED_T1));
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        assertTrue(personService.personExists(DAVE_T1));
                        assertFalse(personService.personExists(FRED_T1));
                        return null;
                    }
                }, T1);
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                registrationService.createUser(FRED_T1, "Fred", "Smith", "freddy");
                cloudContext.addUser(FRED_T1);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertTrue(authenticationService.authenticationExists(DAVE_T1));
                assertTrue(authenticationService.authenticationExists(FRED_T1));
                
                // ensure person created in tenant only
                assertFalse(personService.personExists(DAVE_T1));
                assertFalse(personService.personExists(FRED_T1));
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        assertTrue(personService.personExists(DAVE_T1));
                        assertTrue(personService.personExists(FRED_T1));
                        return null;
                    }
                }, T1);

                return null;
            }
        });
    }

    @Test
    public void createTwoUsersInTwoDomains() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertFalse(authenticationService.authenticationExists(DAVE_T1));
                assertFalse(authenticationService.authenticationExists(FRED_T2));
                assertNull(accountService.getAccountByDomain(T1));
                assertNull(accountService.getAccountByDomain(T2));
                assertFalse(personService.personExists(DAVE_T1));
                assertFalse(personService.personExists(FRED_T2));
                
                registrationService.createUser(DAVE_T1, "David", "Smith", "smithy");
                cloudContext.addUser(DAVE_T1);
                cloudContext.addAccountDomain(T1);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertTrue(authenticationService.authenticationExists(DAVE_T1));
                assertFalse(authenticationService.authenticationExists(FRED_T2));
                assertNotNull(accountService.getAccountByDomain(T1));
                assertNull(accountService.getAccountByDomain(T2));

                assertFalse(personService.personExists(DAVE_T1));
                assertFalse(personService.personExists(FRED_T2));
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        assertTrue(personService.personExists(DAVE_T1));
                        assertFalse(personService.personExists(FRED_T2));
                        assertTrue(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(DAVE_T1));
                        assertFalse(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(FRED_T2));
                        return null;
                    }
                }, T1);
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                registrationService.createUser(FRED_T2, "Fred", "Smith", "freddy");
                cloudContext.addUser(FRED_T2);
                cloudContext.addAccountDomain(T2);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertTrue(authenticationService.authenticationExists(DAVE_T1));
                assertTrue(authenticationService.authenticationExists(FRED_T2));
                assertNotNull(accountService.getAccountByDomain(T1));
                assertNotNull(accountService.getAccountByDomain(T2));
                
                assertFalse(personService.personExists(DAVE_T1));
                assertFalse(personService.personExists(FRED_T2));
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        assertTrue(personService.personExists(DAVE_T1));
                        assertFalse(personService.personExists(FRED_T2));
                        assertTrue(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(DAVE_T1));
                        assertFalse(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(FRED_T2));
                        return null;
                    }
                }, T1);
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        assertFalse(personService.personExists(DAVE_T1));
                        assertTrue(personService.personExists(FRED_T2));
                        assertFalse(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(DAVE_T1));
                        assertTrue(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(FRED_T2));
                        return null;
                    }
                }, T2);
                
                return null;
            }
        });
    }
    
    @Test
    public void isActivatedEmailAddress() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertFalse(registrationService.isActivatedEmailAddress(DAVE_T1));
                assertFalse(registrationService.isActivatedEmailAddress(FRED_T2));
                
                registrationService.createUser(DAVE_T1, "David", "Smith", "smithy");
                cloudContext.addUser(DAVE_T1);
                cloudContext.addAccountDomain(T1);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertTrue(registrationService.isActivatedEmailAddress(DAVE_T1));
                assertFalse(registrationService.isActivatedEmailAddress(FRED_T2));
                return null;
            }
        });
    }
    
    @Test public void signUpAndActivateEmail() throws Exception
    {
        final String email = DAVE_T1;
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertFalse(registrationService.isRegisteredEmailAddress(email));
                assertFalse(registrationService.isActivatedEmailAddress(email));
                return null;
            }
        });
        
        final Registration registration = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Registration>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Registration execute() throws Throwable
            {
                Registration registration = registrationService.registerEmail(email, "test-" + this.getClass().getSimpleName(), null, null);
                assertNotNull(registration);
                assertEquals(email, registration.getEmailAddress());
                assertNotNull(registration.getId());
                assertNotNull(registration.getKey());
                assertNotNull(registration.getRegistrationDate());
                cloudContext.addRegistration(registration);
                return registration;
            }
        });

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertTrue(registrationService.isRegisteredEmailAddress(registration.getEmailAddress()));
                assertFalse(registrationService.isActivatedEmailAddress(email));
                return null;
            }
        });

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                Registration registered = registrationService.activateRegistration(registration.getId(), registration.getKey(), "dave", "c", "password");
                cloudContext.removeRegistration(registration);
                cloudContext.addAccountDomain(T1);
                cloudContext.addUser(DAVE_T1);
                assertEquals(DAVE_T1, registered.getEmailAddress());
                return null;
            }
        });

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertFalse(registrationService.isRegisteredEmailAddress(registration.getEmailAddress()));
                assertTrue(registrationService.isActivatedEmailAddress(email));
                return null;
            }
        });
    }
    
    // CLOUD-154: Invite users to signup in alfresco
    @Test public void signUpInvitedUsers() throws Exception
    {
        // Ensure the inviter exists in Tenant T1
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                registrationService.createUser(DAVE_T1, "David", "Smith", "smithy");
                cloudContext.addUser(DAVE_T1);
                cloudContext.addAccountDomain(T1);
                
                return null;
            }
        });
        
        final String inviteeEmail1 = FRED_T1;
        final String inviteeEmail2 = JOHN_T1;
        final String inviteeEmail3Address = cloudContext.createUserName("some.body", T1);;
        final String inviteeEmail3 = MessageFormat.format("Some Body <{0}>", new Object[] {inviteeEmail3Address});
        
        // Ensure users not already registered
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertFalse(registrationService.isRegisteredEmailAddress(inviteeEmail1));
                assertFalse(registrationService.isActivatedEmailAddress(inviteeEmail1));
                assertFalse(registrationService.isActivatedEmailAddress(inviteeEmail2));
                assertFalse(registrationService.isActivatedEmailAddress(inviteeEmail2));
                assertFalse(registrationService.isRegisteredEmailAddress(inviteeEmail3Address));
                assertFalse(registrationService.isActivatedEmailAddress(inviteeEmail3Address));
                return null;
            }
        });
        
        // Invite 5 emails: 2 of same (future) tenant, one of other tenant, one invalid email-address and one existing user. 
        // Only 2 will should be accepted
        final List<String> emailAdresses = Arrays.asList(
            inviteeEmail1, // Valid user to invite
            inviteeEmail2, // Valid user to invite
            inviteeEmail3,
            FRED_T2, // Other tenant, cannot invite
            "illegalEmailAddress", // Will be rejected
            DAVE_T1 // Exiting user
        );
        
        final List<InvalidEmail> invalidEmails = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<List<InvalidEmail>>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public List<InvalidEmail> execute() throws Throwable
            {
                return TenantUtil.runAsUserTenant(new TenantRunAsWork<List<InvalidEmail>>()
                {
                    @Override
                    public List<InvalidEmail> doWork() throws Exception
                    {
                        String currentUserName = AuthenticationUtil.getFullyAuthenticatedUser();
                        
                        // Using the RunAs user won't cut it, getFullyAuthenticatedUser() is used to determine signup initiator 
                        AuthenticationUtil.setFullyAuthenticatedUser(DAVE_T1);
                        try
                        {
                            return registrationService.registerEmails(emailAdresses, null, null, null, new HashMap<String, Serializable>(), true);
                        }
                        finally
                        {
                            AuthenticationUtil.setFullyAuthenticatedUser(currentUserName);
                        }
                    }
                },
                DAVE_T1, T1);
            }
        });
        
        final List<Registration> registrations =  TenantUtil.runAsTenant(new TenantRunAsWork<List<Registration>>()
                    {
            @Override
            public List<Registration> doWork() throws Exception
            {
               
                List<Registration> registrations = new ArrayList<Registration>();
                // Get the created registrations and add to cloud-context
                for(String email : emailAdresses)
                {
                    try 
                    {
                        Registration reg = registrationService.getRegistration(email);
                        if(reg != null)
                        {
                            registrations.add(reg);
                            cloudContext.addRegistration(reg);
                        }
                    }
                    catch(InvalidEmailAddressException ignore)
                    {
                        // Ignore, one of them will be an illegally formatted one
                    }
                }
                try 
                {
                    Registration reg = registrationService.getRegistration(inviteeEmail3Address);
                    if(reg != null)
                    {
                        registrations.add(reg);
                        cloudContext.addRegistration(reg);
                    }
                }
                catch(InvalidEmailAddressException ignore)
                {
                    // Ignore, one of them will be an illegally formatted one
                }
                return registrations;
            }
        }, T1);
        
        // Validate the created registrations AFTER adding all of them to cloudContext to allow cleanup
        assertEquals(3L, registrations.size());
        for(Registration reg : registrations)
        {
            assertEquals(reg.getInitiatorEmailAddress(), DAVE_T1);
            assertEquals(reg.getInitiatorFirstName(), "David");
            assertEquals(reg.getInitiatorLastName(), "Smith");
        }
        
        // Assert invalid emails and the corresponding reasons
        assertEquals(3l, invalidEmails.size());
        for(InvalidEmail ie : invalidEmails)
        {
            if(ie.getEmail().equals(FRED_T2))
            {
                assertEquals(INVALID_EMAIL_TYPE.INCORRECT_DOMAIN, ie.getType());
            }
            else if(ie.getEmail().equals("illegalEmailAddress"))
            {
                assertEquals(INVALID_EMAIL_TYPE.INCORRECT_DOMAIN, ie.getType());
            } 
            else if(ie.getEmail().equals(DAVE_T1))
            {
                assertEquals(INVALID_EMAIL_TYPE.USER_EXISTS, ie.getType());
            }
            else
            {
                fail("Unexpected InvalidEmail was returned: " + ie.getEmail());
            }
        }
    }
    
 @Test public void signUpCheckRemindersAndAutmaticEndWhenExpired() throws Exception
    {
        final String email = DAVE_T1;
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertFalse(registrationService.isRegisteredEmailAddress(email));
                assertFalse(registrationService.isActivatedEmailAddress(email));
                return null;
            }
        });
        
        final Registration registration = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Registration>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Registration execute() throws Throwable
            {
                Registration registration = registrationService.registerEmail(email, "test-" + this.getClass().getSimpleName(), null, null);
                assertNotNull(registration);
                assertEquals(email, registration.getEmailAddress());
                assertNotNull(registration.getId());
                assertNotNull(registration.getKey());
                assertNotNull(registration.getRegistrationDate());
                cloudContext.addRegistration(registration);
                return registration;
            }
        });

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertTrue(registrationService.isRegisteredEmailAddress(registration.getEmailAddress()));
                assertFalse(registrationService.isActivatedEmailAddress(email));
                return null;
            }
        });

        // No transaction needed for the folowing operations, all READ's
        String processInstanceId = BPMEngineRegistry.getLocalId(registration.getId());
        
        // Move time to trigger first reminder mail
    	List<Execution> executions = processEngine.getRuntimeService()
    		.createExecutionQuery()
    		.processInstanceId(processInstanceId)
    		.list();
    	
    	
    	String executionId = null;
    	for(Execution e : executions)
    	{
    		if(!((ExecutionEntity) e).isProcessInstanceType())
    		{
    			executionId = e.getId();
    			break;
    		}
    	}
    	
    	// Count the number of jobs in the execution
    	long jobCount = processEngine.getManagementService().createJobQuery().executionId(executionId).count();
    	assertEquals(3, jobCount);
    	
    	// Roll activiti-time forward by 3 days and one minute
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.DAY_OF_MONTH, 3);
    	calendar.add(Calendar.MINUTE, 1);
    	Clock actiClock = processEngine.getProcessEngineConfiguration().getClock();
    	actiClock.setCurrentTime(calendar.getTime());
    	
    	// Check if timer-job is executed
    	boolean firstReminderSent = waitUntilJobExecuted(jobCount, executionId);
    	assertTrue("First reminder timer hasn't fired", firstReminderSent);
    	
    	firstReminderSent = waitUntillMailSentCountChanged(1, processInstanceId);
    	assertTrue("First reminder mail wasn't sent", firstReminderSent);
    	
    	// Check the next 3 reminders    	
    	for(int i=0; i < 3; i++)
    	{
    		int numberOfDays =  7 * (i + 1);
    		calendar = Calendar.getInstance();
        	calendar.add(Calendar.DAY_OF_MONTH, numberOfDays);
        	calendar.add(Calendar.MINUTE, 1);
        	actiClock.setCurrentTime(calendar.getTime());
        	
        	boolean reminderSent = waitUntillMailSentCountChanged(2 + i, processInstanceId);
        	assertTrue("Reminder at " + numberOfDays + " days email wasn't sent", reminderSent);
    	}
    	
    	// Finally, check if the flow stops itself after 28 days
    	calendar.add(Calendar.DAY_OF_MONTH, 28);
    	calendar.add(Calendar.MINUTE, 1);
    	actiClock.setCurrentTime(calendar.getTime());
    	
    	boolean finishTimerFired = waitUntilJobExecuted(1, executionId);
    	assertTrue("Finish timer didn't fire",finishTimerFired);
    	
    	assertEquals("Process isn't finished", 0, 
    		processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstanceId).count());
    	
    	// Double-check through service
    	transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
            	cloudContext.removeRegistration(registration);
                assertFalse(registrationService.isRegisteredEmailAddress(email));
                assertFalse(registrationService.isActivatedEmailAddress(email));
                return null;
            }
        });
    }
    
    private boolean waitUntilJobExecuted(long initialCount, String executionId)
    {
    	boolean executed = false;
    	
    	// Wait 20 times .5 second
    	for(int i=0; i<20; i++)
    	{
    		long currentCount = processEngine.getManagementService().createJobQuery().executionId(executionId).count();
    		if(currentCount < initialCount)
    		{
    			executed = true;
    			break;
    		}
    		try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// Ignore this
			}
    		
    	}
    	return executed;
    }
    
    private boolean waitUntillMailSentCountChanged(Integer initialCount, String processInstanceId)
    {
    	boolean executed = false;
    	
    	// Wait 20 times .5 second
    	for(int i=0; i<20; i++)
    	{
    		Integer currentCount = (Integer) processEngine.getRuntimeService().getVariable(processInstanceId, WorkflowModelSelfSignup.WF_PROP_MAILS_SENT_ACTIVITI);
    		if(currentCount != null && currentCount > initialCount)
    		{
    			executed = true;
    			break;
    		}
    		try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// Ignore this
			}
    		
    	}
    	return executed;
    }
    @Test public void getHomeAccount() throws Exception
    {
        final String email = DAVE_T1;
        
        final Account account = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Account execute() throws Throwable
            {
                Account account = registrationService.createUser(DAVE_T1, "dave", "c", "password");
                cloudContext.addUser(DAVE_T1);
                cloudContext.addAccountDomain(T1);
                return account;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                Account homeAccount = registrationService.getHomeAccount(email);
                assertEquals(account, homeAccount);
                return null;
            }
        });
    }
    
    @Test public void alreadyActivatedEmail() throws Exception
    {
        final String email = DAVE_T1;
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                registrationService.createUser(DAVE_T1, "dave", "c", "password");
                cloudContext.addUser(DAVE_T1);
                cloudContext.addAccountDomain(T1);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Registration>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Registration execute() throws Throwable
            {
                Registration registration = registrationService.registerEmail(email, "test-" + this.getClass().getSimpleName(), null, null);
                assertNull(registration.getId());
                assertNull(registration.getKey());
                assertNull(registration.getRegistrationDate());
                return registration;
            }
        });
    }
    
    /**
     * This test method ensures that the addition and removal of a user to/from another network works as expected.
     * So this user will be an 'external user' in that network.
     */
    @Test public void addExternalUserToAnotherNetworkAndRemoveThem() throws Exception
    {
        // Create the test user & a test account (different network)
        final Account destAccount = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Account execute() throws Throwable
            {
                assertFalse(authenticationService.authenticationExists(DAVE_T1));
                assertNull(accountService.getAccountByDomain(T1));
                assertNull(accountService.getAccountByDomain(T2));
                assertFalse(personService.personExists(DAVE_T1));
                
                registrationService.createUser(DAVE_T1, "David", "Smith", "smithy");
                cloudContext.addUser(DAVE_T1);
                cloudContext.addAccountDomain(T1);
                Account destAccount = accountService.createAccount(T2, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                cloudContext.addAccountDomain(T2);
                return destAccount;
            }
        });
        
        // Add the user to the other network.
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertTrue(authenticationService.authenticationExists(DAVE_T1));
                assertNotNull(accountService.getAccountByDomain(T1));
                assertNotNull(accountService.getAccountByDomain(T2));

                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        registrationService.addUser(destAccount.getId(), DAVE_T1);
                        return null;
                    }
                }, T2);
                
                return null;
            }
        });
        
        // validate person & user objects and user's accounts
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertTrue(authenticationService.authenticationExists(DAVE_T1));
                assertNotNull(accountService.getAccountByDomain(T1));
                assertNotNull(accountService.getAccountByDomain(T2));

                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        assertTrue(personService.personExists(DAVE_T1));
                        assertFalse(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(DAVE_T1));
                        assertTrue(registrationService.getSecondaryAccounts(DAVE_T1).contains(destAccount));
                        return null;
                    }
                }, T2);
                
                return null;
            }
        });
        
        // Remove the user from the 'other' network. Validate person & user objects and account memberships.
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                registrationService.removeExternalUser(destAccount.getId(), DAVE_T1);

                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        assertFalse(personService.personExists(DAVE_T1));
                        assertFalse(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(DAVE_T1));
                        assertFalse(registrationService.getSecondaryAccounts(DAVE_T1).contains(destAccount));
                        assertTrue(registrationService.getSecondaryAccounts(DAVE_T1).isEmpty());
                        return null;
                    }
                }, T2);
                
                return null;
            }
        });
    }
    
    /**
     * This test method ensures that the external users are auto-removed when they are removed from (including if they leave themselves) the last site of that network.
     */
    @Test public void addExternalUserAndAutoRemoveAfterLastSite1() throws Exception
    {
        final Account destAccount = removeExternalUserSetup();
        
        // validate person belongs to network
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        assertTrue(personService.personExists(DAVE_T1));
                        assertFalse(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(DAVE_T1));
                        assertTrue(registrationService.getSecondaryAccounts(DAVE_T1).contains(destAccount));
                        assertEquals(2, siteService.listSites(DAVE_T1).size());
                        return null;
                    }
                });
                return null;
            }
        }, AuthenticationUtil.getAdminUserName(), T2);
        
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        // remove from last but one site
                        siteService.removeMembership(SITE1_T2, DAVE_T1);
                        return null;
                    }
                });
                return null;
            }
        }, FRED_T2, T2);
        
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        // is still an external user
                        assertTrue(personService.personExists(DAVE_T1));
                        assertFalse(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(DAVE_T1));
                        assertTrue(registrationService.getSecondaryAccounts(DAVE_T1).contains(destAccount));
                        assertEquals(1, siteService.listSites(DAVE_T1).size());
                        return null;
                    }
                });
                return null;
            }
        }, AuthenticationUtil.getAdminUserName(), T2);
        
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        // remove from last site
                        siteService.removeMembership(SITE2_T2, DAVE_T1);
                        return null;
                    }
                });
                return null;
            }
        }, FRED_T2, T2);
        
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        // is no longer an external user
                        assertFalse(personService.personExists(DAVE_T1));
                        assertFalse(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(DAVE_T1));
                        assertFalse(registrationService.getSecondaryAccounts(DAVE_T1).contains(destAccount));
                        assertTrue(registrationService.getSecondaryAccounts(DAVE_T1).isEmpty());
                        assertEquals(0, siteService.listSites(DAVE_T1).size());
                        return null;
                    }
                });
                return null;
            }
        }, AuthenticationUtil.getAdminUserName(), T2);
    }
    
    /**
     * This test method ensures that the external users are auto-removed when a site is deleted and it is the last site that they belong to in that network.
     */
    @Test public void addExternalUserAndAutoRemoveAfterLastSite2() throws Exception
    {
        final Account destAccount = removeExternalUserSetup();
        
        // validate person belongs to network
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        assertTrue(personService.personExists(DAVE_T1));
                        assertFalse(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(DAVE_T1));
                        assertTrue(registrationService.getSecondaryAccounts(DAVE_T1).contains(destAccount));
                        assertEquals(2, siteService.listSites(DAVE_T1).size());
                        return null;
                    }
                });
                return null;
            }
        }, AuthenticationUtil.getAdminUserName(), T2);
        
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        // delete last but one site
                        siteService.deleteSite(SITE1_T2);
                        return null;
                    }
                });
                return null;
            }
        }, FRED_T2, T2);
        
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        // is still an external user
                        assertTrue(personService.personExists(DAVE_T1));
                        assertFalse(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(DAVE_T1));
                        assertTrue(registrationService.getSecondaryAccounts(DAVE_T1).contains(destAccount));
                        assertEquals(1, siteService.listSites(DAVE_T1).size());
                        return null;
                    }
                });
                return null;
            }
        }, AuthenticationUtil.getAdminUserName(), T2);
        
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        // delete last site
                        siteService.deleteSite(SITE2_T2);
                        return null;
                    }
                });
                return null;
            }
        }, FRED_T2, T2);
        
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        // is no longer an external user
                        assertFalse(personService.personExists(DAVE_T1));
                        assertFalse(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(DAVE_T1));
                        assertFalse(registrationService.getSecondaryAccounts(DAVE_T1).contains(destAccount));
                        assertTrue(registrationService.getSecondaryAccounts(DAVE_T1).isEmpty());
                        assertEquals(0, siteService.listSites(DAVE_T1).size());
                        return null;
                    }
                });
                return null;
            }
        }, AuthenticationUtil.getAdminUserName(), T2);
    }
    
    private Account removeExternalUserSetup()
    {
        AuthenticationUtil.clearCurrentSecurityContext();
        
        // Create the test user & a test account (different network)
        final Account destAccount = TenantUtil.runAsSystemTenant(new TenantRunAsWork<Account>()
        {
            public Account doWork() throws Exception
            {
                return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
                {
                    @Override
                    public Account execute() throws Throwable
                    {
                        assertFalse(authenticationService.authenticationExists(DAVE_T1));
                        assertNull(accountService.getAccountByDomain(T1));
                        assertNull(accountService.getAccountByDomain(T2));
                        assertFalse(personService.personExists(DAVE_T1));
                        
                        registrationService.createUser(DAVE_T1, "David", "Smith", "smithy");
                        cloudContext.addUser(DAVE_T1);
                        cloudContext.addAccountDomain(T1);
                        
                        registrationService.createUser(FRED_T2, "Fred", "Smith", "freddy");
                        cloudContext.addUser(FRED_T2);
                        cloudContext.addAccountDomain(T2);
                        
                        Account destAccount = accountService.getAccountByDomain(T2);
                        
                        return destAccount;
                    }
                });
            }
        }, TenantService.DEFAULT_DOMAIN);
        
        // Create two sites in other network.
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        siteService.createSite("site-preset", SITE1_T2, "site title", "site description", SiteVisibility.PUBLIC);
                        siteService.createSite("site-preset", SITE2_T2, "site title", "site description", SiteVisibility.PRIVATE);
                        return null;
                    }
                });
                return null;
            }
        }, FRED_T2, T2);
        
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        assertTrue(authenticationService.authenticationExists(DAVE_T1));
                        assertNotNull(accountService.getAccountByDomain(T1));
                        assertNotNull(accountService.getAccountByDomain(T2));
                        return null;
                    }
                });
                return null;
            }
        }, TenantService.DEFAULT_DOMAIN);
        
        // Add the user to the other network
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        registrationService.addUser(destAccount.getId(), DAVE_T1);
                        return null;
                    }
                });
                return null;
            }
        }, T2);
        
        // Add the user to both sites
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        siteService.setMembership(SITE1_T2, DAVE_T1, SiteModel.SITE_CONSUMER);
                        assertEquals(1, siteService.listSites(DAVE_T1).size());
                        
                        siteService.setMembership(SITE2_T2, DAVE_T1, SiteModel.SITE_CONSUMER);
                        assertEquals(2, siteService.listSites(DAVE_T1).size());
                        
                        return null;
                    }
                });
                return null;
            }
        }, FRED_T2, T2);
        
        return destAccount;
    }
    
    /**
     * This method tests the removal of a user from their own home network.
     * This is more involved than the removal of an external user as this use case must delete the user
     * from all accounts and delete the user and person objects.
     * 
     * @see #addExternalUserToAnotherNetworkAndRemoveThem()
     */
    @Test public void removeInternalUserFromTheirOwnHomeNetwork() throws Exception
    {
        // Let's start with the simplest case.
        // Create 2 test users in an account
        final Pair<String, Account> userAndAccount = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Pair<String, Account>>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Pair<String, Account> execute() throws Throwable
            {
                assertFalse(authenticationService.authenticationExists(DAVE_T1));
                assertFalse(personService.personExists(DAVE_T1));
                
                registrationService.createUser(DAVE_T1, "David", "Smith", "smithy");
                cloudContext.addUser(DAVE_T1);
                cloudContext.addAccountDomain(T1);
                
                Account accountT1 = accountService.getAccountByDomain(T1);
                return new Pair<String, Account>(DAVE_T1, accountT1);
            }
        });
        
        // The test above asserts that the user and account are setup properly at this stage so we'll not repeat that here.
        
        // It shouldn't be possible to remove a user with the normal remove method
        RetryingTransactionCallback<Void> removeExternalUserFail = new RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                registrationService.removeExternalUser(userAndAccount.getSecond().getId(), userAndAccount.getFirst());
                return null;
            }
        };
        try
        {
            transactionHelper.doInTransaction(removeExternalUserFail);
            fail("Expecting a CannotRemoveUserException");
        }
        catch (CannotRemoveUserException ignored)
        {
            // Expected
        }
        // Now delete them with the 'dangerous' method, which should work.
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                registrationService.deleteUser(userAndAccount.getFirst());
                
                return null;
            }
        });
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        assertFalse("Person exists when they shouldn't", personService.personExists(userAndAccount.getFirst()));
                        assertFalse(authorityService.getContainedAuthorities(AuthorityType.USER, RegistrationServiceImpl.GROUP_INTERNAL_USERS, true).contains(userAndAccount.getFirst()));
                        assertFalse(registrationService.isActivatedEmailAddress(userAndAccount.getFirst()));
                        return null;
                    }
                }, T1);
                
                return null;
            }
        });
    }
    
    @Test public void mobileSignUpAndActivateEmail() throws Exception
    {
        final String email = DAVE_T1;

        final Registration registration = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Registration>()
        {
        	@SuppressWarnings("synthetic-access")
        	@Override
        	public Registration execute() throws Throwable
        	{
        		Map<String, Serializable> analyticsData = Collections.emptyMap();
        		Registration registration = registrationService.registerEmail(email, "Bob", "Jones", "password", "test-" + this.getClass().getSimpleName(), null, null, analyticsData);
        		assertNotNull(registration);
        		assertEquals(email, registration.getEmailAddress());
        		assertNotNull(registration.getId());
        		assertNotNull(registration.getKey());
        		assertNotNull(registration.getRegistrationDate());
        		cloudContext.addRegistration(registration);
        		return registration;
        	}
        });

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
		{
        	@SuppressWarnings("synthetic-access")
        	@Override
        	public Void execute() throws Throwable
        	{
                boolean isPreRegistered = registrationService.isPreRegistered(registration.getId(), registration.getKey());
        		assertTrue(isPreRegistered);
        		return null;
        	}
		});
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
		{
        	@SuppressWarnings("synthetic-access")
        	@Override
        	public Void execute() throws Throwable
        	{
        		try
        		{
	        		// test incorrect password
	        		@SuppressWarnings("unused")
                    Registration registered = registrationService.activateRegistration(registration.getId(), registration.getKey(), null, null, "password1");
	        		fail("Did not catch invalid password");
        		}
        		catch(UnauthorisedException e)
        		{
        			// ok
        		}

        		// test activation with correct password
        		Registration registered = registrationService.activateRegistration(registration.getId(), registration.getKey(), null, null, "password");
        		cloudContext.removeRegistration(registration);
        		cloudContext.addAccountDomain(T1);
        		cloudContext.addUser(DAVE_T1);
        		assertEquals(DAVE_T1, registered.getEmailAddress());
        		return null;
        	}
		});
    }

    // test that a null first name and/or last name results in pre-registered == true
    @Test public void mobileSignUpTest() throws Exception
    {
        final String email = DAVE_T1;

        final Registration registration = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Registration>()
        {
        	@SuppressWarnings("synthetic-access")
        	@Override
        	public Registration execute() throws Throwable
        	{
        		Map<String, Serializable> analyticsData = Collections.emptyMap();
        		Registration registration = registrationService.registerEmail(email, null, null, "password", "test-" + this.getClass().getSimpleName(), null, null, analyticsData);
        		assertNotNull(registration);
        		assertEquals(email, registration.getEmailAddress());
        		assertNotNull(registration.getId());
        		assertNotNull(registration.getKey());
        		assertNotNull(registration.getRegistrationDate());
        		cloudContext.addRegistration(registration);
        		return registration;
        	}
        });

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
		{
        	@SuppressWarnings("synthetic-access")
        	@Override
        	public Void execute() throws Throwable
        	{
                boolean isPreRegistered = registrationService.isPreRegistered(registration.getId(), registration.getKey());
        		assertTrue(isPreRegistered);
        		return null;
        	}
		});
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
		{
        	@SuppressWarnings("synthetic-access")
        	@Override
        	public Void execute() throws Throwable
        	{
        		try
        		{
	        		// cancel the registration
	        		registrationService.cancelRegistration(registration.getId(), registration.getKey());
        		}
        		catch(UnauthorisedException e)
        		{
        			// ok
        		}

        		return null;
        	}
		});
    }
}
