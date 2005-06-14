/**
 * 
 */
package org.alfresco.repo.rule;

import org.alfresco.service.cmr.rule.RuleActionDefinition;

/**
 * Rule action implementation class
 * 
 * @author Roy Wetherall
 */
public class RuleActionDefinitionImpl extends RuleItemDefinitionImpl
                            implements RuleActionDefinition
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4048797883396863026L;    
    
    /**
     * The rule action executor
     */
    private String ruleActionExecutor;

    /**
     * Constructor
     * 
     * @param name  the name
     */
    public RuleActionDefinitionImpl(String name)
    {
        super(name);
    }
        
    /**
     * Set the rule action executor
     * 
     * @param ruleActionExecutor    the rule action executor
     */
    public void setRuleActionExecutor(String ruleActionExecutor)
    {
        this.ruleActionExecutor = ruleActionExecutor;
    }
    
    /**
     * Get the rule aciton executor
     * 
     * @return  the rule action executor
     */
    public String getRuleActionExecutor()
    {
        return ruleActionExecutor;
    }
}
