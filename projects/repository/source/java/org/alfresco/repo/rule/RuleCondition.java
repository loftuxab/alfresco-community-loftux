/**
 * Created on May 25, 2005
 */
package org.alfresco.repo.rule;

import java.util.List;

/**
 * Rule condition interface
 * 
 * @author Roy Wetherall
 */
public interface RuleCondition
{
	/**
	 * Get the name of the rule condition.
	 * <p>
	 * The name is unique and is used to identify the rule condition.
	 * 
	 * @return	the name
	 */
	public String getName();
	
	/**
	 * Get the title of the rule condition.
	 * 
	 * @return	the title
	 */
	public String getTitle();
	
	/**
	 * The description of the rule condition.
	 * 
	 * @return	the description
	 */
	public String getDescription();
	
	/**
	 * A list containing the parmameter defintions for this rule condition.
	 * 
	 * @return	a list of parameter definitions
	 */
	public List<ParameterDefinition> getParameters();
}
