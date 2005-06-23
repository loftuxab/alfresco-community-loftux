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
