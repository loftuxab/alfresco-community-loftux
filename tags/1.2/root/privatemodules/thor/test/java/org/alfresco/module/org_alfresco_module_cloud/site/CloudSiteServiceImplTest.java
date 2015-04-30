/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.site;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.site.SiteMemberInfoImpl;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteMemberInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.test_category.SharedJVMTestsCategory;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.GUID;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.ApplicationContext;

/**
 * Specific tests for extension introduced in the {@link CloudSiteService}.
 * 
 * @author janv
 * @since Thor
 */
@Category(SharedJVMTestsCategory.class)
public class CloudSiteServiceImplTest
{
    private static ApplicationContext testContext;
    
    // Services
    private static AccountService            accountService;
    private static RegistrationService       registrationService;
    private static SiteService               siteService;
    private static RetryingTransactionHelper transactionHelper;
    
    private CloudTestContext cloudContext;
    private String T1;
    private String T2;
    private String USER1_T1;
    private String USER1_T2;
    private String USER2_T1;
    private String USER3_T1; // for testCloud563
    private String USER4_T1; // for testCloud563
    private String USER2_T2;
    private String USER3_T2; // for testCloud563
    private String SITE1_T1;
    private String SITE2_T1;
    private String SITE3_T1; // for testCloud563
    
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        testContext = ApplicationContextHelper.getApplicationContext();
        
