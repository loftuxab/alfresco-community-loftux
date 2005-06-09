/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.io.Serializable;

import org.alfresco.repo.rule.ParameterDefinition;
import org.alfresco.repo.rule.ParameterType;

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
    private ParameterType type;
    
    /**
     * The display label
     */
    private String displayLabel;
	
	/**
	 * Indicates whether it is mandatory for the parameter to be set
	 */
	private boolean isMandatory = false;

    /**
     * Constructor
     * 
     * @param name          the name of the parameter
     * @param type          the type of the parameter
     * @param displayLabel  the display label
     */
    public ParameterDefinitionImpl(
            String name, 
            ParameterType type,
            boolean isMandatory,
            String displayLabel)
    {
        this.name = name;
        this.type = type;
        this.displayLabel = displayLabel;
		this.isMandatory = isMandatory;
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
    public ParameterType getType()
    {
        return this.type;
    }
	
	/**
	 * @see org.alfresco.repo.rule.ParameterDefinition#isMandatory()
	 */
	public boolean isMandatory() 
	{
		return this.isMandatory;
	}

    /**
     * @see org.alfresco.repo.rule.ParameterDefinition#getDisplayLabel()
     */
    public String getDisplayLabel()
    {
        return this.displayLabel;
    }
}
