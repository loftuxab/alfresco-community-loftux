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
     * @param actionableNodeRef
     * @param actionedUponNodeRef TODO
     */
    public void execute(
            NodeRef actionableNodeRef, 
            NodeRef actionedUponNodeRef);
}
