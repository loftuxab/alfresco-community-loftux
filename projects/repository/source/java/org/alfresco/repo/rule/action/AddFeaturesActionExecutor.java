/**
 * 
 */
package org.alfresco.repo.rule.action;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.rule.common.ParameterDefinitionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.namespace.QName;

/**
 * Add features action executor implementation.
 * 
 * @author Roy Wetherall
 */
public class AddFeaturesActionExecutor extends RuleActionExecutorAbstractBase
{
	public static final String NAME = "add-features";
	public static final String PARAM_ASPECT_NAME = "aspect-name";
	
	/**
	 * The node service
	 */
	private NodeService nodeService;
	
	public void setNodeService(NodeService nodeService) 
	{
		this.nodeService = nodeService;
	}

    /**
     * @see org.alfresco.repo.rule.action.RuleActionExecutor#execute(org.alfresco.service.cmr.repository.NodeRef, NodeRef)
     */
    public void executeImpl(RuleAction ruleAction, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
		if (this.nodeService.exists(actionedUponNodeRef) == true)
		{
	        // Get the name of the aspec to add
			Map<String, Serializable> paramValues = ruleAction.getParameterValues();
	        QName aspectQName = (QName)paramValues.get("aspect-name");
	        
			// TODO get the properties that should be set when the aspect is added
			
	        // Add the aspect
	        this.nodeService.addAspect(actionedUponNodeRef, aspectQName, null);
		}
    }

	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		paramList.add(new ParameterDefinitionImpl(PARAM_ASPECT_NAME, ParameterType.QNAME, true, getParamDisplayLabel(PARAM_ASPECT_NAME)));
	}

}
