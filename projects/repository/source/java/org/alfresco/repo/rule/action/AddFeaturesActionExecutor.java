/**
 * 
 */
package org.alfresco.repo.rule.action;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.namespace.QName;

/**
 * Add features action executor implementation.
 * 
 * @author Roy Wetherall
 */
public class AddFeaturesActionExecutor extends RuleActionExecutorAbstractBase
{
	/**
	 * The node service
	 */
	private NodeService nodeService;

    /**
     * Constructor
     * 
     * @param ruleAction
     * @param serviceRegistry
     */
    public AddFeaturesActionExecutor(RuleAction ruleAction, ServiceRegistry serviceRegistry) 
	{
		super(ruleAction, serviceRegistry);		
		this.nodeService = serviceRegistry.getNodeService();
	}

    /**
     * @see org.alfresco.repo.rule.RuleActionExecuter#execute(org.alfresco.service.cmr.repository.NodeRef, NodeRef)
     */
    public void executeImpl(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
		if (this.nodeService.exists(actionedUponNodeRef) == true)
		{
	        // Get the name of the aspec to add
			Map<String, Serializable> paramValues = this.ruleAction.getParameterValues();
	        QName aspectQName = (QName)paramValues.get("aspect-name");
	        
			// TODO get the properties that should be set when the aspect is added
			
	        // Add the aspect
	        this.nodeService.addAspect(actionedUponNodeRef, aspectQName, null);
		}
    }

}
