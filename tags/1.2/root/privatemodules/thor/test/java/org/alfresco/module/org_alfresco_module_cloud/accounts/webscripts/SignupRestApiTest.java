/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.analytics.Analytics;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.registration.Registration;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceImpl.INVALID_EMAIL_TYPE;
import org.alfresco.module.org_alfresco_module_cloud.util.EmailHelper.EmailRequest;
import org.alfresco.module.org_alfresco_module_cloud.util.EmailHelper.EmailTestStorage;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.extensions.webscripts.TestWebScriptServer.DeleteRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Request;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * This class tests the REST API for the account self-signup workflow.
 * 
 * @author Neil McErlean
 * @since Cloud
 */
public class SignupRestApiTest extends BaseWebScriptTest
{
    private static final Log log = LogFactory.getLog(SignupRestApiTest.class);
    
    // Miscellaneous constants.
    private final static String APPLICATION_JSON = "application/json";
    
    // URLs and URL fragments used in this REST API.
    private final static String POST_ACCOUNT_SIGNUP_QUEUE_URL = "/internal/cloud/accounts/signupqueue";
    private final static String POST_ACCOUNT_INITIATED_SIGNUP_QUEUE_URL = "/internal/cloud/accounts/initiatedsignupqueue";
    private final static String POST_ACCOUNT_ACTIVATION_URL = "/internal/cloud/account-activations";
    private final static String GET_SIGNUP_STATUS_URL = "/internal/cloud/accounts/signupqueue/{id}?key={key}";
    private final static String POST_LOGIN_URL = "/api/login?u={u}&pw={pw}";

    private final static String SIGNUP_SOURCE = "unittest-" + SignupRestApiTest.class.getSimpleName();
    
    private final static String PASSWORD = "password";
    private final static String FIRST_NAME = "FN";
    private final static String LAST_NAME = "LN";

    private AccountService            accountService;
    private HistoryService            activitiHistoryService;
    private EmailAddressService       emailService;
    private NodeService               nodeService;
    private RegistrationService       registrationService;
    private RetryingTransactionHelper transactionHelper;

    /**
     * This list holds any workflows started during a test method.
     */
    private CloudTestContext cloudContext;
    private String T1;
    private String T2;
    private String T3;
    private String U1T1;
    private String U2T1;
    private String U3T1;
    
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        cloudContext = new CloudTestContext(this);
        T1 = cloudContext.createTenantName("acme");
        T2 = cloudContext.createTenantName("rush");
        T3 = cloudContext.createTenantName("iron");
        U1T1 = cloudContext.createUserName("joe.bloggs", T1);
        U2T1 = cloudContext.createUserName("fred.bloggs", T1);
        U3T1 = cloudContext.createUserName("jane.o'reilly", T1);
        
