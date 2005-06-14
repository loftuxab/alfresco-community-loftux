/**
 * 
 */
package org.alfresco.repo.rule;

import org.alfresco.service.cmr.rule.RuleServiceException;


/**
 * @author Roy Wetherall
 */
public class RuleActionDefinitionImplTest extends RuleItemDefinitionImplTest
{
    private static final String RULE_ACTION_EXECUTOR = "ruleActionExector";
    
    protected RuleItemDefinitionImpl create()
    {    
        // Test duplicate param name
        try
        {
            RuleActionDefinitionImpl temp = new RuleActionDefinitionImpl(NAME);
            temp.setParameterDefinitions(duplicateParamDefs);
            fail("Duplicate param names are not allowed.");
        }
        catch (RuleServiceException exception)
        {
            // Indicates that there are duplicate param names
        }
        
        // Create a good one
        RuleActionDefinitionImpl temp = new RuleActionDefinitionImpl(NAME);
        assertNotNull(temp);
        temp.setTitle(TITLE);
        temp.setDescription(DESCRIPTION);
        temp.setParameterDefinitions(paramDefs);
        temp.setRuleActionExecutor(RULE_ACTION_EXECUTOR);
        return temp;
    }
    
    /**
     * Test getRuleActionExecutor
     */
    public void testGetRuleActionExecutor()
    {
        RuleActionDefinitionImpl temp = (RuleActionDefinitionImpl)create();
        assertEquals(RULE_ACTION_EXECUTOR, temp.getRuleActionExecutor());
    }
}
