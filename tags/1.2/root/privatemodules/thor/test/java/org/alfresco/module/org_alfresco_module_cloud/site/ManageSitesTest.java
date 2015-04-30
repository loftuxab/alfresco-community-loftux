/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.util.GUID;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.TestWebScriptServer.DeleteRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PutRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Request;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * Tests for managing sites in the Cloud
 * 
 * @author Jamal Kaabi-Mofrad
 */
public class ManageSitesTest extends BaseWebScriptTest
{
    private static final String APPLICATION_JSON = "application/json";

    private static final String URL_SITES = "/api/sites";
    private static final String URL_MEMBERSHIPS = "/memberships";
    private static final String URL_SITES_ADMIN = "/api/admin-sites";

    private RetryingTransactionHelper transactionHelper;
    private AccountService accountService;
    private RegistrationService registrationService;
    private CloudTestContext cloudContext;
    private SiteService siteService;

    private String t1;
    private String t2;
    private String user1_t1;
    private String user2_t1;
    private String user3_t1;
    private String networkAdmin_t1;
    private String user1_t2;
    private String networkAdmin_t2;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        cloudContext = new CloudTestContext(this);

        transactionHelper = (RetryingTransactionHelper) cloudContext.getApplicationContext().getBean(
                    "retryingTransactionHelper");
        accountService = (AccountService) cloudContext.getApplicationContext().getBean("accountService");
        registrationService = (RegistrationService) cloudContext.getApplicationContext().getBean("RegistrationService");
        siteService = (SiteService) cloudContext.getApplicationContext().getBean("siteService");

        t1 = cloudContext.createTenantName("acme");
        t2 = cloudContext.createTenantName("ping");

        user1_t1 = cloudContext.createUserName("bob.marley", t1);
        user2_t1 = cloudContext.createUserName("tom.smith", t1);
        user3_t1 = cloudContext.createUserName("jane.norman", t1);
        networkAdmin_t1 = cloudContext.createUserName("john.doe", t1);

        user1_t2 = cloudContext.createUserName("bill.curly", t2);
        networkAdmin_t2 = cloudContext.createUserName("sara.martins", t2);

        // Set the current security context as the repo admin
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();

        // create test networks
        createAccount(t1, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createAccount(t2, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);

        // Create test users
        createUser(user1_t1, "Bob", "Marley", "password");
        createUser(user2_t1, "Tom", "Smith", "password");
        createUser(user3_t1, "Jane", "Norman", "password");
        createUser(user1_t2, "Bill", "Curly", "password");
        // create 'networkAdmin_t1' user and make him a network admin for tenant1
        createUserAsNetworkAdmin(networkAdmin_t1, "John", "Doe", "password");
        // create 'networkAdmin_t2' user and make him a network admin for tenant2
        createUserAsNetworkAdmin(networkAdmin_t2, "Sara", "Martins", "password");

        AuthenticationUtil.clearCurrentSecurityContext();

    }

    @Override
    public void tearDown() throws Exception
    {
        cloudContext.cleanup();
        AuthenticationUtil.clearCurrentSecurityContext();
    }

