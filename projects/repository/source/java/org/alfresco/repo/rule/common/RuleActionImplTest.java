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

import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleActionDefinition;

/**
 * @author Roy Wetherall
 */
public class RuleActionImplTest extends RuleItemImplTest
{
    private RuleActionDefinitionImpl ruleActionDefinition;    

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        // Create the rule action definition
        this.ruleActionDefinition = new RuleActionDefinitionImpl(NAME);
        this.ruleActionDefinition.setTitle(TITLE);
        this.ruleActionDefinition.setDescription(DESCRIPTION);
        this.ruleActionDefinition.setParameterDefinitions(this.paramDefs);
    }
    
    /**
     * @see org.alfresco.repo.rule.common.RuleItemImplTest#create()
     */
    @Override
    protected RuleItemImpl create()
    {
        return new RuleActionImpl(
                this.ruleActionDefinition, 
                this.paramValues);
    }
    
    public void testGetRuleActionDefintion()
    {
        RuleAction temp = (RuleAction)create();
        RuleActionDefinition def = temp.getRuleActionDefinition();
        assertNotNull(def);
        assertEquals(NAME, def.getName());        
    }
}
