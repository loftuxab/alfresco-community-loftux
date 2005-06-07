/**
 * 
 */
package org.alfresco.repo.rule.impl.action;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.rule.RuleAction;
import org.alfresco.repo.rule.RuleActionExecuter;

/**
 * @author Roy Wetherall
 */
public abstract class RuleActionExecutorAbstractBase implements RuleActionExecuter
{

    protected RuleAction ruleAction;
    protected NodeService nodeService;

    /**
     * 
     */
    public RuleActionExecutorAbstractBase(
            RuleAction ruleAction,
            NodeService nodeService)
    {
        this.ruleAction = ruleAction;
        this.nodeService = nodeService;
    }
}
