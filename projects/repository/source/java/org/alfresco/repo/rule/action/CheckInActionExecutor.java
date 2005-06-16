/**
 * 
 */
package org.alfresco.repo.rule.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.rule.common.ParameterDefinitionImpl;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.version.Version;

/**
 * Check in action executor
 * 
 * @author Roy Wetherall
 */
public class CheckInActionExecutor extends RuleActionExecutorAbstractBase
{
    public static final String NAME = "check-in";
    public static final String PARAM_DESCRIPTION = "description";
    
    /**
     * The node service
     */
    private NodeService nodeService;
    
    /**
     * The coci service
     */
    private CheckOutCheckInService cociService;

	public void setNodeService(NodeService nodeService) 
	{
		this.nodeService = nodeService;
	}
	
	public void setCociService(CheckOutCheckInService cociService) 
	{
		this.cociService = cociService;
	}

    /**
     * @see org.alfresco.repo.rule.action.RuleActionExecuter#execute(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void executeImpl(RuleAction ruleAction, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        // First ensure that the actionedUponNodeRef is a workingCopy
        if (this.nodeService.exists(actionedUponNodeRef) == true &&
			this.nodeService.hasAspect(actionedUponNodeRef, ContentModel.ASPECT_WORKING_COPY) == true)
        {
            // Get the version description
            String description = (String)ruleAction.getParameterValue(PARAM_DESCRIPTION);
            Map<String, Serializable> versionProperties = new HashMap<String, Serializable>(1);
            versionProperties.put(Version.PROP_DESCRIPTION, description);
            
            // TODO determine whether the document should be kept checked out
            
            // Check the node in
            this.cociService.checkin(actionedUponNodeRef, versionProperties);
        }
    }

	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		paramList.add(new ParameterDefinitionImpl(PARAM_DESCRIPTION, ParameterType.STRING, false, getParamDisplayLabel(PARAM_DESCRIPTION)));
	}

}
