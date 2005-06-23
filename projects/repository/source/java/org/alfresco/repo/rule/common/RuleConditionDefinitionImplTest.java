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

import org.alfresco.service.cmr.rule.RuleServiceException;


/**
 * @author Roy Wetherall
 */
public class RuleConditionDefinitionImplTest extends RuleItemDefinitionImplTest
{
    /**
     * Constants used during tests
     */
    private static final String CONDITION_EVALUATOR = "conditionEvaluator";

    /**
     * @see org.alfresco.repo.rule.common.RuleItemDefinitionImplTest#create()
     */
    protected RuleItemDefinitionImpl create()
    {    
        // Test duplicate param name
        try
        {
            RuleConditionDefinitionImpl temp = new RuleConditionDefinitionImpl(NAME);
            temp.setParameterDefinitions(this.duplicateParamDefs);
            fail("Duplicate param names are not allowed.");
        }
        catch (RuleServiceException exception)
        {
            // Indicates that there are duplicate param names
        }
        
        // Create a good one
        RuleConditionDefinitionImpl temp = new RuleConditionDefinitionImpl(NAME);
        assertNotNull(temp);
        temp.setTitle(TITLE);
        temp.setDescription(DESCRIPTION);
        temp.setParameterDefinitions(this.paramDefs);
        temp.setConditionEvaluator(CONDITION_EVALUATOR);
        return temp;
    }
    
    /**
     * Test getConditionEvaluator
     */
    public void testGetConditionEvaluator()
    {
        RuleConditionDefinitionImpl cond = (RuleConditionDefinitionImpl)create();
        assertEquals(CONDITION_EVALUATOR, cond.getConditionEvaluator());
    }
}
