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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.action;

import java.util.Calendar;
import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScheduledDispositionJob implements Job
{
    private static Log logger = LogFactory.getLog(ScheduledDispositionJob.class);

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
    	RecordsManagementActionService rmActionService
    	    = (RecordsManagementActionService)context.getJobDetail().getJobDataMap().get("recordsManagementActionService");
    	NodeService nodeService = (NodeService)context.getJobDetail().getJobDataMap().get("nodeService");
    	

    	// Calculate the date range used in the query
    	Calendar cal = Calendar.getInstance();
    	String year = String.valueOf(cal.get(Calendar.YEAR));
    	String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
    	String dayOfMonth = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

    	//TODO These pad() calls are in RMActionExecuterAbstractBase. I've copied them
    	//     here as I have no access to that class.
    	
    	final String currentDate = padString(year, 2) + "-" + padString(month, 2) +
    	    "-" + padString(dayOfMonth, 2) + "T00:00:00.00Z";
		
		StringBuilder msg = new StringBuilder();
		msg.append("Executing ")
		    .append(this.getClass().getSimpleName())
		    .append(" with currentDate ")
		    .append(currentDate);
		System.out.println(msg.toString());
    	
    	//TODO Copied the 1970 start date from the old RM JavaScript impl.
    	String dateRange = "[\"1970-01-01T00:00:00.00Z\" TO \"" + currentDate + "\"]";

    	// Execute the query and process the results
    	String query = "+ASPECT:\"rma:record\" +ASPECT:\"rma:dispositionSchedule\" +@rma\\:dispositionAsOf:" + dateRange;      

    	SearchService search = (SearchService)context.getJobDetail().getJobDataMap().get("searchService");
    	ResultSet results = search.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_LUCENE, query);
    	
    	List<NodeRef> resultNodes = results.getNodeRefs();
    	results.close();
    	
		msg = new StringBuilder();
		msg.append("Found ")
		    .append(resultNodes.size())
		    .append(" records eligible for disposition.");
		System.out.println(msg.toString());

    	
    	for (NodeRef node : resultNodes	)
    	{
    		String dispActionName = (String)nodeService.getProperty(node, RecordsManagementModel.PROP_DISPOSITION_ACTION_NAME);
    		
    		// TODO Hackery for the demo.
    		//
    		// Only automatically execute "cutoff" actions.
    		// destroy and transfer and anything else should be manual for now
    		if (dispActionName != null && dispActionName.equalsIgnoreCase("cutoff"))
    		{
    			rmActionService.executeRecordsManagementAction(node, dispActionName);
    		}
    		else
    		{
    			System.out.println("Request to automatically execute " + dispActionName
    					+ " action ignored.");
    		}
    	}
    }
    
    //TODO This has been pasted out of RMActionExecuterAbstractBase. To be relocated.
    private String padString(String s, int len)
    {
        String result = s;
        for (int i=0; i<(len - s.length()); i++)
        {
            result = "0" + result;
        }
        return result;
     }
}