    public void testChangeSiteVisibilityAsSiteAdmin() throws Exception
    {
        final String user1_t1_site = "u1t1site" + GUID.generate();

        // Create a new site
        SiteInfo siteInfo = createSite("myPreset", user1_t1_site, "myTitle", "myDescription", SiteVisibility.PUBLIC,
                    user1_t1, t1);
        assertEquals(SiteVisibility.PUBLIC, siteInfo.getVisibility());

        // try to change the site visibility as user2_t1 (same tenant)
        AuthenticationUtil.setFullyAuthenticatedUser(user2_t1);
        final JSONObject changeVisibility = new JSONObject();
        changeVisibility.put("shortName", user1_t1_site);
        changeVisibility.put("visibility", "PRIVATE");

        // we should get AccessDeniedException
        sendRequestAsUserTenant(new PutRequest(URL_SITES + "/" + user1_t1_site, changeVisibility.toString(),
                    APPLICATION_JSON), 500, user2_t1, t1);

        // Make sure the site visibility hasn't been changed
        siteInfo = siteService.getSite(user1_t1_site);
        assertEquals("Site visibility should not have been changed.", SiteVisibility.PUBLIC, siteInfo.getVisibility());

        // set the current user as the network admin for tenant1 (site-admin)
        AuthenticationUtil.setFullyAuthenticatedUser(networkAdmin_t1);

        // Change the visibility to private
        Response response = sendRequestAsUserTenant(
                    new PutRequest(URL_SITES + "/" + user1_t1_site, changeVisibility.toString(), APPLICATION_JSON),
                    200, networkAdmin_t1, t1);

        assertNotNull(response);
        JSONObject jsonObj = new JSONObject(response.getContentAsString());
        assertEquals(SiteVisibility.PRIVATE.toString(), jsonObj.get("visibility"));

        // Change the visibility to moderated. We want to test if we can find
        // the private site before changing its visibility
        changeVisibility.put("visibility", "MODERATED");
        response = sendRequestAsUserTenant(new PutRequest(URL_SITES + "/" + user1_t1_site, changeVisibility.toString(),
                    APPLICATION_JSON), 200, networkAdmin_t1, t1);

        jsonObj = new JSONObject(response.getContentAsString());
        assertEquals(SiteVisibility.MODERATED.toString(), jsonObj.get("visibility"));

        // set the current user as the network admin for tenant2 (site-admin)
        // We want to check other network admins from different networks
        // are not able to modify the site data of the other networks.
        // in fact they should not be able to see the site at all.
        AuthenticationUtil.setFullyAuthenticatedUser(networkAdmin_t2);
        changeVisibility.put("visibility", "PUBLIC");
        sendRequestAsUserTenant(new PutRequest(URL_SITES + "/" + user1_t1_site, changeVisibility.toString(),
                    APPLICATION_JSON), 404, networkAdmin_t2, t2);
    }

