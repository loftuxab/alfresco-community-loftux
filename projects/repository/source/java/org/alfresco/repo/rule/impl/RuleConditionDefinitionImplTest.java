/**
 * 
 */
package org.alfresco.repo.rule.impl;

import org.alfresco.repo.rule.RuleServiceException;


/**
 * @author Roy Wetherall
 */
public class RuleConditionDefinitionImplTest extends RuleItemDefinitionImplTest
{
    protected RuleItemDefinitionImpl create()
    {    
        // Test duplicate param name
        try
        {
            RuleConditionDefinitionImpl temp = new RuleConditionDefinitionImpl(
                    NAME,
                    TITLE,
                    DESCRIPTION,
                    duplicateParamDefs);
            fail("Duplicate param names are not allowed.");
        }
        catch (RuleServiceException exception)
        {
            // Indicates that there are duplicate param names
        }
        
        // Create a good one
        RuleConditionDefinitionImpl temp = new RuleConditionDefinitionImpl(
                NAME,
                TITLE,
                DESCRIPTION,
                paramDefs);
        assertNotNull(temp);
        return temp;
    }
}
