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
package org.alfresco.module.org_alfresco_module_cloud.analytics;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestUser;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.analytics.action.SendAnalyticsRequest;
import org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.repo.jscript.ClasspathScriptLocation;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ScriptLocation;
import org.alfresco.service.cmr.repository.ScriptService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.util.ApplicationContextHelper;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Test Code for {@link AnalyticsService}
 * 
 * @author David Gildeh
 * @since Thor Alfresco Cloud Module 0.1
 */
public class AnalyticsTest
{
    // Log4J Logger
    private static final Log logger = LogFactory.getLog(AnalyticsTest.class);

    // Context
    protected static ApplicationContext        TEST_CONTEXT;
    protected static CloudTestContext          CLOUD_CONTEXT;
    protected static RetryingTransactionHelper TRANSACTION_HELPER;
    protected static ScriptService             SCRIPT_SERVICE;
    
    // Alfresco Services
    private static RegistrationService registrationService;
    
    // List of test users
    private static ArrayList<CloudTestUser> testUsers = new ArrayList<CloudTestUser>();

    /**
     * Initialise various services required by the test.
     */
    @BeforeClass
    public static void initTestsContext() throws Exception
    {
        TEST_CONTEXT       = ApplicationContextHelper.getApplicationContext();
        SCRIPT_SERVICE     = (ScriptService) TEST_CONTEXT.getBean("scriptService");
        TRANSACTION_HELPER = (RetryingTransactionHelper) TEST_CONTEXT.getBean("retryingTransactionHelper");
        CLOUD_CONTEXT      = new CloudTestContext(TEST_CONTEXT);
        
        // Load Services
        registrationService = (RegistrationService)TEST_CONTEXT.getBean("registrationService");
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        String acmeTenant = CLOUD_CONTEXT.createTenantName("acme");
        String zebraTenant = CLOUD_CONTEXT.createTenantName("zebra");
        
        // Create some Test Users
        testUsers.add(new CloudTestUser(CLOUD_CONTEXT.createUserName("david", acmeTenant), 
                "David", "Doberman"));
        testUsers.add(new CloudTestUser(CLOUD_CONTEXT.createUserName("john", acmeTenant),
                "John", "Smith"));
        testUsers.add(new CloudTestUser(CLOUD_CONTEXT.createUserName("colin", zebraTenant),
                "Colin", "Cobra"));
        testUsers.add(new CloudTestUser(CLOUD_CONTEXT.createUserName("kathy", zebraTenant),
                "Kathy", "Jones"));
        
        // Create some test users and Tenants
        for (final CloudTestUser user : testUsers) 
        {
            // Create Users
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    Account account = registrationService.createUser(user.getEmail(),
                            user.getFirstName(), user.getLastName(), "password123");
                    
                    CLOUD_CONTEXT.addAccount(account);
                    CLOUD_CONTEXT.addUser(user.getEmail());
                    return null;
                }
            });     
        }
    }
    
    @AfterClass
    public static void cleanup()
    {
        // Clear all created accounts/users
        if (CLOUD_CONTEXT != null)
        {
            CLOUD_CONTEXT.cleanup();
        }
    }

    /**
     * Does a test to ensure code to call REST APIs for each service to ensure
     * they're working correctly
     * 
     * @throws Exception
     */
    @Test
    public void sendGetRequests() throws Exception
    {
        
        // Get list of initialized Analytics Data Services
        List<AnalyticsDataService> analyticsDataServices = SendAnalyticsRequest
                .getAnalyticsDataServices();

        // Check been has been properly initialized in Spring Config
        assertNotNull(analyticsDataServices);

        // Loop through available data services
        Iterator<AnalyticsDataService> iterator = analyticsDataServices
                .iterator();
        while (iterator.hasNext())
        {

            AnalyticsDataService dataService = iterator.next();

            // Only test enabled services
            if (dataService.isEnabled())
            {

                logger.debug("Testing Analytics Data Service: "
                        + dataService.getServiceName());

                // Test Properties
                AnalyticsProperties props = new AnalyticsProperties();
                props.put("type", "application/ppt");
                props.put("size", 255343333);
                props.put("isEdit", false);
                
                // Generate URL               
                String url = dataService.getRecordEventRequestUrl("test@test.com", 
                        AnalyticsEvent.UPLOAD_DOCUMENT.toString(), props);                
                
                // Do GET Request
                if (url != null) {
                    
                    GetMethod response = dataService.sendAnalyicsRequest(url);
                    // Test Status Code is 200
                    assertTrue(response.getStatusCode() == HttpStatus.SC_OK);

                    if (dataService.getServiceName().equals("MixPanel"))
                    {
                        try 
                        {
                            // Check a successful response was sent back from service
                            assertTrue(response.getResponseBodyAsString().equals("1"));
                        } catch (NullPointerException ne)
                        {
                            // Just log for now
                            logger.error("Null Pointer Exception", ne);
                        }
                        
                    }
                }
            }
        }
    }
    
    /**
     * Test to see that SimpleDB is working. NOTE this isn't really checking the code as its wrapped
     * in try/catches/Action but will test the libraries and connection are present and valid
     * 
     * @throws Exception
     */
    @Test
    public void sentSimpleDBRequest() throws Exception
    {
        // TODO - need to figure out how to run this as lots of properties to get from Config like SimpleDB Domain
    }

    /**
     * Test to record a range of events to test we can generate the types of
     * reports we require
     * 
     * @throws Exception
     */
    @Test
    public void sendRecordEvents() throws Exception
    {
        
        // Run for each of the users
        for (CloudTestUser user : testUsers)
        {
            
            // Initial Sign Up
            final String sourceUrl = this.getClass().getSimpleName() + ".alfresco.com";
            
            Analytics.record_Registration(user.getEmail(), "website", sourceUrl, "80.4.218.197",
                    1320323663L, "http://www.google.com", "/", "alfresco cloud",
                    "", "", "", "", "");
            
            // Registration
            Analytics.record_Activation(user.getEmail());
            // Personal Private Site Created
            Analytics.record_createSite("collaboration", SiteVisibility.PRIVATE);
            
            // Run as fully authenticated User for rest of requests
            AuthenticationUtil.setFullyAuthenticatedUser(user.getEmail());
            
            // User logs in
            Analytics.record_login(user.getEmail());
            
            // User creates a site
            Analytics.record_createSite("collaboration", SiteVisibility.PUBLIC);
            
            // Upload a Document
            Analytics.record_UploadDocument("application/xls", 3223222, false);
            // Edit a Document
            Analytics.record_UploadDocument("application/ppt", 23223112, true);
            
            // Send some Site Invites
            Analytics.record_SiteInvite(false, false);
            Analytics.record_SiteInvite(false, true);
            Analytics.record_SiteInvite(true, true);
            Analytics.record_SiteInvite(true, false);
            
            // Test Site Invite Responses  
            Analytics.record_SiteInviteResponse(user.getEmail(), Analytics.SiteInviteResponse.ACCEPTED, 
                    false, false);
            Analytics.record_SiteInviteResponse(user.getEmail(), Analytics.SiteInviteResponse.ACCEPTED, 
                    false, true);
            Analytics.record_SiteInviteResponse(user.getEmail(), Analytics.SiteInviteResponse.REJECTED, 
                    true, true);
            Analytics.record_SiteInviteResponse(user.getEmail(), Analytics.SiteInviteResponse.IGNORED, 
                    true, false);
            
            // Set Authentication Context to Admin again
            AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        }
    }

    @Test
    public void sendSetProperties() throws Exception
    {

    }
    
    /**
     * Call a JavaScript script to ensure the Script API is functional.
     */
    @Test public void javascriptApi() throws Exception
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        ScriptLocation location = new ClasspathScriptLocation("org/alfresco/module/org_alfresco_module_cloud/analytics/scripts/test_analyticsService.js");
        SCRIPT_SERVICE.executeScript(location, model);
    }
}
