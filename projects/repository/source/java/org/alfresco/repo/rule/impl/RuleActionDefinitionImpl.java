/**
 * 
 */
package org.alfresco.repo.rule.impl;

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
     * @param name  the name
     */
    public RuleActionDefinitionImpl(String name)
    {
        super(name);
    }
}
