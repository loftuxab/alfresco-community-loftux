/**
 * 
 */
package org.alfresco.repo.rule.impl.condition;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.rule.RuleCondition;

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
     * @see org.alfresco.repo.rule.RuleConditionEvaluator#evaluate(org.alfresco.repo.ref.NodeRef, NodeRef)
     */
    public boolean evaluate(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        // Always return true
        return true;
    }

}
