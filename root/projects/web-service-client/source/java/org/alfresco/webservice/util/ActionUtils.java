/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.webservice.util;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.webservice.action.Action;
import org.alfresco.webservice.action.ActionExecutionResult;
import org.alfresco.webservice.action.ActionServiceSoapBindingStub;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;

/**
 * Utility methods making it easy to execute actions.
 * 
 * @author Roy Wetherall
 */
public class ActionUtils 
{
	/**
	 * Executes an action with the provided parameters, returning the result if appropriate.  Return nulls in the case
	 * where no return result for the action has been set.
	 * 
	 * @param actionedUpon	the node that the aciton will action upon
	 * @param actionName	the action name (eg: 'ExecuteScript')
	 * @param parameters	the parameter values of the action
	 * @return Stirng		the result result of the action, null if none provided or action a	
	 */
	public static String executeAction(Reference actionedUpon, String actionName, Map<String, String> parameters)
	{
		String result = null;
		Predicate predicate = new Predicate(new Reference[]{actionedUpon}, null, null);
		
		try
		{
			// Get the action service
			ActionServiceSoapBindingStub actionService = WebServiceFactory.getActionService();
			
			// Create the action object
			Action action = new Action();
			action.setActionName(actionName);
			
			// Set the action parameters if some provided
			if (parameters != null && parameters.size() != 0)
			{
				// Create the list of namedValues to be set on the action
				NamedValue[] namedValues = new NamedValue[parameters.size()];
				int index = 0;
				for (Map.Entry<String, String> entry : parameters.entrySet()) 
				{
					namedValues[index] = Utils.createNamedValue(entry.getKey(), entry.getValue());
					index++;
				}
				
				// Set the parameter values
				action.setParameters(namedValues);
			}
			else
			{
				// TODO for now place a value in the parameter array
				action.setParameters(new NamedValue[]{Utils.createNamedValue("temp", "temp")});
			}
			
			// Execute the action		
			ActionExecutionResult[] actionResults = actionService.executeActions(predicate, new Action[]{action});
			Action actionResult = actionResults[0].getActions(0);
			
			// Look for a return result
			for (NamedValue namedValueResult : actionResult.getParameters()) 
			{
				if (namedValueResult.getName().equals("result") == true)
				{
					result = namedValueResult.getValue();
					break;
				}
			}
		}
		catch (RemoteException exception)
		{
			throw new WebServiceException("Unable to execute action", exception);
		}	
		
		return result;
	}
	
	/**
	 * Executes a script against the actioned upon node.
	 * 
	 * @param actionedUpon	the actioned upon node
	 * @param script		the script node
	 * @return String		the result of the script (null if none)
	 */
	public static String executeScript(Reference actionedUpon, Reference script)
	{
		Map<String, String> parameters = new HashMap<String, String>(1);
		parameters.put("script-ref", Utils.getNodeRef(script));
		return executeAction(actionedUpon, "script", parameters);
	}
	
}
