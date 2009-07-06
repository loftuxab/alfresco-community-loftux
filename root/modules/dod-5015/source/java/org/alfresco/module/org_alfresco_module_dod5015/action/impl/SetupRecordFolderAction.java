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
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule;
import org.alfresco.module.org_alfresco_module_dod5015.VitalRecordDefinition;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Action to close the records folder
 * 
 * @author Roy Wetherall
 */
public class SetupRecordFolderAction extends RMActionExecuterAbstractBase
{
    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        if (this.recordsManagementService.isRecordFolder(actionedUponNodeRef) == true)
        {
            // Inherit the vital record details
            VitalRecordDefinition vrDef = this.recordsManagementService.getVitalRecordDefinition(actionedUponNodeRef);
            if (vrDef != null)
            {
                Map<QName, Serializable> props = new HashMap<QName, Serializable>(2);
                props.put(PROP_VITAL_RECORD_INDICATOR, vrDef.isVitalRecord());
                props.put(PROP_REVIEW_PERIOD, vrDef.getReviewPeriod());
                this.nodeService.addAspect(actionedUponNodeRef, ASPECT_VITAL_RECORD_DEFINITION, props);
            }
                
            // Set up the disposition schedule if the dispositions are being managed at the folder level
            DispositionSchedule di = this.recordsManagementService.getDispositionSchedule(actionedUponNodeRef);
            if (di != null && di.isRecordLevelDisposition() == false)
            {
                // Setup the next disposition action
                this.recordsManagementService.updateNextDispositionAction(actionedUponNodeRef);                
            }
        }
    }

    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        // TODO Auto-generated method stub
    }

}
