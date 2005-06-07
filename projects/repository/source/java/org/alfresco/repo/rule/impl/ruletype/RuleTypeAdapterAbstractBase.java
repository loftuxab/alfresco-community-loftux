/**
 * 
 */
package org.alfresco.repo.rule.impl.ruletype;

import java.util.List;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.rule.Rule;
import org.alfresco.repo.rule.RuleAction;
import org.alfresco.repo.rule.RuleActionExecuter;
import org.alfresco.repo.rule.RuleCondition;
import org.alfresco.repo.rule.RuleConditionEvaluator;
import org.alfresco.repo.rule.RuleService;
import org.alfresco.repo.rule.RuleServiceException;
import org.alfresco.repo.rule.RuleType;
import org.alfresco.repo.rule.RuleTypeAdapter;
import org.alfresco.repo.rule.impl.RuleActionDefinitionImpl;
import org.alfresco.repo.rule.impl.RuleConditionDefinitionImpl;

/**
 * @author Roy Wetherall
 */
public abstract class RuleTypeAdapterAbstractBase implements RuleTypeAdapter
{
    protected RuleType ruleType;
    
    protected PolicyComponent policyComponent;
    
    private RuleService ruleService;
    
    private NodeService nodeService;
    
    /**
     * 
     */
    public RuleTypeAdapterAbstractBase(
            RuleType ruleType,
            PolicyComponent policyComponent,
            RuleService ruleService,
            NodeService nodeService)
    {
        this.ruleType = ruleType;
        this.policyComponent = policyComponent;
        this.ruleService = ruleService; 
        this.nodeService = nodeService;
    }
    
    protected void executeRules(
            NodeRef actionableNodeRef, 
            NodeRef actionedUponNodeRef)
    {
        if (this.ruleService.hasRules(actionableNodeRef) == true)
        {
            List<Rule> rules = this.ruleService.getRulesByRuleType(
                    actionableNodeRef, 
                    this.ruleType);
            
            for (Rule rule : rules)
            {   
                // Get the rule conditions
                List<RuleCondition> conds = rule.getRuleConditions();
                // TODO assume there is one and only one condition for the rule
                RuleCondition cond = conds.get(0);
                RuleConditionEvaluator evaluator = getConditionEvaluator(cond);
                
                // Get the rule acitons
                List<RuleAction> actions = rule.getRuleActions();
                // TODO assume there is one and only one action for the rule
                RuleAction action = actions.get(0);
                RuleActionExecuter executor = getActionExecutor(action);
                
                if (evaluator.evaluate(actionableNodeRef, actionedUponNodeRef) == true)
                {
                    executor.execute(actionableNodeRef, actionedUponNodeRef);
                }
            }
        }
    }

    /**
     * 
     * @param action
     * @return
     */
    private RuleActionExecuter getActionExecutor(RuleAction action)
    {
        RuleActionExecuter executor = null;
        String executorString = ((RuleActionDefinitionImpl)action.getRuleActionDefinition()).getRuleActionExecutor();
        
        try
        {
            // Create the action executor
            executor = (RuleActionExecuter)Class.forName(executorString).
                    getConstructor(new Class[]{RuleAction.class, NodeService.class}).
                    newInstance(new Object[]{action, this.nodeService});
        }
        catch(Exception exception)
        {
            // Error creating and initialising the adapter
            throw new RuleServiceException("Unable to initialise the rule action executor.", exception);
        }
        
        return executor;
    }

    private RuleConditionEvaluator getConditionEvaluator(RuleCondition cond)
    {
        RuleConditionEvaluator evaluator = null;
        String evaluatorString = ((RuleConditionDefinitionImpl)cond.getRuleConditionDefinition()).getConditionEvaluator();
        
        try
        {
            // Create the action evaluator
            evaluator = (RuleConditionEvaluator)Class.forName(evaluatorString).
                    getConstructor(new Class[]{RuleCondition.class, NodeService.class}).
                    newInstance(new Object[]{cond, this.nodeService});
        }
        catch(Exception exception)
        {
            // Error creating and initialising  adapter
            throw new RuleServiceException("Unable to initialise the rule condition evaluator.", exception);
        }
        
        return evaluator;
    }
}
