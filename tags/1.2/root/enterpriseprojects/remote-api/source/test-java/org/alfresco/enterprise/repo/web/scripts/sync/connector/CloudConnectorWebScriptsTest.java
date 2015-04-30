/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.connector;

import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.enterprise.repo.sync.connector.impl.CloudConnectorServiceImpl;
import org.alfresco.enterprise.repo.web.scripts.BaseEnterpriseWebScriptTest;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.remoteconnector.LocalWebScriptConnectorServiceImpl;
import org.alfresco.repo.remoteticket.RemoteAlfrescoTicketServiceImpl;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.remoteticket.RemoteAlfrescoTicketService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.util.PropertyMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.TestWebScriptServer.Request;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * Tests for the various Cloud Connector WebScripts, such as Cloud
 *  Credentials CRUD and Cloud Proxy Pass-Through
 * 
 * @author Nick Burch
 * @since TODO
 */
public class CloudConnectorWebScriptsTest extends BaseEnterpriseWebScriptTest
{
    private static final String CREDENTIALS_URL = "/cloud/person/credentials";
    private static final String TEST_REAL_URL =  "/test/real/person";
    private static final String TEST_PROXY_URL = "/test/proxy/person";

    private static Log logger = LogFactory.getLog(CloudConnectorWebScriptsTest.class);
    
    private MutableAuthenticationService authenticationService;
    private PersonService personService;
    
    private CloudConnectorService cloudConnectorService;
    private RemoteAlfrescoTicketService remoteAlfrescoTicketService;
    
    private static final String USER_ONE = "UserOneSecondToo";
    private static final String USER_TWO = "UserTwoSecondToo";
    private static final String USER_THREE = "UserThreeStill";
    private static final String PASSWORD = "passwordTEST";
    
    // General methods

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        AbstractRefreshableApplicationContext ctx = (AbstractRefreshableApplicationContext)getServer().getApplicationContext();
        this.authenticationService = (MutableAuthenticationService)ctx.getBean("AuthenticationService");
        this.personService = (PersonService)ctx.getBean("PersonService");
        
        this.cloudConnectorService = (CloudConnectorService)ctx.getBean("cloudConnectorService");
        this.remoteAlfrescoTicketService = (RemoteAlfrescoTicketService)ctx.getBean("remoteAlfrescoTicketService");
        
        // Do the setup as admin
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
        
        // Wire up the loop-back connector
        ((RemoteAlfrescoTicketServiceImpl)remoteAlfrescoTicketService).setRemoteConnectorService(
                new LocalWebScriptConnectorServiceImpl(this));
        ((CloudConnectorServiceImpl)cloudConnectorService).setRemoteConnectorService(
                new LocalWebScriptConnectorServiceImpl(this));
        
        // Configure the cloud connector for loopback
        ((CloudConnectorServiceImpl)cloudConnectorService).setCloudBaseUrl(
                LocalWebScriptConnectorServiceImpl.LOCAL_SERVICE_URL);
        
        // Create users
        createUser(USER_ONE);
        createUser(USER_TWO);
        createUser(USER_THREE);

        // Do tests as first user
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        
        // Admin user required to delete user
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
        
