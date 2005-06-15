/**
 * 
 */
package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.config.ConfigService;
import org.alfresco.config.ConfigSource;
import org.alfresco.config.source.ClassPathConfigSource;
import org.alfresco.config.xml.XMLConfigService;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleCondition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;

/**
 * @author Roy Wetherall
 */
public class RuleBaseTest extends BaseSpringTest 
{
    /**
     * Data used in the tests
     */
    protected static final String RULE_TYPE_NAME = "ruleType1";
    protected static final RuleType RULE_TYPE = new RuleTypeImpl(RULE_TYPE_NAME);
    protected static final String ACTION_PROP_NAME_1 = "actionPropName1";
    protected static final String ACTION_PROP_VALUE_1 = "actionPropValue1";
    protected static final String COND_PROP_VALUE_1 = "condPropValue1";
    protected static final String COND_PROP_NAME_1 = "condPropName1";
    protected static final String TITLE = "title";
    protected static final String DESCRIPTION = "description";
    protected static final String CONDITION_DEF_NAME = "conditionDefinition";
    protected static final String ACTION_DEF_NAME = "actionDefinition";
    
    protected NodeService nodeService;
    protected ContentService contentService;
    protected RuleConfig ruleConfig;
    
    /**
     * Config services used
     */
    protected ConfigService configService;
    protected static final String TEST_CONFIG = "org/alfresco/repo/rule/rule-config-test.xml";
    
    protected StoreRef testStoreRef;
    protected NodeRef rootNodeRef;
    protected NodeRef nodeRef;
    protected NodeRef configFolder;
   
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        this.nodeService = (NodeService)this.applicationContext.getBean("nodeService");
        this.contentService = (ContentService)this.applicationContext.getBean("contentService");
        
        // Sort out the required config
        ConfigSource configSource = new ClassPathConfigSource(TEST_CONFIG);
        this.configService = new XMLConfigService(configSource);
        ((XMLConfigService)this.configService).init();
        
        this.ruleConfig = new RuleConfig(this.configService);
        
        this.testStoreRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        this.rootNodeRef = this.nodeService.getRootNode(this.testStoreRef);
        
        // Create the node used for tests
        this.nodeRef = this.nodeService.createNode(
                rootNodeRef,
				ContentModel.ASSOC_CONTAINS,
                QName.createQName("{test}testnode"),
                ContentModel.TYPE_CONTAINER).getChildRef();
        
        // Create the config folder
        this.configFolder = this.nodeService.createNode(
                rootNodeRef,
				ContentModel.ASSOC_CONTAINS,
                QName.createQName("{test}configfolder"),
                ContentModel.TYPE_CONFIGURATIONS).getChildRef();
    }
    
    protected void makeTestNodeActionable()
    {
        // Manually make the test node actionable
        this.nodeService.addAspect(this.nodeRef, ContentModel.ASPECT_ACTIONABLE, null);
        this.nodeService.createAssociation(
                this.nodeRef, 
                configFolder, 
                ContentModel.ASSOC_CONFIGURATIONS);
    }
    
    protected RuleImpl createTestRule(String id)
    {
        // Rule properties
        Map<String, Serializable> conditionProps = new HashMap<String, Serializable>();
        conditionProps.put(COND_PROP_NAME_1, COND_PROP_VALUE_1);
        Map<String, Serializable> actionProps = new HashMap<String, Serializable>();
        actionProps.put(ACTION_PROP_NAME_1, ACTION_PROP_VALUE_1);
        
        RuleConditionDefinition cond = this.ruleConfig.getConditionDefinition(CONDITION_DEF_NAME);
        RuleActionDefinitionImpl action = this.ruleConfig.getActionDefinition(ACTION_DEF_NAME);
        
        // Create the rule
        RuleImpl rule = new RuleImpl(id, RULE_TYPE);
        rule.setTitle(TITLE);
        rule.setDescription(DESCRIPTION);
        rule.addRuleCondition(
                cond, 
                conditionProps);
        rule.addRuleAction(
                action, 
                actionProps);
        
        return rule;
    }
    
    protected void checkRule(RuleImpl rule, String id)
    {
        // Check the basic details of the rule
        assertEquals(id, rule.getId());
        assertEquals(RULE_TYPE.getName(), rule.getRuleType().getName());
        assertEquals(TITLE, rule.getTitle());
        assertEquals(DESCRIPTION, rule.getDescription());
        
        // Check conditions
        List<RuleCondition> ruleConditions = rule.getRuleConditions();
        assertNotNull(ruleConditions);
        assertEquals(1, ruleConditions.size());
        assertEquals(CONDITION_DEF_NAME, ruleConditions.get(0).getRuleConditionDefinition().getName());
        Map<String, Serializable> condParams = ruleConditions.get(0).getParameterValues();
        assertNotNull(condParams);
        assertEquals(1, condParams.size());
        assertTrue(condParams.containsKey(COND_PROP_NAME_1));
        assertEquals(COND_PROP_VALUE_1, condParams.get(COND_PROP_NAME_1));
        
        // Check the actions
        List<RuleAction> ruleActions = rule.getRuleActions();
        assertNotNull(ruleActions);
        assertEquals(1, ruleActions.size());
        assertEquals(ACTION_DEF_NAME, ruleActions.get(0).getRuleActionDefinition().getName());
        Map<String, Serializable> actionParams = ruleActions.get(0).getParameterValues();
        assertNotNull(actionParams);
        assertEquals(1, actionParams.size());
        assertTrue(actionParams.containsKey(ACTION_PROP_NAME_1));
        assertEquals(ACTION_PROP_VALUE_1, actionParams.get(ACTION_PROP_NAME_1));
    }
}
