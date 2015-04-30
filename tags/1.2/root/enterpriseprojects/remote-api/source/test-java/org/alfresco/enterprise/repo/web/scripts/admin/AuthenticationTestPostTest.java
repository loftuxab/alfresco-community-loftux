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
public class AuthenticationTestPostTest extends BaseEnterpriseWebScriptTest
{
    // Miscellaneous constants used throughout this test class.
    private static final String POST_AUTHENTICATION_URL = "/enterprise/admin/admin-authentication-test";
    
    private static Log logger = LogFactory.getLog(AuthenticationTestPostTest.class);
    
    @Override protected void setUp() throws Exception
    {
        super.setUp();
        AuthenticationComponent authenticationComponent = (AuthenticationComponent)getServer().getApplicationContext().getBean("authenticationComponent");
        authenticationComponent.setSystemUserAsCurrentUser();
    }
    
    @Override protected void tearDown() throws Exception
    {
    }
    
    public void testNoAuthenticator() throws Exception
    {
        String url = POST_AUTHENTICATION_URL;
        
        PostRequest req = new PostRequest(url, "{}", "application/json");
        
        JSONObject body = new JSONObject();
        body.put("userName", "inewton"); 
        body.put("password", "password"); 
        sendRequest(new PostRequest(url,  body.toString(), "application/json"), Status.STATUS_BAD_REQUEST);           
    }
    
    public void testNoUser() throws Exception
    {
        String url = POST_AUTHENTICATION_URL;
        
        PostRequest req = new PostRequest(url, "{}", "application/json");
        
        JSONObject body = new JSONObject();
        body.put("authenticatorName", "ldap1"); 
        body.put("password", "password"); 
        sendRequest(new PostRequest(url,  body.toString(), "application/json"), Status.STATUS_BAD_REQUEST);           
    }
    
    /*
     * Disabled since it needs LDAP connection 
     */
    public void DISABLED_testLDAP1() throws Exception
    {
        String url = POST_AUTHENTICATION_URL;
                
        JSONObject body = new JSONObject();
        body.put("authenticatorName", "ldap1"); 
        body.put("userName", "inewton"); 
        body.put("password", "password"); 
        Response resp = sendRequest(new PostRequest(url,  body.toString(), "application/json"), Status.STATUS_OK);   
            
        String contentAsString = resp.getContentAsString();
        
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        
        logger.debug(contentAsString);
    }
    
    /*
     * Disabled since it needs LDAP connection
     */
    public void DISABLED_testLDAP1WrongPassword() throws Exception
    {
        String url = POST_AUTHENTICATION_URL;
                
        JSONObject body = new JSONObject();
        body.put("authenticatorName", "ldap1"); 
        body.put("userName", "inewton"); 
        body.put("password", "wrongpassword"); 
        Response resp = sendRequest(new PostRequest(url,  body.toString(), "application/json"), Status.STATUS_OK);   
            
        String contentAsString = resp.getContentAsString();
        
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        
        logger.debug(contentAsString);
    }
}