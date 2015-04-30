/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.repo.workflow.WorkflowNotificationUtils;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * A simple helper class which is responsible for sending emails and which supports
 * some configuration for test. Also hides some wrinkles that exist within the {@link MailActionExecuter}
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class EmailHelper implements InitializingBean
{
    private static final String REPLACE_UNALLOWED_CHARACTER_ALTERNATIVE_EMAIL = "[<>\\\"]";
    
    // Note that because the email recipients are very often not Alfresco users (i.e. not authorities), we cannot
    // use the NotificationService. Instead we must rely on the normal MailActionExecuter.
    
    private static final Log log = LogFactory.getLog(EmailHelper.class);
    
    private ActionService actionService;
    
    /**
     * This object is used as a storage list for emails in test mode.
     */
    private EmailTestStorage emailTestStorage;
    
    public void setActionService(ActionService actionService)
    {
        this.actionService = actionService;
    }
    
    public void setEmailTestStorage(EmailTestStorage emailTestStorage)
    {
        this.emailTestStorage = emailTestStorage;
    }
    
    @Override public void afterPropertiesSet()
    {
        ParameterCheck.mandatory("actionService", actionService);
        
        // 'override' workflow template location
        WorkflowNotificationUtils.WF_ASSIGNED_TEMPLATE = "alfresco/module/org_alfresco_module_cloud/email_templates/workflow/wf-email.ftl";
    }
    
    public void sendEmail(EmailRequest emailRequest)
    {
        emailRequest.validate();
        
        // MailActionExecuter does not support sending emails to multiple recipients unless all
        // those recipients are Alfresco authorities. i.e. users or groups.
        for (String emailRecipient : emailRequest.getToEmails())
        {
            // If the test storage bean is not set, then we will send 'real' emails (or try to).
            if (this.emailTestStorage == null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Email sent: " + emailRequest);
                }
                
                // In production, this object reference should be null.
                Action mail = actionService.createAction(MailActionExecuter.NAME);
                
                mail.setParameterValue(MailActionExecuter.PARAM_FROM, emailRequest.getFromEmail());
                if(emailRequest.getFromPersonalName() != null)
                {
                    mail.setParameterValue(MailActionExecuter.PARAM_FROM_PERSONAL_NAME, emailRequest.getFromPersonalName());
                }
                
                if (emailRequest.getLocae() != null)
                {
                    mail.setParameterValue(MailActionExecuter.PARAM_LOCALE, emailRequest.getLocae());
                }
                
                // This is the normal flow through this method.
                mail.setParameterValue(MailActionExecuter.PARAM_TO, emailRecipient);
                mail.setParameterValue(MailActionExecuter.PARAM_SUBJECT, emailRequest.getSubject());
                if (emailRequest.getSubjectParams() != null && emailRequest.getSubjectParams().length > 0)
                {
                	mail.setParameterValue(MailActionExecuter.PARAM_SUBJECT_PARAMS, emailRequest.getSubjectParams());
                }
                mail.setParameterValue(MailActionExecuter.PARAM_TEMPLATE, emailRequest.getTemplateRef());
                mail.setParameterValue(MailActionExecuter.PARAM_TEMPLATE_MODEL, (Serializable) emailRequest.getTemplateModel());
                mail.setParameterValue(MailActionExecuter.PARAM_IGNORE_SEND_FAILURE, emailRequest.getIgnoreSendFailure());
                
                actionService.executeAction(mail, null, true, emailRequest.getSendAsynchronously());
            }
            else
            {
                // If it is set, then 'real' emails will not be set and instead all EmailRequest objects
                // will be stored in the specified object. This should only happen in test code
                if (log.isDebugEnabled())
                {
                    log.debug("Email routed to test storage: " + emailRequest);
                }
                this.emailTestStorage.add(emailRequest);
            }
        }
    }
    
    
    /**
     * Represents a request to send an email.
     */
    public static class EmailRequest
    {
        /**
         * The email address of the sender.
         */
        private String fromEmail;
        
        /**
         * Optional personal name of the sender.
         */
        private String fromPersonalName;
        
        /**
         * Optional Locale for subject and body text
         */
        private Locale locale;
        
        /**
         * The email addresses (1 or many) of the recipients. These will all be "to:" recipients. There is currently
         * no support for "cc:" or "bcc:".
         */
        private List<String> toEmails;
        /**
         * The text or message key of the email subject line.
         */
        private String subjectLine = "";
        /**
         * Parameters to use when creating the subject from a message key
         */
        private Object[] subjectParams;
        /**
         * Classpath or NodeRef string whose content is the email template (the view in MVC).
         */
        private String templateRef;
        /**
         * The model for the email template (the model in MVC).
         */
        private Map<String, Serializable> templateModel;
        private boolean ignoreSendFailure = false;
        private boolean sendAsynchronously = true;
        
        public void validate()
        {
            ParameterCheck.mandatoryString("fromEmail", fromEmail);
            ParameterCheck.mandatoryCollection("toEmails", toEmails);
            // subjectLine could be blank.
            ParameterCheck.mandatory("template", templateRef);
            ParameterCheck.mandatory("templateModel", templateModel);
        }
        
        public String getFromEmail() { return this.fromEmail; }
        
        public String getFromPersonalName() { return this.fromPersonalName; }

        public Locale getLocae() { return this.locale; }
        
        public List<String> getToEmails() { return Collections.unmodifiableList(this.toEmails); }
        
        public String getSubject() { return this.subjectLine == null ? "" : this.subjectLine; }
        
        public Object[] getSubjectParams()
        {
            return this.subjectParams == null ? null : this.subjectParams.clone();
        }
        
        public String getTemplateRef() { return this.templateRef; }
        
        public Map<String, Serializable> getTemplateModel() { return Collections.unmodifiableMap(this.templateModel); }
        
        public Serializable getIgnoreSendFailure() { return this.ignoreSendFailure; }
        
        public boolean getSendAsynchronously() { return this.sendAsynchronously; }
        
        
        public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }
        
        public void setFromPersonalName(String fromPersonalName) { this.fromPersonalName = fromPersonalName; }

        public void setLocale(Locale locale) { this.locale = locale; }

        public void setToEmails(List<String> toEmails) { this.toEmails = toEmails; }
        
        public void setSubjectLine(String subjectLine) { this.subjectLine = subjectLine; }

        public void setSubjectParams(Object[] params)
        {
            if (params == null)
            {
                this.subjectParams = new Object[0];
            }
            else
            {
                this.subjectParams = params.clone();
            }
        }
        
        public void setTemplate(String templateRef) { this.templateRef = templateRef; }
        
        public void setTemplateModel(Map<String, Serializable> templateModel) { this.templateModel = templateModel; }
        
        public void setIgnoreSendFailure(boolean ignoreSendFailure) { this.ignoreSendFailure = ignoreSendFailure; }
        
        public void setSendAsynchronously(boolean sendAsynchronously) { this.sendAsynchronously = sendAsynchronously; }
        
        @Override public String toString()
        {
            StringBuilder msg = new StringBuilder();
            msg.append(EmailRequest.class.getSimpleName()).append(' ').append(toEmails).append(" subject: ").append(subjectLine);
            return msg.toString();
        }
    }
    
    public static class EmailTestStorage
    {
        private final List<EmailRequest> emailRequests = new ArrayList<EmailRequest>();
        
        public void reset() { this.emailRequests.clear(); }
        
        public void add(EmailRequest emailRequest)
        {
            this.emailRequests.add(emailRequest);
        }
        
        public int getEmailCount() { return this.emailRequests.size(); }
        
        public EmailRequest getEmailRequest(int i) { return this.emailRequests.get(i); }

        public void clear()
        {
            this.emailRequests.clear();
        }
    }
}
