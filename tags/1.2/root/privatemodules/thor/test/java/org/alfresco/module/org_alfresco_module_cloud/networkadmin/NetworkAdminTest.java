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
package org.alfresco.module.org_alfresco_module_cloud.networkadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceImpl.INVALID_EMAIL_TYPE;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceImpl.InvalidEmail;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService.TYPE;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.repo.management.subsystems.ApplicationContextFactory;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.EqualsHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

public class NetworkAdminTest
{
    private final static String TEST_DOMAIN_PREFIX = "acme";

    private static ApplicationContext testContext;
    
    // Services
    private RegistrationService registrationService;
    private AccountService accountService;
    private RetryingTransactionHelper transactionHelper;

    private CloudTestContext cloudContext;
    
    private CloudPersonService cloudPersonService;
    private NodeService nodeService;

    private List<Account> testAccounts;
    
    private TestPerson[] testPeople = null;
    
    /**
     * Initialise various services required by the test.
     */
    @Before public void initTestsContext() throws Exception
    {
        testContext = ApplicationContextHelper.getApplicationContext();
        
        transactionHelper = (RetryingTransactionHelper)testContext.getBean("retryingTransactionHelper");
        registrationService = (RegistrationService)testContext.getBean("registrationService");
        accountService = (AccountService)testContext.getBean("AccountService");
        cloudPersonService = (CloudPersonService)testContext.getBean("cloudPersonService");
        nodeService = (NodeService)testContext.getBean("NodeService");

        MailActionExecuter mailActionExecutor = (MailActionExecuter) ((ApplicationContextFactory)testContext
                .getBean("OutboundSMTP")).getApplicationContext().getBean("mail");
        mailActionExecutor.setTestMode(true);
        mailActionExecutor.clearLastTestMessage();
    }
    
    @Before public void init() throws Exception
    {
        cloudContext = new CloudTestContext(testContext);

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

        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());

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
    			
    			// Create a second account/tenant
    			String tenantName2 = cloudContext.createTenantName(TEST_DOMAIN_PREFIX + ".1");

