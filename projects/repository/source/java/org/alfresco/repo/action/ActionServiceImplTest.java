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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.evaluator.InCategoryEvaluator;
import org.alfresco.repo.action.evaluator.MatchTextEvaluator;
import org.alfresco.repo.action.evaluator.NoConditionEvaluator;
import org.alfresco.repo.action.executer.AddFeaturesActionExecuter;
import org.alfresco.repo.action.executer.CheckInActionExecuter;
import org.alfresco.repo.action.executer.CheckOutActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.action.ActionConditionDefinition;
import org.alfresco.service.cmr.action.ActionDefinition;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.action.CompositeAction;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;

/**
 * Action service test
 * 
 * @author Roy Wetherall
 */
public class ActionServiceImplTest extends BaseSpringTest
{
	private static final String BAD_NAME = "badName";
	
	private NodeService nodeService;
	private ActionService actionService;
	private StoreRef testStoreRef;
	private NodeRef rootNodeRef;
	private NodeRef nodeRef;
	
	@Override
	protected void onSetUpInTransaction() throws Exception
	{
		this.nodeService = (NodeService)this.applicationContext.getBean("nodeService");
		this.actionService = (ActionService)this.applicationContext.getBean("actionService");
		
        // Create the store and get the root node
        this.testStoreRef = this.nodeService.createStore(
                StoreRef.PROTOCOL_WORKSPACE, "Test_"
                        + System.currentTimeMillis());
        this.rootNodeRef = this.nodeService.getRootNode(this.testStoreRef);

        // Create the node used for tests
        this.nodeRef = this.nodeService.createNode(
        		this.rootNodeRef,
                ContentModel.ASSOC_CHILDREN,
                QName.createQName("{test}testnode"),
                ContentModel.TYPE_CONTENT).getChildRef();
	}
	
	/**
	 * Test getActionDefinition
	 */
	public void testGetActionDefinition()
	{
		ActionDefinition action = this.actionService.getActionDefinition(AddFeaturesActionExecuter.NAME);
		assertNotNull(action);
		assertEquals(AddFeaturesActionExecuter.NAME, action.getName());
		
		ActionConditionDefinition nullCondition = this.actionService.getActionConditionDefinition(BAD_NAME);
		assertNull(nullCondition);		
	}

	/**
	 * Test getActionDefintions
	 */
	public void testGetActionDefinitions()
	{
		List<ActionDefinition> defintions = this.actionService.getActionDefinitions();
		assertNotNull(defintions);
		assertFalse(defintions.isEmpty());
	}	

	/**
	 * Test getActionConditionDefinition
	 */
	public void testGetActionConditionDefinition()
	{
		ActionConditionDefinition condition = this.actionService.getActionConditionDefinition(NoConditionEvaluator.NAME);
		assertNotNull(condition);
		assertEquals(NoConditionEvaluator.NAME, condition.getName());
		
		ActionConditionDefinition nullCondition = this.actionService.getActionConditionDefinition(BAD_NAME);
		assertNull(nullCondition);	
	}

	/**
	 * Test getActionConditionDefinitions
	 *
	 */
	public void testGetActionConditionDefinitions()
	{
		List<ActionConditionDefinition> defintions = this.actionService.getActionConditionDefinitions();
		assertNotNull(defintions);
		assertFalse(defintions.isEmpty());
	}

	/**
	 * Test create action condition
	 */
	public void testCreateActionCondition()
	{
		ActionCondition condition = this.actionService.createActionCondition(NoConditionEvaluator.NAME);
		assertNotNull(condition);
		assertEquals(NoConditionEvaluator.NAME, condition.getActionConditionDefinitionName());
	}

	/**
	 * Test createAction
	 */
	public void testCreateAction()
	{
		Action action = this.actionService.createAction(AddFeaturesActionExecuter.NAME);
		assertNotNull(action);
		assertEquals(AddFeaturesActionExecuter.NAME, action.getActionDefinitionName());
	}

	/**
	 * Test createCompositeAction
	 */
	public void testCreateCompositeAction()
	{
		CompositeAction action = this.actionService.createCompositeAction();
		assertNotNull(action);
		assertEquals(CompositeActionImpl.COMPOSITE_ACTION, action.getActionDefinitionName());
	}

