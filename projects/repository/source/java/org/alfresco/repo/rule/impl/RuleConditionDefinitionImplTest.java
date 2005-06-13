/**
 * 
 */
package org.alfresco.repo.rule.impl;

import org.alfresco.service.cmr.rule.RuleServiceException;


/**
 * @author Roy Wetherall
 */
public class RuleConditionDefinitionImplTest extends RuleItemDefinitionImplTest
{
    /**
     * Constants used during tests
     */
    private static final String CONDITION_EVALUATOR = "conditionEvaluator";

    /**
     * @see org.alfresco.repo.rule.impl.RuleItemDefinitionImplTest#create()
     */
    protected RuleItemDefinitionImpl create()
    {    
        // Test duplicate param name
        try
        {
            RuleConditionDefinitionImpl temp = new RuleConditionDefinitionImpl(NAME);
            temp.setParameterDefinitions(this.duplicateParamDefs);
            fail("Duplicate param names are not allowed.");
        }
        catch (RuleServiceException exception)
        {
            // Indicates that there are duplicate param names
        }
        
        // Create a good one
        RuleConditionDefinitionImpl temp = new RuleConditionDefinitionImpl(NAME);
        assertNotNull(temp);
        temp.setTitle(TITLE);
        temp.setDescription(DESCRIPTION);
        temp.setParameterDefinitions(this.paramDefs);
        temp.setConditionEvaluator(CONDITION_EVALUATOR);
        return temp;
    }
    
    /**
     * Test getConditionEvaluator
     */
    public void testGetConditionEvaluator()
    {
        RuleConditionDefinitionImpl cond = (RuleConditionDefinitionImpl)create();
        assertEquals(CONDITION_EVALUATOR, cond.getConditionEvaluator());
    }
}
