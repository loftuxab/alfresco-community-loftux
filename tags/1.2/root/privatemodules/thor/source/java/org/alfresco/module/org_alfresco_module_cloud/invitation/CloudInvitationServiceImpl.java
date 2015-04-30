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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.analytics.Analytics;
import org.alfresco.module.org_alfresco_module_cloud.analytics.Analytics.SiteInviteResponse;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigAdminService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.directory.InvalidEmailAddressException;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck.FailureReason;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.NoSuchPersonException;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowException;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.collections.CollectionUtils;
import org.alfresco.util.collections.Function;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class CloudInvitationServiceImpl implements CloudInvitationService
{
    // Implementation note on invitations and tenants/accounts.
    //
    // There are quite a few permutations of the Cloud site invitation that must be understood.
    // But first some terminology:
    //   the site tenant: the tenant which contains the site.
    //   internal user (of a tenant): a user who has the tenant as their home tenant.
    //   external user (of a tenant): a user who has the tenant as one of their secondary tenants.
    //
    // The inviter may be an internal or external user of the site tenant.
    // The invitee may exist or may not exist as a user of Alfresco Cloud.
    // The invitee may be an internal or external user (or potential user) of the site tenant.
    //
    // The most complex invitation example is the one where a user who is an external user of the site tenant
    // invites another external user to that site. Obviously to do so the inviter, must themselves be
    // a member of the site tenant.
    // e.g. Tenant 'alfresco.com' has a private site 'Site X'.
    //      user1@ibm.example has been invited as a member of that site.
    //      user1@ibm.example then invites user2@microsoft.example to the site.
    
    private static final Log log = LogFactory.getLog(CloudInvitationServiceImpl.class);
    
    private AccountService accountService;
    private DirectoryService directoryService;
    private EmailAddressService emailAddressService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
    private PersonService personService;
    private RegistrationService registrationService;
    private SiteService siteService;
    private WorkflowService workflowService;
    private SAMLConfigAdminService samlConfigAdminService;
    
    private String timerRemind3 = "P3D";
    private String timerRemind7 = "R3/P7D";
    private String timerEnd = "P28D";    
    
    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }
    
    public void setDirectoryService(DirectoryService directoryService)
    {
        this.directoryService = directoryService;
    }
    
    public void setEmailAddressService(EmailAddressService emailAddressService)
    {
        this.emailAddressService = emailAddressService;
    }
    
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }
    
    public void setRegistrationService(RegistrationService registrationService)
    {
        this.registrationService = registrationService;
    }
    
    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }
    
    public void setWorkflowService(WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }
    
    public void setSamlConfigAdminService(SAMLConfigAdminService service)
    {
        this.samlConfigAdminService = service;
    }
    
    public void setTimerRemind3(String timerRemind3)
    {
        this.timerRemind3 = timerRemind3;
    }

    public void setTimerRemind7(String timerRemind7)
    {
        this.timerRemind7 = timerRemind7;
    }

    public void setTimerEnd(String timerEnd)
    {
        this.timerEnd = timerEnd;
    }
    
    
    @Override public CloudInvitation startInvitation(final String inviterEmail,
                                                     String inviteeEmail, String inviteeRole,
                                                     String siteShortName, String inviterMessage)
    {
        if (log.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("startInvitation site:").append(siteShortName)
               .append(" invitee:").append(inviteeEmail)
               .append(" role:").append(inviteeRole)
               .append(" inviter:").append(inviterEmail);
            log.debug(msg.toString());
            log.debug("Invitation is starting on tenant '" + TenantUtil.getCurrentDomain() + "'");
        }
        
        // validate invitee email address
        inviteeEmail = inviteeEmail.toLowerCase();
        String inviteeDomain = emailAddressService.getDomain(inviteeEmail);
        FailureReason failureReason = checkEmailValidForInvitations(inviteeEmail);
        
        if (failureReason != null)
        {
            throw new InvalidEmailAddressException("Invitee address " + inviteeEmail + " is not accepted: " + failureReason);
        }
        inviteeEmail = emailAddressService.getAddress(inviteeEmail).toLowerCase();
        
        
        // Get profile information from the inviter person object. To do this, we must switch to a tenant which contains the person object for that user.
        // The person object for that user will be present in any tenant where the user is present, but we'll go to the user's home tenant as the person will always be there.
        Account inviterHomeAccount = registrationService.getHomeAccount(inviterEmail);
        String inviterTenantId = inviterHomeAccount.getTenantId();
        
        final Map<QName, Serializable> personProps = TenantUtil.runAsUserTenant(new TenantRunAsWork<Map<QName, Serializable>>()
        {
            @Override
            public Map<QName, Serializable> doWork() throws Exception
            {
                NodeRef person = personService.getPerson(inviterEmail, false);
                return nodeService.getProperties(person);
            }
        }, inviterEmail, inviterTenantId);
        
        
        // Reject invitations to sites that do not exist.
        SiteInfo site = siteService.getSite(siteShortName);
        if (site == null)
        {
            throw new CloudInvitationServiceException("Cannot issue invitation to non-existent site: " + siteShortName);
        }
        
        // Get the (latest) workflow definition for cloud site invitation.
        WorkflowDefinition invitationDefinition = workflowService.getDefinitionByName(WorkflowModelCloudInvitation.WORKFLOW_DEFINITION_NAME);
        
        // create workflow properties
        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        
        props.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, I18NUtil.getMessage("siwf_invitation.workflow.description"));
        
        // The inviter
        props.put(WorkflowModelCloudInvitation.WF_PROP_INVITER_EMAIL_ADDRESS, inviterEmail);
        props.put(WorkflowModelCloudInvitation.WF_PROP_INVITER_FIRST_NAME, personProps.get(ContentModel.PROP_FIRSTNAME));
        props.put(WorkflowModelCloudInvitation.WF_PROP_INVITER_LAST_NAME, personProps.get(ContentModel.PROP_LASTNAME));
        props.put(WorkflowModelCloudInvitation.WF_INVITER_PERSONAL_MESSAGE, inviterMessage);
        
        // The invitee
        props.put(WorkflowModelCloudInvitation.WF_PROP_INVITEE_EMAIL_ADDRESS, inviteeEmail);
        
        props.put(WorkflowModelCloudInvitation.WF_PROP_SITE_SHORT_NAME, siteShortName);
        
        props.put(WorkflowModelCloudInvitation.WF_PROP_SITE_TENANT_ID, TenantUtil.getCurrentDomain());
        String accountNameOfSiteTenant = accountService.getAccountByDomain(TenantUtil.getCurrentDomain()).getName();
        props.put(WorkflowModelCloudInvitation.WF_PROP_SITE_TENANT_TITLE, accountNameOfSiteTenant);
        
        String siteTitle = site.getTitle();
        props.put(WorkflowModelCloudInvitation.WF_PROP_SITE_TITLE, siteTitle);
        props.put(WorkflowModelCloudInvitation.WF_PROP_INVITEE_ROLE, inviteeRole);
        
        props.put(WorkflowModel.ASSOC_PACKAGE, workflowService.createPackage(null));
        String guid = GUID.generate();
        props.put(WorkflowModelCloudInvitation.WF_PROP_KEY, guid);
        props.put(WorkflowModelCloudInvitation.WF_PROP_TIMER_REMIND3, timerRemind3);
        props.put(WorkflowModelCloudInvitation.WF_PROP_TIMER_REMIND7, timerRemind7);
        props.put(WorkflowModelCloudInvitation.WF_PROP_TIMER_END, timerEnd);
        
        // start the workflow
        CloudInvitationImpl invitation;
        WorkflowPath path = workflowService.startWorkflow(invitationDefinition.getId(), props);
        if (path.isActive())
        {
            final String pathInstanceId = path.getInstance().getId();
            
            WorkflowTask startTask = workflowService.getStartTask(pathInstanceId);
            workflowService.endTask(startTask.getId(), null);
            
            invitation = new CloudInvitationImpl(pathInstanceId, guid);
            invitation.setInviterEmail(inviterEmail);
            invitation.setInviteeEmail(inviteeEmail);
            invitation.setSiteShortName(siteShortName);
            invitation.setSiteTitle(siteTitle);
        }
        else
        {
            invitation = new CloudInvitationImpl(null, null);
        }
        
        // Analytics event.
        boolean inviteeIsNewUser = ! directoryService.userExists(inviteeEmail);
        boolean inviteeIsExternalUser = ! inviteeDomain.equals(emailAddressService.getDomain(inviterEmail));
        Analytics.record_SiteInvite(inviteeIsNewUser, inviteeIsExternalUser);
        
        return invitation;
    }

    /**
     * This method checks whether the specified email is valid for a site invitation in the cloud.
     * Note that email validity for invitations does not match that for signup.
     * 
     * @param inviteeEmail
     * @param inviteeDomain
     * @return a FailureReason if there is one, else <tt>null</tt>.
     * @since Thor Phase 2 Sprint 1
     */
    private FailureReason checkEmailValidForInvitations(String email)
    {
        if (!emailAddressService.isAcceptedAddress(email))
        {
            throw new InvalidEmailAddressException("Invitee address " + email + " is not a valid address");
        }
        
        String emailDomain = emailAddressService.getDomain(email);
        DomainValidityCheck check = emailAddressService.validateDomain(emailDomain);
        
        FailureReason reason = check.getFailureReason();
        
        // Although 'PUBLIC' emails may be invalid elsewhere (e.g. signup), they are allowed for invitations
        if (reason != null && reason.equals(FailureReason.PUBLIC))
        {
            reason = null;
        }
        return reason;
    }
    
    // CLOUD-1360 - switch to correct tenant (if current tenant is SAML-enabled and invitation is for another tenant)
    private String getTenantDomain(String workflowId)
    {
        String runAsTenantDomain = TenantUtil.getCurrentDomain();
        
        if (samlConfigAdminService.isEnabled(runAsTenantDomain))
        {
            Map<QName, Serializable> props = workflowService.getPathProperties(workflowId);
            String siteTenantId = ((String) props.get(WorkflowModelCloudInvitation.WF_PROP_SITE_TENANT_ID));
            
            if (! runAsTenantDomain.equals(siteTenantId))
            {
                runAsTenantDomain = siteTenantId;
            }
        }
        
        return runAsTenantDomain;
    }
    
    @Override
    public CloudInvitation acceptInvitation(final String workflowId, final String key)
    {
        return TenantUtil.runAsTenant(new TenantRunAsWork<CloudInvitation>()
        {
            public CloudInvitation doWork() throws Exception
            {
                InvitationAcceptanceStrategy acceptanceStrategy = new SimpleInvitationAcceptanceStrategy(workflowId, key);
                return acceptanceStrategy.acceptInvitation();
            }
        }, getTenantDomain(workflowId));
    }
    
    @Override
    public CloudInvitation acceptInvitationWithSignup(final String workflowId, final String key, final String inviteeFirstName, final String inviteeLastName, final String inviteePassword)
    {
        return TenantUtil.runAsTenant(new TenantRunAsWork<CloudInvitation>()
        {
            public CloudInvitation doWork() throws Exception
            {
                InvitationAcceptanceStrategy acceptanceStrategy = new SignupInvitationAcceptanceStrategy(workflowId, key, inviteeFirstName, inviteeLastName, inviteePassword);
                return acceptanceStrategy.acceptInvitation();
            }
        }, getTenantDomain(workflowId));
    }
    
    @Override
    public CloudInvitation acceptInvitationAtAlternativeEmail(final String workflowId, final String key, final String alternativeEmail)
    {
        return TenantUtil.runAsTenant(new TenantRunAsWork<CloudInvitation>()
        {
            public CloudInvitation doWork() throws Exception
            {
                InvitationAcceptanceStrategy acceptanceStrategy = new AlternativeEmailInvitationAcceptanceStrategy(workflowId, key, alternativeEmail);
                return acceptanceStrategy.acceptInvitation();
            }
        }, getTenantDomain(workflowId));
    }

    
    private WorkflowTask getTaskForWorkflowPath(String workflowId)
    {
    	WorkflowTaskQuery processTaskQuery = new WorkflowTaskQuery();
    	processTaskQuery.setProcessId(workflowId);
        List<WorkflowTask> tasks = workflowService.queryTasks(processTaskQuery, false);
        
        if (tasks == null || tasks.size() == 0)
        {
            throw new CloudInvitationServiceException("Invalid invitation identifier: " + workflowId);
        }
        WorkflowTask task = tasks.get(0);
        return task;
    }

    
    @Override
    public CloudInvitation rejectInvitation(final String workflowId, final String key)
    {
        log.debug("Rejecting invitation on tenant '" + TenantUtil.getCurrentDomain() + "'");
        
        validateInvitationKeyAndId(workflowId, key);
        
        if (log.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("rejectInvitation id:").append(workflowId);
            log.debug(msg.toString());
        }
        
        return TenantUtil.runAsTenant(new TenantRunAsWork<CloudInvitation>()
        {
            public CloudInvitation doWork() throws Exception
            {
                CloudInvitation existingInvitation = getCloudInvitationFromWorkflow(workflowId, key);
                
                WorkflowTask task = getTaskForWorkflowPath(workflowId);
                
                String inviteeEmailAddress = (String) task.getProperties().get(WorkflowModelCloudInvitation.WF_PROP_INVITEE_EMAIL_ADDRESS);
                String inviterEmailAddress = (String) task.getProperties().get(WorkflowModelCloudInvitation.WF_PROP_INVITER_EMAIL_ADDRESS);
                
                Map<QName, Serializable> newProps = new HashMap<QName, Serializable>();
                newProps.put(WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_QNAME, WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_REJECT);
                workflowService.updateTask(task.getId(), newProps, null, null);
                workflowService.endTask(task.getId(), null);
                
                CloudInvitationImpl result = (CloudInvitationImpl)existingInvitation;
                result.setResponse(WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_REJECT);
                
                // Analytics event.
                boolean inviteeIsNewUser = ! directoryService.userExists(inviteeEmailAddress);
                boolean inviteeIsExternalUser = ! emailAddressService.getDomain(inviteeEmailAddress).equals(emailAddressService.getDomain(inviterEmailAddress));
                Analytics.record_SiteInviteResponse(inviterEmailAddress, SiteInviteResponse.REJECTED, inviteeIsNewUser, inviteeIsExternalUser);
                
                return result;
            }
        }, getTenantDomain(workflowId));
    }
    
    @Override
    public void cancelInvitation(final String workflowId, String key)
    {
        log.debug("Cancelling invitation on tenant '" + TenantUtil.getCurrentDomain() + "'");
        
        validateInvitationKeyAndId(workflowId, key);
        
        if (log.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("cancelInvitation id:").append(workflowId);
            log.debug(msg.toString());
        }
        
        TenantUtil.runAsTenant(new TenantRunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                WorkflowTask task = getTaskForWorkflowPath(workflowId);
                
                Map<QName, Serializable> newProps = new HashMap<QName, Serializable>();
                newProps.put(WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_QNAME, WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_CANCEL);
                workflowService.updateTask(task.getId(), newProps, null, null);
                workflowService.endTask(task.getId(), null);
                
                return null;
            }
        }, getTenantDomain(workflowId));
    }
    
    public CloudInvitation getInvitationStatus(final String workflowId, final String key)
    {
        log.debug("Getting invitation-status on tenant '" + TenantUtil.getCurrentDomain() + "'");
        
        validateInvitationKeyAndId(workflowId, key);
        
        return TenantUtil.runAsTenant(new TenantRunAsWork<CloudInvitation>()
        {
            public CloudInvitation doWork() throws Exception
            {
                CloudInvitation existingInvitation = getCloudInvitationFromWorkflow(workflowId, key);
                return existingInvitation;
            }
        }, getTenantDomain(workflowId));
    }
    
    @Override
    public void addInviteeToSite(final String inviterEmail,final String siteShortName, final String inviteeEmail, final String inviteeRole)
    {
        // At this point we know that the inviteeEmail represents a user that exists and is registered.
        String currentTenantID = TenantUtil.getCurrentDomain();
        Account account = accountService.getAccountByDomain(currentTenantID);
        
        // Add the user to the domain if they are not in it already.
        boolean inviteeIsUserInSiteDomain = isUserMemberOfDomain(account, inviteeEmail);
        if ( !inviteeIsUserInSiteDomain)
        {
            registrationService.addUser(account.getId(), inviteeEmail);
        }
        
        // It is the inviterEmail user who issued the invitation and who should therefore add the user.
        // cf. the nominated-invitation in 'standard' Alfresco.
        // Note that we do this in the tenant in which the workflow was initiated - which will be the tenant
        // that contains the site.
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                siteService.setMembership(siteShortName, inviteeEmail, inviteeRole);
                
                return null;
            }
        }, inviterEmail);
    }
    
    /**
     * Is the specified email address a member of the given domain (as home or as secondary)
     */
    private boolean isUserMemberOfDomain(Account account, String email)
    {
        List<Long> accounts = directoryService.getAllAccounts(email);
        return account == null ? false : accounts.contains(account.getId());
    }
    
    /**
     * This method converts the specified workflowId and key into a {@link CloudInvitation} object using the
     * specified {@link WorkflowTask}.
     * 
     * @param workflowId
     * @param key
     * @param task this must be a valid task for an inflight workflow or the start task for a completed workflow.
     */
    private CloudInvitation getCloudInvitationFromWorkflow(final String workflowId, final String key, WorkflowTask task)
    {
        Map<QName, Serializable> props = task.getProperties();
        
        // The key has already been validated, so the check below shouldn't strictly be necessary.
        String recoveredKey = (String) props.get(WorkflowModelCloudInvitation.WF_PROP_KEY);
        if (recoveredKey == null || !recoveredKey.equals(key))
        {
            throw new CloudInvitationServiceException("Invalid invitation identifier: " + workflowId + ", " + key);
        }
        
        CloudInvitationImpl result = new CloudInvitationImpl(workflowId, key);
        
        result.setStartDate((Date)props.get(WorkflowModel.PROP_START_DATE));
        
        final Serializable inviteeEmail = props.get(WorkflowModelCloudInvitation.WF_PROP_INVITEE_EMAIL_ADDRESS);
        result.setInviteeEmail((String) inviteeEmail);
        result.setInviteeFirstName((String) props.get(WorkflowModelCloudInvitation.WF_PROP_INVITEE_FIRST_NAME));
        result.setInviteeLastName((String) props.get(WorkflowModelCloudInvitation.WF_PROP_INVITEE_LAST_NAME));
        
        result.setInviterEmail((String) props.get(WorkflowModelCloudInvitation.WF_PROP_INVITER_EMAIL_ADDRESS));
        result.setInviterFirstName((String) props.get(WorkflowModelCloudInvitation.WF_PROP_INVITER_FIRST_NAME));
        result.setInviterLastName((String) props.get(WorkflowModelCloudInvitation.WF_PROP_INVITER_LAST_NAME));
        
        result.setInviteeRole((String) props.get(WorkflowModelCloudInvitation.WF_PROP_INVITEE_ROLE));
        
        result.setSiteShortName((String) props.get(WorkflowModelCloudInvitation.WF_PROP_SITE_SHORT_NAME));
        result.setSiteTitle((String) props.get(WorkflowModelCloudInvitation.WF_PROP_SITE_TITLE));
        
        result.setSiteTenantId((String) props.get(WorkflowModelCloudInvitation.WF_PROP_SITE_TENANT_ID));
        result.setSiteTenantTitle((String) props.get(WorkflowModelCloudInvitation.WF_PROP_SITE_TENANT_TITLE));
        
        // And is the invitee activated?
        boolean inviteeIsActivated = registrationService.isActivatedEmailAddress((String) inviteeEmail);
        result.setInviteeIsActivated(Boolean.valueOf(inviteeIsActivated));
        
        // And is the invitee already a member of the tenant containing the site to which the invitee is invited?
        // If they are, then they will have a cm:person node in that tenant.
        boolean inviteeIsMember = personService.personExists((String)inviteeEmail);
        result.setInviteeIsMember(Boolean.valueOf(inviteeIsMember));
            
        return result;
    }
    
    /**
     * This method converts the specified workflowId and key into a {@link CloudInvitation} object.
     * 
     * @param workflowId the id of an existing workflow instance.
     * @param key the unique key assigned to that workflow
     * @return a {@link CloudInvitation} object.
     * @throws CloudInvitationServiceException if the workflowId does not correspond to an existing workflow instance
     *         or if the supplied key does not match the key on the workflow instance.
     */
    private CloudInvitation getCloudInvitationFromWorkflow(final String workflowId, final String key)
    {
        // Note that we cannot call workflowService.getPathProperties() as the workflow may be complete (inactive)
        // and this method throws exceptions for inactive workflows.
        //
        // So we must go to the start task to retrieve the data
        WorkflowTask startTask = workflowService.getStartTask(workflowId);
        
        return getCloudInvitationFromWorkflow(workflowId, key, startTask);
    }
    
    /**
     * This method validates the id and key associated with a particular invitation.
     * 
     * @param id the workflow id
     * @param key the unique key
     * @throws NoInvitationWorkflowException if the invitation workflow could not be found or if the key did not match.
     */
    private void validateInvitationKeyAndId(final String id, final String key)
    {
        WorkflowInstance workflowInstance = null;
        try
        {
            workflowInstance = workflowService.getWorkflowById(id);
        }
        catch (WorkflowException ignored)
        {
            // Intentionally empty.
        }
        
        if (workflowInstance == null)
        {
            throw new NoInvitationWorkflowException("Invalid invitation identifier: " + id + ", " + key);
        }
        
        String recoveredKey;
        // The mechanism for retrieving the key depends on whether the workflow is active or not.
        if ( workflowInstance.isActive())
        {
            // If the workflow is active we will be able to read the path properties.
            Map<QName, Serializable> pathProps = workflowService.getPathProperties(id);
            
            recoveredKey = (String) pathProps.get(WorkflowModelCloudInvitation.WF_PROP_KEY);
        }
        else
        {
            throw new NoInvitationWorkflowException("Invalid invitation identifier: " + id + ", " + key);
        }
        if (recoveredKey == null || !recoveredKey.equals(key))
        {
            throw new NoInvitationWorkflowException("Invalid invitation identifier: " + id + ", " + key);
        }
    }
    
    @Override public List<CloudInvitation> listPendingInvitationsForInvitee(String username, int limit)
    {
        log.debug("Listing pending invitations for invitee on tenant '" + TenantUtil.getCurrentDomain() + "'");
        
        ParameterCheck.mandatoryString("username", username);
        if (limit <= 0)
        {
            limit = -1;
        }
        
        Map<QName, String> taskProps = new HashMap<QName, String>();
        taskProps.put(WorkflowModelCloudInvitation.WF_PROP_INVITEE_EMAIL_ADDRESS, username);
        
        List<CloudInvitation> invitations = queryForInvitationTasks(taskProps, limit);
        return invitations;
    }
    
    @Override public List<CloudInvitation> listPendingInvitationsForSite(String siteShortName, String inviteeUserName, int limit)
    {
        log.debug("Listing pending invitations for site on tenant '" + TenantUtil.getCurrentDomain() + "'");
        
        ParameterCheck.mandatoryString("siteShortName", siteShortName);
        if (limit <= 0)
        {
            limit = -1;
        }
         
        Map<QName, String> taskProps = new HashMap<QName, String>();
        taskProps.put(WorkflowModelCloudInvitation.WF_PROP_SITE_SHORT_NAME, siteShortName);
        if (inviteeUserName != null)
        {
            taskProps.put(WorkflowModelCloudInvitation.WF_PROP_INVITEE_EMAIL_ADDRESS, inviteeUserName);
        }
        
        List<CloudInvitation> invitations = queryForInvitationTasks(taskProps, limit);
        return invitations;
    }
    
    
    @Override public void remindInvitee(final String workflowId, String key)
    {
        validateInvitationKeyAndId(workflowId, key);
        
        TenantUtil.runAsTenant(new TenantRunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                // Update outcome to 'remind'
                Map<QName, Serializable> taskProps = new HashMap<QName, Serializable>(1);
                taskProps.put(WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_QNAME, WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_REMIND);
                WorkflowTask currentTask = getTaskForWorkflowPath(workflowId);
                workflowService.updateTask(currentTask.getId(), taskProps, null, null);
                
                // Finish the task to trigger reminder
                workflowService.endTask(currentTask.getId(), null);
                
               return null;
            }
        }, getTenantDomain(workflowId));
    }
    
    private List<CloudInvitation> queryForInvitationTasks(final Map<QName, String> taskProperties, final int limit)
    {
        WorkflowTaskQuery query = new WorkflowTaskQuery();
        query.setActive(Boolean.TRUE);
        query.setTaskState(WorkflowTaskState.IN_PROGRESS);
        query.setLimit(limit);
        
        // This is instead of setProcessName(), which was used for jBPM workflows.
        query.setTaskName(WorkflowModelCloudInvitation.WF_INVITATION_ACCEPTANCE_PENDING_TASK);
        
        HashMap<QName, Object> props = new HashMap<QName, Object>();
        for (Map.Entry<QName, String> entry : taskProperties.entrySet())
        {
            props.put(entry.getKey(), entry.getValue());
        }
        query.setProcessCustomProps(props);
        
        List<WorkflowTask> tasks = workflowService.queryTasks(query);
        
        List<CloudInvitation> result = new ArrayList<CloudInvitation>(tasks.size());
        
        for (WorkflowTask task : tasks)
        {
            Map<QName, Serializable> taskProps = task.getProperties();
            final String id = task.getPath().getInstance().getId();
            final String key = (String) taskProps.get(WorkflowModelCloudInvitation.WF_PROP_KEY);
            
            // This should never happen, but just in case
            if (key == null)
            {
                throw new CloudInvitationServiceException("Unexpected null key");
            }
            
            CloudInvitation invitation = getCloudInvitationFromWorkflow(id, key);
            
            // Get cm:person nodes in this tenant for the inviter and the invitee.
            // Note that if the invitee is not a member of this tenant, which would obviously
            // include the case where the invitee is not a registered user, then the inviteeNode
            // will be null.
            NodeRef inviterNode = getPersonNodeIfExists(invitation.getInviterEmail());
            NodeRef inviteeNode = getPersonNodeIfExists(invitation.getInviteeEmail());
            
            CloudInvitationImpl invitationImpl = (CloudInvitationImpl) invitation;
            
            Map<String, Serializable> inviterProps = getPropertiesWithStringKeys(inviterNode);
            invitationImpl.setInviterProperties(inviterProps);
            
            if (inviteeNode != null)
            {
                Map<String, Serializable> inviteeProps = getPropertiesWithStringKeys(inviteeNode);
                invitationImpl.setInviteeProperties(inviteeProps);
                
                // If the invitee has an avatar, we can add its URL.
                // TODO This would all be easier if we had put the invitee (and inviter) cm:person NodeRefs into the CloudInvitation
                List<AssociationRef> avatarAssocs = nodeService.getTargetAssocs(inviteeNode, ContentModel.ASSOC_AVATAR);
                if ( !avatarAssocs.isEmpty())
                {
                    NodeRef avatarNodeRef = avatarAssocs.get(0).getTargetRef();
                    invitationImpl.setInviteeAvatarNode(avatarNodeRef);
                }
            }
            
            result.add(invitation);
        }
        
        return result;
    }

    private NodeRef getPersonNodeIfExists(String username)
    {
        NodeRef result = null;
        if (personService.personExists(username))
        {
            try
            {
                result = personService.getPerson(username, false);
            }
            catch (NoSuchPersonException ignored)
            {
                // TODO This seems as if it should not be necessary and yet I see that an external,
                //      non-existent invitee gives personExists() == true.
            }
        }
        
        return result;
    }

    private Map<String, Serializable> getPropertiesWithStringKeys(NodeRef inviterNode)
    {
        Map<QName, Serializable> qnameKeyedProps = nodeService.getProperties(inviterNode);
        
        Function<QName, String> transformFunction = new Function<QName, String>()
        {
            public String apply(QName value)
            {
                return value.toPrefixString(namespaceService);
            }
        };
        
        Map<String, Serializable> stringKeyedProps = (Map<String, Serializable>) CollectionUtils.transformKeys(qnameKeyedProps, transformFunction);
        return stringKeyedProps;
    }
    
    
    /**
     * This class holds the basic business logic for accepting a {@link CloudInvitation cloud invitation}.
     * 
     * @author Neil Mc Erlean
     * @since Thor Phase 2 Sprint 1
     */
    private abstract class InvitationAcceptanceStrategy
    {
        private final String workflowId;
        private final String key;
        
        public InvitationAcceptanceStrategy(String workflowId, String key)
        {
            this.workflowId = workflowId;
            this.key = key;
        }
        
        /**
         * This method implements the basic logic of accepting a cloud invitation with some extension
         * points for alternate flows through this feature.
         * @return the (possibly modified) CloudInvitation.
         */
        public CloudInvitation acceptInvitation()
        {
            log.debug("Accepting invitation on tenant '" + TenantUtil.getCurrentDomain() + "'");
            
            // We must always validate the id & key
            validateInvitationKeyAndId(workflowId, key);
            
            // Then retrieve the available data from the workflow
            CloudInvitationImpl existingInvitation = (CloudInvitationImpl)getCloudInvitationFromWorkflow(workflowId, key);
            
            // Retrieve the invitee's email address.
            String inviteeEmailAddress = getInviteeEmailAddress(existingInvitation);
            existingInvitation.setInviteeEmail(inviteeEmailAddress);
            
            // Does the invitee user already exist in the system?
            final boolean inviteeAlreadyExists = directoryService.userExists(inviteeEmailAddress);
            
            // If there's any special behaviour tied to the invitee's existence or otherwise, do it now.
            handleUserExistence(existingInvitation, inviteeAlreadyExists);
            
            String siteShortName = existingInvitation.getSiteShortName();
            
            if (log.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("invitee= ").append(inviteeEmailAddress).append("; site= ").append(siteShortName);
                log.debug(msg.toString());
            }
            
            // Now we know we have an active user - perhaps one that has just registered or perhaps one that already existed.
            // And that user will have an email address.
            // We need to set that emailAddress into the workflow in order to move to the next task, which is site membership.
            
            WorkflowTask task = getTaskForWorkflowPath(workflowId);
            
            String inviterEmailAddress = (String) task.getProperties().get(WorkflowModelCloudInvitation.WF_PROP_INVITER_EMAIL_ADDRESS);
            
            Map<QName, Serializable> newProps = new HashMap<QName, Serializable>();
            newProps.put(WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_QNAME, WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_ACCEPT);
            newProps.put(WorkflowModelCloudInvitation.WF_PROP_INVITEE_EMAIL_ADDRESS, inviteeEmailAddress);
            workflowService.updateTask(task.getId(), newProps, null, null);
            workflowService.endTask(task.getId(), null);
            
            CloudInvitationImpl result = (CloudInvitationImpl)existingInvitation;
            result.setResponse(WorkflowModelCloudInvitation.WF_INVITATION_OUTCOME_ACCEPT);
            
            // Analytics event for the invitee's acceptance.
            final boolean inviteeIsExternalUser = ! emailAddressService.getDomain(inviteeEmailAddress).equals(emailAddressService.getDomain(inviterEmailAddress));
            Analytics.record_SiteInviteResponse(inviterEmailAddress, SiteInviteResponse.ACCEPTED, !inviteeAlreadyExists, inviteeIsExternalUser);
            
            return result;
        }
        
        /**
         * This is an extension point for any behaviour which depends on the invitee's existence or non-existence.
         * It is required that the invitee user exists after invocation of this method.
         * 
         * @param invitation           the ongoing invitation.
         * @param inviteeAlreadyExists <tt>true</tt> if the invitee user already exists, else <tt>false</tt>.
         */
        protected abstract void handleUserExistence(CloudInvitation invitation, boolean inviteeAlreadyExists);

        
        /**
         * This method retrieves the invitee's email address.
         * By default this is the value stored in the workflow which was provided by the inviter.
         */
        protected String getInviteeEmailAddress(CloudInvitation existingInvitation)
        {
            return existingInvitation.getInviteeEmail();
        }
    }
    
    /**
     * This class holds the business logic extensions for accepting a {@link CloudInvitation cloud invitation}
     * where the invitee accepts on the email address to which the invitation was sent and the accepting user
     * already exists as an activated user (which is the normal use case flow).
     * 
     * @author Neil Mc Erlean
     * @since Thor Phase 2 Sprint 1
     */
    private class SimpleInvitationAcceptanceStrategy extends InvitationAcceptanceStrategy
    {
        /**
         * @param workflowId the workflow id
         * @param key        the key to ensure the caller has the correct workflow
         */
        public SimpleInvitationAcceptanceStrategy(String workflowId, String key)
        {
            super(workflowId, key);
        }
        
        @Override
        protected void handleUserExistence(CloudInvitation invitation, boolean inviteeAlreadyExists)
        {
            if ( !inviteeAlreadyExists)
            {
                throw new InvalidInvitationAcceptanceException("Invitation acceptance failed as invitee does not exist.");
            }
            else
            {
                // Intentionally empty
            }
        }
    }
    
    /**
     * This class holds the business logic extensions for accepting a {@link CloudInvitation cloud invitation}
     * where the invitee does not exist on the system and must sign up as part of the acceptance.
     * 
     * @author Neil Mc Erlean
     * @since Thor Phase 2 Sprint 1
     */
    private class SignupInvitationAcceptanceStrategy extends InvitationAcceptanceStrategy
    {
        private String inviteeFirstName, inviteeLastName, inviteePassword;
        
        /**
         * 
         * @param workflowId       the workflow id
         * @param key              the key to ensure the caller has the correct workflow
         * @param inviteeFirstName the invitee's first name.
         * @param inviteeLastName  the invitee's last name.
         * @param inviteePassword  the invitee's password.
         */
        public SignupInvitationAcceptanceStrategy(String workflowId, String key, String inviteeFirstName, String inviteeLastName, String inviteePassword)
        {
            super(workflowId, key);
            
            ParameterCheck.mandatoryString("inviteeFirstName", inviteeFirstName);
            ParameterCheck.mandatoryString("inviteeLastName", inviteeLastName);
            ParameterCheck.mandatoryString("inviteePassword", inviteePassword);
            
            this.inviteeFirstName = inviteeFirstName;
            this.inviteeLastName  = inviteeLastName;
            this.inviteePassword  = inviteePassword;
        }
        
        @Override
        protected void handleUserExistence(CloudInvitation invitation, boolean inviteeAlreadyExists)
        {
            if ( !inviteeAlreadyExists)
            {
                // In order to ensure that we have no activations without registrations, we will send a 'registration'
                // analytic event here. All the various google & web data are null for Site_Invite-based registrations.
                //
                // But we will only do so if the user had not registered already by themselves.
                final String inviteeEmail = invitation.getInviteeEmail();
                if ( !registrationService.isRegisteredEmailAddress(inviteeEmail))
                {
                    Analytics.record_Registration(inviteeEmail, "Site_Invite", null,
                            null, null, null, null, null, null, null, null, null, null);
                }
                
                // We need to create the user. For this to work, they should have already provided
                // the necessary profile information as part of the accept webscript call.
                ParameterCheck.mandatoryString("inviteeFirstName", inviteeFirstName);
                ParameterCheck.mandatoryString("inviteeLastName", inviteeLastName);
                ParameterCheck.mandatoryString("inviteePassword", inviteePassword);
                
                registrationService.createUser(inviteeEmail, inviteeFirstName, inviteeLastName, inviteePassword);
                
                if (log.isDebugEnabled())
                {
                    log.debug(inviteeEmail + " created.");
                }
            }
            else
            {
                throw new InvalidInvitationAcceptanceException("Invitation acceptance failed as invitee already exists.");
            }
        }
    }
    
    /**
     * This class holds the business logic extensions for accepting a {@link CloudInvitation cloud invitation}
     * where the invitee accepts on a different email address from that to which the invitation was sent.
     * 
     * @author Neil Mc Erlean
     * @since Thor Phase 2 Sprint 1
     */
    private class AlternativeEmailInvitationAcceptanceStrategy extends InvitationAcceptanceStrategy
    {
        private String inviteesAlternativeEmailAddress;
        
        /**
         * 
         * @param workflowId                      the workflow id
         * @param key                             the key to ensure the caller has the correct workflow
         * @param inviteesAlternativeEmailAddress the invitee's alternative email address (required).
         */
        public AlternativeEmailInvitationAcceptanceStrategy(String workflowId, String key, String inviteesAlternativeEmailAddress)
        {
            super(workflowId, key);
            this.inviteesAlternativeEmailAddress  = inviteesAlternativeEmailAddress;
        }
        
        @Override
        protected void handleUserExistence(CloudInvitation invitation, boolean inviteeAlreadyExists)
        {
            
            if ( !inviteeAlreadyExists)
            {
                throw new InvalidInvitationAcceptanceException("Cannot allow a non-existent user to accept an invitation on an alternative email address.");
            }
            else
            {
                // Intentionally empty
            }
        }
        
        @Override
        protected String getInviteeEmailAddress(CloudInvitation existingInvitation)
        {
            return this.inviteesAlternativeEmailAddress;
        }
    }
}