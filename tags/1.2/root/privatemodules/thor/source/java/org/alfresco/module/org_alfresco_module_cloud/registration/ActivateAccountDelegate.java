/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.task.Task;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.ParameterCheck;

/**
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class ActivateAccountDelegate extends AbstractSignupDelegate
{
    private TaskService activitiTaskService;

    public void setTaskService(TaskService service)
    {
        this.activitiTaskService = service;
    }
    
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        // This class uses a rather roundabout way to retrieve the password.
        // However this is intentional and is done for security reasons.
        
        // Get most of the signup metadata from the usual source - the execution variables.
        final String emailToSignUp = (String) execution.getVariable(WF_PROP_EMAIL_ADDRESS_ACTIVITI);
        final String firstName = (String) execution.getVariable(WF_PROP_FIRST_NAME_ACTIVITI);
        final String lastName = (String) execution.getVariable(WF_PROP_LAST_NAME_ACTIVITI);
        
        // But we cannot get the password from the execution as we have intentionally not stored it there.
        // Instead we recover the password from the 'accountActivationPendingTask' task itself.
        List<Task> tasks = activitiTaskService.createTaskQuery()
                                                  .taskDefinitionKey(WorkflowModelSelfSignup.WF_ACCOUNT_ACTIVATION_PENDING_TASK_STRING)
                                                  .processInstanceId(execution.getProcessInstanceId())
                                              .list();
        if (tasks.size() != 1)
        {
            throw new AlfrescoRuntimeException("Unexpected count of task instances: " + tasks.size());
        }
        Task task = tasks.get(0);
        final String password = (String) activitiTaskService.getVariable(task.getId(), WF_PROP_PASSWORD_ACTIVITI);

        ParameterCheck.mandatoryString(WF_PROP_EMAIL_ADDRESS_ACTIVITI, emailToSignUp);
        ParameterCheck.mandatoryString(WF_PROP_FIRST_NAME_ACTIVITI, firstName);
        ParameterCheck.mandatoryString(WF_PROP_LAST_NAME_ACTIVITI, lastName);
        ParameterCheck.mandatoryString(WF_PROP_PASSWORD_ACTIVITI, password);

        registrationService.createUser(emailToSignUp, firstName, lastName, password);
    }
}
