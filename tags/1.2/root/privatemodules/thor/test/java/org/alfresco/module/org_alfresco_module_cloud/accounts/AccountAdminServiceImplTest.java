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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck.FailureReason;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Test code for {@link AccountAdminService}.
 * 
 * @author Neil Mc Erlean
 * @since Thor Phase 2 Sprint 2
 */
public class AccountAdminServiceImplTest
{
    // Services
    protected static ApplicationContext TEST_CONTEXT;
    
    protected static AccountAdminService       ACCOUNT_ADMIN_SERVICE;
    protected static AccountService            ACCOUNT_SERVICE;
    protected static AuthorityService          AUTHORITY_SERVICE;
    protected static CloudPersonService        CLOUD_PERSON_SERVICE;
    protected static DirectoryService          DIRECTORY_SERVICE;
    protected static EmailAddressService       EMAIL_ADDRESS_SERVICE;
    protected static NodeService               NODE_SERVICE;
    protected static RegistrationService       REGISTRATION_SERVICE;
    protected static RetryingTransactionHelper TRANSACTION_HELPER;
    
    private CloudTestContext cloudContext;
    private String           publicTenant;
    private String           privateTenant;
    private String           standardTenant;
    private Account          publicAccount;
    private Account          privateAccount;
    private Account          standardAccount;
    
    
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        TEST_CONTEXT = ApplicationContextHelper.getApplicationContext();
        
