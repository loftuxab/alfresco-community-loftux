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
package org.alfresco.service.cmr.action;

/**
 * Action execution details interface.
 * 
 * @author Roy Wetherall
 */
public interface ActionExecutionDetails
{
	/**
	 * Gets the title of the action that is being executed.
	 * 
	 * @return	the title of the action
	 */
	String getTitle();
	
	/**
	 * Gets the action that these execution details relate to.  Will return null
	 * if the action was not saved when it was executed.
	 * 
	 * @return	the action
	 */
	Action getAction();
	
	/**
	 * Get the current execution status.
	 * 
	 * @return  the current execution status
	 */
	ActionExecutionStatus getExecutionStatus();
	
	/**
	 * Gets the error message, null if no error encountered.
	 * 
	 * @return	the error message
	 */
	String getErrorMessage();
	
	/**
	 * Gets the detailed error message, usually a stack trace.  Null if no
	 * error was encountered.
	 * 
	 * @return	the detailed error message
	 */
	String getErrorDetails();
}
