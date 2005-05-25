package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.Map;

public interface RuleItemInstance <T> 
{
	/**
	 * Get the rule item that relates to this rule item instance
	 * 
	 * @return	the rule item
	 */
	public T getRuleItem();
	
	/**
	 * Get the parameter values
	 * 
	 * @return	get the parameter values
	 */
	public Map<String, Serializable> getParameterValues();
	
	/**
	 * Sets the parameter values
	 * 
	 * @param parameterValues	the parameter values
	 */
	public void setParameterValues(Map<String, Serializable> parameterValues);
}
