/**
 * 
 */
package org.alfresco.repo.rule.ruletype;

import java.util.List;

import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.rule.RuleTypeAdapter;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
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
	//protected ServiceRegistry serviceRegistry;
	
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
	//	this.serviceRegistry = serviceRegistry;
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
				this.ruleService.addRulePendingExecution(actionableNodeRef, actionedUponNodeRef, rule);
            }
        }
    }

    
}
