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
package org.alfresco.module.org_alfresco_module_cloud.users;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableUpdate;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.registration.Registration;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.registration.SendEmailDelegate;
import org.alfresco.module.org_alfresco_module_cloud.registration.WorkflowModelSelfSignup;
import org.alfresco.module.org_alfresco_module_cloud.util.EmailHelper.EmailRequest;
import org.alfresco.module.org_alfresco_module_cloud.util.EmailHelper.EmailTestStorage;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.alfresco.service.namespace.QName;
import org.alfresco.test_category.SharedJVMTestsCategory;
import org.alfresco.util.Pair;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.experimental.categories.Category;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * This class tests the REST API for the reset password workflow.
 * 
 * @author Neil McErlean
 * @since Alfresco Cloud Module (Thor)
 */
@Category(SharedJVMTestsCategory.class)
public class ResetPasswordRestApiTest extends BaseWebScriptTest
{
    // Miscellaneous constants.
    private final static String APPLICATION_JSON = "application/json";
    
    // URLs and URL fragments used in this REST API.
    private final static String REQUEST_PASSWORD_RESET_URL = "/internal/cloud/users/passwords/resetrequests";
    private final static String POST_CHANGED_PASSWORD_URL = "/internal/cloud/users/passwords";
    
    private HistoryService               activitiHistoryService;
    private MutableAuthenticationService authenticationService;
    private RegistrationService          registrationService;
    private WorkflowService              workflowService;
    
    /**
     * This list holds any workflows started during a test method.
     */
    private CloudTestContext cloudContext;
    
    private String unknownuser;
    private String registeredUser;
    private String activatedUser;
    private String activatedUserWithApostropheInHisEmail;

    /**
     * A regex to help extract ids and keys from URLs in these emails.
     * e.g. http://host:8081/share/path/to/Share/reset/password/script?key=c9fac744-f682-4ccb-b5bc-6f655beaee1e&id=activiti$651
     */
    private static final Pattern ID_AND_KEY_IN_RESET_PASSWORD_URL_PATTERN = Pattern.compile(".*?key=(.*)&id=(.*)$");
        
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        cloudContext = new CloudTestContext(this);
        
        String tenantName = cloudContext.createTenantName("forget");
        unknownuser = cloudContext.createUserName("unknown", tenantName);
        registeredUser = cloudContext.createUserName("registered", tenantName);
        activatedUser = cloudContext.createUserName("activated", tenantName);
        activatedUserWithApostropheInHisEmail = cloudContext.createUserName("john.o'reilly", tenantName);
        
        activitiHistoryService = (HistoryService) cloudContext.getApplicationContext().getBean("activitiHistoryService");
        authenticationService = (MutableAuthenticationService)cloudContext.getApplicationContext().getBean("AuthenticationService");
        registrationService = (RegistrationService)cloudContext.getApplicationContext().getBean("RegistrationService");
        workflowService = (WorkflowService)cloudContext.getApplicationContext().getBean("WorkflowService");
        
        // We want one registered user.
        registerActivateUser(registeredUser, null, null, null, false);
        
        // Two activated users
        registerActivateUser(activatedUser, "Steve", "Jobs", "RIP", true);
        registerActivateUser(activatedUserWithApostropheInHisEmail, "john", "o'reilly", "password", true);
        
