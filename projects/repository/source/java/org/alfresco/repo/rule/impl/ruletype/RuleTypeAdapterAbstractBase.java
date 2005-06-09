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
 * Rule type adapter abstract base class implmentation.
 * 
 * @author Roy Wetherall
 */
public abstract class RuleTypeAdapterAbstractBase implements RuleTypeAdapter
{
	/**
	 * The rule type
	 */
	protected RuleType ruleType;
	
	/**
	 * The policy component
	 */
    protected PolicyComponent policyComponent;
    
	/**
	 * The rule service
	 */
    private RuleService ruleService;
    
	/**
	 * The node service
	 */
    private NodeService nodeService;
    
    /**
     * Constructor
     * 
     * @param ruleType			the rule type
     * @param policyComponent	the policy component
     * @param ruleService		the rule service
     * @param nodeService		the node service
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
    
	/**
	 * Execute rules that relate to the actionable node for this type on the
	 * actioned upon node reference.
	 * 
	 * @param actionableNodeRef		the actionable node reference
	 * @param actionedUponNodeRef	the actioned upon node reference
	 */
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
				if (conds.size() == 0)
				{
					throw new RuleServiceException("No rule conditions have been specified for the rule.");
				}
				else if (conds.size() > 1)
				{
					// TODO at the moment we only support one rule condition
					throw new RuleServiceException("Currently only one rule condition can be specified per rule.");
				}
				
				// Get the single rule condition
				RuleCondition cond = conds.get(0);
                RuleConditionEvaluator evaluator = getConditionEvaluator(cond);
                
                // Get the rule acitons
                List<RuleAction> actions = rule.getRuleActions();
				if (actions.size() == 0)
				{
					throw new RuleServiceException("No rule actions have been specified for the rule.");
				}
				else if (actions.size() > 1)
				{
					// TODO at the moment we only support one rule action
					throw new RuleServiceException("Currently only one rule action can be specified per rule.");
				}
				
				// Get the single action
                RuleAction action = actions.get(0);
                RuleActionExecuter executor = getActionExecutor(action);
                
				// Evaluate the condition
                if (evaluator.evaluate(actionableNodeRef, actionedUponNodeRef) == true)
                {
					// Execute the rule
                    executor.execute(actionableNodeRef, actionedUponNodeRef);
                }
            }
        }
    }

    /**
     * Get the action executor instance.
     * 
     * @param action	the action
     * @return			the action executor
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
            // Error creating and initialising
            throw new RuleServiceException("Unable to initialise the rule action executor.", exception);
        }
        
        return executor;
    }

	/**
	 * Get the condition evaluator.
	 * 
	 * @param cond	the rule condition
	 * @return		the rule condition evaluator
	 */
    private RuleConditionEvaluator getConditionEvaluator(RuleCondition cond)
    {
        RuleConditionEvaluator evaluator = null;
        String evaluatorString = ((RuleConditionDefinitionImpl)cond.getRuleConditionDefinition()).getConditionEvaluator();
        
        try
        {
            // Create the condition evaluator
            evaluator = (RuleConditionEvaluator)Class.forName(evaluatorString).
                    getConstructor(new Class[]{RuleCondition.class, NodeService.class}).
                    newInstance(new Object[]{cond, this.nodeService});
        }
        catch(Exception exception)
        {
            // Error creating and initialising 
            throw new RuleServiceException("Unable to initialise the rule condition evaluator.", exception);
        }
        
        return evaluator;
    }
}
