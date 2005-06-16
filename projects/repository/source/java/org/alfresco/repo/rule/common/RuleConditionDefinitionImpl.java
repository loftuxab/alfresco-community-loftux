/**
 * 
 */
package org.alfresco.repo.rule.common;

import org.alfresco.service.cmr.rule.RuleConditionDefinition;

/**
 * Rule condition implementation class.
 * 
 * @author Roy Wetherall
 */
public class RuleConditionDefinitionImpl extends RuleItemDefinitionImpl 
                               implements RuleConditionDefinition
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3688505493618177331L;

    /**
     * Condition evaluator
     */
    private String conditionEvaluator;
    
    /**
     * Constructor
     * 
     * @param name                  the name
     */
    public RuleConditionDefinitionImpl(String name)
    {
        super(name);
    }

    /**
     * Set the condition evaluator
     * 
     * @param conditionEvaluator  the condition evaluator
     */
    public void setConditionEvaluator(String conditionEvaluator)
    {
        this.conditionEvaluator = conditionEvaluator;
    }
    
    /**
     * Get the condition evaluator
     * 
     * @return  the condition evaluator
     */
    public String getConditionEvaluator()
    {
        return conditionEvaluator;
    }
}
