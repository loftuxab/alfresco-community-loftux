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
import java.util.Date;
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
import org.alfresco.service.cmr.action.ActionServiceException;
import org.alfresco.service.cmr.action.CompositeAction;
import org.alfresco.service.cmr.action.ParameterizedItem;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
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
public class ActionServiceImpl implements ActionService, RuntimeActionService, ApplicationContextAware
{ 
	/**
	 * The application context
	 */
	private ApplicationContext applicationContext;
	
	/**
	 * The node service
	 */
	private NodeService nodeService;
	
	/**
	 * The search service
	 */
	private SearchService searchService;
	
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
	 * Set the search service
	 * 
	 * @param searchService  the search service
	 */
	public void setSearchService(SearchService searchService)
	{
		this.searchService = searchService;
	}
	
	/**
	 * Gets the saved action folder reference
	 * 
	 * @param nodeRef	the node reference
	 * @return			the node reference
	 */
	private NodeRef getSavedActionFolderRef(NodeRef nodeRef)
	{
		List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(nodeRef, ActionableAspect.ASSOC_NAME_SAVEDACTIONFOLDER);
		if (assocs.size() != 1)
		{
			throw new ActionServiceException("Unable to retrieve the saved action folder reference.");
		}
		
		return assocs.get(0).getChildRef();
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
	 * @see org.alfresco.service.cmr.action.ActionService#createActionCondition(java.lang.String, java.util.Map)
	 */
	public ActionCondition createActionCondition(String name, Map<String, Serializable> params)
	{
		ActionCondition condition = createActionCondition(name);
		condition.setParameterValues(params);
		return condition;
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#createAction()
	 */
	public Action createAction(String name)
	{
		return new ActionImpl(GUID.generate(),name);
	}
	
	/**
	 * @see org.alfresco.service.cmr.action.ActionService#createAction(java.lang.String, java.util.Map)
	 */
	public Action createAction(String name, Map<String, Serializable> params)
	{
		Action action = createAction(name);
		action.setParameterValues(params);
		return action;
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
					// TODO should we check each individual actions conditions and only potentially
					// execute some of them .. not for now
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
	 * @see org.alfresco.repo.action.RuntimeActionService#registerActionConditionEvaluator(org.alfresco.repo.action.evaluator.ActionConditionEvaluator)
	 */
	public void registerActionConditionEvaluator(ActionConditionEvaluator actionConditionEvaluator) 
	{
		ActionConditionDefinition cond = actionConditionEvaluator.getRuleConditionDefintion();
		this.conditionDefinitions.put(cond.getName(), cond);
	}

	/**
	 * @see org.alfresco.repo.action.RuntimeActionService#registerActionExecuter(org.alfresco.repo.action.executer.ActionExecuter)
	 */
	public void registerActionExecuter(ActionExecuter actionExecuter) 
	{
		ActionDefinition action = actionExecuter.getRuleActionDefinition();
		this.actionDefinitions.put(action.getName(), action);
	}
	
	/**
	 * Gets the action node ref from the action id
	 * 
	 * @param nodeRef	the node reference
	 * @param actionId	the acition id
	 * @return			the action node reference
	 */
	private NodeRef getActionNodeRefFromId(NodeRef nodeRef, String actionId)
	{
		NodeRef result = null;
		
		if (this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == true)
		{
			DynamicNamespacePrefixResolver namespacePrefixResolver = new DynamicNamespacePrefixResolver();
			namespacePrefixResolver.addDynamicNamespace(NamespaceService.SYSTEM_MODEL_PREFIX, NamespaceService.SYSTEM_MODEL_1_0_URI);
			
			List<NodeRef> nodeRefs = searchService.selectNodes(
					getSavedActionFolderRef(nodeRef),
					"*[@sys:" + ContentModel.PROP_NODE_UUID.getLocalName() + "='" + actionId + "']",
					null,
					namespacePrefixResolver,
					false);
			if (nodeRefs.size() != 0)
			{
				result = nodeRefs.get(0);
			}
		}
		
		return result;
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#saveAction(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.action.Action)
	 */
	public void saveAction(NodeRef nodeRef, Action action)
	{
		NodeRef actionNodeRef = getActionNodeRefFromId(nodeRef, action.getId());
		if (actionNodeRef == null)
		{		
			if (this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == false)
			{
				// Apply the actionable aspect
				this.nodeService.addAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE, null);
			}
				
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
					getSavedActionFolderRef(nodeRef),
					ContentModel.ASSOC_SAVED_ACTIONS,
					ContentModel.ASSOC_SAVED_ACTIONS,
					actionType,
					props).getChildRef();
			
			// Update the created details
			((ActionImpl)action).setCreator((String)this.nodeService.getProperty(actionNodeRef, ContentModel.PROP_CREATOR));
			((ActionImpl)action).setCreatedDate((Date)this.nodeService.getProperty(actionNodeRef, ContentModel.PROP_CREATED));
		}
		
		saveActionImpl(actionNodeRef, action);
	}
	
	/**
	 * @see org.alfresco.repo.action.RuntimeActionService#saveActionImpl(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.action.Action)
	 */
	public void saveActionImpl(NodeRef actionNodeRef, Action action)
	{
		// Save action properties
		saveActionProperties(actionNodeRef, action);
		
		// Update the parameters of the action
		saveParameters(actionNodeRef, action);
		
		// Update the conditions of the action
		saveConditions(actionNodeRef, action);
		
		if (action instanceof CompositeAction)
		{
			// Update composite action
			saveActions(actionNodeRef, (CompositeAction)action);
		}
		
		// Update the modified details
		((ActionImpl)action).setModifier((String)this.nodeService.getProperty(actionNodeRef, ContentModel.PROP_MODIFIER));
		((ActionImpl)action).setModifiedDate((Date)this.nodeService.getProperty(actionNodeRef, ContentModel.PROP_MODIFIED));
	}

	/**
	 * Save the action property values
	 * 
	 * @param actionNodeRef	the action node reference
	 * @param action		the action
	 */
	private void saveActionProperties(NodeRef actionNodeRef, Action action)
	{
		// Update the action property values
		Map<QName, Serializable> props = this.nodeService.getProperties(actionNodeRef);
		props.put(ContentModel.PROP_ACTION_TITLE, action.getTitle());
		props.put(ContentModel.PROP_ACTION_DESCRIPTION, action.getDescription());
		this.nodeService.setProperties(actionNodeRef, props);
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
				saveActionImpl(actionNodeRef, idToAction.get(actionNodeRef.getId()));
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
			
			saveActionImpl(actionNodeRef, entry.getValue());
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
	 * @see org.alfresco.service.cmr.action.ActionService#getActions(org.alfresco.service.cmr.repository.NodeRef)
	 */
	public List<Action> getActions(NodeRef nodeRef)
	{
		List<Action> result = new ArrayList<Action>();
		
		if (this.nodeService.exists(nodeRef) == true &&
			this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == true)
		{
			List<ChildAssociationRef> actions = this.nodeService.getChildAssocs(getSavedActionFolderRef(nodeRef), ContentModel.ASSOC_SAVED_ACTIONS);
			for (ChildAssociationRef action : actions)
			{
				NodeRef actionNodeRef = action.getChildRef();
				result.add(createAction(actionNodeRef));
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
		// Populate the action properties
		populateActionProperties(actionNodeRef, action);
		
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
	 * Populates the action properties from the node reference
	 * 	
	 * @param actionNodeRef	the action node reference
	 * @param action		the action
	 */
	private void populateActionProperties(NodeRef actionNodeRef, Action action)
	{
		Map<QName, Serializable> props = this.nodeService.getProperties(actionNodeRef);
		action.setTitle((String)props.get(ContentModel.PROP_ACTION_TITLE));
		action.setDescription((String)props.get(ContentModel.PROP_ACTION_DESCRIPTION));
		((ActionImpl)action).setCreator((String)props.get(ContentModel.PROP_CREATOR));
		((ActionImpl)action).setCreatedDate((Date)props.get(ContentModel.PROP_CREATED));
		((ActionImpl)action).setModifier((String)props.get(ContentModel.PROP_MODIFIER));
		((ActionImpl)action).setModifiedDate((Date)props.get(ContentModel.PROP_MODIFIED));
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
	public void populateCompositeAction(NodeRef compositeNodeRef, CompositeAction compositeAction)
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
			this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == true)
		{
			NodeRef actionNodeRef = getActionNodeRefFromId(nodeRef, actionId);
			if (actionNodeRef != null)
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
			this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == true)
		{
			NodeRef actionNodeRef = getActionNodeRefFromId(nodeRef, action.getId());
			if (actionNodeRef != null)
			{
				this.nodeService.removeChild(getSavedActionFolderRef(nodeRef), actionNodeRef);
			}
		}		
	}

	/**
	 * @see org.alfresco.service.cmr.action.ActionService#removeAllActions(org.alfresco.service.cmr.repository.NodeRef)
	 */
	public void removeAllActions(NodeRef nodeRef)
	{
		if (this.nodeService.exists(nodeRef) == true &&
			this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == true)
		{
			List<ChildAssociationRef> actions = new ArrayList<ChildAssociationRef>(this.nodeService.getChildAssocs(getSavedActionFolderRef(nodeRef), ContentModel.ASSOC_SAVED_ACTIONS));
			for (ChildAssociationRef action : actions)
			{
				this.nodeService.removeChild(getSavedActionFolderRef(nodeRef), action.getChildRef());
			}
		}		
	}
}
