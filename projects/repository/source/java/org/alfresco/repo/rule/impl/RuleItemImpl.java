package org.alfresco.repo.rule.impl;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.rule.RuleItem;

/**
 * Rule item instance implementation class.
 * 
 * @author Roy Wetherall
 */
public abstract class RuleItemImpl implements RuleItem, Serializable
{
    /**
     * The parameter values
     */
    private Map<String, Serializable> parameterValues;

    /**
     * Constructor
     * 
     * @param ruleItem  the rule item
     */
    public RuleItemImpl()
    {
        this(null);
    }
    
    /**
     * Constructor
     * 
     * @param ruleItem          the rule item
     * @param parameterValues   the parameter values
     */
    public RuleItemImpl(Map<String, Serializable> parameterValues)
    {        
        // TODO need to check that the parameter values being set correspond
        // correctly to the parameter definions on the rule item defintion
        this.parameterValues = parameterValues;
    }

    /**
     * @see org.alfresco.repo.rule.RuleItem#getParameterValues()
     */
    public Map<String, Serializable> getParameterValues()
    {
        return this.parameterValues;
    }

    /**
     * @see org.alfresco.repo.rule.RuleItem#setParameterValues(java.util.Map)
     */
    public void setParameterValues(Map<String, Serializable> parameterValues)
    {
        // TODO need to check that the parameter values being set correspond
        //      correctly to the parameter definions on the rule item defintion
        this.parameterValues = parameterValues;
    }
}
