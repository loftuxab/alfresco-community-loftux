/**
 * 
 */
package org.alfresco.repo.rule.action;

import java.util.List;

import org.alfresco.repo.rule.common.ParameterDefinitionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.namespace.QName;

/**
 * Copy action executor.
 * <p>
 * Copies the actioned upon node to a specified location.
 * 
 * @author Roy Wetherall
 */
public class MoveActionExecutor extends RuleActionExecutorAbstractBase
{
    public static final String NAME = "move";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_ASSOC_TYPE_QNAME = "assoc-type";
    public static final String PARAM_ASSOC_QNAME = "assoc-name";
    
    /**
     * Node service
     */
    private NodeService nodeService;
	
	public void setNodeService(NodeService nodeService) 
	{
		this.nodeService = nodeService;
	}

	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, ParameterType.NODE_REF, true, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
		paramList.add(new ParameterDefinitionImpl(PARAM_ASSOC_TYPE_QNAME, ParameterType.QNAME, true, getParamDisplayLabel(PARAM_ASSOC_TYPE_QNAME)));
		paramList.add(new ParameterDefinitionImpl(PARAM_ASSOC_QNAME, ParameterType.QNAME, true, getParamDisplayLabel(PARAM_ASSOC_QNAME)));
	}

    /**
     * @see org.alfresco.repo.rule.action.RuleActionExecutor#execute(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void executeImpl(RuleAction ruleAction, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
		if (this.nodeService.exists(actionedUponNodeRef) == true)
		{
	        NodeRef destinationParent = (NodeRef)ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER);
	        QName destinationAssocTypeQName = (QName)ruleAction.getParameterValue(PARAM_ASSOC_TYPE_QNAME);
	        QName destinationAssocQName = (QName)ruleAction.getParameterValue(PARAM_ASSOC_QNAME);
	        
	        this.nodeService.moveNode(
	                actionedUponNodeRef,
	                destinationParent,
	                destinationAssocTypeQName,
	                destinationAssocQName);
		}
    }

}
