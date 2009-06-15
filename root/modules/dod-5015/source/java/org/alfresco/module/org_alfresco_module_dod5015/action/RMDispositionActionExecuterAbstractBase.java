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
package org.alfresco.module.org_alfresco_module_dod5015.action;

import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionInstructions;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author Roy Wetherall
 */
public abstract class RMDispositionActionExecuterAbstractBase extends RMActionExecuterAbstractBase
{
    /** Indicates whether the eligibility of the record should be checked or not */
    // TODO add the capability to override this value using a property on the action
    protected boolean checkEligibility = true;
    
    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        // Check the validity of the actoin (is it the next action, are we dealing with the correct type of object for the disposition level?
        DispositionInstructions di = checkDispositionActionExecutionValidity(actionedUponNodeRef);
        
        // TODO check for frozen state (can not execute a disposition action when frozen)
        
        // Check the eligability of the action
        if (checkEligibility == false || this.recordsManagementService.isNextDispositionActionEligible(actionedUponNodeRef) == true)
        {
            if (di.isRecordLevelDisposition() == true)
            {
                // Check that we do indeed have a record 
                if (this.recordsManagementService.isRecord(actionedUponNodeRef) == true)
                {
                    // Can only execute disposition action on record if declared
                    if (this.recordsManagementService.isRecordDeclared(actionedUponNodeRef) == true)
                    {
                        // Execute record level disposition
                        executeRecordLevelDisposition(action, actionedUponNodeRef);
                    }
                    else
                    {
                        throw new AlfrescoRuntimeException("Unable to execute disposition action" + 
                                getName() + 
                                ", because the record is not declared. (" 
                                + actionedUponNodeRef.toString() + 
                                ")");
                    }
                }
                else
                {
                    throw new AlfrescoRuntimeException("Unable to execute disposition action" + 
                            getName() + 
                            ", because disposition is expected at the record level and this node is not a record. (" 
                            + actionedUponNodeRef.toString() + 
                            ")");
                }
            }
            else
            {
                if (this.recordsManagementService.isRecordFolder(actionedUponNodeRef) == true)
                {
                    if (this.recordsManagementService.isRecordFolderDeclared(actionedUponNodeRef) == true)
                    {
                        executeRecordFolderLevelDisposition(action, actionedUponNodeRef);
                    }
                    else
                    {
                        throw new AlfrescoRuntimeException("Unable to execute disposition action" + 
                                getName() + 
                                ", because not all the records in the record are declared. (" 
                                + actionedUponNodeRef.toString() + 
                                ")");                        
                    }
                }
                else
                {
                    throw new AlfrescoRuntimeException("Unable to execute disposition action" + 
                            getName() + 
                            ", because disposition is expected at the record folder level and this node is not a record folder. (" 
                            + actionedUponNodeRef.toString() + 
                            ")");
                }
                
            }
            
            if (this.nodeService.exists(actionedUponNodeRef) == true)
            {
                // Update the disposition schedule
                this.recordsManagementService.updateNextDispositionAction(actionedUponNodeRef);
            }
        }
        else
        {
            throw new AlfrescoRuntimeException("Unable to execute disposition action" + 
                    getName() + 
                    ", because the next disposition action on the record or record folder is not eligiable. (" 
                    + actionedUponNodeRef.toString() + 
                    ")");
        }
    }

    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        // TODO add the "checkEligibility" parameter
    }
    
    /**
     * 
     * @param action
     * @param record
     */
    protected abstract void executeRecordLevelDisposition(Action action, NodeRef record);
    
    /**
     * 
     * @param action
     * @param recordFolder
     */
    protected abstract void executeRecordFolderLevelDisposition(Action action, NodeRef recordFolder);
    
    /**
     * 
     * @param nodeRef
     * @return
     */
    protected DispositionInstructions checkDispositionActionExecutionValidity(NodeRef nodeRef)
    {
        // Check the node has associated disposition instructions
        DispositionInstructions di = this.recordsManagementService.getDispositionInstructions(nodeRef);
        if (di == null)
        {
            throw new AlfrescoRuntimeException("Unable to find disposition instructions for node.  Can not execute disposition action " + 
                                                getName() + 
                                                ". (" 
                                                + nodeRef.toString() + 
                                                ")");
        }
        
        // Check the node has the disposition schedule aspect applied
        if (this.nodeService.hasAspect(nodeRef, ASPECT_DISPOSITION_SCHEDULE) == false)
        {
            throw new AlfrescoRuntimeException("Unable to execute disposition action" + 
                    getName() + 
                    ", because node does not have a disposition schedule set. (" 
                    + nodeRef.toString() + 
                    ")");
        }
        
        // Check this the next disposition action
        String actionName = (String)this.nodeService.getProperty(nodeRef, PROP_DISPOSITION_ACTION);
        if (actionName == null || actionName.equals(getName()) == false)
        {
            throw new AlfrescoRuntimeException("Unable to execute disposition action" + 
                    getName() + 
                    ", because this is not the next disposition action for this record or record folder. (" 
                    + nodeRef.toString() + 
                    ")");
        }
        
        return di;
    }

}
