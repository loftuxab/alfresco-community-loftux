/**
 * 
 */
package org.alfresco.repo.rule.impl;

import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.rule.RuleCondition;

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
