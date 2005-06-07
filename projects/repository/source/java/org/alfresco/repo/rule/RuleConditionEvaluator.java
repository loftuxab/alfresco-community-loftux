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
     * @param actionableNodeRef
     * @param actionedUponNodeRef TODO
     * @return
     */
    public boolean evaluate(
            NodeRef actionableNodeRef, 
            NodeRef actionedUponNodeRef);
}
