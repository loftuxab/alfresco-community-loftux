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
package org.alfresco.repo.action.evaluator;

import org.alfresco.repo.action.ActionConditionDefinitionImpl;
import org.alfresco.repo.action.ParameterizedItemAbstractBase;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.action.ActionConditionDefinition;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Rule condition evaluator abstract base implementation.
 * 
 * @author Roy Wetherall
 */
public abstract class ActionConditionEvaluatorAbstractBase extends ParameterizedItemAbstractBase implements ActionConditionEvaluator
{	
	
	protected ActionConditionDefinition actionConditionDefinition;		
	
	public void init()
	{
		// Call back to the action service to register the condition
		this.runtimeActionService.registerActionConditionEvaluator(this);
	}
	
	public ActionConditionDefinition getRuleConditionDefintion() 
	{
		if (this.actionConditionDefinition == null)
		{
			this.actionConditionDefinition = new ActionConditionDefinitionImpl(this.name);
			((ActionConditionDefinitionImpl)this.actionConditionDefinition).setTitle(getTitle());
			((ActionConditionDefinitionImpl)this.actionConditionDefinition).setDescription(getDescription());
			((ActionConditionDefinitionImpl)this.actionConditionDefinition).setAdhocPropertiesAllowed(getAdhocPropertiesAllowed());
			((ActionConditionDefinitionImpl)this.actionConditionDefinition).setConditionEvaluator(this.name);
			((ActionConditionDefinitionImpl)this.actionConditionDefinition).setParameterDefinitions(getParameterDefintions());
		}
		return this.actionConditionDefinition;
	}
	
	/**
     * @see org.alfresco.repo.action.evaluator.ActionConditionEvaluator#evaluate(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean evaluate(ActionCondition actionCondition, NodeRef actionedUponNodeRef)
    {
        checkMandatoryProperties(actionCondition, getRuleConditionDefintion());
        return evaluateImpl(actionCondition, actionedUponNodeRef);
    }
	
    /**
     * Evaluation implementation
     * @param actionedUponNodeRef   the actioned upon node reference
     */
	protected abstract boolean evaluateImpl(ActionCondition actionCondition, NodeRef actionedUponNodeRef);
}
