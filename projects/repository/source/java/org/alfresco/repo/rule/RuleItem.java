package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.Map;

/**
 * Rule item interface
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
	 * Get value of a named parameter.
	 * 
	 * @param name	the parameter name
	 * @return		the value of the parameter
	 */
	public Serializable getParameterValue(String name);
	
	/**
	 * Sets the parameter values
	 * 
	 * @param parameterValues	the parameter values
	 */
	public void setParameterValues(
            Map<String, Serializable> parameterValues);
	
	/**
	 * Sets the value of a parameter.
	 * 
	 * @param name		the parameter name
	 * @param value		the parameter value
	 */
	public void setParameterValue(String name, Serializable value);
}
