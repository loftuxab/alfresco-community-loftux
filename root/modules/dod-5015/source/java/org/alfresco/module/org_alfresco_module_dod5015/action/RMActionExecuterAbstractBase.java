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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAction;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.springframework.beans.factory.BeanNameAware;

/**
 * Records management action executer base class
 * 
 * @author Roy Wetherall
 */
public abstract class RMActionExecuterAbstractBase  extends ActionExecuterAbstractBase
                                                    implements RecordsManagementAction,
                                                               RecordsManagementModel,
                                                               BeanNameAware
{
    /** Action name */
    protected String name;
    
    /** Node service */
    protected NodeService nodeService;
    
    /** Dictionary service */
    protected DictionaryService dictionaryService;
    
    /** Action service */
    private ActionService actionService;
    
    /** Records management action service */
    private RecordsManagementActionService recordsManagementActionService;
    
    /**
     * Set node service
     * 
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Set the dictionary service
     * 
     * @param dictionaryService
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    /**
     * Set action service 
     * 
     * @param actionService
     */
    public void setActionService(ActionService actionService)
    {
        this.actionService = actionService;
    }
    
    /**
     * Set records managment service
     * 
     * @param recordsManagementActionService
     */
    public void setRecordsManagementActionService(RecordsManagementActionService recordsManagementActionService)
    {
        this.recordsManagementActionService = recordsManagementActionService;
    }
    
    /**
     * Init method
     */
    @Override
    public void init()
    {
        super.init();
        
        // Do the RM action registration
        this.recordsManagementActionService.register(this);
    }
    
    /**
     * @see org.alfresco.repo.action.CommonResourceAbstractBase#setBeanName(java.lang.String)
     */
    public void setBeanName(String name)
    {
        this.name = name;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAction#getName()
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAction#execute(org.alfresco.service.cmr.repository.NodeRef, java.util.Map)
     */
    public void execute(NodeRef filePlanComponent, Map<String, Serializable> parameters)
    {
        // Create the action
        Action action = this.actionService.createAction(name);
        action.setParameterValues(parameters);
        
        // Execute the action
        this.actionService.executeAction(action, filePlanComponent);          
    }
        
    /**
     * Get the record categories for a given file plan component
     * 
     * @param nodeRef
     * @return
     */
    protected List<NodeRef> getRecordCategories(NodeRef nodeRef)
    {
        // Get the record categories for this node
        List<NodeRef> recordCategories = new ArrayList<NodeRef>(1);
        getAncestorsOfType(nodeRef, recordCategories, TYPE_RECORD_CATEGORY);
        return recordCategories;
    }    
    
    /**
     * Get the record folders for a given file plan component
     * 
     * @param nodeRef
     * @return
     */
    protected List<NodeRef> getRecordFolders(NodeRef nodeRef)
    {
        // Get the record folders for this node
        List<NodeRef> recordFolders = new ArrayList<NodeRef>(1);
        getAncestorsOfType(nodeRef, recordFolders, TYPE_RECORD_FOLDER);
        return recordFolders;
    }

    /**
     * Gets the ancestors of the specified type for a given file plan component
     * 
     * @param nodeRef
     * @param results
     * @param typeToMatch
     */
    private void getAncestorsOfType(NodeRef nodeRef, List<NodeRef> results, QName typeToMatch)
    {
        if (nodeRef != null)
        {
            if (typeToMatch.equals(nodeService.getType(nodeRef)) == true)
            {
                results.add(nodeRef);
            }
            else
            {
                List<ChildAssociationRef> assocs = nodeService.getParentAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
                for (ChildAssociationRef assoc : assocs)
                {
                    NodeRef parent = assoc.getParentRef();
                    getAncestorsOfType(parent, results, typeToMatch);
                }
            }
        }
    }
    
    /**
     * TODO Tempory helper method to help since we are currently presuming only one record category
     * @param nodeRef
     * @return
     */
    protected NodeRef getRecordCategory(NodeRef nodeRef)
    {
        List<NodeRef> recordCategories = getRecordCategories(nodeRef);
        if (recordCategories.size() == 0)
        {
            throw new AlfrescoRuntimeException("The record being declared has no associated record category.");
        }
        else if (recordCategories.size() != 1)
        {
            throw new AlfrescoRuntimeException("Multiple record categories when declaring a record is not yet supported.");
        }
        return recordCategories.get(0); 
    }
    
    /**
     * Function to pad a string with zero '0' characters to the required length
     * 
     * @param s     String to pad with leading zero '0' characters
     * @param len   Length to pad to
     * 
     * @return padded string or the original if already at >=len characters 
     */
    protected String padString(String s, int len)
    {
       String result = s;
       for (int i=0; i<(len - s.length()); i++)
       {
           result = "0" + result;
       }
       return result;
    }
    
    protected NodeRef getNextDispositionAction(NodeRef recordCategory, NodeRef record)
    {
        NodeRef dispositionAction = null;
        
        String actionId = (String)this.nodeService.getProperty(record, RecordsManagementModel.PROP_DISPOSITION_ACTION_ID);
        List<ChildAssociationRef> dispositionActions = this.nodeService.getChildAssocs(recordCategory, RecordsManagementModel.ASSOC_DISPOSITION_ACTIONS, RegexQNamePattern.MATCH_ALL);

        if (dispositionActions.size() != 0)
        {
            if (actionId == null)
            {
                dispositionAction = dispositionActions.get(0).getChildRef();
            }
            else
            {
                int index = 0;
                for (ChildAssociationRef assoc : dispositionActions)
                {
                    NodeRef temp = assoc.getChildRef();
                    if (actionId.equals(temp.getId()) == true)
                    {
                        break;
                    }         
                    index++;
                }
                
                index++;
                if (index != dispositionActions.size())
                {
                    dispositionAction = dispositionActions.get(index).getChildRef();
                }
            }
        }
        
        return dispositionAction;
    }
    
    protected NodeRef getDispositionAction(NodeRef record)
    {
        NodeRef dispositionAction = null;
        String actionId = (String)this.nodeService.getProperty(record, RecordsManagementModel.PROP_DISPOSITION_ACTION_ID);
        if (actionId != null)
        {
            dispositionAction = new NodeRef(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore"), actionId);
        }
        return dispositionAction;
    }
    
    /**
     * 
     * @param recordCategory
     * @param record
     */
    protected void setNextDispositionAction(NodeRef recordCategory, NodeRef record)
    {
        // Set up the details of the first disposition action
        NodeRef dispositionAction = getNextDispositionAction(recordCategory, record);
        if (dispositionAction != null)
        {
            // Get the properties of the record
            Map<QName, Serializable> recordProps = this.nodeService.getProperties(record);
            
            // Check for the disposition schedule aspect
            if (this.nodeService.hasAspect(record, ASPECT_DISPOSITION_SCHEDULE) == false)
            {
                // Add the disposition schedule aspect
                this.nodeService.addAspect(record, ASPECT_DISPOSITION_SCHEDULE, null);
            }
            else
            {
                // Set the previous action details
                recordProps.put(PROP_PREVIOUS_DISPOSITION_DISPOSITION_ACTION, recordProps.get(PROP_DISPOSITION_ACTION));
                recordProps.put(PROP_PREVIOUS_DISPOSITION_DISPOSITION_DATE, new Date());
            }
            
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
                    contextDate = (Date)this.nodeService.getProperty(record, QName.createQName(periodPropertyValue));
                }
                
                // Calculate the asof date
                asOfDate = calculateAsOfDate(period, contextDate);
            }            
            
            // Get the name of the action
            String dispositionActionName = (String)this.nodeService.getProperty(dispositionAction, PROP_DISPOSITION_ACTION_NAME);
            
            // Set the property values
            recordProps.put(PROP_DISPOSITION_ACTION_ID, dispositionAction.getId());
            recordProps.put(PROP_DISPOSITION_ACTION, dispositionActionName);
            if (asOfDate != null)
            {
                recordProps.put(PROP_DISPOSITION_AS_OF, asOfDate);
            }
            
            // TODO all the event stuff ...                    
            
            // Set the properties of the record
            this.nodeService.setProperties(record, recordProps);            
        }
    }
    
    /**
     * TODO .. this code is in here for the demo 
     *      .. should be moved into the period data type implementation
     *      
     * Calculates the next interval date for a given type of date interval.
     * 
     * @param period
     * @param fromDate
     * @return 
     */
    protected Date calculateAsOfDate(String period, Date date)
    {
        // Split the period value and retrieve the unit and value
        String[] arr = period.split("\\|");
        String unit = arr[0];
        String valueString = arr[1];
        int value = Integer.parseInt(valueString);
        
        Calendar calendar = Calendar.getInstance();     
        calendar.setTime(date);
        
        if (unit.equals("none") == true)
        {
            // Return null as there is no period date to calculate
            return null;
        }
        else if (unit.equals("day") == true) 
        {
            // Daily calculation
            calendar.add(Calendar.DAY_OF_YEAR, value);
        } 
        else if (unit.equals("week") == true) 
        {
            // Weekly calculation
            calendar.add(Calendar.WEEK_OF_YEAR, value);
        } 
        else if (unit.equals("month") == true) 
        {
            // Monthly calculation
            calendar.add(Calendar.MONTH, value);
        }
        else if (unit.equals("year") == true) 
        {
            // Annual calculation
            calendar.add(Calendar.YEAR, value);
        }
        else if (unit.equals("monthend") == true) 
        {
            // Month end calculation
            calendar.add(Calendar.MONTH, value);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            
            // Set the time one minute to midnight
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
        } 
        //TODO Should this be 'quarterend'?
        else if (unit.equals("quaterend") == true) 
        {
            // Quarter end calculation
            calendar.add(Calendar.MONTH, value*3);
            int currentMonth = calendar.get(Calendar.MONTH);
            if (currentMonth >= 0 && currentMonth <= 2)
            {
                calendar.set(Calendar.MONTH, 0);
            }
            else if (currentMonth >= 3 && currentMonth <= 5)
            {
                calendar.set(Calendar.MONTH, 3);
            }
            else if (currentMonth >= 6 && currentMonth <= 8)
            {
                calendar.set(Calendar.MONTH, 6);
            }
            else if (currentMonth >= 9 && currentMonth <= 11)
            {
                calendar.set(Calendar.MONTH, 9);
            }
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_YEAR, -1);

            // Set the time one minute to midnight
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
        } 
        else if (unit.equals("yearend") == true) 
        {
            // Year end calculation
            calendar.add(Calendar.YEAR, value);
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_YEAR, -1);

            // Set the time one minute to midnight
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
        } 
        else if (unit.equals("fyend") == true) 
        {
            // Financial year end calculation
            throw new RuntimeException("Finacial year end is currently unsupported.");

            // Set the time one minute to midnight 
            //calendar.set(Calendar.HOUR_OF_DAY, 23);
            //calendar.set(Calendar.MINUTE, 59);
        } 
                
        return calendar.getTime();
    } 

}
