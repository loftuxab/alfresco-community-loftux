/**
 * 
 */
package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.rule.impl.condition.MatchTextEvaluator;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleActionDefinition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleServiceException;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.debug.NodeStoreInspector;
import org.springframework.util.StopWatch;

/**
 * @author Roy Wetherall
 */
public class RuleServiceSystemTest extends BaseSpringTest
{
    private RuleService ruleService;
    private NodeService nodeService;
    private StoreRef testStoreRef;
    private NodeRef rootNodeRef;
    private NodeRef nodeRef;
    private NodeRef configFolder;
    
    /**
     * 
     */
    public RuleServiceSystemTest()
    {
        super();
    }
    
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        // Get the required services
        this.nodeService = (NodeService)this.applicationContext.getBean("nodeService");
        this.ruleService = (RuleService)this.applicationContext.getBean("ruleService");
        
        this.testStoreRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        this.rootNodeRef = this.nodeService.getRootNode(this.testStoreRef);
        
        // Create the node used for tests
        this.nodeRef = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
    }

    /**
     * Test:
     *          rule type:  inbound
     *          condition:  no-condition()
     *          action:     add-features(
     *                          aspect-name = versionable)
     */
    public void testAddFeaturesAction()
    {
        this.ruleService.makeActionable(this.nodeRef);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        RuleConditionDefinition cond = this.ruleService.getConditionDefintion("no-condition");
        RuleActionDefinition action = this.ruleService.getActionDefinition("add-features");
        
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put("aspect-name", DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE);        
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addRuleCondition(cond, null);
        rule.addRuleAction(action, params);
        
        this.ruleService.addRule(this.nodeRef, rule);
				
		NodeRef newNodeRef = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();        
        assertTrue(this.nodeService.hasAspect(newNodeRef, DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE));   
		
		System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
    }   
	
	/**
     * Test:
     *          rule type:  inbound
     *          condition:  match-text(
     *          				text = .doc,
     *          				operation = CONTAINS)
     *          action:     add-features(
     *                          aspect-name = versionable)
     */
	public void testContainsTextCondition()
	{
		this.ruleService.makeActionable(this.nodeRef);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        RuleConditionDefinition cond = this.ruleService.getConditionDefintion(MatchTextEvaluator.NAME);
        RuleActionDefinition action = this.ruleService.getActionDefinition("add-features");
        
        Map<String, Serializable> actionParams = new HashMap<String, Serializable>(1);
		actionParams.put("aspect-name", DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE);        
        
		Map<String, Serializable> condParams = new HashMap<String, Serializable>(1);
		condParams.put(MatchTextEvaluator.PARAM_TEXT, ".doc");
		
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addRuleCondition(cond, condParams);
        rule.addRuleAction(action, actionParams);
        
        this.ruleService.addRule(this.nodeRef, rule);
		
		// Test condition failure
		Map<QName, Serializable> props1 = new HashMap<QName, Serializable>();
		props1.put(DictionaryBootstrap.PROP_QNAME_NAME, "bobbins.txt");
		NodeRef newNodeRef = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER,
                props1).getChildRef();        
        assertFalse(this.nodeService.hasAspect(newNodeRef, DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE));  
		
		// Test condition success
		Map<QName, Serializable> props2 = new HashMap<QName, Serializable>();
		props2.put(DictionaryBootstrap.PROP_QNAME_NAME, "bobbins.doc");
		NodeRef newNodeRef2 = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER,
                props2).getChildRef();        
        assertTrue(this.nodeService.hasAspect(newNodeRef2, DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE)); 
		
		try
		{
			// Test name not set
			NodeRef newNodeRef3 = this.nodeService.createNode(
	                this.nodeRef,
	                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
	                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
	                DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();        
		}
		catch (RuleServiceException exception)
		{
			// Correct since text-match is a mandatory property
		}
	}
    
    public void testPerformanceOfRuleExecution()
    {
        StopWatch sw = new StopWatch();
        
        // Create actionable nodes
        sw.start("create nodes with no rule executed");
        for (int i = 0; i < 100; i++)
        {
            this.nodeService.createNode(
                    this.nodeRef,
                    DictionaryBootstrap.CHILD_ASSOC_QNAME_CONTAINS,
                    DictionaryBootstrap.CHILD_ASSOC_QNAME_CONTAINS,
                    DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef(); 
            assertFalse(this.nodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE));
        }
        sw.stop();
        
        this.ruleService.makeActionable(this.nodeRef);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        RuleConditionDefinition cond = this.ruleService.getConditionDefintion("no-condition");
        RuleActionDefinition action = this.ruleService.getActionDefinition("add-features");
        
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put("aspect-name", DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE);        
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addRuleCondition(cond, null);
        rule.addRuleAction(action, params);
        
        this.ruleService.addRule(this.nodeRef, rule);
        
        sw.start("create nodes with one rule run (apply versionable aspect)");
        for (int i = 0; i < 100; i++)
        {
            NodeRef nodeRef = this.nodeService.createNode(
                    this.nodeRef,
					QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
					QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                    DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
            assertTrue(this.nodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE));
        }
        sw.stop();
        
        System.out.println(sw.prettyPrint());
    }
}
