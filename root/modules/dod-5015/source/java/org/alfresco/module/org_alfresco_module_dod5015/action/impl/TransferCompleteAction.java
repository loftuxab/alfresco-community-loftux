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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * Transfer complete action
 * 
 * @author Roy Wetherall
 */
public class TransferCompleteAction extends RMActionExecuterAbstractBase
{
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase#isExecutableImpl(org.alfresco.service.cmr.repository.NodeRef,
     *      java.util.Map, boolean)
     */
    @Override
    protected boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        QName className = this.nodeService.getType(filePlanComponent);
        if (this.dictionaryService.isSubClass(className, TYPE_TRANSFER) == true)
        {
            return true;
        }
        else
        {
            List<ChildAssociationRef> assocs = this.nodeService.getParentAssocs(filePlanComponent, ASSOC_TRANSFERRED, RegexQNamePattern.MATCH_ALL);
            return assocs.size() > 0;
        }
    }

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
     *      org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        QName className = this.nodeService.getType(actionedUponNodeRef);
        if (this.dictionaryService.isSubClass(className, TYPE_TRANSFER) == true)
        {
            List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(actionedUponNodeRef, ASSOC_TRANSFERRED, RegexQNamePattern.MATCH_ALL);
            for (ChildAssociationRef assoc : assocs)
            {
                markComplete(assoc.getChildRef());
            }

            // Delete the transfer object
            this.nodeService.deleteNode(actionedUponNodeRef);

            NodeRef transferNodeRef = (NodeRef) AlfrescoTransactionSupport.getResource(TransferAction.KEY_TRANSFER_NODEREF);
            if (transferNodeRef != null)
            {
                if (transferNodeRef.equals(actionedUponNodeRef))
                {
                    AlfrescoTransactionSupport.bindResource(TransferAction.KEY_TRANSFER_NODEREF, null);
                }
            }
        }
        else
        {
            throw new AlfrescoRuntimeException("Actioned upon node is not a valid transfer object.");
        }
    }

    /**
     * Marks the node complete
     * 
     * @param nodeRef
     *            disposition lifecycle node reference
     */
    private void markComplete(NodeRef nodeRef)
    {
        // Set the completed date
        this.nodeService.setProperty(nodeRef, PROP_DISPOSITION_ACTION_COMPLETED_AT, new Date());
        this.nodeService.setProperty(nodeRef, PROP_DISPOSITION_ACTION_COMPLETED_BY, AuthenticationUtil.getRunAsUser());

        // Update to the next disposition action
        updateNextDispositionAction(nodeRef);
    }

}
