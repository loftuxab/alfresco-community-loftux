/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.invitation;

import org.alfresco.service.namespace.QName;

/**
 * Workflow Model for cloud invitations.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public interface WorkflowModelCloudInvitation
{
    // namespace
    public static final String NAMESPACE_URI = "http://www.alfresco.org/model/workflow/cloud/siteinvitation/1.0";
    
    // process name
    public static final QName WF_PROCESS_SELF_SIGNUP = QName.createQName(NAMESPACE_URI, "siteInvitationCloud");
    public static final String WORKFLOW_DEFINITION_NAME = "activiti$siteInvitationCloud";

    // tasks
    public static final QName WF_INVITATION_ACCEPTANCE_PENDING_TASK = QName.createQName(NAMESPACE_URI, "invitationAcceptancePendingTask");
    
    // reminders
    public static final QName WF_PROP_TIMER_REMIND3 = QName.createQName(NAMESPACE_URI, "remindTimer3");
    public static final QName WF_PROP_TIMER_REMIND7 = QName.createQName(NAMESPACE_URI, "remindTimer7");
    public static final QName WF_PROP_TIMER_END = QName.createQName(NAMESPACE_URI, "endTimer");
    
    // workflow properties
    
    public static final QName WF_PROP_KEY = QName.createQName(NAMESPACE_URI, "key");
    public static final String WF_PROP_KEY_ACTIVITI = "siwf_key";
    
    // Inviter
    public static final QName WF_PROP_INVITER_EMAIL_ADDRESS = QName.createQName(NAMESPACE_URI, "inviterEmail");
    public static final String WF_PROP_INVITER_EMAIL_ADDRESS_ACTIVITI = "siwf_inviterEmail";
    
    public static final QName WF_PROP_INVITER_FIRST_NAME = QName.createQName(NAMESPACE_URI, "inviterFirstName");
    public static final String WF_PROP_INVITER_FIRST_NAME_ACTIVITI = "siwf_inviterFirstName";
    
    public static final QName WF_PROP_INVITER_LAST_NAME = QName.createQName(NAMESPACE_URI, "inviterLastName");
    public static final String WF_PROP_INVITER_LAST_NAME_ACTIVITI = "siwf_inviterLastName";
    
    public static final QName WF_INVITER_PERSONAL_MESSAGE = QName.createQName(NAMESPACE_URI, "inviterPersonalMessage");
    public static final String WF_INVITER_PERSONAL_MESSAGE_ACTIVITI = "siwf_inviterPersonalMessage";
    
    // Invitee
    public static final QName WF_PROP_INVITEE_EMAIL_ADDRESS = QName.createQName(NAMESPACE_URI, "inviteeEmail");
    public static final String WF_PROP_INVITEE_EMAIL_ADDRESS_ACTIVITI = "siwf_inviteeEmail";
    
    public static final QName WF_PROP_INVITEE_ROLE = QName.createQName(NAMESPACE_URI, "inviteeRole");
    public static final String WF_PROP_INVITEE_ROLE_ACTIVITI = "siwf_inviteeRole";
    
    public static final QName WF_PROP_INVITEE_FIRST_NAME = QName.createQName(NAMESPACE_URI, "inviteeFirstName");
    public static final String WF_PROP_INVITEE_FIRST_NAME_ACTIVITI = "siwf_inviteeFirstName";
    
    public static final QName WF_PROP_INVITEE_LAST_NAME = QName.createQName(NAMESPACE_URI, "inviteeLastName");
    public static final String WF_PROP_INVITEE_LAST_NAME_ACTIVITI = "siwf_inviteeLastName";
    
    public static final QName WF_PROP_INVITEE_PASSWORD = QName.createQName(NAMESPACE_URI, "inviteePassword");
    public static final String WF_PROP_INVITEE_PASSWORD_ACTIVITI = "siwf_inviteePassword";
    
    // Site
    public static final QName WF_PROP_SITE_SHORT_NAME = QName.createQName(NAMESPACE_URI, "siteShortName");
    public static final String WF_PROP_SITE_SHORT_NAME_ACTIVITI = "siwf_siteShortName";
    
    public static final QName WF_PROP_SITE_TITLE = QName.createQName(NAMESPACE_URI, "siteTitle");
    public static final String WF_PROP_SITE_TITLE_ACTIVITI = "siwf_siteTitle";
    
    public static final QName WF_PROP_SITE_TENANT_ID = QName.createQName(NAMESPACE_URI, "siteTenantId");
    public static final QName WF_PROP_SITE_TENANT_TITLE = QName.createQName(NAMESPACE_URI, "siteTenantTitle");
    
    public static final QName WF_PROP_MAILS_SENT = QName.createQName(NAMESPACE_URI, "mailsSent");
    public static final String WF_PROP_MAILS_SENT_ACTIVITI = "siwf_mailsSent";
    

    // workflow state
    public static final String WF_INVITATION_OUTCOME = "siwf_invitationOutcome";
    public static final QName WF_INVITATION_OUTCOME_QNAME = QName.createQName(NAMESPACE_URI, "invitationOutcome");
    
    public static final String WF_INVITATION_OUTCOME_ACCEPT = "accept";
    public static final String WF_INVITATION_OUTCOME_REJECT = "reject";
    public static final String WF_INVITATION_OUTCOME_CANCEL = "cancel";
    public static final String WF_INVITATION_OUTCOME_REMIND = "remind";
    
    public static final String WF_EMAIL_TEMPLATE = "siwf_emailTemplate";
    public static final String WF_EMAIL_TEMPLATE_REMINDER = "site-invitation-reminder-email.ftl";
    public static final String WF_EMAIL_TEMPLATE_INITIAL = "site-invitation-email.ftl";
}
