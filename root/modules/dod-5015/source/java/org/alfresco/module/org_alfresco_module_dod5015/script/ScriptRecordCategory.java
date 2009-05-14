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
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.repo.jscript.Scopeable;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.mozilla.javascript.Scriptable;

/**
 * @author Roy Wetherall
 */
public class ScriptRecordCategory implements Serializable, Scopeable
{
    private static final long serialVersionUID = 1801006559401292415L;

    private List<NodeRef> recordCategoryNodeRefs;
    
    /** Scriptable scope object */
    private Scriptable scope;
    
    /** Value converter */
    private ValueConverter valueConverter = new ValueConverter();
    
    private ServiceRegistry services;
    
    /**
     * @see org.alfresco.repo.jscript.Scopeable#setScope(org.mozilla.javascript.Scriptable)
     */
    public void setScope(Scriptable scope)
    {
        this.scope = scope;
    }
    
    /**
     * Constructor
     * 
     * @param scope
     * @param services
     * @param recordCategory
     */
    public ScriptRecordCategory(Scriptable scope, ServiceRegistry services, NodeRef recordCategory)
    {
        this.recordCategoryNodeRefs = new ArrayList<NodeRef>(1);
        this.recordCategoryNodeRefs.add(recordCategory);
        this.services = services;
        this.scope = scope;
    }
    
    /**
     * Constructor
     * 
     * @param scope
     * @param services
     * @param recordCategoryNodeRefs
     */
    public ScriptRecordCategory(Scriptable scope, ServiceRegistry services, List<NodeRef> recordCategoryNodeRefs)
    {
        this.recordCategoryNodeRefs = recordCategoryNodeRefs;
        this.services = services;
        this.scope = scope;
    }
    
    /**
     * TODO
     * Currently assumes the first record category as the one to use ...
     * 
     * @return
     */
    private NodeRef getRecordCategory()
    {
        return this.recordCategoryNodeRefs.get(0);
    }
    
    /**
     * 
     * @return
     */
    public boolean getHasRetention()
    {
        return ((Boolean)this.services.getNodeService().getProperty(
                                            getRecordCategory(), 
                                            RecordsManagementModel.PROP_RETENTION_INDICATOR)).booleanValue();
    }
    
    /**
     * 
     * @return
     */
    public String getNextRecordId()
    {
        return GUID.generate();
    }
    
    /**
     * 
     * @param fromDate
     * @return
     */
    public Serializable getCutOffDate(Serializable fromDate)
    {
        return calculatePropertyAsOfDate(RecordsManagementModel.PROP_CUT_OFF_SCHEDULE_PERIOD, fromDate);
    }
    
    /**
     * 
     * @param fromDate
     * @return
     */
    public Serializable getNextReviewDate(Serializable fromDate)
    {
        return calculatePropertyAsOfDate(RecordsManagementModel.PROP_REVIEW_PERIOD, fromDate);
    }
    
    /**
     * 
     * @param fromDate
     * @return
     */
    public Serializable getEndRetentionDate(Serializable fromDate)
    {
        return calculatePropertyAsOfDate(RecordsManagementModel.PROP_RETENTION_SCHEDULE_PERIOD, fromDate);
    }   
    
    /**
     * 
     * @param property
     * @param fromDate
     * @return
     */
    private Serializable calculatePropertyAsOfDate(QName property, Serializable fromDate)
    {
        // Get the period value of the property
        String period = (String)this.services.getNodeService().getProperty(getRecordCategory(), property);
        
        return calculateAsOfDate(period, fromDate);
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
    private Serializable calculateAsOfDate(String period, Serializable fromDate)
    {
        Date date = (Date)this.valueConverter.convertValueForRepo(fromDate);
        
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
        else if (unit.equals("quaterend") == true) 
        {
            // Quater end calculation
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
                
        return valueConverter.convertValueForScript(services, scope, null, calendar.getTime());
    }    
}
