/**
 * 
 */
package org.alfresco.repo.rule.ruletype;

import java.util.List;

import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.rule.CommonResourceAbstractBase;
import org.alfresco.repo.rule.RuleExecution;
import org.alfresco.repo.rule.RuleRegistration;
import org.alfresco.repo.rule.common.RuleTypeImpl;
import org.alfresco.service.cmr.repository.NodeRef;
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

	public void setPolicyComponent(PolicyComponent policyComponent) 
	{
		this.policyComponent = policyComponent;
	}
	
	public void setRuleService(RuleService ruleService) 
	{
		this.ruleService = ruleService;
	}
	
	protected abstract String getDisplayLabel();
	
	public void init()
	{
		// Call back to rule service to register rule type
		((RuleRegistration)this.ruleService).registerRuleType(this);
		
		// Register the policy bahaviour
		registerPolicyBehaviour();
	}
	
	protected abstract void registerPolicyBehaviour();
    
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
        if (this.ruleService.hasRules(actionableNodeRef) == true)
        {
            List<Rule> rules = this.ruleService.getRulesByRuleType(
                    actionableNodeRef, 
                    this.ruleType);
			
            for (Rule rule : rules)
            {   
				((RuleExecution)this.ruleService).addRulePendingExecution(actionableNodeRef, actionedUponNodeRef, rule);
            }
        }
    }

    
}
