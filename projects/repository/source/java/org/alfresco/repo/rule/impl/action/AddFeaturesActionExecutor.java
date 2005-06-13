/**
 * 
 */
package org.alfresco.repo.rule.impl.action;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.namespace.QName;
import org.springframework.context.ApplicationContext;

/**
 * Add features action executor implementation.
 * 
 * @author Roy Wetherall
 */
public class AddFeaturesActionExecutor extends RuleActionExecutorAbstractBase
{
	private NodeService nodeService;

    /**
	 * Constructor
	 * 
	 * @param ruleAction	      the rule action
	 * @param nodeService	      the node service
     * @param applicationContext  the application context
	 */
    public AddFeaturesActionExecutor(
            RuleAction ruleAction,
            ApplicationContext applicationContext)
    {
        super(ruleAction, applicationContext);
        
        this.nodeService = (NodeService)this.applicationContext.getBean("nodeService");
    }

    /**
     * @see org.alfresco.repo.rule.RuleActionExecuter#execute(org.alfresco.service.cmr.repository.NodeRef, NodeRef)
     */
    public void executeImpl(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        // Get the name of the aspec to add
		Map<String, Serializable> paramValues = this.ruleAction.getParameterValues();
        QName aspectQName = (QName)paramValues.get("aspect-name");
        
		// TODO get the properties that should be set when the aspect is added
		
        // Add the aspect
        this.nodeService.addAspect(actionedUponNodeRef, aspectQName, null);        
    }

}
