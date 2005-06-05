/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.util.List;

import org.alfresco.repo.rule.RuleActionDefinition;
import org.alfresco.repo.rule.RuleConditionDefinition;
import org.alfresco.repo.rule.RuleService;
import org.alfresco.repo.rule.RuleType;
import org.alfresco.util.BaseSpringTest;

/**
 * @author Roy Wetherall
 */
public class RuleServiceImplTest extends BaseSpringTest
{
    /**
     * Rule service
     */
    private RuleService ruleService;
    
    /**
     * Sets the rule service
     * 
     * @param ruleService
     */
    public void setRuleService(RuleService ruleService)
    {
        this.ruleService = ruleService;
    }
    
    public void testGetRuleType()
    {
        // TODO at the moment this is just a holding test to 
        //      ensure the data reuired by the UI is returned
        
        List<RuleType> ruleTypes = this.ruleService.getRuleTypes();
        assertNotNull(ruleTypes);
        for (RuleType ruleType : ruleTypes)
        {
            assertNotNull(ruleType.getName());
            assertNotNull(ruleType.getDisplayLabel());
            System.out.println("Rule type: " + ruleType.getName() + " " + ruleType.getDisplayLabel());
        }
    }
    
    public void testGetActionDefinitions()
    {
        // TODO at the moment this is just a holding test to 
        // ensure the data reuired by the UI is returned        
        
        List<RuleActionDefinition> actions = this.ruleService.getActionDefinitions();
        assertNotNull(actions);
        for (RuleActionDefinition action : actions)
        {
            assertNotNull(action.getName());
            assertNotNull(action.getTitle());
            assertNotNull(action.getDescription());
            System.out.println("Action defintion: " + action.getName());
        }
    }
    
    public void testGetConditionDefinitions()
    {
        // TODO at the moment this is just a holding test to 
        // ensure the data reuired by the UI is returned        
        
        List<RuleConditionDefinition> conds = this.ruleService.getConditionDefinitions();
        assertNotNull(conds);
        for (RuleConditionDefinition cond : conds)
        {
            assertNotNull(cond.getName());
            assertNotNull(cond.getTitle());
            assertNotNull(cond.getDescription());
            System.out.println("Condition defintion: " + cond.getName());
        }
    }
    
    public void testcreateRule()
    {
        // TODO
    }
}
