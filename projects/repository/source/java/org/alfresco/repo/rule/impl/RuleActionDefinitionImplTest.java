/**
 * 
 */
package org.alfresco.repo.rule.impl;

import org.alfresco.repo.rule.RuleServiceException;


/**
 * @author Roy Wetherall
 */
public class RuleActionDefinitionImplTest extends RuleItemDefinitionImplTest
{
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
        return temp;
    }
}
