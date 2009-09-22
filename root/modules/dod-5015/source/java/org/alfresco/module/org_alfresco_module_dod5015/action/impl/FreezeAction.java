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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
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
    /** Parameter names */
    public static final String PARAM_REASON = "reason";
    
    /** Hold node reference key */
    private static final String KEY_HOLD_NODEREF = "holdNodeRef";
    
    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        if (this.recordsManagementService.isRecord(actionedUponNodeRef) == true ||
            this.recordsManagementService.isRecordFolder(actionedUponNodeRef) == true)
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
            NodeRef holdNodeRef = (NodeRef)AlfrescoTransactionSupport.getResource(KEY_HOLD_NODEREF);            
            if (holdNodeRef == null)
            {
                // Calculate a transfer name
                QName nodeDbid = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "node-dbid");
                Long dbId = (Long)this.nodeService.getProperty(actionedUponNodeRef, nodeDbid);
                String transferName = padString(dbId.toString(), 10);
                
                // Create the hold object
                Map<QName, Serializable> holdProps = new HashMap<QName, Serializable>(2);
                holdProps.put(ContentModel.PROP_NAME, transferName);
                holdProps.put(PROP_HOLD_REASON, reason);
                holdNodeRef = this.nodeService.createNode(root, 
                                                          ASSOC_HOLDS, 
                                                          QName.createQName(RM_URI, transferName), 
                                                          TYPE_HOLD,
                                                          holdProps).getChildRef();
                
                // Bind the hold node reference to the transaction
                AlfrescoTransactionSupport.bindResource(KEY_HOLD_NODEREF, holdNodeRef);
            }
                
            // Link the record to the hold
            this.nodeService.addChild(  holdNodeRef, 
                                        actionedUponNodeRef, 
                                        ASSOC_FROZEN_RECORDS, 
                                        ASSOC_FROZEN_RECORDS);
            
            // Apply the freeze aspect
            Map<QName, Serializable> props = new HashMap<QName, Serializable>(2);
            props.put(PROP_FROZEN_AT, new Date());
            props.put(PROP_FROZEN_BY, AuthenticationUtil.getFullyAuthenticatedUser());
            this.nodeService.addAspect(actionedUponNodeRef, ASPECT_FROZEN, props);
                        
            // Mark all the folders contents as frozen
            if (this.recordsManagementService.isRecordFolder(actionedUponNodeRef) == true)
            {
                List<NodeRef> records = this.recordsManagementService.getRecords(actionedUponNodeRef);
                for (NodeRef record : records)
                {
                    this.nodeService.addAspect(record, ASPECT_FROZEN, props);
                }
            }
        }
        else
        {
            throw new AlfrescoRuntimeException("Can only freeze records or record folders.");
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
    public Set<QName> getProtectedProperties()
    {
        HashSet<QName> qnames = new HashSet<QName>();
        qnames.add(PROP_HOLD_REASON);
        return qnames;
    }

    @Override
    protected boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        if (this.recordsManagementService.isRecord(filePlanComponent) == true ||
                this.recordsManagementService.isRecordFolder(filePlanComponent) == true)
        {
            // Get the property values
            if(parameters != null)
            {
                String reason = (String)parameters.get(PARAM_REASON);
                if (reason == null || reason.length() == 0)
                {
                    if(throwException)
                    {
                        throw new AlfrescoRuntimeException("Can not freeze a record without a reason.");
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        else
        {
            if(throwException)
            {
                throw new AlfrescoRuntimeException("Can only freeze records or record folders.");
            }
            else
            {
                return false;
            }
        }        
    }

    
}