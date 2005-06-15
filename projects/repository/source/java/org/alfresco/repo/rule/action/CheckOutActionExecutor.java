/**
 * 
 */
package org.alfresco.repo.rule.action;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.namespace.QName;

/**
 * Check out action executor
 * 
 * @author Roy Wetherall
 */
public class CheckOutActionExecutor extends RuleActionExecutorAbstractBase
{
    public static final String NAME = "check-out";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_ASSOC_TYPE_QNAME = "assoc-type";
    public static final String PARAM_ASSOC_QNAME = "assoc-name";

    /**
     * The version operations service
     */
    private CheckOutCheckInService cociService;
	
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
    public CheckOutActionExecutor(RuleAction ruleAction, ServiceRegistry serviceRegistry) 
	{
		super(ruleAction, serviceRegistry);		
		this.cociService = serviceRegistry.getCheckOutCheckInService();
		this.nodeService = serviceRegistry.getNodeService();
	}

    /**
     * @see org.alfresco.repo.rule.RuleActionExecuter#execute(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void executeImpl(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
		if (this.nodeService.exists(actionedUponNodeRef) == true)
		{
	        // Get the destination details
	        NodeRef destinationParent = (NodeRef)this.ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER);
	        QName destinationAssocTypeQName = (QName)this.ruleAction.getParameterValue(PARAM_ASSOC_TYPE_QNAME);
	        QName destinationAssocQName = (QName)this.ruleAction.getParameterValue(PARAM_ASSOC_QNAME);
	        
	        if (destinationParent == null || destinationAssocTypeQName == null || destinationAssocQName == null)
	        {
	            // Check the node out to the current location
	            this.cociService.checkout(actionedUponNodeRef);
	        }
	        else
	        {
	            // Check the node out to the specified location
	            this.cociService.checkout(
	                    actionedUponNodeRef, 
	                    destinationParent, 
	                    destinationAssocTypeQName, 
	                    destinationAssocQName);
	        }
		}
    }

}
