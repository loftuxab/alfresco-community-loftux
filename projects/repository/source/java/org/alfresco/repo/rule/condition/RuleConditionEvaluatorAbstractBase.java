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
package org.alfresco.repo.rule.condition;

import org.alfresco.repo.rule.RuleItemAbstractBase;
import org.alfresco.repo.rule.RuleRegistration;
import org.alfresco.repo.rule.common.RuleConditionDefinitionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.RuleCondition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;

/**
 * Rule condition evaluator abstract base implementation.
 * 
 * @author Roy Wetherall
 */
public abstract class RuleConditionEvaluatorAbstractBase extends RuleItemAbstractBase implements RuleConditionEvaluator
{	
	protected RuleConditionDefinition ruleConditionDefinition;		
	
	public void init()
	{
		// Call back to the rule service to register the condition defintion
		((RuleRegistration)this.ruleService).registerRuleConditionEvaluator(this);
	}
	
	public RuleConditionDefinition getRuleConditionDefintion() 
	{
		if (this.ruleConditionDefinition == null)
		{
			this.ruleConditionDefinition = new RuleConditionDefinitionImpl(this.name);
			((RuleConditionDefinitionImpl)this.ruleConditionDefinition).setTitle(getTitle());
			((RuleConditionDefinitionImpl)this.ruleConditionDefinition).setDescription(getDescription());
			((RuleConditionDefinitionImpl)this.ruleConditionDefinition).setConditionEvaluator(this.name);
			((RuleConditionDefinitionImpl)this.ruleConditionDefinition).setParameterDefinitions(getParameterDefintions());
		}
		return this.ruleConditionDefinition;
	}
	
	/**
     * @see org.alfresco.repo.rule.condition.RuleConditionEvaluator#evaluate(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean evaluate(RuleCondition ruleCondition, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        checkMandatoryProperties(ruleCondition, getRuleConditionDefintion());
        return evaluateImpl(ruleCondition, actionableNodeRef, actionedUponNodeRef);
    }
	
    /**
     * Evaluation implementation
     * 
     * @param actionableNodeRef     the actionable node reference
     * @param actionedUponNodeRef   the actioned upon node reference
     */
	protected abstract boolean evaluateImpl(RuleCondition ruleCondition, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef);
}
