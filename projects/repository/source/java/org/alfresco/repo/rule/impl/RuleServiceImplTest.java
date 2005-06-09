/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.util.List;

import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.rule.ParameterDefinition;
import org.alfresco.repo.rule.Rule;
import org.alfresco.repo.rule.RuleActionDefinition;
import org.alfresco.repo.rule.RuleConditionDefinition;
import org.alfresco.repo.rule.RuleService;
import org.alfresco.repo.rule.RuleType;

/**
 * @author Roy Wetherall
 */
public class RuleServiceImplTest extends RuleBaseTest
{
    /**
     * Rule service
     */
    private RuleService ruleService;
    
    /**
     * Dictionary service
     */
    private DictionaryService dictionaryService;
    
    private PolicyComponent policyComponent;
    
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();      
        
        this.dictionaryService = (DictionaryService)this.applicationContext.getBean("dictionaryService");
        this.policyComponent = (PolicyComponent)this.applicationContext.getBean("policyComponent");
        
        this.ruleService = new RuleServiceImpl();
        ((RuleServiceImpl)this.ruleService).setConfigService(this.configService);
        ((RuleServiceImpl)this.ruleService).setNodeService(this.nodeService);
        ((RuleServiceImpl)this.ruleService).setContentService(this.contentService);
        ((RuleServiceImpl)this.ruleService).setDictionaryService(this.dictionaryService);
        ((RuleServiceImpl)this.ruleService).setPolicyComponent(this.policyComponent);
        ((RuleServiceImpl)this.ruleService).init();
    }
    
    /**
     * Test get rule type
     */
    public void testGetRuleType()
    {
        List<RuleType> ruleTypes = this.ruleService.getRuleTypes();
        assertNotNull(ruleTypes);
        assertEquals(2, ruleTypes.size());
        
        for (RuleType ruleType : ruleTypes)
        {
            assertEquals("displayLabel", ruleType.getDisplayLabel());
        }        
    }
    
    /**
     * Test getActionDefintions
     */
    public void testGetActionDefinitions()
    {
        List<RuleActionDefinition> actions = this.ruleService.getActionDefinitions();
        assertNotNull(actions);
        assertEquals(2, actions.size());
        
        for (RuleActionDefinition action : actions)
        {
            assertEquals("title", action.getTitle());
            assertEquals("description", action.getDescription());
            List<ParameterDefinition> params = action.getParameterDefinitions();
            assertNotNull(params);
            assertEquals(2, params.size());
        }        
    }
    
    /**
     * Test getConditionDefinitions
     */
    public void testGetConditionDefinitions()
    {
        List<RuleConditionDefinition> conds = this.ruleService.getConditionDefinitions();
        assertNotNull(conds);
        assertEquals(2, conds.size());
        
        for (RuleConditionDefinition cond : conds)
        {
            assertEquals("title", cond.getTitle());
            assertEquals("description", cond.getDescription());
            List<ParameterDefinition> params = cond.getParameterDefinitions();
            assertNotNull(params);
            assertEquals(1, params.size());
        }
        
    }

    /**
     * Test makeActionable
     *
     */
    public void testMakeActionable()
    {
        this.ruleService.makeActionable(this.nodeRef);
        assertTrue(this.nodeService.hasAspect(this.nodeRef, DictionaryBootstrap.ASPECT_QNAME_ACTIONABLE));
        
        List<NodeAssocRef> nodeAssocRefs = this.nodeService.getTargetAssocs(
                                               nodeRef, 
                                               DictionaryBootstrap.ASSOC_QNAME_CONFIGURATIONS);
        assertEquals(1, nodeAssocRefs.size());
		
		assertNotNull(this.nodeService.createNode(
							this.rootNodeRef,
							DictionaryBootstrap.ASSOC_QNAME_CHILDREN,
							QName.createQName(NamespaceService.ALFRESCO_URI, "systemconfiguration"),
							DictionaryBootstrap.TYPE_QNAME_SYTEM_FOLDER));
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
        RuleType ruleType = new RuleTypeImpl("testRuleType");
        Rule newRule = this.ruleService.createRule(ruleType);
        assertNotNull(newRule);
        assertNotNull(newRule.getId());
        assertEquals("testRuleType", newRule.getRuleType().getName());
    }
}
