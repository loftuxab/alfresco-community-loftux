/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.rule.RuleCondition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;

/**
 * @author Roy Wetherall
 */
public class RuleConditionImpl extends RuleItemImpl implements Serializable,
        RuleCondition
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3257288015402644020L;
    
    /**
     * Rule condition defintion
     */
    private RuleConditionDefinition ruleConditionDefinition;

    /**
     * Constructor
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
     * @see org.alfresco.service.cmr.rule.RuleCondition#getRuleConditionDefinition()
     */
    public RuleConditionDefinition getRuleConditionDefinition()
    {
        return this.ruleConditionDefinition;
    }

}
