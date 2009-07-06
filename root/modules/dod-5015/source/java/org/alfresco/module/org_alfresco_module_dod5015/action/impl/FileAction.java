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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.VitalRecordDefinition;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Files a record into a particular record folder
 * 
 * @author Roy Wetherall
 */
public class FileAction extends RMActionExecuterAbstractBase
{
    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        // TODO check the record is within a folder?     
        
        // TODO check that the folder we are filing into is not closed
        
        // TODO if this is a declared record already .. what do we do? .. it's a re-file!
        
        // Get the disposition instrcutions for the actioned upon record
        DispositionSchedule di = this.recordsManagementService.getDispositionSchedule(actionedUponNodeRef);
                
        // Add the record and undeclared aspect
        nodeService.addAspect(actionedUponNodeRef, RecordsManagementModel.ASPECT_RECORD, null);
        
        // Get the records properties
        Map<QName, Serializable> recordProperties = this.nodeService.getProperties(actionedUponNodeRef);
        
        // Calculate the filed date and record identifier
        Calendar fileCalendar = Calendar.getInstance();
        String year = Integer.toString(fileCalendar.get(Calendar.YEAR));
        QName nodeDbid = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "node-dbid");
        String recordId = year + "-" + padString(recordProperties.get(nodeDbid).toString(), 10);
        recordProperties.put(RecordsManagementModel.PROP_DATE_FILED, fileCalendar.getTime());
        recordProperties.put(RecordsManagementModel.PROP_IDENTIFIER, recordId);             
        
        // Set the record properties
        this.nodeService.setProperties(actionedUponNodeRef, recordProperties);        

        // Calculate the review schedule
        VitalRecordDefinition viDef = this.recordsManagementService.getVitalRecordDefinition(actionedUponNodeRef);
        Date reviewAsOf = viDef.getNextReviewDate();
        if (reviewAsOf != null)
        {
            Map<QName, Serializable> reviewProps = new HashMap<QName, Serializable>(1);
            reviewProps.put(RecordsManagementModel.PROP_REVIEW_AS_OF, reviewAsOf);
            this.nodeService.addAspect(actionedUponNodeRef, RecordsManagementModel.ASPECT_VITAL_RECORD, reviewProps);
        }
        
        // Set up the disposition schedule if the dispositions are being managed at the record level
        if (di != null && di.isRecordLevelDisposition() == true)
        {
            // Setup the next disposition action
            this.recordsManagementService.updateNextDispositionAction(actionedUponNodeRef);
        }
    }

    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {              
        // No parameters
    }

}
