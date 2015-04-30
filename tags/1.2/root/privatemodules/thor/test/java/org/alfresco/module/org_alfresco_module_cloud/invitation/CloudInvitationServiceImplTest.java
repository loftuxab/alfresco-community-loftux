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
package org.alfresco.module.org_alfresco_module_cloud.invitation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.Clock;
import org.activiti.engine.runtime.Execution;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.workflow.BPMEngineRegistry;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Integration tests for the {@link CloudInvitationService}.
 * <p/>
 * There are a number of variations in the invitation process, but the following are always true:
 * <ul>
 * <li>The inviter user always exists.</li>
 * <li>The site to which the invitation is issued must always exist.</li>
 * <li>The inviter must have the requisite permissions to issue an invitation.</li>
 * <li>The invitation workflow runs in the same tenant as the site, which is the inviter's current tenant.</li>
 * </ul>
 * <p/>
 * Having accepted the above facts, there are a number of possible variations for an invitation:
 * <ul>
 * <li>The inviter may be an INTERNAL or EXTERNAL member of the site's tenant (Cloud app probably only supports invitations
 *     from internal members).</li>
 * <li>The invitee may EXIST or NOT EXIST as a user in the system.</li>
 * <li>The invitee may be in the same tenant as the site (INTERNAL) or in another tenant (EXTERNAL).</li>
 * <li>The invitee may ACCEPT or DECLINE.</li>
 * </ul>
 * 
 * The invitee can also 'accept-alt-email'. This is an alternate flow where an invitee is invited at e.g.
 * userx@alfresco.com but already has an account with the address userx@someothercompany.com
 * and wishes to accept in that account.
 * 
 * @author Neil Mc Erlean
 * @since Thor.
 */
public class CloudInvitationServiceImplTest
{
    private static final Log log = LogFactory.getLog(CloudInvitationServiceImplTest.class);
    
    private static ApplicationContext testContext;
    
    // Services
    private static AccountService            accountService;
    private static CloudInvitationService    invitationService;
    private static DirectoryService          directoryService;
    private static RegistrationService       registrationService;
    private static SiteService               siteService;
    private static RetryingTransactionHelper transactionHelper;
    private static WorkflowService           workflowService;
    private static ProcessEngine             processEngine;
    
    private CloudTestContext cloudContext;
    private String tenant1;
    private String tenant2;
    private String publicTenant;
    private String inviterT1;
    private String externalInviterT1_HomeT2;
    private String inviteeT1;
    private String inviteeT2;
    private String inviteePublic;
    private String siteShortName_T1;
    
    private final static String inviteeRole = SiteModel.SITE_COLLABORATOR;
    
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        testContext = ApplicationContextHelper.getApplicationContext();
        
