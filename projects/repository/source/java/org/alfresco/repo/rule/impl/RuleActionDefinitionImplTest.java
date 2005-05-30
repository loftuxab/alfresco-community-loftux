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
            RuleActionDefinitionImpl temp = new RuleActionDefinitionImpl(
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
        RuleActionDefinitionImpl temp = new RuleActionDefinitionImpl(
                NAME,
                TITLE,
                DESCRIPTION,
                paramDefs);
        assertNotNull(temp);
        return temp;
    }
}
