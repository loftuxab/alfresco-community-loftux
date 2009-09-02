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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementAction;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEvent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Period;
import org.alfresco.service.namespace.QName;

/**
 * Disposition action implementation
 * 
 * @author Roy Wetherall
 */
public class DispositionActionDefinitionImpl implements DispositionActionDefinition, RecordsManagementModel
{
    /** Service registry */
    private RecordsManagementServiceRegistry services;
    
    /** Node service */
    private NodeService nodeService;
    
    /** Disposition action node reference */
    private NodeRef dispositionActionNodeRef;
    
    /** Action index */
    private int index;
    
    /**
     * Constructor
     * 
     * @param services  service registry
     * @param nodeRef   disposition action node reference
     * @param index     index of disposition action
     */
    public DispositionActionDefinitionImpl(RecordsManagementServiceRegistry services, NodeRef nodeRef, int index)
    {
        this.services = services;
        this.nodeService = services.getNodeService();
        this.dispositionActionNodeRef = nodeRef;
        this.index = index;
    }
    
    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition#getNodeRef()
     */
    public NodeRef getNodeRef()
    {
        return this.dispositionActionNodeRef;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition#getIndex()
     */
    public int getIndex()
    {
        return this.index;
    }
    
    /**
     *  @see org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition#getId()
     */
    public String getId()
    {
        return this.dispositionActionNodeRef.getId();
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition#getDescription()
     */
    public String getDescription()
    {
        return (String)nodeService.getProperty(this.dispositionActionNodeRef, PROP_DISPOSITION_DESCRIPTION);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition#getName()
     */
    public String getName()
    {
        return (String)nodeService.getProperty(this.dispositionActionNodeRef, PROP_DISPOSITION_ACTION_NAME);
    }
    
    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition#getLabel()
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
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition#getPeriod()
     */
    public Period getPeriod()
    {
        return (Period)nodeService.getProperty(this.dispositionActionNodeRef, PROP_DISPOSITION_PERIOD);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition#getPeriodProperty()
     */
    public QName getPeriodProperty()
    {
        QName result = null;
        String value = (String)nodeService.getProperty(this.dispositionActionNodeRef, PROP_DISPOSITION_PERIOD_PROPERTY);
        if (value != null)
        {
            result = QName.createQName(value);
        }
        return result;        
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition#getEvents()
     */
    @SuppressWarnings("unchecked")
    public List<RecordsManagementEvent> getEvents()
    {
        List<RecordsManagementEvent> events = null;
        Collection<String> eventNames = (Collection<String>)nodeService.getProperty(this.dispositionActionNodeRef, PROP_DISPOSITION_EVENT);
        if (eventNames != null)
        {
            events = new ArrayList<RecordsManagementEvent>(eventNames.size());
            for (String eventName : eventNames)
            {
                RecordsManagementEvent event = this.services.getRecordsManagementEventService().getEvent(eventName);
                events.add(event);
            }
        }
        else
        {
            events = java.util.Collections.EMPTY_LIST;
        }
        return events;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition#eligibleOnFirstCompleteEvent()
     */
    public boolean eligibleOnFirstCompleteEvent()
    {
        boolean result = true;        
        String value = (String)this.services.getNodeService().getProperty(this.dispositionActionNodeRef, PROP_DISPOSITION_EVENT_COMBINATION);
        if (value != null && value.equals("and") == true)
        {
            result = false;
        }
        return result;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition#getLocation()
     */
    public String getLocation()
    {
        return (String)this.services.getNodeService().getProperty(this.dispositionActionNodeRef, PROP_DISPOSITION_LOCATION);
    }
}
