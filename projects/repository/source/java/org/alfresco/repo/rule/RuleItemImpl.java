package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.rule.RuleItem;

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
    private Map<String, Serializable> parameterValues = new HashMap<String, Serializable>();

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
        if (parameterValues != null)
		{
	        // TODO need to check that the parameter values being set correspond
	        // correctly to the parameter definions on the rule item defintion
	        this.parameterValues = parameterValues;
		}
    }

    /**
     * @see org.alfresco.service.cmr.rule.RuleItem#getParameterValues()
     */
    public Map<String, Serializable> getParameterValues()
    {
        Map<String, Serializable> result = this.parameterValues;
        if (result == null)
        {
            result = new HashMap<String, Serializable>();
        }
        return result;
    }
	
	/**
	 * @see org.alfresco.service.cmr.rule.RuleItem#getParameterValue(String)
	 */
	public Serializable getParameterValue(String name)
	{
		return this.parameterValues.get(name);
	}
	
	/**
     * @see org.alfresco.service.cmr.rule.RuleItem#setParameterValues(java.util.Map)
     */
    public void setParameterValues(Map<String, Serializable> parameterValues)
    {
		if (parameterValues != null)
		{
			// TODO need to check that the parameter values being set correspond
			//      correctly to the parameter definions on the rule item defintion
			this.parameterValues = parameterValues;
		}
    }
	
	/**
	 * @see org.alfresco.service.cmr.rule.RuleItem#setParameterValue(String, Serializable)
	 */
	public void setParameterValue(String name, Serializable value)
	{
		this.parameterValues.put(name, value);
	}
}
