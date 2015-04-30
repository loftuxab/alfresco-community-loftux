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
package org.alfresco.module.org_alfresco_module_cloud.person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.filestore.FileContentReader;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;


public class PersonReplicationComponentTest
{
    private static ApplicationContext testContext;
    
    // Services
    private static RegistrationService registrationService;
    private static NodeService nodeService;
    private static PersonService personService;
    private static AccountService accountService;
    private static PersonReplicationComponent personReplicationComponent;
    private static ContentService contentService;
    private static PreferenceService preferenceService;
    private static RetryingTransactionHelper transactionHelper;
    
    private CloudTestContext cloudContext;
    
    private String T1;
    private String T2;
    private String T3;
    private String U1T1;
    
    private Account accountT1;
    private Account accountT2;
    private Account accountT3;
    
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        testContext = ApplicationContextHelper.getApplicationContext();
        
        transactionHelper = (RetryingTransactionHelper)testContext.getBean("retryingTransactionHelper");
        registrationService = (RegistrationService)testContext.getBean("registrationService");
        accountService = (AccountService)testContext.getBean("accountService");
        personService = (PersonService)testContext.getBean("personService");
        nodeService = (NodeService)testContext.getBean("nodeService");
        contentService = (ContentService)testContext.getBean("contentService");
        preferenceService = (PreferenceService)testContext.getBean("preferenceService");
        personReplicationComponent = (PersonReplicationComponent)testContext.getBean("personReplicationComponent");
    }
    
    @Before public void init() throws Exception
    {
        cloudContext = new CloudTestContext(testContext);
        T1 = cloudContext.createTenantName("acme");
        T2 = cloudContext.createTenantName("rush");
        T3 = cloudContext.createTenantName("iron");
        U1T1 = cloudContext.createUserName("dave", T1);
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                // create the source person (in T1)
                registrationService.createUser(U1T1, "David", "Smith", "smithy");
                
                // create account (for T2)
                accountT2 = accountService.createAccount(T2, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);

                // create account (for T3)
                accountT3 = accountService.createAccount(T3, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                
                accountT1 = accountService.getAccountByDomain(T1);
                
                // override quota for number of network admins (default is 0 for free account)
                accountT1.getUsageQuota().setPersonNetworkAdminCountQuota(5);
                accountService.updateAccount(accountT1);

                // add properties and avatar to person & make them a network admin.
                registrationService.promoteUserToNetworkAdmin(accountT1.getId(), U1T1);
                
                TenantUtil.runAsTenant(new TenantRunAsWork<Void>()
                {
                    public Void doWork() throws Exception
                    {
                        NodeRef person = personService.getPerson(U1T1);
                        
                        // set property
                        nodeService.setProperty(person, ContentModel.PROP_ORGANIZATION, "QA");
                        
                        // set content property
                        ContentWriter writer = contentService.getWriter(person, ContentModel.PROP_PERSONDESC, true);
                        writer.setMimetype(MimetypeMap.MIMETYPE_HTML);
                        writer.putContent("<b>Person Description</b>");
                        
                        // set avatar
                        ClassPathResource avatar = new ClassPathResource("org/alfresco/module/org_alfresco_module_cloud/person/avatar.txt");
                        FileContentReader avatarReader = new FileContentReader(avatar.getFile());
                        personReplicationComponent.writePersonAvatar(person, avatarReader);
                        
                        // set locale
                        Map<String, Serializable> preferences = new HashMap<String, Serializable>();
                        preferences.put("locale", "ja_JA");
                        preferenceService.setPreferences(U1T1, preferences); 
                        
                        return null;
                    }
                }, T1);
                
                cloudContext.addUser(U1T1);
                cloudContext.addAccountDomain(T1);
                cloudContext.addAccountDomain(T2);
                cloudContext.addAccountDomain(T3);

                return null;
            }
        });
    }

    /** Delete any temporary accounts created during the previous test */
    @After public void cleanup()
    {
        cloudContext.cleanup();
    }
    
    @Test
    public void copyPerson() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override public Void execute() throws Throwable
            {
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        NodeRef personToBeCopied = personService.getPerson(U1T1);
                        assertTrue("Expected cm:person to have cloud:networkAdmin aspect", nodeService.hasAspect(personToBeCopied, CloudModel.ASPECT_NETWORK_ADMIN));
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
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        personReplicationComponent.copyPerson(U1T1, T1, T2);
                        return null;
                    }
                }, T2);
                
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
                        NodeRef person = personService.getPerson(U1T1);
                        assertNotNull(person);
                        
                        // check property
                        assertEquals("QA", nodeService.getProperty(person, ContentModel.PROP_ORGANIZATION));
                        
                        // check content
                        ContentReader reader = contentService.getReader(person, ContentModel.PROP_PERSONDESC);
                        assertNotNull(reader);
                        assertTrue(reader.exists());
                        assertEquals("<b>Person Description</b>", reader.getContentString());

                        // check avatar
                        ContentReader avatar = personReplicationComponent.readPersonAvatar(person);
                        assertNotNull(avatar);
                        assertEquals("avatar", avatar.getContentString());
                        
                        // check preferences
                        Serializable locale = preferenceService.getPreference(U1T1, "locale");
                        assertNotNull(locale);
                        assertEquals("ja_JA", locale);
                        
                        assertFalse("Expected copied cm:person NOT to have cloud:networkAdmin aspect", nodeService.hasAspect(person, CloudModel.ASPECT_NETWORK_ADMIN));
                        return null;
                    }
                }, T2);
                
                return null;
            }
        });
    }

    @Test
    public void syncProperties() throws Exception
    {
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
                        registrationService.addUser(accountT2.getId(), U1T1);
                        registrationService.addUser(accountT3.getId(), U1T1);
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
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        NodeRef person = personService.getPerson(U1T1);
                        nodeService.setProperty(person, ContentModel.PROP_ORGANIZATION, "Quality Assurance");
                        return null;
                    }
                }, T1);
                
                return null;
            }
        });
        
        final TenantRunAsWork<Void> testDesc = new TenantRunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                NodeRef person = personService.getPerson(U1T1);
                assertNotNull(person);
                assertEquals("Quality Assurance", nodeService.getProperty(person, ContentModel.PROP_ORGANIZATION));
                return null;
            }
        };

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                return TenantUtil.runAsTenant(testDesc, T1);
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                return TenantUtil.runAsTenant(testDesc, T2);
            }
        });

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                return TenantUtil.runAsTenant(testDesc, T3);
            }
        });
    }

    @Test
    public void syncContent() throws Exception
    {
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
                        registrationService.addUser(accountT2.getId(), U1T1);
                        registrationService.addUser(accountT3.getId(), U1T1);
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
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        NodeRef person = personService.getPerson(U1T1);
                        ContentWriter writer = contentService.getWriter(person, ContentModel.PROP_PERSONDESC, true);
                        writer.setMimetype(MimetypeMap.MIMETYPE_HTML);
                        writer.putContent("<b>Updated Person Description</b>");
                        return null;
                    }
                }, T1);
                
                return null;
            }
        });
        
        final TenantRunAsWork<Void> testDesc = new TenantRunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                NodeRef person = personService.getPerson(U1T1);
                assertNotNull(person);
                ContentReader reader = contentService.getReader(person, ContentModel.PROP_PERSONDESC);
                assertNotNull(reader);
                assertTrue(reader.exists());
                assertEquals("<b>Updated Person Description</b>", reader.getContentString());
                return null;
            }
        };
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                return TenantUtil.runAsTenant(testDesc, T1);
            }
        });

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                return TenantUtil.runAsTenant(testDesc, T2);
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                return TenantUtil.runAsTenant(testDesc, T3);
            }
        });
    }
    
    @Test
    public void syncAvatar() throws Exception
    {
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
                        registrationService.addUser(accountT2.getId(), U1T1);
                        registrationService.addUser(accountT3.getId(), U1T1);
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
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        NodeRef person = personService.getPerson(U1T1);
                        ClassPathResource avatar = new ClassPathResource("org/alfresco/module/org_alfresco_module_cloud/person/updatedavatar.txt");
                        FileContentReader avatarReader = new FileContentReader(avatar.getFile());
                        personReplicationComponent.writePersonAvatar(person, avatarReader);
                        return null;
                    }
                }, T1);
                
                return null;
            }
        });
        
        final TenantRunAsWork<Void> testDesc = new TenantRunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                NodeRef person = personService.getPerson(U1T1);
                assertNotNull(person);
                ContentReader avatar = personReplicationComponent.readPersonAvatar(person);
                assertNotNull(avatar);
                assertEquals("updatedavatar", avatar.getContentString());
                return null;
            }
        };
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                return TenantUtil.runAsTenant(testDesc, T1);
            }
        });

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                return TenantUtil.runAsTenant(testDesc, T2);
            }
        });

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                return TenantUtil.runAsTenant(testDesc, T3);
            }
        });
    }

    @Test
    public void syncPreferences() throws Exception
    {
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
                        registrationService.addUser(accountT2.getId(), U1T1);
                        registrationService.addUser(accountT3.getId(), U1T1);
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
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        Map<String, Serializable> preferences = new HashMap<String, Serializable>();
                        preferences.put("locale", "en_US");
                        preferenceService.setPreferences(U1T1, preferences); 
                        return null;
                    }
                }, T1);
                
                return null;
            }
        });
        
        final TenantRunAsWork<Void> testDesc = new TenantRunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                assertEquals("en_US", preferenceService.getPreference(U1T1, "locale"));
                return null;
            }
        };

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                return TenantUtil.runAsTenant(testDesc, T1);
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                return TenantUtil.runAsTenant(testDesc, T2);
            }
        });

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                return TenantUtil.runAsTenant(testDesc, T3);
            }
        });
    }

}
