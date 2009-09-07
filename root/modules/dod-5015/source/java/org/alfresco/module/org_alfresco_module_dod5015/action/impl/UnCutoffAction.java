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
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * UnCutoff action implementation
 * 
 * @author Roy Wetherall
 */
public class UnCutoffAction extends RMActionExecuterAbstractBase
{   
    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        if (nodeService.hasAspect(actionedUponNodeRef, ASPECT_DISPOSITION_LIFECYCLE) == true &&
            nodeService.hasAspect(actionedUponNodeRef, ASPECT_CUT_OFF) == true)
        {
            // Get the last disposition action
            DispositionAction da = recordsManagementService.getLastCompletedDispostionAction(actionedUponNodeRef);
            
            // Check that the last disposition action was a cutoff
            if (da == null || da.getName().equals("cutoff") == false)
            {
                // Can not undo cut off since cut off was not the last thing done
                throw new AlfrescoRuntimeException("Can not undo cut off since last disposition action was not cut off");
            }
            
            // Delete the current disposition action
            DispositionAction currentDa = recordsManagementService.getNextDispositionAction(actionedUponNodeRef);
            if (currentDa != null)
            {
                nodeService.deleteNode(currentDa.getNodeRef());
            }
            
            // Move the previous (cutoff) disposition back to be current
            nodeService.moveNode(da.getNodeRef(), actionedUponNodeRef, ASSOC_NEXT_DISPOSITION_ACTION, ASSOC_NEXT_DISPOSITION_ACTION);
            
            // Reset the started and completed property values
            nodeService.setProperty(da.getNodeRef(), PROP_DISPOSITION_ACTION_STARTED_AT, null);
            nodeService.setProperty(da.getNodeRef(), PROP_DISPOSITION_ACTION_STARTED_BY, null);
            nodeService.setProperty(da.getNodeRef(), PROP_DISPOSITION_ACTION_COMPLETED_AT, null);
            nodeService.setProperty(da.getNodeRef(), PROP_DISPOSITION_ACTION_COMPLETED_BY, null);  
            
            // Remove the cutoff aspect
            nodeService.removeAspect(actionedUponNodeRef, ASPECT_CUT_OFF);
            if (recordsManagementService.isRecordFolder(actionedUponNodeRef) == true)
            {
                List<NodeRef> records = this.recordsManagementService.getRecords(actionedUponNodeRef);
                for (NodeRef record : records)
                {
                    nodeService.removeAspect(record, ASPECT_CUT_OFF);
                }
            }
        }
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase#isExecutableImpl(org.alfresco.service.cmr.repository.NodeRef, java.util.Map, boolean)
     */
    @Override
    protected boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        boolean result = true;
        
        if (nodeService.hasAspect(filePlanComponent, ASPECT_DISPOSITION_LIFECYCLE) == true &&
            nodeService.hasAspect(filePlanComponent, ASPECT_CUT_OFF) == true)
        {
            // Get the last disposition action
            DispositionAction da = recordsManagementService.getLastCompletedDispostionAction(filePlanComponent);
            
            // Check that the last disposition action was a cutoff
            if (da == null || da.getName().equals("cutoff") == false)
            {
                if (throwException == true)
                {
                    // Can not undo cut off since cut off was not the last thing done
                    throw new AlfrescoRuntimeException("Can not undo cut off since last disposition action was not cut off");
                }
                result = false;
            }
        }
        else
        {
            if (throwException == true)
            {
                throw new AlfrescoRuntimeException("Can not undo cut off since the node has not been cut off");
            }
            result = false;
        }
        
        return result;
    }

    
}