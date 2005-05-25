/**
 * Created on May 25, 2005
 */
package org.alfresco.repo.rule;

import java.util.List;

/**
 * Rule action interface.
 * 
 * @author Roy Wetherall
 */
public interface RuleAction
{
	/**
	 * Get the name of the rule action.
	 * <p>
	 * The name is unique and is used to identify the rule action.
	 * 
	 * @return	the name of the rule action
	 */
	public String getName();
	
	/**
	 * The title of the rule action.
	 * 
	 * @return	the title of the rule action
	 */
	public String getTitle();
	
	/**
	 * The description of the rule action.
	 * 
	 * @return	the description of the rule action
	 */
	public String getDescription();
	
	/**
	 * A list containing the parmameter defintions for this rule action.
	 * 
	 * @return	a list of parameter definitions
	 */
	public List<ParameterDefinition> getParameterDefinitions();
}