        // Test methods aren't interested in emails sent during setup.
        cloudContext.getEmailTestStorage().clear();
    }
    
    @Override
    public void tearDown() throws Exception
    {
        cloudContext.cleanup();
        
        // Ensure that there are no sensitive data remaining in the Activiti HistoryService.
        List<HistoricTaskInstance> tasks = activitiHistoryService.createHistoricTaskInstanceQuery()
                                                                     .taskDefinitionKey(WorkflowModelResetPassword.TASK_RESET_PASSWORD)
                                                                 .list();
        for (HistoricTaskInstance task : tasks)
        {
            List<HistoricDetail> historicDetails = activitiHistoryService.createHistoricDetailQuery().taskId(task.getId()).list();
            
            for (HistoricDetail detail : historicDetails)
            {
                if (detail instanceof HistoricVariableUpdate)
                {
                    HistoricVariableUpdate hvu = (HistoricVariableUpdate)detail;
                    assertFalse("There should be no keys named *password* in the Activiti History Service.",
                                hvu.getVariableName().contains("assword")); // Not trying to be funny. Want to match password & Password
                }
            }
        }
    }
    
    /**
     * This method tests that various calls to actually change a password give 404s where they should.
     */
    public void testForgottenPasswordNegativeFlows() throws Exception
    {
        // Some initial sanity checks.
        validateUserStatusAndInProgressWorkflows(activatedUser, false, true, 0);
        
        // Now POST an email address to initiate the forgotten password workflow
        postRequestResetPasswordCall(activatedUser, 200);
        
        // and an email should have been sent inviting them to reset their password
        final EmailTestStorage emailTestStorage = cloudContext.getEmailTestStorage();
        assertEquals("Wrong number of emails sent", 1, emailTestStorage.getEmailCount());
        final EmailRequest emailReq = emailTestStorage.getEmailRequest(0);
        
        // At this point we should have one workflow in progress for this email address/username.
        final List<WorkflowTask> inProgressResetPasswordWorkflows = getInProgressResetPasswordWorkflows(activatedUser);
        assertEquals("Wrong number of workflows in progress.", 1, inProgressResetPasswordWorkflows.size());
        
        // Retrieve the correct key and id from the email's URL.
        String resetPasswordUrl = (String) emailReq.getTemplateModel().get(SendResetPasswordEmailDelegate.RESET_PASSWORD_URL);
        
        Pair<String, String> idKey = extractIdKey(resetPasswordUrl);
        
        // The user can then access a Share page to reset their password. That page will call the repo REST API to actually change the password.
        // We'll simulate that call from Share here - but with bad data
        
        // An unmatched key should 404
        postChangePasswordCall("the sparrow flies south in summer", idKey.getFirst(), "badKey", 404);
        
        // An unrecognised id should 404
        postChangePasswordCall("the sparrow flies south in summer", "badId", idKey.getSecond(), 404);
    }
    
    /**
     * This method extracts an id, key pair from URL query parameters.
     */
    private Pair<String, String> extractIdKey(String url)
    {
        Matcher m = ID_AND_KEY_IN_RESET_PASSWORD_URL_PATTERN.matcher(url);
        assertTrue("Reset Password URL could not by matched for <id, key>", m.matches());
        final String key = m.group(1);
        final String id = m.group(2);
        return new Pair<String, String>(id, key);
    }
    
    /**
     * This method tests the behaviour when a user unknown to the system tries to reset their (non-existent) password.
     */
    public void testForgottenPasswordUnknownUser() throws Exception
    {
        // Some initial sanity checks.
        validateUserStatusAndInProgressWorkflows(unknownuser, false, false, 0);
        
        // Now POST an email address to initiate the forgotten password workflow
        postRequestResetPasswordCall(unknownuser, 200);
        
        // For such a user, the workflow should end 'immediately'.
        final List<WorkflowTask> inProgressResetPasswordWorkflows = getInProgressResetPasswordWorkflows(unknownuser);
        assertTrue("Unexpectedly found in-progress workflows for a test user - after test", inProgressResetPasswordWorkflows.isEmpty());
        
        // and an email should have been sent inviting them to sign up or log in (with another email obviously)
        final EmailTestStorage emailTestStorage = cloudContext.getEmailTestStorage();
        assertEquals("Wrong number of emails sent", 1, emailTestStorage.getEmailCount());
        final EmailRequest emailReq = emailTestStorage.getEmailRequest(0);
        
        // Was the right email sent?
        assertTrue("Wrong email template used for reset-password-request", emailReq.getTemplateRef().endsWith("reset-password-no-such-user-email.ftl"));
    }
    
    public void testForgottenPasswordActivatedUser() throws Exception
    {
        // Some initial sanity checks.
        validateUserStatusAndInProgressWorkflows(activatedUser, false, true, 0);
        
        // Now POST an email address to initiate the forgotten password workflow
        postRequestResetPasswordCall(activatedUser, 200);
        
        // For such a user, the workflow should send an email to the user inviting them to reset their password.
        final EmailTestStorage emailTestStorage = cloudContext.getEmailTestStorage();
        assertEquals("Wrong number of emails sent", 1, emailTestStorage.getEmailCount());
        final EmailRequest pleaseReset_EmailReq = emailTestStorage.getEmailRequest(0);
        
        // Was the right email sent?
        assertTrue("Wrong email template used for reset-password-request",  pleaseReset_EmailReq.getTemplateRef().endsWith("reset-password-activated-user-email.ftl"));
        
        // We'll need to get the id and the key for this workflow instance as we'll use these when resetting the password.
        // We'll just hack into the email template model to get them.
        String resetPasswordUrl = (String) pleaseReset_EmailReq.getTemplateModel().get(SendResetPasswordEmailDelegate.RESET_PASSWORD_URL);
        
        Pair<String, String> idKey = extractIdKey(resetPasswordUrl);
        
        // At this point we should have one workflow in progress for this email address/username.
        final List<WorkflowTask> inProgressResetPasswordWorkflows = getInProgressResetPasswordWorkflows(activatedUser);
        assertEquals("Wrong number of workflows in progress.", 1, inProgressResetPasswordWorkflows.size());
        
        
        // The user can then access a Share page to reset their password. That page will call the repo REST API to actually change the password.
        // We'll simulate that call from Share here.
        final String password = "the sparrow flies south in summer";
        postChangePasswordCall(password, idKey.getFirst(), idKey.getSecond(), 200);
        
        // We'll now try to authenticate to ensure that the password actually changed.
        authenticationService.authenticate(activatedUser, password.toCharArray());
        assertEquals(activatedUser, AuthenticationUtil.getFullyAuthenticatedUser());
        AuthenticationUtil.clearCurrentSecurityContext();
    }
    
    // CLOUD-2184
    public void testForgottenPasswordActivatedUserWithApostropheInHisEmail() throws Exception
    {
        // Some initial sanity checks.
        validateUserStatusAndInProgressWorkflows(activatedUserWithApostropheInHisEmail, false, true, 0);
        
        // Now POST an email address to initiate the forgotten password workflow
        postRequestResetPasswordCall(activatedUserWithApostropheInHisEmail, 200);
        
        // For such a user, the workflow should send an email to the user inviting them to reset their password.
        final EmailTestStorage emailTestStorage = cloudContext.getEmailTestStorage();
        assertEquals("Wrong number of emails sent", 1, emailTestStorage.getEmailCount());
        final EmailRequest pleaseReset_EmailReq = emailTestStorage.getEmailRequest(0);
        
        // Was the right email sent?
        assertTrue("Wrong email template used for reset-password-request",  pleaseReset_EmailReq.getTemplateRef().endsWith("reset-password-activated-user-email.ftl"));
        
        // We'll need to get the id and the key for this workflow instance as we'll use these when resetting the password.
        // We'll just hack into the email template model to get them.
        String resetPasswordUrl = (String) pleaseReset_EmailReq.getTemplateModel().get(SendResetPasswordEmailDelegate.RESET_PASSWORD_URL);
        
        Pair<String, String> idKey = extractIdKey(resetPasswordUrl);
        
        // At this point we should have one workflow in progress for this email address/username.
        final List<WorkflowTask> inProgressResetPasswordWorkflows = getInProgressResetPasswordWorkflows(activatedUserWithApostropheInHisEmail);
        assertEquals("Wrong number of workflows in progress.", 1, inProgressResetPasswordWorkflows.size());
        
        
        // The user can then access a Share page to reset their password. That page will call the repo REST API to actually change the password.
        // We'll simulate that call from Share here.
        final String password = "new password";
        postChangePasswordCall(password, idKey.getFirst(), idKey.getSecond(), 200);
        
        // We'll now try to authenticate to ensure that the password actually changed.
        authenticationService.authenticate(activatedUserWithApostropheInHisEmail, password.toCharArray());
        assertEquals(activatedUserWithApostropheInHisEmail, AuthenticationUtil.getFullyAuthenticatedUser());
        AuthenticationUtil.clearCurrentSecurityContext();
    }
    
    public void testGetResetWorkflowStatus() throws Exception
    {
        // Some initial sanity checks.
        validateUserStatusAndInProgressWorkflows(activatedUser, false, true, 0);
        
        // Now POST an email address to initiate the forgotten password workflow
        postRequestResetPasswordCall(activatedUser, 200);
        
        // The workflow should send an email to the user inviting them to reset their password.
        final EmailTestStorage emailTestStorage = cloudContext.getEmailTestStorage();
        assertEquals("Wrong number of emails sent", 1, emailTestStorage.getEmailCount());
        final EmailRequest pleaseReset_EmailReq = emailTestStorage.getEmailRequest(0);
        
        // We'll need to get the id and the key for this workflow instance as we'll use these when querying the workflow status.
        // We'll just hack into the email template model to get them.
        String resetPasswordUrl = (String) pleaseReset_EmailReq.getTemplateModel().get(SendResetPasswordEmailDelegate.RESET_PASSWORD_URL);
        final Pair<String, String> idKey = extractIdKey(resetPasswordUrl);
        
        // With this id and key, we can query the status of the pending workflow.
        String getStatusUrl = REQUEST_PASSWORD_RESET_URL + "/" + idKey.getFirst() + "?key=" + idKey.getSecond();
        Response rsp = sendRequest(new GetRequest(getStatusUrl), 200);
        String contentAsString = rsp.getContentAsString();
        JSONObject jsonObj = (JSONObject) JSONValue.parse(contentAsString);
        String username = (String) jsonObj.get("username");
        assertEquals("Wrong username", activatedUser, username);
        
        
        // If we ask for status of using an invalid key: 404
        getStatusUrl = REQUEST_PASSWORD_RESET_URL + "/" + idKey.getFirst() + "?key=" + "rubbish";
        sendRequest(new GetRequest(getStatusUrl), 404);
        
        // If we ask for status of using an unrecognised id: 404
        getStatusUrl = REQUEST_PASSWORD_RESET_URL + "/" + "rubbish?key=rubbish";
        sendRequest(new GetRequest(getStatusUrl), 404);
    }
    
    /**
     * This method tests the behaviour when a user who is registered but not yet activated tries to reset their (non-existent) password.
     */
    public void testForgottenPasswordRegisteredUser() throws Exception
    {
        // Some initial sanity checks.
        validateUserStatusAndInProgressWorkflows(registeredUser, true, false, 0);
        
        // For this user, there should be one in-progress workflow instance.
        final List<WorkflowTask> inProgressSignupWorkflows = getInProgressSignupWorkflows(registeredUser);
        assertEquals("Wrong number of in-progress signup workflows", 1, inProgressSignupWorkflows.size());
        
        // Now POST the email address to initiate the forgotten password workflow
        postRequestResetPasswordCall(registeredUser, 200);
        
        // For such a user, the workflow should end 'immediately'.
        final List<WorkflowTask> inProgressResetPasswordWorkflows = getInProgressResetPasswordWorkflows(registeredUser);
        assertTrue("Unexpectedly found in-progress workflows for a test user - after test", inProgressResetPasswordWorkflows.isEmpty());
        
        // and an email should have been sent inviting them to activate that account.
        final EmailTestStorage emailTestStorage = cloudContext.getEmailTestStorage();
        assertEquals("Wrong number of emails sent", 1, emailTestStorage.getEmailCount());
        final EmailRequest emailReq = emailTestStorage.getEmailRequest(0);
        
        // Was the right email sent?
        assertTrue("Wrong email template used for reset-password-request (registered user)", emailReq.getTemplateRef().endsWith("self-signup-requested-email.ftl"));
        
        // Did the email contain a link with the right id/key?
        String activateAccountUrl = (String) emailReq.getTemplateModel().get(SendEmailDelegate.ACTIVATE_ACCOUNT_URL); // TODO Duplicate this field?
        Pair<String, String> idKeyFromEmail = extractIdKey(activateAccountUrl);
        
        Pair<String, String> idKeyFromSignupWorkflow = new Pair<String, String>((String) inProgressSignupWorkflows.get(0).getPath().getInstance().getId(),
                                                                                (String) inProgressSignupWorkflows.get(0).getProperties().get(WorkflowModelSelfSignup.WF_PROP_KEY));
        assertEquals(idKeyFromSignupWorkflow, idKeyFromEmail);
        
        // For this user, there should STILL be one in-progress workflow instances i.e. it's not complete and we didn't start a new one.
        assertEquals("Wrong number of in-progress signup workflows", 1, getInProgressSignupWorkflows(registeredUser).size());
    }
    
    
    /**
     * This method asserts that the user is correctly registered/activated and that there are the correct number
     * of in-progress reset-password workflows.
     */
    private void validateUserStatusAndInProgressWorkflows(final String username, final boolean isRegistered, final boolean isActivated, final int workflowCount)
    {
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                assertEquals(isRegistered, registrationService.isRegisteredEmailAddress(username));
                assertEquals(isActivated, registrationService.isActivatedEmailAddress(username));
                
                return null;
            }
        }, AuthenticationUtil.getAdminUserName());
        
        assertEquals(workflowCount, getInProgressResetPasswordWorkflows(username).size());
    }
    
    @SuppressWarnings("unchecked")
    private void postRequestResetPasswordCall(String username, int expectedStatus)
         throws IOException, UnsupportedEncodingException
    {
        JSONObject obj = new JSONObject();
        obj.put("username", username);
        
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();
        
        Response rsp = sendRequest(new PostRequest(REQUEST_PASSWORD_RESET_URL, jsonString, APPLICATION_JSON), expectedStatus);
        
        if (expectedStatus == 200)
        {
            String contentAsString = rsp.getContentAsString();
            
            JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
            assertNotNull("Problem reading JSON", jsonRsp);
            
            boolean success = (Boolean) jsonRsp.get("success");
            assertTrue(success);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void postChangePasswordCall(String password, String id, String key, int expectedStatus)
         throws IOException, UnsupportedEncodingException
    {
        JSONObject obj = new JSONObject();
        obj.put("password", password);
        obj.put("id", id);
        obj.put("key", key);
        
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();
        
        Response rsp = sendRequest(new PostRequest(POST_CHANGED_PASSWORD_URL, jsonString, APPLICATION_JSON), expectedStatus);
        
        if (expectedStatus == 200)
        {
            String contentAsString = rsp.getContentAsString();
            
            JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
            assertNotNull("Problem reading JSON", jsonRsp);
            
            boolean success = (Boolean) jsonRsp.get("success");
            assertTrue(success);
        }
    }
    
    private List<WorkflowTask> getInProgressResetPasswordWorkflows(String username)
    {
        WorkflowTaskQuery query = new WorkflowTaskQuery();
        query.setActive(Boolean.TRUE);
        query.setTaskState(WorkflowTaskState.IN_PROGRESS);
        
        HashMap<QName, Object> props = new HashMap<QName, Object>();
        props.put(WorkflowModelResetPassword.WF_PROP_USERNAME, username);
        query.setProcessCustomProps(props);
        
        List<WorkflowTask> tasks = workflowService.queryTasks(query);
        return tasks;
    }
    
    private List<WorkflowTask> getInProgressSignupWorkflows(String username)
    {
        WorkflowTaskQuery query = new WorkflowTaskQuery();
        query.setActive(Boolean.TRUE);
        query.setTaskState(WorkflowTaskState.IN_PROGRESS);
        
        HashMap<QName, Object> props = new HashMap<QName, Object>();
        props.put(WorkflowModelSelfSignup.WF_PROP_EMAIL_ADDRESS, username);
        query.setProcessCustomProps(props);
        
        List<WorkflowTask> tasks = workflowService.queryTasks(query);
        return tasks;
    }
    
    private void registerActivateUser(final String email, final String fName, final String lName, final String password, final boolean activate)
    {
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                Registration registeredUserReg = registrationService.registerEmail(email, "test-" + this.getClass().getSimpleName(), null, null);
                cloudContext.addRegistration(registeredUserReg);

                if (activate)
                {
                    // activate user
                    registrationService.activateRegistration(registeredUserReg.getId(), registeredUserReg.getKey(), fName, lName, password);
                }
                return null;
            }
        }, AuthenticationUtil.getAdminUserName());
    }
}
