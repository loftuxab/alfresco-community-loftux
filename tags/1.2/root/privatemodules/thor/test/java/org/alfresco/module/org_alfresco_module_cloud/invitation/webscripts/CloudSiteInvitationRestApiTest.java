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
package org.alfresco.module.org_alfresco_module_cloud.invitation.webscripts;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.invitation.CloudInvitation;
import org.alfresco.module.org_alfresco_module_cloud.invitation.CloudInvitationService;
import org.alfresco.module.org_alfresco_module_cloud.invitation.WorkflowModelCloudInvitation;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.tenant.TenantContextHolder;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Request;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * This class tests the REST API for the {@link CloudInvitationService}
 * 
 * @author Neil McErlean
 * @since Alfresco Cloud Module (Thor)
 */
public class CloudSiteInvitationRestApiTest extends BaseWebScriptTest
{
    // FIXME I've started refactoring this class to make it cleaner. Still more to do.
    
    private static final Log log = LogFactory.getLog(CloudSiteInvitationRestApiTest.class);
    
    // URLs and URL fragments used in this REST API.
    private final static String POST_CLOUD_SITE_INVITE_URL              = "/internal/cloud/sites/{shortname}/invitations";
    private final static String POST_CLOUD_SITE_INVITATION_RESPONSE_URL = "/internal/cloud/site-invitations/{invite_id}/responses";
    private final static String GET_CLOUD_SITE_INVITATION_URL           = "/internal/cloud/site-invitations/{invite_id}?key={key}";
    private final static String POST_CLOUD_SITE_INVITATION_REMINDER_URL = "/internal/cloud/site-invitations/{invite_id}/reminder?key={key}";
    private static final String GET_PENDING_INVITATIONS_BY_INVITEE      = "/internal/cloud/invitations?inviteeUserName={inviteeUserName}";
    private static final String GET_PENDING_INVITATIONS_BY_SITE         = "/internal/cloud/sites/{shortname}/invitations?inviteeUserName={inviteeUserName?}";
    
    private RegistrationService       registrationService;
    private SiteService               siteService;
    private RetryingTransactionHelper transactionHelper;
    
    // These tests require various users in various states.
    private String tenant1;
    private String tenant2;
    private String preexistingUser1_tenant1;
    private String preexistingUser2_tenant1;
    private String user2_tenant2;
    
    private String user3_tenant2;
    private String user4_tenant2;
    private String user5_tenant2;
    private String user6_tenant2;
    
    private CloudTestContext cloudContext;
    
