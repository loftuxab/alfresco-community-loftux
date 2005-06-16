/**
 * Created on Jun 16, 2005
 */
package org.alfresco.repo.rule.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.rule.common.ParameterDefinitionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.namespace.QName;

/**
 * Simple workflow action executor
 * 
 * @author Roy Wetherall
 */
public class SimpleWorkflowActionExecutor extends RuleActionExecutorAbstractBase 
{
	public static final String NAME = "simple-workflow";
	public static final String PARAM_APPROVE_STEP = "approve-step";
	public static final String PARAM_APPROVE_FOLDER = "approve-folder";
	public static final String PARAM_APPROVE_MOVE = "approve-move";
	public static final String PARAM_REJECT_STEP = "reject-step";
	public static final String PARAM_REJECT_FOLDER = "reject-folder";
	public static final String PARAM_REJECT_MOVE = "reject-move";
	
	private NodeService nodeService;

	public void setNodeService(NodeService nodeService) 
	{
		this.nodeService = nodeService;
	}

	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		paramList.add(new ParameterDefinitionImpl(PARAM_APPROVE_STEP, ParameterType.STRING, false, getParamDisplayLabel(PARAM_APPROVE_STEP)));
		paramList.add(new ParameterDefinitionImpl(PARAM_APPROVE_FOLDER, ParameterType.NODE_REF, false, getParamDisplayLabel(PARAM_APPROVE_FOLDER)));
		paramList.add(new ParameterDefinitionImpl(PARAM_APPROVE_MOVE, ParameterType.BOOLEAN, false, getParamDisplayLabel(PARAM_APPROVE_MOVE)));
		paramList.add(new ParameterDefinitionImpl(PARAM_REJECT_STEP, ParameterType.STRING, false, getParamDisplayLabel(PARAM_REJECT_STEP)));
		paramList.add(new ParameterDefinitionImpl(PARAM_REJECT_FOLDER, ParameterType.NODE_REF, false, getParamDisplayLabel(PARAM_REJECT_FOLDER)));
		paramList.add(new ParameterDefinitionImpl(PARAM_REJECT_MOVE, ParameterType.BOOLEAN, false, getParamDisplayLabel(PARAM_REJECT_MOVE)));		
	}

	/**
	 * @see org.alfresco.repo.rule.action.RuleActionExecutorAbstractBase#executeImpl(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	protected void executeImpl(
			RuleAction ruleAction,
			NodeRef actionableNodeRef,
			NodeRef actionedUponNodeRef) 
	{
		if (this.nodeService.exists(actionedUponNodeRef) == true)
		{
			// Get the parameter values
			String approveStep = (String)ruleAction.getParameterValue(PARAM_APPROVE_STEP);
			NodeRef approveFolder = (NodeRef)ruleAction.getParameterValue(PARAM_APPROVE_FOLDER);
			Boolean approveMove = (Boolean)ruleAction.getParameterValue(PARAM_APPROVE_MOVE);
			String rejectStep = (String)ruleAction.getParameterValue(PARAM_REJECT_STEP);
			NodeRef rejectFolder = (NodeRef)ruleAction.getParameterValue(PARAM_REJECT_FOLDER);
			Boolean rejectMove = (Boolean)ruleAction.getParameterValue(PARAM_REJECT_MOVE);
			
			// Set the property values
			Map<QName, Serializable> propertyValues = new HashMap<QName, Serializable>();
			propertyValues.put(ContentModel.PROP_APPROVE_STEP, approveStep);
			propertyValues.put(ContentModel.PROP_APPROVE_FOLDER, approveFolder);
			propertyValues.put(ContentModel.PROP_APPROVE_MOVE, approveMove.booleanValue());
			propertyValues.put(ContentModel.PROP_REJECT_STEP, rejectStep);
			propertyValues.put(ContentModel.PROP_REJECT_FOLDER, rejectFolder);
			propertyValues.put(ContentModel.PROP_REJECT_MOVE, rejectMove.booleanValue());
			
			// Apply the simple workflow aspect to the node
			this.nodeService.addAspect(actionedUponNodeRef, ContentModel.ASPECT_SIMPLE_WORKFLOW, propertyValues);
		}
	}
}
