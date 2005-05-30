/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.io.Serializable;

import org.alfresco.repo.rule.ParameterDefinition;

/**
 * Parameter definition implementation class.
 * 
 * @author Roy Wetherall
 */
public class ParameterDefinitionImpl implements ParameterDefinition, Serializable
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3976741384558751799L;

    /**
     * The name of the parameter
     */
    private String name;
    
    /**
     * The type of the parameter
     */
    private Class type;
    
    /**
     * The display label
     */
    private String displayLabel;

    /**
     * Constructor
     * 
     * @param name          the name of the parameter
     * @param type          the type of the parameter
     * @param displayLabel  the display label
     */
    public ParameterDefinitionImpl(
            String name, 
            Class type, 
            String displayLabel)
    {
        this.name = name;
        this.type = type;
        this.displayLabel = displayLabel;
    }
    
    /**
     * @see org.alfresco.repo.rule.ParameterDefinition#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see org.alfresco.repo.rule.ParameterDefinition#getType()
     */
    public Class getType()
    {
        return this.type;
    }

    /**
     * @see org.alfresco.repo.rule.ParameterDefinition#getDisplayLabel()
     */
    public String getDisplayLabel()
    {
        return this.displayLabel;
    }

}
