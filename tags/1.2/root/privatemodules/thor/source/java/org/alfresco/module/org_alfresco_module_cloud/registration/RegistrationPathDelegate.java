/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This {@link JavaDelegate activiti delegate} is reponsible for the initial email address checking.
 * On initial signup request, is the email blocked? already registered? not registered?
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public class RegistrationPathDelegate extends AbstractSignupDelegate
{
    private static final Log log = LogFactory.getLog(RegistrationPathDelegate.class);
    
    private EmailAddressService emailAddressService;
    
    public void setEmailAddressService(EmailAddressService emailAddressService)
    {
        this.emailAddressService = emailAddressService;
    }
    
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        final String email = (String) execution.getVariable(WF_PROP_EMAIL_ADDRESS_ACTIVITI);
        ParameterCheck.mandatoryString(WF_PROP_EMAIL_ADDRESS_ACTIVITI, email);
        
//        String domain = emailAddressService.getDomain(email);
//        boolean emailIsValid = emailAddressService.isAcceptedDomain(domain).isValid();
//        
//        if ( !emailIsValid)
//        {
//            execution.setVariable(WF_REGISTRATION_STATUS, WF_REGISTRATION_STATUS_BLOCKED);
//            // No email template required currently for blocked signups
//        }
//        else
//        {
            if (registrationService.isActivatedEmailAddress(email))
            {
                execution.setVariable(WF_REGISTRATION_STATUS, WF_REGISTRATION_STATUS_ALREADY_ACTIVATED);
                execution.setVariable(WF_EMAIL_TEMPLATE, WF_EMAIL_TEMPLATE_ALREADY_ACTIVATED);
            }
            else
            {
                execution.setVariable(WF_REGISTRATION_STATUS, WF_REGISTRATION_STATUS_NONE);
                if (execution.getVariable(WF_PROP_INITIATOR_EMAIL_ADDRESS_ACTIVITI) != null)
                {
                    execution.setVariable(WF_EMAIL_TEMPLATE, WF_EMAIL_TEMPLATE_ACTIVATE_WITH_INITIATOR);
                }
                else
                {
                    execution.setVariable(WF_EMAIL_TEMPLATE, WF_EMAIL_TEMPLATE_ACTIVATE);
                }
            }
//        }
        
        if (log.isDebugEnabled())
        {
            final StringBuilder msg = new StringBuilder()
                .append("'").append(email).append("' ").append(WF_REGISTRATION_STATUS).append(" ").append(execution.getVariable(WF_REGISTRATION_STATUS));
            log.debug(msg.toString());
        }
    }
}
