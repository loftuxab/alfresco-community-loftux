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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;

import junit.framework.TestCase;

/**
 * @author Roy Wetherall
 */
public abstract class RuleItemDefinitionImplTest extends TestCase
{
    protected static final String NAME = "name";
    protected static final String TITLE = "title";
    protected static final String DESCRIPTION = "description";    
    protected List<ParameterDefinition> paramDefs = new ArrayList<ParameterDefinition>();
    protected List<ParameterDefinition> duplicateParamDefs = new ArrayList<ParameterDefinition>();
    
    private static final String PARAM1_DISPLAYNAME = "param1-displayname";
    private static final String PARAM1_NAME = "param1-name";
    private static final ParameterType PARAM1_TYPE = ParameterType.STRING;
    private static final ParameterType PARAM2_TYPE = ParameterType.STRING;
    private static final String PARAM2_DISPLAYNAME = "param2-displaname";
    private static final String PARAM2_NAME = "param2-name";
    
    @Override
    protected void setUp() throws Exception
    {
        // Create param def lists
        this.paramDefs.add(new ParameterDefinitionImpl(PARAM1_NAME, PARAM1_TYPE, false, PARAM1_DISPLAYNAME));
        this.paramDefs.add(new ParameterDefinitionImpl(PARAM2_NAME, PARAM2_TYPE, false,  PARAM2_DISPLAYNAME));        
        this.duplicateParamDefs.add(new ParameterDefinitionImpl(PARAM1_NAME, PARAM1_TYPE, false,  PARAM1_DISPLAYNAME));
        this.duplicateParamDefs.add(new ParameterDefinitionImpl(PARAM1_NAME, PARAM1_TYPE, false,  PARAM1_DISPLAYNAME));
    }
    
    public void testConstructor()
    {
        create();
    }

    protected abstract RuleItemDefinitionImpl create();
    
    public void testGetName()
    {
        RuleItemDefinitionImpl temp = create();
        assertEquals(NAME, temp.getName());
    }
    
    public void testGetTitle()
    {
        RuleItemDefinitionImpl temp = create();
        assertEquals(TITLE, temp.getTitle());
    }
    
    public void testGetDescription()
    {
        RuleItemDefinitionImpl temp = create();
        assertEquals(DESCRIPTION, temp.getDescription());
    }
    
    public void testGetParameterDefintions()
    {
        RuleItemDefinitionImpl temp = create();
        List<ParameterDefinition> params = temp.getParameterDefinitions();
        assertNotNull(params);
        assertEquals(2, params.size());
        int i = 0;
        for (ParameterDefinition definition : params)
        {
            if (i == 0)
            {
                assertEquals(PARAM1_NAME, definition.getName());
                assertEquals(PARAM1_TYPE, definition.getType());
                assertEquals(PARAM1_DISPLAYNAME, definition.getDisplayLabel());
            }
            else
            {
                assertEquals(PARAM2_NAME, definition.getName());
                assertEquals(PARAM2_TYPE, definition.getType());
                assertEquals(PARAM2_DISPLAYNAME, definition.getDisplayLabel());
            }
            i++;
        }
    }
    
    public void testGetParameterDefinition()
    {
        RuleItemDefinitionImpl temp = create();
        ParameterDefinition definition = temp.getParameterDefintion(PARAM1_NAME);
        assertNotNull(definition);
        assertEquals(PARAM1_NAME, definition.getName());
        assertEquals(PARAM1_TYPE, definition.getType());
        assertEquals(PARAM1_DISPLAYNAME, definition.getDisplayLabel());
        
        ParameterDefinition nullDef = temp.getParameterDefintion("bobbins");
        assertNull(nullDef);
    }
}
