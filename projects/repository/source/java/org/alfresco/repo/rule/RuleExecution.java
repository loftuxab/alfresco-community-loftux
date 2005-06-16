/**
 * Created on Jun 17, 2005
 */
package org.alfresco.repo.rule;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;

/**
 * @author Roy Wetherall
 */
public interface RuleExecution 
{
	void addRulePendingExecution(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef, Rule rule);

	void executePendingRules();	
}
