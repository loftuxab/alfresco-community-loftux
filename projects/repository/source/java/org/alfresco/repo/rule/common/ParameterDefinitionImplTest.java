/**
 * 
 */
package org.alfresco.repo.rule.common;

import org.alfresco.service.cmr.rule.ParameterType;

import junit.framework.TestCase;

/**
 * Parameter definition implementation unit test.
 * 
 * @author Roy Wetherall
 */
public class ParameterDefinitionImplTest extends TestCase
{
    private static final String NAME = "param-name";
    private static final String DISPLAY_LABEL = "The display label.";
    
    public void testConstructor()
    {
        create();
    }
   
    private ParameterDefinitionImpl create()
    {
        ParameterDefinitionImpl paramDef = new ParameterDefinitionImpl(
                NAME,
                ParameterType.STRING,
                true,
                DISPLAY_LABEL);
        assertNotNull(paramDef);
        return paramDef;
    }
    
    public void testGetName()
    {
        ParameterDefinitionImpl temp = create();
        assertEquals(NAME, temp.getName());
    }
    
    public void testGetClass()
    {
        ParameterDefinitionImpl temp = create();
        assertEquals(ParameterType.STRING, temp.getType());
    }
	
	public void testIsMandatory()
	{
		ParameterDefinitionImpl temp = create();
		assertTrue(temp.isMandatory());
	}
    
    public void testGetDisplayLabel()
    {
        ParameterDefinitionImpl temp = create();
        assertEquals(DISPLAY_LABEL, temp.getDisplayLabel());    
    }
}
