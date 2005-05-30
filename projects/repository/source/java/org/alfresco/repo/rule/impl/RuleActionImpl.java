/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.rule.RuleAction;
import org.alfresco.repo.rule.RuleActionDefinition;

/**
 * @author Roy Wetherall
 */
public class RuleActionImpl extends RuleItemImpl implements Serializable,
        RuleAction
{
    /**
     * 
     */
    private static final long serialVersionUID = 3258135760426186548L;
    
    /**
     * 
     */
    private RuleActionDefinition ruleActionDefinition;

    /**
     * 
     */
    public RuleActionImpl(RuleActionDefinition ruleActionDefinition)
    {
        this(ruleActionDefinition, null);
    }

    /**
     * @param parameterValues
     */
    public RuleActionImpl(
            RuleActionDefinition ruleActionDefinition, 
            Map<String, Serializable> parameterValues)
    {
        super(parameterValues);
        this.ruleActionDefinition = ruleActionDefinition;
    }

    /**
     * @see org.alfresco.repo.rule.RuleAction#getRuleActionDefinition()
     */
    public RuleActionDefinition getRuleActionDefinition()
    {
        return this.ruleActionDefinition;
    }

}
