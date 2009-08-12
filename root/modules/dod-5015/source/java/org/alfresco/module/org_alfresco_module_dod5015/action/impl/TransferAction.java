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

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMDispositionActionExecuterAbstractBase;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Transfer action
 * 
 * @author Roy Wetherall
 */
public class TransferAction extends RMDispositionActionExecuterAbstractBase
{    
    /** Transfer node reference key */
    private static final String KEY_TRANSFER_NODEREF = "transferNodeRef";
    
    /** Indictates whether the transfer is an accession or not */
    private boolean isAccession = false;
    
    /**
     * Indicates whether this transfer is an accession or not
     * 
     * @param isAccession
     */
    public void setIsAccession(boolean isAccession)
    {
        this.isAccession = isAccession;
    }
    
    /**
     * Do not set the transfer action to auto-complete
     * 
     * @see org.alfresco.module.org_alfresco_module_dod5015.action.RMDispositionActionExecuterAbstractBase#getSetDispositionActionComplete()
     */
    @Override
    public boolean getSetDispositionActionComplete()
    {
        return false;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.action.RMDispositionActionExecuterAbstractBase#executeRecordFolderLevelDisposition(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeRecordFolderLevelDisposition(Action action, NodeRef recordFolder)
    {
        doTransfer(recordFolder);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.action.RMDispositionActionExecuterAbstractBase#executeRecordLevelDisposition(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeRecordLevelDisposition(Action action, NodeRef record)
    {
        doTransfer(record);
    }
    
    /**
     * Create the transfer node and link the dispositoin lifecycle node beneth it
     * 
     * @param dispositionLifeCycleNodRef        disposition lifecycle node
     */
    private void doTransfer(NodeRef dispositionLifeCycleNodRef)
    {
        // Get the root rm node
        NodeRef root = this.recordsManagementService.getRecordsManagementRoot(dispositionLifeCycleNodRef);
        
        // Get the hold object
        NodeRef transferNodeRef = (NodeRef)AlfrescoTransactionSupport.getResource(KEY_TRANSFER_NODEREF);            
        if (transferNodeRef == null)
        {
            // Calculate a transfer name
            QName nodeDbid = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "node-dbid");
            Long dbId = (Long)this.nodeService.getProperty(dispositionLifeCycleNodRef, nodeDbid);
            String transferName = padString(dbId.toString(), 10);
            
            // Create the transfer object
            Map<QName, Serializable> transferProps = new HashMap<QName, Serializable>(2);
            transferProps.put(ContentModel.PROP_NAME, transferName);
            transferProps.put(PROP_TRANSFER_ACCESSION_INDICATOR, this.isAccession);
            transferNodeRef = this.nodeService.createNode(root, 
                                                      ASSOC_TRANSFERS, 
                                                      QName.createQName(RM_URI, transferName), 
                                                      TYPE_TRANSFER,
                                                      transferProps).getChildRef();
            
            // Bind the hold node reference to the transaction
            AlfrescoTransactionSupport.bindResource(KEY_TRANSFER_NODEREF, transferNodeRef);
        }
        
        // Link the record to the hold
        this.nodeService.addChild(transferNodeRef, 
                                  dispositionLifeCycleNodRef, 
                                  ASSOC_TRANSFERED, 
                                  ASSOC_TRANSFERED);
    }
}
