/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.evaluator.ActionConditionEvaluator;
import org.alfresco.repo.action.executer.ActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.action.ActionConditionDefinition;
import org.alfresco.service.cmr.action.ActionDefinition;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.action.CompositeAction;
import org.alfresco.service.cmr.action.ParameterizedItem;
import org.alfresco.service.cmr.configuration.ConfigurableService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Action service implementation
 * 
 * @author Roy Wetherall
 */
public class ActionServiceImpl implements ActionService, ActionRegistration, ApplicationContextAware
{ 
	private static final QName ASSOC_NAME_ACTION_FOLDER = QName.createQName(NamespaceService.ALFRESCO_URI, "actionFolder");
	private static final QName ASSOC_NAME_ACTIONS = QName.createQName(NamespaceService.ALFRESCO_URI, "actions");
	
	/**
	 * The application context
	 */
	private ApplicationContext applicationContext;
	
	/**
	 * The node service
	 */
	private NodeService nodeService;
	
	/**
	 * The configurable service
	 */
	private ConfigurableService configurableService;
	
	/**
	 * All the condition definitions currently registered
	 */
	private Map<String, ActionConditionDefinition> conditionDefinitions = new HashMap<String, ActionConditionDefinition>();
	
	/**
	 * All the action definitions currently registered
	 */
	private Map<String, ActionDefinition> actionDefinitions = new HashMap<String, ActionDefinition>(); 
	
	/**
	 * Set the application context
	 * 
	 * @param applicationContext	the application context
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}
	
	/**
	 * Set the node service
	 * 
	 * @param nodeService  the node service
	 */
	public void setNodeService(NodeService nodeService)
	{
		this.nodeService = nodeService;
	}
	
	/**
	 * Set the configurable service
	 * 
	 * @param configurableService  the configurable service
	 */
	public void setConfigurableService(ConfigurableService configurableService)
	{
		this.configurableService = configurableService;
	}
	
