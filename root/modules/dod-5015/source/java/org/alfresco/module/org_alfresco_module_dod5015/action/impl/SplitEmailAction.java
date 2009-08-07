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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.action.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.model.ImapModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Split Email Action
 * 
 * Splits the attachments for an email message to be independent records.
 * 
 * @author Mark Rogers
 */
public class SplitEmailAction extends RMActionExecuterAbstractBase
{
    /** Logger */
    private static Log logger = LogFactory.getLog(SplitEmailAction.class);

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
     *      org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        nodeService.getType(actionedUponNodeRef);
        logger.debug("split email:" + actionedUponNodeRef);

        if (recordsManagementService.isRecord(actionedUponNodeRef) == true)
        {
            if (recordsManagementService.isRecordDeclared(actionedUponNodeRef) == false)
            {
                ChildAssociationRef parent = nodeService.getPrimaryParent(actionedUponNodeRef);

                List<AssociationRef> refs = nodeService.getTargetAssocs(actionedUponNodeRef, ImapModel.ASSOC_IMAP_ATTACHMENT);

                for(AssociationRef ref : refs)
                {
                    /**
                     * Move the attachments up one level, to the parent folder of the record
                     */
                    logger.debug("split attachment:" + actionedUponNodeRef);
                    String bareName = (String)nodeService.getProperty(ref.getTargetRef(), ContentModel.PROP_NAME);
                    QName assocName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(bareName));
                    nodeService.moveNode(ref.getTargetRef(), parent.getParentRef(), parent.getTypeQName(), assocName);
                }

                /**
                 * Now get rid of the old attatchment folder
                 */
                List<AssociationRef> folderRefs = nodeService.getTargetAssocs(actionedUponNodeRef, ImapModel.ASSOC_IMAP_ATTACHMENTS_FOLDER);
                for(AssociationRef ref : folderRefs)
                {
                    nodeService.removeChild(parent.getParentRef(), ref.getTargetRef());
                }
            }
            else
            {
                throw new AlfrescoRuntimeException("Record has already been declared - can't split it. (" + actionedUponNodeRef.toString() + ")");
            }
        }
        else
        {
            throw new AlfrescoRuntimeException("Can only split a record. (" + actionedUponNodeRef.toString() + ")");
        }
    }

    @Override
    protected boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        if (recordsManagementService.isRecord(filePlanComponent) == true)
        {
            if (recordsManagementService.isRecordDeclared(filePlanComponent))
            {
                if (throwException)
                {
                    throw new AlfrescoRuntimeException("Can only split an undeclared record. (" + filePlanComponent.toString() + ")");
                }     
                else
                {
                    return false;
                }
            }
        }
        else
        {
            if (throwException)
            {
                throw new AlfrescoRuntimeException("Can only split a record. (" + filePlanComponent.toString() + ")");
            }
            else
            {
                return false;
            }
        }
        return true;
    }
}
