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
import java.util.HashMap;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Freeze Action
 * 
 * @author Roy Wetherall
 */
public class FreezeAction extends RMActionExecuterAbstractBase
{
    public static final String PARAM_REASON = "freeze.reason";
    
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        QName nodeType = this.nodeService.getType(actionedUponNodeRef);
        
        // Check the existance of the node and that it is a disposition lifecycle node
        if (this.nodeService.hasAspect(actionedUponNodeRef, ASPECT_RECORD) == true ||
            this.dictionaryService.isSubClass(nodeType, TYPE_RECORD_FOLDER) == true)
        {
            // Get the property values
            String reason = (String)action.getParameterValue(PARAM_REASON);
            if (reason == null || reason.length() == 0)
            {
                throw new AlfrescoRuntimeException("Can not freeze a record without a reason.");
            }
            
            // Get the root rm node
            NodeRef root = this.recordsManagementService.getRecordsManagementRoot(actionedUponNodeRef);
            
            // Get the hold object
            // TODO check to see if there is a related hold in the transaction context
            
            // Calculate a transfer name
            QName nodeDbid = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "node-dbid");
            Long dbId = (Long)this.nodeService.getProperty(actionedUponNodeRef, nodeDbid);
            String transferName = "Transfer -" + padString(dbId.toString(), 10);
            
            // Create the hold object
            Map<QName, Serializable> holdProps = new HashMap<QName, Serializable>(2);
            holdProps.put(ContentModel.PROP_NAME, transferName);
            holdProps.put(PROP_HOLD_REASON, reason);
            NodeRef holdNodeRef = this.nodeService.createNode(  root, 
                                                                ASSOC_HOLDS, 
                                                                QName.createQName(RM_URI, transferName), 
                                                                TYPE_HOLD,
                                                                holdProps).getChildRef();
            
            // Link the record to the hold
            // TODO what should we do about the assoc names?
            this.nodeService.addChild(  holdNodeRef, 
                                        actionedUponNodeRef, 
                                        ASSOC_FROZEN_RECORDS, 
                                        ASSOC_FROZEN_RECORDS);
            
            // Apply the freeze aspect
            this.nodeService.addAspect(actionedUponNodeRef, ASPECT_FROZEN, null);
            
            // TODO if the actioned upon node is a container (or records folder) do we need to traverse
            //      down the tree marking stuff as frozen
        }
        else
        {
            throw new AlfrescoRuntimeException("Can only freeze records or record folders.");
        }
        
    }
}