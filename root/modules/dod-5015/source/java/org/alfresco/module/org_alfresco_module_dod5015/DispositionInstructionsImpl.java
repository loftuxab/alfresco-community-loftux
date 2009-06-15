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

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * @author Roy Wetherall
 */
public class DispositionInstructionsImpl implements DispositionInstructions,
                                                    RecordsManagementModel
{
    private ServiceRegistry services;
    
    private NodeRef dispositionDefinitionNodeRef;
    
    private List<DispositionAction> actions;
    private Map<String, DispositionAction> actionsById;
    
    public DispositionInstructionsImpl(ServiceRegistry services,  NodeRef nodeRef)
    {
        // TODO check that we have a disposition definition node reference
        
        this.dispositionDefinitionNodeRef = nodeRef;
        this.services = services;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionInstructions#getDispositionAuthority()
     */
    public String getDispositionAuthority()
    {
        return (String)this.services.getNodeService().getProperty(this.dispositionDefinitionNodeRef, PROP_DISPOSITION_AUTHORITY);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionInstructions#getDispositionInstructions()
     */
    public String getDispositionInstructions()
    {
        return (String)this.services.getNodeService().getProperty(this.dispositionDefinitionNodeRef, PROP_DISPOITION_INSTRUCTIONS);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.DispositionInstructions#isRecordLevelDisposition()
     */
    public boolean isRecordLevelDisposition()
    {
        boolean result = false;
        Boolean value = (Boolean)this.services.getNodeService().getProperty(this.dispositionDefinitionNodeRef, PROP_RECORD_LEVEL_DISPOSITION);
        if (value != null)
        {
            result = value.booleanValue();
        }            
        return result;
    }

    public DispositionAction getDispositionAction(String id)
    {
        if (this.actions == null)
        {
            getDispositionActionsImpl();
        }
        
        return this.actionsById.get(id);
    }

    public List<DispositionAction> getDispositionActions()
    {
        if (this.actions == null)
        {
            getDispositionActionsImpl();
        }
        
        return this.actions;
    }
    
    private void getDispositionActionsImpl()
    {
        List<ChildAssociationRef> assocs = this.services.getNodeService().getChildAssocs(
                                                      this.dispositionDefinitionNodeRef, 
                                                      ASSOC_DISPOSITION_ACTIONS, 
                                                      RegexQNamePattern.MATCH_ALL);
        this.actions = new ArrayList<DispositionAction>(assocs.size());
        this.actionsById = new HashMap<String, DispositionAction>(assocs.size()); 
        int index = 0;
        for (ChildAssociationRef assoc : assocs)
        {
            NodeRef nodeRef = assoc.getChildRef();
            DispositionAction da = new DispositionActionImpl(services, assoc.getChildRef(), index); 
            actions.add(da);
            actionsById.put(da.getId(), da);
            index++;
        }
    }
    
    
    
    
}
