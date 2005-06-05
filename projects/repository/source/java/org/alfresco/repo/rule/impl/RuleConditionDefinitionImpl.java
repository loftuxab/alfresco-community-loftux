/**
 * 
 */
package org.alfresco.repo.rule.impl;

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
     */
    public RuleConditionDefinitionImpl(String name)
    {
        super(name);
    }


}