        ACCOUNT_ADMIN_SERVICE = (AccountAdminService) TEST_CONTEXT.getBean("accountAdminService");
        ACCOUNT_SERVICE       = (AccountService) TEST_CONTEXT.getBean("accountService");
        AUTHORITY_SERVICE     = (AuthorityService) TEST_CONTEXT.getBean("authorityService");
        CLOUD_PERSON_SERVICE  = (CloudPersonService) TEST_CONTEXT.getBean("cloudPersonService");
        DIRECTORY_SERVICE     = (DirectoryService) TEST_CONTEXT.getBean("directoryService");
        EMAIL_ADDRESS_SERVICE = (EmailAddressService) TEST_CONTEXT.getBean("emailAddressService");
        NODE_SERVICE          = (NodeService) TEST_CONTEXT.getBean("nodeService");
        REGISTRATION_SERVICE  = (RegistrationService)TEST_CONTEXT.getBean("registrationService");
        TRANSACTION_HELPER    = (RetryingTransactionHelper)TEST_CONTEXT.getBean("retryingTransactionHelper");
    }
    
    @Before public void init() throws Exception
    {
        cloudContext = new CloudTestContext(TEST_CONTEXT);
        publicTenant = cloudContext.createTenantName("public");
        privateTenant = cloudContext.createTenantName("private");
        standardTenant = cloudContext.createTenantName("standard");
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                publicAccount = createAccount(publicTenant, AccountType.PUBLIC_DOMAIN_ACCOUNT_TYPE, true);
                cloudContext.addAccount(publicAccount);
                
                privateAccount = createAccount(privateTenant, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                cloudContext.addAccount(privateAccount);
                
                standardAccount = createAccount(standardTenant, AccountType.STANDARD_NETWORK_ACCOUNT_TYPE, true);
                cloudContext.addAccount(standardAccount);
                
                return null;
            }
        });
    }
    
    /** Delete any temporary accounts created during the previous test */
    @After public void cleanup()
    {
        cloudContext.cleanup();
    }
    
    @Test(expected=AccountAdminServiceException.class) public void convertPublicAccountToPrivateShouldFail() throws Exception
    {
        // We don't need to have any users in the account. It should always fail.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                ACCOUNT_ADMIN_SERVICE.changeAccountType(publicAccount, AccountType.FREE_NETWORK_ACCOUNT_TYPE);
                return null;
            }
        });
    }
    
    @Test public void convertPrivateAccountToPublic() throws Exception
    {
        // We'll use these external users below.
        final String privateTenant2 = cloudContext.createTenantName("abcdefgh");
        // I use a lot of external users to ensure some paging behaviour is correct.
        final List<String> externalUsers = new ArrayList<String>();
        for (int i = 0; i < 20; i ++)
        {
            externalUsers.add(cloudContext.createUserName("external" + i, privateTenant2));
        }
        
        // Create some internal users in the private account
        //        and some external users.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                // Create two internal users
                String internalUser1 = cloudContext.createUserName("internal1", privateTenant);
                String internalUser2 = cloudContext.createUserName("internal2", privateTenant);
                
                REGISTRATION_SERVICE.createUser(internalUser1, "F", "L", "p");
                cloudContext.addUser(internalUser1);
                
                REGISTRATION_SERVICE.createUser(internalUser2, "F", "L", "p");
                cloudContext.addUser(internalUser2);
                
                
                // Create a second (private) account
                Account privateAccount2 = createAccount(privateTenant2, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                cloudContext.addAccount(privateAccount2);
                
                // And put some users in it.
                for (String externalUser : externalUsers)
                {
                    REGISTRATION_SERVICE.createUser(externalUser, "F", "L", "p");
                    cloudContext.addUser(externalUser);
                    
                    // And add those users to the original tenant.
                    REGISTRATION_SERVICE.addUser(privateAccount.getId(), externalUser);
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
                ACCOUNT_ADMIN_SERVICE.changeAccountType(privateAccount, AccountType.PUBLIC_DOMAIN_ACCOUNT_TYPE);
                return null;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                // Ensure that the expected side-effects have occurred.
                
                assertFalse("account was not disabled", ACCOUNT_SERVICE.getAccount(privateAccount.getId()).isEnabled());
                for (String externalUser : externalUsers)
                {
                    assertFalse("external user was not removed: " + externalUser,
                                DIRECTORY_SERVICE.getSecondaryAccounts(externalUser).contains(privateAccount.getId()));
                }
                
                assertEquals("Recently PUBLIC-ised domain should have been blacklisted as PUBLIC.",
                             FailureReason.PUBLIC, EMAIL_ADDRESS_SERVICE.getInvalidDomain(privateTenant).getFailureReason());
                
                return null;
            }
        });
    }

    private boolean isNetwotkAdmin(String email)
    {
        final boolean userIsAdmin = AUTHORITY_SERVICE.getContainedAuthorities(AuthorityType.USER, "GROUP_NETWORK_ADMINS", true).contains(email);
        if (userIsAdmin)
        {
            return true;
        }
        
        final NodeRef personNode = CLOUD_PERSON_SERVICE.getPerson(email, false);
        
        return NODE_SERVICE.hasAspect(personNode, CloudModel.ASPECT_NETWORK_ADMIN);
   }
    
    @Test public void upgradeFreePrivateAccountToPaidPrivate() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                ACCOUNT_ADMIN_SERVICE.changeAccountType(privateAccount, AccountType.STANDARD_NETWORK_ACCOUNT_TYPE);
                
                return null;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertEquals(AccountType.STANDARD_NETWORK_ACCOUNT_TYPE,
                            ACCOUNT_SERVICE.getAccount(privateAccount.getId()).getType().getId());
                
                return null;
            }
        });
    }
    
    /**
     * This method creates a new account with the specified parameters.
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
        
        return account;
    }
    
    @Test
    public void testConvertStandardAccountToFree() {
        
        // Create a network admin user and a normal user.
        final String networkAdmin = cloudContext.createUserName("networkAdmin", standardTenant);
        final String user = cloudContext.createUserName("internal", standardTenant);
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                REGISTRATION_SERVICE.createUser(networkAdmin, "F", "L", "p");
                REGISTRATION_SERVICE.promoteUserToNetworkAdmin(standardAccount.getId(), networkAdmin);
                cloudContext.addUser(networkAdmin);
                
                REGISTRATION_SERVICE.createUser(user, "F", "L", "p");
                cloudContext.addUser(user);
                
                return null;
            }
        });

        //Validate network admins are removed
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>() {
                    public Object doWork() {
                        assertTrue("NetworkAdmin not demoted.", isNetwotkAdmin(networkAdmin));
                        return null;
                    }
                }, standardTenant);
                return null;
            }
        });

        // Demote the network
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                ACCOUNT_ADMIN_SERVICE.changeAccountType(standardAccount, AccountType.PUBLIC_DOMAIN_ACCOUNT_TYPE);
                return null;
            }
        });

        //Validate network admins are removed
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>() {
                    public Object doWork() {
                        assertFalse("NetworkAdmin not demoted.", isNetwotkAdmin(networkAdmin));
                        return null;
                    }
                }, standardTenant);
                return null;
            }
        });
    }
}
