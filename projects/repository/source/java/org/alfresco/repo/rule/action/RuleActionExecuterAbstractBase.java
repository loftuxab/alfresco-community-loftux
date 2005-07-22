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
package org.alfresco.repo.rule.action;

import org.alfresco.repo.rule.RuleItemAbstractBase;
import org.alfresco.repo.rule.RuleRegistration;
import org.alfresco.repo.rule.common.RuleActionDefinitionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleActionDefinition;

/**
 * Rule action executor abstract base.
 * 
 * @author Roy Wetherall
 */
public abstract class RuleActionExecuterAbstractBase extends RuleItemAbstractBase implements RuleActionExecuter
{
	protected RuleActionDefinition ruleActionDefinition;
	
	public void init()
	{
		((RuleRegistration)this.ruleService).registerRuleActionExecutor(this);
	}
	
	public RuleActionDefinition getRuleActionDefinition() 
	{
		if (this.ruleActionDefinition == null)
		{
			this.ruleActionDefinition = new RuleActionDefinitionImpl(this.name);
			((RuleActionDefinitionImpl)this.ruleActionDefinition).setTitle(getTitle());
			((RuleActionDefinitionImpl)this.ruleActionDefinition).setDescription(getDescription());
			((RuleActionDefinitionImpl)this.ruleActionDefinition).setRuleActionExecutor(this.name);
			((RuleActionDefinitionImpl)this.ruleActionDefinition).setParameterDefinitions(getParameterDefintions());
		}
		return this.ruleActionDefinition;
	}
	
	/**
     * @see org.alfresco.repo.rule.action.RuleActionExecuter#execute(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
     */
    public void execute(RuleAction ruleAction, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {        
        // Check the mandatory properties
        checkMandatoryProperties(ruleAction, this.ruleActionDefinition);
        
        // Execute the implementation
        executeImpl(ruleAction, actionableNodeRef, actionedUponNodeRef);        
    }
	
    /**
     * Execute the action implementation
     * 
     * @param actionableNodeRef     the actionable node
     * @param actionedUponNodeRef   the actioned upon node
     */
	protected abstract void executeImpl(RuleAction ruleAction, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef);

     
}
