/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.rule.RuleCondition;
import org.alfresco.repo.rule.RuleConditionDefinition;

/**
 * @author Roy Wetherall
 */
public class RuleConditionImpl extends RuleItemImpl implements Serializable,
        RuleCondition
{
    /**
     * 
     */
    private static final long serialVersionUID = 3257288015402644020L;
    
    /**
     * 
     */
    private RuleConditionDefinition ruleConditionDefinition;

    /**
     * 
     */
    public RuleConditionImpl(RuleConditionDefinition ruleConditionDefinition)
    {
        this(ruleConditionDefinition, null);
    }

    /**
     * @param parameterValues
     */
    public RuleConditionImpl(
            RuleConditionDefinition ruleConditionDefinition, 
            Map<String, Serializable> parameterValues)
    {
        super(parameterValues);
        this.ruleConditionDefinition = ruleConditionDefinition;
    }

    /**
     * @see org.alfresco.repo.rule.RuleCondition#getRuleConditionDefinition()
     */
    public RuleConditionDefinition getRuleConditionDefinition()
    {
        return this.ruleConditionDefinition;
    }

}
