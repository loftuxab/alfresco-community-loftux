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
package org.alfresco.module.org_alfresco_module_cloud.users;

import java.io.IOException;
import java.io.StringWriter;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountAdminService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.registration.Registration;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.test_category.SharedJVMTestsCategory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.junit.experimental.categories.Category;
import org.springframework.extensions.webscripts.TestWebScriptServer.DeleteRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * This class tests the REST API for Network Admin management.
 * 
 * @author Neil McErlean
 * @since Alfresco Cloud Module (Thor)
 */
@Category(SharedJVMTestsCategory.class)
public class NetworkAdminRestApiTest extends BaseWebScriptTest
{
    private static final Log log = LogFactory.getLog(NetworkAdminRestApiTest.class);
    
    // Miscellaneous constants.
    private final static String APPLICATION_JSON = "application/json";
    
    // URLs and URL fragments used in this REST API.
    private final static String PROMOTE_USER_BY_ACCOUNT_ID_URL = "/internal/cloud/accounts/{id}/networkadmins";
    private final static String PROMOTE_USER_BY_ACCOUNT_DOMAIN_URL = "/internal/cloud/domains/{domainName}/account/networkadmins";
    
    private AccountService accountService;
    private AccountAdminService accountAdminService;
    private RegistrationService registrationService;
    private TransactionService transactionService;
    
    private CloudTestContext cloudContext;
    
    private String networkAdminUser, nonNetworkAdminUser, externalUser, noSuchUser, networkAdminUser2;
    private String T1, T2, T3;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        cloudContext = new CloudTestContext(this);
        
        accountService = (AccountService)cloudContext.getApplicationContext().getBean("AccountService");
        accountAdminService = (AccountAdminService)cloudContext.getApplicationContext().getBean("AccountAdminService");
        registrationService = (RegistrationService)cloudContext.getApplicationContext().getBean("RegistrationService");
        transactionService = (TransactionService)cloudContext.getApplicationContext().getBean("TransactionService");
        
        T1 = cloudContext.createTenantName("networkadmin");
        T2 = cloudContext.createTenantName("networkadminexternal");
        T3 = cloudContext.createTenantName("standard");
        
