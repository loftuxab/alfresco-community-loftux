package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author Roy Wetherall
 */
public interface RuleItem
{
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
	public void setParameterValues(
            Map<String, Serializable> parameterValues);
}
