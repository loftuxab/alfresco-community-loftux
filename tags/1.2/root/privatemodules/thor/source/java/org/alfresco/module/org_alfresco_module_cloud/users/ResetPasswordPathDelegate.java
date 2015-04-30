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
package org.alfresco.module.org_alfresco_module_cloud.users;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This {@link JavaDelegate activiti delegate} is reponsible for checking the user's status, which in turn
 * determines which workflow path will be followed.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class ResetPasswordPathDelegate extends AbstractResetPasswordDelegate
{
    private static final Log log = LogFactory.getLog(ResetPasswordPathDelegate.class);
    
    private RegistrationService registrationService;
    
    public void setRegistrationService(RegistrationService registrationService)
    {
        this.registrationService = registrationService;
    }
    
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        // Note that in the cloud, the username == email address
        final String userName = (String) execution.getVariable(WF_PROP_USERNAME_ACTIVITI);
        ParameterCheck.mandatoryString(WF_PROP_USERNAME_ACTIVITI, userName);
        
        final boolean userIsRegistered = registrationService.isRegisteredEmailAddress(userName);
        final boolean userIsActivated = registrationService.isActivatedEmailAddress(userName);
        
        if (userIsRegistered)
        {
            execution.setVariable(WF_USERNAME_STATUS, WF_USERNAME_STATUS_REGISTERED);
        }
        else if (userIsActivated)
        {
            execution.setVariable(WF_USERNAME_STATUS, WF_USERNAME_STATUS_ACTIVATED);
        }
        else
        {
            execution.setVariable(WF_USERNAME_STATUS, WF_USERNAME_STATUS_NONE);
        }
        
        if (log.isDebugEnabled())
        {
            final StringBuilder msg = new StringBuilder()
                .append("'").append(userName).append("' ").append(WF_USERNAME_STATUS).append(" ").append(execution.getVariable(WF_USERNAME_STATUS));
            log.debug(msg.toString());
        }
    }
}
