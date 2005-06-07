/**
 * 
 */
package org.alfresco.repo.rule.impl.action;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.rule.RuleAction;

/**
 * @author Roy Wetherall
 */
public class AddFeaturesActionExecutor extends RuleActionExecutorAbstractBase
{
    public AddFeaturesActionExecutor(
            RuleAction ruleAction, 
            NodeService nodeService)
    {
        super(ruleAction, nodeService);
    }

    /**
     * @see org.alfresco.repo.rule.RuleActionExecuter#execute(org.alfresco.repo.ref.NodeRef, NodeRef)
     */
    public void execute(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        System.out.println("Executing add features for " + actionedUponNodeRef.getId());
        
        Map<String, Serializable> paramValues = this.ruleAction.getParameterValues();
        QName aspectQName = (QName)paramValues.get("aspect-name");
        // TODO handle exeception here if param not set ..
        
        // TODO get the mandatory values for the aspect
        
        // Add the aspect
        this.nodeService.addAspect(actionedUponNodeRef, aspectQName, null);        
    }

}
