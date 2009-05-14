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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Records Management Service Implementation
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementServiceImpl implements RecordsManagementService
{
    private static Log logger = LogFactory.getLog(RecordsManagementServiceImpl.class);

    private Map<String, RecordState> states;
    
    private ActionService actionService;
    
    public void setActionService(ActionService actionService)
    {
        this.actionService = actionService;
    }
    
    public void setStates(List<RecordState> states)
    {
        // Clear the existing map of states
        this.states = new HashMap<String, RecordState>(states.size());
        for (RecordState recordState : states)
        {
            this.states.put(recordState.getName(), recordState);
        }
    }    
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#addRecordState(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, java.util.Map)
     */
    public void addRecordState(NodeRef record, String stateName, Map<String, Serializable> context)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("adding record state for node " + record);
        }
        
        // Get the state
        RecordState state = states.get(stateName);
        if (state == null)
        {
            throw new AlfrescoRuntimeException("The record state '" + stateName + "' has not been defined");
        }
        
        // Create the action
        Action action = this.actionService.createAction(state.getOnAddStateAction());
        action.setParameterValues(context);
        
        // Execute the action
        this.actionService.executeAction(action, record);        
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#removeRecordState(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, java.util.Map)
     */
    public void removeRecordState(NodeRef record, String stateName, Map<String, Serializable> context)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("removing record state for node " + record);
        }

        // Get the state
        RecordState state = states.get(stateName);
        if (state == null)
        {
            throw new AlfrescoRuntimeException("The record state '" + stateName + "' has not been defined");
        }
        
        // Create the action
        Action action = this.actionService.createAction(state.getOnRemoveStateAction());
        action.setParameterValues(context);
        
        // Execute the action
        this.actionService.executeAction(action, record);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getRecordStates(org.alfresco.service.cmr.repository.NodeRef)
     */
    public String[] getRecordStates(NodeRef record)
    {
        throw new UnsupportedOperationException("Currently unsupported");
    }    
}