	/**
	 * @see org.alfresco.service.cmr.action.ActionService#getActionDefinition(java.lang.String)
	 */
	public ActionDefinition getActionDefinition(String name)
	{
		return this.actionDefinitions.get(name);
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#getActionDefinitions()
	 */
	public List<ActionDefinition> getActionDefinitions()
	{
		return new ArrayList<ActionDefinition>(this.actionDefinitions.values());
	}	

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#getActionConditionDefinition(java.lang.String)
	 */
	public ActionConditionDefinition getActionConditionDefinition(String name)
	{
		return this.conditionDefinitions.get(name);
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#getActionConditionDefinitions()
	 */
	public List<ActionConditionDefinition> getActionConditionDefinitions()
	{
		return new ArrayList<ActionConditionDefinition>(this.conditionDefinitions.values());
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#createActionCondition(java.lang.String)
	 */
	public ActionCondition createActionCondition(String name)
	{
		return new ActionConditionImpl(GUID.generate(), name);
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#createAction()
	 */
	public Action createAction(String name)
	{
		return new ActionImpl(GUID.generate(),name);
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#createCompositeAction()
	 */
	public CompositeAction createCompositeAction()
	{
		return new CompositeActionImpl(GUID.generate());
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#evaluateAction(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
	 */
	public boolean evaluateAction(Action action, NodeRef actionedUponNodeRef)
	{
		boolean result = true;
		
		if (action.hasActionConditions() == true)
		{
			List<ActionCondition> actionConditions = action.getActionConditions();
			for (ActionCondition condition : actionConditions)
			{
				result = result && evaluateActionCondition(condition, actionedUponNodeRef);
			}
		}
		
		return result;
	}
	
	/**
	 * @see org.alfresco.service.cmr.action.ActionService#evaluateActionCondition(org.alfresco.service.cmr.action.ActionCondition, org.alfresco.service.cmr.repository.NodeRef)
	 */
	public boolean evaluateActionCondition(ActionCondition condition, NodeRef actionedUponNodeRef)
	{
		ActionConditionDefinitionImpl actionConditionDefinition = (ActionConditionDefinitionImpl)getActionConditionDefinition(condition.getActionConditionDefinitionName());
		String beanName = actionConditionDefinition.getConditionEvaluator();
		ActionConditionEvaluator evaluator = (ActionConditionEvaluator)this.applicationContext.getBean(beanName);
		return evaluator.evaluate(condition, actionedUponNodeRef);
	}
	
	/**
	 * @see org.alfresco.service.cmr.action.ActionService#executeAction(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef, boolean)
	 */
	public void executeAction(Action action, NodeRef actionedUponNodeRef, boolean checkConditions)
	{
		if (checkConditions == false || evaluateAction(action, actionedUponNodeRef) == true)
		{
			if (action instanceof CompositeAction)
			{
				for (Action subAction : ((CompositeAction)action).getActions())
				{
					executeAction(subAction, actionedUponNodeRef);
				}
			}
			else
			{
				ActionDefinitionImpl actionDefinition = (ActionDefinitionImpl)getActionDefinition(action.getActionDefinitionName());
				String beanName = actionDefinition.getRuleActionExecutor();
				ActionExecuter executer = (ActionExecuter)this.applicationContext.getBean(beanName);
				executer.execute(action, actionedUponNodeRef);
			}
		}
	}
	
	/**
	 * @see org.alfresco.service.cmr.action.ActionService#executeAction(org.alfresco.service.cmr.action.Action, NodeRef)
	 */
	public void executeAction(Action action, NodeRef actionedUponNodeRef)
	{
		executeAction(action, actionedUponNodeRef, true);
	}

	/**
	 * @see org.alfresco.repo.action.ActionRegistration#registerActionConditionEvaluator(org.alfresco.repo.action.evaluator.ActionConditionEvaluator)
	 */
	public void registerActionConditionEvaluator(ActionConditionEvaluator actionConditionEvaluator) 
	{
		ActionConditionDefinition cond = actionConditionEvaluator.getRuleConditionDefintion();
		this.conditionDefinitions.put(cond.getName(), cond);
	}

	/**
	 * @see org.alfresco.repo.action.ActionRegistration#registerActionExecuter(org.alfresco.repo.action.executer.ActionExecuter)
	 */
	public void registerActionExecuter(ActionExecuter actionExecuter) 
	{
		ActionDefinition action = actionExecuter.getRuleActionDefinition();
		this.actionDefinitions.put(action.getName(), action);
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#saveAction(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.action.Action)
	 */
	public void saveAction(NodeRef nodeRef, Action action)
	{
		NodeRef actionNodeRef = new NodeRef(nodeRef.getStoreRef(), action.getId());
		if (this.nodeService.exists(actionNodeRef) == false)
		{
			NodeRef actionFolderNodeRef = getActionFolder(nodeRef); 

			Map<QName, Serializable> props = new HashMap<QName, Serializable>(2);
			props.put(ContentModel.PROP_DEFINITION_NAME, action.getActionDefinitionName());
			props.put(ContentModel.PROP_NODE_UUID, action.getId());
			
			QName actionType = ContentModel.TYPE_ACTION;
			if(action instanceof CompositeAction)
			{
				actionType = ContentModel.TYPE_COMPOSITE_ACTION;
			}
			
			// Create the action node
			actionNodeRef = this.nodeService.createNode(
					actionFolderNodeRef,
					ContentModel.ASSOC_CONTAINS,
					ASSOC_NAME_ACTIONS,
					actionType,
					props).getChildRef();
		}
		
		// TODO update any other action properties here !!
		
		// Update the parameters of the action
		saveParameters(actionNodeRef, action);
		
		// Update the conditions of the action
		saveConditions(actionNodeRef, action);
		
		if (action instanceof CompositeAction)
		{
			// Update composite action
			saveActions(actionNodeRef, (CompositeAction)action);
		}
	}

	/**
	 * Save the actions of a composite action
	 * 
	 * @param compositeActionNodeRef	the node reference of the coposite action
	 * @param compositeAction			the composite action
	 */
	private void saveActions(NodeRef compositeActionNodeRef, CompositeAction compositeAction)
	{
		// TODO Need a way of sorting the order of the actions

		Map<String, Action> idToAction = new HashMap<String, Action>();
		for (Action action : compositeAction.getActions())
		{	
			idToAction.put(action.getId(), action);
		}
		
		List<ChildAssociationRef> actionRefs = this.nodeService.getChildAssocs(compositeActionNodeRef, ContentModel.ASSOC_ACTIONS);
		for (ChildAssociationRef actionRef : actionRefs)
		{
			NodeRef actionNodeRef = actionRef.getChildRef();
			if (idToAction.containsKey(actionNodeRef.getId()) == false)
			{
				// Delete the action
				this.nodeService.removeChild(compositeActionNodeRef, actionNodeRef);
			}
			else
			{
				// Update the action
				saveAction(actionNodeRef, idToAction.get(actionNodeRef.getId()));
				idToAction.remove(actionNodeRef.getId());
			}
			
		}
		
		// Create the actions remaining
		for (Map.Entry<String, Action> entry : idToAction.entrySet())
		{
			Map<QName, Serializable> props = new HashMap<QName, Serializable>(2);
			props.put(ContentModel.PROP_DEFINITION_NAME, entry.getValue().getActionDefinitionName());
			props.put(ContentModel.PROP_NODE_UUID, entry.getValue().getId());
			
			NodeRef actionNodeRef = this.nodeService.createNode(
					compositeActionNodeRef,
					ContentModel.ASSOC_ACTIONS,
					ContentModel.ASSOC_ACTIONS,
					ContentModel.TYPE_ACTION,
					props).getChildRef();
			
			saveAction(actionNodeRef, entry.getValue());
		}
	}

	/**
	 * Saves the conditions associated with an action
	 * 
	 * @param actionNodeRef		the action node reference
	 * @param action			the action
	 */
	private void saveConditions(NodeRef actionNodeRef, Action action)
	{
		// TODO Need a way of sorting out the order of the conditions

		Map<String, ActionCondition> idToCondition = new HashMap<String, ActionCondition>();
		for (ActionCondition actionCondition : action.getActionConditions())
		{	
			idToCondition.put(actionCondition.getId(), actionCondition);
		}
		
		List<ChildAssociationRef> conditionRefs = this.nodeService.getChildAssocs(actionNodeRef, ContentModel.ASSOC_CONDITIONS);
		for (ChildAssociationRef conditionRef : conditionRefs)
		{
			NodeRef conditionNodeRef = conditionRef.getChildRef();
			if (idToCondition.containsKey(conditionNodeRef.getId()) == false)
			{
				// Delete the condition
				this.nodeService.removeChild(actionNodeRef, conditionNodeRef);
			}
			else
			{
				// Update the conditions parameters
				saveParameters(conditionNodeRef, idToCondition.get(conditionNodeRef.getId()));
				idToCondition.remove(conditionNodeRef.getId());
			}
			
		}
		
		// Create the conditions remaining
		for (Map.Entry<String, ActionCondition> entry : idToCondition.entrySet())
		{
			Map<QName, Serializable> props = new HashMap<QName, Serializable>(2);
			props.put(ContentModel.PROP_DEFINITION_NAME, entry.getValue().getActionConditionDefinitionName());
			props.put(ContentModel.PROP_NODE_UUID, entry.getValue().getId());
			
			NodeRef conditionNodeRef = this.nodeService.createNode(
					actionNodeRef,
					ContentModel.ASSOC_CONDITIONS,
					ContentModel.ASSOC_CONDITIONS,
					ContentModel.TYPE_ACTION_CONDITION,
					props).getChildRef();
			
			saveParameters(conditionNodeRef, entry.getValue());
		}		
	}

	/**
	 * Saves the parameters associated with an action or condition
	 * 
	 * @param parameterizedNodeRef	the parameterized item node reference
	 * @param item					the parameterized item
	 */
	private void saveParameters(NodeRef parameterizedNodeRef, ParameterizedItem item)
	{
		Map<String, Serializable> parameterMap = new HashMap<String, Serializable>();
		parameterMap.putAll(item.getParameterValues());
		
		List<ChildAssociationRef> parameters = this.nodeService.getChildAssocs(parameterizedNodeRef, ContentModel.ASSOC_PARAMETERS);
		for (ChildAssociationRef ref : parameters)
		{
			NodeRef paramNodeRef = ref.getChildRef();
			Map<QName, Serializable> nodeRefParameterMap = this.nodeService.getProperties(paramNodeRef);
			String paramName = (String)nodeRefParameterMap.get(ContentModel.PROP_PARAMETER_NAME);
			if (parameterMap.containsKey(paramName) == false)
			{
				// Delete parameter from node ref
				this.nodeService.removeChild(parameterizedNodeRef, paramNodeRef);				
			}
			else
			{
				// Update the parameter value
				nodeRefParameterMap.put(ContentModel.PROP_PARAMETER_VALUE, parameterMap.get(paramName));
				this.nodeService.setProperties(paramNodeRef, nodeRefParameterMap);
				parameterMap.remove(paramName);
			}
		}
		
		// Add any remaing parameters
		for (Map.Entry<String, Serializable> entry : parameterMap.entrySet())
		{
			Map<QName, Serializable> nodeRefProperties = new HashMap<QName, Serializable>(2);
			nodeRefProperties.put(ContentModel.PROP_PARAMETER_NAME, entry.getKey());
			nodeRefProperties.put(ContentModel.PROP_PARAMETER_VALUE, entry.getValue());
			
			this.nodeService.createNode(
					parameterizedNodeRef,
					ContentModel.ASSOC_PARAMETERS,
					ContentModel.ASSOC_PARAMETERS,
					ContentModel.TYPE_ACTION_PARAMETER,
					nodeRefProperties);
		}
	}

	/**
	 * Gets the folder where the actions are stored
	 * 
	 * @param nodeRef	the node reference
	 * @return			the action folder node reference
	 */
	private NodeRef getActionFolder(NodeRef nodeRef)
	{
		// First check whether the node is configurable
		if (this.configurableService.isConfigurable(nodeRef) == false)
		{
			this.configurableService.makeConfigurable(nodeRef);
		}
		
		// Get the configurable folder and check whether the action folder is there
		NodeRef actionFolder = null;
		NodeRef configFolder = this.configurableService.getConfigurationFolder(nodeRef);
		List<ChildAssociationRef> children = this.nodeService.getChildAssocs(configFolder, ASSOC_NAME_ACTION_FOLDER);
		if (children.size() == 0)
		{
			// Add the actions folder to the configurable folder
			actionFolder = this.nodeService.createNode(
					configFolder, 
					ContentModel.ASSOC_CONTAINS, 
					ASSOC_NAME_ACTION_FOLDER, 
					ContentModel.TYPE_SYSTEM_FOLDER).getChildRef();
		}
		else
		{
			// Get the existing action folder
			actionFolder = children.get(0).getChildRef();
		}
		
		return actionFolder;
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#getActions(org.alfresco.service.cmr.repository.NodeRef)
	 */
	public List<Action> getActions(NodeRef nodeRef)
	{
		List<Action> result = new ArrayList<Action>();
		
		if (this.nodeService.exists(nodeRef) == true &&
			this.configurableService.isConfigurable(nodeRef) == true)
		{
			NodeRef actionFolder = getActionFolder(nodeRef);
			if (actionFolder != null)
			{
				List<ChildAssociationRef> actions = this.nodeService.getChildAssocs(actionFolder, ASSOC_NAME_ACTIONS);
				for (ChildAssociationRef action : actions)
				{
					NodeRef actionNodeRef = action.getChildRef();
					result.add(createAction(actionNodeRef));
				}
			}
		}
		
		return result;
	}

	/**
	 * Create an action from the action node reference
	 * 
	 * @param actionNodeRef		the action node reference
	 * @return					the action
	 */
	private Action createAction(NodeRef actionNodeRef)
	{
		Action result = null;
		
		Map<QName, Serializable> properties = this.nodeService.getProperties(actionNodeRef);
		
		QName actionType = this.nodeService.getType(actionNodeRef);
		if (ContentModel.TYPE_COMPOSITE_ACTION.equals(actionType) == true)
		{
			// Create a composite action
			result = new CompositeActionImpl(actionNodeRef.getId());
			populateCompositeAction(actionNodeRef, (CompositeAction)result);
		}
		else
		{
			// Create an action
			result = new ActionImpl(actionNodeRef.getId(), (String)properties.get(ContentModel.PROP_DEFINITION_NAME));
			populateAction(actionNodeRef, result);
		}
		
		return result;
	}

	/**
	 * Populate the details of the action from the node reference
	 * 
	 * @param actionNodeRef		the action node reference
	 * @param action			the action
	 */
	private void populateAction(NodeRef actionNodeRef, Action action)
	{
		// Set the parameters
		populateParameters(actionNodeRef, action);
		
		// Set the conditions
		List<ChildAssociationRef> conditions = this.nodeService.getChildAssocs(actionNodeRef, ContentModel.ASSOC_CONDITIONS);
		for (ChildAssociationRef condition : conditions)
		{
			NodeRef conditionNodeRef = condition.getChildRef();
			action.addActionCondition(createActionCondition(conditionNodeRef));
		}
	}

	/**
	 * Populate the parameteres of a parameterized item from the parameterized item node reference
	 * 
	 * @param parameterizedItemNodeRef	the parameterized item node reference
	 * @param parameterizedItem			the parameterized item
	 */
	private void populateParameters(NodeRef parameterizedItemNodeRef, ParameterizedItem parameterizedItem)
	{
		List<ChildAssociationRef> parameters = this.nodeService.getChildAssocs(parameterizedItemNodeRef, ContentModel.ASSOC_PARAMETERS);
		for (ChildAssociationRef parameter : parameters)
		{
			NodeRef parameterNodeRef = parameter.getChildRef();
			Map<QName, Serializable> properties = this.nodeService.getProperties(parameterNodeRef);
			parameterizedItem.setParameterValue(
					(String)properties.get(ContentModel.PROP_PARAMETER_NAME),
					properties.get(ContentModel.PROP_PARAMETER_VALUE));
		}
	}
	
	/**
	 * Creates an action condition from an action condition node reference
	 * 
	 * @param conditionNodeRef	the condition node reference
	 * @return					the action condition
	 */
	private ActionCondition createActionCondition(NodeRef conditionNodeRef)
	{
		Map<QName, Serializable> properties = this.nodeService.getProperties(conditionNodeRef);
		ActionCondition condition = new ActionConditionImpl(conditionNodeRef.getId(), (String)properties.get(ContentModel.PROP_DEFINITION_NAME));
		populateParameters(conditionNodeRef, condition);
		return condition;
	}

	/**
	 * Populates a composite action from a composite action node reference
	 * 
	 * @param compositeNodeRef	the composite action node reference
	 * @param compositeAction	the composite action
	 */
	private void populateCompositeAction(NodeRef compositeNodeRef, CompositeAction compositeAction)
	{
		populateAction(compositeNodeRef, compositeAction);
		
		List<ChildAssociationRef> actions = this.nodeService.getChildAssocs(compositeNodeRef, ContentModel.ASSOC_ACTIONS);
		for (ChildAssociationRef action : actions)
		{
			NodeRef actionNodeRef = action.getChildRef();
			compositeAction.addAction(createAction(actionNodeRef));
		}		
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#getAction(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
	 */
	public Action getAction(NodeRef nodeRef, String actionId)
	{
		Action result = null;
		
		if (this.nodeService.exists(nodeRef) == true &&
			this.configurableService.isConfigurable(nodeRef) == true)
		{
			NodeRef actionNodeRef = new NodeRef(nodeRef.getStoreRef(), actionId);
			if (this.nodeService.exists(actionNodeRef) == true)
			{
				result = createAction(actionNodeRef);
			}
		}
		
		return result;
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#removeAction(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.action.Action)
	 */
	public void removeAction(NodeRef nodeRef, Action action)
	{
		if (this.nodeService.exists(nodeRef) == true &&
			this.configurableService.isConfigurable(nodeRef) == true)
		{
			NodeRef actionNodeRef = new NodeRef(nodeRef.getStoreRef(), action.getId());
			if (this.nodeService.exists(actionNodeRef) == true)
			{
				NodeRef actionFolder = getActionFolder(nodeRef);
				this.nodeService.removeChild(actionFolder, actionNodeRef);
			}
		}		
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#removeAllActions(org.alfresco.service.cmr.repository.NodeRef)
	 */
	public void removeAllActions(NodeRef nodeRef)
	{
		if (this.nodeService.exists(nodeRef) == true &&
			this.configurableService.isConfigurable(nodeRef) == true)
		{
			NodeRef actionFolder = getActionFolder(nodeRef);
			List<ChildAssociationRef> actions = new ArrayList<ChildAssociationRef>(this.nodeService.getChildAssocs(actionFolder, ASSOC_NAME_ACTIONS));
			for (ChildAssociationRef action : actions)
			{
				this.nodeService.removeChild(actionFolder, action.getChildRef());
			}
		}		
	}
}
