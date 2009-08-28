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
import java.util.Collections;
import java.util.Set;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMDispositionActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Destroy action
 * 
 * @author Roy Wetherall
 */
public class DestroyAction extends RMDispositionActionExecuterAbstractBase
{
    private static final QName ASPECT_RM_GHOSTED = QName.createQName(RecordsManagementModel.RM_URI, "ghosted");

    private boolean ghostingEnabled = true;

    public void setGhostingEnabled(boolean ghostingEnabled)
    {
        this.ghostingEnabled = ghostingEnabled;
    }

    @Override
    protected void executeRecordFolderLevelDisposition(Action action, NodeRef recordFolder)
    {
        this.nodeService.deleteNode(recordFolder);
    }

    @Override
    protected void executeRecordLevelDisposition(Action action, NodeRef record)
    {
        // Do ghosting, if it is enabled
        if (this.ghostingEnabled)
        {
            // First purge (synchronously) all content properties
            Set<QName> props = this.nodeService.getProperties(record).keySet();
            props.retainAll(this.dictionaryService.getAllProperties(DataTypeDefinition.CONTENT));
            for (QName prop : props)
            {
                this.nodeService.removeProperty(record, prop);
            }

            // Finally, add the ghosted aspect (TODO: Any properties?)
            this.nodeService.addAspect(record, DestroyAction.ASPECT_RM_GHOSTED, Collections
                    .<QName, Serializable> emptyMap());
        }
        else
        {
            this.nodeService.deleteNode(record);
        }
    }
}
