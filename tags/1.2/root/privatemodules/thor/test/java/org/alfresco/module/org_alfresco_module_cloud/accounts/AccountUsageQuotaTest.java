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
package org.alfresco.module.org_alfresco_module_cloud.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.usage.TenantContentUsageImpl;
import org.alfresco.module.org_alfresco_module_cloud.usage.TenantQuotaService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.Scheduler;
import org.springframework.context.ApplicationContext;

/**
 * Simple test for account usages and quotas
 * 
 * @author janv
 * @since Thor
 */
public class AccountUsageQuotaTest
{
    private static ApplicationContext testContext;
    
    // Services
    private static RegistrationService registrationService;
    private static MutableAuthenticationService authenticationService;
    private static AccountService accountService;
    private static SiteService siteService;
    private static RetryingTransactionHelper transactionHelper;
    private static NodeService nodeService;
    private static ContentService contentService;
    private static VersionService versionService;
    private static TenantContentUsageImpl contentUsageUpdate;
    
    private CloudTestContext cloudContext;
    private String T1;
    private String T2;
    
    private String DAVE_T1;
    private String FRED_T1;
    private String ALICE_T1;
    private String BOB_T1;
    
    private String CHARLIE_T2;
    private String EVE_T2;
    
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        testContext = ApplicationContextHelper.getApplicationContext();
        
        transactionHelper = (RetryingTransactionHelper)testContext.getBean("retryingTransactionHelper");
        registrationService = (RegistrationService)testContext.getBean("registrationService");
        accountService = (AccountService)testContext.getBean("accountService");
        authenticationService = (MutableAuthenticationService)testContext.getBean("authenticationService");
        siteService = (SiteService)testContext.getBean("siteService");
        nodeService = (NodeService)testContext.getBean("nodeService");
        contentService = (ContentService)testContext.getBean("contentService");
        versionService = (VersionService)testContext.getBean("versionService");
        
        // Let's shut down the scheduler so that we aren't competing with the scheduled version of the tenantContentUsageUpdateJob
        Scheduler scheduler = (Scheduler) testContext.getBean("schedulerFactory");
        scheduler.shutdown();
        
