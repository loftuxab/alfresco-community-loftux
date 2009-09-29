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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * Disposition instructions implementation
 * 
 * @author Roy Wetherall
 */
public class DispositionScheduleImpl implements DispositionSchedule,
                                                RecordsManagementModel
{
    private NodeService nodeService;
    private RecordsManagementServiceRegistry services;
    private NodeRef dispositionDefinitionNodeRef;
    
    private List<DispositionActionDefinition> actions;
    private Map<String, DispositionActionDefinition> actionsById;
    
    public DispositionScheduleImpl(RecordsManagementServiceRegistry services, NodeService nodeService,  NodeRef nodeRef)
    {
        // TODO check that we have a disposition definition node reference
        
        this.dispositionDefinitionNodeRef = nodeRef;
        this.nodeService = nodeService;
        this.services = services;
    }

    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule#getNodeRef()
     */
    public NodeRef getNodeRef()
    {
        return this.dispositionDefinitionNodeRef;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule#getDispositionAuthority()
     */
    public String getDispositionAuthority()
    {
        return (String)this.nodeService.getProperty(this.dispositionDefinitionNodeRef, PROP_DISPOSITION_AUTHORITY);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule#getDispositionInstructions()
     */
    public String getDispositionInstructions()
    {
        return (String)this.nodeService.getProperty(this.dispositionDefinitionNodeRef, PROP_DISPOSITION_INSTRUCTIONS);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule#isRecordLevelDisposition()
     */
    public boolean isRecordLevelDisposition()
    {
        boolean result = false;
        Boolean value = (Boolean)this.nodeService.getProperty(this.dispositionDefinitionNodeRef, PROP_RECORD_LEVEL_DISPOSITION);
        if (value != null)
        {
            result = value.booleanValue();
        }            
        return result;
    }

    public DispositionActionDefinition getDispositionActionDefinition(String id)
    {
        if (this.actions == null)
        {
            getDispositionActionsImpl();
        }
        
        return this.actionsById.get(id);
    }

    public List<DispositionActionDefinition> getDispositionActionDefinitions()
    {
        if (this.actions == null)
        {
            getDispositionActionsImpl();
        }
        
        return this.actions;
    }
    
    private void getDispositionActionsImpl()
    {
        List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(
                                                      this.dispositionDefinitionNodeRef, 
                                                      ASSOC_DISPOSITION_ACTION_DEFINITIONS, 
                                                      RegexQNamePattern.MATCH_ALL);
        this.actions = new ArrayList<DispositionActionDefinition>(assocs.size());
        this.actionsById = new HashMap<String, DispositionActionDefinition>(assocs.size()); 
        int index = 0;
        for (ChildAssociationRef assoc : assocs)
        {            
            DispositionActionDefinition da = new DispositionActionDefinitionImpl(services, assoc.getChildRef(), index); 
            actions.add(da);
            actionsById.put(da.getId(), da);
            index++;
        }
    }
    
    
    
    
}
