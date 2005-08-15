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
package org.alfresco.repo.action.executer;

import org.alfresco.repo.action.ActionDefinitionImpl;
import org.alfresco.repo.action.ParameterizedItemAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionDefinition;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Rule action executor abstract base.
 * 
 * @author Roy Wetherall
 */
public abstract class ActionExecuterAbstractBase extends ParameterizedItemAbstractBase implements ActionExecuter
{
	/**
	 * Action definition
	 */
	protected ActionDefinition actionDefinition;
	
	/**
	 * Init method	 
	 */
	public void init()
	{
		this.runtimeActionService.registerActionExecuter(this);
	}
	
	/**
	 * Get rule action definition
	 * 
	 * @return	the action definition object
	 */
	public ActionDefinition getRuleActionDefinition() 
	{
		if (this.actionDefinition == null)
		{
			this.actionDefinition = new ActionDefinitionImpl(this.name);
			((ActionDefinitionImpl)this.actionDefinition).setTitle(getTitle());
			((ActionDefinitionImpl)this.actionDefinition).setDescription(getDescription());
			((ActionDefinitionImpl)this.actionDefinition).setAdhocPropertiesAllowed(getAdhocPropertiesAllowed());
			((ActionDefinitionImpl)this.actionDefinition).setRuleActionExecutor(this.name);
			((ActionDefinitionImpl)this.actionDefinition).setParameterDefinitions(getParameterDefintions());
		}
		return this.actionDefinition;
	}
	
	/**
     * @see org.alfresco.repo.action.executer.ActionExecuter#execute(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
     */
    public void execute(Action action, NodeRef actionedUponNodeRef)
    {        
        // Check the mandatory properties
        checkMandatoryProperties(action, this.actionDefinition);
        
        // Execute the implementation
        executeImpl(action, actionedUponNodeRef);        
    }
	
    /**
     * Execute the action implementation
     * 
     * @param action				the action
     * @param actionedUponNodeRef   the actioned upon node
     */
	protected abstract void executeImpl(Action action, NodeRef actionedUponNodeRef);

     
}
