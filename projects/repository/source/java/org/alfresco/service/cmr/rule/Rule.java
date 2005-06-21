
package org.alfresco.service.cmr.rule;

import java.io.Serializable;
import java.util.Date;
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
    String getId();
    
    /**
     * Get the created date
     * 
     * @return  the created date
     */
    Date getCreatedDate();
    
    /**
     * Get the modified date
     * 
     * @return  the modified date
     */
    Date getModifiedDate();

    /**
     * Get the rule type
     * 
     * @return  the rule type
     */
    RuleType getRuleType();

    /**
     * Get the title of the rule
     * 
     * @return	the title
     */
    String getTitle();

    /**
     * Set the title of the rule
     * 
     * @param title  the title
     */
    void setTitle(String title);

    /**
     * Get the description of the rule
     * 
     * @return	the description of the rule
     */
    String getDescription();

    /**
     * Set the description of the rule
     * 
     * @param description  the description of the rule
     */
    void setDescription(String description);

    /**
     * Get a list of rule conditions.
     * 
     * @return     the list of rule conditions
     */
    List<RuleCondition> getRuleConditions();

    /**
     * Add a rule condition to the rule.
     * 
     * @param ruleConditionDefinition	the rule condition definition
     * @param parameterValues			the parameter values
     * @return							the added rule condition
     */
    RuleCondition addRuleCondition(
            RuleConditionDefinition ruleConditionDefinition,
            Map<String, Serializable> parameterValues);

    /**
     * Remove a rule condition
     * 
     * @param ruleCondition		the rule condition
     */
    void removeRuleCondition(RuleCondition ruleCondition);
    
    /**
     * Removes all the rule conditions from the rule   
     */
    void removeAllRuleConditions();

    /**
     * Get a list of rule actions.
     * 
     * @return      the list of rule actions
     */
    List<RuleAction> getRuleActions();

    /**
     * Add a rule action to the rule.
     * 
     * @param ruleActionDefinition	the rule action definition
     * @param parameterValues		the action parameters
     * @return						the rule action
     */
    RuleAction addRuleAction(
    		RuleActionDefinition ruleActionDefinition,
    		Map<String, Serializable> parameterValues);
    
    /**
     * Remove a rule action
     * 
     * @param ruleAction	the rule action
     */
    void removeRuleAction(RuleAction ruleAction);
    
    /**
     * Removes all the rule actions
     */
    void removeAllRuleActions();

}