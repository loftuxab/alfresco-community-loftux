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
        return temp;
    }
}
