/**
 * 
 */
package org.alfresco.repo.rule.impl.condition;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
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
	 * @param ruleCondition		the rule condition
	 * @param nodeService		the node service
	 */
    public NoConditionEvaluator(RuleCondition ruleCondition, NodeService nodeService)
    {
        super(ruleCondition, nodeService);
    }

    /**
     * @see org.alfresco.repo.rule.RuleConditionEvaluator#evaluate(org.alfresco.service.cmr.repository.NodeRef, NodeRef)
     */
    public boolean evaluate(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        // Always return true
        return true;
    }

}
