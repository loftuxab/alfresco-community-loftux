/**
 * Created on May 25, 2005
 */
package org.alfresco.repo.rule;

/**
 * Parameter definition interface.
 * 
 * @author Roy Wetherall
 */
public interface ParameterDefinition 
{
	/**
	 * Get the name of the parameter.
	 * <p>
	 * This is unique and is used to identify the parameter.
	 * 
	 * @return	the parameter name
	 */
	public String getName();
	
	/**
	 * Get the type of parameter
	 * 
	 * @return	the parameter type
	 */
	public ParameterType getType();
	
	/**
	 * Get the display label of the parameter.
	 * 
	 * @return	the parameter display label
	 */
	public String getDisplayLabel();
	
}
