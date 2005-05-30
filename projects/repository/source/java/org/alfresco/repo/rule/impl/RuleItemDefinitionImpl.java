/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

import org.alfresco.repo.rule.ParameterDefinition;
import org.alfresco.repo.rule.RuleItemDefinition;
import org.alfresco.repo.rule.RuleServiceException;

/**
 * Rule item implementation class
 * 
 * @author Roy Wetherall
 */
public abstract class RuleItemDefinitionImpl implements RuleItemDefinition, Serializable
{
    /**
     * The name of the rule item
     */
    private String name;
    
    /**
     * The title of the rule item
     */
    private String title;
    
    /**
     * The description of the rule item
     */
    private String description;
    
    /**
     * The list of parameters associated with the rule item
     */
    private List<ParameterDefinition> parameterDefinitions;

    /**
     * Error messages
     */
    private static final String ERR_NAME_DUPLICATION = "The names " +
            "given to parameter definitions must be unique within the " +
            "scope of the rule item definition.";

    /**
     * Constructor
     * 
     * @param name                  the name 
     * @param title                 the title
     * @param description           the description
     * @param parameterDefinitions  the parameter definitions
     */
    public RuleItemDefinitionImpl(
            String name, 
            String title, 
            String description, 
            List<ParameterDefinition> parameterDefinitions)
    {
        this.name = name;
        this.title = title;
        this.description = description;
        
        if (hasDuplicateNames(parameterDefinitions) == false)
        {
            this.parameterDefinitions = parameterDefinitions;
        }
        else
        {
            throw new RuleServiceException(ERR_NAME_DUPLICATION);
        }
    }

    /**
     * Determines whether the list of parameter defintions contains duplicate
     * names of not.
     * 
     * @param parameterDefinitions  a list of parmeter definitions
     * @return                      true if there are name duplications, false
     *                              otherwise
     */
    private boolean hasDuplicateNames(List<ParameterDefinition> parameterDefinitions)
    {
        boolean result = false;
        if (parameterDefinitions != null)
        {
            HashSet<String> temp = new HashSet<String>(parameterDefinitions.size());
            for (ParameterDefinition definition : parameterDefinitions)
            {
                temp.add(definition.getName());
            }
            result = (parameterDefinitions.size() != temp.size());
        }
        return result;
    }

    /**
     * @see org.alfresco.repo.rule.RuleItemDefinition#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see org.alfresco.repo.rule.RuleItemDefinition#getTitle()
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * @see org.alfresco.repo.rule.RuleItemDefinition#getDescription()
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * @see org.alfresco.repo.rule.RuleItemDefinition#getParameterDefinitions()
     */
    public List<ParameterDefinition> getParameterDefinitions()
    {
        return this.parameterDefinitions;
    }
}