        // We need some back-end services in this test case to do the workflow tidyup, which isn't
        // currently possible over the REST API.
        accountService = (AccountService) cloudContext.getApplicationContext().getBean("AccountService");
        activitiHistoryService = (HistoryService) cloudContext.getApplicationContext().getBean("activitiHistoryService");
        emailService = (EmailAddressService) cloudContext.getApplicationContext().getBean("emailAddressService");
        nodeService = (NodeService)cloudContext.getApplicationContext().getBean("NodeService");
        registrationService = (RegistrationService)cloudContext.getApplicationContext().getBean("RegistrationService");
        transactionHelper = (RetryingTransactionHelper)cloudContext.getApplicationContext().getBean("retryingTransactionHelper");
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
    }
    
    @Override
    public void tearDown() throws Exception
    {
        cloudContext.cleanup();
    }
    
    /**
     * This method tests that a selection of illegal email addresses are rejected by the REST API.
     */
    public void testSignUpForAccount_MalformedEmails() throws Exception
    {
        String[] variousIllegalEmails = new String[] {"notAnEmail", "nodomain@", "@nouser"};
        
        for (String illegalEmail : variousIllegalEmails)
        {
            postCreateAccountCall(illegalEmail, null, null, null, SIGNUP_SOURCE, 400);
        }
        
        assertEquals("Wrong number of emails sent", 0, cloudContext.getEmailTestStorage().getEmailCount());
    }
    
    /**
     * This method tests that a blocked email address is silently rejected by signup workflow.
     */
    public void XtestSignUpForAccount_BlockedEmail() throws Exception
    {
        String blockedPublicEmail = "person.person@gmail.com";
        postCreateAccountCall(blockedPublicEmail, null, null, null, SIGNUP_SOURCE, 200);
        
        assertFalse("Blocked email was on registered list.", registrationService.isRegisteredEmailAddress(blockedPublicEmail));
    }
    
    public void testSignupNegativeFlows() throws Exception
    {
        // Initially no pending signup requests.
        assertFalse(registrationService.isRegisteredEmailAddress(U1T1));
        
        // Now POST an email address to initiate self signup
        Pair<String, String> idKey = postCreateAccountCall(U1T1, null, null, null, SIGNUP_SOURCE, 200);
        cloudContext.addRegistration(new Pair<String, String>(idKey.getFirst(), idKey.getSecond()));
        
        // A bad key should give a 404
        postActivationRequest(idKey.getFirst(), "rubbish", FIRST_NAME, LAST_NAME, PASSWORD, 404);
        
        // A bad id should give a 404
        postActivationRequest("rubbish", idKey.getSecond(), FIRST_NAME, LAST_NAME, PASSWORD, 404);
        
        // We'll activate the account in the normal way
        postActivationRequest(idKey.getFirst(), idKey.getSecond(), FIRST_NAME, LAST_NAME, PASSWORD, 200);
        
        // A second attempt to activate an already-activated account should give a 404
        postActivationRequest(idKey.getFirst(), idKey.getSecond(), FIRST_NAME, LAST_NAME, PASSWORD, 404);
    }
    
    public void testSignUpForAccount_NewUserNewAccount() throws Exception
    {
        // Initially no pending signup requests.
        assertFalse(registrationService.isRegisteredEmailAddress(U1T1));
        
        // Now POST an email address to initiate self signup
        Pair<String, String> idKey = postCreateAccountCall(U1T1, null, null, null, SIGNUP_SOURCE, 200);
        
        // One email should have been sent
        EmailTestStorage emailTestStorage = cloudContext.getEmailTestStorage();
        assertEquals("Wrong number of emails sent", 1, emailTestStorage.getEmailCount());
        final EmailRequest emailReq = emailTestStorage.getEmailRequest(0);
        
        // Was the right email sent?
        assertTrue("Wrong email template used for signup-request", emailReq.getTemplateRef().endsWith("self-signup-requested-email.ftl"));
        
        cloudContext.addRegistration(new Pair<String, String>(idKey.getFirst(), idKey.getSecond()));
        
        // Get pending signup requests per email.
        assertTrue("email address was not registered", registrationService.isRegisteredEmailAddress(U1T1));
        
        
        // Now, before we go ahead and activate this email/username, we'll send the workflow through the reminder email loop.
        registrationService.resendActivationRequest(idKey.getFirst(), idKey.getSecond());
        assertEquals("Wrong number of emails sent", 2, emailTestStorage.getEmailCount());
        final EmailRequest emailReqResentEmail = emailTestStorage.getEmailRequest(1);
        
        // Was the right email sent?
        assertTrue("Wrong email template used for re-sent signup-request", emailReqResentEmail.getTemplateRef().endsWith("self-signup-requested-email.ftl"));
        
        
        // Then POST an 'activation' request, but only to 1 address.
        postActivationRequest(idKey.getFirst(), idKey.getSecond(), FIRST_NAME, LAST_NAME, PASSWORD, 200);
        
        // Some final checks of the back-end Accounts and Users.
        final String emailDomain = emailService.getDomain(U1T1);
        Account account = accountService.getAccountByDomain(emailDomain);
        assertNotNull("Account was null.", account);
        
        cloudContext.addAccountDomain(emailDomain);
        cloudContext.addUser(U1T1);
        
        // Again POST an email address to initiate self signup - this should result in a different email
        postCreateAccountCall(U1T1, null, null, null, SIGNUP_SOURCE, 200);
        
        assertEquals("Wrong number of emails sent", 3, emailTestStorage.getEmailCount());
        final EmailRequest emailReqAlreadySignedUp = emailTestStorage.getEmailRequest(2);
        
        // Was the right email sent?
        assertTrue("Wrong email template used for repeat signup-request", emailReqAlreadySignedUp.getTemplateRef().endsWith("self-signup-already-registered-email.ftl"));
        
        
        // The workflow should be ended.
        assertFalse(registrationService.isRegisteredEmailAddress(U1T1));
        cloudContext.removeRegistration(new Pair<String, String>(idKey.getFirst(), idKey.getSecond()));
    }
    
    /**
     * This method tests that an email can be registered along with the various analytics data that we support.
     * The account is not activated. The analytics data are not validated, they are simply sent through the stack
     * and so the test only ensures that exceptions are not thrown.
     */
    public void testRegisterAnEmailWithAnalyticsData() throws Exception
    {
        // Initially no pending signup requests.
        assertFalse(registrationService.isRegisteredEmailAddress(U1T1));
        
        // Now POST an email address to initiate self signup - with analytics data.
        Map<String, Serializable> analyticsData = new HashMap<String, Serializable>();
        analyticsData.put(AccountSignupPost.PARAM_IP_ADDRESS,       "100.100.100.100");
        analyticsData.put(AccountSignupPost.PARAM_LANDING_TIME,     123456789L);
        analyticsData.put(AccountSignupPost.PARAM_LANDING_REFERRER, "test-referrer");
        analyticsData.put(AccountSignupPost.PARAM_LANDING_PAGE,     "test-page");
        analyticsData.put(AccountSignupPost.PARAM_LANDING_KEYWORDS, (Serializable)Arrays.asList(new String[]{"key1", "key1", "key3"}));
        analyticsData.put(AccountSignupPost.PARAM_UTM_SOURCE,       "test-utmsource");
        analyticsData.put(AccountSignupPost.PARAM_UTM_MEDIUM,       "test-utmmedium");
        analyticsData.put(AccountSignupPost.PARAM_UTM_TERM,         "test-utmterm");
        analyticsData.put(AccountSignupPost.PARAM_UTM_CONTENT,      "test-utmcontent");
        analyticsData.put(AccountSignupPost.PARAM_UTM_CAMPAIGN,     "test-utmcampaign");
        
        Pair<String, String> idKey = postCreateAccountCall(U1T1, null, null, null, SIGNUP_SOURCE, analyticsData, 200);
        
        cloudContext.addRegistration(new Pair<String, String>(idKey.getFirst(), idKey.getSecond()));
    }
    
    /**
     * This test method ensures that if a user (or more correctly a potential user) signs up for
     * an account with a given email address, does NOT activate that account, and then tries to
     * sign up with the same email address again, that the first pending registration workflow instance will
     * be reused.
     */
    public void testSigningUpForSameAccountTwiceShouldReuseWorkflowInstance() throws Exception
    {
        // Initially no pending signup requests.
        assertFalse(registrationService.isRegisteredEmailAddress(U1T1));
        
        // Now POST an email address to initiate self signup
        Pair<String, String> idKey = postCreateAccountCall(U1T1, null, null, null, SIGNUP_SOURCE, 200);
        
        // One email should have been sent
        EmailTestStorage emailTestStorage = cloudContext.getEmailTestStorage();
        assertEquals("Wrong number of emails sent", 1, emailTestStorage.getEmailCount());
        final EmailRequest emailReq = emailTestStorage.getEmailRequest(0);
        
        // Was the right email sent?
        assertTrue("Wrong email template used for signup-request", emailReq.getTemplateRef().endsWith("self-signup-requested-email.ftl"));
        
        // Ensure that the tests clean up properly.
        cloudContext.addRegistration(new Pair<String, String>(idKey.getFirst(), idKey.getSecond()));
        
        // Now post the same email address again.
        Pair<String, String> secondIdKey = postCreateAccountCall(U1T1, null, null, null, SIGNUP_SOURCE, 200);
        assertEquals("Signup workflow idKey should not have changed", idKey, secondIdKey);
        
        // This should trigger the sending of a new email inviting the user to activate their account
        // One email should have been sent
        assertEquals("Wrong number of emails sent", 2, emailTestStorage.getEmailCount());
        
        // We're not interested in how this workflow proceeds from this point as that is covered by other tests.
        // Also the TestContext tearDown should ensure that it is cleaned up, so there's nothing else to do here.
    }
    
    public void testGetSignupStatus() throws Exception
    {
        // Now POST an email address to initiate self signup
        Pair<String, String> idKey1 = postCreateAccountCall(U1T1, null, null, null, SIGNUP_SOURCE, 200);
        Pair<String, String> idKey2 = postCreateAccountCall(U2T1, null, null, null, SIGNUP_SOURCE, 200);
        
        cloudContext.addRegistration(idKey1);
        cloudContext.addRegistration(idKey2);
        
        // Activate the account
        postActivationRequest(idKey1.getFirst(), idKey1.getSecond(), FIRST_NAME, LAST_NAME, PASSWORD, 200);
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            public Void execute() throws Throwable
            {
                // We should now have one registered and one activated.
                assertFalse(registrationService.isRegisteredEmailAddress(U1T1));
                assertTrue(registrationService.isRegisteredEmailAddress(U2T1));
                
                assertTrue(registrationService.isActivatedEmailAddress(U1T1));
                assertFalse(registrationService.isActivatedEmailAddress(U2T1));
                
                return null;
            }
        }, false, true);
        
        // Do the invitation status webscripts agree?
        String url1 = GET_SIGNUP_STATUS_URL.replace("{id}", idKey1.getFirst()).replace("{key}", idKey1.getSecond());
        String url2 = GET_SIGNUP_STATUS_URL.replace("{id}", idKey2.getFirst()).replace("{key}", idKey2.getSecond());
        sendRequest(new GetRequest(url1), 404);
        
        Response statusRsp2 = sendRequest(new GetRequest(url2), 200);
        JSONObject statusRsp2Json = (JSONObject) JSONValue.parse(statusRsp2.getContentAsString());
        
        assertTrue((Boolean) statusRsp2Json.get("isRegistered"));
        assertFalse((Boolean) statusRsp2Json.get("isActivated"));
    }
    
    public void testGetSignupStatusNegativeCases() throws Exception
    {
        // Register a single email address
        Pair<String, String> idKey = postCreateAccountCall(U1T1, null, null, null, SIGNUP_SOURCE, 200);
        
        cloudContext.addRegistration(idKey);
        
        // If we give the correct workflow id and wrong id, we return a 404.
        String url = GET_SIGNUP_STATUS_URL.replace("{id}", idKey.getFirst()).replace("{key}", "rubbish");
        sendRequest(new GetRequest(url), 404);
        
        // If workflow id is not recognised it's still a 404.
        url = GET_SIGNUP_STATUS_URL.replace("{id}", "rubbish").replace("{key}", "rubbish");
        sendRequest(new GetRequest(url), 404);
    }
    
    
    public void testAddExternalUser() throws Exception
    {
        // create user (with home account)
        registrationService.createUser(U1T1, "David", "Smith", "smithy");
        cloudContext.addUser(U1T1);
        cloudContext.addAccountDomain(T1);
        // create external account
        Account accountT2 = accountService.createAccount(T2, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
        cloudContext.addAccountDomain(T2);
        
        // add user to external account
        String url = "/internal/cloud/account/" + accountT2.getId() + "/users"; 
        Request req = new PostRequest(url, "", APPLICATION_JSON);
        Map<String, String> args = new HashMap<String, String>();
        args.put("userId", U1T1);
        req.setArgs(args);
        Response rsp = sendRequest(req, 200);
        
        // validate response (at least valid JSON)
        assertNotNull("invalid JSON", JSONValue.parse(rsp.getContentAsString()));
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    public void testRemoveInternalUser() throws Exception
    {
        // create user (with home account)
        Account accountT1 = registrationService.createUser(U1T1, "David", "Smith", "smithy");
        cloudContext.addUser(U1T1);
        cloudContext.addAccountDomain(T1);
        // create external account
        Account accountT2 = accountService.createAccount(T2, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
        cloudContext.addAccountDomain(T2);
        registrationService.addUser(accountT2.getId(), U1T1);

        // remove external user from their INTERNAL/HOME account 
        String url = "/internal/cloud/account/" + accountT1.getId() + "/users/" + U1T1; 
        Request req = new DeleteRequest(url);
        Response rsp = sendRequest(req, 200);
        
        // validate response (at least valid JSON)
        String contentAsString = rsp.getContentAsString();
        assertNotNull("invalid JSON", JSONValue.parse(contentAsString));
    }
    
    public void testRemoveExternalUser() throws Exception
    {
        // create user (with home account)
        registrationService.createUser(U1T1, "David", "Smith", "smithy");
        cloudContext.addUser(U1T1);
        cloudContext.addAccountDomain(T1);
        // create external account
        Account accountT2 = accountService.createAccount(T2, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
        cloudContext.addAccountDomain(T2);
        registrationService.addUser(accountT2.getId(), U1T1);

        // remove external user from account - this time by domain name
        String url = "/internal/cloud/domains/" + accountT2.getDomains().get(0) + "/account/users/" + U1T1; 

        Request req = new DeleteRequest(url);
        Response rsp = sendRequest(req, 200);
        
        // validate response (at least valid JSON)
        assertNotNull("invalid JSON", JSONValue.parse(rsp.getContentAsString()));
    }
    
    public void testUserAccounts() throws Exception
    {
        // create user (with home account)
        registrationService.createUser(U1T1, "David", "Smith", "smithy");
        cloudContext.addUser(U1T1);
        cloudContext.addAccountDomain(T1);
        // create external account
        Account accountT2 = accountService.createAccount(T2, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
        cloudContext.addAccountDomain(T2);
        registrationService.addUser(accountT2.getId(), U1T1);
        Account accountT3 = accountService.createAccount(T3, AccountType.FREE_NETWORK_ACCOUNT_TYPE, true);
        cloudContext.addAccountDomain(T3);
        registrationService.addUser(accountT3.getId(), U1T1);
        
        // get user accounts
        String url = "/internal/cloud/user/" + U1T1 + "/accounts"; 
        Request req = new GetRequest(url);
        Response rsp = sendRequest(req, 200);
        
        // validate response (at least valid JSON)
        assertNotNull("invalid JSON", JSONValue.parse(rsp.getContentAsString()));
    }

    public void testPreRegisteredSignup() throws Exception
    {
        // Initially no pending signup requests.
        assertFalse(registrationService.isRegisteredEmailAddress(U1T1));
        
        // Now POST an email address to initiate self signup
        Pair<String, String> idKey = postCreateAccountCall(U1T1, "Joe", "Bloggs", "pass123", SIGNUP_SOURCE, 200);

        cloudContext.addRegistration(new Pair<String, String>(idKey.getFirst(), idKey.getSecond()));
        
        String url = GET_SIGNUP_STATUS_URL.replace("{id}", idKey.getFirst()).replace("{key}", idKey.getSecond());
        Response rsp = sendRequest(new GetRequest(url), 200);
        String contentAsString = rsp.getContentAsString();
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        assertEquals(true, jsonRsp.get("isPreRegistered"));

        // Then POST an 'activation' request with incorrect password
        postActivationRequest(idKey.getFirst(), idKey.getSecond(), null, null, PASSWORD + "dodgy", 401);

        // Then POST an 'activation' request, with correct password
        postActivationRequest(idKey.getFirst(), idKey.getSecond(), null, null, PASSWORD, 200);
        
        // Some final checks of the back-end Accounts and Users.
        String emailDomain = emailService.getDomain(U1T1);

        cloudContext.addAccountDomain(emailDomain);
        cloudContext.addUser(U1T1);

        // The workflow should be ended.
        assertFalse(registrationService.isRegisteredEmailAddress(U1T1));
        cloudContext.removeRegistration(new Pair<String, String>(idKey.getFirst(), idKey.getSecond()));
    }
    
    public void testInviteSignup() throws Exception
    {
        // Create user (with home account)
        registrationService.createUser(U1T1, "David", "Smith", "smithy");
        cloudContext.addUser(U1T1);
        cloudContext.addAccountDomain(T1);

        final int initialEmailCount = cloudContext.getEmailTestStorage().getEmailCount();

        String message = "Message";
        String source = "Website";

        // Add two valid emails which one has an apostrophe (CLOUD-2184) in the user name, and an invalid email to invite
        final Response response = postInviteAccount(Arrays.asList(U2T1, U3T1, "invalidEmail"), message, source, U1T1, 200);

        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                assertNotNull(registrationService.getRegistration(U2T1));
                assertNotNull(registrationService.getRegistration(U3T1));
                assertEquals(initialEmailCount + 2, cloudContext.getEmailTestStorage().getEmailCount());

                // Response should report one invalid email-address
                JSONObject responseJson = (JSONObject) new JSONParser().parse(response.getContentAsString());
                assertNotNull(responseJson);

                JSONArray invalidEmails = (JSONArray) responseJson.get("invalidEmails");
                assertNotNull(invalidEmails);
                assertEquals(1L, invalidEmails.size());

                JSONObject invalidAddress = (JSONObject) invalidEmails.get(0);
                assertNotNull(invalidAddress);
                assertEquals("invalidEmail", invalidAddress.get("email"));
                assertEquals(INVALID_EMAIL_TYPE.INCORRECT_DOMAIN.toString(), invalidAddress.get("failureReason"));

                return null;
            }
        }, T1);
    }
    
    /**
     * @param emailToSignUp the email address being signed up.
     * @param source the source of the registration. See {@link Analytics#record_Registration} for details.
     * @param expectedStatus the expected HTTP response status of this call.
     */
    private Pair<String, String> postCreateAccountCall(String emailToSignUp, String firstName, String lastName, String password,
    		String source, int expectedStatus) throws IOException, UnsupportedEncodingException
    {
        Map<String, Serializable> emptyMap = Collections.emptyMap();
        return postCreateAccountCall(emailToSignUp, firstName, lastName, password, source, emptyMap, expectedStatus);
    }
    
    /**
     * @param emailToSignUp the email address being signed up.
     * @param source the source of the registration. See {@link Analytics#record_Registration(String, String, String, long, String, String, String, String, String, String, String, String)} for details.
     * @param analyticsParams optional extra analytics params. See {@link Analytics#record_Registration(String, String, String, long, String, String, String, String, String, String, String, String)} for details.
     * @param expectedStatus the expected HTTP response status of this call.
     */
    @SuppressWarnings("unchecked")
    private Pair<String, String> postCreateAccountCall(String emailToSignUp, String firstName, String lastName, String password, String source,
            Map<String, Serializable> analyticsParams, int expectedStatus) throws IOException, UnsupportedEncodingException
    {
        JSONObject obj = new JSONObject();
        obj.put("email", emailToSignUp);
        obj.put("source", source);
        
        if(firstName != null)
        {
        	obj.put("firstName", firstName);
        }
        
        if(lastName != null)
        {
        	obj.put("lastName", lastName);
        }
        
        if(password != null)
        {
        	obj.put("password", PASSWORD);
        }
        
        // Add optional analytics properties.
        for (Map.Entry<String, Serializable> entry : analyticsParams.entrySet())
        {
            if (entry.getValue() instanceof List)
            {
                List listValue = (List)entry.getValue();
                JSONArray jsonArray = new JSONArray();
                for (Iterator iter = listValue.iterator(); iter.hasNext(); )
                {
                    jsonArray.add(iter.next());
                }
                obj.put(entry.getKey(), jsonArray);
            }
            obj.put(entry.getKey(), entry.getValue());
        }
        
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();
        
        Response rsp = sendRequest(new PostRequest(POST_ACCOUNT_SIGNUP_QUEUE_URL, jsonString, APPLICATION_JSON), expectedStatus);
        
        if (expectedStatus == 200)
        {
            String contentAsString = rsp.getContentAsString();
            
            JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
            assertNotNull("Problem reading JSON", jsonRsp);
            
            JSONObject registrationRsp = (JSONObject) jsonRsp.get("registration");
            String email = (String) registrationRsp.get("email");
            assertEquals("Wrong email", emailToSignUp, email);

            Registration registration = registrationService.getRegistration(email);
            return (registration == null) ? new Pair<String, String>(null, null) : new Pair<String, String>(registration.getId(), registration.getKey());
        }
        else
        {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private void postActivationRequest(String id, String key, String firstName, String lastName, String password, int expectedStatus) throws IOException, UnsupportedEncodingException
    {
        JSONObject obj = new JSONObject();

        if(firstName != null)
        {
        	obj.put(AccountActivationPost.PARAM_FIRST_NAME, firstName);
        }
        
        if(lastName != null)
        {
        	obj.put(AccountActivationPost.PARAM_LAST_NAME,  lastName);
        }
        
        obj.put(AccountActivationPost.PARAM_PASSWORD,  password);
        obj.put(AbstractAccountSignupWebscript.PARAM_ID,  id);
        obj.put(AbstractAccountSignupWebscript.PARAM_KEY,  key);
       
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();

        sendRequest(new PostRequest(POST_ACCOUNT_ACTIVATION_URL, jsonString, APPLICATION_JSON), expectedStatus);
    }
    
    @SuppressWarnings("unchecked")
    private Response postInviteAccount(final List<String> emails, final String message, final String source, final String inviterEmail, final int expectedStatus) throws IOException, UnsupportedEncodingException
    {
        return TenantUtil.runAsSystemTenant(new TenantRunAsWork<Response>()
        {
            @Override
            public Response doWork() throws Exception
            {
                JSONObject obj = new JSONObject();
                JSONArray emailsArray = new JSONArray();
                obj.put(AccountInitiatedSignupPost.PARAM_EMAILS, emailsArray);
                for (String email : emails)
                {
                    emailsArray.add(email);
                }

                obj.put(AccountInitiatedSignupPost.PARAM_MESSAGE, message);
                obj.put(AccountInitiatedSignupPost.PARAM_SOURCE, source);

                StringWriter stringWriter = new StringWriter();
                obj.writeJSONString(stringWriter);
                String jsonString = stringWriter.toString();

                return sendRequest(new PostRequest(POST_ACCOUNT_INITIATED_SIGNUP_QUEUE_URL,
                            jsonString, APPLICATION_JSON), expectedStatus, inviterEmail);

            }
        }, T1);
    }
    
    // CLOUD-2184.
    public void testSignUpForAccount_NewUserWithApostropheInHisEmail_NewAccount() throws Exception
    {
        // Initially no pending signup requests.
        assertFalse(registrationService.isRegisteredEmailAddress(U3T1));
        
        // Now POST an email address to initiate self signup
        Pair<String, String> idKey = postCreateAccountCall(U3T1, null, null, null, SIGNUP_SOURCE, 200);
        
        // One email should have been sent
        EmailTestStorage emailTestStorage = cloudContext.getEmailTestStorage();
        assertEquals("Wrong number of emails sent", 1, emailTestStorage.getEmailCount());
        final EmailRequest emailReq = emailTestStorage.getEmailRequest(0);
        
        // Was the right email sent?
        assertTrue("Wrong email template used for signup-request", emailReq.getTemplateRef().endsWith("self-signup-requested-email.ftl"));
        
        cloudContext.addRegistration(new Pair<String, String>(idKey.getFirst(), idKey.getSecond()));
        
        // Get pending signup requests per email.
        assertTrue("email address was not registered", registrationService.isRegisteredEmailAddress(U3T1));
        
        
        // Now, before we go ahead and activate this email/username, we'll send the workflow through the reminder email loop.
        registrationService.resendActivationRequest(idKey.getFirst(), idKey.getSecond());
        assertEquals("Wrong number of emails sent", 2, emailTestStorage.getEmailCount());
        final EmailRequest emailReqResentEmail = emailTestStorage.getEmailRequest(1);
        
        // Was the right email sent?
        assertTrue("Wrong email template used for re-sent signup-request", emailReqResentEmail.getTemplateRef().endsWith("self-signup-requested-email.ftl"));
        
        // Then POST an 'activation' request, but only to 1 address.
        postActivationRequest(idKey.getFirst(), idKey.getSecond(), "jane", "o'reilly", PASSWORD, 200);
        
        // Some final checks of the back-end Accounts and Users.
        final String emailDomain = emailService.getDomain(U3T1);
        Account account = accountService.getAccountByDomain(emailDomain);
        assertNotNull("Account was null.", account);
        
        cloudContext.addAccountDomain(emailDomain);
        cloudContext.addUser(U3T1);
        
        // Again POST an email address to initiate self signup - this should result in a different email
        postCreateAccountCall(U3T1, null, null, null, SIGNUP_SOURCE, 200);
        
        assertEquals("Wrong number of emails sent", 3, emailTestStorage.getEmailCount());
        final EmailRequest emailReqAlreadySignedUp = emailTestStorage.getEmailRequest(2);
        
        // Was the right email sent?
        assertTrue("Wrong email template used for repeat signup-request", emailReqAlreadySignedUp.getTemplateRef().endsWith("self-signup-already-registered-email.ftl"));
        
        
        // The workflow should be ended.
        assertFalse(registrationService.isRegisteredEmailAddress(U3T1));
        cloudContext.removeRegistration(new Pair<String, String>(idKey.getFirst(), idKey.getSecond()));
    }
}
