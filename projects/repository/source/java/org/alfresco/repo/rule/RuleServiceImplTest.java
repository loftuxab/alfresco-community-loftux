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
package org.alfresco.repo.rule;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.rule.common.RuleTypeImpl;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleActionDefinition;
import org.alfresco.service.cmr.rule.RuleCondition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;
import org.alfresco.service.cmr.rule.RuleType;


/**
 * Rule service implementation test
 * 
 * @author Roy Wetherall
 */
public class RuleServiceImplTest extends BaseRuleTest
{    
   
    /**
     * Test get rule type
     */
    public void testGetRuleType()
    {
        List<RuleType> ruleTypes = this.ruleService.getRuleTypes();
        assertNotNull(ruleTypes);   
    }
    
    /**
     * Test getActionDefintions
     */
    public void testGetActionDefinitions()
    {
        List<RuleActionDefinition> actions = this.ruleService.getActionDefinitions();
        assertNotNull(actions);   	
    }
    
    /**
     * Test getConditionDefinitions
     */
    public void testGetConditionDefinitions()
    {
        List<RuleConditionDefinition> conds = this.ruleService.getConditionDefinitions();
        assertNotNull(conds);    
    }

    /**
     * Test makeActionable
     *
     */
    public void testMakeActionable()
    {
        this.ruleService.makeActionable(this.nodeRef);
        assertTrue(this.nodeService.hasAspect(this.nodeRef, ContentModel.ASPECT_ACTIONABLE));
        assertTrue(this.nodeService.hasAspect(this.nodeRef, ContentModel.ASPECT_CONFIGURABLE));
        List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(this.nodeRef, ContentModel.ASSOC_CONFIGURATIONS);
        assertNotNull(assocs);
        assertEquals(1, assocs.size());
    }
    
    /**
     * Test isActionable
     *
     */
    public void testIsActionable()
    {
        assertFalse(this.ruleService.isActionable(this.nodeRef));
        this.ruleService.makeActionable(this.nodeRef);
        assertTrue(this.ruleService.isActionable(this.nodeRef));
    }
    
    /**
     * Test createRule
     */
    public void testCreateRule()
    {
        RuleType ruleType = new RuleTypeImpl("ruleType1");
        Rule newRule = this.ruleService.createRule(ruleType);
        assertNotNull(newRule);
        assertNotNull(newRule.getId());
        assertEquals("ruleType1", newRule.getRuleType().getName());
    }
    
    /**
     * Test addRule
     *
     */
    public void testAddRule()
    {
        this.ruleService.makeActionable(this.nodeRef);
        Rule newRule = createTestRule("123");        
        this.ruleService.addRule(this.nodeRef, newRule);        
    }
    
    public void testRemoveAllRules()
    {
        this.ruleService.removeAllRules(this.nodeRef);
        List<Rule> rules1 = this.ruleService.getRules(this.nodeRef);
        assertNotNull(rules1);
        assertEquals(0, rules1.size());
        
        this.ruleService.makeActionable(this.nodeRef);
        Rule newRule = this.ruleService.createRule(ruleType);        
        this.ruleService.addRule(this.nodeRef, newRule); 
        Rule newRule2 = this.ruleService.createRule(ruleType);
        this.ruleService.addRule(this.nodeRef, newRule2); 
        
        List<Rule> rules2 = this.ruleService.getRules(this.nodeRef);
        assertNotNull(rules2);
        assertEquals(2, rules2.size());
        
        this.ruleService.removeAllRules(this.nodeRef);
        
        List<Rule> rules3 = this.ruleService.getRules(this.nodeRef);
        assertNotNull(rules3);
        assertEquals(0, rules3.size());
        
    }
    
    /**
     * Test get rules
     */
    public void testGetRules()
    {
        // Check that there are no rules associationed with the node
        List<Rule> noRules = this.ruleService.getRules(this.nodeRef);
        assertNotNull(noRules);
        assertEquals(0, noRules.size());
        
        // Check that we still get nothing back after the details of the node
        // have been cached in the rule store
        List<Rule> noRulesAfterCache = this.ruleService.getRules(this.nodeRef);
        assertNotNull(noRulesAfterCache);
        assertEquals(0, noRulesAfterCache.size());
        
        // Add a rule to the node
        testAddRule();
        
        // Get the rule from the rule service
        List<Rule> rules = this.ruleService.getRules(this.nodeRef);
        assertNotNull(rules);
        assertEquals(1, rules.size());
        
        // Check the details of the rule
        Rule rule = rules.get(0);
        assertEquals("title", rule.getTitle());
        assertEquals("description", rule.getDescription());
        assertNotNull(rule.getCreatedDate());
        assertNotNull(rule.getModifiedDate());
        
        // Check that the condition action have been retireved correctly
        List<RuleCondition> conditions = rule.getRuleConditions();
        assertNotNull(conditions);
        assertEquals(1, conditions.size());        
        List<RuleAction> actions = rule.getRuleActions();
        assertNotNull(actions);
        assertEquals(1, actions.size());
    }
    
    /**
     * Test disabling the rules
     */
    public void testRulesDisabled()
    {
        testAddRule();
        assertTrue(this.ruleService.rulesEnabled(this.nodeRef));
        this.ruleService.disableRules(this.nodeRef);
        assertFalse(this.ruleService.rulesEnabled(this.nodeRef));
        this.ruleService.enableRules(this.nodeRef);
        assertTrue(this.ruleService.rulesEnabled(this.nodeRef));
    }
}
