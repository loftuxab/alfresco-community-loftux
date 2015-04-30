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

package org.alfresco.module.org_alfresco_module_cloud.facet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.repo.search.impl.solr.facet.SolrFacetService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.util.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.webscripts.TestWebScriptServer.*;

/**
 * This class tests the ResT API of the {@link SolrFacetService}.
 *
 * @author Jamal Kaabi-Mofrad
 * @since 5.0
 */
public class CloudFacetRestApiTest extends BaseWebScriptTest
{

    private static final String FACETS = "facets";

    private final static String GET_FACETS_URL       = "/api/facet/facet-config";
    private final static String PUT_FACET_URL_FORMAT = "/api/facet/facet-config/{0}?relativePos={1}";
    private final static String POST_FACETS_URL      = GET_FACETS_URL;
    private final static String PUT_FACETS_URL       = GET_FACETS_URL;
    private static final String APPLICATION_JSON = "application/json";

    private RetryingTransactionHelper transactionHelper;
    private AccountService accountService;
    private RegistrationService registrationService;
    private CloudTestContext cloudContext;

    private String t1; // tenant 1
    private String t2; // tenant 2
    private String user1_nonAdmin_t1;
    private String networkAdmin_t1;
    private String networkAdmin_t2;

    private Map<String, List<String>> filtersMap = new HashMap<>();

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        cloudContext = new CloudTestContext(this);
        transactionHelper = (RetryingTransactionHelper) cloudContext.getApplicationContext().getBean("retryingTransactionHelper");
        accountService = (AccountService) cloudContext.getApplicationContext().getBean("accountService");
        registrationService = (RegistrationService) cloudContext.getApplicationContext().getBean("RegistrationService");

        t1 = cloudContext.createTenantName("acme");
        t2 = cloudContext.createTenantName("ping");

        user1_nonAdmin_t1 = cloudContext.createUserName("bob.marley", t1);
        networkAdmin_t1 = cloudContext.createUserName("john.doe", t1);

        networkAdmin_t2 = cloudContext.createUserName("sara.martins", t2);

        // Set the current security context as the repo admin
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();

        // create test networks
        createAccount(t1, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createAccount(t2, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);

        // Create test user
        createUser(user1_nonAdmin_t1, "Bob", "Marley", "password");
        // create 'networkAdmin_t1' user and make him a network admin for tenant1
        createUserAsNetworkAdmin(networkAdmin_t1, "John", "Doe", "password");
        // create 'networkAdmin_t2' user and make him a network admin for tenant2
        createUserAsNetworkAdmin(networkAdmin_t2, "Sara", "Martins", "password");

        AuthenticationUtil.clearCurrentSecurityContext();
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();

        deleteFilters();

        cloudContext.cleanup();
    }

    public void testNonNetworkAdminCannotCreateUpdateSolrFacets() throws Exception
    {
        // Create a filter object
        final JSONObject filter = new JSONObject();
        final String filterName = "filter" + System.currentTimeMillis();
        addFilter(networkAdmin_t1, filterName);
        filter.put("filterID", filterName);
        filter.put("facetQName", "cm:test1");
        filter.put("displayName", "facet-menu.facet.test1");
        filter.put("displayControl", "alfresco/search/FacetFilters/test1");
        filter.put("maxFilters", 5);
        filter.put("hitThreshold", 1);
        filter.put("minFilterValueLength", 4);
        filter.put("sortBy", "ALPHABETICALLY");

        // Non-Network-Admin tries to create a filter
        AuthenticationUtil.setFullyAuthenticatedUser(user1_nonAdmin_t1);
        sendRequest(new PostRequest(POST_FACETS_URL, filter.toString(), APPLICATION_JSON), 403);

        // Network-Admin creates a filter
        AuthenticationUtil.setFullyAuthenticatedUser(networkAdmin_t1);
        // Post the filter
        sendRequest(new PostRequest(POST_FACETS_URL, filter.toString(), APPLICATION_JSON), 200);

        // Non-Network-Admin tries to modify the filter
        AuthenticationUtil.setFullyAuthenticatedUser(user1_nonAdmin_t1);
        Response response = sendRequest(new GetRequest(GET_FACETS_URL + "/" + filterName), 200);
        JSONObject jsonRsp = new JSONObject(new JSONTokener(response.getContentAsString()));
        assertEquals(filterName, jsonRsp.getString("filterID"));
        assertEquals(5, jsonRsp.getInt("maxFilters"));
        // Now change the maxFilters value and try to update
        jsonRsp.put("maxFilters", 10);
        sendRequest(new PutRequest(PUT_FACETS_URL, jsonRsp.toString(), APPLICATION_JSON), 403);

    }

