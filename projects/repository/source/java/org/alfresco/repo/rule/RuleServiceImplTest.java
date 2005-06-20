/**
 * 
 */
package org.alfresco.repo.rule;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.rule.common.RuleTypeImpl;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleActionDefinition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;


/**
 * Rule service implementation test
 * 
 * @author Roy Wetherall
 */
public class RuleServiceImplTest extends RuleBaseTest
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
        
        List<AssociationRef> nodeAssocRefs = this.nodeService.getTargetAssocs(
                                               nodeRef, 
                                               ContentModel.ASSOC_CONFIGURATIONS);
        assertEquals(1, nodeAssocRefs.size());
		
		assertNotNull(this.nodeService.createNode(
							this.rootNodeRef,
							ContentModel.ASSOC_CHILDREN,
							QName.createQName(NamespaceService.ALFRESCO_URI, "systemconfiguration"),
							ContentModel.TYPE_SYTEM_FOLDER));
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
        Rule newRule = createTestRule("123");
        this.ruleService.addRule(this.nodeRef, newRule); 
        Rule newRule2 = createTestRule("456");
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
    }
}
