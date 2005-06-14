/**
 * 
 */
package org.alfresco.repo.rule.condition;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.RuleCondition;
import org.springframework.context.ApplicationContext;

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
	 * @param ruleCondition		  the rule condition
	 * @param applicationContext  the application context
	 */
    public NoConditionEvaluator(
            RuleCondition ruleCondition, 
            NodeService nodeService,
            ApplicationContext applicationContext)
    {
        super(ruleCondition, nodeService, applicationContext);
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
