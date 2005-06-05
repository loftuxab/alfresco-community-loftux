/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.util.List;

import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;

/**
 * @author Roy Wetherall
 */
public class RuleStoreTest extends RuleBaseTest
{
    private static final String RULE_ID = "1";
    
    /**
     * Services used during tests
     */
    private NodeService nodeService;
    private ContentService contentService;
    
    /**
     * Rule store
     */
    private RuleStore ruleStore;
    
    /**
     * Items used during tests
     */
    private StoreRef storeRef;
    private NodeRef rootNodeRef;
    private NodeRef nodeRef;
    
    /**
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        
        // Set the services
        this.nodeService = (NodeService)this.applicationContext.getBean("indexingNodeService");
        this.contentService = (ContentService)this.applicationContext.getBean("contentService");
        
        // Create the rule store
        this.ruleStore = new RuleStore(
                this.nodeService, 
                this.contentService,
                new RuleConfig(this.configService));
        
        // Create the store and get the root node reference
        this.storeRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        this.rootNodeRef = this.nodeService.getRootNode(this.storeRef);
        
        // Create the node used for tests
        this.nodeRef = this.nodeService.createNode(
                rootNodeRef,
                null,
                QName.createQName("{test}contains"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        
        // TODO this should be made actionable in the correct way !!!
        // Create the config folder
        NodeRef configFolder = this.nodeService.createNode(
                rootNodeRef,
                null,
                QName.createQName("{test}contains"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        this.nodeService.createAssociation(this.nodeRef, configFolder, RuleStore.ASSOC_QNAME_CONFIGURATIONS);
    }
    
    public void testGet()
    {
        testPut();
        
        List<RuleImpl> rules = this.ruleStore.get(this.nodeRef);
        assertNotNull(rules);
        assertEquals(1, rules.size());
        
        RuleImpl rule = rules.get(0);
        checkRule(rule, RULE_ID);
    }
    
    public void testPut()
    {
        RuleImpl newRule = createTestRule(RULE_ID);
        this.ruleStore.put(this.nodeRef, newRule);
        
        NodeRef ruleContent = newRule.getRuleContentNodeRef();
        assertNotNull(ruleContent);
        
        ContentReader contentReader = this.contentService.getReader(ruleContent);
        assertNotNull(contentReader);
        String ruleXML = contentReader.getContentString();
        assertNotNull(ruleXML);
    }

//    private RuleImpl newRule()
//    {
//        // Rule properties
//        Map<String, Serializable> conditionProps = new HashMap<String, Serializable>();
//        conditionProps.put("prop1", "value1");
//        Map<String, Serializable> actionProps = new HashMap<String, Serializable>();
//        actionProps.put("prop1", "value1");
//        
//        // Create a new rule
//        RuleImpl newRule = new RuleImpl("1", new RuleTypeImpl("ruleType1"));
//        newRule.setTitle("The title");
//        newRule.setDescription("The description");
//        newRule.addRuleCondition(
//                new RuleConditionDefinitionImpl("condition1"), 
//                conditionProps);
//        newRule.addRuleAction(
//                new RuleActionDefinitionImpl("action1"), 
//                actionProps);
//        
//        return newRule;
//    }
    
    public void testRemove()
    {
        
    }
}
