/**
 * 
 */
package org.alfresco.repo.rule.impl;

import org.alfresco.repo.rule.RuleType;

/**
 * Rule type implementation class.
 * 
 * @author Roy Wetherall
 */
public class RuleTypeImpl implements RuleType
{
    /**
     * The name of the rule type
     */
    private String name;
    
    /**
     * The display label
     */
    private String displayLabel;
    
    /**
     * The ruleType adapter
     */
    private String ruleTypeAdapter;
    
    /**
     * 
     */
    public RuleTypeImpl(String name)
    {
        this.name = name;
    }

    /**
     * @see org.alfresco.repo.rule.RuleType#getName()
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Set the display label
     * 
     * @param displayLabel  the displaylabel
     */
    public void setDisplayLabel(String displayLabel)
    {
        this.displayLabel = displayLabel;
    }

    /**
     * @see org.alfresco.repo.rule.RuleType#getDisplayLabel()
     */
    public String getDisplayLabel()
    {
        return this.displayLabel;
    }
    
    /**
     * Sets the rule type adapter
     * 
     * @param ruleTypeAdapter  the rule type adapter
     */
    public void setRuleTypeAdapter(String ruleTypeAdapter)
    {
        this.ruleTypeAdapter = ruleTypeAdapter;
    }
    
    /**
     * Gets the rule type adapter
     * 
     * @return  the rule type adapter
     */
    public String getRuleTypeAdapter()
    {
        return ruleTypeAdapter;
    }
}
