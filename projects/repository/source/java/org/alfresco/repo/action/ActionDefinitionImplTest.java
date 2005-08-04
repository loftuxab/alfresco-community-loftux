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
package org.alfresco.repo.action;

import org.alfresco.service.cmr.rule.RuleServiceException;


/**
 * @author Roy Wetherall
 */
public class ActionDefinitionImplTest extends BaseParameterizedItemDefinitionImplTest
{
    private static final String RULE_ACTION_EXECUTOR = "ruleActionExector";
    
    protected ParameterizedItemDefinitionImpl create()
    {    
        // Test duplicate param name
        try
        {
            ActionDefinitionImpl temp = new ActionDefinitionImpl(NAME);
            temp.setParameterDefinitions(duplicateParamDefs);
            fail("Duplicate param names are not allowed.");
        }
        catch (RuleServiceException exception)
        {
            // Indicates that there are duplicate param names
        }
        
        // Create a good one
        ActionDefinitionImpl temp = new ActionDefinitionImpl(NAME);
        assertNotNull(temp);
        temp.setTitle(TITLE);
        temp.setDescription(DESCRIPTION);
        temp.setParameterDefinitions(paramDefs);
        temp.setRuleActionExecutor(RULE_ACTION_EXECUTOR);
        return temp;
    }
    
    /**
     * Test getRuleActionExecutor
     */
    public void testGetRuleActionExecutor()
    {
        ActionDefinitionImpl temp = (ActionDefinitionImpl)create();
        assertEquals(RULE_ACTION_EXECUTOR, temp.getRuleActionExecutor());
    }
}
