/**
 * Created on May 25, 2005
 */
package org.alfresco.service.cmr.rule;


/**
 * Rule type interface.
 * 
 * @author Roy Wetherall
 */
public interface RuleType
{
	/**
	 * Get the name of the rule type.
	 * <p>
	 * The name is unique and is used to identify the rule type.
	 * 
	 * @return	the name of the rule type
	 */
	public String getName();
	
	/**
	 * Get the display label of the rule type.
	 * 
	 * @return	the display label
	 */
	public String getDisplayLabel();
}