        // Delete users, and their credentials, and their tickets
        for (String user : new String[] {USER_ONE, USER_TWO, USER_THREE})
        {
            // Delete credentials, as them
            AuthenticationUtil.setFullyAuthenticatedUser(user);
            cloudConnectorService.deleteCloudCredentials();
            
            // Delete the user, as admin
            AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
            if(this.personService.personExists(user))
            {
               this.personService.deletePerson(user);
            }
            if(this.authenticationService.authenticationExists(user))
            {
               this.authenticationService.deleteAuthentication(user);
            }
        }
    }
    
    private void createUser(String userName)
    {
        // Make sure a new user is created every time
        // This ensures there are no credentials for them already existing
        //  which might confuse things later in the test
        if(this.personService.personExists(userName))
        {
           this.personService.deletePerson(userName);
        }
        if(this.authenticationService.authenticationExists(userName))
        {
           this.authenticationService.deleteAuthentication(userName);
        }
        
        
        // Create a fresh user
        this.authenticationService.createAuthentication(userName, PASSWORD.toCharArray());

        // Create the person properties
        PropertyMap personProps = new PropertyMap();
        personProps.put(ContentModel.PROP_USERNAME, userName);
        personProps.put(ContentModel.PROP_FIRSTNAME, "First");
        personProps.put(ContentModel.PROP_LASTNAME, "Last");
        personProps.put(ContentModel.PROP_EMAIL, "FirstName123.LastName123@email.com");
        personProps.put(ContentModel.PROP_JOBTITLE, "JobTitle123");
        personProps.put(ContentModel.PROP_JOBTITLE, "Organisation123");

        // Create person node for user
        this.personService.createPerson(personProps);

        // Check they're really there now
        assertEquals(true, this.personService.personExists(userName));
        assertNotNull(this.personService.getPerson(userName));
    }
    
    private JSONObject asJSON(Response response) throws Exception
    {
        String json = response.getContentAsString();
        if (json == null || json.length() == 0)
        {
            throw new IllegalArgumentException("Expected JSON but got an empty response: " + response);
        }
        
        JSONParser p = new JSONParser();
        Object o = p.parse(json);
        
        if (o instanceof JSONObject)
        {
            return (JSONObject)o; 
        }
        throw new IllegalArgumentException("Expected JSONObject, got " + o + " from " + json);
    }
    private Request buildGetCredentialsRequest()
    {
        Request req = new Request("GET", CREDENTIALS_URL);
        req.setType(MimetypeMap.MIMETYPE_JSON);
        return req;
    }
    private Request buildDeleteCredentialsRequest()
    {
        Request req = new Request("DELETE", CREDENTIALS_URL);
        return req;
    }
    @SuppressWarnings("unchecked")
    private Request buildPostCredentialsRequest(String username, String password)
    {
        Request req = new Request("POST", CREDENTIALS_URL);
        req.setType(MimetypeMap.MIMETYPE_JSON);
        
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        byte[] body = json.toJSONString().getBytes();
        
        logger.debug("Will record credentials of " + username + " / " + password);
        
        req.setBody(body);
        return req;
    }
    private Request buildTestPersonDetailsRequest(boolean proxy)
    {
        String url = TEST_PROXY_URL;
        if (! proxy)
            url = TEST_REAL_URL;
        
        logger.debug("Fetching person details, proxy=" + proxy);
        
        Request req = new Request("TESTING", url);
        req.setType(MimetypeMap.MIMETYPE_JSON);
        return req;
    }
    
    /**
     * Getting, storing fetching, and deleting credentials
     */
    public void testGetStoreDeleteCredentials() throws Exception
    {
        // Run this test initially as the first user
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);
        JSONObject json;
        
        // Run this test with the authentication from the test on the remote end
        setCustomAuthenticatorFactory(null);
        getServer().setServletAuthenticatorFactory(null);
        
        
        // Initially they should have no credentials
        json = asJSON( sendRequest(buildGetCredentialsRequest(), Status.STATUS_OK) );
        assertEquals(Boolean.FALSE, json.get("known"));
        
        // Can't delete when nothing there
        sendRequest(buildDeleteCredentialsRequest(), Status.STATUS_BAD_REQUEST);
        
        
        // Save some
        json = asJSON( sendRequest(buildPostCredentialsRequest(USER_TWO, PASSWORD), Status.STATUS_OK) );
        assertEquals(Boolean.TRUE, json.get("loginValid"));
        assertEquals(USER_TWO, json.get("username"));
        
        // They should now show up
        json = asJSON( sendRequest(buildGetCredentialsRequest(), Status.STATUS_OK) );
        assertEquals(Boolean.TRUE, json.get("known"));
        assertEquals(USER_TWO, json.get("username"));
        
        
        // Update them again
        json = asJSON( sendRequest(buildPostCredentialsRequest(USER_THREE, PASSWORD), Status.STATUS_OK) );
        assertEquals(Boolean.TRUE, json.get("loginValid"));
        assertEquals(USER_THREE, json.get("username"));
        
        // New ones seen
        json = asJSON( sendRequest(buildGetCredentialsRequest(), Status.STATUS_OK) );
        assertEquals(Boolean.TRUE, json.get("known"));
        assertEquals(USER_THREE, json.get("username"));
        
        
        // If we try to send invalid details, we get an error
        json = asJSON( sendRequest(buildPostCredentialsRequest(USER_TWO, "invalid"), Status.STATUS_OK) );
        assertEquals(Boolean.FALSE, json.get("loginValid"));
        
        // Old ones remain, invalid ones didn't overwrite
        json = asJSON( sendRequest(buildGetCredentialsRequest(), Status.STATUS_OK) );
        assertEquals(Boolean.TRUE, json.get("known"));
        assertEquals(USER_THREE, json.get("username"));
        
        
        // Flip to another user, they won't see these
        AuthenticationUtil.setFullyAuthenticatedUser(USER_THREE);
        json = asJSON( sendRequest(buildGetCredentialsRequest(), Status.STATUS_OK) );
        assertEquals(Boolean.FALSE, json.get("known"));

        // Back and they return
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);
        json = asJSON( sendRequest(buildGetCredentialsRequest(), Status.STATUS_OK) );
        assertEquals(Boolean.TRUE, json.get("known"));
        assertEquals(USER_THREE, json.get("username"));
        
        
        // Delete them
        sendRequest(buildDeleteCredentialsRequest(), Status.STATUS_OK);
        
        // Gone
        json = asJSON( sendRequest(buildGetCredentialsRequest(), Status.STATUS_OK) );
        assertEquals(Boolean.FALSE, json.get("known"));
        
        // Can't double delete
        sendRequest(buildDeleteCredentialsRequest(), Status.STATUS_BAD_REQUEST);
    }
    
    /**
     * Checks that we can do the proxy passthrough to the cloud
     *  with the current user's cloud credentials
     */
    public void testCloudPassThrough() throws Exception
    {
        // Run this test initially as the first user
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);
        JSONObject json;
        
        // No credentials will exist for the user to start with
        assertEquals(null, cloudConnectorService.getCloudCredentials());
        logger.debug("Confirmed that no Cloud Credentials exist");

        
        // Fetch the real thing, to check that works
        json = asJSON( sendRequest(buildTestPersonDetailsRequest(false), Status.STATUS_OK, USER_ONE) );
        assertEquals(USER_ONE, json.get("username"));
        
        
        // Fetch it via the proxy, as a user without credentials, check we're told to get lost
        sendRequest(buildTestPersonDetailsRequest(true), Status.STATUS_FORBIDDEN, USER_ONE);
        logger.debug("Fetch is not allowed via the proxy with no Cloud Credentials");

        
        // Store some credentials, check they're stored
        json = asJSON( sendRequest(buildPostCredentialsRequest(USER_THREE, PASSWORD), Status.STATUS_OK, USER_ONE) );
        assertEquals(Boolean.TRUE, json.get("loginValid"));
        assertEquals(USER_THREE, json.get("username"));
        logger.debug("Cloud Credentials have been POSTed for Storage");
        
        // Some credentials should have been set
        logger.debug("Credentials stored are " + cloudConnectorService.getCloudCredentials());
        assertNotNull(cloudConnectorService.getCloudCredentials());
        logger.debug("Cloud Credentials are now " + cloudConnectorService.getCloudCredentials());

        
        // Perform the fetch, check we now see the details for the user we set
        json = asJSON( sendRequest(buildTestPersonDetailsRequest(true), Status.STATUS_OK, USER_ONE) );
        assertEquals(USER_THREE, json.get("username"));
        logger.debug("Credentials test webscript reports " + json.toString());
        
        
        // Change the credentials and re-fetch, will change
        json = asJSON( sendRequest(buildPostCredentialsRequest(USER_TWO, PASSWORD), Status.STATUS_OK) );
        assertEquals(Boolean.TRUE, json.get("loginValid"));
        assertEquals(USER_TWO, json.get("username"));
        logger.debug("Credentials changed to " + cloudConnectorService.getCloudCredentials());

        json = asJSON( sendRequest(buildTestPersonDetailsRequest(true), Status.STATUS_OK) );
        assertEquals(USER_TWO, json.get("username"));
        
        
        // Store credentials and fetch as a third user, check we see our own things, not User One's
        AuthenticationUtil.setFullyAuthenticatedUser(USER_THREE);
        sendRequest(buildTestPersonDetailsRequest(true), Status.STATUS_FORBIDDEN);
        
        json = asJSON( sendRequest(buildPostCredentialsRequest(USER_TWO, PASSWORD), Status.STATUS_OK) );
        assertEquals(Boolean.TRUE, json.get("loginValid"));
        assertEquals(USER_TWO, json.get("username"));

        json = asJSON( sendRequest(buildTestPersonDetailsRequest(true), Status.STATUS_OK) );
        assertEquals(USER_TWO, json.get("username"));
        
        
        // Zap credentials and tickets, check it stops working again
        sendRequest(buildDeleteCredentialsRequest(), Status.STATUS_OK);
        sendRequest(buildTestPersonDetailsRequest(true), Status.STATUS_FORBIDDEN);
    }
}
