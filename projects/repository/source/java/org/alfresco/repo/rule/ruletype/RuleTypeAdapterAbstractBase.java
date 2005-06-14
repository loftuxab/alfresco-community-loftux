/**
 * 
 */
package org.alfresco.repo.rule.ruletype;

import java.util.List;

import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.rule.RuleActionDefinitionImpl;
import org.alfresco.repo.rule.RuleActionExecuter;
import org.alfresco.repo.rule.RuleConditionDefinitionImpl;
import org.alfresco.repo.rule.RuleConditionEvaluator;
import org.alfresco.repo.rule.RuleTypeAdapter;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleCondition;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleServiceException;
import org.alfresco.service.cmr.rule.RuleType;

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
	 * Service registry
	 */
	protected ServiceRegistry serviceRegistry;
	
	/**
	 * The rule service
	 */
    private RuleService ruleService;

    
    /**
     * Constructor
     * 
     * @param ruleType			the rule type
     * @param serviceRegistry	the service registry
     */
    public RuleTypeAdapterAbstractBase(
			RuleType ruleType, 
			RuleService ruleService,
			PolicyComponent policyComponent,
			ServiceRegistry serviceRegistry)
    {
        this.ruleType = ruleType;
        this.policyComponent = policyComponent;
		this.serviceRegistry = serviceRegistry;
        this.ruleService = ruleService; 
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
                    getConstructor(new Class[]{RuleAction.class, ServiceRegistry.class}).
                    newInstance(new Object[]{action, this.serviceRegistry});
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
                    getConstructor(new Class[]{RuleCondition.class, ServiceRegistry.class}).
                    newInstance(new Object[]{cond, this.serviceRegistry});
        }
        catch(Exception exception)
        {
            // Error creating and initialising 
            throw new RuleServiceException("Unable to initialise the rule condition evaluator.", exception);
        }
        
        return evaluator;
    }
}
