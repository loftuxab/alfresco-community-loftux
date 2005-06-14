/**
 * 
 */
package org.alfresco.repo.rule.condition;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.RuleCondition;

/**
 * No condition evaluator implmentation.
 * 
 * @author Roy Wetherall
 */
public class NoConditionEvaluator extends
        RuleConditionEvaluatorAbstractBase
{
	/**
	 * Evaluator constants
	 */
	public static final String NAME = "no-condition";
	
	/**
	 * Constructor 
	 * 
	 * @param ruleCondition
	 * @param serviceRegistry
	 */
	public NoConditionEvaluator(RuleCondition ruleCondition, ServiceRegistry serviceRegistry) 
	{
		super(ruleCondition, serviceRegistry);
	}


    /**
     * @see org.alfresco.repo.rule.condition.RuleConditionEvaluatorAbstractBase#evaluateImpl(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean evaluateImpl(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        // Always return true
        return true;
    }

}
