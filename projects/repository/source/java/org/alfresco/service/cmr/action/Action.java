/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.service.cmr.action;

import java.util.List;


/**
 * The rule action interface
 * 
 * @author Roy Wetherall
 */
public interface Action extends ParameterizedItem
{
	/**
	 * Get the name of the action definition that relates to this action
	 * 
	 * @return	the action defintion name
	 */
	String getActionDefinitionName();
	
	/**
	 * Indicates whether the action has any conditions specified
	 * 
	 * @return  true if the action has any conditions specified, flase otherwise
	 */
	boolean hasActionConditions();
	
	/**
	 * Gets the index of an action condition
	 * 
	 * @param actionCondition	the action condition
	 * @return					the index
	 */
	int indexOfActionCondition(ActionCondition actionCondition);
	
	/**
	 * Gets a list of the action conditions for this action
	 * 
	 * @return  list of action conditions
	 */
	List<ActionCondition> getActionConditions();
	
	/**
	 * Get the action condition at a given index
	 * 
	 * @param index  the index
	 * @return		 the action condition
	 */
	ActionCondition getActionCondition(int index);
	
	/**
	 * Add an action condition to the action
	 * 
	 * @param actionCondition  an action condition
	 */
	void addActionCondition(ActionCondition actionCondition);
	
	/**
	 * Add an action condition at the given index
	 * 
	 * @param index				the index
	 * @param actionCondition	the action condition
	 */
	void addActionCondition(int index, ActionCondition actionCondition);
	
	/**
	 * Replaces the current action condition at the given index with the 
	 * action condition provided.
	 * 
	 * @param index				the index
	 * @param actionCondition	the action condition
	 */
	void setActionCondition(int index, ActionCondition actionCondition);
	
	/**
	 * Removes an action condition
	 * 
	 * @param actionCondition  an action condition
	 */
	void removeActionCondition(ActionCondition actionCondition);
	
	/**
	 * Removes all action conditions 
	 */
	void removeAllActionConditions();
}
