/**
 * 
 */
package org.alfresco.repo.rule.impl;

import junit.framework.TestCase;

/**
 * Parameter definition implementation unit test.
 * 
 * @author Roy Wetherall
 */
public class RuleTypeImplTest extends TestCase
{
    private static final String NAME = "name";
    private static final String DISPLAY_LABEL = "The display label.";
    
    public void testConstructor()
    {
        create();
    }
   
    private RuleTypeImpl create()
    {
        RuleTypeImpl temp = new RuleTypeImpl(
                NAME,
                DISPLAY_LABEL);
        assertNotNull(temp);
        return temp;
    }
    
    public void testGetName()
    {
        RuleTypeImpl temp = create();
        assertEquals(NAME, temp.getName());
    }
    
    public void testGetDisplayLabel()
    {
        RuleTypeImpl temp = create();
        assertEquals(DISPLAY_LABEL, temp.getDisplayLabel());    
    }
}