        contentUsageUpdate = (TenantContentUsageImpl)testContext.getBean("tenantContentUsageImpl");
    }
    
    @Before public void init() throws Exception
    {
        cloudContext = new CloudTestContext(testContext);
        
        T1 = cloudContext.createTenantName("acme");
        T2 = cloudContext.createTenantName("rush");
        
        DAVE_T1 = cloudContext.createUserName("dave", T1);
        FRED_T1 = cloudContext.createUserName("fred", T1);
        ALICE_T1 = cloudContext.createUserName("alice", T1);
        BOB_T1 = cloudContext.createUserName("bob", T1);
        
        CHARLIE_T2 = cloudContext.createUserName("charlie", T2);
        EVE_T2 = cloudContext.createUserName("eve", T2);
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
    }
    
    /** Delete any temporary accounts created during the previous test */
    @After public void cleanup()
    {
        cloudContext.cleanup();
    }
    
    private static final String TEXT_CONTENT_1 = "Hello World";
    private static final String TEXT_CONTENT_2 = "Goodbye World";
    
    @Test
    public void testFileUsage() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertNull(accountService.getAccountByDomain(T1));
                
                registrationService.createUser(FRED_T1, "Fred", "Smith", "freddy");
                cloudContext.addUser(FRED_T1);
                cloudContext.addAccountDomain(T1);
                
                assertNull(accountService.getAccountByDomain(T2));
                
                registrationService.createUser(CHARLIE_T2, "Charles", "C", "charlie");
                cloudContext.addUser(CHARLIE_T2);
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
                assertTrue(authenticationService.authenticationExists(FRED_T1));
                Account account = accountService.getAccountByDomain(T1);
                assertNotNull(account);
                
                assertEquals(TenantQuotaService.UNKNOWN, account.getUsageQuota().getFileUsage()); // not yet updated
                
                assertTrue(authenticationService.authenticationExists(CHARLIE_T2));
                account = accountService.getAccountByDomain(T2);
                assertNotNull(account);
                
                assertEquals(TenantQuotaService.UNKNOWN, account.getUsageQuota().getFileUsage()); // not yet updated
                
                return null;
            }
        });

        // retrieve initial content usages
        // NOTE: sample content is created in the background, so wait for sample content import to complete
        final int MAX_RETRIES = 15;
        final long RETRY_PAUSE = 2000;
        int retry = 0;
        Pair<Long, Long> startUsages = new Pair<Long, Long>(-1l, -1l);
        while (retry < MAX_RETRIES && (startUsages.getFirst() <= 0 || startUsages.getSecond() <= 0))
        {
            Thread.sleep(RETRY_PAUSE);

            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    contentUsageUpdate.execute(); // force file usage update - for any dirty stores
                    return null;
                }
            });
            
            startUsages = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Pair<Long, Long>>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Pair<Long, Long> execute() throws Throwable
                {
                    long startUsageT1 = accountService.getAccountByDomain(T1).getUsageQuota().getFileUsage();
                    long startUsageT2 = accountService.getAccountByDomain(T2).getUsageQuota().getFileUsage();
                    return new Pair<Long, Long>(startUsageT1, startUsageT2);
                }
            });
            
            retry++;
        }
        
        if (retry == MAX_RETRIES && (startUsages.getFirst() <= 0 || startUsages.getSecond() <= 0))
        {
            fail("Failed to retrieve starting content usage quota for home site sample content");
        }

        final long startUsageT1 = startUsages.getFirst();
        final long startUsageT2 = startUsages.getSecond();
        
        AuthenticationUtil.clearCurrentSecurityContext(); // clear fully authenticated user ("admin") - affects site creation (which uses this to setup perms)

        // note: for file usages - currently assume single tenant for txn
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        siteService.createSite("sitePreset", "FredPriv", "", "", SiteVisibility.PRIVATE);
                        return null;
                    }
                }, FRED_T1, T1);
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        siteService.createSite("sitePreset", "CharliePub", "", "", SiteVisibility.PUBLIC);
                        return null;
                    }
                }, CHARLIE_T2, T2);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                contentUsageUpdate.execute(); // force file usage update - for any dirty stores
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertEquals(startUsageT1, accountService.getAccountByDomain(T1).getUsageQuota().getFileUsage());
                assertEquals(startUsageT2, accountService.getAccountByDomain(T2).getUsageQuota().getFileUsage());
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        createVersionedContent(siteService.getSite("FredPriv").getNodeRef(), TEXT_CONTENT_1);
                        return null;
                    }
                }, FRED_T1, T1);
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        createVersionedContent(siteService.getSite("CharliePub").getNodeRef(), TEXT_CONTENT_2);
                        return null;
                    }
                }, CHARLIE_T2, T2);
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                contentUsageUpdate.execute(); // force file usage update - for any dirty stores
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertEquals(startUsageT1 + TEXT_CONTENT_1.length(), accountService.getAccountByDomain(T1).getUsageQuota().getFileUsage());
                assertEquals(startUsageT2 + TEXT_CONTENT_2.length(), accountService.getAccountByDomain(T2).getUsageQuota().getFileUsage());

                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                Account account = accountService.getAccountByDomain(T1);
                account.getUsageQuota().setFileQuota(startUsageT1 + 20);
                accountService.updateAccount(account);
                
                account = accountService.getAccountByDomain(T2);
                account.getUsageQuota().setFileQuota(startUsageT2 + 30);
                accountService.updateAccount(account);
                
                return null;
            }
        });
        
        // -ve test
        try
        {
            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                    {
                        public Object doWork() throws Exception
                        {
                            createVersionedContent(siteService.getSite("FredPriv").getNodeRef(), TEXT_CONTENT_1);
                            return null;
                        }
                    }, FRED_T1, T1);
                    
                    return null;
                }
            });
            
            fail("Unexpected - should not be able to exceed file space quota");
        }
        catch (AlfrescoRuntimeException are)
        {
            // expected - should have rolled back
        }
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        createVersionedContent(siteService.getSite("CharliePub").getNodeRef(), TEXT_CONTENT_2);
                        return null;
                    }
                }, CHARLIE_T2, T2);
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                contentUsageUpdate.execute(); // force file usage update - for any dirty stores
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertEquals(startUsageT1 + 1 * TEXT_CONTENT_1.length(), accountService.getAccountByDomain(T1).getUsageQuota().getFileUsage());
                assertEquals(startUsageT2 + 2 * TEXT_CONTENT_2.length(), accountService.getAccountByDomain(T2).getUsageQuota().getFileUsage());
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                Account account = accountService.getAccountByDomain(T1);
                account.getUsageQuota().setFileQuota(startUsageT1 + 30);
                accountService.updateAccount(account);
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        createVersionedContent(siteService.getSite("FredPriv").getNodeRef(), TEXT_CONTENT_1);
                        return null;
                    }
                }, FRED_T1, T1);
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                contentUsageUpdate.execute(); // force file usage update - for any dirty stores
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertEquals(startUsageT1 + 2 * TEXT_CONTENT_1.length(), accountService.getAccountByDomain(T1).getUsageQuota().getFileUsage());
                assertEquals(startUsageT2 + 2 * TEXT_CONTENT_2.length(), accountService.getAccountByDomain(T2).getUsageQuota().getFileUsage());
                
                return null;
            }
        });
    }
    
    private void createVersionedContent(NodeRef parentNodeRef, String textContent)
    {
        NodeRef nodeRef = nodeService.createNode(
                parentNodeRef, 
                ContentModel.ASSOC_CHILDREN, 
                QName.createQName("{test}MyVersionableNode"),
                ContentModel.TYPE_CONTENT,
                null).getChildRef();
        
        // Add the content to the node
        ContentWriter contentWriter = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
        contentWriter.putContent(textContent);
        
        versionService.createVersion(nodeRef, null);
    }
    
    @Test
    public void getSiteCountUsage() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertNull(accountService.getAccountByDomain(T1));
                
                registrationService.createUser(DAVE_T1, "David", "Smith", "smithy");
                registrationService.createUser(FRED_T1, "Fred", "Smith", "freddy");
                cloudContext.addUser(FRED_T1);
                cloudContext.addUser(DAVE_T1);
                cloudContext.addAccountDomain(T1);
                
                assertNull(accountService.getAccountByDomain(T2));
                
                registrationService.createUser(CHARLIE_T2, "Charles", "C", "charlie");
                cloudContext.addUser(CHARLIE_T2);
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
                assertTrue(authenticationService.authenticationExists(FRED_T1));
                Account account = accountService.getAccountByDomain(T1);
                assertNotNull(account);
                
                assertEquals(2L, account.getUsageQuota().getSiteCountUsage()); // 2 users => 2 private sites
                
                assertTrue(authenticationService.authenticationExists(CHARLIE_T2));
                account = accountService.getAccountByDomain(T2);
                assertNotNull(account);
                
                assertEquals(1L, account.getUsageQuota().getSiteCountUsage()); // 1 user => 1 private site
                
                return null;
            }
        });
        
        AuthenticationUtil.clearCurrentSecurityContext(); // clear fully authenticated user ("admin") - affects site creation (which uses this to setup perms)
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        siteService.createSite("sitePreset", "DavePub", "", "", SiteVisibility.PUBLIC);
                        siteService.createSite("sitePreset", "DavePriv", "", "", SiteVisibility.PRIVATE);
                        return null;
                    }
                }, DAVE_T1, T1);
                
                TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        siteService.createSite("sitePreset", "FredPub", "", "", SiteVisibility.PUBLIC);
                        return null;
                    }
                }, FRED_T1, T1);
                
                TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        siteService.createSite("sitePreset", "FredPub", "", "", SiteVisibility.PUBLIC);
                        return null;
                    }
                }, CHARLIE_T2, T2);
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertEquals(5L, accountService.getAccountByDomain(T1).getUsageQuota().getSiteCountUsage());
                assertEquals(2L, accountService.getAccountByDomain(T2).getUsageQuota().getSiteCountUsage());
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                Account account = accountService.getAccountByDomain(T1);
                account.getUsageQuota().setSiteCountQuota(5);
                accountService.updateAccount(account);
                return null;
            }
        });
        
        // -ve test
        try
        {
            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                    {
                        public Object doWork() throws Exception
                        {
                            siteService.createSite("sitePreset", "DavePub2", "", "", SiteVisibility.PUBLIC);
                            return null;
                        }
                    }, DAVE_T1, T1);
                    
                    return null;
                }
            });
            
            fail("Unexpected - should not be able to exceed site count quota");
        }
        catch (AlfrescoRuntimeException are)
        {
            // expected - should have rolled back
        }
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        siteService.deleteSite("DavePub");
                        siteService.createSite("sitePreset", "DavePub2", "", "", SiteVisibility.PUBLIC);
                        return null;
                    }
                }, DAVE_T1, T1);
                
                return null;
            }
        });
        
        // -ve test
        try
        {
            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                    {
                        public Object doWork() throws Exception
                        {
                            siteService.createSite("sitePreset", "DavePub3", "", "", SiteVisibility.PUBLIC);
                            return null;
                        }
                    }, DAVE_T1, T1);
                    
                    return null;
                }
            });
            
            fail("Unexpected - should not be able to exceed site count quota");
        }
        catch (AlfrescoRuntimeException are)
        {
            // expected - should have rolled back
        }
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                Account account = accountService.getAccountByDomain(T1);
                account.getUsageQuota().setSiteCountQuota(6);
                accountService.updateAccount(account);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        siteService.createSite("sitePreset", "DavePub3", "", "", SiteVisibility.PUBLIC);
                        return null;
                    }
                }, DAVE_T1, T1);
                
                return null;
            }
        });
        
        // -ve test
        try
        {
            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    TenantUtil.runAsUserTenant(new TenantRunAsWork<Object>()
                    {
                        public Object doWork() throws Exception
                        {
                            siteService.createSite("sitePreset", "DavePub4", "", "", SiteVisibility.PUBLIC);
                            return null;
                        }
                    }, DAVE_T1, T1);
                    
                    return null;
                }
            });
            
            fail("Unexpected - should not be able to exceed site count quota");
        }
        catch (AlfrescoRuntimeException are)
        {
            // expected - should have rolled back
        }
    }
    
    private final static int BC = 0; // N/A now that we have fixed usage adjustment - remove from test at some point
    
    @Test
    public void getPersonTotalCountUsage() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertNull(accountService.getAccountByDomain(T1));
                
                registrationService.createUser(DAVE_T1, "David", "Smith", "smithy");
                cloudContext.addUser(DAVE_T1);
                cloudContext.addAccountDomain(T1);
                
                assertNull(accountService.getAccountByDomain(T2));
                
                registrationService.createUser(CHARLIE_T2, "Charles", "C", "charlie");
                cloudContext.addUser(CHARLIE_T2);
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
                assertEquals(1+BC, accountService.getAccountByDomain(T1).getUsageQuota().getPersonCountUsage());
                assertEquals(1+BC, accountService.getAccountByDomain(T2).getUsageQuota().getPersonCountUsage());
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
                assertEquals(2+BC, accountService.getAccountByDomain(T1).getUsageQuota().getPersonCountUsage());
                assertEquals(1+BC, accountService.getAccountByDomain(T2).getUsageQuota().getPersonCountUsage());
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                Account account = accountService.getAccountByDomain(T1);
                account.getUsageQuota().setPersonCountQuota(2+BC);
                accountService.updateAccount(account);
                return null;
            }
        });
        
        // -ve test
        try
        {
            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    registrationService.createUser(ALICE_T1, "Alice", "Jones", "ally");
                    return null;
                }
            });
            
            fail("Unexpected - should not be able to exceed person count quota");
        }
        catch (AlfrescoRuntimeException are)
        {
            // expected - should have rolled back
        }
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                Account account = accountService.getAccountByDomain(T1);
                account.getUsageQuota().setPersonCountQuota(3+BC);
                accountService.updateAccount(account);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                registrationService.createUser(ALICE_T1, "Alice", "Jones", "ally");
                return null;
            }
        });
        
        // -ve test
        try
        {
            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    registrationService.createUser(BOB_T1, "Bob", "Edwards", "bobby");
                    return null;
                }
            });
            
            fail("Unexpected - should not be able to exceed person count quota");
        }
        catch (AlfrescoRuntimeException are)
        {
            // expected - should have rolled back
        }
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertEquals(3+BC, accountService.getAccountByDomain(T1).getUsageQuota().getPersonCountUsage());
                
                return null;
            }
        });
    }
    
    
    @Test
    public void getPersonIntOnlyCountUsage() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertNull(accountService.getAccountByDomain(T1));
                
                registrationService.createUser(DAVE_T1, "David", "Smith", "smithy");
                cloudContext.addUser(DAVE_T1);
                cloudContext.addAccountDomain(T1);
                
                assertNull(accountService.getAccountByDomain(T2));
                
                registrationService.createUser(CHARLIE_T2, "Charlies", "C", "charlie");
                cloudContext.addUser(CHARLIE_T2);
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
                assertEquals(1+BC, accountService.getAccountByDomain(T1).getUsageQuota().getPersonIntOnlyCountUsage());
                assertEquals(1+BC, accountService.getAccountByDomain(T2).getUsageQuota().getPersonIntOnlyCountUsage());
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
                assertEquals(2+BC, accountService.getAccountByDomain(T1).getUsageQuota().getPersonIntOnlyCountUsage());
                assertEquals(1+BC, accountService.getAccountByDomain(T2).getUsageQuota().getPersonIntOnlyCountUsage());
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                Account account = accountService.getAccountByDomain(T1);
                account.getUsageQuota().setPersonCountQuota(2+BC);
                accountService.updateAccount(account);
                return null;
            }
        });
        
        // -ve test
        try
        {
            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    registrationService.createUser(ALICE_T1, "Alice", "Jones", "ally");
                    return null;
                }
            });
            
            fail("Unexpected - should not be able to exceed person (internal only) count quota");
        }
        catch (AlfrescoRuntimeException are)
        {
            // expected - should have rolled back
        }
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                Account account = accountService.getAccountByDomain(T1);
                
                account.getUsageQuota().setPersonIntOnlyCountQuota(3+BC); // note: should this also auto-update the total quota (if larger) ?
                account.getUsageQuota().setPersonCountQuota(4+BC);
                
                accountService.updateAccount(account);
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                registrationService.createUser(ALICE_T1, "Alice", "Jones", "ally");
                return null;
            }
        });
        
        // -ve test
        try
        {
            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    registrationService.createUser(BOB_T1, "Bob", "Edwards", "bobby");
                    return null;
                }
            });
            
            fail("Unexpected - should not be able to exceed person (internal only) count quota");
        }
        catch (AlfrescoRuntimeException are)
        {
            // expected - should have rolled back
        }
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                assertEquals(3+BC, accountService.getAccountByDomain(T1).getUsageQuota().getPersonIntOnlyCountUsage());
                assertEquals(3+BC, accountService.getAccountByDomain(T1).getUsageQuota().getPersonCountUsage());
                
                return null;
            }
        });
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                registrationService.addUser(accountService.getAccountByDomain(T1).getId(), CHARLIE_T2);
                return null;
            }
        });
        
        // -ve test
        try
        {
            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    registrationService.addUser(accountService.getAccountByDomain(T1).getId(), EVE_T2);
                    return null;
                }
            });
            
            fail("Unexpected - should not be able to exceed person (total) count quota");
        }
        catch (AlfrescoRuntimeException are)
        {
            // expected - should have rolled back
        }
    }
}
