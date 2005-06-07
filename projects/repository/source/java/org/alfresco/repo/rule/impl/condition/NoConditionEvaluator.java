/**
 * 
 */
package org.alfresco.repo.rule.impl.condition;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.rule.RuleCondition;

/**
 * @author Roy Wetherall
 */
public class NoConditionEvaluator extends
        RuleConditionEvaluatorAbstractBase
{

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
