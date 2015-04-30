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
package org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PutRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * This class tests the Remote API of the {@link AccountService}.
 * 
 * @author Neil McErlean
 * @since Alfresco Cloud Module
 */
public class AccountRestApiTest extends BaseWebScriptTest
{
    // Miscellaneous constants.
    private static final String DATA = "data";
    
    private final static String GET_ACCOUNT_TYPES_URL = "/internal/cloud/account-types";
    private final static String GET_ACCOUNT_BY_ID_URL = "/internal/cloud/accounts/";
    private final static String GET_ACCOUNTS_URL = "/internal/cloud/accounts"; // No trailing slash
    
    private final static String TEST_DOMAIN_PREFIX = "acme";
    
    private List<Account> testAccounts;
    
    private AccountService accountService;
    private RetryingTransactionHelper transactionHelper;
    
    private CloudTestContext cloudContext;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        cloudContext = new CloudTestContext(this);
        accountService = (AccountService) cloudContext.getApplicationContext().getBean("accountService");
        transactionHelper = (RetryingTransactionHelper)cloudContext.getApplicationContext().getBean("retryingTransactionHelper");  
        
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        // We must create an account via the Foundation API in order to retrieve it via the remote API.
        testAccounts = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<List<Account>>()
            {
                @SuppressWarnings("synthetic-access")
                public List<Account> execute() throws Throwable
                {
                    List<Account> accs = new ArrayList<Account>();
                    for (int i = 0; i < 3; i++)
                    {
                        String name = cloudContext.createTenantName(TEST_DOMAIN_PREFIX + i);
                        Account newAccount = accountService.createAccount(name, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
                        accs.add(newAccount);
                        cloudContext.addAccount(newAccount);
                    }
                    for (int i = 3; i < 6; i++)
                    {
                        String name = cloudContext.createTenantName(TEST_DOMAIN_PREFIX + i);
                        Account newAccount = accountService.createAccount(name, AccountType.STANDARD_NETWORK_ACCOUNT_TYPE, true);
                        accs.add(newAccount);
                        cloudContext.addAccount(newAccount);
                    }
                    return accs;
                }
            }, false, true);
    }
    
    @Override
    public void tearDown() throws Exception
    {
        cloudContext.cleanup();

        super.tearDown();
    }

    public void testGetAccount() throws Exception
    {
        Account testAccount = testAccounts.get(0);
        final String testDomain = testAccounts.get(0).getTenantId();
        
        final int expectedStatus = 200;
        Response rspToIdBasedReq = sendRequest(new GetRequest(GET_ACCOUNT_BY_ID_URL + testAccount.getId()), expectedStatus);
        Response rspToDomainBasedReq = sendRequest(new GetRequest("/internal/cloud/domains/" + testDomain + "/account"), expectedStatus);
        
        assertEquals("id-based rsp didn't match domain-based rsp", rspToIdBasedReq.getContentAsString(),
                                                                   rspToDomainBasedReq.getContentAsString());

        String contentAsString = rspToIdBasedReq.getContentAsString();
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        
        JSONObject dataObj = (JSONObject)jsonRsp.get(DATA);
        
        // If we can get the id, then it's a valid long.
        @SuppressWarnings("unused")
        long id = (Long)dataObj.get("id");
        
        assertEquals("name was wrong", testDomain, (String) dataObj.get("name"));
        assertEquals("type was wrong", new Long(AccountType.FREE_NETWORK_ACCOUNT_TYPE), (Long) dataObj.get("type"));
        assertEquals("PRIVATE_EMAIL_DOMAIN", dataObj.get("className"));
        assertEquals("Free", dataObj.get("classDisplayName"));
        
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        assertTrue("creation date was wrong", ((String) dataObj.get("creationDate")).startsWith(today));
        
        JSONArray domains = (JSONArray) dataObj.get("domains");
        assertNotNull("domains array was null", domains);
        assertEquals("domains length wrong", 1, domains.size());
        assertEquals("domains[0] wrong", testDomain, (String) domains.get(0));
    }
    
    public void testGetNonExistentAccount() throws Exception
    {
        final int expectedStatus = 404;
        final long noSuchAccountId = -42L;
        sendRequest(new GetRequest(GET_ACCOUNT_BY_ID_URL + noSuchAccountId), expectedStatus);
    }
    
    /**
     * This method gets a page of accounts from the accounts.get REST API.
     */
    public void testGetAccounts() throws Exception
    {
        int pageSize = 5;
        String pagingOpts = "?startIndex=0&pageSize="+pageSize;
        
        Response rsp = sendRequest(new GetRequest(GET_ACCOUNTS_URL + pagingOpts), 200);
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        
        JSONObject dataObj = (JSONObject)jsonRsp.get(DATA);
        assertNotNull("JSON 'data' object was null", dataObj);
        
        // The number of accounts as reported by itemCount.
        long accountCount = (Long) dataObj.get("itemCount");
        assertEquals("Expected page of accounts", pageSize, accountCount);
        
        // These are the actual account metadata.
        JSONArray itemsArray = (JSONArray) dataObj.get("items");
        assertEquals("Wrong account count", accountCount, itemsArray.size());
        
        // filter by account type id
        filterByAccountTypeId(AccountType.FREE_NETWORK_ACCOUNT_TYPE, pageSize);
        filterByAccountTypeId(AccountType.STANDARD_NETWORK_ACCOUNT_TYPE, pageSize);
        
        // sort by account type id
        sortByAccountTypeId(50, true);
        sortByAccountTypeId(50, false);
    }
    
    private void filterByAccountTypeId(int accountTypeId, int pageSize) throws IOException
    {
        String pagingOpts = "?typeId="+accountTypeId+"&startIndex=0&pageSize="+pageSize;
        
        Response rsp = sendRequest(new GetRequest(GET_ACCOUNTS_URL + pagingOpts), 200);
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        
        JSONObject dataObj = (JSONObject)jsonRsp.get(DATA);
        assertNotNull("JSON 'data' object was null", dataObj);
        
        JSONArray itemsArray = (JSONArray) dataObj.get("items");
        assertTrue(itemsArray.size() > 0);
        
        for (int i = 0; i < itemsArray.size(); i++)
        {
            JSONObject jsonObj = (JSONObject)itemsArray.get(i);
            assertEquals(accountTypeId, ((Long)jsonObj.get("type")).intValue());
        }
    }
    
    private void sortByAccountTypeId(int pageSize, boolean ascending) throws IOException
    {
        String pagingOpts = "?sortBy=typeId:"+(ascending ? "ASC" : "DESC")+"&startIndex=0&pageSize="+pageSize;
        
        Response rsp = sendRequest(new GetRequest(GET_ACCOUNTS_URL + pagingOpts), 200);
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        
        JSONObject dataObj = (JSONObject)jsonRsp.get(DATA);
        assertNotNull("JSON 'data' object was null", dataObj);
        
        JSONArray itemsArray = (JSONArray) dataObj.get("items");
        assertTrue(itemsArray.size() > 0);
        
        int previous = (ascending ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        int first = previous;
        for (int i = 0; i < itemsArray.size(); i++)
        {
            JSONObject jsonObj = (JSONObject)itemsArray.get(i);
            int current = ((Long)jsonObj.get("type")).intValue();
            assertTrue((ascending ? (current >= previous) : (current <= previous)));
            previous = current;
        }
        
        // make sure they weren't all the same !
        assertTrue(ascending ? (previous > first) : (previous < first));
    }
    
    /**
     * This method tests account-types.get.
     * Account types are defined in account-types-context.xml
     */
    public void testGetAccountTypes() throws Exception
    {
        final String pagingOpts = "?startIndex=0&pageSize=5";
        
        Response rsp = sendRequest(new GetRequest(GET_ACCOUNT_TYPES_URL + pagingOpts), 200);
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        
        JSONObject dataObj = (JSONObject)jsonRsp.get(DATA);
        final long startIndex = (Long)dataObj.get("startIndex");
        final long total = (Long)dataObj.get("total");
        final long pageSize = (Long)dataObj.get("pageSize");
        final long itemCount = (Long)dataObj.get("itemCount");
        
        assertEquals(startIndex, 0);
        assertEquals(pageSize, 5);
        // There are 2 AccountTypes defined in the system today (see account-types-context.xml). This will rise.
        final int accountTypeCount = 2;
        assertTrue("Incorrect number of account types reported.", total >= accountTypeCount);
        assertTrue(itemCount >= accountTypeCount);
        
        JSONArray itemsArray = (JSONArray) dataObj.get("items");
        assertEquals(itemCount, itemsArray.size());
        

//        {
//           "data":
//           {
//              "total": 2,
//              "pageSize": 5,
//              "startIndex": 0,
//              "itemCount": 2,
//              "items":
//              [
//                 {
//                    "id": 0,
//                    "name": "Free (0)",
//                    "quotas":
//                    {
//                       "fileSize": 10737418240,
//                       "siteCount": -1,
//                       "personCount": -1,
//                       "personIntOnlyCount": -1
//                    },
//                    "accountClass" : { "name": "PRIVATE_EMAIL_DOMAIN", "displayName": "Free Private Email Domain" }
//                 },
//                 {
//                    "id": 100,
//                    "name": "Premium Business (100)",
//                    "quotas":
//                    {
//                       "fileSize": 10737418240,
//                       "siteCount": -1,
//                       "personCount": -1,
//                       "personIntOnlyCount": -1
//                    },
//                    "accountClass" : { "name": "PAID_BUSINESS", "displayName": "Paid Business" }
//                 }
//              ]
//           }
//        }
    }
    
    /**
     * This test method ensures that a free account can be upgraded to a paid business account.
     */
    @SuppressWarnings("unchecked")
    public void testUpgradeAnAccount() throws Exception
    {
        // Get an existing account
        final String testDomain = testAccounts.get(0).getTenantId();
        final String accountUrl = "/internal/cloud/domains/" + testDomain + "/account";
        
        Response rsp = sendRequest(new GetRequest(accountUrl), 200);
        
        String contentAsString = rsp.getContentAsString();
        JSONObject jsonObject = (JSONObject) JSONValue.parse(contentAsString);
        
        JSONObject dataObj = (JSONObject)jsonObject.get(DATA);
        assertNotNull("JSON 'data' object was null", dataObj);
        
        // and check its account type is 'free'
        final long accountId = (Long) dataObj.get("id");
        final Long originalAccountType = (Long) dataObj.get("type");
        assertEquals("type was wrong", AccountType.FREE_NETWORK_ACCOUNT_TYPE, originalAccountType.intValue());
        
        
        // Now update the account type.
        JSONObject obj = new JSONObject();
        obj.put("accountTypeId", AccountType.STANDARD_NETWORK_ACCOUNT_TYPE);
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();
        
        rsp = sendRequest(new PutRequest(accountUrl, jsonString, "application/json"), 200);
        
        // Does the put response give the correct account type?
        contentAsString = rsp.getContentAsString();
        jsonObject = (JSONObject) JSONValue.parse(contentAsString);
        
        dataObj = (JSONObject)jsonObject.get(DATA);
        assertNotNull("JSON 'data' object was null", dataObj);
        
        assertEquals("put-rsp accountId was wrong", new Long(accountId), (Long) dataObj.get("id"));
        final Long accountType = (Long) dataObj.get("type");
        assertEquals("put-rsp type was wrong", AccountType.STANDARD_NETWORK_ACCOUNT_TYPE, accountType.intValue());
        
        
        // Now let's go back to the DB and check that the account type is read correctly
        rsp = sendRequest(new GetRequest(accountUrl), 200);
        
        contentAsString = rsp.getContentAsString();
        jsonObject = (JSONObject) JSONValue.parse(contentAsString);
        
        dataObj = (JSONObject)jsonObject.get(DATA);
        assertNotNull("JSON 'data' object was null", dataObj);
        
        assertEquals("get-rsp accountId was wrong", new Long(accountId), (Long) dataObj.get("id"));
        assertEquals("get-rsp type was wrong", new Long(AccountType.STANDARD_NETWORK_ACCOUNT_TYPE), (Long) dataObj.get("type"));
    }

    /**
     * This test method ensures that the proper error code is returned when a request is made to upgrade an account
     * to a non-existent account type.
     */
    @SuppressWarnings("unchecked")
    public void testUpgradeAnAccountToANonExistentAccountType() throws Exception
    {
        // Get an existing account
        final String testDomain = testAccounts.get(0).getTenantId();
        final String accountUrl = "/internal/cloud/domains/" + testDomain + "/account";
        
        // and ensure that it exists
        sendRequest(new GetRequest(accountUrl), 200);
        
        // Now update the account type.
        final int noSuchAccountTypeId = 123456789;
        JSONObject obj = new JSONObject();
        obj.put("accountTypeId", noSuchAccountTypeId);
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();
        
        // we expect to get a 400 error code, rather than e.g. a 5xx.
        sendRequest(new PutRequest(accountUrl, jsonString, "application/json"), 400);
    }
}
