/**
 * 
 */
package org.alfresco.repo.rule.action;

import java.util.List;

import org.alfresco.repo.rule.common.ParameterDefinitionImpl;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
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
	
	public void setNodeService(NodeService nodeService) 
	{
		this.nodeService = nodeService;
	}
	
	public void setCociService(CheckOutCheckInService cociService) 
	{
		this.cociService = cociService;
	}
    
	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, ParameterType.NODE_REF, false, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
		paramList.add(new ParameterDefinitionImpl(PARAM_ASSOC_TYPE_QNAME, ParameterType.QNAME, false, getParamDisplayLabel(PARAM_ASSOC_TYPE_QNAME)));
		paramList.add(new ParameterDefinitionImpl(PARAM_ASSOC_QNAME, ParameterType.QNAME, false, getParamDisplayLabel(PARAM_ASSOC_QNAME)));
	}

    /**
     * @see org.alfresco.repo.rule.action.RuleActionExecuter#execute(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void executeImpl(RuleAction ruleAction, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
		if (this.nodeService.exists(actionedUponNodeRef) == true)
		{
	        // Get the destination details
	        NodeRef destinationParent = (NodeRef)ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER);
	        QName destinationAssocTypeQName = (QName)ruleAction.getParameterValue(PARAM_ASSOC_TYPE_QNAME);
	        QName destinationAssocQName = (QName)ruleAction.getParameterValue(PARAM_ASSOC_QNAME);
	        
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
