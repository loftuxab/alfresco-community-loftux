/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleActionDefinition;

/**
 * @author Roy Wetherall
 */
public class RuleActionImpl extends RuleItemImpl implements Serializable,
        RuleAction
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3258135760426186548L;
    
    /**
     * Rule action definition
     */
    private RuleActionDefinition ruleActionDefinition;

    /**
     * Constructor
     * 
     * @param ruleActionDefinition  the rule action definition
     */
    public RuleActionImpl(RuleActionDefinition ruleActionDefinition)
    {
        this(ruleActionDefinition, null);
    }

    /**
     * Constructor 
     * 
     * @param ruleActionDefinition  the rule action definition
     * @param parameterValues       the parameter values
     */
    public RuleActionImpl(
            RuleActionDefinition ruleActionDefinition, 
            Map<String, Serializable> parameterValues)
    {
        super(parameterValues);
        this.ruleActionDefinition = ruleActionDefinition;
    }

    /**
     * @see org.alfresco.service.cmr.rule.RuleAction#getRuleActionDefinition()
     */
    public RuleActionDefinition getRuleActionDefinition()
    {
        return this.ruleActionDefinition;
    }
}
