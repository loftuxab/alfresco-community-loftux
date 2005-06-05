/**
 * 
 */
package org.alfresco.repo.rule.impl;

import org.alfresco.repo.rule.RuleAction;
import org.alfresco.repo.rule.RuleActionDefinition;

/**
 * @author Roy Wetherall
 */
public class RuleActionImplTest extends RuleItemImplTest
{
    private RuleActionDefinitionImpl ruleActionDefinition;    

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        // Create the rule action definition
        this.ruleActionDefinition = new RuleActionDefinitionImpl(NAME);
        this.ruleActionDefinition.setTitle(TITLE);
        this.ruleActionDefinition.setDescription(DESCRIPTION);
        this.ruleActionDefinition.setParameterDefinitions(this.paramDefs);
    }
    
    /**
     * @see org.alfresco.repo.rule.impl.RuleItemImplTest#create()
     */
    @Override
    protected RuleItemImpl create()
    {
        return new RuleActionImpl(
                this.ruleActionDefinition, 
                this.paramValues);
    }
    
    public void testGetRuleActionDefintion()
    {
        RuleAction temp = (RuleAction)create();
        RuleActionDefinition def = temp.getRuleActionDefinition();
        assertNotNull(def);
        assertEquals(NAME, def.getName());        
    }
}