    			Account account2 = accountService.createAccount(tenantName2, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
    			testAccounts.add(account2);
    			cloudContext.addAccount(account2);

    			for(int j = 0; j < testPeople.length; j++)
    			{
    				String email = cloudContext.createUserName(testPeople[j].getUsername(), tenantName1);

	    			// Add some users from the first account to the second account (as external users)
	    			registrationService.addUser(account2.getId(), email);
            		cloudContext.addUser(email);
    			}

        		return null;
        	}
        }, false, true);
    }
    
    /** Delete any temporary accounts created during the previous test */
    @After public void cleanup()
    {
        cloudContext.cleanup();
    }
    
    private void checkPeople(TestPerson[] expectedPeople, List<NodeRef> people)
    {
    	for(NodeRef personNodeRef : people)
    	{
			Map<QName, Serializable> props = nodeService.getProperties(personNodeRef);
			String firstName = (String)props.get(ContentModel.PROP_FIRSTNAME);
			String lastName = (String)props.get(ContentModel.PROP_LASTNAME);
			String username = (String)props.get(ContentModel.PROP_USERNAME);
			System.out.println(lastName + ":" + firstName + ":" + username);
    	}

    	int i = 0;
    	for(TestPerson expectedPerson : expectedPeople)
    	{
    		NodeRef person = people.get(i);

			Map<QName, Serializable> props = nodeService.getProperties(person);
			String firstName = (String)props.get(ContentModel.PROP_FIRSTNAME);
			String lastName = (String)props.get(ContentModel.PROP_LASTNAME);
			String username = (String)props.get(ContentModel.PROP_USERNAME);
			boolean networkAdmin = nodeService.hasAspect(person, CloudModel.ASPECT_NETWORK_ADMIN);

			assertEquals(expectedPerson, new TestPerson(firstName, lastName, username, networkAdmin));

			i++;
    	}    	
    }
    
    //@Test
    public void testListNetworkUsers()
    {
    	Account testAccount = testAccounts.get(0);

    	List<NodeRef> people = TenantUtil.runAsUserTenant(new TenantRunAsWork<List<NodeRef>>()
    	{
			@Override
			public List<NodeRef> doWork() throws Exception
			{
				PagingResults<NodeRef> results = cloudPersonService.getPeople("", "firstName", 0, 3, TYPE.INTERNAL, false);
				List<NodeRef> people = results.getPage();
		    	TestPerson[] expectedPeople = new TestPerson[] {testPeople[1]};
		    	checkPeople(expectedPeople, people);

		    	return people;
			}
		}, "david.smith@" + testAccount.getName(), testAccount.getName());
    }

    @Test
    public void testListNetworkUsers1()
    {
    	Account testAccount = testAccounts.get(0);

    	List<NodeRef> people = TenantUtil.runAsUserTenant(new TenantRunAsWork<List<NodeRef>>()
    	{
			@Override
			public List<NodeRef> doWork() throws Exception
			{
				PagingResults<NodeRef> results = cloudPersonService.getPeople("", "lastName", 0, 3, TYPE.INTERNAL, false);
				List<NodeRef> people = results.getPage();
		    	TestPerson[] expectedPeople = new TestPerson[] {testPeople[3], testPeople[6], testPeople[4]};
		    	checkPeople(expectedPeople, people);

		    	return people;
			}
		}, "david.smith@" + testAccount.getName(), testAccount.getName());
    }
    
    @Test
    public void testListNetworkUsersNetworkAdmin()
    {
    	Account testAccount = testAccounts.get(0);

    	List<NodeRef> people = TenantUtil.runAsUserTenant(new TenantRunAsWork<List<NodeRef>>()
    	{
			@Override
			public List<NodeRef> doWork() throws Exception
			{
				PagingResults<NodeRef> results = cloudPersonService.getPeople("", "lastName", 0, 3, TYPE.INTERNAL, true);
				assertEquals(4, results.getTotalResultCount().getFirst().intValue());
                assertEquals(4, results.getTotalResultCount().getSecond().intValue());
				List<NodeRef> people = results.getPage();
		    	TestPerson[] expectedPeople = new TestPerson[] {testPeople[8], testPeople[2], testPeople[5]};
		    	checkPeople(expectedPeople, people);

		    	return people;
			}
		}, "david.smith@" + testAccount.getName(), testAccount.getName());
    }
    
    @Test
    public void testListNetworkUsersExternal()
    {
    	Account testAccount = testAccounts.get(1);

    	List<NodeRef> people = TenantUtil.runAsUserTenant(new TenantRunAsWork<List<NodeRef>>()
    	{
			@Override
			public List<NodeRef> doWork() throws Exception
			{
				PagingResults<NodeRef> results = cloudPersonService.getPeople("", "lastName", 0, 3, TYPE.EXTERNAL, false);
				List<NodeRef> people = results.getPage();
				
				// expected people are external in this account and not network admins
		    	TestPerson[] expectedPeople = new TestPerson[] {
		    			testPeople[8].setNetworkAdmin(false),
		    			testPeople[3].setNetworkAdmin(false),
		    			testPeople[2].setNetworkAdmin(false)
		    	};
		    	checkPeople(expectedPeople, people);

		    	return people;
			}
		}, "david.smith@" + testAccount.getName(), testAccount.getName());
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

   // @Test
    public void testCreateRegistrations()
    {
    	//		registrationService.registerEmail("steven.glover@test2.com", "test", null, "Please join me at Alfresco Cloud");

    	final List<String> emails = new ArrayList<String>();
    	emails.add(cloudContext.createUserName(testPeople[0].getUsername(), testAccounts.get(0).getName()));
    	emails.add(cloudContext.createUserName("humpty.dumpty", testAccounts.get(0).getName()));
    	emails.add(cloudContext.createUserName("micky.mouse", "test.domain"));

    	List<InvalidEmail> invalidEmails = TenantUtil.runAsTenant(new TenantRunAsWork<List<InvalidEmail>>()
    	{
    		@Override
    		public List<InvalidEmail> doWork() throws Exception
    		{
    			Map<String, Serializable> emptyMap = Collections.emptyMap();
    			return registrationService.registerEmails(emails, "test", null, "Please join me at Alfresco Cloud", emptyMap);
    		}
    	}, testAccounts.get(0).getName());

    	List<InvalidEmail> expected = new ArrayList<InvalidEmail>();
    	expected.add(new InvalidEmail(cloudContext.createUserName(testPeople[0].getUsername(), testAccounts.get(0).getName()), INVALID_EMAIL_TYPE.USER_EXISTS));
    	expected.add(new InvalidEmail(cloudContext.createUserName("micky.mouse", "test.domain"), INVALID_EMAIL_TYPE.INCORRECT_DOMAIN));
    	assertSameElements(expected, invalidEmails);
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
