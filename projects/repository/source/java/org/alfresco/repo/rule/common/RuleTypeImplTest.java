/**
 * 
 */
package org.alfresco.repo.rule.common;

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
    private static final String RULE_TYPE_ADAPTER = "org.alfresco.repo.rule.impl.TestAdapter";
    
    public void testConstructor()
    {
        create();
    }
   
    private RuleTypeImpl create()
    {
        RuleTypeImpl temp = new RuleTypeImpl(
                NAME);
        assertNotNull(temp);
        temp.setDisplayLabel(DISPLAY_LABEL);
        temp.setRuleTypeAdapter(RULE_TYPE_ADAPTER);
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
    
    public void testGetRuleTypeAdapter()
    {
        RuleTypeImpl temp = create();
        assertEquals(RULE_TYPE_ADAPTER, temp.getRuleTypeAdapter());   
    }
}
