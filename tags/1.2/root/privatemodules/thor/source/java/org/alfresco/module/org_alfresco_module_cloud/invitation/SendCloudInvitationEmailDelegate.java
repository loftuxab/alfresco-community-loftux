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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigAdminService;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.util.EmailHelper;
import org.alfresco.module.org_alfresco_module_cloud.util.EmailHelper.EmailRequest;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.web.scripts.TenantWebScriptServlet;
import org.alfresco.repo.workflow.BPMEngineRegistry;
import org.alfresco.repo.workflow.activiti.ActivitiConstants;
import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.workflow.WorkflowException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.util.StringUtils;

/**
 * This {@link JavaDelegate} implementation is responsible for sending site-invitation emails.
 * 
 * @author Neil Mc Erlean
 * @author Frederik Heremans
 * @since Alfresco Cloud Module (Thor)
 */
public class SendCloudInvitationEmailDelegate extends AbstractCloudInvitationDelegate
{
    private static final Log log = LogFactory.getLog(SendCloudInvitationEmailDelegate.class);
    
    private final static String templatePath = "alfresco/module/org_alfresco_module_cloud/email_templates/invitation/";
    
    private static final String FTL_INVITER_MESSAGE = "inviter_message";
    private static final String FTL_ACCEPT_INVITATION_URL = "accept_invitation_url";
    private static final String FTL_REJECT_INVITATION_URL = "reject_invitation_url";
    private static final String FTL_SITE_SHORT_NAME = "site_short_name";
    private static final String FTL_SITE_TITLE = "site_title";
    private static final String FTL_INVITER_EMAIL = "inviter_email";
    private static final String FTL_INVITER_FIRST_NAME = "inviter_first_name";
    private static final String FTL_INVITER_LAST_NAME = "inviter_last_name";
    private static final String FTL_INVITEE_ROLE = "invitee_role";
    
    private EmailHelper emailHelper;
    private String emailSender;
    private PreferenceService preferenceService;
    private EmailAddressService emailAddressService;
    private SAMLConfigAdminService samlConfigAdminService;
    
    public void setEmailHelper(EmailHelper emailHelper)
    {
        this.emailHelper = emailHelper;
    }
    
    public void setPreferenceService(PreferenceService preferenceService)
    {
        this.preferenceService = preferenceService;
    }
    
    public void setEmailAddressService(EmailAddressService emailAddressService)
    {
        this.emailAddressService = emailAddressService;
    }
    
    public void setSamlConfigAdminService(SAMLConfigAdminService service)
    {
        this.samlConfigAdminService = service;
    }
    
