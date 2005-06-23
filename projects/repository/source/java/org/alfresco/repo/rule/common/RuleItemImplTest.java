/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.rule.common;

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
