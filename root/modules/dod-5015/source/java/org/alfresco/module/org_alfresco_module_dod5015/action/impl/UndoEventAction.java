/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_dod5015.action.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionAction;
import org.alfresco.module.org_alfresco_module_dod5015.EventCompletionDetails;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Undo event action
 * 
 * @author Roy Wetherall
 */
public class UndoEventAction extends RMActionExecuterAbstractBase
{
    public static final String PARAM_EVENT_NAME = "eventName";
    
    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        String eventName = (String)action.getParameterValue(PARAM_EVENT_NAME);
        
        if (this.nodeService.hasAspect(actionedUponNodeRef, ASPECT_DISPOSITION_LIFECYCLE) == true)
        {
            // Get the next disposition action
            DispositionAction da = this.recordsManagementService.getNextDispositionAction(actionedUponNodeRef);
            if (da != null)
            {
                // Get the disposition event
                EventCompletionDetails event = getEvent(da, eventName);
                if (event != null)
                {
                    // Update the event so that it is undone
                    NodeRef eventNodeRef = event.getNodeRef();
                    Map<QName, Serializable> props = this.nodeService.getProperties(eventNodeRef);
                    props.put(PROP_EVENT_EXECUTION_COMPLETE, false);
                    props.put(PROP_EVENT_EXECUTION_COMPLETED_AT, null);
                    props.put(PROP_EVENT_EXECUTION_COMPLETED_BY, null);
                    this.nodeService.setProperties(eventNodeRef, props);
                    
                    // Check to see if the events eligible property needs to be updated
                    updateEventEigible(da);
                    
                }
                else
                {
                    throw new AlfrescoRuntimeException("The event " + eventName + " can not be undone, because it is not defined on the disposition lifecycle.");
                }
            }
        }
    }
    
    /**
     * Get the event from the dispostion action
     * 
     * @param da
     * @param eventName
     * @return
     */
    private EventCompletionDetails getEvent(DispositionAction da, String eventName)
    {
        EventCompletionDetails result = null;
        List<EventCompletionDetails> events = da.getEventCompletionDetails();
        for (EventCompletionDetails event : events)
        {
            if (eventName.equals(event.getEventName()) == true)
            {
                result = event;
                break;
            }
        }
        return result;
    }
    
    /**
     * 
     * @param da
     * @param nodeRef
     */
    private void updateEventEigible(DispositionAction da)
    {
        List<EventCompletionDetails> events = da.getEventCompletionDetails();
        
        boolean eligible = false;
        if (da.getDispositionActionDefinition().eligibleOnFirstCompleteEvent() == false)
        {
            eligible = true;
            for (EventCompletionDetails event : events)
            {
                if (event.isEventComplete() == false)
                {
                    eligible = false;
                    break;
                }
            }
        }
        else
        {
            for (EventCompletionDetails event : events)
            {
                if (event.isEventComplete() == true)
                {
                    eligible = true;
                    break;
                }
            }
        }
        
        // Update the property with the eligible value
        this.nodeService.setProperty(da.getNodeRef(), PROP_DISPOSITION_EVENTS_ELIGIBLE, eligible);
    }
    

    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        // TODO add parameter definitions ....
        // eventName

    }

}
