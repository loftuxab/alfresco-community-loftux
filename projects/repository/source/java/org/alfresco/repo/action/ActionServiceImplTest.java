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

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.evaluator.MatchTextEvaluator;
import org.alfresco.repo.action.evaluator.NoConditionEvaluator;
import org.alfresco.repo.action.executer.AddFeaturesActionExecuter;
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
	
}