        accountService = (AccountService)testContext.getBean("accountService");
        directoryService = (DirectoryService)testContext.getBean("directoryService");
        invitationService = (CloudInvitationService)testContext.getBean("cloudInvitationService");
        registrationService = (RegistrationService)testContext.getBean("registrationService");
        siteService = (SiteService)testContext.getBean("siteService");
        transactionHelper = (RetryingTransactionHelper)testContext.getBean("retryingTransactionHelper");
        workflowService = (WorkflowService)testContext.getBean("WorkflowService");
        processEngine = (ProcessEngine)testContext.getBean("activitiProcessEngine");
    }
    
    /**
     * Initialise various data required by the test.
     */
    @Before public void initTestData() throws Exception
    {
        cloudContext = new CloudTestContext(testContext);
        tenant1 = cloudContext.createTenantName("one");
        tenant2 = cloudContext.createTenantName("two");
        publicTenant = cloudContext.createTenantName("public");
        inviterT1 = cloudContext.createUserName("larry", tenant1);
        
        // This user has T2 as home tenant, but will be added to T1.
        externalInviterT1_HomeT2 = cloudContext.createUserName("imposter", tenant2);
        inviteeT1 = cloudContext.createUserName("curly", tenant1);
        inviteeT2 = cloudContext.createUserName("moe", tenant2);
        inviteePublic = cloudContext.createUserName("public.person", publicTenant);
        siteShortName_T1 = "testSite" + GUID.generate();
        
        // Create the test users - we do this as system in the default tenant.
        TenantRunAsWork<Void> createUsersWork = new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @SuppressWarnings("synthetic-access")
                    @Override
                    public Void execute() throws Throwable
                    {
                        registrationService.createUser(inviterT1, "f", "l", "password");
                        cloudContext.addUser(inviterT1);
                        cloudContext.addAccountDomain(tenant1);
                        
                        registrationService.createUser(externalInviterT1_HomeT2, "f", "l", "password");
                        registrationService.addUser(accountService.getAccountByDomain(tenant1).getId(), externalInviterT1_HomeT2);
                        cloudContext.addUser(externalInviterT1_HomeT2);
                        
                        registrationService.createUser(inviteeT1, "f", "l", "password");
                        cloudContext.addUser(inviteeT1);
                        // Same tenant as previous user.
                        
                        registrationService.createUser(inviteeT2, "f", "l", "password");
                        cloudContext.addUser(inviteeT2);
                        cloudContext.addAccountDomain(tenant2);
                        
                        registrationService.createUser(inviteePublic, "f", "l", "password");
                        cloudContext.addUser(inviteePublic);
                        cloudContext.addAccountDomain(publicTenant);
                        return null;
                    }
                });
                return null;
            }
        };
        TenantUtil.runAsUserTenant(createUsersWork, AuthenticationUtil.getSystemUserName(), TenantService.DEFAULT_DOMAIN);
        
        // Create the site to which the invitees will be invited - we do this as the inviter user in the inviter's tenant.
        TenantRunAsWork<Void> createSiteWork = new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        siteService.createSite("site-preset", siteShortName_T1, "site title", "site description", SiteVisibility.PRIVATE);
                        
                        // And there is a user who is external to this tenant, but who will be inviting others into this
                        // site. To do this, the user must be a manager of the site.
                        siteService.setMembership(siteShortName_T1, externalInviterT1_HomeT2, SiteModel.SITE_MANAGER);
                        return null;
                    }
                });
                return null;
            }
        };
        TenantUtil.runAsUserTenant(createSiteWork, inviterT1, tenant1);
        
        // Ensure each starts with no authorisation.
        AuthenticationUtil.clearCurrentSecurityContext();
    }
    
    /** Delete any temporary accounts created during the previous test */
    @After public void cleanup()
    {
        cloudContext.cleanup();
    }
    
    /**
     * This method starts a new invitation.
     * @param inviterUser the user who is issuing the invitation.
     * @param inviteeUser the user who is being invited.
     * @param inviteeRole the role with which the invitee is to join the site
     * @param siteShortName the site to which the invitee is invited
     * @param siteTenant the tenant holding the site.
     * @return a {@link CloudInvitation invitation} object.
     */
    private CloudInvitation issueInvitation(final String inviterUser, final String inviteeUser,
                                            final String inviteeRole, final String siteShortName, final String siteTenant)
    {
        CloudInvitation invitation =
        
        // The inviter issues an invitation - these are always issues in the site tenant.
        TenantUtil.runAsUserTenant(new TenantRunAsWork<CloudInvitation>()
        {
            @Override public CloudInvitation doWork() throws Exception
            {
                return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<CloudInvitation>()
                {
                    @Override
                    public CloudInvitation execute() throws Throwable
                    {
                        log.debug("Issuing invitation...");
                        log.debug("     <current tenant> '" + TenantUtil.getCurrentDomain() +"'");
                        log.debug("   inviter = " + inviterUser);
                        log.debug("   invitee = " + inviteeUser);
                        log.debug("   role    = " + inviteeRole);
                        log.debug("   site    = " + siteShortName);
                        
                        return invitationService.startInvitation(inviterUser, inviteeUser, inviteeRole, siteShortName, "Hello");
                    }
                });
            }
        }, inviterUser, siteTenant);
        
        return invitation;
    }
    
    /**
     * This method accepts the invitation using the email address in the invitation.
     * @param inviteeUser the user who is accepting the invitation.
     * @param invitation the invitation to be accepted.
     * @param siteTenant the tenant containing the site.
     */
    private void acceptInvitation(final String inviteeUser, final CloudInvitation invitation, final String siteTenant, final boolean inviteeExists)
    {
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                log.debug("Accepting invitation...");
                log.debug("     <current tenant> '" + TenantUtil.getCurrentDomain() +"'");
                log.debug("   invitee = " + inviteeUser);
                
                if (inviteeExists)
                {
                    invitationService.acceptInvitation(invitation.getId(), invitation.getKey());
                }
                else
                {
                    invitationService.acceptInvitationWithSignup(invitation.getId(), invitation.getKey(), "testFirstName", "testLastName", "password");
                }
                
                return null;
            }
        }, siteTenant);
    }
    
    /**
     * This method accepts the invitation but uses an alternative email address supplied by the invitee.
     * Note that this email address must already have a valid account.
     * 
     * @param inviteeUser the user who is accepting the invitation.
     * @param invitation the invitation to be accepted.
     * @param siteTenant the tenant containing the site.
     * @param alternativeInviteeEmail the email address on which the invitee wishes to accept the invitation.
     */
    private void acceptInvitationAtAlternativeEmail(final String inviteeUser, final CloudInvitation invitation,
                final String siteTenant, final String alternativeInviteeEmail)
    {
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                log.debug("Accepting invitation...");
                log.debug("     <current tenant> '" + TenantUtil.getCurrentDomain() +"'");
                log.debug("   invitee = " + inviteeUser + ", but accepting as " + alternativeInviteeEmail);
                
                invitationService.acceptInvitationAtAlternativeEmail(invitation.getId(), invitation.getKey(), alternativeInviteeEmail);
                
                return null;
            }
        }, siteTenant);
    }
    
    /**
     * Declines the invitation
     * @param inviteeUser the user who was invited.
     * @param invitation the invitation object.
     */
    private void declineInvitation(final String inviteeUser, final CloudInvitation invitation, final String siteTenant)
    {
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                log.debug("Accepting invitation...");
                log.debug("     <current tenant> '" + TenantUtil.getCurrentDomain() +"'");
                log.debug("   invitee = " + inviteeUser);
                
                invitationService.rejectInvitation(invitation.getId(), invitation.getKey());
                
                return null;
            }
        }, siteTenant);
    }
    
    private void cancelInvitation(final String inviterUser, final CloudInvitation invitation, final String siteTenant)
    {
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        invitationService.cancelInvitation(invitation.getId(), invitation.getKey());
                        return null;
                    }
                });
                
                return null;
            }
        }, siteTenant);
    }
    
    /**
     * This method checks that the invitee has access to the inviter's tenant and also the correct role in the site.
     * 
     * @param inviteeRole the role the inviter was offered
     * @param siteShortName the short name of the site
     * @param invitee the invitee's username
     * @param inviter the inviter's username
     * @param siteTenant the inviter's tenant (also the site's tenant).
     */
    private void validateSiteMembershipAndAccountAccess(final String inviteeRole, final String siteShortName, final String invitee,
                                                        final String inviter, final String siteTenant)
    {
        ensureAccountAccess(invitee, siteTenant, true);
        ensureSiteMembership(inviteeRole, siteShortName, invitee, siteTenant);
    }
    
    /**
     * This method checks that the specified user has access to the specified tenant.
     * 
     * @param user the username to check.
     * @param tenantId the tenant.
     */
    private void ensureAccountAccess(final String user, final String tenantId, final boolean shouldHaveAccess)
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        boolean usersAccountsContainSpecifiedTenant = false;
                        
                        List<Long> accountIds = directoryService.getAllAccounts(user);
                        for (Long accountId : accountIds)
                        {
                            Account account = accountService.getAccount(accountId);
                            if (account.getTenantId().equals(tenantId))
                            {
                                usersAccountsContainSpecifiedTenant = true;
                            }
                        }
                        assertEquals("User's membership of the tenant was wrong", shouldHaveAccess, usersAccountsContainSpecifiedTenant);
                        return null;
                    }
                });
            }
        }, user, tenantId);
    }
    
    /**
     * This method checks that the invitee has the correct role in the inviter's site.
     * 
     * @param inviteeRole the role the invitee should have. (<tt>null</tt> means they should have no role.)
     * @param siteShortName the short name of the site
     * @param invitee the invitee's username
     * @param inviter the inviter's username
     * @param siteTenant the inviter's tenant (also the site's tenant).
     */
    private void ensureSiteMembership(final String inviteeRole, final String siteShortName,
                                      final String invitee, final String siteTenant)
    {
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        assertEquals("Invitee had wrong role", inviteeRole, siteService.getMembersRole(siteShortName, invitee));
                        return null;
                    }
                });
            }
            // Make this check as system to ensure permissions check succeeds. The invitee won't even be in this tenant.
        }, siteTenant);
    }
    
    /**
     * This test method checks that the {@link WorkflowModelCloudInvitation#WORKFLOW_DEFINITION_NAME cloud invitation workflow}
     * is deployed.
     */
    @Test public void invitationWorkflowWasDeployed() throws Exception
    {
        final TenantRunAsWork<Boolean> runAsWork = new TenantRunAsWork<Boolean>()
        {
            @Override
            public Boolean doWork() throws Exception
            {
                Boolean result = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>()
                {
                    @SuppressWarnings("synthetic-access")
                    public Boolean execute() throws Throwable
                    {
                        WorkflowDefinition wfDef = workflowService.getDefinitionByName(WorkflowModelCloudInvitation.WORKFLOW_DEFINITION_NAME);
                        return wfDef != null;
                    }
                });
                return result;
            }
        };
        
        // Although these workflows are deployed on the default tenant and shared across all tenants, the Cloud module
        // only requires that the workflows (definitions) be usable on all 'user' tenants.
        // We'll test on a tenant that we know to have been created as part of this test class.
        boolean workflowDeployedOnUserTenant = TenantUtil.runAsTenant(runAsWork, tenant1);
        assertTrue("Workflow definition missing", workflowDeployedOnUserTenant);
    }
    
    @Test public void inviteUserAndCancelInvitation() throws Exception
    {
        final CloudInvitation invitation = issueInvitation(inviterT1, inviteeT1, inviteeRole, siteShortName_T1, tenant1);
        
        cancelInvitation(inviterT1, invitation, tenant1);
        
        // Then the inviter ensures that this invitation is no longer there.
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                try
                {
                    invitationService.getInvitationStatus(invitation.getId(), invitation.getKey());
                }
                catch (NoInvitationWorkflowException expectedException)
                {
                    return null;
                }
                fail("Unexpectedly found live invitation workflow.");
                return null;
            }
        });
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    @Test public void invitePublicDomainUser() throws Exception
    {
        final CloudInvitation invitation = issueInvitation(inviterT1, inviteePublic, inviteeRole, siteShortName_T1, tenant1);
        
        acceptInvitation(inviteePublic, invitation, tenant1, true);
        
        validateSiteMembershipAndAccountAccess(inviteeRole, siteShortName_T1, inviteePublic, inviterT1, tenant1);
    }
    
    @Test public void internalInviterInvites_Existing_Internal_InviteeWho_Accepts() throws Exception
    {
        CloudInvitation invitation = issueInvitation(inviterT1, inviteeT1, inviteeRole, siteShortName_T1, tenant1);
        acceptInvitation(inviteeT1, invitation, tenant1, true);
        
        validateSiteMembershipAndAccountAccess(inviteeRole, siteShortName_T1, inviteeT1, inviterT1, tenant1);
    }
    
    @Test public void internalInviterInvites_Existing_Internal_InviteeWho_Declines() throws Exception
    {
        CloudInvitation invitation = issueInvitation(inviterT1, inviteeT1, inviteeRole, siteShortName_T1, tenant1);
        declineInvitation(inviterT1, invitation, tenant1);
    }
    
    @Test public void internalInviterInvites_Existing_External_InviteeWho_Accepts() throws Exception
    {
        CloudInvitation invitation = issueInvitation(inviterT1, inviteeT2, inviteeRole, siteShortName_T1, tenant1);
        acceptInvitation(inviteeT2, invitation, tenant1, true);
        
        validateSiteMembershipAndAccountAccess(inviteeRole, siteShortName_T1, inviteeT2, inviterT1, tenant1);
    }
    
    @Test public void internalInviterInvites_Existing_External_InviteeWho_Declines() throws Exception
    {
        CloudInvitation invitation = issueInvitation(inviterT1, inviteeT2, inviteeRole, siteShortName_T1, tenant1);
        declineInvitation(inviteeT2, invitation, tenant1);
    }
    
    @Test public void internalInviterInvites_NonExistent_Internal_InviteeWho_Accepts() throws Exception
    {
        final String newInviteeT1 = cloudContext.createUserName("no.exist", tenant1);
        
        CloudInvitation invitation = issueInvitation(inviterT1, newInviteeT1, inviteeRole, siteShortName_T1, tenant1);
        acceptInvitation(newInviteeT1, invitation, tenant1, false);
        
        validateSiteMembershipAndAccountAccess(inviteeRole, siteShortName_T1, newInviteeT1, inviterT1, tenant1);
    }
    
    @Test public void internalInviterInvites_NonExistent_Internal_InviteeWho_Declines() throws Exception
    {
        final String newInviteeT1 = cloudContext.createUserName("no.exist.decline", tenant1);
        
        CloudInvitation invitation = issueInvitation(inviterT1, newInviteeT1, inviteeRole, siteShortName_T1, tenant1);
        declineInvitation(newInviteeT1, invitation, tenant1);
    }
    
    @Test public void internalInviterInvites_NonExistent_External_InviteeWho_Accepts() throws Exception
    {
        final String newInviteeT2 = cloudContext.createUserName("no.exist", tenant2);
        
        CloudInvitation invitation = issueInvitation(inviterT1, newInviteeT2, inviteeRole, siteShortName_T1, tenant1);
        acceptInvitation(newInviteeT2, invitation, tenant1, false);
        
        validateSiteMembershipAndAccountAccess(inviteeRole, siteShortName_T1, newInviteeT2, inviterT1, tenant1);
    }
    
    @Test public void internalInviterInvites_NonExistent_External_InviteeWho_Declines() throws Exception
    {
        final String newInviteeT2 = cloudContext.createUserName("no.exist.decline", tenant2);
        
        CloudInvitation invitation = issueInvitation(inviterT1, newInviteeT2, inviteeRole, siteShortName_T1, tenant1);
        declineInvitation(newInviteeT2, invitation, tenant1);
    }
    
    @Test public void externalInviterInvites_Existing_Internal_InviteeWho_Accepts() throws Exception
    {
        CloudInvitation invitation = issueInvitation(externalInviterT1_HomeT2, inviteeT1, inviteeRole, siteShortName_T1, tenant1);
        acceptInvitation(inviteeT1, invitation, tenant1, true);
        
        validateSiteMembershipAndAccountAccess(inviteeRole, siteShortName_T1, inviteeT1, externalInviterT1_HomeT2, tenant1);
    }
    
    @Test public void externalInviterInvites_Existing_Internal_InviteeWho_Declines() throws Exception
    {
        CloudInvitation invitation = issueInvitation(externalInviterT1_HomeT2, inviteeT1, inviteeRole, siteShortName_T1, tenant1);
        declineInvitation(inviteeT1, invitation, tenant1);
    }
    
    @Test public void externalInviterInvites_Existing_External_InviteeWho_Accepts() throws Exception
    {
        CloudInvitation invitation = issueInvitation(externalInviterT1_HomeT2, inviteeT2, inviteeRole, siteShortName_T1, tenant1);
        acceptInvitation(inviteeT2, invitation, tenant1, true);
        
        validateSiteMembershipAndAccountAccess(inviteeRole, siteShortName_T1, inviteeT2, externalInviterT1_HomeT2, tenant1);
    }
    
    @Test public void externalInviterInvites_Existing_External_InviteeWho_Declines() throws Exception
    {
        CloudInvitation invitation = issueInvitation(externalInviterT1_HomeT2, inviteeT2, inviteeRole, siteShortName_T1, tenant1);
        declineInvitation(inviteeT2, invitation, tenant1);
    }
    
    @Test public void externalInviterInvites_NonExistent_Internal_InviteeWho_Accepts() throws Exception
    {
        final String newInviteeT1 = cloudContext.createUserName("no.exist", tenant1);
        
        CloudInvitation invitation = issueInvitation(externalInviterT1_HomeT2, newInviteeT1, inviteeRole, siteShortName_T1, tenant1);
        acceptInvitation(newInviteeT1, invitation, tenant1, false);
        
        validateSiteMembershipAndAccountAccess(inviteeRole, siteShortName_T1, newInviteeT1, externalInviterT1_HomeT2, tenant1);
    }
    
    @Test public void externalInviterInvites_NonExistent_Internal_InviteeWho_Declines() throws Exception
    {
        final String newInviteeT1 = cloudContext.createUserName("no.exist.decline", tenant1);
        
        CloudInvitation invitation = issueInvitation(externalInviterT1_HomeT2, newInviteeT1, inviteeRole, siteShortName_T1, tenant1);
        declineInvitation(newInviteeT1, invitation, tenant1);
    }
    
    @Test public void externalInviterInvites_NonExistent_External_InviteeWho_Accepts() throws Exception
    {
        final String newInviteeT2 = cloudContext.createUserName("no.exist", tenant2);
        
        CloudInvitation invitation = issueInvitation(externalInviterT1_HomeT2, newInviteeT2, inviteeRole, siteShortName_T1, tenant1);
        acceptInvitation(newInviteeT2, invitation, tenant1, false);
        
        validateSiteMembershipAndAccountAccess(inviteeRole, siteShortName_T1, newInviteeT2, externalInviterT1_HomeT2, tenant1);
    }
    
    @Test public void externalInviterInvites_NonExistent_External_InviteeWho_Declines() throws Exception
    {
        final String newInviteeT2 = cloudContext.createUserName("no.exist.decline", tenant2);
        
        CloudInvitation invitation = issueInvitation(externalInviterT1_HomeT2, newInviteeT2, inviteeRole, siteShortName_T1, tenant1);
        declineInvitation(newInviteeT2, invitation, tenant1);
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    @Test public void acceptInvitationAtAlternativeEmailAddress() throws Exception
    {
        // One user invites another to a site. But the invitee wishes to accept using an alternative email address.
        // This email address could be in the same tenant as that at which they've been invited or it could be in
        // a different tenant. We'll use one in a different tenant in this test.
        
        // Note: inviteeT1 is invited but inviteeT2 accepts
        CloudInvitation invitation = issueInvitation(inviterT1, inviteeT1, inviteeRole, siteShortName_T1, tenant1);
        acceptInvitationAtAlternativeEmail(inviteeT1, invitation, tenant1, inviteeT2);
        
        // The user who was invited was already in the tenant, but should not have access to the site - that user didn't accept.
        validateSiteMembershipAndAccountAccess(null, siteShortName_T1, inviteeT1, inviterT1, tenant1);
        
        // The user who accepted should be in the tenant and have access to the site.
        validateSiteMembershipAndAccountAccess(inviteeRole, siteShortName_T1, inviteeT2, inviterT1, tenant1);
    }
    
    /**
     * A user cannot accept an invitation at an email address which does not have a valid account.
     * @since Thor Phase 2 Sprint 1
     */
    @Test public void acceptInvitationAtAlternativeEmailAddressWhichDoesntExist() throws Exception
    {
        CloudInvitation invitation = issueInvitation(inviterT1, inviteeT1, inviteeRole, siteShortName_T1, tenant1);
        final String nonExistentUserId = "no.exist@no-exist.test";
        
        boolean acceptanceFailed = false;
        try
        {
            acceptInvitationAtAlternativeEmail(inviteeT1, invitation, tenant1, nonExistentUserId);
        }
        catch (InvalidInvitationAcceptanceException expected)
        {
            acceptanceFailed = true;
        }
        assertTrue("Invitee successfully accepted at a non-existent email", acceptanceFailed);
        
        // The user who was invited didn't accept and therefore cannot be in the site
        ensureSiteMembership(null, siteShortName_T1, inviteeT1, tenant1);
        
        // The user who tried to accept cannot do so as they don't exist and therefore cannot be in the tenant or in the site
        // Let's just make sure that they still don't exist i.e. that they haven't been created as a side-effect of the invitation process.
        assertFalse(directoryService.userExists(nonExistentUserId));
    }
    
    /**
     * Check all exepected reminders and check if process self-destructs after 28 days.
     */
    @Test public void checkinvitationReminderAndExpiration() throws Exception
    {
        CloudInvitation invitation = issueInvitation(inviterT1, inviteeT1, inviteeRole, siteShortName_T1, tenant1);
        
        // No transaction needed for the folowing operations, all READ's
        String processInstanceId = BPMEngineRegistry.getLocalId(invitation.getId());
        
        // Move time to trigger first reminder mail
        List<Execution> executions = processEngine.getRuntimeService()
            .createExecutionQuery()
            .processInstanceId(processInstanceId)
            .list();
        
        
        String executionId = null;
        for(Execution e : executions)
        {
            if(!((ExecutionEntity) e).isProcessInstanceType())
            {
                executionId = e.getId();
                break;
            }
        }
        
        // Count the number of jobs in the execution
        long jobCount = processEngine.getManagementService().createJobQuery().executionId(executionId).count();
        assertEquals(3, jobCount);
        
        // Roll activiti-time forward by 3 days and one minute
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        calendar.add(Calendar.MINUTE, 1);
        Clock actiClock = processEngine.getProcessEngineConfiguration().getClock();
        actiClock.setCurrentTime(calendar.getTime());
        
        // Check if timer-job is executed
        boolean firstReminderSent = waitUntilJobExecuted(jobCount, executionId);
        assertTrue("First reminder timer hasn't fired", firstReminderSent);
        
        firstReminderSent = waitUntillMailSentCountChanged(1, processInstanceId);
        assertTrue("First reminder mail wasn't sent", firstReminderSent);
        
        // Check the next 3 reminders       
        for(int i=0; i < 3; i++)
        {
            int numberOfDays =  7 * (i + 1);
            calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, numberOfDays);
            calendar.add(Calendar.MINUTE, 1);
            actiClock.setCurrentTime(calendar.getTime());
                 
            boolean reminderSent = waitUntillMailSentCountChanged(2 + i, processInstanceId);
            assertTrue("Reminder at " + numberOfDays + " days email wasn't sent", reminderSent);
        }
        
        // Finally, check if the flow stops itself after 28 days
        calendar.add(Calendar.DAY_OF_MONTH, 28);
        calendar.add(Calendar.MINUTE, 1);
        actiClock.setCurrentTime(calendar.getTime());
           
        boolean finishTimerFired = waitUntilJobExecuted(1, executionId);
        assertTrue("Finish timer didn't fire",finishTimerFired);
        
        assertEquals("Process isn't finished", 0, 
            processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstanceId).count());
    }
    
    
    @Test public void checkManualreminder() throws Exception
    {
        // Send the invite
        final CloudInvitation invitation = issueInvitation(inviterT1, inviteeT1, inviteeRole, siteShortName_T1, tenant1);
        
        Integer currentMailCount = (Integer) processEngine.getRuntimeService()
            .getVariable(BPMEngineRegistry.getLocalId(invitation.getId()), WorkflowModelCloudInvitation.WF_PROP_MAILS_SENT_ACTIVITI);
        
        assertEquals((Integer)1, currentMailCount);
        
        // Ask for a manual reminder (in right tennant)
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        invitationService.remindInvitee(invitation.getId(), invitation.getKey());
                        return null;
                    }
                });
            }
        }, AuthenticationUtil.getSystemUserName(), tenant1);
        
        currentMailCount = (Integer) processEngine.getRuntimeService()
        .getVariable(BPMEngineRegistry.getLocalId(invitation.getId()), WorkflowModelCloudInvitation.WF_PROP_MAILS_SENT_ACTIVITI);
        
        assertEquals((Integer) 2, currentMailCount);
        
        // Validate no registration-data has been lost
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        CloudInvitation newinvitation = invitationService.getInvitationStatus(invitation.getId(), invitation.getKey());
                        assertEquals(invitation.getId(), newinvitation.getId());
                        assertEquals(invitation.getInviteeEmail(), newinvitation.getInviteeEmail());
                        assertEquals(invitation.getInviterEmail(), newinvitation.getInviterEmail());
                        assertEquals(invitation.getSiteShortName(), newinvitation.getSiteShortName());
                        assertEquals(invitation.getSiteTitle(), newinvitation.getSiteTitle());
                       
                        return null;
                    }
                });
            }
        }, AuthenticationUtil.getSystemUserName(), tenant1);
        
    }
    
    private boolean waitUntilJobExecuted(long initialCount, String executionId)
    {
        boolean executed = false;
        
        // Wait 20 times .5 second
        for(int i=0; i<20; i++)
        {
            long currentCount = processEngine.getManagementService().createJobQuery().executionId(executionId).count();
            if(currentCount < initialCount)
            {
                executed = true;
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Ignore this
            }
            
        }
        return executed;
    }
    
    private boolean waitUntillMailSentCountChanged(Integer initialCount, String processInstanceId)
    {
        boolean executed = false;
        
        // Wait 20 times .5 second
        for(int i=0; i<20; i++)
        {
            Integer currentCount = (Integer) processEngine.getRuntimeService().getVariable(processInstanceId, WorkflowModelCloudInvitation.WF_PROP_MAILS_SENT_ACTIVITI);
            if(currentCount != null && currentCount > initialCount)
            {
                executed = true;
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Ignore this
            }
            
        }
        return executed;
    }
}
