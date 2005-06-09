/**
 * 
 */
package org.alfresco.repo.rule;

/**
 * Rule condition interface
 * 
 * @author Roy Wetherall
 */
public interface RuleCondition extends RuleItem
{
	/**
	 * Get the rule confition definition
	 * 
	 * @return	the rule condition definition
	 */
    public RuleConditionDefinition getRuleConditionDefinition();
}
