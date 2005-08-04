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

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author Roy Wetherall
 */
public interface ActionService
{
	ActionDefinition getActionDefinition(String name);
	
	List<ActionDefinition> getActionDefinitions();
	
	ActionConditionDefinition getActionConditionDefinition(String name);
	
	List<ActionConditionDefinition> getActionConditionDefinitions();
	
	Action createAction(String name);
	
	CompositeAction createCompositeAction();
	
	ActionCondition createActionCondition(String name);
	
	void executeAction(Action action, NodeRef actionedUponNodeRef);
	
	void executeAction(Action action, NodeRef actionedUponNodeRef, boolean checkConditions);
	
	boolean evaluateAction(Action action, NodeRef actionedUponNodeRef);
	
	boolean evaluateActionCondition(ActionCondition condition, NodeRef actionedUponNodeRef);
	
	// TODO
	// void saveAction(Action action);
}
