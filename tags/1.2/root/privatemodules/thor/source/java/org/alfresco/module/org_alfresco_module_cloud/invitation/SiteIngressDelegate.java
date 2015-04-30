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
package org.alfresco.module.org_alfresco_module_cloud.invitation;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This {@link JavaDelegate} implementation triggers the addition of a user (an email address) to a Share site.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public class SiteIngressDelegate extends AbstractCloudInvitationDelegate
{
    private static final Log log = LogFactory.getLog(SiteIngressDelegate.class);
    
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        String inviterEmail = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_INVITER_EMAIL_ADDRESS_ACTIVITI);
        String inviteeEmail = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_INVITEE_EMAIL_ADDRESS_ACTIVITI);
        String siteShortName = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_SITE_SHORT_NAME_ACTIVITI);
        String inviteeRole = (String) execution.getVariable(WorkflowModelCloudInvitation.WF_PROP_INVITEE_ROLE_ACTIVITI);
        
        ParameterCheck.mandatoryString(WorkflowModelCloudInvitation.WF_PROP_INVITER_EMAIL_ADDRESS_ACTIVITI, inviterEmail);
        ParameterCheck.mandatoryString(WorkflowModelCloudInvitation.WF_PROP_INVITEE_EMAIL_ADDRESS_ACTIVITI, inviteeEmail);
        ParameterCheck.mandatoryString(WorkflowModelCloudInvitation.WF_PROP_SITE_SHORT_NAME_ACTIVITI, siteShortName);
        
        if (log.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Adding ").append(inviteeEmail).append(" to ").append(siteShortName);
            log.debug(msg.toString());
        }
            
        cloudInvitationService.addInviteeToSite(inviterEmail, siteShortName, inviteeEmail, inviteeRole);
    }
}
