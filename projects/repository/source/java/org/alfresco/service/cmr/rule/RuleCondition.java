/**
 * 
 */
package org.alfresco.service.cmr.rule;


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
