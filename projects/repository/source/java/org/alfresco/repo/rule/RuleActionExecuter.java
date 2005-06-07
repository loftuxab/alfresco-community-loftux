/**
 * 
 */
package org.alfresco.repo.rule;

import org.alfresco.repo.ref.NodeRef;

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
