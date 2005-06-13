/**
 * 
 */
package org.alfresco.repo.rule;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author Roy Wetherall
 */
public interface RuleActionExecuter
{
    /**
     * 
     * @param actionableNodeRef
     * @param actionedUponNodeRef TODO
     */
    public void execute(
            NodeRef actionableNodeRef, 
            NodeRef actionedUponNodeRef);
}
