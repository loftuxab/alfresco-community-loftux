
package org.alfresco.service.cmr.rule;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Roy Wetherall
 */
public interface Rule 
{
    /**
     * Gets the unique identifier of the rule
     * 
     * @return  the id
     */
    public abstract String getId();

    /**
     * Get the rule type
     * 
     * @return  the rule type
     */
    public abstract RuleType getRuleType();

    /**
     * Get the title of the rule
     * 
     * @return	the title
     */
    public abstract String getTitle();

    /**
     * Set the title of the rule
     * 
     * @param title  the title
     */
    public abstract void setTitle(String title);

    /**
     * Get the description of the rule
     * 
     * @return	the description of the rule
     */
    public abstract String getDescription();

    /**
     * Set the description of the rule
     * 
     * @param description  the description of the rule
     */
    public abstract void setDescription(String description);

    /**
     * Get a list of rule conditions.
     * 
     * @return     the list of rule conditions
     */
    public abstract List<RuleCondition> getRuleConditions();

    /**
     * Add a rule condition to the rule.
     * 
     * @param ruleConditionDefinition	the rule condition definition
     * @param parameterValues			the parameter values
     * @return							the added rule condition
     */
    public abstract RuleCondition addRuleCondition(
            RuleConditionDefinition ruleConditionDefinition,
            Map<String, Serializable> parameterValues);

    /**
     * Remove a rule condition
     * 
     * @param ruleCondition		the rule condition
     */
    public abstract void removeRuleCondition(RuleCondition ruleCondition);

    /**
     * Get a list of rule actions.
     * 
     * @return      the list of rule actions
     */
    public abstract List<RuleAction> getRuleActions();

    /**
     * Add a rule action to the rule.
     * 
     * @param ruleActionDefinition	the rule action definition
     * @param parameterValues		the action parameters
     * @return						the rule action
     */
    public abstract RuleAction addRuleAction(
    		RuleActionDefinition ruleActionDefinition,
    		Map<String, Serializable> parameterValues);

    /**
     * Remove a rule action
     * 
     * @param ruleAction	the rule action
     */
    public abstract void removeRuleAction(RuleAction ruleAction);

}