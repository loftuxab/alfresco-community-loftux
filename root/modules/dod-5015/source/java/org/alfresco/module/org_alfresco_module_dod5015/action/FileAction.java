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
package org.alfresco.module.org_alfresco_module_dod5015.action;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
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
        // TODO check the record is wihin a folder?                
        
        // TODO if this is a declared record already .. what do we do? .. it's a refile!
        
        // Get the record categories
        // TODO for now assume only one record category
        List<NodeRef> recordCategories = getRecordCategories(actionedUponNodeRef);
        if (recordCategories.size() == 0)
        {
            throw new AlfrescoRuntimeException("The record being declared has no associated record category.");
        }
        else if (recordCategories.size() != 1)
        {
            throw new AlfrescoRuntimeException("Multiple record categories when declaring a record is not yet supported.");
        }
        NodeRef recordCategory = recordCategories.get(0);        
        
        // Add the record and undeclared aspect
        nodeService.addAspect(actionedUponNodeRef, RecordsManagementModel.ASPECT_RECORD, null);
        nodeService.addAspect(actionedUponNodeRef, RecordsManagementModel.ASPECT_UNDECLARED_RECORD, null);
        
        // Get the records properties
        Map<QName, Serializable> recordProperties = this.nodeService.getProperties(actionedUponNodeRef);
        
        // Calculate the filed date
        Calendar fileCalendar = Calendar.getInstance();
        String year = Integer.toString(fileCalendar.get(Calendar.YEAR));
        QName nodeDbid = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "node-dbid");
        String recordId = year + "-" + padString(recordProperties.get(nodeDbid).toString(), 10);
        recordProperties.put(RecordsManagementModel.PROP_DATE_FILED, fileCalendar.getTime());
        recordProperties.put(RecordsManagementModel.PROP_IDENTIFIER, recordId);             
        
        // Set the record properties
        this.nodeService.setProperties(actionedUponNodeRef, recordProperties);        

        // Calculate the review schedule
        String reviewPeriod = (String)this.nodeService.getProperty(recordCategory, RecordsManagementModel.PROP_REVIEW_PERIOD);
        Date reviewAsOf = calculateAsOfDate(reviewPeriod, fileCalendar.getTime());
        if (reviewAsOf != null)
        {
            Map<QName, Serializable> reviewProps = new HashMap<QName, Serializable>(1);
            reviewProps.put(RecordsManagementModel.PROP_REVIEW_AS_OF, reviewAsOf);
            this.nodeService.addAspect(actionedUponNodeRef, RecordsManagementModel.ASPECT_REVIEW_SCHEDULE, reviewProps);
        }
        
        // TODO move this into a generic method we can reuse
        
        // Set up the details of the first disposition action
        NodeRef dispositionAction = getNextDispositionAction(recordCategory, actionedUponNodeRef);
        if (dispositionAction != null)
        {
            // Calculate the asOf date
            Date asOfDate = null;
            String period = (String)this.nodeService.getProperty(dispositionAction, RecordsManagementModel.PROP_DISPOSITION_PERIOD);
            if (period != null)
            {
                // Use NOW as the default context date
                Date contextDate = new Date();
                
                // Get the period properties value
                String periodPropertyValue = (String)this.nodeService.getProperty(dispositionAction, RecordsManagementModel.PROP_DISPOSITION_PERIOD_PROPERTY);
                if (periodPropertyValue != null)
                {
                    contextDate = (Date)this.nodeService.getProperty(actionedUponNodeRef, QName.createQName(periodPropertyValue));
                }
                
                // Calculate the asof date
                asOfDate = calculateAsOfDate(period, contextDate);
            }
            
            // Get the name of the action
            String dispositionActionName = (String)this.nodeService.getProperty(dispositionAction, RecordsManagementModel.PROP_DISPOSITION_ACTION_NAME);
            
            // Set the property values
            Map<QName, Serializable> dispositionProps = new HashMap<QName, Serializable>(4);
            dispositionProps.put(RecordsManagementModel.PROP_DISPOSITION_ACTION_ID, dispositionAction.getId());
            dispositionProps.put(RecordsManagementModel.PROP_DISPOSITION_ACTION, dispositionActionName);
            if (asOfDate != null)
            {
                dispositionProps.put(RecordsManagementModel.PROP_DISPOSITION_AS_OF, asOfDate);
            }
            
            // TODO all the event stuff ...
                    
            // TODO set up all the historical stuff
            
            // Apply the aspect
            this.nodeService.addAspect(actionedUponNodeRef, RecordsManagementModel.ASPECT_DISPOSITION_SCHEDULE, dispositionProps);
        }
    }

    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {              
    }

}