        // We want three activated user across the 2 tenants.
        networkAdminUser = cloudContext.createUserName("internaluser1", T1);
        nonNetworkAdminUser = cloudContext.createUserName("internaluser2", T1);
        noSuchUser = cloudContext.createUserName("usernotcreated", T1);
        externalUser = cloudContext.createUserName("externaluser", T2);
        networkAdminUser2 = cloudContext.createUserName("internaluser3", T3);

//        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
//        {
//        	@SuppressWarnings("synthetic-access")
//        	public Void execute() throws Throwable
//        	{
                AuthenticationUtil.runAs(new RunAsWork<Void>()
        		{
                	@Override
                	public Void doWork() throws Exception
                	{
		                for (String username : new String[] {networkAdminUser, nonNetworkAdminUser, externalUser, networkAdminUser2})
		                {
		                    Registration activeUserReg = registrationService.registerEmail(username, "test-" + this.getClass().getSimpleName(), null, null);
		                    cloudContext.addRegistration(activeUserReg);
		                    registrationService.activateRegistration(activeUserReg.getId(), activeUserReg.getKey(), "fff", "lll", "password");
		                    cloudContext.addUser(username);
		                }
		               
		                // override quota for number of network admins (default is 0 for free account)
		                Account t1Account = accountService.getAccountByDomain(T1);
		                t1Account.getUsageQuota().setPersonNetworkAdminCountQuota(5);
		                accountService.updateAccount(t1Account);
		                
		                // promote one of the users before any tests start
		                long t1AccountId = t1Account.getId();
		                registrationService.promoteUserToNetworkAdmin(t1AccountId, networkAdminUser);
		                
		                // We also want the external user to be in the T1 tenant.
		                registrationService.addUser(t1AccountId, externalUser);
		                
		                //Setup standard account, t3, with a network admin.
		                Account t3Account = accountService.getAccountByDomain(T3);
		                accountAdminService.changeAccountType(t3Account, AccountType.STANDARD_NETWORK_ACCOUNT_TYPE);
		                
		                long t3AccountId = t3Account.getId();
                        registrationService.promoteUserToNetworkAdmin(t3AccountId, networkAdminUser2);
		                
		                return null;
                    }
                }, AuthenticationUtil.getAdminUserName());
//
//                return null;
//        	}
//        }, false, true);
    }
    
    @Override
    public void tearDown() throws Exception
    {
        cloudContext.cleanup();
    }
    
    @SuppressWarnings("unchecked")
    private void promoteUserViaREST(final String username, final String networkAdminUsername, final String tenantDomain, final int expectedStatus) throws IOException
    {
    	TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
    	{
    		@Override
    		public Void doWork() throws Exception
    		{
		        log.debug("Calling for promotion of user " + username + " in tenant " + tenantDomain);
		        log.debug("Tenant account id = " + accountService.getAccountByDomain(tenantDomain).getId());
		        log.debug("Expected status = " + expectedStatus);
		        
		        JSONObject obj = new JSONObject();
		        obj.put("username", username);
		        
		        StringWriter stringWriter = new StringWriter();
		        obj.writeJSONString(stringWriter);
		        String jsonString = stringWriter.toString();
		        
		        final String promotionUrl = PROMOTE_USER_BY_ACCOUNT_DOMAIN_URL.replace("{domainName}", tenantDomain);
		        Response rsp = sendRequest(new PostRequest(promotionUrl, jsonString, APPLICATION_JSON), expectedStatus);
		        
		        if (expectedStatus == 200)
		        {
		            //TODO assertions on rsp
		        }
		        return null;
			}
		}, networkAdminUsername, tenantDomain);
    }
    
    @SuppressWarnings("unchecked")
    private void promoteUserViaREST(final String username, final String networkAdminUsername, final long accountId, final int expectedStatus) throws IOException
    {
    	String tenantDomain = accountService.getAccount(accountId).getName();
    	
    	TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
    	{
    		@Override
    		public Void doWork() throws Exception
    		{
		        JSONObject obj = new JSONObject();
		        obj.put("username", username);
		        
		        StringWriter stringWriter = new StringWriter();
		        obj.writeJSONString(stringWriter);
		        String jsonString = stringWriter.toString();
		        
		        final String promotionUrl = PROMOTE_USER_BY_ACCOUNT_ID_URL.replace("{id}", Long.toString(accountId));
		        Response rsp = sendRequest(new PostRequest(promotionUrl, jsonString, APPLICATION_JSON), expectedStatus);
		        
		        if (expectedStatus == 200)
		        {
		            //TODO assertions on rsp
		        }

		        return null;
    		}
    	}, networkAdminUsername, tenantDomain);
    }
    
    private void demoteUserViaREST(final String username, final String networkAdminUsername, final String tenantDomain, final int expectedStatus) throws IOException
    {
    	TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
    	{
    		@Override
    		public Void doWork() throws Exception
    		{
		        final String promotionUrl = PROMOTE_USER_BY_ACCOUNT_DOMAIN_URL.replace("{domainName}", tenantDomain) + "/" + username;
		        Response rsp = sendRequest(new DeleteRequest(promotionUrl), expectedStatus);
		        
		        if (expectedStatus == 200)
		        {
		            //TODO assertions on rsp
		        }
		
		        return null;
			}
		}, networkAdminUsername, tenantDomain);
    }

    private void demoteUserViaREST(final String username, final String networkAdminUsername, final long accountId, final int expectedStatus) throws IOException
    {
    	String tenantDomain = accountService.getAccount(accountId).getName();

    	TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
    	{
    		@Override
    		public Void doWork() throws Exception
    		{
    			final String promotionUrl = PROMOTE_USER_BY_ACCOUNT_ID_URL.replace("{id}", Long.toString(accountId)) + "/" + username;
    			Response rsp = sendRequest(new DeleteRequest(promotionUrl), expectedStatus);
    			
    			if (expectedStatus == 200)
    			{
    				//TODO assertions on rsp
			    }

		        return null;
			}
		}, networkAdminUsername, tenantDomain);
	}
    
    public void testPromoteAndDemoteUserByAccountId() throws Exception
    {
        long accountId = accountService.getAccountByDomain(T1).getId();
        promoteUserViaREST(nonNetworkAdminUser, networkAdminUser, accountId, 200);
        
        demoteUserViaREST(nonNetworkAdminUser, networkAdminUser, accountId, 200);
    }
    
    public void testPromoteAndDemoteUserByAccountDomain() throws Exception
    {
        promoteUserViaREST(nonNetworkAdminUser, networkAdminUser, T1, 200);
        
        demoteUserViaREST(nonNetworkAdminUser, networkAdminUser, T1, 200);
    }
    
    public void testDemoteLastAdminUserShouldFail() throws Exception
    {
        demoteUserViaREST(networkAdminUser2, networkAdminUser2, T3, 403);
    }
    
    public void testPromoteAndDemoteNonExistentUsers() throws Exception
    {
        promoteUserViaREST(noSuchUser, networkAdminUser, T1, 400);
        demoteUserViaREST(noSuchUser, networkAdminUser, T1, 404);
    }
    
    public void testPromoteExternalUserShouldFail() throws Exception
    {
        promoteUserViaREST(externalUser, networkAdminUser, T1, 403);
    }
    
    public void testDemoteUserWhoIsntAnAdmin() throws Exception
    {
        demoteUserViaREST(nonNetworkAdminUser, networkAdminUser, T1, 200);
    }
    
    public void testPromoteUserWhoIsAlreadyAnAdmin() throws Exception
    {
        promoteUserViaREST(networkAdminUser, networkAdminUser, T1, 200);
    }
}
