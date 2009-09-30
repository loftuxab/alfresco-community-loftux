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
package org.alfresco.module.org_alfresco_module_dod5015.action.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Relinquish Hold Action
 * 
 * @author Roy Wetherall
 */
public class RelinquishHoldAction extends RMActionExecuterAbstractBase
{
    /** Logger */
    private static Log logger = LogFactory.getLog(RelinquishHoldAction.class);

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        QName nodeType = this.nodeService.getType(actionedUponNodeRef);
        if (this.dictionaryService.isSubClass(nodeType, TYPE_HOLD) == true)
        {
            final NodeRef holdNodeRef = actionedUponNodeRef;
            List<ChildAssociationRef> frozenNodeAssocs = this.nodeService.getChildAssocs(holdNodeRef, ASSOC_FROZEN_RECORDS, RegexQNamePattern.MATCH_ALL);
            
            if (logger.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("Relinquishing hold ").append(holdNodeRef)
                    .append(" which has ").append(frozenNodeAssocs.size()).append(" frozen node(s).");
                logger.debug(msg.toString());
            }
            
            for (ChildAssociationRef assoc : frozenNodeAssocs)
            {
                final NodeRef nextFrozenNode = assoc.getChildRef();
                
                // Remove the freeze if this is the only hold that references the node
                removeFreeze(nextFrozenNode, holdNodeRef);
            }
            
            if (logger.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("Deleting hold object ").append(holdNodeRef)
                    .append(" with name ").append(nodeService.getProperty(holdNodeRef, ContentModel.PROP_NAME));
                logger.debug(msg.toString());
            }
            
            // Delete the hold node
            this.nodeService.deleteNode(holdNodeRef);
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
    private void removeFreeze(NodeRef nodeRef, NodeRef holdBeingRelinquished)
    {
        // Get all the holds and remove this node from them
        List<ChildAssociationRef> assocs = this.nodeService.getParentAssocs(nodeRef, ASSOC_FROZEN_RECORDS, RegexQNamePattern.MATCH_ALL);
        
        if (logger.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Removing freeze from ").append(nodeRef).append(" which has ")
                .append(assocs.size()).append(" holds");
            logger.debug(msg.toString());
        }

        // We should only remove the frozen aspect if there are no other 'holds' in effect for this node.
        boolean otherHoldsAreInEffect = false;
        for (ChildAssociationRef chAssRef : assocs)
        {
            if (!chAssRef.getParentRef().equals(holdBeingRelinquished))
            {
                otherHoldsAreInEffect = true;
                break;
            }
        }
        
        if (!otherHoldsAreInEffect)
        {
            if (logger.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("Removing frozen aspect from ").append(nodeRef);
                logger.debug(msg.toString());
            }

            // Remove the aspect
            this.nodeService.removeAspect(nodeRef, ASPECT_FROZEN);
        }
        
        // Remove the freezes on the child records as long as there is no other hold referencing them
        if (this.recordsManagementService.isRecordFolder(nodeRef) == true)
        {
            if (logger.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append(nodeRef).append(" is a record folder");
                logger.debug(msg.toString());
            }
            for (NodeRef record : recordsManagementService.getRecords(nodeRef))
            {
                removeFreeze(record, holdBeingRelinquished);
            }
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