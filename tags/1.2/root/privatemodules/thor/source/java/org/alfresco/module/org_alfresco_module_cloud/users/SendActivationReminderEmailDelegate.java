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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceException;
import org.alfresco.module.org_alfresco_module_cloud.registration.WorkflowModelSelfSignup;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;

/**
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class SendActivationReminderEmailDelegate extends AbstractResetPasswordDelegate
{
    private RegistrationService registrationService;
    private WorkflowService workflowService;
    
    public void setRegistrationService(RegistrationService registrationService)
    {
        this.registrationService = registrationService;
    }
    
    public void setWorkflowService(WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }
    
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        // So we're running within the context of a reset-password workflow. But this class will be sending messages
        // to an ongoing signup workflow. Don't get confused between these two workflows!
        
        final String username = (String) execution.getVariable(WorkflowModelResetPassword.WF_PROP_USERNAME_ACTIVITI);
        ParameterCheck.mandatoryString(WF_PROP_USERNAME_ACTIVITI, username);
        
        // If we've got to this stage, we know that the user/email address is a registered, but not an active user.
        WorkflowTaskQuery query = new WorkflowTaskQuery();
        query.setActive(Boolean.TRUE);
        query.setTaskState(WorkflowTaskState.IN_PROGRESS);
        // This is instead of setProcessName(), which was used for jBPM workflows.
        query.setTaskName(WorkflowModelSelfSignup.WF_ACCOUNT_ACTIVATION_PENDING_TASK);
        
        HashMap<QName, Object> props = new HashMap<QName, Object>();
        props.put(WorkflowModelSelfSignup.WF_PROP_EMAIL_ADDRESS, username);
        query.setProcessCustomProps(props);
        
        List<WorkflowTask> tasks = workflowService.queryTasks(query);
        // Given what we know above, we should expect to find one task in this list.
        if (tasks == null || tasks.isEmpty())
        {
            throw new RegistrationServiceException("Could not resend activation request.");
        }
        
        // We need to request that the ongoing signup workflow resend the activation email.
        final WorkflowTask signupTask = tasks.get(0);
        final String signupId = signupTask.getPath().getInstance().getId();
        Map<QName, Serializable> signupTaskProps = signupTask.getProperties();
        final String key = (String) signupTaskProps.get(WorkflowModelSelfSignup.WF_PROP_KEY);
        registrationService.resendActivationRequest(signupId, key);
    }
}
