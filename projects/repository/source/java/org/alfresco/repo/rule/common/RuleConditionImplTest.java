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

import org.alfresco.service.cmr.rule.RuleCondition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;

/**
 * @author Roy Wetherall
 */
public class RuleConditionImplTest extends BaseRuleItemImplTest
{
    private RuleConditionDefinitionImpl ruleConditionDefinition;    

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        // Create the rule condition definition
        this.ruleConditionDefinition = new RuleConditionDefinitionImpl(NAME);
        this.ruleConditionDefinition.setTitle(TITLE);
        this.ruleConditionDefinition.setDescription(DESCRIPTION);
        this.ruleConditionDefinition.setParameterDefinitions(this.paramDefs);                
    }
    
    /**
     * @see org.alfresco.repo.rule.common.RuleItemImplTest#create()
     */
    @Override
    protected RuleItemImpl create()
    {
        return new RuleConditionImpl(
                this.ruleConditionDefinition, 
                this.paramValues);
    }
    
    public void testGetRuleConditionDefintion()
    {
        RuleCondition temp = (RuleCondition)create();
        RuleConditionDefinition def = temp.getRuleConditionDefinition();
        assertNotNull(def);
        assertEquals(NAME, def.getName());        
    }
}