	/**
	 * Evaluate action
	 */
	public void testEvaluateAction()
	{
		Action action = this.actionService.createAction(AddFeaturesActionExecuter.NAME);
		assertTrue(this.actionService.evaluateAction(action, this.nodeRef));
		
		ActionCondition condition = this.actionService.createActionCondition(MatchTextEvaluator.NAME);
		condition.setParameterValue(MatchTextEvaluator.PARAM_TEXT, "*.doc");
		action.addActionCondition(condition);
		
		assertFalse(this.actionService.evaluateAction(action, this.nodeRef));
		this.nodeService.setProperty(this.nodeRef, ContentModel.PROP_NAME, "myDocument.doc");
		assertTrue(this.actionService.evaluateAction(action, this.nodeRef));
		
		ActionCondition condition2 = this.actionService.createActionCondition(MatchTextEvaluator.NAME);
		condition2.setParameterValue(MatchTextEvaluator.PARAM_TEXT, "my");
		action.addActionCondition(condition2);
		assertTrue(this.actionService.evaluateAction(action, this.nodeRef));
		
		this.nodeService.setProperty(this.nodeRef, ContentModel.PROP_NAME, "document.doc");
		assertFalse(this.actionService.evaluateAction(action, this.nodeRef));
	}
	
	/**
	 * Test evaluate action condition
	 */
	public void testEvaluateActionCondition()
	{
		ActionCondition condition = this.actionService.createActionCondition(MatchTextEvaluator.NAME);
		condition.setParameterValue(MatchTextEvaluator.PARAM_TEXT, "*.doc");
		
		assertFalse(this.actionService.evaluateActionCondition(condition, this.nodeRef));
		this.nodeService.setProperty(this.nodeRef, ContentModel.PROP_NAME, "myDocument.doc");
		assertTrue(this.actionService.evaluateActionCondition(condition, this.nodeRef));
	}
	
	/**
	 * Test execute action
	 */
	public void testExecuteAction()
	{
		assertFalse(this.nodeService.hasAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE));
		
		Action action = this.actionService.createAction(AddFeaturesActionExecuter.NAME);
		action.setParameterValue(AddFeaturesActionExecuter.PARAM_ASPECT_NAME, ContentModel.ASPECT_VERSIONABLE);
		
