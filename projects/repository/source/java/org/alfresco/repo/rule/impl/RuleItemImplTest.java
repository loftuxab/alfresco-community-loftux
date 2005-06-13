/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;

import junit.framework.TestCase;

/**
 * @author Roy Wetherall
 */
public abstract class RuleItemImplTest extends TestCase
{
    protected List<ParameterDefinition> paramDefs = new ArrayList<ParameterDefinition>();
    protected Map<String, Serializable> paramValues = new HashMap<String, Serializable>();    
    
    protected static final String NAME = "name";
    protected static final String TITLE = "title";
    protected static final String DESCRIPTION = "description";
    
    private static final String PARAM_1 = "param1";
    private static final String VALUE_1 = "value1";
    private static final String PARAM_2 = "param2";
    private static final String VALUE_2 = "value2";    
    private static final String PARAM_DISPLAYLABEL = "displayLabel";
   
    @Override
    protected void setUp() throws Exception
    {
        // Create param defs
        paramDefs.add(new ParameterDefinitionImpl(PARAM_1, ParameterType.STRING, false,  PARAM_DISPLAYLABEL));
        paramDefs.add(new ParameterDefinitionImpl(PARAM_2, ParameterType.STRING, false,  PARAM_DISPLAYLABEL));
        
        // Create param values
        paramValues.put(PARAM_1, VALUE_1);
        paramValues.put(PARAM_2, VALUE_2);
    }
    
    public void testConstructor()
    {
        create();
    }

    protected abstract RuleItemImpl create();
    
    public void testGetParameterValues()
    {
        RuleItemImpl temp = create();
        Map<String, Serializable> tempParamValues = temp.getParameterValues();
        assertNotNull(tempParamValues);
        assertEquals(2, tempParamValues.size());
        for (Map.Entry entry : tempParamValues.entrySet())
        {
            if (entry.getKey() == PARAM_1)
            {
                assertEquals(VALUE_1, entry.getValue());
            }
            else if (entry.getKey() == PARAM_2)
            {
                assertEquals(VALUE_2, entry.getValue());
            }
            else
            {
                fail("There is an unexpected entry here.");            
            }
        }
    }
	
	public void testGetParameterValue()
	{
		RuleItemImpl temp = create();
		assertNull(temp.getParameterValue("bobbins"));
		assertEquals(VALUE_1, temp.getParameterValue(PARAM_1));
	}
	
	public void testSetParameterValue()
	{
		RuleItemImpl temp = create();
		temp.setParameterValue("bobbins", "value");
		assertEquals("value", temp.getParameterValue("bobbins"));
	}
}
