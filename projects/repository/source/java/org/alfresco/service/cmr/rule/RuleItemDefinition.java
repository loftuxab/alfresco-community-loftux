package org.alfresco.service.cmr.rule;

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
    
    /**
     * Get the parameter definition by name
     * 
     * @param name  the name of the parameter
     * @return      the parameter definition, null if none found
     */
    public ParameterDefinition getParameterDefintion(String name);
}
