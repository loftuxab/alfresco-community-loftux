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
package org.alfresco.module.org_alfresco_module_cloud.registration;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigAdminService;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.util.EmailHelper;
import org.alfresco.module.org_alfresco_module_cloud.util.EmailHelper.EmailRequest;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.web.scripts.TenantWebScriptServlet;
import org.alfresco.repo.workflow.BPMEngineRegistry;
import org.alfresco.repo.workflow.activiti.ActivitiConstants;
import org.alfresco.service.cmr.workflow.WorkflowException;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * This {@link JavaDelegate} implementation is responsible for sending the appropriate email
 * after a potential user has requested a Cloud account. Depending on process-state, sends
 * a reminder instead of initial invite mail when called (based on {@link WorkflowModelSelfSignup#WF_PROP_MAILS_SENT}).
 * 
 * @author Neil Mc Erlean
 * @author Frederik Heremans
 * @author Erik Winlof
 * @since Alfresco Cloud Module (Thor)
 */
public class SendEmailDelegate extends AbstractSignupDelegate
{
    private static final String REPO_PREFIX = "cm:";
    
    private static final String LOGIN_URL = "login_url";
    private static final String NETWORK_NAME = "network_name";
    private static final String RESET_PASSWORD_URL = "reset_password_url";
    private static final String CANCEL_REGISTRATION_URL = "cancel_registration_url";
    private static final String USER_MESSAGE = "userMessage";
    private static final String INITIATOR_FIRST_NAME = "initiator_first_name";
    private static final String INITIATOR_LAST_NAME = "initiator_last_name";
    private static final String SAML_ENABLED = "saml_enabled";
    
    public static final String ACTIVATE_ACCOUNT_URL = "activate_account_url";
    
    
    private String emailSender;
    private EmailHelper emailHelper;
    private EmailAddressService emailAddressService;
    private SAMLConfigAdminService samlConfigAdminService;
    
    public void setEmailSender(String emailSender)
    {
        this.emailSender = emailSender;
    }
    
    public void setEmailHelper(EmailHelper emailHelper)
    {
        this.emailHelper = emailHelper;
    }
    
    public void setEmailAddressService(EmailAddressService emailAddressService)
    {
        this.emailAddressService = emailAddressService;
    }
    
    public void setSamlConfigAdminService(SAMLConfigAdminService service)
    {
        this.samlConfigAdminService = service;
    }
    
    private final static String templatePath = "alfresco/module/org_alfresco_module_cloud/email_templates/signup/";
    
    public void execute(DelegateExecution execution) throws Exception
    {
        String emailRequestingSignUp = (String) execution.getVariable(WorkflowModelSelfSignup.WF_PROP_EMAIL_ADDRESS_ACTIVITI);
        if (emailRequestingSignUp == null)
        {
            throw new WorkflowException("Illegal null email variable.");
        }
        
        Boolean isSamlDirectSignup = (Boolean) execution.getVariable(WorkflowModelSelfSignup.WF_PROP_SAML_DIRECT_SIGNUP_ACTIVITI);
        if ((isSamlDirectSignup != null) && (isSamlDirectSignup == true))
        {
            // CLOUD-1159 - special case (new user login direct to profile page) - no email needs to be sent !
            return;
        }
        
        // Check if an initial signup-mail should be sent, or a reminder-message
        Integer numberOfMailsSent = (Integer) execution.getVariable(WorkflowModelSelfSignup.WF_PROP_MAILS_SENT_ACTIVITI);
        if(numberOfMailsSent == null)
        {
            numberOfMailsSent = 0;
        }
        
        String message = (String) execution.getVariable(WorkflowModelSelfSignup.WF_PROP_MESSAGE_ACTIVITI);
        String registrationStatus = (String) execution.getVariable(WorkflowModelSelfSignup.WF_REGISTRATION_STATUS);
        String initiatorEmail = (String) execution.getVariable(WorkflowModelSelfSignup.WF_PROP_INITIATOR_EMAIL_ADDRESS_ACTIVITI);

        String template = null;
        if(numberOfMailsSent > 0)
        {
            // Use alternative template, not handling the initial mail
            if (initiatorEmail != null)
            {
                template = WorkflowModelSelfSignup.WF_EMAIL_TEMPLATE_REMINDER_WITH_INITIATOR;
            }
            else
            {
                template = WorkflowModelSelfSignup.WF_EMAIL_TEMPLATE_REMINDER;
            }
        }
        else
        {
            template = (String) execution.getVariable(WorkflowModelSelfSignup.WF_EMAIL_TEMPLATE);
        }

        // Prior to moving templates to the classpath, templates were stored in the repo. It is therefore possible
        // for an old workflow to still reference a repo template - in that case, redirect them to the classpath.
        if (template != null && template.startsWith(REPO_PREFIX))
        {
            template = template.substring(REPO_PREFIX.length());
        }
        
        Map<String, Serializable> emailTemplateModel = new HashMap<String, Serializable>();
        String subjectLine = null;
        Object[] subjectParams = null;
        String fromPersonalName = null;

        if (WorkflowModelSelfSignup.WF_REGISTRATION_STATUS_NONE.equals(registrationStatus))
        {
            String key = (String) execution.getVariable(WorkflowModelSelfSignup.WF_PROP_KEY_ACTIVITI);
            final String activateAccountUrl = createAccountUrl(emailRequestingSignUp, key, execution.getProcessInstanceId(), false);
            emailTemplateModel.put(ACTIVATE_ACCOUNT_URL, activateAccountUrl);
            emailTemplateModel.put(USER_MESSAGE, message);

            /**
             * Make sure the cancel link:
             * - always appear in "initiated signup emails"
             * - only appear after the 1st reminder in "self signup emails"
             */
            if(initiatorEmail != null || numberOfMailsSent > 1)
            {
                emailTemplateModel.put(CANCEL_REGISTRATION_URL, createAccountUrl(emailRequestingSignUp, key, execution.getProcessInstanceId(), true));
            }

            if (initiatorEmail != null)
            {
                String initiatorFirstName = (String) execution.getVariable(WorkflowModelSelfSignup.WF_PROP_INITIATOR_FIRST_NAME_ACTIVITI);
                String initiatorLastName = (String) execution.getVariable(WorkflowModelSelfSignup.WF_PROP_INITIATOR_LAST_NAME_ACTIVITI);
                emailTemplateModel.put(INITIATOR_FIRST_NAME, initiatorFirstName);
                emailTemplateModel.put(INITIATOR_LAST_NAME, initiatorLastName);
                subjectLine = REPO_PREFIX + template;
                subjectParams = new Object[] {initiatorFirstName, initiatorLastName};
                fromPersonalName = (initiatorFirstName != null ? initiatorFirstName + " " : "") + (initiatorLastName != null ? initiatorLastName : "");
            }
            else
            {
                subjectLine = REPO_PREFIX + template;
            }
        }
        else if (WorkflowModelSelfSignup.WF_REGISTRATION_STATUS_ALREADY_ACTIVATED.equals(registrationStatus))
        {
            String emailDomain = emailAddressService.getDomain(emailRequestingSignUp);
            
            final String loginUrl = createLoginUrl(emailDomain);
            emailTemplateModel.put(LOGIN_URL, loginUrl);
            
            emailTemplateModel.put(NETWORK_NAME, emailDomain);
            
            final String passwordUrl = createResetPasswordUrl();
            emailTemplateModel.put(RESET_PASSWORD_URL, passwordUrl);

            subjectLine = REPO_PREFIX + template;
        }
        else
        {
            throw new AlfrescoRuntimeException("Unrecognised registration status: " + registrationStatus);
        }

        // Add Flag to template model to allow for SAML specific email contents
        String emailDomain = emailAddressService.getDomain(emailRequestingSignUp);

        // Is this tenant under IdP-control (ie. SAML-enabled for Share login)
        boolean samlEnabled = samlConfigAdminService.isEnabled(emailDomain);
        if (samlEnabled)
        {
            emailTemplateModel.put(SAML_ENABLED, samlEnabled);
        }
        
        // Update sent mail count
        numberOfMailsSent++;
        execution.setVariable(WorkflowModelSelfSignup.WF_PROP_MAILS_SENT_ACTIVITI, numberOfMailsSent);
        
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setFromEmail(emailSender);
        if (fromPersonalName != null)
        {
            emailRequest.setFromPersonalName(fromPersonalName);
        }
        emailRequest.setToEmails(Arrays.asList(new String[]{emailRequestingSignUp}));
        emailRequest.setSubjectLine(subjectLine);
        emailRequest.setSubjectParams(subjectParams);
        emailRequest.setTemplate(getTemplateRef(template));
        emailRequest.setTemplateModel(emailTemplateModel);
        emailRequest.setIgnoreSendFailure(true); // TODO
        
        this.emailHelper.sendEmail(emailRequest);
    }
    
    /**
     * This method creates a URL for the 'activate account' or 'cancel registration' link which appears in the 
     * 'self-signup-requested-email.ftl' and 'self-signup-requested-email-reminder.ftl'.
     */
    private String createAccountUrl(final String email, final String guid, final String id, boolean cancel)
    {
        // The invitation workflow is running in the current tenant. In order to ensure that the invitee accepts or rejects
        // in the same tenant as the workflow instance, we need to add that data into the URL.
        String currentTenantId = TenantUtil.getCurrentDomain();
        
        String emailDomain = emailAddressService.getDomain(email);
        
        // Is this tenant under IdP-control (ie. SAML-enabled for Share login)
        boolean samlEnabled = samlConfigAdminService.isEnabled(emailDomain);
        
        // But there is a quirk: if the current tenant is the default tenant, then the id in the URL should be "-system-".
        if (TenantUtil.isCurrentDomainDefault())
        {
            // Apparently the Thor project depends on RemoteAPI and so can see this constant. TODO Perhaps it shouldn't.
            currentTenantId = TenantUtil.SYSTEM_TENANT;
        }
        
        StringBuilder msg = new StringBuilder();
        msg.append(sysAdminParams.getShareProtocol()).append("://")
           .append(sysAdminParams.getShareHost()).append(":").append(sysAdminParams.getSharePort()).append("/")
           .append(sysAdminParams.getShareContext()).append("/");
        
        if (samlEnabled && !cancel)
        {
            msg.append(emailDomain)
               .append("?page=activation")
               .append("%3Fkey%3D").append(guid)
               .append("%26").append("id%3D").append(BPMEngineRegistry.createGlobalId(ActivitiConstants.ENGINE_ID, id));
        }
        else
        {
            msg.append(currentTenantId)
               .append("/page/activation?")
               .append("key=").append(guid)
               .append("&").append("id=").append(BPMEngineRegistry.createGlobalId(ActivitiConstants.ENGINE_ID, id));
        }
        
        if(cancel)
        {
            msg.append("&cancel=true");
        }
        return msg.toString();
    }
    
    /**
     * This method creates a URL for the 'log in' link which appears in the self-signup-already-registered-email.ftl
     */
    private String createLoginUrl(String emailDomain)
    {
        // Is this tenant under IdP-control (ie. SAML-enabled for Share login)
        boolean samlEnabled = samlConfigAdminService.isEnabled(emailDomain);
        
        StringBuilder msg = new StringBuilder();
        msg.append(sysAdminParams.getShareProtocol()).append("://")
           .append(sysAdminParams.getShareHost()).append(":").append(sysAdminParams.getSharePort()).append("/")
           .append(sysAdminParams.getShareContext());
        
        if (samlEnabled)
        {
            msg.append("/")
               .append(emailDomain);
        }
        
        return msg.toString();
    }
    
    /**
     * This method creates a URL for the 'reset password' link which appears in the self-signup-already-registered-email.ftl
     */
    private String createResetPasswordUrl()
    {
        StringBuilder msg = new StringBuilder();
        msg.append(sysAdminParams.getShareProtocol()).append("://")
           .append(sysAdminParams.getShareHost()).append(":").append(sysAdminParams.getSharePort()).append("/")
           .append(sysAdminParams.getShareContext()).append("/")
           .append("page/forgot-password");
        
        return msg.toString();
    }
    
    protected String getTemplateRef(String emailTemplate)
    {
        return templatePath + emailTemplate;
    }
}
