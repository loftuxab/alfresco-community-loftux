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
package org.alfresco.repo.rule.ruletype;

import java.util.List;

import org.alfresco.repo.action.CommonResourceAbstractBase;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.rule.RuleTypeImpl;
import org.alfresco.repo.rule.RuntimeRuleService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleType;

/**
 * Rule type adapter abstract base class implmentation.
 * 
 * @author Roy Wetherall
 */
public abstract class RuleTypeAdapterAbstractBase extends CommonResourceAbstractBase implements RuleTypeAdapter
{
	/**
	 * The rule type
	 */
	protected RuleType ruleType;
	
	/**
	 * The policy component
	 */
    protected PolicyComponent policyComponent;    
	
	/**
	 * The rule service
	 */
    private RuleService ruleService;
    
    /**
     * The node service
     */
    private NodeService nodeService;
	
	/**
	 * Get the rule type
	 */
	public RuleType getRuleType() 
	{
		if (this.ruleType == null)
		{
			this.ruleType = new RuleTypeImpl(this.name);
			((RuleTypeImpl)this.ruleType).setDisplayLabel(getDisplayLabel());
		}
		return this.ruleType;
	}

	/**
	 * Set the policy component
	 * 
	 * @param policyComponent  the policy component
	 */
	public void setPolicyComponent(PolicyComponent policyComponent) 
	{
		this.policyComponent = policyComponent;
	}
	
	/**
	 * Set the node service
	 * 
	 * @param nodeService  the node service
	 */
	public void setNodeService(NodeService nodeService) 
	{
		this.nodeService = nodeService;
	}
	
	/**
	 * Set the rule service
	 * 
	 * @param ruleService
	 */
	public void setRuleService(RuleService ruleService) 
	{
		this.ruleService = ruleService;
	}
	
	/**
	 * Gets the display label for the rule type
	 * 
	 * @return  the display label
	 */
	protected abstract String getDisplayLabel();
	
	public void init()
	{
		// Call back to rule service to register rule type
		((RuntimeRuleService)this.ruleService).registerRuleType(this);
		
		// Register the policy bahaviour
		registerPolicyBehaviour();
	}
	
	/**
	 * Registers the policy behaviour that triggers the rule execution
	 */
	protected abstract void registerPolicyBehaviour();
    
	/**
	 * Execute the rules of the parents of the actioned upon node
	 * 
	 * @param actionedUponNodeRef	the actioned upon node reference
	 */
	protected void executeParentRules(NodeRef actionedUponNodeRef)
	{
		List<ChildAssociationRef> assocs = this.nodeService.getParentAssocs(actionedUponNodeRef);
		for (ChildAssociationRef assoc : assocs) 
		{
			executeRules(assoc.getParentRef(), actionedUponNodeRef);
		}
	}
	
	/**
	 * Execute rules that relate to the actionable node for this type on the
	 * actioned upon node reference.
	 * 
	 * @param actionableNodeRef		the actionable node reference
	 * @param actionedUponNodeRef	the actioned upon node reference
	 */
    protected void executeRules(
            NodeRef actionableNodeRef, 
            NodeRef actionedUponNodeRef)
    {
        if (this.ruleService.rulesEnabled(actionableNodeRef) == true && this.ruleService.hasRules(actionableNodeRef) == true)
        {
            List<Rule> rules = this.ruleService.getRules(
                    actionableNodeRef, 
                    true,
                    this.ruleType.getName());
			
            for (Rule rule : rules)
            {   
				((RuntimeRuleService)this.ruleService).addRulePendingExecution(actionableNodeRef, actionedUponNodeRef, rule);
            }
        }
    }

    
}
