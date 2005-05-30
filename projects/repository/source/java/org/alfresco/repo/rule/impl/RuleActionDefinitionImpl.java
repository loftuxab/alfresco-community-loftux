/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.util.List;

import org.alfresco.repo.rule.ParameterDefinition;
import org.alfresco.repo.rule.RuleActionDefinition;

/**
 * Rule action implementation class
 * 
 * @author Roy Wetherall
 */
public class RuleActionDefinitionImpl extends RuleItemDefinitionImpl
                            implements RuleActionDefinition
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4048797883396863026L;

    /**
     * Constructor
     * 
     * @param name                  the name
     * @param title                 the title
     * @param description           the description
     * @param parameterDefinitions  the parameterDefinitions
     */
    public RuleActionDefinitionImpl(
            String name, 
            String title, 
            String description, 
            List<ParameterDefinition> parameterDefinitions)
    {
        super(name, title, description, parameterDefinitions);
    }

}
