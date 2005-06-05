/**
 * 
 */
package org.alfresco.repo.rule.impl;

import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.rule.RuleAction;

/**
 * @author Roy Wetherall
 */
public interface RuleActionExecuter
{
    /**
     * 
     * @param nodeRef
     * @param ruleAction
     */
    public void execute(NodeRef nodeRef, RuleAction ruleAction);
}