    @Override protected void setUp() throws Exception
    {
        super.setUp();
        
        cloudContext = new CloudTestContext(this);
        tenant1 = cloudContext.createTenantName("network1");
        tenant2 = cloudContext.createTenantName("network2");
        // Note these user names' case matters. Don't use upper case letters.
        preexistingUser1_tenant1 = cloudContext.createUserName("preexistinguser1", tenant1);
        preexistingUser2_tenant1 = cloudContext.createUserName("preexistinguser2", tenant1);
        
        user2_tenant2 = cloudContext.createUserName("user2", tenant2);
        user3_tenant2 = cloudContext.createUserName("user3", tenant2);
        user4_tenant2 = cloudContext.createUserName("user4", tenant2);
        user5_tenant2 = cloudContext.createUserName("user5", tenant2);
        // CLOUD-2184
        user6_tenant2 = cloudContext.createUserName("john.o'reilly", tenant2);
        
        registrationService = (RegistrationService)cloudContext.getApplicationContext().getBean("RegistrationService");
        siteService = (SiteService) cloudContext.getApplicationContext().getBean("siteService");
        transactionHelper = (RetryingTransactionHelper) cloudContext.getApplicationContext().getBean("retryingTransactionHelper");
        
        // Precreate some users.
        transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            @Override public Void execute() throws Throwable
            {
                AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
                
                log.debug("Creating user " + preexistingUser1_tenant1);
                
                assertNotNull(registrationService.createUser(preexistingUser1_tenant1, "first___", "last___", "password"));
                cloudContext.addUser(preexistingUser1_tenant1);
                cloudContext.addAccountDomain(tenant1);
                
                log.debug("Creating user " + preexistingUser2_tenant1);
                
                assertNotNull(registrationService.createUser(preexistingUser2_tenant1, "first___", "last___", "password"));
                cloudContext.addUser(preexistingUser2_tenant1);
                cloudContext.addAccountDomain(tenant2);
                
                // We don't add user[345]_tenant2 as we're using those to test invitation of non-existing users.
                return null;
            }
        });
        TenantContextHolder.setTenantDomain(tenant1);
    }
    
    @Override
    public void tearDown() throws Exception
    {
        cloudContext.cleanup();
    }
    
    private Response sendTenantRequest(final Request req, final int expectedStatus) throws IOException
    {
        // TODO: override TestWebScriptServer to support tenant-switching (eg. /service => /a/<tenant>)
        return TenantUtil.runAsTenant(new TenantRunAsWork<Response>()
        {
            public Response doWork() throws Exception
            {
                // note: wrapped in runAsTenant since /service will clear tenant ctx
                return sendRequest(req, expectedStatus);
            }
        }, TenantUtil.getCurrentDomain());
    }
    
    /**
     * This method tests the various ways in which the invitation REST API should return 404s.
     */
    public void testInvitation404Flows() throws Exception
    {
        final String inviterUser = this.preexistingUser1_tenant1;
        final String inviteeUser = this.user2_tenant2;
        
        final String siteShortName = getSiteShortNameForUser(inviterUser);
        final String inviteeRole = SiteModel.SITE_COLLABORATOR;
        
        assertThatSiteIsValidForInvitations(inviterUser, tenant1, siteShortName);
        
        // Issue the invitation
        List<Map<String, Object>> invitationsRsp = postSiteInvitations(inviterUser, Arrays.asList(new String[]{inviteeUser}), inviteeRole, siteShortName);
        
        // Store the id and key which we'll need below.
        Pair<String, String> idKey = new Pair<String, String>((String) invitationsRsp.get(0).get("id"),
                                                              (String) invitationsRsp.get(0).get("key"));
        
        // The correct id and key should give 200.
        getInvitationStatus(idKey.getFirst(), idKey.getSecond(), 200);
        
        // But a bad key should give 404.
        getInvitationStatus(idKey.getFirst(), "rubbish", 404);
        
        // And a bad id should give 404.
        getInvitationStatus("rubbish", idKey.getSecond(), 404);
        
        // Now we'll reject the invitation in order to complete the workflow.
        postInvitationResponse(CloudInvitation.RESPONSE_REJECT, idKey.getFirst(), idKey.getSecond(),
                null, null, null, null, 200);
        
        // POSTing the same reject call again should also trigger a 404
        postInvitationResponse(CloudInvitation.RESPONSE_REJECT, idKey.getFirst(), idKey.getSecond(),
                null, null, null, null, 404);
        
        // Completed workflows should give 404 when queried for status.
        getInvitationStatus(idKey.getFirst(), idKey.getSecond(), 404);
        
        
        // Then just to cover all possibilities, we'll issue a new invitation:
        invitationsRsp = postSiteInvitations(inviterUser, Arrays.asList(new String[]{inviteeUser}), inviteeRole, siteShortName);
        idKey = new Pair<String, String>((String) invitationsRsp.get(0).get("id"),
                                                              (String) invitationsRsp.get(0).get("key"));
        
        // This time we'll accept the invitation.
        postInvitationResponse(CloudInvitation.RESPONSE_ACCEPT, idKey.getFirst(), idKey.getSecond(),
                "joe", "soap", "password", null, 200);
        
        // Completed workflows should give 404 when queried for status.
        getInvitationStatus(idKey.getFirst(), idKey.getSecond(), 404);
        
        // And accept it twice for the 404
        postInvitationResponse(CloudInvitation.RESPONSE_ACCEPT, idKey.getFirst(), idKey.getSecond(),
                "joe", "soap", "password", null, 404);
    }
    
    public void testSendReminder() throws Exception 
    {
        final String inviterUser = this.preexistingUser1_tenant1;
        final String inviteeExistingUser = this.user2_tenant2;
        final String inviteeNewUser = this.user3_tenant2;
        
        final String siteShortName = getSiteShortNameForUser(inviterUser);
        final String inviteeRole = SiteModel.SITE_COLLABORATOR;
        
        assertThatSiteIsValidForInvitations(inviterUser, tenant1, siteShortName);
        
        // Issue the invitations
        final List<String> invitees = Arrays.asList(new String[]{inviteeExistingUser, inviteeNewUser});
        List<Map<String, Object>> invitationsRsp = postSiteInvitations(inviterUser, invitees, inviteeRole, siteShortName);
        
        // Store the id and key which we'll need below.
        Pair<String, String> idKey = new Pair<String, String>((String) invitationsRsp.get(0).get("id"),
                                                              (String) invitationsRsp.get(0).get("key"));
        
        // How many emails have already been sent?
        int emailCount = cloudContext.getEmailTestStorage().getEmailCount();
        
        // Request sending of a reminder to the invitee, 200 expected
        postReminderAndAssertStatus(idKey.getFirst(), idKey.getSecond(), 200);
        
        // Check if the email has been sent
        assertEquals(emailCount + 1, cloudContext.getEmailTestStorage().getEmailCount());

        // Request sending of a reminder using wrong KEY, 404 expected
        postReminderAndAssertStatus(idKey.getFirst(), "nonsense", 404);
        
        // Request sending of a reminder using unexisting id, 404 expected
        postReminderAndAssertStatus("activiti$nonsense", idKey.getSecond(), 404);
        
        // Check if NO additional emails have been sent by the 404's
        assertEquals(emailCount + 1, cloudContext.getEmailTestStorage().getEmailCount());
        
    }
    
    public void testPendingInvitations() throws Exception
    {
        final String inviterUser = this.preexistingUser1_tenant1;
        final String inviteeExistingUser = this.user2_tenant2;
        final String inviteeNewUser = this.user3_tenant2;
        
        final String siteShortName = getSiteShortNameForUser(inviterUser);
        final String inviteeRole = SiteModel.SITE_COLLABORATOR;
        
        assertThatSiteIsValidForInvitations(inviterUser, tenant1, siteShortName);
        
        // Issue the invitations
        final List<String> invitees = Arrays.asList(new String[]{inviteeExistingUser, inviteeNewUser});
        List<Map<String, Object>> invitationsRsp = postSiteInvitations(inviterUser, invitees, inviteeRole, siteShortName);
        
        // Store the id and key which we'll need below.
        Pair<String, String> idKey = new Pair<String, String>((String) invitationsRsp.get(0).get("id"),
                                                              (String) invitationsRsp.get(0).get("key"));
        
        // Now we'll get the pending invitations via the two APIs
        List<Map<String, Object>> invitationsExisting = getPendingInvitationsBySite(siteShortName, inviteeExistingUser, -1, 200);
        List<Map<String, Object>> invitationsNew = getPendingInvitationsBySite(siteShortName, inviteeNewUser, -1, 200);
        List<Map<String, Object>> invitationsAll = getPendingInvitationsBySite(siteShortName, null, -1, 200);
        
        assertEquals(1, invitationsExisting.size());
        assertEquals(1, invitationsNew.size());
        assertEquals(2, invitationsAll.size());
        
        invitationsExisting = getPendingInvitationsByInvitee(siteShortName, inviteeExistingUser, -1, 200);
        invitationsNew = getPendingInvitationsByInvitee(siteShortName, inviteeNewUser, -1, 200);
        // No 'all' in this case.
        
        assertEquals(1, invitationsExisting.size());
        assertEquals(1, invitationsNew.size());
        
        
        // Get them with a 'limit'/pageSize of 1. - This is only testing that the REST API can take the pageSize param and is limiting results.
        assertEquals(1, getPendingInvitationsBySite(siteShortName, null, 1, 200).size());
        assertEquals(1, getPendingInvitationsByInvitee(siteShortName, inviteeNewUser, 1, 200).size());
        
        
        // Now we'll reject the invitation in order to complete the workflow.
        postInvitationResponse(CloudInvitation.RESPONSE_REJECT, idKey.getFirst(), idKey.getSecond(),
                null, null, null, null, 200);
        
        // Get the pending invitations again to see that one has gone.
        invitationsExisting = getPendingInvitationsBySite(siteShortName, inviteeExistingUser, -1, 200);
        invitationsNew = getPendingInvitationsBySite(siteShortName, inviteeNewUser, -1, 200);
        invitationsAll = getPendingInvitationsBySite(siteShortName, null, -1, 200);
        
        assertEquals(0, invitationsExisting.size());
        assertEquals(1, invitationsNew.size());
        assertEquals(1, invitationsAll.size());
        
        invitationsExisting = getPendingInvitationsByInvitee(siteShortName, inviteeExistingUser, -1, 200);
        invitationsNew = getPendingInvitationsByInvitee(siteShortName, inviteeNewUser, -1, 200);
        
        assertEquals(0, invitationsExisting.size());
        assertEquals(1, invitationsNew.size());
    }
    
    public void testPendingInvitationsNoSuchSite() throws Exception
    {
        List<Map<String, Object>> invitations = getPendingInvitationsBySite("rubbish", "user@user.example", -1, 200);
        assertTrue(invitations.isEmpty());
        
        invitations = getPendingInvitationsByInvitee("rubbish", "user@user.example", -1, 200);
        assertTrue(invitations.isEmpty());
    }
    
    /**
     * Tests one user inviting another to their site and the invitee rejecting the invitation.
     */
    public void testInviteExternalUser_UserRejects() throws Exception
    {
        final String inviterUser = this.preexistingUser1_tenant1;
        final String inviteeUser = this.user2_tenant2;
        
        final String siteShortName = getSiteShortNameForUser(inviterUser);
        final String inviteeRole = SiteModel.SITE_COLLABORATOR;
        
        assertThatSiteIsValidForInvitations(inviterUser, tenant1, siteShortName);
        
        // Issue the invitation
        List<Map<String, Object>> invitationsRsp = postSiteInvitations(inviterUser, Arrays.asList(new String[]{inviteeUser}), inviteeRole, siteShortName);
        
        // One email per invitee should have been sent.
        assertEquals("Wrong number of emails sent.", 1, cloudContext.getEmailTestStorage().getEmailCount());
        
        // Quick check of the REST API response
        assertEquals("Wrong number of invitations reported.", 1, invitationsRsp.size());
        Map<String, Object> invitationRsp1 = invitationsRsp.get(0);
        
        assertEquals("Wrong invitee email", inviteeUser, invitationRsp1.get("inviteeEmail"));
        final String id = (String) invitationRsp1.get("id");
        final String key = (String) invitationRsp1.get("key");
        assertNotNull("Missing id", id);
        assertNotNull("Missing key", key);
        
        // The id and key are needed in all future interactions with this invitation workflow instance.
        final Pair<String, String> idKey = new Pair<String, String>(id, key);
        
        
        log.debug("Rejecting invitation");
        
        // We don't need to provide any information other than the workflow ID, key and the reject indicator.
        Map<String, Object> rejectRsp = postInvitationResponse(CloudInvitation.RESPONSE_REJECT, idKey.getFirst(), idKey.getSecond(),
                null, null, null, null, 200);
        
        assertEquals("Wrong siteShortName", siteShortName, rejectRsp.get("siteShortName"));
        assertEquals("Wrong response", WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_REJECT, rejectRsp.get("response"));
    }
    
    /**
     * This test method invites multiple new external users to a site and then accepts those invitations.
     */
    public void testInviteExternalUsers_UsersAccept_DontExist() throws Exception
    {
        // We'll use the users' default home sites.
        final String siteShortName = getSiteShortNameForUser(preexistingUser1_tenant1);
        
        final List<String> inviteeUsers = Arrays.asList(new String[]{user3_tenant2, user4_tenant2, user5_tenant2, user6_tenant2});
        final String inviteeRole = SiteModel.SITE_COLLABORATOR;
        // Send the invitation(s)
        List<Map<String, Object>> invitationsRsp = postSiteInvitations(preexistingUser1_tenant1, inviteeUsers, inviteeRole, siteShortName);
        
        acceptInvitationsWithSignup(siteShortName, invitationsRsp);
        
        ensureUsersExistAndHaveCorrectSiteRole(preexistingUser1_tenant1, inviteeUsers, siteShortName,
                    inviteeRole);
    }
    
    /**
     * This test method invites an existing external user to a site and then accepts that invitation.
     */
    public void testInviteExternalUser_UserAccepts_AlreadyExists() throws Exception
    {
        String siteShortName = getSiteShortNameForUser(preexistingUser1_tenant1);
        
        String inviteeRole = SiteModel.SITE_COLLABORATOR;
        
        // Send the invitation
        Map<String, Object> invitationRsp = postSiteInvitation(preexistingUser1_tenant1, preexistingUser2_tenant1, inviteeRole, siteShortName);
        
        // Accept the invitation
        acceptInvitation(siteShortName, invitationRsp);
        
        ensureUserExistsAndHasCorrectSiteRole(preexistingUser1_tenant1, preexistingUser2_tenant1, siteShortName, inviteeRole);
    }
    
    /**
     * This method tests the acceptance of a site invitation at an alternative, existing email.
     * @since Thor Phase 2 Sprint 1
     */
    public void testInviteUserWhoAcceptsOnDifferentEmail() throws Exception
    {
        final String siteShortName = getSiteShortNameForUser(preexistingUser1_tenant1);
        
        // User invites another user to a site. The invitee doesn't exist, but that shouldn't matter.
        Map<String, Object> invitationRsp = postSiteInvitation(preexistingUser1_tenant1, user5_tenant2, SiteModel.SITE_COLLABORATOR, siteShortName);
        
        // Accept the invitation
        acceptInvitationOnAlternativeEmail(siteShortName, preexistingUser2_tenant1, "password", invitationRsp, 200);
        
        ensureUserExistsAndHasCorrectSiteRole(preexistingUser1_tenant1, preexistingUser2_tenant1, siteShortName, SiteModel.SITE_COLLABORATOR);
    }
    
    /**
     * This method tests the acceptance of a site invitation at an alternative, existing email.
     * @since Thor Phase 2 Sprint 1
     */
    public void testInviteUserWhoAcceptsOnDifferentEmailWithIncorrectPassword() throws Exception
    {
        final String siteShortName = getSiteShortNameForUser(preexistingUser1_tenant1);
        
        // User invites another user to a site. The invitee doesn't exist, but that shouldn't matter.
        Map<String, Object> invitationRsp = postSiteInvitation(preexistingUser1_tenant1, user5_tenant2, SiteModel.SITE_COLLABORATOR, siteShortName);
        
        // Accept the invitation
        acceptInvitationOnAlternativeEmail(siteShortName, preexistingUser2_tenant1, "wrongPassword", invitationRsp, 403);
    }
    
    /**
     * This method tests the acceptance of a site invitation at an alternative, non-existent email, which should fail.
     * @since Thor Phase 2 Sprint 1
     */
    public void testInviteUserWhoAcceptsOnNonExistentEmail() throws Exception
    {
        final String siteShortName = getSiteShortNameForUser(preexistingUser1_tenant1);
        
        // User invites another user to a site. The invitee doesn't exist, but that shouldn't matter.
        Map<String, Object> invitationRsp = postSiteInvitation(preexistingUser1_tenant1, user5_tenant2, SiteModel.SITE_COLLABORATOR, siteShortName);
        
        // Accept the invitation. Here the accepting user does not exist either, and that DOES matter.
        acceptInvitationOnAlternativeEmail(siteShortName, user4_tenant2, "password", invitationRsp, 403);
    }
    
    private Response sendTenantRequest(String tenantDomain, final Request req, final int expectedStatus) throws IOException
    {
        // TODO override TestWebScriptServer to support tenant-switching - ie. /service => /a/<tenant>
        return TenantUtil.runAsTenant(new TenantRunAsWork<Response>()
        {
            public Response doWork() throws Exception
            {
                return sendRequest(req, expectedStatus);
            }
        }, tenantDomain);
    }
    
    private void ensureUserExistsAndHasCorrectSiteRole(final String inviterUser, final String inviteeUser, final String siteShortName,
                                                       final String inviteeRole)
    {
        assertTrue("Invited user not activated.", registrationService.isActivatedEmailAddress(inviteeUser));
        
        TenantRunAsWork<Void> checkSiteMembershipWork = new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                SiteInfo site = siteService.getSite(siteShortName);
                assertNotNull("Site was null.", site);
                assertEquals("invitee had wrong role", inviteeRole, siteService.getMembersRole(siteShortName, inviteeUser));
                return null;
            }
        };
        TenantUtil.runAsUserTenant(checkSiteMembershipWork, inviterUser, this.tenant1);
    }
    
    private void ensureUsersExistAndHaveCorrectSiteRole(final String inviterUser,
                final List<String> inviteeUsers, final String siteShortName,
                final String inviteeRole)
    {
        // Now ensure that the users exist and are members of the site - users are on the default tenant.
        for (String invitee : inviteeUsers)
        {
            ensureUserExistsAndHasCorrectSiteRole(inviterUser, invitee, siteShortName, inviteeRole);
        }
    }
    
    private void acceptInvitation(final String siteShortName, Map<String, Object> invitationData) throws IOException
    {
        Map<String, Object> acceptRsp = postInvitationResponse(CloudSiteInvitationResponsePost.JSON_VAR_RESPONSE_ACCEPT,
                                     (String) invitationData.get("id"), (String) invitationData.get("key"),
                                     null, null, null, null, 200);
        
        assertEquals("Wrong siteShortName", siteShortName, acceptRsp.get("siteShortName"));
        assertEquals("Wrong response", CloudSiteInvitationResponsePost.JSON_VAR_RESPONSE_ACCEPT, acceptRsp.get("response"));
    }
    
    private void acceptInvitationWithSignup(final String siteShortName, Map<String, Object> invitationData) throws IOException
    {
        Map<String, Object> acceptRsp = postInvitationResponse(CloudSiteInvitationResponsePost.JSON_VAR_RESPONSE_ACCEPT,
                                     (String) invitationData.get("id"), (String) invitationData.get("key"),
                                     "name", "surname", "password", null, 200);
        
        assertEquals("Wrong siteShortName", siteShortName, acceptRsp.get("siteShortName"));
        assertEquals("Wrong response", CloudSiteInvitationResponsePost.JSON_VAR_RESPONSE_ACCEPT, acceptRsp.get("response"));
    }
    
    private void acceptInvitationsWithSignup(final String siteShortName, List<Map<String, Object>> invitationData) throws IOException
    {
        for (Map<String, Object> invitationDataItem : invitationData)
        {
            acceptInvitationWithSignup(siteShortName, invitationDataItem);
        }
    }
    
    private void acceptInvitationOnAlternativeEmail(final String siteShortName, String altEmail, String altPassword,
                                                    Map<String, Object> invitationData, int expectedStatusCode) throws IOException
    {
        Map<String, Object> acceptRsp = postInvitationResponse(CloudSiteInvitationResponsePost.JSON_VAR_RESPONSE_ACCEPT_ALT_EMAIL,
                                     (String) invitationData.get("id"), (String) invitationData.get("key"),
                                     null, null, altPassword, altEmail, expectedStatusCode);
        if (expectedStatusCode == 200)
        {
            assertEquals("Wrong siteShortName", siteShortName, acceptRsp.get("siteShortName"));
            
            // The response from the webscript gives us the invitation status as used by the workflow, not the REST API.
            assertEquals("Wrong response", CloudSiteInvitationResponsePost.JSON_VAR_RESPONSE_ACCEPT, acceptRsp.get("response"));
        }
    }
    
    private void acceptInvitations(final String siteShortName, List<Map<String, Object>> invitationsRsp) throws IOException
    {
        // Now we'll accept all the invitations. We do this as 'no-auth' as the invitee is not yet a registered user of the system.
        log.debug("Accepting all invitations...");
        for (Map<String, Object> invitationData : invitationsRsp)
        {
            acceptInvitation(siteShortName, invitationData);
        }
    }
    
    private Map<String, Object> getInvitationStatus(String id, String key, int expectedStatus) throws IOException
    {
        String getInviteeStatusUrl = GET_CLOUD_SITE_INVITATION_URL.replace("{invite_id}", id)
                                                                  .replace("{key}", key);
        Response rsp = sendTenantRequest(new GetRequest(getInviteeStatusUrl), expectedStatus);
        
        if (expectedStatus == 200)
        {
            String contentAsString = rsp.getContentAsString();
            
            JSONObject jsonObj = (JSONObject) JSONValue.parse(contentAsString);
            assertNotNull("Trouble reading JSON", jsonObj);
            Map<String, Object> map = jsonObjectToMap(jsonObj);
            
            return map;
        }
        else
        {
            return null;
        }
    }
    
    private List<Map<String, Object>> getPendingInvitationsBySite(String siteShortName, String invitee, int limit, int expectedStatus) throws IOException
    {
        if (invitee == null)
        {
            invitee = "";
        }
        
        String getInvitationsUrl = GET_PENDING_INVITATIONS_BY_SITE.replace("{shortname}", siteShortName)
                                                                  .replace("{inviteeUserName?}", invitee);
        if (limit > 0)
        {
            getInvitationsUrl = getInvitationsUrl + "&pageSize=" + limit;
        }
        
        Response rsp = sendTenantRequest(new GetRequest(getInvitationsUrl), expectedStatus);
        
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonObj = (JSONObject) JSONValue.parse(contentAsString);
        assertNotNull("Trouble reading JSON", jsonObj);
        
        JSONObject dataObj = (JSONObject) jsonObj.get("data");
        JSONArray invitations = (JSONArray) dataObj.get("invitations");
        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (@SuppressWarnings("rawtypes") Iterator iter = invitations.iterator(); iter.hasNext(); )
        {
            JSONObject nextInvitation = (JSONObject) iter.next();
            result.add(jsonObjectToMap(nextInvitation));
        }
        
        return result;
    }
    
    private List<Map<String, Object>> getPendingInvitationsByInvitee(String siteShortName, String invitee, int limit, int expectedStatus) throws IOException
    {
        String getInvitationsUrl = GET_PENDING_INVITATIONS_BY_INVITEE.replace("{inviteeUserName}", invitee);
        if (limit > 0)
        {
            getInvitationsUrl = getInvitationsUrl + "&pageSize=" + limit;
        }
        Response rsp = sendTenantRequest(new GetRequest(getInvitationsUrl), expectedStatus);
        
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonObj = (JSONObject) JSONValue.parse(contentAsString);
        assertNotNull("Trouble reading JSON", jsonObj);
        
        JSONObject dataObj = (JSONObject) jsonObj.get("data");
        JSONArray invitations = (JSONArray) dataObj.get("invitations");
        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (@SuppressWarnings("rawtypes") Iterator iter = invitations.iterator(); iter.hasNext(); )
        {
            JSONObject nextInvitation = (JSONObject) iter.next();
            result.add(jsonObjectToMap(nextInvitation));
        }
        
        return result;
    }
    
    private Map<String, Object> postSiteInvitation(String inviterEmail, String inviteeEmail, String inviteeRole, String siteShortName) throws IOException
    {
        return this.postSiteInvitations(inviterEmail, Arrays.asList(new String[] {inviteeEmail}), inviteeRole, siteShortName).get(0);
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> postSiteInvitations(String inviterEmail, List<String> inviteeEmails, String inviteeRole, String siteShortName) throws IOException
    {
        log.debug("Inviting users " + inviteeEmails + " to site " + siteShortName + " as " + inviteeRole);
        
        // How many emails have already been sent?
        final int emailCount = cloudContext.getEmailTestStorage().getEmailCount();
        
        
        // Assemble the JSON we're going to POST...
        JSONArray inviteesJSON = new JSONArray();
        inviteesJSON.addAll(inviteeEmails);
        
        JSONObject obj = new JSONObject();
        obj.put("inviterEmail", inviterEmail);
        obj.put("inviteeEmails", inviteesJSON);
        obj.put("role", inviteeRole);
        
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();
        
        log.debug(obj.toJSONString());
        
        
        // ... and the URL
        String url = POST_CLOUD_SITE_INVITE_URL.replace("{shortname}", siteShortName);
        
        log.debug(url);
        
        
        // and POST the data
        Response rsp = sendTenantRequest(new PostRequest(url, jsonString, "application/json"), 200);
        
        
        // Parse the HTTP response to extract the server's response to our "POST invitations" call.
        List<Map<String, Object>> invitationData = new ArrayList<Map<String, Object>>();
        
        String contentAsString = rsp.getContentAsString();
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        assertNotNull("Problem encountered with invitation rsp JSON", jsonRsp);
        
        log.debug(jsonRsp);
        
        JSONArray invitations = (JSONArray) jsonRsp.get("invitations");
        
        for (@SuppressWarnings("rawtypes") Iterator iter = invitations.iterator(); iter.hasNext(); )
        {
            Map<String, Object> invitationMap = jsonObjectToMap((JSONObject) iter.next());
            invitationData.add(invitationMap);
        }
        
        
        // We can assert that some expected side-effect of the call have happened.
        assertEquals("Wrong number of emails sent.", emailCount + inviteeEmails.size(), cloudContext.getEmailTestStorage().getEmailCount());
        String emailSubject = cloudContext.getEmailTestStorage().getEmailRequest(0).getSubject();
        assertEquals("Wrong email template sent.", "cloud-invitation.email.subjectline", emailSubject);
        assertEquals("Wrong number of invitees", inviteeEmails.size(), invitationData.size());
        
        return invitationData;
    }
    
    /**
     * Convenience method posts a reminder-request for an invitation.
     * @param id the id of the invitation-process
     * @param key the key associated
     * @return status-code of the request
     * @throws IOException 
     * @throws UnsupportedEncodingException 
     */
    private void postReminderAndAssertStatus(String id, String key, int expectedStatus) throws UnsupportedEncodingException, IOException
    {
        String url = POST_CLOUD_SITE_INVITATION_REMINDER_URL.replace("{invite_id}", id).replace("{key}", key);
        log.debug("Posting reminder request to URL: " + url);
        
        sendTenantRequest(new PostRequest(url, "", "application/json"), expectedStatus);
    }
    
    /**
     * This convenience method converts a {@link JSONObject} into a {@link Map}.
     * It performs a shallow mapping, so any {@link JSONObject objects} or {@link JSONArray arrays}
     * contained within the original {@link JSONObject} will not be converted.
     * 
     * @param jsonObject a {@link JSONObject}.
     * @return a {@link Map} where the keys are from {@link JSONObject#keySet()} and the values are from {@link JSONObject#get(Object)}.
     */
    private Map<String, Object> jsonObjectToMap(JSONObject jsonObject)
    {
        Map<String, Object> result = new HashMap<String, Object>();
        
        for (Object key : jsonObject.keySet())
        {
            result.put((String) key, jsonObject.get(key));
        }
        return result;
    }
    
    /**
     * 
     * @param response {@link CloudSiteInvitationResponse#JSON_VAR_RESPONSE_ACCEPT}, {@link CloudSiteInvitationResponse#JSON_VAR_RESPONSE_ACCEPT_ALT_EMAIL}
     *                 or {@link CloudSiteInvitationResponse#JSON_VAR_RESPONSE_REJECT}
     * @param id the workflow id
     * @param key the workflow key
     * @param inviteeFirstName
     * @param inviteeLastName
     * @param inviteePassword
     * @param altEmail
     * @param expectedStatus HTTP expected response.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> postInvitationResponse(String response, String id, String key, String inviteeFirstName, String inviteeLastName,
                                            String inviteePassword, String altEmail, int expectedStatus) throws IOException
    {
        JSONObject obj = new JSONObject();
        obj.put(CloudSiteInvitationResponsePost.JSON_VAR_RESPONSE, response);
        obj.put(AbstractCloudInvitationWebscript.PARAM_KEY, key);
        if (inviteeFirstName != null) obj.put(CloudSiteInvitationResponsePost.JSON_VAR_FIRST_NAME, inviteeFirstName);
        if (inviteeLastName != null) obj.put(CloudSiteInvitationResponsePost.JSON_VAR_LAST_NAME, inviteeLastName);
        if (inviteePassword != null) obj.put(CloudSiteInvitationResponsePost.JSON_VAR_PASSWORD, inviteePassword);
        
        if (altEmail != null) obj.put(CloudSiteInvitationResponsePost.JSON_VAR_ALT_EMAIL, altEmail);
        
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();
        
        String url = POST_CLOUD_SITE_INVITATION_RESPONSE_URL.replace("{invite_id}", id);
        
        log.debug("POSTing invitation response to " + url + " :");
        log.debug(jsonString);
        
        
        Response rsp = sendTenantRequest(new PostRequest(url, jsonString.toString(), "application/json"), expectedStatus);
        
        if (expectedStatus == 200)
        {
            String contentAsString = rsp.getContentAsString();
            
            JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
            assertNotNull("Problem reading json", jsonRsp);
            
            Map<String, Object> jsonMap = jsonObjectToMap(jsonRsp);
            return jsonMap;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * This method calculates the name of the specified user's home site.
     * This is based on the current naming convention used in the site import code during user account activation.
     * 
     * @param userEmail the user's email address
     * @return the name of that user's home site.
     */
    private String getSiteShortNameForUser(String userEmail)
    {
        return userEmail.replaceAll("\\.", "-").replaceAll("@", "-");
    }
    
    /**
     * This method checks that the specified site is usable for issuing invitations.
     * @param inviterUser
     * @param tenantOfSite
     * @param siteShortName
     */
    private void assertThatSiteIsValidForInvitations(final String inviterUser, final String tenantOfSite, final String siteShortName) throws Exception
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Pair<String, String>>()
        {
            @Override
            public Pair<String, String> doWork() throws Exception
            {
                // Ensure that the site to which the invitee will be invited exists
                SiteInfo site = siteService.getSite(siteShortName);
                assertNotNull("No site called " + siteShortName, site);
                assertEquals("Site had wrong visibility", SiteVisibility.PRIVATE, site.getVisibility());
                
                return null;
            }
        }, inviterUser, tenantOfSite);
        
        log.debug("Invitation is to site " + siteShortName + ", which is valid.");
    }
}
