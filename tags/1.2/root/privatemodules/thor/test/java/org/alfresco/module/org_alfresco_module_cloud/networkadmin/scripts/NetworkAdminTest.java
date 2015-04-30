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
package org.alfresco.module.org_alfresco_module_cloud.networkadmin.scripts;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceImpl.INVALID_EMAIL_TYPE;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceImpl.InvalidEmail;
import org.alfresco.module.org_alfresco_module_cloud.tenant.BaseTenantWebScriptTest;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.util.EqualsHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

public class NetworkAdminTest extends BaseTenantWebScriptTest
{
    private final static String GET_PEOPLE_URL = "/internal/cloud/people?skipCount={0}&maxItems={1}&filter={2}&sortBy={3}&internal={4}&networkAdmin={5}";
    private final static String CREATE_PEOPLE_URL = "/internal/cloud/people";

    private final static String TEST_DOMAIN_PREFIX = "acme";
    
    private List<Account> testAccounts;
    
    private AccountService accountService;
    private RetryingTransactionHelper transactionHelper;
    private RegistrationService registrationService;

    private CloudTestContext cloudContext;
    
    private TestPerson[] testPeople = null;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        cloudContext = new CloudTestContext(this);
        accountService = (AccountService) cloudContext.getApplicationContext().getBean("accountService");
        transactionHelper = (RetryingTransactionHelper)cloudContext.getApplicationContext().getBean("retryingTransactionHelper");
        registrationService = (RegistrationService)cloudContext.getApplicationContext().getBean("RegistrationService");
        
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());

        testPeople = new TestPerson[]
        {
        		new TestPerson("David", "Smith", "david.smith", true),
        		new TestPerson("Bob", "Jones", "bob.jones", false),
        		new TestPerson("Bill", "Grainger", "bill.grainger", true),
        		new TestPerson("Jill", "Fry", "jill.fry", false),
        		new TestPerson("Steve", "Hunt", "steve.hunt", false),
        		new TestPerson("Claire", "Knight", "claire.knight", true),
        		new TestPerson("John", "Hatfield", "john.hatfield", false),
        		new TestPerson("James", "Laurie", "james.laurie", false),
        		new TestPerson("Liz", "Bird", "liz.bird", true),
        		new TestPerson("Cliff", "Munro", "cliff.munro", false)
        };

        testAccounts = new ArrayList<Account>();
        
        // We must create an account via the Foundation API in order to retrieve it via the remote API.
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
        	@SuppressWarnings("synthetic-access")
        	public Void execute() throws Throwable
        	{
        		String tenantName1 = cloudContext.createTenantName(TEST_DOMAIN_PREFIX + ".0");

    			Account account1 = accountService.createAccount(tenantName1, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
    			testAccounts.add(account1);
    			cloudContext.addAccount(account1);
    			
    			// override quota for number of network admins (default is 0 for free account)
                account1.getUsageQuota().setPersonNetworkAdminCountQuota(5);
                accountService.updateAccount(account1);

    			for(int j = 0; j < testPeople.length; j++)
    			{
    				String email = cloudContext.createUserName(testPeople[j].getUsername(), tenantName1);
            		registrationService.createUser(email, testPeople[j].getFirstName(), testPeople[j].getLastName(), testPeople[j].getUsername());
            		if(testPeople[j].isNetworkAdmin())
            		{
            			registrationService.promoteUserToNetworkAdmin(account1.getId(), email);
            		}
            		cloudContext.addUser(email);
    			}

        		return null;
        	}
        }, false, true);
    }
    
    @Override
    public void tearDown() throws Exception
    {
        //cloudContext.cleanup();

        super.tearDown();
    }
    
    private void checkPerson(JSONObject p, int i)
    {
        assertEquals(testPeople[i].getFirstName(), p.get("firstName"));
        assertEquals(testPeople[i].getLastName(), p.get("lastName"));
    }

    private void checkAdmin(JSONObject p)
    {
        assertEquals("Administrator", p.get("firstName"));
    }

    private void checkPaging(JSONObject jsonRsp, long expectedMaxItems, long expectedSkipCount, long expectedTotalItems, String expectedConfidence)
    {
    	JSONObject paging = (JSONObject)jsonRsp.get("paging");
    	Long maxItems = (Long)paging.get("maxItems");
    	assertNotNull(maxItems);
    	assertEquals(expectedMaxItems, maxItems.longValue());
    	
    	Long skipCount = (Long)paging.get("skipCount");
    	assertNotNull(skipCount);
    	assertEquals(expectedSkipCount, skipCount.longValue());
    	
    	Long totalItems = (Long)paging.get("totalItems");
    	assertNotNull(totalItems);
    	assertEquals(expectedTotalItems, totalItems.longValue());
    	
    	String confidenceString = (String)paging.get("confidence");
    	assertNotNull(confidenceString);
    	assertEquals(expectedConfidence, confidenceString.toLowerCase());
    }

    private JSONObject listPeople(int skip, int maxItems, boolean internal, boolean networkAdmin, String sortBy, int expectedStatus) throws IOException
    {
    	MessageFormat f = new MessageFormat(GET_PEOPLE_URL);
    	GetRequest req = new GetRequest(f.format(new String[] {String.valueOf(skip), String.valueOf(maxItems), "", sortBy, internal ? "true" : "false", networkAdmin ? "true" : "false"}));
    	
    	Response rsp = sendRequest(req, expectedStatus);
    	
    	String contentAsString = rsp.getContentAsString();
    	JSONObject jsonRsp = (JSONObject)JSONValue.parse(contentAsString);
    	return jsonRsp;
    }

    public void testPaging() throws Exception
    {
    	Account testAccount = testAccounts.get(0);

    	TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
    	{
			@Override
			public Void doWork() throws Exception
			{
		    	// get first page
		    	JSONObject jsonRsp = listPeople(0, 1, true, false, "lastName", 200);
		    	JSONArray peopleArray = (JSONArray)jsonRsp.get("data");
		    	assertEquals(1, peopleArray.size());

		    	checkPaging(jsonRsp, 1l, 0l, 6l, "exact");
		    	JSONObject p = (JSONObject)peopleArray.get(0);
		    	checkPerson(p, 3);
		    	
		    	// get a second page
		    	jsonRsp = listPeople(1, 3, true, false, "lastName", 200);
		    	peopleArray = (JSONArray)jsonRsp.get("data");
		    	assertEquals(3, peopleArray.size());

		    	checkPaging(jsonRsp, 3l, 1l, 6l, "exact");
		    	p = (JSONObject)peopleArray.get(0);	
		    	checkPerson(p, 6);
		    	p = (JSONObject)peopleArray.get(1);	
		    	checkPerson(p, 4);
                p = (JSONObject)peopleArray.get(2); 
                checkPerson(p, 1);

				return null;
			}
		}, "david.smith@" + testAccount.getName(), testAccount.getName());
    }
    
    public void testPagingNonNetworkAdmin() throws Exception
    {
    	Account testAccount = testAccounts.get(0);

    	TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
    	{
			@Override
			public Void doWork() throws Exception
			{
		    	JSONObject jsonRsp = listPeople(0, 3, true, false, "lastName", 401);
				return null;
			}
		}, "bob.jones@" + testAccount.getName(), testAccount.getName());
    }

    private void assertSameElements(List<?> expected, List<?> test)
    {
    	assertEquals(expected.size(), test.size());
    	for(Object val : expected)
    	{
    		if(!test.contains(val))
    		{
    			fail("Expected element " + val);
    		}
    	}
    }

    public void testCreatePeople()
    {
    	Account testAccount = testAccounts.get(0);

    	TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
    	{
			@SuppressWarnings("unchecked")
			@Override
			public Void doWork() throws Exception
			{
		    	JSONArray emailsArray = new JSONArray();
		    	emailsArray.add(cloudContext.createUserName(testPeople[0].getUsername(), testAccounts.get(0).getName()));
		    	emailsArray.add(cloudContext.createUserName("humpty.dumpty", testAccounts.get(0).getName()));
		    	emailsArray.add(cloudContext.createUserName("micky.mouse", "test.domain"));

		    	JSONObject o = new JSONObject();
		    	o.put("emails", emailsArray);
		    	o.put("source", "accountSettings");
		    	o.put("source_url", "");
		    	o.put("message", "Test message");
		    	String body = o.toJSONString();

		        Response rsp = sendRequest(new PostRequest(CREATE_PEOPLE_URL, body, "application/json"), 200);
		        String contentAsString = rsp.getContentAsString();
		    	JSONObject jsonRsp = (JSONObject)JSONValue.parse(contentAsString);
		
		    	System.out.println(contentAsString);
		
		    	JSONArray invalidEmailsArray = (JSONArray)jsonRsp.get("invalidEmails");
		    	assertEquals(2, invalidEmailsArray.size());
		    	List<InvalidEmail> invalidEmails = new ArrayList<InvalidEmail>();
		    	for(int i = 0; i < invalidEmailsArray.size(); i++)
		    	{
		    		JSONObject invalidEmail = (JSONObject)invalidEmailsArray.get(i);
    		    		invalidEmails.add(new InvalidEmail((String)invalidEmail.get("email"), INVALID_EMAIL_TYPE.getType((String)invalidEmail.get("failureReason"))));
		    	}

		    	List<InvalidEmail> expected = new ArrayList<InvalidEmail>();
		    	expected.add(new InvalidEmail(cloudContext.createUserName(testPeople[0].getUsername(), testAccounts.get(0).getName()), INVALID_EMAIL_TYPE.USER_EXISTS));
		    	expected.add(new InvalidEmail(cloudContext.createUserName("micky.mouse", "test.domain"), INVALID_EMAIL_TYPE.INCORRECT_DOMAIN));
		    	assertSameElements(expected, invalidEmails);
		    	
		    	return null;
			}
    	}, "david.smith@" + testAccount.getName(), testAccount.getName());
    }

    private static class TestPerson
    {
    	private String firstName;
    	private String lastName;
    	private String username;
    	private boolean networkAdmin;
    	
		public TestPerson(String firstName, String lastName, String username,
				boolean networkAdmin)
		{
			super();
			this.firstName = firstName;
			this.lastName = lastName;
			this.username = username;
			this.networkAdmin = networkAdmin;
		}

		public String getFirstName()
		{
			return firstName;
		}

		public String getLastName()
		{
			return lastName;
		}

		public String getUsername()
		{
			return username;
		}

		public boolean isNetworkAdmin()
		{
			return networkAdmin;
		}
		
		public TestPerson setNetworkAdmin(boolean networkAdmin)
		{
			this.networkAdmin = networkAdmin;
			return this;
		}
		
		public boolean equals(Object other)
		{
			if(!(other instanceof TestPerson))
			{
				return false;
			}

			TestPerson testPerson = (TestPerson)other;
			return (EqualsHelper.nullSafeEquals(firstName, testPerson.getFirstName()) &&
					EqualsHelper.nullSafeEquals(lastName, testPerson.getLastName()) &&
					EqualsHelper.nullSafeEquals(networkAdmin, testPerson.isNetworkAdmin()));
		}
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("TestPerson[");
			sb.append("First name : ");
			sb.append(firstName);
			sb.append("Last name : ");
			sb.append(lastName);
			sb.append("Username : ");
			sb.append(username);
			sb.append("Network admin : ");
			sb.append(networkAdmin);
			return sb.toString();
		}
    }
}
