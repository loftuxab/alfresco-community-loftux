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
package org.alfresco.module.org_alfresco_module_cloud.users;

import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This {@link JavaDelegate activiti delegate} is responsible for actually updating the user's password.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class PerformResetPasswordDelegate extends AbstractResetPasswordDelegate
{
    private static final Log log = LogFactory.getLog(PerformResetPasswordDelegate.class);
    
    private MutableAuthenticationService authenticationService;
    private TaskService                  activitiTaskService;
    
    public void setAuthenticationService(MutableAuthenticationService service)
    {
        this.authenticationService = service;
    }
    
    public void setTaskService(TaskService service)
    {
        this.activitiTaskService = service;
    }
    
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        // This method chooses to take a rather indirect route to access the password value.
        // This is for security reasons. We do not want to store the password in the Activiti DB.
        
        
        // We can get the username from the execution (process scope).
        final String username = (String) execution.getVariable(WorkflowModelResetPassword.WF_PROP_USERNAME_ACTIVITI);
        
        // But we cannot get the password from the execution as we have intentionally not stored the password there.
        // Instead we recover the password from the specific task in which it was set.
        
        List<Task> activitiTasks = activitiTaskService.createTaskQuery()
                                          .taskDefinitionKey(WorkflowModelResetPassword.TASK_RESET_PASSWORD)
                                          .processInstanceId(execution.getProcessInstanceId())
                                      .list();
        if (activitiTasks.size() != 1)
        {
            throw new AlfrescoRuntimeException("Unexpected count of task instances: " + activitiTasks.size());
        }
        Task activitiTask = activitiTasks.get(0);
        String activitiTaskId = activitiTask.getId();
        final String password = (String) activitiTaskService.getVariable(activitiTaskId, WorkflowModelResetPassword.WF_PROP_PASSWORD_ACTIVITI);
        
        if (log.isDebugEnabled())
        {
            log.debug("Retrieved new password from task " + activitiTaskId);
        }
        
        ParameterCheck.mandatoryString(WF_PROP_USERNAME_ACTIVITI, username);
        ParameterCheck.mandatoryString(WF_PROP_PASSWORD_ACTIVITI, password);
        
        if (log.isDebugEnabled())
        {
            log.debug("Changing password for " + username);
            // Don't log the password. :)
        }
        
        this.authenticationService.setAuthentication(username, password.toCharArray());
    }
}
