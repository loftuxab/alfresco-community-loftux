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
package org.alfresco.module.org_alfresco_module_dod5015;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementAction;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * @author Roy Wetherall
 */
public class DispositionActionImpl implements DispositionAction, 
                                              RecordsManagementModel
{
    private RecordsManagementServiceRegistry services;
    private NodeRef dispositionNodeRef;
    private DispositionActionDefinition dispositionActionDefinition;    
    
    /**
     * Constructor 
     * 
     * @param services
     * @param dispositionActionNodeRef
     */
    public DispositionActionImpl(RecordsManagementServiceRegistry services, NodeRef dispositionActionNodeRef)    
    {
        this.services = services;
        this.dispositionNodeRef = dispositionActionNodeRef;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionAction#getDispositionActionDefinition()
     */
    public DispositionActionDefinition getDispositionActionDefinition()
    {
        if (this.dispositionActionDefinition == null)
        {
            // Get the current action
            String id = (String)services.getNodeService().getProperty(this.dispositionNodeRef, PROP_DISPOSITION_ACTION_ID);
            
            // Get the disposition instructions for the owning node
            NodeRef recordNodeRef = this.services.getNodeService().getPrimaryParent(this.dispositionNodeRef).getParentRef();
            DispositionSchedule ds = this.services.getRecordsManagementService().getDispositionSchedule(recordNodeRef);
            
            // Get the disposition action definition
            this.dispositionActionDefinition = ds.getDispositionActionDefinition(id);
        }
        
        return this.dispositionActionDefinition;
        
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionAction#getNodeRef()
     */
    public NodeRef getNodeRef()
    {
       return this.dispositionNodeRef;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionAction#getLabel()
     */
    public String getLabel()
    {
        String name = getName();
        String label = name;
        
        // get the disposition action from the RM action service
        RecordsManagementAction action = this.services.getRecordsManagementActionService().getDispositionAction(name);
        if (action != null)
        {
            label = action.getLabel();
        }
        
        return label;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionAction#getId()
     */
    public String getId()
    {
        return (String)this.services.getNodeService().getProperty(this.dispositionNodeRef, PROP_DISPOSITION_ACTION_ID);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionAction#getName()
     */
    public String getName()
    {
        return (String)this.services.getNodeService().getProperty(this.dispositionNodeRef, PROP_DISPOSITION_ACTION);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionAction#getAsOfDate()
     */
    public Date getAsOfDate()
    {
        return (Date)this.services.getNodeService().getProperty(this.dispositionNodeRef, PROP_DISPOSITION_AS_OF);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionAction#isEventsEligible()
     */
    public boolean isEventsEligible()
    {
        return ((Boolean)this.services.getNodeService().getProperty(this.dispositionNodeRef, PROP_DISPOSITION_EVENTS_ELIGIBLE)).booleanValue();
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionAction#getCompletedAt()
     */
    public Date getCompletedAt()
    {
        return (Date)this.services.getNodeService().getProperty(this.dispositionNodeRef, PROP_DISPOSITION_ACTION_COMPLETED_AT);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionAction#getCompletedBy()
     */
    public String getCompletedBy()
    {
        return (String)this.services.getNodeService().getProperty(this.dispositionNodeRef, PROP_DISPOSITION_ACTION_COMPLETED_BY);
    }

    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionAction#getStartedAt()
     */
    public Date getStartedAt()
    {
        return (Date)this.services.getNodeService().getProperty(this.dispositionNodeRef, PROP_DISPOSITION_ACTION_STARTED_AT);
    }

    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionAction#getStartedBy()
     */
    public String getStartedBy()
    {
        return (String)this.services.getNodeService().getProperty(this.dispositionNodeRef, PROP_DISPOSITION_ACTION_STARTED_BY);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionAction#getEventCompletionDetails()
     */
    public List<EventCompletionDetails> getEventCompletionDetails()
    {
        List<ChildAssociationRef> assocs = this.services.getNodeService().getChildAssocs(
                                                        this.dispositionNodeRef, 
                                                        ASSOC_EVENT_EXECUTIONS, 
                                                        RegexQNamePattern.MATCH_ALL);
        List<EventCompletionDetails> result = new ArrayList<EventCompletionDetails>(assocs.size());
        for (ChildAssociationRef assoc : assocs)
        {
            Map<QName, Serializable> props = this.services.getNodeService().getProperties(assoc.getChildRef()); 
            String eventName = (String)props.get(PROP_EVENT_EXECUTION_NAME); 
            EventCompletionDetails ecd = new EventCompletionDetails(
                    assoc.getChildRef(), eventName, 
                    this.services.getRecordsManagementEventService().getEvent(eventName).getDisplayLabel(),
                    getBooleanValue(props.get(PROP_EVENT_EXECUTION_AUTOMATIC), false),
                    getBooleanValue(props.get(PROP_EVENT_EXECUTION_COMPLETE), false),
                    (Date)props.get(PROP_EVENT_EXECUTION_COMPLETED_AT),
                    (String)props.get(PROP_EVENT_EXECUTION_COMPLETED_BY));
            result.add(ecd);
        }
        
        return result;
    }
    
    /**
     * Helper method to deal with boolean values
     * 
     * @param value
     * @param defaultValue
     * @return
     */
    private boolean getBooleanValue(Object value, boolean defaultValue)
    {
        boolean result = defaultValue;
        if (value != null && value instanceof Boolean)
        {
            result = ((Boolean)value).booleanValue();
        }
        return result;
    }

}
