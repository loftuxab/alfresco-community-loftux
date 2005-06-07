/**
 * 
 */
package org.alfresco.repo.rule;

import org.alfresco.repo.ref.NodeRef;

/**
 * @author Roy Wetherall
 */
public interface RuleConditionEvaluator
{
    /**
     * 
     * @param nodeRef
     * @param ruleCondition
     * @return
     */
    public boolean evaluate(NodeRef nodeRef, RuleCondition ruleCondition);
}