		this.actionService.executeAction(action, this.nodeRef);
		assertTrue(this.nodeService.hasAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE));
		
		this.nodeService.removeAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE);
		assertFalse(this.nodeService.hasAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE));
		
		ActionCondition condition = this.actionService.createActionCondition(MatchTextEvaluator.NAME);
		condition.setParameterValue(MatchTextEvaluator.PARAM_TEXT, "*.doc");
		action.addActionCondition(condition);
				
		this.actionService.executeAction(action, this.nodeRef);
		assertFalse(this.nodeService.hasAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE));
		
		this.actionService.executeAction(action, this.nodeRef, true);
		assertFalse(this.nodeService.hasAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE));
		
		this.actionService.executeAction(action, this.nodeRef, false);
		assertTrue(this.nodeService.hasAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE));
		
		this.nodeService.removeAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE);
		assertFalse(this.nodeService.hasAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE));
		
		this.nodeService.setProperty(this.nodeRef, ContentModel.PROP_NAME, "myDocument.doc");
		this.actionService.executeAction(action, this.nodeRef);
		assertTrue(this.nodeService.hasAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE));
		
		this.nodeService.removeAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE);
		assertFalse(this.nodeService.hasAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE));
		
		// Exceute composite action
		// TODO
	}	
	
	public void testGetAndGetAllWithNoActions()
	{
		assertNull(this.actionService.getAction(this.nodeRef, AddFeaturesActionExecuter.NAME));
		List<Action> actions = this.actionService.getActions(this.nodeRef);
		assertNotNull(actions);
		assertEquals(0, actions.size());
	}
	
	public void testGetAll()
	{
		
	}
	
	/**
	 * Test saving an action with no conditions
	 */
	public void testSaveActionNoCondition()
	{
		// TODO check the audiatble properties of the action
		
		// Create the action
		Action action = this.actionService.createAction(AddFeaturesActionExecuter.NAME);
		String actionId = action.getId();
		
		// Set the parameters of the action
		action.setParameterValue(AddFeaturesActionExecuter.PARAM_ASPECT_NAME, ContentModel.ASPECT_VERSIONABLE);
		
		// Set the title and description of the action
		action.setTitle("title");
		action.setDescription("description");
				
		// Save the action
		this.actionService.saveAction(this.nodeRef, action);
		
		// Get the action
		Action savedAction = this.actionService.getAction(this.nodeRef, actionId);
		
		// Check the action 
		assertEquals(action.getId(), savedAction.getId());
		assertEquals(action.getActionDefinitionName(), savedAction.getActionDefinitionName());
		
		// Check the title and the description
		assertEquals("title", savedAction.getTitle());
		assertEquals("description", savedAction.getDescription());
		
		// Check the properties
		assertEquals(1, savedAction.getParameterValues().size());
		assertEquals(ContentModel.ASPECT_VERSIONABLE, savedAction.getParameterValue(AddFeaturesActionExecuter.PARAM_ASPECT_NAME));
				
		// Check the conditions
		assertNotNull(savedAction.getActionConditions());
		assertEquals(0, savedAction.getActionConditions().size());
		
		// Edit the properties of the action		
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
		properties.put(ContentModel.PROP_NAME, "testName");
		action.setParameterValue(AddFeaturesActionExecuter.PARAM_ASPECT_PROPERTIES, (Serializable)properties);
		action.setParameterValue(AddFeaturesActionExecuter.PARAM_ASPECT_NAME, ContentModel.ASPECT_AUDITABLE);
		
		this.actionService.saveAction(this.nodeRef, action);
		Action savedAction2 = this.actionService.getAction(this.nodeRef, actionId);
		
		// Check the updated properties
		assertEquals(2, savedAction2.getParameterValues().size());
		assertEquals(ContentModel.ASPECT_AUDITABLE, savedAction2.getParameterValue(AddFeaturesActionExecuter.PARAM_ASPECT_NAME));
		Map<QName, Serializable> temp = (Map<QName, Serializable>)savedAction2.getParameterValue(AddFeaturesActionExecuter.PARAM_ASPECT_PROPERTIES);
		assertNotNull(temp);
		assertEquals(1, temp.size());
		assertEquals("testName", temp.get(ContentModel.PROP_NAME));
		
		//System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
	}

	/**
	 * Test saving an action with conditions
	 */
	public void testSaveActionWithConditions()
	{
		// Create the action
		Action action = this.actionService.createAction(AddFeaturesActionExecuter.NAME);
		String actionId = action.getId();
		
		// Set the parameters of the action
		action.setParameterValue(AddFeaturesActionExecuter.PARAM_ASPECT_NAME, ContentModel.ASPECT_VERSIONABLE);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
		properties.put(ContentModel.PROP_NAME, "testName");
		action.setParameterValue(AddFeaturesActionExecuter.PARAM_ASPECT_PROPERTIES, (Serializable)properties);
		
		// Set the conditions of the action
		ActionCondition actionCondition = this.actionService.createActionCondition(NoConditionEvaluator.NAME);
		ActionCondition actionCondition2 = this.actionService.createActionCondition(MatchTextEvaluator.NAME);
		actionCondition2.setParameterValue(MatchTextEvaluator.PARAM_TEXT, "*.doc");
		action.addActionCondition(actionCondition);
		action.addActionCondition(actionCondition2);
		
		// Save the action
		this.actionService.saveAction(this.nodeRef, action);
		
		// Get the action
		Action savedAction = this.actionService.getAction(this.nodeRef, actionId);
		
		// Check the action 
		assertEquals(action.getId(), savedAction.getId());
		assertEquals(action.getActionDefinitionName(), savedAction.getActionDefinitionName());
		
		// Check the properties
		assertEquals(action.getParameterValues().size(), savedAction.getParameterValues().size());
		assertEquals(ContentModel.ASPECT_VERSIONABLE, savedAction.getParameterValue(AddFeaturesActionExecuter.PARAM_ASPECT_NAME));
		Map<QName, Serializable> temp = (Map<QName, Serializable>)savedAction.getParameterValue(AddFeaturesActionExecuter.PARAM_ASPECT_PROPERTIES);
		assertNotNull(temp);
		assertEquals(1, temp.size());
		assertEquals("testName", temp.get(ContentModel.PROP_NAME));
		
		// Check the conditions
		assertNotNull(savedAction.getActionConditions());
		assertEquals(2, savedAction.getActionConditions().size());
		for (ActionCondition savedCondition : savedAction.getActionConditions())
		{
			if (savedCondition.getActionConditionDefinitionName().equals(NoConditionEvaluator.NAME) == true)
			{
				assertEquals(0, savedCondition.getParameterValues().size());
			}
			else if (savedCondition.getActionConditionDefinitionName().equals(MatchTextEvaluator.NAME) == true)
			{
				assertEquals(1, savedCondition.getParameterValues().size());
				assertEquals("*.doc", savedCondition.getParameterValue(MatchTextEvaluator.PARAM_TEXT));
			}
			else
			{
				fail("There is a condition here that we are not expecting.");
			}
		}
		
		// Modify the conditions of the action
		ActionCondition actionCondition3 = this.actionService.createActionCondition(InCategoryEvaluator.NAME);
		actionCondition3.setParameterValue(InCategoryEvaluator.PARAM_CATEGORY_ASPECT, ContentModel.ASPECT_ACTIONABLE);
		action.addActionCondition(actionCondition3);
		action.removeActionCondition(actionCondition);
		actionCondition2.setParameterValue(MatchTextEvaluator.PARAM_TEXT, "*.exe");
		actionCondition2.setParameterValue(MatchTextEvaluator.PARAM_OPERATION, MatchTextEvaluator.Operation.EXACT);
		
		this.actionService.saveAction(this.nodeRef, action);
		Action savedAction2 = this.actionService.getAction(this.nodeRef, actionId);
		
		// Check that the conditions have been updated correctly
		assertNotNull(savedAction2.getActionConditions());
		assertEquals(2, savedAction2.getActionConditions().size());
		for (ActionCondition savedCondition : savedAction2.getActionConditions())
		{
			if (savedCondition.getActionConditionDefinitionName().equals(InCategoryEvaluator.NAME) == true)
			{
				assertEquals(1, savedCondition.getParameterValues().size());
				assertEquals(ContentModel.ASPECT_ACTIONABLE, savedCondition.getParameterValue(InCategoryEvaluator.PARAM_CATEGORY_ASPECT));
			}
			else if (savedCondition.getActionConditionDefinitionName().equals(MatchTextEvaluator.NAME) == true)
			{
				assertEquals(2, savedCondition.getParameterValues().size());
				assertEquals("*.exe", savedCondition.getParameterValue(MatchTextEvaluator.PARAM_TEXT));
				assertEquals(MatchTextEvaluator.Operation.EXACT, savedCondition.getParameterValue(MatchTextEvaluator.PARAM_OPERATION));
			}
			else
			{
				fail("There is a condition here that we are not expecting.");
			}
		}
		
		//System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
	}
	
	/**
	 * Test saving a composite action
	 */
	public void testSaveCompositeAction()
	{
		Action action1 = this.actionService.createAction(AddFeaturesActionExecuter.NAME);
		Action action2 = this.actionService.createAction(CheckInActionExecuter.NAME);
		
		CompositeAction compositeAction = this.actionService.createCompositeAction();
		String actionId = compositeAction.getId();
		compositeAction.addAction(action1);
		compositeAction.addAction(action2);
		
		this.actionService.saveAction(this.nodeRef, compositeAction);
		assertEquals(1, this.actionService.getActions(this.nodeRef).size());
		CompositeAction savedCompositeAction = (CompositeAction)this.actionService.getAction(this.nodeRef, actionId);
		
		// Check the saved composite action
		assertEquals(2, savedCompositeAction.getActions().size());
		for (Action action : savedCompositeAction.getActions())
		{
			if (action.getActionDefinitionName().equals(AddFeaturesActionExecuter.NAME) == true)
			{
				assertEquals(action, action1);
			}
			else if (action.getActionDefinitionName().equals(CheckInActionExecuter.NAME) == true)
			{
				assertEquals(action, action2);
			}
			else
			{
				fail("We have an action here we are not expecting.");
			}
		}
		
		// Change the actions and re-save
		compositeAction.removeAction(action1);
		Action action3 = this.actionService.createAction(CheckOutActionExecuter.NAME);
		compositeAction.addAction(action3);
		action2.setParameterValue(CheckInActionExecuter.PARAM_DESCRIPTION, "description");
		
		this.actionService.saveAction(this.nodeRef, compositeAction);
		assertEquals(1, this.actionService.getActions(this.nodeRef).size());
		CompositeAction savedCompositeAction2 = (CompositeAction)this.actionService.getAction(this.nodeRef, actionId);
		
		assertEquals(2, savedCompositeAction2.getActions().size());
		for (Action action : savedCompositeAction2.getActions())
		{
			if (action.getActionDefinitionName().equals(CheckOutActionExecuter.NAME) == true)
			{
				assertEquals(action, action3);
			}
			else if (action.getActionDefinitionName().equals(CheckInActionExecuter.NAME) == true)
			{
				assertEquals(action, action2);
				assertEquals("description", action2.getParameterValue(CheckInActionExecuter.PARAM_DESCRIPTION));
			}
			else
			{
				fail("We have an action here we are not expecting.");
			}
		}
	}
	
	/**
	 * Test remove action
	 */
	public void testRemove()
	{
		assertEquals(0, this.actionService.getActions(this.nodeRef).size());
		
		Action action1 = this.actionService.createAction(AddFeaturesActionExecuter.NAME);
		this.actionService.saveAction(this.nodeRef, action1);
		Action action2 = this.actionService.createAction(CheckInActionExecuter.NAME);
		this.actionService.saveAction(this.nodeRef, action2);		
		assertEquals(2, this.actionService.getActions(this.nodeRef).size());
		
		this.actionService.removeAction(this.nodeRef, action1);
		assertEquals(1, this.actionService.getActions(this.nodeRef).size());
		
		this.actionService.removeAllActions(this.nodeRef);
		assertEquals(0, this.actionService.getActions(this.nodeRef).size());		
	}
}
