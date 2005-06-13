/**
 * Created on May 25, 2005
 */
package org.alfresco.service.cmr.rule;

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
	 * Indicates whether the parameter is mandatory or not.
	 * <p>
	 * If a parameter is mandatory it means that the value can not be null.
	 * 
	 * @return	true if the parameter is mandatory, false otherwise
	 */
	public boolean isMandatory();
	
	/**
	 * Get the display label of the parameter.
	 * 
	 * @return	the parameter display label
	 */
	public String getDisplayLabel();
	
}
