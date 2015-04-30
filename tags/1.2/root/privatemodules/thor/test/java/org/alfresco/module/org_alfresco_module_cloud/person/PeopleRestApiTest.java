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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.invitation.CloudInvitationService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.tenant.BaseTenantWebScriptTest;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.tenant.TenantContextHolder;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.test_category.SharedJVMTestsCategory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.experimental.categories.Category;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * This class tests getting webscripts that get people/profile data (as customised for the cloud) including:
 * 
 *    people.get      (=> /api/people and /api/people/<userName>)
 *    WF metadata.get (=> /webframework/content/metadata?user=<userName>)
 * 
 * @author Neil McErlean, janv
 * @since Alfresco Cloud Module (Thor)
 */
@Category(SharedJVMTestsCategory.class)
public class PeopleRestApiTest extends BaseTenantWebScriptTest
{
    private final static String GET_PEOPLE_URL = "/api/people";
    private final static String GET_PERSON_URL = "/api/people/";
    
    private final static String GET_PERSON_WF_METADATA_URL = "/webframework/content/metadata?user=";
    private final static String ESC_NAMESPACE_CONTENT = "{http://www.alfresco.org/model/content/1.0}";
    
    private RegistrationService registrationService;
    private AccountService accountService;
    private RetryingTransactionHelper transactionHelper;
    private CloudInvitationService cloudInvitationService;
    private SiteService siteService;
    
    private CloudTestContext cloudContext;
    
    private String T1, T2;
    private String USER1_T1, USER2_T1_NETADMIN, USER3_T2, USER4_T2, USER5_T2;
    
    private String SUPER_ADMIN_BASE = "admin"; // preset for cloud
    private String SUPER_ADMIN_T1; // super admin in context of T1
    private String SUPER_ADMIN_T2; // super admin in context of T2
    
    private String SITE1_T1 = "siteA";
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        cloudContext = new CloudTestContext(this);
        
        registrationService = (RegistrationService) cloudContext.getApplicationContext().getBean("RegistrationService");
        accountService = (AccountService) cloudContext.getApplicationContext().getBean("AccountService");
        transactionHelper = (RetryingTransactionHelper)cloudContext.getApplicationContext().getBean("retryingTransactionHelper");
        cloudInvitationService = (CloudInvitationService) cloudContext.getApplicationContext().getBean("CloudInvitationService");
        siteService = (SiteService) cloudContext.getApplicationContext().getBean("SiteService");
        
        // We must create some users via the Foundation API in order to retrieve them via the remote API.
        T1 = cloudContext.createTenantName("acme.com");
        T2 = cloudContext.createTenantName("another.co.uk");
        
        USER1_T1 = cloudContext.createUserName("u1", T1);
        USER2_T1_NETADMIN = cloudContext.createUserName("u2_na", T1);
        
        USER3_T2 = cloudContext.createUserName("u3", T2);
        USER4_T2 = cloudContext.createUserName("u4", T2);
        USER5_T2 = cloudContext.createUserName("u5", T2);
        
        SUPER_ADMIN_T1 = cloudContext.createUserName(SUPER_ADMIN_BASE, T1);
        SUPER_ADMIN_T2 = cloudContext.createUserName(SUPER_ADMIN_BASE, T2);
        
        // Create the test users - we do this as system in the default (ie. super) tenant
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
                        createUser(USER1_T1, T1);
                        createUser(USER2_T1_NETADMIN, T1);
                        
                        createUser(USER3_T2, T2);
                        createUser(USER4_T2, T2);
                        createUser(USER5_T2, T2);
                        
                        Account account = registrationService.getHomeAccount(USER1_T1);
                        long accountId = account.getId();
                        
                        // USER3_T2 added to T1 without explicit site membership - atypical ...
                        registrationService.addUser(accountId, USER3_T2); // note: USER3_T2 in this instance does not belong to any sites in T1
                        
                        // override quota for number of network admins (default is 0 for free account)
                        account.getUsageQuota().setPersonNetworkAdminCountQuota(5);
                        accountService.updateAccount(account);
                        