    public void testOnlyAuthenticatedUsersCanGetFacets() throws Exception
    {
        // Remove the current authentication details
        AuthenticationUtil.clearCurrentSecurityContext();
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getGuestUserName());
        Response rsp = sendRequest(new GetRequest(GET_FACETS_URL), 401);

        // Non-Network-Admin
        AuthenticationUtil.setFullyAuthenticatedUser(user1_nonAdmin_t1);
        rsp = sendRequest(new GetRequest(GET_FACETS_URL), 200);
        String contentAsString = rsp.getContentAsString();
        JSONObject jsonRsp = new JSONObject(new JSONTokener(contentAsString));
        JSONArray facetsArray = (JSONArray) jsonRsp.get(FACETS);
        assertNotNull("JSON 'facets' array was null", facetsArray);
    }

    public void testNetworkAdminReordersFacets() throws Exception
    {
        // Get the default facets
        final List<String> idsIndexes = AuthenticationUtil.runAs(new RunAsWork<List<String>>()
        {
            @Override
            public List<String> doWork() throws Exception
            {
                Response rsp = sendRequest(new GetRequest(GET_FACETS_URL), 200);

                JSONObject jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));

                final JSONArray facetsArray = (JSONArray) jsonRsp.get(FACETS);
                assertNotNull("JSON 'facets' array was null", facetsArray);

                List<String> idsIndexes = getListFromJsonArray(facetsArray);
                assertTrue("There should be more than 1 built-in facet", facetsArray.length() > 1);

                return idsIndexes;
            }
        }, networkAdmin_t1);

        // Reorder the facets - move up the last facet
        final String lastIndexId = AuthenticationUtil.runAs(new RunAsWork<String>()
        {
            @Override
            public String doWork() throws Exception
            {
                final String lastIndexId = idsIndexes.get(idsIndexes.size() - 1);
                final String url = PUT_FACET_URL_FORMAT.replace("{0}", lastIndexId).replace("{1}", "-1");
                sendRequest(new PutRequest(url, "", APPLICATION_JSON), 200);

                return lastIndexId;
            }
        }, networkAdmin_t1);

        // Get the facets to see if the reordering has worked
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Now get the facets back and we should see that one has moved.
                Response rsp = sendRequest(new GetRequest(GET_FACETS_URL), 200);

                JSONObject jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));

                JSONArray newfacetsArray = (JSONArray) jsonRsp.get(FACETS);
                assertNotNull("JSON 'facets' array was null", newfacetsArray);
                final List<String> newIdsIndexes = getListFromJsonArray(newfacetsArray);
                // Note here that the last Facet JSON object *is* moved one place up the list.
                assertEquals(CollectionUtils.moveLeft(1, lastIndexId, idsIndexes), newIdsIndexes);

                return null;
            }
        }, networkAdmin_t1);

        // Get the facets for other tenant. The reordering shouldn't affect the other tenants.
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                Response rsp = sendRequest(new GetRequest(GET_FACETS_URL), 200);

                JSONObject jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));

                JSONArray newfacetsArray = (JSONArray) jsonRsp.get(FACETS);
                assertNotNull("JSON 'facets' array was null", newfacetsArray);
                final List<String> newIdsIndexes = getListFromJsonArray(newfacetsArray);
                assertEquals(idsIndexes, newIdsIndexes);

                return null;
            }
        }, networkAdmin_t2);
    }

    public void testDefaultValues() throws Exception
    {
        final String filterNameOne = "filterOne" + System.currentTimeMillis();
        final String filterNameTwo = "filterTwo" + System.currentTimeMillis();
        addFilter(networkAdmin_t1, filterNameOne);
        addFilter(networkAdmin_t1, filterNameTwo);
        final List<String> expectedValues = Arrays.asList(new String[] { "sit1", "site2", "site3" });

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Build the Filter object - ignore the optional values
                JSONObject filter_one = new JSONObject();
                filter_one.put("filterID", filterNameOne);
                filter_one.put("facetQName", "cm:test1");
                filter_one.put("displayName", "facet-menu.facet.test1");
                filter_one.put("displayControl", "alfresco/search/FacetFilters/test1");
                filter_one.put("maxFilters", 5);
                filter_one.put("hitThreshold", 1);
                filter_one.put("minFilterValueLength", 4);
                filter_one.put("sortBy", "ALPHABETICALLY");

                // Post the filter
                sendRequest(new PostRequest(POST_FACETS_URL, filter_one.toString(),APPLICATION_JSON), 200);

                return null;
            }
        }, networkAdmin_t1);

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Retrieve the created filter
                Response response = sendRequest(new GetRequest(GET_FACETS_URL + "/" + filterNameOne), 200);
                JSONObject jsonRsp = new JSONObject(new JSONTokener(response.getContentAsString()));
                assertEquals(filterNameOne, jsonRsp.getString("filterID"));
                assertEquals("{http://www.alfresco.org/model/content/1.0}test1", jsonRsp.getString("facetQName"));
                assertEquals("facet-menu.facet.test1", jsonRsp.getString("displayName"));
                assertEquals("alfresco/search/FacetFilters/test1", jsonRsp.getString("displayControl"));
                assertEquals(5, jsonRsp.getInt("maxFilters"));
                assertEquals(1, jsonRsp.getInt("hitThreshold"));
                assertEquals(4, jsonRsp.getInt("minFilterValueLength"));
                assertEquals("ALPHABETICALLY", jsonRsp.getString("sortBy"));
                // Check the Default values
                assertEquals("ALL", jsonRsp.getString("scope"));
                assertFalse(jsonRsp.getBoolean("isEnabled"));
                assertFalse(jsonRsp.getBoolean("isDefault"));
                return null;
            }
        }, networkAdmin_t1);

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Build the Filter object with all the values
                JSONObject filter_two = new JSONObject();
                filter_two.put("filterID", filterNameTwo);
                filter_two.put("facetQName", "cm:test2");
                filter_two.put("displayName", "facet-menu.facet.test2");
                filter_two.put("displayControl", "alfresco/search/FacetFilters/test2");
                filter_two.put("maxFilters", 5);
                filter_two.put("hitThreshold", 1);
                filter_two.put("minFilterValueLength", 4);
                filter_two.put("sortBy", "ALPHABETICALLY");
                filter_two.put("scope", "SCOPED_SITES");
                filter_two.put("scopedSites", expectedValues);
                filter_two.put("isEnabled", true);

                // Post the filter
               sendRequest(new PostRequest(POST_FACETS_URL, filter_two.toString(), APPLICATION_JSON), 200);

               return null;
           }
       }, networkAdmin_t1);

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Retrieve the created filter
                Response response = sendRequest(new GetRequest(GET_FACETS_URL + "/" + filterNameTwo), 200);
                JSONObject jsonRsp = new JSONObject(new JSONTokener(response.getContentAsString()));

                assertEquals(filterNameTwo, jsonRsp.getString("filterID"));
                assertEquals("SCOPED_SITES", jsonRsp.getString("scope"));
                assertTrue(jsonRsp.getBoolean("isEnabled"));
                JSONArray jsonArray = jsonRsp.getJSONArray("scopedSites");
                List<String> retrievedValues = getListFromJsonArray(jsonArray);
                // Sort the list
                Collections.sort(retrievedValues);
                assertEquals(expectedValues, retrievedValues);

                return null;
            }
        }, networkAdmin_t1);

        // Get the facets for other tenant. Tenant2 should not be able to view Tenant1's filters.
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                sendRequest(new GetRequest(GET_FACETS_URL + "/" + filterNameTwo), 404);
                return null;
            }
        }, networkAdmin_t2);
    }

    public void testCreateUpdateFacetWithInvalidFilterId() throws Exception
    {
        // Build the Filter object
        final JSONObject filter = new JSONObject();
        final String filterName = "filter" + System.currentTimeMillis();
        addFilter(networkAdmin_t1, filterName);
        filter.put("filterID", filterName);
        filter.put("facetQName", "cm:test1");
        filter.put("displayName", "facet-menu.facet.test1");
        filter.put("displayControl", "alfresco/search/FacetFilters/test1");
        filter.put("maxFilters", 5);
        filter.put("hitThreshold", 1);
        filter.put("minFilterValueLength", 4);
        filter.put("sortBy", "ALPHABETICALLY");

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Post the filter
                sendRequest(new PostRequest(POST_FACETS_URL, filter.toString(), APPLICATION_JSON), 200);

                return null;
            }
        }, networkAdmin_t1);

        // Check the filter has been created
        final JSONObject jsonRsp = AuthenticationUtil.runAs(new RunAsWork<JSONObject>()
        {
            @Override
            public JSONObject doWork() throws Exception
            {
                // Retrieve the created filter
                Response response = sendRequest(new GetRequest(GET_FACETS_URL + "/" + filterName), 200);
                JSONObject jsonRsp = new JSONObject(new JSONTokener(response.getContentAsString()));
                assertEquals(filterName, jsonRsp.getString("filterID"));

                return jsonRsp;
            }
        }, networkAdmin_t1);

        // Network-Admin tries to change the FilterID value
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Now change the filterID value and try to update
                jsonRsp.put("filterID", filterName + "Modified");
                sendRequest(new PutRequest(PUT_FACETS_URL, jsonRsp.toString(), APPLICATION_JSON), 400);

                return null;
            }
        }, networkAdmin_t1);

        // Network-Admin tries to create a filter with a duplicate FilterID
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Post the filter
                sendRequest(new PostRequest(POST_FACETS_URL, filter.toString(), APPLICATION_JSON), 400);

                return null;
            }
        }, networkAdmin_t1);

    }

    public void testUpdateSingleValue() throws Exception
    {
        // Build the Filter object
        final JSONObject filter = new JSONObject();
        final String filterName = "filter" + System.currentTimeMillis();
        addFilter(networkAdmin_t1, filterName);
        filter.put("filterID", filterName);
        filter.put("facetQName", "cm:test");
        filter.put("displayName", "facet-menu.facet.test1");
        filter.put("displayControl", "alfresco/search/FacetFilters/test");
        filter.put("maxFilters", 5);
        filter.put("hitThreshold", 1);
        filter.put("minFilterValueLength", 4);
        filter.put("sortBy", "ALPHABETICALLY");
        filter.put("isEnabled", true);

        JSONObject customProp = new JSONObject();
        // custom prop
        JSONObject blockIncludeRequest = new JSONObject();
        blockIncludeRequest.put("name", "blockIncludeFacetRequest");
        blockIncludeRequest.put("value", "true");
        customProp.put("blockIncludeFacetRequest", blockIncludeRequest);
        filter.put("customProperties", customProp);

        // Create the filter
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Post the filter
                sendRequest(new PostRequest(POST_FACETS_URL, filter.toString(), "application/json"), 200);
                return null;
            }
        }, networkAdmin_t1);

        // Check the created filter
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Retrieve the created filter
                Response response = sendRequest(new GetRequest(GET_FACETS_URL + "/" + filterName), 200);
                JSONObject jsonRsp = new JSONObject(new JSONTokener(response.getContentAsString()));

                assertEquals(filterName, jsonRsp.getString("filterID"));
                assertEquals("facet-menu.facet.test1", jsonRsp.getString("displayName"));
                return null;
            }
        }, networkAdmin_t1);

        // Network-Admin updates displayName
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                JSONObject singleValueJson = new JSONObject();
                singleValueJson.put("filterID", filterName);
                // Change the displayName value and update
                singleValueJson.put("displayName", "facet-menu.facet.modifiedValue");
                sendRequest(new PutRequest(PUT_FACETS_URL, singleValueJson.toString(), "application/json"), 200);

                return null;
            }
        }, networkAdmin_t1);

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                Response response = sendRequest(new GetRequest(GET_FACETS_URL + "/" + filterName), 200);
                JSONObject jsonRsp = new JSONObject(new JSONTokener(response.getContentAsString()));

                // Check the modified value has been persisted
                assertEquals("facet-menu.facet.modifiedValue", jsonRsp.getString("displayName"));

                // Make sure the rest of values haven't been changed
                assertEquals(filterName, jsonRsp.getString("filterID"));
                assertEquals("{http://www.alfresco.org/model/content/1.0}test", jsonRsp.getString("facetQName"));
                assertEquals("alfresco/search/FacetFilters/test", jsonRsp.getString("displayControl"));
                assertEquals(5, jsonRsp.getInt("maxFilters"));
                assertEquals(1, jsonRsp.getInt("hitThreshold"));
                assertEquals(4, jsonRsp.getInt("minFilterValueLength"));
                assertEquals("ALPHABETICALLY", jsonRsp.getString("sortBy"));
                assertEquals("ALL", jsonRsp.getString("scope"));
                assertFalse(jsonRsp.getBoolean("isDefault"));
                assertTrue(jsonRsp.getBoolean("isEnabled"));
                // Make sure custom properties haven't been deleted
                JSONObject retrievedCustomProp = jsonRsp.getJSONObject("customProperties");
                JSONObject retrievedBlockIncludeRequest = retrievedCustomProp.getJSONObject("blockIncludeFacetRequest");
                assertEquals("{http://www.alfresco.org/model/solrfacetcustomproperty/1.0}blockIncludeFacetRequest", retrievedBlockIncludeRequest.get("name"));
                assertEquals("true", retrievedBlockIncludeRequest.get("value"));

                return null;
            }
        }, networkAdmin_t1);
    }

    private List<String> getListFromJsonArray(JSONArray facetsArray) throws JSONException
    {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < facetsArray.length(); i++)
        {
            Object object = facetsArray.get(i);
            if (object instanceof JSONObject)
            {
                final JSONObject nextFacet = (JSONObject) object;
                final String nextId = nextFacet.getString("filterID");
                result.add(nextId);
            }
            else
            {
                result.add((String) object);
            }
        }
        return result;
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

    private void deleteFilters() throws IOException
    {
        for (final Entry<String, List<String>> entry : filtersMap.entrySet())
        {
            AuthenticationUtil.runAs(new RunAsWork<Void>()
            {
                @Override
                public Void doWork() throws Exception
                {
                    transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                    {
                        @Override
                        public Void execute() throws Throwable
                        {
                            for (String filter : entry.getValue())
                            {
                                sendRequest(new DeleteRequest(GET_FACETS_URL + "/" + filter), 200);
                            }
                            return null;
                        }
                    });

                    return null;
                }
            }, entry.getKey());
        }
    }

    private void addFilter(String creator, String filterName)
    {
        List<String> filters = filtersMap.get(creator);
        if (filters == null)
        {
            filters = new ArrayList<String>();
        }
        filtersMap.put(creator, filters);
    }
}
