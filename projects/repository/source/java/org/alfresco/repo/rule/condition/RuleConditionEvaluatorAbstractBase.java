/**
 * 
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
