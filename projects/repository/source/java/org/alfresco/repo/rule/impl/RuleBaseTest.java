/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.config.ConfigService;
import org.alfresco.config.ConfigSource;
import org.alfresco.config.source.ClassPathConfigSource;
import org.alfresco.config.xml.XMLConfigService;
import org.alfresco.repo.rule.RuleAction;
import org.alfresco.repo.rule.RuleCondition;
import org.alfresco.repo.rule.RuleType;
import org.alfresco.util.BaseSpringTest;

/**
 * @author Roy Wetherall
 */
public class RuleBaseTest extends BaseSpringTest
{
    /**
     * Data used in the tests
     */
    protected static final String RULE_TYPE_NAME = "ruleType";
    protected static final RuleType RULE_TYPE = new RuleTypeImpl(RULE_TYPE_NAME);
    protected static final String ACTION_PROP_NAME_1 = "actionPropName1";
    protected static final String ACTION_PROP_VALUE_1 = "actionPropValue1";
    protected static final String COND_PROP_VALUE_1 = "condPropValue1";
    protected static final String COND_PROP_NAME_1 = "condPropName1";
    protected static final String TITLE = "title";
    protected static final String DESCRIPTION = "description";
    protected static final String CONDITION_DEF_NAME = "conditionDefinition";
    protected static final String ACTION_DEF_NAME = "actionDefinition";
    
    /**
     * Config services used
     */
    protected ConfigService configService;
    protected static final String TEST_CONFIG = "org/alfresco/repo/rule/impl/rule-config-test.xml";
   
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        // Sort out the required config
        ConfigSource configSource = new ClassPathConfigSource(TEST_CONFIG);
        this.configService = new XMLConfigService(configSource);
        ((XMLConfigService)this.configService).init();
    }
    
    protected RuleImpl createTestRule(String id)
    {
        // Rule properties
        Map<String, Serializable> conditionProps = new HashMap<String, Serializable>();
        conditionProps.put(COND_PROP_NAME_1, COND_PROP_VALUE_1);
        Map<String, Serializable> actionProps = new HashMap<String, Serializable>();
        actionProps.put(ACTION_PROP_NAME_1, ACTION_PROP_VALUE_1);
        
        // Create the rule
        RuleImpl rule = new RuleImpl(id, RULE_TYPE);
        rule.setTitle(TITLE);
        rule.setDescription(DESCRIPTION);
        rule.addRuleCondition(
                new RuleConditionDefinitionImpl(CONDITION_DEF_NAME), 
                conditionProps);
        rule.addRuleAction(
                new RuleActionDefinitionImpl(ACTION_DEF_NAME), 
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
