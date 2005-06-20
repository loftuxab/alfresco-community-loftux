/**
 * Created on Jun 17, 2005
 */
package org.alfresco.repo.rule;

import org.alfresco.repo.rule.action.RuleActionExecutor;
import org.alfresco.repo.rule.condition.RuleConditionEvaluator;
import org.alfresco.repo.rule.ruletype.RuleTypeAdapter;

/**
 * @author Roy Wetherall
 */
public interface RuleRegistration
{
	void registerRuleType(RuleTypeAdapter ruleTypeAdapter);
	
	void registerRuleConditionEvaluator(RuleConditionEvaluator ruleConditionEvaluator);
	
	void registerRuleActionExecutor(RuleActionExecutor ruleActionExecutor);
}