                        // Make USER2_T1_NETADMIN a network admin in T1
                        registrationService.promoteUserToNetworkAdmin(accountId, USER2_T1_NETADMIN);
                        
                        return null;
                    }
                });
                return null;
            }
        };
        TenantUtil.runAsUserTenant(createUsersWork, AuthenticationUtil.getSystemUserName(), TenantService.DEFAULT_DOMAIN);
        
        // Create the site to which the invitees will be invited - we do this as the inviter user in the inviter's tenant.
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
                        siteService.createSite("site-preset", SITE1_T1, "site title", "site description", SiteVisibility.PRIVATE);
                        
                        cloudInvitationService.addInviteeToSite(USER1_T1, SITE1_T1, USER4_T2, SiteModel.SITE_CONSUMER); // note: this will also addUser to T1
                        
                        return null;
                    }
                });
                return null;
            }
        };
        TenantUtil.runAsUserTenant(createSiteWork, USER1_T1, T1);
        
        
        // Ensure each test starts with no authentication or tenant context
        AuthenticationUtil.clearCurrentSecurityContext();
        TenantContextHolder.clearTenantDomain();
    }
    
    private Account createUser(final String username, final String domain)
    {
        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
        {
            public Account execute() throws Throwable
            {
                Account result = registrationService.createUser(username, "first", "last", "password");
                cloudContext.addUser(username);
                cloudContext.addAccountDomain(domain);
                return result;
            }
        }, false, true);
    }
    
    @Override
    public void tearDown() throws Exception
    {
        cloudContext.cleanup();
        super.tearDown();
    }
    
    public void testGetAllPeople() throws Exception
    {
        // Get all people in T1
        Map<String, JSONObject> peopleMap = checkAllPeopleVisibility(SUPER_ADMIN_T1, T1, "", 
                new String[]{SUPER_ADMIN_T1, USER1_T1, USER2_T1_NETADMIN, USER3_T2, USER4_T2});
        
        // Ensure they are correctly marked as internal/external.
        assertFalse("person 1 should have been internal within tenant T1", (Boolean) peopleMap.get(USER1_T1).get("isExternal"));
        assertFalse("person 2 should have been internal within tenant T1", (Boolean) peopleMap.get(USER2_T1_NETADMIN).get("isExternal"));
        assertTrue("person 3 should have been external within tenant T1", (Boolean) peopleMap.get(USER3_T2).get("isExternal"));
        assertTrue("person 4 should have been external within tenant T1", (Boolean) peopleMap.get(USER4_T2).get("isExternal"));
        
        // And ensure they are correctly reported as network admins
        assertFalse("person 1 should NOT have been an network admin", (Boolean) peopleMap.get(USER1_T1).get("isNetworkAdmin"));
        assertTrue("person 2 should have been an network admin", (Boolean) peopleMap.get(USER2_T1_NETADMIN).get("isNetworkAdmin"));
        assertFalse("person 3 should NOT have been an network admin", (Boolean) peopleMap.get(USER3_T2).get("isNetworkAdmin"));
        assertFalse("person 4 should NOT have been an network admin", (Boolean) peopleMap.get(USER4_T2).get("isNetworkAdmin"));
        
        // Get all people in T2
        peopleMap = checkAllPeopleVisibility(SUPER_ADMIN_T2, T2, "", 
                new String[]{SUPER_ADMIN_T2, USER3_T2, USER4_T2, USER5_T2});
        
        // Ensure they are correctly marked as internal/external.
        assertFalse("person 3 should have been internal within tenant T2", (Boolean) peopleMap.get(USER3_T2).get("isExternal"));
        assertFalse("person 4 should have been internal within tenant T2", (Boolean) peopleMap.get(USER4_T2).get("isExternal"));
        assertFalse("person 5 should have been internal within tenant T2", (Boolean) peopleMap.get(USER5_T2).get("isExternal"));
        
        // And ensure they are correctly reported as network admins
        assertFalse("person 3 should NOT have been an network admin", (Boolean) peopleMap.get(USER3_T2).get("isNetworkAdmin"));
        assertFalse("person 4 should NOT have been an network admin", (Boolean) peopleMap.get(USER3_T2).get("isNetworkAdmin"));
        assertFalse("person 5 should NOT have been an network admin", (Boolean) peopleMap.get(USER3_T2).get("isNetworkAdmin"));
    }
    
    /**
     * This method takes a JSONArray of people and turns them into a Map, keyed by userName
     */
    private Map<String, JSONObject> getMapOfPeople(JSONArray peopleArray)
    {
        Map<String, JSONObject> peopleMap = new HashMap<String, JSONObject>();
        for (int i = 0; i < peopleArray.size(); i++)
        {
            JSONObject nextPersonObj = (JSONObject) peopleArray.get(i);
            String userName = (String) nextPersonObj.get("userName");
            assertNotNull(userName);
            peopleMap.put(userName, nextPersonObj);
        }
        return peopleMap;
    }
    
    public void testGetFilteredPeople_InternalExternal() throws Exception
    {
        // Check internal people
        checkAllPeopleVisibility(SUPER_ADMIN_T1, T1,  "?internal=true", new String[]{SUPER_ADMIN_T1, USER1_T1, USER2_T1_NETADMIN});
        checkAllPeopleVisibility(SUPER_ADMIN_T2, T2,  "?internal=true", new String[]{SUPER_ADMIN_T2, USER3_T2, USER4_T2, USER5_T2});
        
        // Check external people
        checkAllPeopleVisibility(SUPER_ADMIN_T1, T1,  "?internal=false", new String[]{USER3_T2, USER4_T2});
        checkAllPeopleVisibility(SUPER_ADMIN_T2, T2,  "?internal=false", new String[]{});
    }
    
    public void testGetFilteredPeople_NetworkAdmins() throws Exception
    {
        // Check network-admin people
        checkAllPeopleVisibility(SUPER_ADMIN_T1, T1, "?networkAdmin=true", new String[]{USER2_T1_NETADMIN});
        checkAllPeopleVisibility(SUPER_ADMIN_T2, T2, "?networkAdmin=true", new String[]{});
        
        // Check non-network-admin people
        checkAllPeopleVisibility(SUPER_ADMIN_T1, T1, "?networkAdmin=false", new String[]{SUPER_ADMIN_T1, USER1_T1, USER3_T2, USER4_T2});
        checkAllPeopleVisibility(SUPER_ADMIN_T2, T2, "?networkAdmin=false", new String[]{SUPER_ADMIN_T2, USER3_T2, USER4_T2, USER5_T2});
    }
    
    // via /api/people (=> /api/people or /api/people?filter=* or /api/people?...)
    public void testPeopleListing() throws Exception
    {
        // Get all people visible to the given user within the context of the selected network
        //
        // - either people who are in the same home network (and invited to selected network - even if no common sites) 
        // - or if not in the same home network then are invited to one or more common sites (within the selected network)
        
        // within context of T1
        // note: USER3_T2 invited to SITE1_T1, USER4_T2 added to tenant (but does not belong to any sites - atypical ?), USER5_T2 not invited
        checkAllPeopleVisibility(SUPER_ADMIN_T1, T1,  "", new String[]{SUPER_ADMIN_T1, USER1_T1, USER2_T1_NETADMIN, USER3_T2, USER4_T2});
        checkAllPeopleVisibility(USER1_T1, T1, "", new String[]{USER1_T1, USER2_T1_NETADMIN, USER4_T2}); // can see USER4_T2 since in SITE1_T1
        checkAllPeopleVisibility(USER2_T1_NETADMIN, T1, "", new String[]{USER1_T1, USER2_T1_NETADMIN});
        
        checkAllPeopleVisibility(USER3_T2, T1, "", new String[]{USER3_T2, USER4_T2}); // not: USER1_T1, USER2_T1_NETADMIN, USER5_T2
        checkAllPeopleVisibility(USER4_T2, T1, "", new String[]{USER1_T1, USER3_T2, USER4_T2}); // not: USER2_T1_NETADMIN, USER5_T2
        
        // within context of T2
        checkAllPeopleVisibility(SUPER_ADMIN_T2, T2,  "", new String[]{SUPER_ADMIN_T2, USER3_T2, USER4_T2, USER5_T2});
        checkAllPeopleVisibility(USER3_T2, T2, "", new String[]{USER3_T2, USER4_T2, USER5_T2});
        checkAllPeopleVisibility(USER4_T2, T2, "", new String[]{USER3_T2, USER4_T2, USER5_T2});
        checkAllPeopleVisibility(USER5_T2, T2, "", new String[]{USER3_T2, USER4_T2, USER5_T2});
    }
    
    // via /api/people/<userName> and also /webframework/content/metadata?user=<userName>
    public void testPersonListing() throws Exception
    {
        // Check each sample person is visible / not visible to the given user within the context of the selected network 
        
        // within context of T1
        checkPersonVisibility(SUPER_ADMIN_T1, T1, new String[]{SUPER_ADMIN_T1, USER1_T1, USER2_T1_NETADMIN, USER3_T2, USER4_T2},  new String[]{SUPER_ADMIN_T2, USER5_T2});
        checkPersonVisibility(USER1_T1, T1, new String[]{USER1_T1, USER2_T1_NETADMIN, USER4_T2}, new String[]{SUPER_ADMIN_T1, USER3_T2, USER5_T2});
        checkPersonVisibility(USER2_T1_NETADMIN, T1, new String[]{USER1_T1, USER2_T1_NETADMIN}, new String[]{SUPER_ADMIN_T1, USER3_T2, USER4_T2, USER5_T2});
        
        checkPersonVisibility(USER3_T2, T1, new String[]{USER3_T2, USER4_T2}, new String[]{SUPER_ADMIN_T1, USER1_T1, USER2_T1_NETADMIN, USER5_T2});
        checkPersonVisibility(USER4_T2, T1, new String[]{USER1_T1, USER3_T2, USER4_T2}, new String[]{SUPER_ADMIN_T1, USER2_T1_NETADMIN, USER5_T2});
        
        // within context of T2
        checkPersonVisibility(SUPER_ADMIN_T2, T2, new String[]{SUPER_ADMIN_T2, USER3_T2, USER4_T2, USER5_T2}, new String[]{SUPER_ADMIN_T1, USER1_T1, USER2_T1_NETADMIN});
        checkPersonVisibility(USER3_T2, T2, new String[]{USER3_T2, USER4_T2, USER5_T2}, new String[]{SUPER_ADMIN_T2, USER1_T1, USER2_T1_NETADMIN});
        checkPersonVisibility(USER4_T2, T2, new String[]{USER3_T2, USER4_T2, USER5_T2}, new String[]{SUPER_ADMIN_T2, USER1_T1, USER2_T1_NETADMIN});
        checkPersonVisibility(USER5_T2, T2, new String[]{USER3_T2, USER4_T2, USER5_T2}, new String[]{SUPER_ADMIN_T2, USER1_T1, USER2_T1_NETADMIN});
    }
    
    public void testPeopleProfileVisibility() throws Exception
    {
        checkProfileVisibility(USER1_T1, T1, new String[]{USER4_T2}, new String[]{USER1_T1, USER2_T1_NETADMIN});
        checkProfileVisibility(USER4_T2, T1, new String[]{USER1_T1}, new String[]{USER3_T2, USER4_T2});
    }
    
    private void checkPersonVisibility(final String userName, final String tenantDomain, final String[] expectedUserNames, final String[] notExpectedUserNames) throws IOException
    {
        AuthenticationUtil.setFullyAuthenticatedUser(userName);
        
        TenantUtil.runAsTenant(new TenantRunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                //
                // use /api/people/<userName>
                //
                
                for (String expectedUserName : expectedUserNames)
                {
                    Response rsp = sendRequest(new GetRequest(GET_PERSON_URL+expectedUserName), 200);
                    String contentAsString = rsp.getContentAsString();
                    
                    JSONObject person = (JSONObject)JSONValue.parse(contentAsString);
                    
                    String retUserName = (String)person.get("userName");
                    assertEquals(expectedUserName, retUserName);
                }
                
                for (String notExpectedUserName : notExpectedUserNames)
                {
                    sendRequest(new GetRequest(GET_PERSON_URL+notExpectedUserName), 404); // ignore response
                }
                
                //
                // use /webframework/content/metadata?user=<userName>
                //
                
                for (String expectedUserName : expectedUserNames)
                {
                    Response rsp = sendRequest(new GetRequest(GET_PERSON_WF_METADATA_URL+expectedUserName), 200);
                    String contentAsString = rsp.getContentAsString();
                    
                    JSONObject obj = (JSONObject)JSONValue.parse(contentAsString);
                    JSONObject personProps = (JSONObject)((JSONObject)obj.get("data")).get("properties");
                    
                    String retUserName = (String)personProps.get(ESC_NAMESPACE_CONTENT+"userName");
                    assertEquals(expectedUserName, retUserName);
                }
                
                for (String notExpectedUserName : notExpectedUserNames)
                {
                    sendRequest(new GetRequest(GET_PERSON_WF_METADATA_URL+notExpectedUserName), 500); // ignore response, currently throws: java.lang.IllegalArgumentException - Person is a mandatory parameter
                }
                
                return null;
            }
        }, tenantDomain);
    }
    
    private Map<String, JSONObject> checkAllPeopleVisibility(final String userName, final String tenantDomain, final String urlSuffixIn, final String[] allExpectedUserNames) throws IOException
    {
        AuthenticationUtil.setFullyAuthenticatedUser(userName);
        
        return TenantUtil.runAsTenant(new TenantRunAsWork<Map<String, JSONObject>>()
        {
            public Map<String, JSONObject> doWork() throws Exception
            {
                String urlSuffix = urlSuffixIn;
                
                // belts-and-braces: explicitly add filter=* (even though it is implied for "all")
                if ((urlSuffix == null) || (urlSuffix.isEmpty()))
                {
                    urlSuffix = "?filter=*";
                }
                else if (! urlSuffix.contains("filter="))
                {
                    urlSuffix = urlSuffix + "&filter=*";
                }
                
                Response rsp = sendRequest(new GetRequest(GET_PEOPLE_URL+urlSuffix), 200);
                String contentAsString = rsp.getContentAsString();
                
                JSONObject jsonObj = (JSONObject) JSONValue.parse(contentAsString);
                JSONArray peopleArray = (JSONArray) jsonObj.get("people");
                
                Map<String, JSONObject> peopleMap = getMapOfPeople(peopleArray);
                
                int expectedPeopleCount = allExpectedUserNames.length;
                assertEquals("Wrong number of people ["+peopleMap.keySet()+"]", expectedPeopleCount, peopleArray.size());
                
                for (String expectedUserName : allExpectedUserNames)
                {
                    JSONObject person = peopleMap.get(expectedUserName);
                    assertNotNull("Expected "+expectedUserName, person);
                }
                
                return peopleMap;
            }
        }, tenantDomain);
    }
    
    private void checkProfileVisibility(final String userName, final String tenantDomain, final String[] userNamesLimited, final String[] userNamesFull) throws IOException
    {
        AuthenticationUtil.setFullyAuthenticatedUser(userName);
        
        TenantUtil.runAsTenant(new TenantRunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                List<String> allExpectedUserNames = new ArrayList<String>(userNamesLimited.length + userNamesFull.length);
                allExpectedUserNames.addAll(Arrays.asList(userNamesLimited));
                allExpectedUserNames.addAll(Arrays.asList(userNamesFull));
                
                //
                // via /api/people (?filter=*)
                //
                Map<String, JSONObject> peopleMap = checkAllPeopleVisibility(userName, tenantDomain, "", allExpectedUserNames.toArray(new String[0]));
                
                for (String expectedUserName  : userNamesLimited)
                {
                    checkProfileProps(peopleMap.get(expectedUserName), true, false);
                }
                
                for (String expectedUserName  : userNamesFull)
                {
                    checkProfileProps(peopleMap.get(expectedUserName), false, false);
                }
                
                //
                // via /api/people/<userName>
                //
                for (String expectedUserName  : userNamesLimited)
                {
                    Response rsp = sendRequest(new GetRequest(GET_PERSON_URL+expectedUserName), 200);
                    String contentAsString = rsp.getContentAsString();
                    JSONObject obj = (JSONObject)JSONValue.parse(contentAsString);
                    checkProfileProps(obj, true, false);
                }
                
                for (String expectedUserName  : userNamesFull)
                {
                    Response rsp = sendRequest(new GetRequest(GET_PERSON_URL+expectedUserName), 200);
                    String contentAsString = rsp.getContentAsString();
                    JSONObject obj = (JSONObject)JSONValue.parse(contentAsString);
                    checkProfileProps(obj, false, false);
                }
                
                //
                // via /webframework/content/metadata?user=<userName>
                //
                for (String expectedUserName  : userNamesLimited)
                {
                    Response rsp = sendRequest(new GetRequest(GET_PERSON_WF_METADATA_URL+expectedUserName), 200);
                    String contentAsString = rsp.getContentAsString();
                    JSONObject obj = (JSONObject)JSONValue.parse(contentAsString);
                    checkProfileProps(obj, true, true);
                }
                
                for (String expectedUserName  : userNamesFull)
                {
                    Response rsp = sendRequest(new GetRequest(GET_PERSON_WF_METADATA_URL+expectedUserName), 200);
                    String contentAsString = rsp.getContentAsString();
                    JSONObject obj = (JSONObject)JSONValue.parse(contentAsString);
                    checkProfileProps(obj, false, true);
                }
                
                return null;
            }
        }, tenantDomain);
    }
    
    // note:
    // - exact prop visibility subject-to-change (=> which might require corresponding update to this test)
    // - webframework/content/metadata?user=<userName> currently returns all person props for full profile
    private void checkProfileProps(JSONObject obj, boolean isLimited, boolean wfMetadata)
    {
        JSONObject personProps = obj;
        
        String NS = ESC_NAMESPACE_CONTENT;
        if (wfMetadata)
        {
            personProps = (JSONObject)((JSONObject)obj.get("data")).get("properties");
        }
        
        if (isLimited)
        {
            // limited profile - check for example props that should NOT be visible (assuming that they have been set)
            if (!wfMetadata)
            {
                assertNull(personProps.get("email"));
                assertNull(personProps.get("sizeCurrent"));
                assertNull(personProps.get("enabled"));
                assertNull(personProps.get("emailFeedDisabled"));
                assertNull(personProps.get("isExternal"));
                assertNull(personProps.get("isNetworkAdmin"));
            }
            else
            {
                assertNull(personProps.get(NS+"email"));
                assertNull(personProps.get(NS+"sizeCurrent"));
                
                assertNull(obj.get("isExternal"));
                assertNull(obj.get("isNetworkAdmin"));
            }
        }
        else
        {
            // full profile - check for example props that should be there (ie. usually set)
            if (!wfMetadata)
            {
                assertNotNull(personProps.get("email"));
                assertNotNull(personProps.get("userName"));
                assertNotNull(personProps.get("sizeCurrent"));
                assertNotNull(personProps.get("enabled"));
                assertNotNull(personProps.get("emailFeedDisabled"));
                assertNotNull(personProps.get("isExternal"));
                assertNotNull(personProps.get("isNetworkAdmin"));
            }
            else
            {
                assertNotNull(personProps.get(NS+"email"));
                assertNotNull(personProps.get(NS+"userName"));
                assertNotNull(personProps.get(NS+"sizeCurrent"));
                //assertNotNull(personProps.get(NS+"emailFeedDisabled")); // not returned if not set
                
                assertNotNull(((JSONObject)obj.get("data")).get("isExternal"));
                assertNotNull(((JSONObject)obj.get("data")).get("isNetworkAdmin"));
            }
        }
    }
}