    public void testChangeMembershipRoleAsSiteAdmin() throws Exception
    {
        final String user1_t1_site = "u1t1site" + GUID.generate();
        // Create a new site
        SiteInfo siteInfo = createSite("myPreset", user1_t1_site, "myTitle", "myDescription", SiteVisibility.PUBLIC,
                    user1_t1, t1);
        assertNotNull(siteInfo);

        // Build the JSON membership object
        JSONObject membership = new JSONObject();
        membership.put("role", SiteModel.SITE_CONSUMER);
        JSONObject person = new JSONObject();
        person.put("userName", user2_t1);
        membership.put("person", person);

        // Post the membership
        Response response = sendRequestAsUserTenant(new PostRequest(URL_SITES + "/" + user1_t1_site + URL_MEMBERSHIPS,
                    membership.toString(), APPLICATION_JSON), 200, user1_t1, t1);

        JSONObject jsonObj = new JSONObject(response.getContentAsString());
        // Check the result
        assertEquals(SiteModel.SITE_CONSUMER, jsonObj.get("role"));
        assertEquals(user2_t1, jsonObj.getJSONObject("authority").get("userName"));

        // user2_t1 tries to change his membership role
        AuthenticationUtil.setFullyAuthenticatedUser(user2_t1);
        membership.put("role", SiteModel.SITE_COLLABORATOR);
        sendRequestAsUserTenant(new PutRequest(URL_SITES + "/" + user1_t1_site + URL_MEMBERSHIPS,
                    membership.toString(), APPLICATION_JSON), 500, user2_t1, t1);
        // Make sure the membership role hasn't been changed
        String role = siteService.getMembersRole(user1_t1_site, user2_t1);
        assertEquals("User's role should not have been changed.", SiteModel.SITE_CONSUMER.toString(), role);

        // networkAdmin_t2 tries to change the membership role of the user2_t1
        // note: that networkAdmin_t2 is not tenant1's administrator
        AuthenticationUtil.setFullyAuthenticatedUser(networkAdmin_t2);
        membership.put("role", SiteModel.SITE_COLLABORATOR);
        sendRequestAsUserTenant(new PutRequest(URL_SITES + "/" + user1_t1_site + URL_MEMBERSHIPS,
                    membership.toString(), APPLICATION_JSON), 404, networkAdmin_t2, t2);

        // Make sure the membership role hasn't been changed
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
                        String role = siteService.getMembersRole(user1_t1_site, user2_t1);
                        assertEquals("User's role should not have been changed.", SiteModel.SITE_CONSUMER.toString(),
                                    role);
                        return null;
                    }
                });
                return null;
            }
        }, user2_t1, t1);

        // set the current user as the site-admin
        AuthenticationUtil.setFullyAuthenticatedUser(networkAdmin_t1);
        response = sendRequestAsUserTenant(
                    new PutRequest(URL_SITES + "/" + user1_t1_site + URL_MEMBERSHIPS, membership.toString(),
                                APPLICATION_JSON), 200, networkAdmin_t1, t1);
        jsonObj = new JSONObject(response.getContentAsString());
        // Check the result
        assertEquals(SiteModel.SITE_COLLABORATOR, jsonObj.get("role"));
        assertEquals(user2_t1, jsonObj.getJSONObject("authority").get("userName"));
    }

    public void testDeleteMembershipAsSiteAdmin() throws Exception
    {
        final String user1_t1_site = "u1t1site" + GUID.generate();
        // Create a new site
        SiteInfo siteInfo = createSite("myPreset", user1_t1_site, "myTitle", "myDescription", SiteVisibility.PUBLIC,
                    user1_t1, t1);
        assertNotNull(siteInfo);

        // Build the JSON membership object
        JSONObject membership = new JSONObject();
        membership.put("role", SiteModel.SITE_CONSUMER);
        JSONObject person = new JSONObject();
        person.put("userName", user2_t1);
        membership.put("person", person);

        // Post the membership
        Response response = sendRequestAsUserTenant(new PostRequest(URL_SITES + "/" + user1_t1_site + URL_MEMBERSHIPS,
                    membership.toString(), APPLICATION_JSON), 200, user1_t1, t1);
        JSONObject jsonObj = new JSONObject(response.getContentAsString());
        // Check the result
        assertEquals(SiteModel.SITE_CONSUMER, jsonObj.get("role"));
        assertEquals(user2_t1, jsonObj.getJSONObject("authority").get("userName"));

        // try to delete user2_t1 from the site
        AuthenticationUtil.setFullyAuthenticatedUser(user3_t1);
        sendRequestAsUserTenant(new DeleteRequest(URL_SITES + "/" + user1_t1_site + URL_MEMBERSHIPS + "/" + user2_t1),
                    500, user3_t1, t1);
        assertTrue(user3_t1 + " doesn’t have permission to delete users from the site",
                    siteService.isMember(user1_t1_site, user2_t1));

        // networkAdmin_t2 tries to delete user2_t1 from the sit
        // note: that networkAdmin_t2 is not tenant1's administrator
        AuthenticationUtil.setFullyAuthenticatedUser(networkAdmin_t2);
        sendRequestAsUserTenant(new DeleteRequest(URL_SITES + "/" + user1_t1_site + URL_MEMBERSHIPS + "/" + user2_t1),
                    404, networkAdmin_t2, t2);

        // Make sure the membership role hasn't been changed
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
                        assertTrue(networkAdmin_t2 + " doesn’t have permission to delete users from the site",
                                    siteService.isMember(user1_t1_site, user2_t1));

                        return null;
                    }
                });
                return null;
            }
        }, networkAdmin_t1, t1);

        // set the current user as the site-admin
        AuthenticationUtil.setFullyAuthenticatedUser(networkAdmin_t1);
        sendRequestAsUserTenant(new DeleteRequest(URL_SITES + "/" + user1_t1_site + URL_MEMBERSHIPS + "/" + user2_t1),
                    200, networkAdmin_t1, t1);
        assertFalse(siteService.isMember(user1_t1_site, user2_t1));
    }

    public void testDeleteSiteAsSiteAdmin() throws Exception
    {
        final String user1_t1_site = "u1t1site" + GUID.generate();
        // Create a new site
        SiteInfo siteInfo = createSite("myPreset", user1_t1_site, "myTitle", "myDescription", SiteVisibility.PUBLIC,
                    user1_t1, t1);
        assertNotNull(siteInfo);

        // try to delete the site
        AuthenticationUtil.setFullyAuthenticatedUser(user3_t1);
        // Delete the site
        sendRequestAsUserTenant(new DeleteRequest(URL_SITES + "/" + user1_t1_site), 500, user3_t1, t1);
        // Get the site
        Response response = sendRequestAsUserTenant(new GetRequest(URL_SITES + "/" + user1_t1_site), 200, user1_t1, t1);
        JSONObject jsonObj = new JSONObject(response.getContentAsString());
        assertEquals(user1_t1_site, jsonObj.get("shortName"));

        // try to delete the site
        // note: that networkAdmin_t2 is not tenant1's administrator
        AuthenticationUtil.setFullyAuthenticatedUser(networkAdmin_t2);
        // Delete the site
        sendRequestAsUserTenant(new DeleteRequest(URL_SITES + "/" + user1_t1_site), 404, networkAdmin_t2, t2);
        // Get the site
        response = sendRequestAsUserTenant(new GetRequest(URL_SITES + "/" + user1_t1_site), 200, user1_t1, t1);
        jsonObj = new JSONObject(response.getContentAsString());
        assertEquals(user1_t1_site, jsonObj.get("shortName"));

        // set the current user as the site-admin
        AuthenticationUtil.setFullyAuthenticatedUser(networkAdmin_t1);
        // Delete the site
        sendRequestAsUserTenant(new DeleteRequest(URL_SITES + "/" + user1_t1_site), 200, networkAdmin_t1, t1);
        sendRequestAsUserTenant(new GetRequest(URL_SITES + "/" + user1_t1_site), 404, networkAdmin_t1, t1);
    }

    public void testGetAllSitesAsSiteAdmin() throws Exception
    {
        String user1_t1PublicSiteName = GUID.generate();
        String user1_t1ModeratedSiteName = GUID.generate();
        String user1_t1PrivateSiteName = GUID.generate();

        String user2_t1PrivateSiteName = GUID.generate();

        // user1_t1 public site
        SiteInfo siteInfo = createSite("myPreset", user1_t1PublicSiteName, "myTitle", "myDescription",
                    SiteVisibility.PUBLIC, user1_t1, t1);
        assertNotNull(siteInfo);

        // user1_t1 moderated site
        siteInfo = createSite("myPreset", user1_t1ModeratedSiteName, "u1ModeratedSite", "myDescription",
                    SiteVisibility.MODERATED, user1_t1, t1);
        assertNotNull(siteInfo);

        // user1_t1 private site
        siteInfo = createSite("myPreset", user1_t1PrivateSiteName, "u1PrivateSite", "myDescription",
                    SiteVisibility.PRIVATE, user1_t1, t1);
        assertNotNull(siteInfo);

        AuthenticationUtil.setFullyAuthenticatedUser(user2_t1);
        // user2_t1 private site
        siteInfo = createSite("myPreset", user2_t1PrivateSiteName, "u2PrivateSite", "myDescription",
                    SiteVisibility.PRIVATE, user2_t1, t1);
        assertNotNull(siteInfo);

        AuthenticationUtil.setFullyAuthenticatedUser(user3_t1);
        // Note: we'll get 404 rather than 403
        sendRequestAsUserTenant(new GetRequest(URL_SITES_ADMIN), 404, user3_t1, t1);

        AuthenticationUtil.setFullyAuthenticatedUser(networkAdmin_t1);
        Response response = sendRequestAsUserTenant(new GetRequest(URL_SITES_ADMIN), 200, networkAdmin_t1, t1);
        JSONObject jsonObject = new JSONObject(response.getContentAsString());
        JSONArray jsonArray = jsonObject.getJSONObject("list").getJSONArray("entries");

        // SiteAdmin can see the public, moderated and private sites
        // tenant1 has 4 users, each has a default private site. In addition
        // user1_t1 created 3 sites and user2_t1 created 1 site, so in total we have 8 sites for tenant1
        assertEquals(8, jsonArray.length());
        assertTrue("Site admin can access all the sites (PUBLIC | MODERATED | PRIVATE).", canSeePrivateSites(jsonArray));

        // Check if tenant2's admin can see tenant1 sites
        AuthenticationUtil.setFullyAuthenticatedUser(networkAdmin_t2);
        response = sendRequestAsUserTenant(new GetRequest(URL_SITES_ADMIN), 200, networkAdmin_t2, t2);
        jsonObject = new JSONObject(response.getContentAsString());
        jsonArray = jsonObject.getJSONObject("list").getJSONArray("entries");
        // tenant2 has 2 users, each has a default private site
        assertEquals(2, jsonArray.length());
    }

    public void testGetAllSitesPagedAsSiteAdmin() throws Exception
    {
        // we use this as a name filter
        long siteNamePrefix = System.currentTimeMillis();
        String siteNameSuffix = GUID.generate();
        ;
        String user1_t1PublicSiteName = siteNamePrefix + siteNameSuffix.substring(siteNameSuffix.lastIndexOf('-'));

        SiteInfo siteInfo = createSite("myPreset", user1_t1PublicSiteName, "u1PublicSite", "myDescription",
                    SiteVisibility.PUBLIC, user1_t1, t1);
        assertNotNull(siteInfo);
        // Create 5 more sites
        for (int i = 1; i < 6; i++)
        {
            siteInfo = createSite("myPreset", GUID.generate(), "u1PublicSite" + i, "myDescription" + i,
                        SiteVisibility.PUBLIC, user1_t1, t1);
            assertNotNull(siteInfo);
        }

        AuthenticationUtil.setFullyAuthenticatedUser(networkAdmin_t1);

        Response response = sendRequestAsUserTenant(new GetRequest(URL_SITES_ADMIN + "?maxItems=5&skipCount=0"), 200,
                    networkAdmin_t1, t1);
        JSONObject jsonObject = new JSONObject(response.getContentAsString());
        JSONObject paging = jsonObject.getJSONObject("list").getJSONObject("pagination");
        assertEquals("The skipCount must be 0", 0, paging.getInt("skipCount"));
        assertEquals("The maxItems must be 5", 5, paging.getInt("maxItems"));
        // tenant1 has 4 users, each has a default private site. In addition
        // user1_t1 created 6 sites, so in total we have 10 sites for tenant1
        assertEquals("The totalItems must be 10", 10, paging.getInt("totalItems"));
        assertTrue(paging.getBoolean("hasMoreItems"));

        response = sendRequestAsUserTenant(new GetRequest(URL_SITES_ADMIN + "?nf=" + siteNamePrefix
                    + "&maxItems=5&skipCount=0"), 200, networkAdmin_t1, t1);
        jsonObject = new JSONObject(response.getContentAsString());
        paging = jsonObject.getJSONObject("list").getJSONObject("pagination");
        assertEquals("The count must be 1", 1, paging.getInt("count"));
        assertEquals("The maxItems must be 5", 5, paging.getInt("maxItems"));
        assertEquals("The totalItems must be 1", 1, paging.getInt("totalItems"));
        assertFalse(paging.getBoolean("hasMoreItems"));
    }

    private boolean canSeePrivateSites(JSONArray jsonArray) throws Exception
    {
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject obj = jsonArray.getJSONObject(i);
            String visibility = obj.getJSONObject("entry").getString("visibility");
            if (SiteVisibility.PRIVATE.equals(SiteVisibility.valueOf(visibility)))
            {
                return true;
            }
        }
        return false;
    }

    private Account createAccount(final String domain, final int type, final boolean enabled) throws Exception
    {
        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
        {
            @Override
            public Account execute() throws Throwable
            {
                Account account = accountService.createAccount(domain, type, enabled);
                cloudContext.addAccount(account);
                return account;
            }
        });
    }

    private Account createUserAsNetworkAdmin(final String email, final String firstName, final String lastName,
                final String password)
    {
        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
        {
            @Override
            public Account execute() throws Throwable
            {
                Account account = createUser(email, firstName, lastName, password);
                assertNotNull("Account was null.", account);
                registrationService.promoteUserToNetworkAdmin(account.getId(), email);

                return account;
            }
        });
    }

    private Account createUser(final String email, final String firstName, final String lastName, final String password)
    {
        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
        {
            @Override
            public Account execute() throws Throwable
            {
                Account account = registrationService.createUser(email, firstName, lastName, password);
                cloudContext.addUser(email);
                assertNotNull("Account was null.", account);

                return account;
            }
        });
    }

    private SiteInfo createSite(final String sitePreset, final String shortName, final String title,
                final String description, final SiteVisibility visibility, String runAsUser, String runAsTenant)
    {
        TenantRunAsWork<SiteInfo> createSiteWork = new TenantRunAsWork<SiteInfo>()
        {
            @Override
            public SiteInfo doWork() throws Exception
            {
                return transactionHelper
                            .doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<SiteInfo>()
                            {
                                @Override
                                public SiteInfo execute() throws Throwable
                                {
                                    return siteService
                                                .createSite(sitePreset, shortName, title, description, visibility);
                                }
                            });
            }
        };
        return TenantUtil.runAsUserTenant(createSiteWork, runAsUser, runAsTenant);
    }

    private Response sendRequestAsUserTenant(final Request request, final int expectedStatus, final String runAsUser,
                final String runAsTenant)
    {
        return TenantUtil.runAsUserTenant(new TenantRunAsWork<Response>()
        {
            @Override
            public Response doWork() throws Exception
            {
                Response response = sendRequest(request, expectedStatus);

                return response;
            }
        }, runAsUser, runAsTenant);
    }

}
