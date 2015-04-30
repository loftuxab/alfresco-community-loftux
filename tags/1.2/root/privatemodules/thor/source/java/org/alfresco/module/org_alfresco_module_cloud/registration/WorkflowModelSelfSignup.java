/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.registration;

import org.alfresco.service.namespace.QName;

/**
 * Workflow Model for a Cloud Account self-signup.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public interface WorkflowModelSelfSignup
{
    // namespace
    public static final String NAMESPACE_URI = "http://www.alfresco.org/model/workflow/signup/selfsignup/1.0";
    
    // process name
    public static final QName WF_PROCESS_SELF_SIGNUP = QName.createQName(NAMESPACE_URI, "self-signup");
    public static final String WORKFLOW_DEFINITION_NAME = "activiti$accountSelfSignup";

    // tasks
    public static final QName WF_REQUEST_SELF_SIGNUP_TASK = QName.createQName(NAMESPACE_URI, "requestSelfSignupTask");
    public static final String WF_ACCOUNT_ACTIVATION_PENDING_TASK_STRING = "accountActivationPendingTask";
    public static final QName WF_ACCOUNT_ACTIVATION_PENDING_TASK = QName.createQName(NAMESPACE_URI, WF_ACCOUNT_ACTIVATION_PENDING_TASK_STRING);
    
    // reminders
    public static final QName WF_PROP_TIMER_REMIND3 = QName.createQName(NAMESPACE_URI, "remindTimer3");
    public static final QName WF_PROP_TIMER_REMIND7 = QName.createQName(NAMESPACE_URI, "remindTimer7");
    public static final QName WF_PROP_TIMER_END = QName.createQName(NAMESPACE_URI, "endTimer");
    
    // workflow properties
    public static final QName WF_PROP_EMAIL_ADDRESS = QName.createQName(NAMESPACE_URI, "emailAddress");
    public static final String WF_PROP_EMAIL_ADDRESS_ACTIVITI = "suwf_emailAddress";
    
    public static final QName WF_PROP_MESSAGE = QName.createQName(NAMESPACE_URI, "message");
    public static final String WF_PROP_MESSAGE_ACTIVITI = "suwf_message";

    public static final QName WF_PROP_KEY = QName.createQName(NAMESPACE_URI, "key");
    public static final String WF_PROP_KEY_ACTIVITI = "suwf_key";
    
    public static final QName WF_PROP_FIRST_NAME = QName.createQName(NAMESPACE_URI, "firstName");
    public static final String WF_PROP_FIRST_NAME_ACTIVITI = "suwf_firstName";
    
    public static final QName WF_PROP_LAST_NAME = QName.createQName(NAMESPACE_URI, "lastName");
    public static final String WF_PROP_LAST_NAME_ACTIVITI = "suwf_lastName";
    
    public static final QName WF_PROP_IS_PREREGISTERED = QName.createQName(NAMESPACE_URI, "isPreRegistered");
    public static final String WF_PROP_IS_PREREGISTERED_ACTIVITI = "suwf_isPreRegistered";
    
    public static final QName WF_PROP_PASSWORD = QName.createQName(NAMESPACE_URI, "password");
    public static final String WF_PROP_PASSWORD_ACTIVITI = "suwf_password";
    
    public static final QName WF_PROP_MAILS_SENT = QName.createQName(NAMESPACE_URI, "mailsSent");
    public static final String WF_PROP_MAILS_SENT_ACTIVITI = "suwf_mailsSent";

    public static final QName WF_PROP_INITIATOR_EMAIL_ADDRESS = QName.createQName(NAMESPACE_URI, "initiatorEmailAddress");
    public static final String WF_PROP_INITIATOR_EMAIL_ADDRESS_ACTIVITI = "suwf_initiatorEmailAddress";

    public static final QName WF_PROP_INITIATOR_FIRST_NAME = QName.createQName(NAMESPACE_URI, "initiatorFirstName");
    public static final String WF_PROP_INITIATOR_FIRST_NAME_ACTIVITI = "suwf_initiatorFirstName";

    public static final QName WF_PROP_INITIATOR_LAST_NAME = QName.createQName(NAMESPACE_URI, "initiatorLastName");
    public static final String WF_PROP_INITIATOR_LAST_NAME_ACTIVITI = "suwf_initiatorLastName";

    // workflow state
    public static final String WF_REGISTRATION_STATUS = "suwf_emailStatus";
    public static final String WF_REGISTRATION_STATUS_NONE = "None";
    public static final String WF_REGISTRATION_STATUS_ALREADY_ACTIVATED = "AlreadyActivated";
    public static final String WF_REGISTRATION_STATUS_BLOCKED = "Blocked";
    
    public static final QName WF_PROP_ACTIVATION_OUTCOME = QName.createQName(NAMESPACE_URI, "activationOutcome");
    public static final String WF_PROP_ACTIVATION_OUTCOME_PROCEED = "Proceed";
    public static final String WF_PROP_ACTIVATION_OUTCOME_RESEND_ACTIVATION_EMAIL = "ResendActivationEmail";
    //public static final String WF_PROP_ACTIVATION_OUTCOME_INVALID_PASSWORD = "InvalidPassword";
    
    public static final String WF_EMAIL_TEMPLATE = "suwf_emailTemplate";
    public static final String WF_EMAIL_TEMPLATE_ACTIVATE = "self-signup-requested-email.ftl";
    public static final String WF_EMAIL_TEMPLATE_ACTIVATE_WITH_INITIATOR = "self-signup-requested-with-initiator-email.ftl";
    public static final String WF_EMAIL_TEMPLATE_ALREADY_ACTIVATED = "self-signup-already-registered-email.ftl";
    public static final String WF_EMAIL_TEMPLATE_REMINDER = "self-signup-reminder-email.ftl";
    public static final String WF_EMAIL_TEMPLATE_REMINDER_WITH_INITIATOR = "self-signup-reminder-with-initiator-email.ftl";

    // CLOUD-1159 - special case (new user login direct to profile page)
    public static final QName WF_PROP_SAML_DIRECT_SIGNUP = QName.createQName(NAMESPACE_URI, "samlDirectSignup");
    public static final String WF_PROP_SAML_DIRECT_SIGNUP_ACTIVITI = "suwf_samlDirectSignup";
}
