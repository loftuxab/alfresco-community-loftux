package org.alfresco.repo.rule;

import java.util.List;

public interface RuleItemDefinition 
{
	/**
	 * Get the name of the rule item.
	 * <p>
	 * The name is unique and is used to identify the rule item.
	 * 
	 * @return	the name of the rule action
	 */
	public String getName();
	
	/**
	 * The title of the rule item.
	 * 
	 * @return	the title of the rule item
	 */
	public String getTitle();
	
	/**
	 * The description of the rule item.
	 * 
	 * @return	the description of the rule item
	 */
	public String getDescription();
	
	/**
	 * A list containing the parmameter defintions for this rule item.
	 * 
	 * @return	a list of parameter definitions
	 */
	public List<ParameterDefinition> getParameterDefinitions();
}
