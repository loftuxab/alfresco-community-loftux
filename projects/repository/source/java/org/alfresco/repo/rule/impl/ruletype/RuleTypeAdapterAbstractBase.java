/**
 * 
 */
package org.alfresco.repo.rule.impl.ruletype;

import java.util.List;

import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.rule.Rule;
import org.alfresco.repo.rule.RuleService;
import org.alfresco.repo.rule.RuleType;
import org.alfresco.repo.rule.RuleTypeAdapter;

/**
 * @author Roy Wetherall
 */
public abstract class RuleTypeAdapterAbstractBase implements RuleTypeAdapter
{
    protected RuleType ruleType;
    
    protected PolicyComponent policyComponent;
    
    private RuleService ruleService;
    
    /**
     * 
     */
    public RuleTypeAdapterAbstractBase(
            RuleType ruleType,
            PolicyComponent policyComponent,
            RuleService ruleService)
    {
        this.ruleType = ruleType;
        this.policyComponent = policyComponent;
        this.ruleService = ruleService; 
    }
    
    protected void executeRules(NodeRef nodeRef)
    {
        if (this.ruleService.hasRules(nodeRef) == true)
        {
            List<Rule> rules = this.ruleService.getRulesByRuleType(
                    nodeRef, 
                    this.ruleType);
            
            for (Rule rule : rules)
            {   
                // Check the condition ..
                
                // Do the action ..
            }
        }
    }
}
