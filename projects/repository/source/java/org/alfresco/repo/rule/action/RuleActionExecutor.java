/**
 * 
 */
package org.alfresco.repo.rule.action;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleActionDefinition;

/**
 * @author Roy Wetherall
 */
public interface RuleActionExecutor
{
	public RuleActionDefinition getRuleActionDefinition();
	
    /**
     * 
     * @param actionableNodeRef
     * @param actionedUponNodeRef TODO
     */
    public void execute(
			RuleAction ruleAction,
            NodeRef actionableNodeRef, 
            NodeRef actionedUponNodeRef);
}
