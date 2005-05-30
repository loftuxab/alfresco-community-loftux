/**
 * 
 */
package org.alfresco.repo.rule.impl;

import org.alfresco.repo.rule.RuleCondition;
import org.alfresco.repo.rule.RuleConditionDefinition;

/**
 * @author Roy Wetherall
 */
public class RuleConditionImplTest extends RuleItemImplTest
{
    private RuleConditionDefinition ruleConditionDefinition;    

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        // Create the rule condition definition
        this.ruleConditionDefinition = new RuleConditionDefinitionImpl(
                NAME,
                TITLE,
                DESCRIPTION,
                this.paramDefs);
    }
    
    /**
     * @see org.alfresco.repo.rule.impl.RuleItemImplTest#create()
     */
    @Override
    protected RuleItemImpl create()
    {
        return new RuleConditionImpl(
                this.ruleConditionDefinition, 
                this.paramValues);
    }
    
    public void testGetRuleConditionDefintion()
    {
        RuleCondition temp = (RuleCondition)create();
        RuleConditionDefinition def = temp.getRuleConditionDefinition();
        assertNotNull(def);
        assertEquals(NAME, def.getName());        
    }
}
