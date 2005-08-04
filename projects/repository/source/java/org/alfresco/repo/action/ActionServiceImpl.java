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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.action.evaluator.ActionConditionEvaluator;
import org.alfresco.repo.action.executer.ActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.action.ActionConditionDefinition;
import org.alfresco.service.cmr.action.ActionDefinition;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.action.CompositeAction;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.GUID;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Roy Wetherall
 */
public class ActionServiceImpl implements ActionService, ActionRegistration, ApplicationContextAware
{
	/**
	 * The application context
	 */
	private ApplicationContext applicationContext;
	
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
}
