/*
 * Copyright 2013-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.admin;

import org.alfresco.enterprise.repo.web.scripts.BaseEnterpriseWebScriptTest;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * @author Mark Rogers
 * @since 4.2
 */
public class UserRegistrySynchronizationTestPostTest extends BaseEnterpriseWebScriptTest
{
    // Miscellaneous constants used throughout this test class.
    private static final String POST_TEST_SYNC_URL = "/enterprise/admin/admin-user-sync-test";
    
    private static Log logger = LogFactory.getLog(UserRegistrySynchronizationTestPostTest.class);
    
    @Override protected void setUp() throws Exception
    {
        super.setUp();
        AuthenticationComponent authenticationComponent = (AuthenticationComponent)getServer().getApplicationContext().getBean("authenticationComponent");
        authenticationComponent.setSystemUserAsCurrentUser();
    }
    
    @Override protected void tearDown() throws Exception
    {
    }
    
    /**
     * A missing authenticatorName should result in a HTTP error
     * 
     * @throws Exception
     */
    public void testNoAuthenticator() throws Exception
    {
        String url = POST_TEST_SYNC_URL;
        
        PostRequest req = new PostRequest(url, "{}", "application/json");
        
        JSONObject body = new JSONObject();
        sendRequest(new PostRequest(url,  body.toString(), "application/json"), Status.STATUS_BAD_REQUEST);           
    }
    
    /**
     * A bad authenticator (one that does not exist) should still get a successful HTTP status but the failure should be
     * in the diagnostic
     * 
     * @throws Exception
     */
    public void testBadAuthenticator() throws Exception
    {
        String url = POST_TEST_SYNC_URL;
        
        PostRequest req = new PostRequest(url, "{}", "application/json");
        
        JSONObject body = new JSONObject();
        body.put("authenticatorName", "not-exist"); 
        body.put("maxItems", "100"); 
        Response resp = sendRequest(new PostRequest(url,  body.toString(), "application/json"), Status.STATUS_OK);    
        String contentAsString = resp.getContentAsString();
        
        // We should have a JSON response but the test should fail since the authenticator does not exist
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        assertTrue("testPassed not present", jsonRsp.containsKey("testPassed"));
        assertFalse("test has not failed", (Boolean)jsonRsp.get("testPassed"));  
    }
}