        accountService = (AccountService)testContext.getBean("accountService");
        registrationService = (RegistrationService)testContext.getBean("registrationService");
        siteService = (SiteService)testContext.getBean("siteService");
        transactionHelper = (RetryingTransactionHelper)testContext.getBean("retryingTransactionHelper");
    }
    
    /**
     * Initialise various data required by the test.
     */
    @Before public void initTestData() throws Exception
    {
        cloudContext = new CloudTestContext(testContext);
        
        T1 = cloudContext.createTenantName("one");
        USER1_T1 = cloudContext.createUserName("larry", T1);
        USER2_T1 = cloudContext.createUserName("curly", T1);
        USER3_T1 = cloudContext.createUserName("bob", T1);
        USER4_T1 = cloudContext.createUserName("bill", T1);
        
        T2 = cloudContext.createTenantName("two");
        USER1_T2 = cloudContext.createUserName("imposter", T2);
        USER2_T2 = cloudContext.createUserName("moe", T2);
        USER3_T2 = cloudContext.createUserName("outsider", T2);
        
        SITE1_T1 = "testSite-" + GUID.generate();
        SITE2_T1 = "testSite-" + GUID.generate();
        SITE3_T1 = "testSite-" + GUID.generate();
        
        // Create the test users - we do this as system in the default tenant.
        TenantRunAsWork<Void> createUsersWork = new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @SuppressWarnings("synthetic-access")
                    @Override
                    public Void execute() throws Throwable
                    {
                        registrationService.createUser(USER1_T1, "f", "l", "password");
                        cloudContext.addUser(USER1_T1);
                        
                        registrationService.createUser(USER2_T1, "f", "l", "password");
                        cloudContext.addUser(USER2_T1);
                        
                        registrationService.createUser(USER3_T1, "f", "l", "password");
                        cloudContext.addUser(USER3_T1);
                        
                        registrationService.createUser(USER4_T1, "f", "l", "password");
                        cloudContext.addUser(USER4_T1);

                        cloudContext.addAccountDomain(T1);
                        
                        
                        registrationService.createUser(USER1_T2, "f", "l", "password");
                        registrationService.addUser(accountService.getAccountByDomain(T1).getId(), USER1_T2); // external user added to T1
                        cloudContext.addUser(USER1_T2);
                        
                        registrationService.createUser(USER2_T2, "f", "l", "password");
                        registrationService.addUser(accountService.getAccountByDomain(T1).getId(), USER2_T2); // external user added to T1
                        cloudContext.addUser(USER2_T2);
                        
                        registrationService.createUser(USER3_T2, "f", "l", "password");
                        registrationService.addUser(accountService.getAccountByDomain(T1).getId(), USER3_T2); // external user added to T1
                        cloudContext.addUser(USER3_T2);
                        
                        cloudContext.addAccountDomain(T2);
                        
                        return null;
                    }
                });
                return null;
            }
        };
        TenantUtil.runAsUserTenant(createUsersWork, AuthenticationUtil.getSystemUserName(), TenantService.DEFAULT_DOMAIN);
        
        // Create additional sites
        TenantRunAsWork<Void> createSiteWork = new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        siteService.createSite("site-preset", SITE1_T1, "site title", "site description", SiteVisibility.PUBLIC);
                        siteService.createSite("site-preset", SITE2_T1, "site title", "site description", SiteVisibility.MODERATED);
                        return null;
                    }
                });
                return null;
            }
        };
        TenantUtil.runAsUserTenant(createSiteWork, USER1_T1, T1);
        
        // Ensure each test starts with no authentication or tenant context
        AuthenticationUtil.clearCurrentSecurityContext();
    }
    
    /** Delete any temporary accounts created during the previous test */
    @After public void cleanup()
    {
        cloudContext.cleanup();
    }
    
    private String getUserHomeSiteShortName(String email)
    {
        // refer to RegistrationService.createUser
        return email.replace('@', '-').replace('.', '-');
    }

    /*
     * Test for https://issues.alfresco.com/jira/browse/CLOUD-563.
     * Test external user site member visibility.
     */
    @Test public void testCloud563()
    {
        // Create additional site
        TenantRunAsWork<Void> createSiteWork = new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        siteService.createSite("site-preset", SITE3_T1, "site title", "site description", SiteVisibility.PUBLIC);
                        return null;
                    }
                });
                return null;
            }
        };
        TenantUtil.runAsUserTenant(createSiteWork, USER3_T1, T1);

        // invite external user
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        siteService.setMembership(SITE3_T1, USER3_T2, SiteModel.SITE_CONSUMER);
                        
                        return null;
                    }
                });
                return null;
            }
        }, USER3_T1, T1);

        // external user in T1, has no site memberships in common with user USER4_T1
        checkSiteListMembers(USER4_T1, T1, SITE3_T1, SiteVisibility.PUBLIC, new String[]{USER3_T1}, true);
    }

    @Test public void listSitesAndMembers() throws Exception
    {
        // check site lists for 4 users (across both tenants)
        
        checkSiteList(USER1_T1, T1, new String[]{getUserHomeSiteShortName(USER1_T1),SITE1_T1,SITE2_T1});
        checkSiteList(USER2_T1, T1, new String[]{getUserHomeSiteShortName(USER2_T1),SITE1_T1,SITE2_T1});
        checkSiteList(USER1_T1, T2, new String[]{});
        checkSiteList(USER2_T1, T2, new String[]{});
        
        checkSiteList(USER1_T2, T2, new String[]{getUserHomeSiteShortName(USER1_T2)});
        checkSiteList(USER2_T2, T2, new String[]{getUserHomeSiteShortName(USER2_T2)});
        checkSiteList(USER1_T2, T1, new String[]{});
        checkSiteList(USER2_T2, T1, new String[]{});
        
        // check 4 private home sites
        checkSiteListMembers(USER1_T1, T1, getUserHomeSiteShortName(USER1_T1), SiteVisibility.PRIVATE, new String[]{USER1_T1}, false);
        checkSiteListMembers(USER2_T1, T1, getUserHomeSiteShortName(USER2_T1), SiteVisibility.PRIVATE, new String[]{USER2_T1}, false);
        checkSiteListMembers(USER1_T2, T2, getUserHomeSiteShortName(USER1_T2), SiteVisibility.PRIVATE, new String[]{USER1_T2}, false);
        checkSiteListMembers(USER2_T2, T2, getUserHomeSiteShortName(USER2_T2), SiteVisibility.PRIVATE, new String[]{USER2_T2}, false);
        
        // check public site on T1
        checkSiteListMembers(USER1_T1, T1, SITE1_T1, SiteVisibility.PUBLIC, new String[]{USER1_T1}, false);
        checkSiteListMembers(USER2_T1, T1, SITE1_T1, SiteVisibility.PUBLIC, new String[]{USER1_T1}, false);
        
        // check moderated site on T1
        checkSiteListMembers(USER1_T1, T1, SITE2_T1, SiteVisibility.MODERATED, new String[]{USER1_T1}, false);
        checkSiteListMembers(USER2_T1, T1, SITE2_T1, SiteVisibility.MODERATED, new String[]{USER1_T1}, false);
        
        // invite external users
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        siteService.setMembership(SITE1_T1, USER1_T2, SiteModel.SITE_CONSUMER);
                        siteService.setMembership(SITE2_T1, USER1_T2, SiteModel.SITE_CONSUMER);
                        
                        siteService.setMembership(SITE2_T1, USER2_T2, SiteModel.SITE_CONSUMER);
                        
                        return null;
                    }
                });
                return null;
            }
        }, USER1_T1, T1);
        
        checkSiteList(USER1_T2, T1, new String[]{SITE1_T1,SITE2_T1});
        checkSiteList(USER2_T2, T1, new String[]{SITE2_T1});
        
        // check public site on T1
        checkSiteListMembers(USER1_T1, T1, SITE1_T1, SiteVisibility.PUBLIC, new String[]{USER1_T1,USER1_T2}, false);
        checkSiteListMembers(USER2_T1, T1, SITE1_T1, SiteVisibility.PUBLIC, new String[]{USER1_T1}, false); // THOR-1173 (USER2_T1 is not a member of SITE1_T1)
        
        checkSiteListMembers(USER1_T2, T1, SITE1_T1, SiteVisibility.PUBLIC, new String[]{USER1_T1,USER1_T2}, false);
        
        // check moderated site on T1
        checkSiteListMembers(USER1_T1, T1, SITE2_T1, SiteVisibility.MODERATED, new String[]{USER1_T1,USER1_T2,USER2_T2}, false);
        checkSiteListMembers(USER2_T1, T1, SITE2_T1, SiteVisibility.MODERATED, new String[]{USER1_T1}, false); // THOR-1173 (USER2_T1 is not a member of SITE2_T1)
        
        checkSiteListMembers(USER1_T2, T1, SITE2_T1, SiteVisibility.MODERATED, new String[]{USER1_T1,USER1_T2,USER2_T2}, false);
        checkSiteListMembers(USER2_T2, T1, SITE2_T1, SiteVisibility.MODERATED, new String[]{USER1_T1,USER1_T2,USER2_T2}, false);
        
        // invite internal user
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        siteService.setMembership(SITE1_T1, USER2_T1, SiteModel.SITE_CONSUMER);
                        return null;
                    }
                });
                return null;
            }
        }, USER1_T1, T1);
        
        // check public site on T1
        checkSiteListMembers(USER1_T1, T1, SITE1_T1, SiteVisibility.PUBLIC, new String[]{USER1_T1,USER2_T1,USER1_T2}, false);
        checkSiteListMembers(USER2_T1, T1, SITE1_T1, SiteVisibility.PUBLIC, new String[]{USER1_T1,USER2_T1,USER1_T2}, false);
        
        checkSiteListMembers(USER1_T2, T1, SITE1_T1, SiteVisibility.PUBLIC, new String[]{USER1_T1,USER2_T1,USER1_T2}, false);
        
        // check moderated site on T1
        checkSiteListMembers(USER1_T1, T1, SITE2_T1, SiteVisibility.MODERATED, new String[]{USER1_T1,USER1_T2,USER2_T2}, false);
        checkSiteListMembers(USER2_T1, T1, SITE2_T1, SiteVisibility.MODERATED, new String[]{USER1_T1,USER1_T2}, false); // THOR-1173 (USER2_T1 is not a member of SITE2_T1)
        
        checkSiteListMembers(USER1_T2, T1, SITE2_T1, SiteVisibility.MODERATED, new String[]{USER1_T1,USER1_T2,USER2_T2}, false);
        checkSiteListMembers(USER2_T2, T1, SITE2_T1, SiteVisibility.MODERATED, new String[]{USER1_T1,USER1_T2,USER2_T2}, false);
    }
    
    private void checkSiteList(final String userName, String tenantDomain, final String[] expectedSiteNames)
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        List<SiteInfo> siteInfos = siteService.listSites(null, null, -1);
                        List<String> siteNames = new ArrayList<String>(siteInfos.size());
                        
                        for (SiteInfo siteInfo : siteInfos)
                        {
                            siteNames.add(siteInfo.getShortName());
                        }

                        for (String expectedSiteName : expectedSiteNames)
                        {
                            assertTrue(expectedSiteName+" ["+siteNames+"]", siteNames.contains(expectedSiteName));
                        }
                        
                        return null;
                    }
                });
                
                return null;
            }
        }, userName, tenantDomain);
    }
    
    private void checkSiteListMembers(String userName, String tenantDomain, final String siteShortName, final SiteVisibility expectedSiteVisibility, final String[] expectedUserNames, final boolean checkExact)
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        SiteInfo siteInfo = siteService.getSite(siteShortName);
                        assertNotNull(siteInfo);
                        assertEquals(siteInfo+"", expectedSiteVisibility, siteInfo.getVisibility());
                        
                        Map<String, String> memberRoles = siteService.listMembers(getUserHomeSiteShortName(siteShortName), null, null, -1);
                        // Test for CLOUD-1640
                        List<SiteMemberInfo> memberInfo = siteService.listMembersInfo(getUserHomeSiteShortName(siteShortName), null, null, -1, false);

                        for (String expectedUserName : expectedUserNames)
                        {
                            assertNotNull(expectedUserName+" ["+memberRoles+"]", memberRoles.get(expectedUserName));
                            memberRoles.remove(expectedUserName);
                            
                            int index = memberInfo.indexOf(new SiteMemberInfoImpl(expectedUserName, null, false));
                            assertTrue("Expected SiteMemberInfo object at index 0.", index >= 0);
                            SiteMemberInfo memInfo = memberInfo.get(index);
                            assertNotNull(expectedUserName + " [" + memberInfo + "]", memInfo);
                            memberInfo.remove(memInfo);
                        }

                        if(checkExact)
                        {
                        	assertEquals(0, memberRoles.size());
                            assertEquals(0, memberInfo.size());
                        }

                        return null;
                    }
                });
                
                return null;
            }
        }, userName, tenantDomain);
    }
}
