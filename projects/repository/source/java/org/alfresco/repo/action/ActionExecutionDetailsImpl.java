/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.action;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionExecutionDetails;
import org.alfresco.service.cmr.action.ActionExecutionStatus;

/**
 * Action execution details implementation
 * 
 * @author Roy Wetherall
 */
public class ActionExecutionDetailsImpl implements ActionExecutionDetails
{
	/**
	 * The title
	 */
	private String title;
	
	/**
	 * The description
	 */
	private String description;
	
	/**
	 * The action
	 */
	private Action action;
	
	/**
	 * The action execution status
	 */
	private ActionExecutionStatus executionStatus = ActionExecutionStatus.PENDING;
	
	/**
	 * Indicates whether a compensating action has been executed
	 */
	private boolean compensatingActionExecuted = false;
	
	/**
	 * The error message
	 */
	private String errorMessage;
	
	/**
	 * The details of the error
	 */
	private String errorDetails;
	
	/**
	 * @see org.alfresco.service.cmr.action.ActionExecutionDetails#getTitle()
	 */
	public String getTitle()
	{
		return this.title;
	}
	
	/**
	 * Set the title
	 * 
	 * @param title  the title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionExecutionDetails#getDescription()
	 */
	public String getDescription()
	{
		return this.description;
	}
	
	/**
	 * Set the description
	 * 
	 * @param description	the description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionExecutionDetails#getAction()
	 */
	public Action getAction()
	{
		return this.action;
	}
	
	/**
	 * Set the action that this execution details relates to.
	 * 
	 * @param action	the action
	 */
	public void setAction(Action action)
	{
		this.action = action;
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionExecutionDetails#getExecutionStatus()
	 */
	public ActionExecutionStatus getExecutionStatus()
	{
		return this.executionStatus;
	}
	
	/**
	 * Set the execution status
	 * 
	 * @param executionStatus	the execution status
	 */
	public void setExecutionStatus(ActionExecutionStatus executionStatus)
	{
		this.executionStatus = executionStatus;
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionExecutionDetails#getCompensatingActionExecuted()
	 */
	public boolean getCompensatingActionExecuted()
	{
		return this.compensatingActionExecuted;
	}
	
	/**
	 * Set the value that indicates whether a compensating action has been executed.
	 * 
	 * @param compensatingActionExecuted	true if a compensating action has been executed, false otherwise
	 */
	public void setCompensatingActionExecuted(boolean compensatingActionExecuted)
	{
		this.compensatingActionExecuted = compensatingActionExecuted;
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionExecutionDetails#getErrorMessage()
	 */
	public String getErrorMessage()
	{
		return this.errorMessage;
	}

	/**
	 * Set the error message
	 * 
	 * @param errorMessage	 the error message
	 */
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
	
	/**
	 * @see org.alfresco.service.cmr.action.ActionExecutionDetails#getErrorDetails()
	 */
	public String getErrorDetails()
	{
		return this.errorDetails;
	}
	
	/**
	 * Set the error details
	 * 
	 * @param errorDetails	the error details
	 */
	public void setErrorDetails(String errorDetails)
	{
		this.errorDetails = errorDetails;
	}
}
