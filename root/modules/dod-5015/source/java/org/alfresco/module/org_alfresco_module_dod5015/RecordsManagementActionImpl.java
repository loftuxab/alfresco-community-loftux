/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.beans.factory.BeanNameAware;

public class RecordsManagementActionImpl implements RecordsManagementAction, BeanNameAware
{
    protected ActionService actionService;
    protected RecordsManagementActionService recordsMgtActionService;
    private String actionName;
    private String beanName;
    
    public String getActionName()
    {
        return actionName;
    }

    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    public void setActionService(ActionService actionService)
    {
        this.actionService = actionService;
    }
    
    public void setRecordsManagementActionService(RecordsManagementActionService recordsMgtActionService)
    {
        this.recordsMgtActionService = recordsMgtActionService;
    }
    
    public void init()
    {
        this.recordsMgtActionService.register(this);
    }

    public void execute(NodeRef filePlanComponent, Map<String, Serializable> parameters)
    {
        // Create the action
        Action action = this.actionService.createAction(actionName);
        action.setParameterValues(parameters);
        
        // Execute the action
        this.actionService.executeAction(action, filePlanComponent);     
    }

    public String getName()
    {
        return this.beanName;
    }

    public void setBeanName(String beanName)
    {
        this.beanName = beanName;
    }
}
