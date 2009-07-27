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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * Relinquish Hold Action
 * 
 * @author Roy Wetherall
 */
public class RelinquishHoldAction extends RMActionExecuterAbstractBase
{
    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        QName nodeType = this.nodeService.getType(actionedUponNodeRef);
        if (this.dictionaryService.isSubClass(nodeType, TYPE_HOLD) == true)
        {
            List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(actionedUponNodeRef, ASSOC_FROZEN_RECORDS, RegexQNamePattern.MATCH_ALL);
            for (ChildAssociationRef assoc : assocs)
            {
                // Remove the freeze if this is the only hold that references the node
                removeFreeze(assoc.getChildRef(), 1);
                
                // Remove the freeze's on the child records as long as there is no other hold referencing them
                if (this.recordsManagementService.isRecordFolder(actionedUponNodeRef) == true)
                {
                    List<NodeRef> records = this.recordsManagementService.getRecords(actionedUponNodeRef);
                    for (NodeRef record : records)
                    {
                        removeFreeze(record, 0);
                    }
                }
            }
            
            // Delete the hold node
            this.nodeService.deleteNode(actionedUponNodeRef);
        }
        else
        {
            throw new AlfrescoRuntimeException("Can not relinquish a hold on a node that is not of type " + TYPE_HOLD.toString() + 
                                               "(" + actionedUponNodeRef.toString() + ")");
        }
    }
    
    /**
     * Removes a freeze from a node
     * 
     * @param nodeRef   node reference
     */
    private void removeFreeze(NodeRef nodeRef, int count)
    {
        // Get all the holds and remove this node from them
        List<ChildAssociationRef> assocs = this.nodeService.getParentAssocs(nodeRef, ASSOC_FROZEN_RECORDS, RegexQNamePattern.MATCH_ALL);
        if (assocs.size() == count)
        {
            // Remove the aspect
            this.nodeService.removeAspect(nodeRef, ASPECT_FROZEN);
        }
    }
    
    @Override
    public Set<QName> getProtectedAspects()
    {
        HashSet<QName> qnames = new HashSet<QName>();
        qnames.add(ASPECT_FROZEN);
        return qnames;
    }

    @Override
    protected boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        QName nodeType = this.nodeService.getType(filePlanComponent);
        if (this.dictionaryService.isSubClass(nodeType, TYPE_HOLD) == true)
        {
            return true;
        }
        else
        {
            if(throwException)
            {
                throw new AlfrescoRuntimeException("Can not relinquish a hold on a node that is not of type " + TYPE_HOLD.toString() + 
                    "(" + filePlanComponent.toString() + ")");
            }
            else
            {
                return false;
            }
        }
    }

    
}