/**
 * 
 */
package org.alfresco.repo.rule.impl.condition;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.rule.RuleCondition;
import org.alfresco.repo.rule.RuleConditionEvaluator;

/**
 * @author Roy Wetherall
 */
public abstract class RuleConditionEvaluatorAbstractBase implements RuleConditionEvaluator
{
    private RuleCondition ruleCondition;
    private NodeService nodeService;

    /**
     * 
     */
    public RuleConditionEvaluatorAbstractBase(
            RuleCondition ruleCondition, 
            NodeService nodeService)
    {
        this.ruleCondition = ruleCondition;
        this.nodeService = nodeService;
    }

}
