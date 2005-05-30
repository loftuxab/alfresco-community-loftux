/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.util.List;

import org.alfresco.repo.rule.ParameterDefinition;
import org.alfresco.repo.rule.RuleConditionDefinition;

/**
 * Rule condition implementation class.
 * 
 * @author Roy Wetherall
 */
public class RuleConditionDefinitionImpl extends RuleItemDefinitionImpl 
                               implements RuleConditionDefinition
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3688505493618177331L;

    /**
     * Constructor
     * 
     * @param name                  the name
     * @param title                 the title
     * @param description           the description
     * @param parameterDefinitions  the parameter definitions
     */
    public RuleConditionDefinitionImpl(
            String name, 
            String title, 
            String description, 
            List<ParameterDefinition> parameterDefinitions)
    {
        super(name, title, description, parameterDefinitions);
    }


}
