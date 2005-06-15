/**
 * Created on Jun 16, 2005
 */
package org.alfresco.repo.rule.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
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

	/**
	 * Constructor
	 * 
	 * @param ruleAction		the rule action
	 * @param serviceRegistry	the service registry
	 */
	public SimpleWorkflowActionExecutor(
			RuleAction ruleAction,
			ServiceRegistry serviceRegistry) 
	{
		super(ruleAction, serviceRegistry);		
		this.nodeService = serviceRegistry.getNodeService();
	}

	/**
	 * @see org.alfresco.repo.rule.action.RuleActionExecutorAbstractBase#executeImpl(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	protected void executeImpl(
			NodeRef actionableNodeRef,
			NodeRef actionedUponNodeRef) 
	{
		if (this.nodeService.exists(actionedUponNodeRef) == true)
		{
			// Get the parameter values
			String approveStep = (String)this.ruleAction.getParameterValue(PARAM_APPROVE_STEP);
			NodeRef approveFolder = (NodeRef)this.ruleAction.getParameterValue(PARAM_APPROVE_FOLDER);
			Boolean approveMove = (Boolean)this.ruleAction.getParameterValue(PARAM_APPROVE_MOVE);
			String rejectStep = (String)this.ruleAction.getParameterValue(PARAM_REJECT_STEP);
			NodeRef rejectFolder = (NodeRef)this.ruleAction.getParameterValue(PARAM_REJECT_FOLDER);
			Boolean rejectMove = (Boolean)this.ruleAction.getParameterValue(PARAM_REJECT_MOVE);
			
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