    @Override
    public void execute(final DelegateExecution execution) throws Exception
    {
        String inviteeEmail = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_INVITEE_EMAIL_ADDRESS_ACTIVITI);
        if (inviteeEmail == null)
        {
            throw new WorkflowException("Illegal null email variable.");
        }
        String key = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_KEY_ACTIVITI);
        
        if (log.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Sending site invitation email to ").append(inviteeEmail)
               .append(" with id,key=").append(execution.getProcessInstanceId()).append(",").append(key);
            log.debug(msg.toString());
        }
        
        // Extract number of mails sent
        Integer numberOfMailsSent = (Integer) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_MAILS_SENT_ACTIVITI);
        if(numberOfMailsSent == null)
        {
            numberOfMailsSent = 0;
        }
        
        String inviterEmail = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_INVITER_EMAIL_ADDRESS_ACTIVITI);
        String inviterFirstName = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_INVITER_FIRST_NAME_ACTIVITI);
        String inviterLastName = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_INVITER_LAST_NAME_ACTIVITI);
        String siteShortName = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_SITE_SHORT_NAME_ACTIVITI);
        String siteTitle = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_SITE_TITLE_ACTIVITI);
        String inviterMessage = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_INVITER_PERSONAL_MESSAGE_ACTIVITI);
        String inviteeRole = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_INVITEE_ROLE_ACTIVITI);
        
        final EmailRequest emailRequest = new EmailRequest();
        
        // Inviter-email address shouldn't be used to send this mail. Instead, use a fixed address and use the inviter's first and lastname
        // as personal name (as defined in the SMTP RFC's: "Bob Example" <bob@example.org>) - CLOUD-38
        emailRequest.setFromEmail(emailSender);
        emailRequest.setLocale(getInviterLocale(inviterEmail));
        
        if(inviterFirstName != null || inviterLastName != null)
        {
            emailRequest.setFromPersonalName(
                        (inviterFirstName != null ? inviterFirstName + " " : "") +
                        (inviterLastName != null ? inviterLastName : ""));
        }
        
        emailRequest.setToEmails(Arrays.asList(new String[]{inviteeEmail}));
        if (siteTitle == null || siteTitle.trim().length() == 0)
        {
            siteTitle = I18NUtil.getMessage("cloud-invitation.email.empty-site-title-replacement");
        }
        if(numberOfMailsSent == 0)
        {
            // Initial invitation message
            emailRequest.setSubjectLine("cloud-invitation.email.subjectline");
            emailRequest.setSubjectParams(new Object[] {siteTitle, inviterFirstName, inviterLastName});
            emailRequest.setTemplate(getTemplateRef(WorkflowModelCloudInvitation.WF_EMAIL_TEMPLATE_INITIAL));
        }
        else
        {
            // Reminder message
            emailRequest.setSubjectLine("cloud-invitation.reminder.email.subjectline");
            emailRequest.setSubjectParams(new Object[] {siteTitle, inviterFirstName, inviterLastName});
            emailRequest.setTemplate(getTemplateRef(WorkflowModelCloudInvitation.WF_EMAIL_TEMPLATE_REMINDER));
        }
        
        numberOfMailsSent++;
        execution.setVariable(WorkflowModelCloudInvitation.WF_PROP_MAILS_SENT_ACTIVITI, numberOfMailsSent);
        
        Map<String, Serializable> templateModel = new HashMap<String, Serializable>();
        templateModel.put(FTL_INVITER_EMAIL, inviterEmail);
        templateModel.put(FTL_INVITER_FIRST_NAME, inviterFirstName);
        templateModel.put(FTL_INVITER_LAST_NAME, inviterLastName);
        templateModel.put(FTL_INVITER_MESSAGE, inviterMessage);
        templateModel.put(FTL_ACCEPT_INVITATION_URL, createAcceptInvitationUrl(key, execution.getProcessInstanceId(), inviteeEmail));
        templateModel.put(FTL_REJECT_INVITATION_URL, createRejectInvitationUrl(key, execution.getProcessInstanceId(), inviteeEmail));
        templateModel.put(FTL_SITE_SHORT_NAME, siteShortName);
        templateModel.put(FTL_SITE_TITLE, siteTitle);
        templateModel.put(FTL_INVITEE_ROLE, inviteeRole);
        
        emailRequest.setTemplateModel(templateModel);
        emailRequest.setIgnoreSendFailure(true);
        emailRequest.setSendAsynchronously(true);
        
        emailHelper.sendEmail(emailRequest);
    }
    
    public void setEmailSender(String emailSender)
    {
        this.emailSender = emailSender;
    }
    
    private String createInvitationUrl(final String guid, final String processInstanceId, boolean reject, String inviteeEmail)
    {
        // The invitation workflow is running in the current tenant. In order to ensure that the invitee accepts or rejects
        // in the same tenant as the workflow instance, we need to add that data into the URL.
        String currentTenantId = TenantUtil.getCurrentDomain();
        
        String emailDomain = emailAddressService.getDomain(inviteeEmail);
        
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
        
        if (samlEnabled && !reject)
        {
            msg.append(emailDomain)
               .append("?page=invitation")
               .append("%3Fkey%3D").append(guid)
               .append("%26").append("id%3D").append(BPMEngineRegistry.createGlobalId(ActivitiConstants.ENGINE_ID, processInstanceId));
        }
        else
        {
            msg.append(currentTenantId)
               .append("/page/invitation?")
               .append("key=").append(guid)
               .append("&").append("id=").append(BPMEngineRegistry.createGlobalId(ActivitiConstants.ENGINE_ID, processInstanceId));
        }
        
        if (reject)
        {
            msg.append("&").append("reject=true");
        }
        return msg.toString();
    }

    private String createAcceptInvitationUrl(final String guid, final String processInstanceId, String inviteeEmail)
    {
        return this.createInvitationUrl(guid, processInstanceId, false, inviteeEmail);
    }
    
    private String createRejectInvitationUrl(final String guid, final String processInstanceId, String inviteeEmail)
    {
        return this.createInvitationUrl(guid, processInstanceId, true, inviteeEmail);
    }
    
    protected String getTemplateRef(final String emailTemplate)
    {
        return templatePath + emailTemplate;
    }
    
    
    
    private Locale getInviterLocale(String inviter)
    {
        Locale locale = null;
        String localeString = (String)preferenceService.getPreference(inviter, "locale");
        if (localeString != null)
        {
            locale = StringUtils.parseLocaleString(localeString);
        }
        return locale;

    }
}
