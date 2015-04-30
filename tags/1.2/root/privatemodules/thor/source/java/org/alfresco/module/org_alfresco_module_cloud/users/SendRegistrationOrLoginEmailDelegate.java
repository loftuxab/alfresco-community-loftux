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
package org.alfresco.module.org_alfresco_module_cloud.users;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.module.org_alfresco_module_cloud.util.EmailHelper;
import org.alfresco.module.org_alfresco_module_cloud.util.EmailHelper.EmailRequest;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class SendRegistrationOrLoginEmailDelegate extends AbstractResetPasswordDelegate
{
    private static final Log log = LogFactory.getLog(SendRegistrationOrLoginEmailDelegate.class);
    
    private final static String template = "reset-password-no-such-user-email.ftl";
    private final static String templatePath = "alfresco/module/org_alfresco_module_cloud/email_templates/reset_password/";
    
    private final static String LOGIN_URL = "login_url";
    private final static String SIGNUP_URL = "initiate_signup_url";
    
    private String emailSender;
    private EmailHelper emailHelper;
    private String signupUrl = "http://cloud.alfresco.com";
    
    public void setEmailSender(String emailSender)
    {
        this.emailSender = emailSender;
    }
    
    public void setEmailHelper(EmailHelper emailHelper)
    {
        this.emailHelper = emailHelper;
    }
    
    public void setSignupUrl(String signupUrl)
    {
        this.signupUrl = signupUrl;
    }
    
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        // Note that in the cloud, the username == email address
        final String userName = (String) execution.getVariable(WF_PROP_USERNAME_ACTIVITI);
        ParameterCheck.mandatoryString(WF_PROP_USERNAME_ACTIVITI, userName);
        
        Map<String, Serializable> emailTemplateModel = new HashMap<String, Serializable>();
        
        emailTemplateModel.put(SIGNUP_URL, signupUrl);
        emailTemplateModel.put(LOGIN_URL, createLoginUrl());
        emailTemplateModel.put("username", userName);
        
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setFromEmail(emailSender);
        emailRequest.setToEmails(Arrays.asList(new String[]{userName}));
        emailRequest.setSubjectLine(template);
        emailRequest.setTemplate(getTemplateRef(template));
        emailRequest.setTemplateModel(emailTemplateModel);
        emailRequest.setIgnoreSendFailure(true); // TODO
        
        this.emailHelper.sendEmail(emailRequest);
        
        if (log.isDebugEnabled())
        {
            log.debug("Have initiated signup request for username " + userName);
        }
    }
    
    /**
     * This method creates a URL for the 'log in' link which appears in the email
     */
    private String createLoginUrl()
    {
        StringBuilder msg = new StringBuilder();
        msg.append(sysAdminParams.getShareProtocol()).append("://")
           .append(sysAdminParams.getShareHost()).append(":").append(sysAdminParams.getSharePort()).append("/")
           .append(sysAdminParams.getShareContext());
        
        return msg.toString();
    }
    
    protected String getTemplateRef(String emailTemplate)
    {
        return templatePath + emailTemplate;
    }
}
