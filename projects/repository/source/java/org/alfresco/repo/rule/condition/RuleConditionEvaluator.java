/**
 * 
 */
package org.alfresco.repo.rule.condition;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.RuleCondition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;

/**
 * @author Roy Wetherall
 */
public interface RuleConditionEvaluator
{
	public RuleConditionDefinition getRuleConditionDefintion();
	
    public boolean evaluate(
			RuleCondition ruleCondition,
            NodeRef actionableNodeRef, 
            NodeRef actionedUponNodeRef);
}
