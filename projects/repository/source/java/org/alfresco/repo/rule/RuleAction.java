/**
 * 
 */
package org.alfresco.repo.rule;

/**
 * The rule action interface
 * 
 * @author Roy Wetherall
 */
public interface RuleAction extends RuleItem
{
	/**
	 * Get the rule action definition
	 * 
	 * @return	the rule action definition 
	 */
    public RuleActionDefinition getRuleActionDefinition();
}
