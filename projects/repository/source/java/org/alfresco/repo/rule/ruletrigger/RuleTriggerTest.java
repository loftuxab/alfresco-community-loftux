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
package org.alfresco.repo.rule.ruletrigger;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;

/**
 * Rule trigger test
 * 
 * @author Roy Wetherall
 */
public class RuleTriggerTest extends BaseSpringTest
{
	private static final String ON_CREATE_NODE_TRIGGER = "on-create-node-trigger";
	private static final String ON_UPDATE_NODE_TRIGGER = "on-update-node-trigger";
	private static final String ON_DELETE_NODE_TRIGGER = "on-delete-node-trigger";
	private static final String ON_CREATE_CHILD_ASSOCIATION_TRIGGER = "on-create-child-association-trigger";
	private static final String ON_DELETE_CHILD_ASSOCIATION_TRIGGER = "on-delete-child-association-trigger";
	private static final String ON_CREATE_ASSOCIATION_TRIGGER = "on-create-association-trigger";
	private static final String ON_DELETE_ASSOCIATION_TRIGGER = "on-delete-association-trigger";
	private static final String ON_CONTENT_UPDATE_TRIGGER = "on-content-update-trigger";

	private NodeService nodeService;
	private ContentService contentService;
	
	private StoreRef testStoreRef;
	private NodeRef rootNodeRef;
	
	@Override
	protected void onSetUpInTransaction() throws Exception
	{
		this.nodeService = (NodeService)this.applicationContext.getBean("nodeService");
		this.contentService = (ContentService)this.applicationContext.getBean("contentService");
		
		this.testStoreRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        this.rootNodeRef = this.nodeService.getRootNode(this.testStoreRef);
	}
	
	public void testOnCreateNodeTrigger()
	{
		TestRuleType ruleType = createTestRuleType(ON_CREATE_NODE_TRIGGER);
		assertFalse(ruleType.rulesTriggered);
		
		// Try and trigger the type
        this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER);
		
		// Check to see if the rule type has been triggered
        assertTrue(ruleType.rulesTriggered);
	}
	
	public void testOnUpdateNodeTrigger()
	{
		NodeRef nodeRef = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER).getChildRef();
		
		TestRuleType ruleType = createTestRuleType(ON_UPDATE_NODE_TRIGGER);
		assertFalse(ruleType.rulesTriggered);
		
		// Try and trigger the type
		this.nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, "nameChanged");
		
		// Check to see if the rule type has been triggered
        assertTrue(ruleType.rulesTriggered);		
	}
	
	public void testOnDeleteNodeTrigger()
	{
		NodeRef nodeRef = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER).getChildRef();
		
		TestRuleType ruleType = createTestRuleType(ON_DELETE_NODE_TRIGGER);
		assertFalse(ruleType.rulesTriggered);
		
		// Try and trigger the type
		this.nodeService.deleteNode(nodeRef);
		
		// Check to see if the rule type has been triggered
        assertTrue(ruleType.rulesTriggered);		
	}
	
	public void testOnCreateChildAssociationTrigger()
	{
		NodeRef nodeRef = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER).getChildRef();
		NodeRef nodeRef2 = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER).getChildRef();
		
		TestRuleType ruleType = createTestRuleType(ON_CREATE_CHILD_ASSOCIATION_TRIGGER);
		assertFalse(ruleType.rulesTriggered);
		
		// Try and trigger the type
		this.nodeService.addChild(
				nodeRef, 
				nodeRef2,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"));
		
		// Check to see if the rule type has been triggered
        assertTrue(ruleType.rulesTriggered);		
	}
	
	public void testOnDeleteChildAssociationTrigger()
	{
		NodeRef nodeRef = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER).getChildRef();
		NodeRef nodeRef2 = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER).getChildRef();
		this.nodeService.addChild(
				nodeRef, 
				nodeRef2,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"));
		
		TestRuleType ruleType = createTestRuleType(ON_DELETE_CHILD_ASSOCIATION_TRIGGER);
		assertFalse(ruleType.rulesTriggered);
		
		// Try and trigger the type
		this.nodeService.removeChild(nodeRef, nodeRef2);
		
		// Check to see if the rule type has been triggered
        assertTrue(ruleType.rulesTriggered);		
	}
	
	public void testOnCreateAssociationTrigger()
	{
		NodeRef nodeRef = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER).getChildRef();
		NodeRef nodeRef2 = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER).getChildRef();
		
		TestRuleType ruleType = createTestRuleType(ON_CREATE_ASSOCIATION_TRIGGER);
		assertFalse(ruleType.rulesTriggered);
		
		// Try and trigger the type
		this.nodeService.createAssociation(nodeRef, nodeRef2, QName.createQName(NamespaceService.ALFRESCO_URI, "children"));
		
		// Check to see if the rule type has been triggered
        assertTrue(ruleType.rulesTriggered);		
	}
	
	public void testOnDeleteAssociationTrigger()
	{
		NodeRef nodeRef = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER).getChildRef();
		NodeRef nodeRef2 = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER).getChildRef();
		this.nodeService.createAssociation(nodeRef, nodeRef2, QName.createQName(NamespaceService.ALFRESCO_URI, "children"));
		
		TestRuleType ruleType = createTestRuleType(ON_DELETE_ASSOCIATION_TRIGGER);
		assertFalse(ruleType.rulesTriggered);
		
		// Try and trigger the type
		this.nodeService.removeAssociation(nodeRef, nodeRef2, QName.createQName(NamespaceService.ALFRESCO_URI, "children"));
		
		// Check to see if the rule type has been triggered
        assertTrue(ruleType.rulesTriggered);		
	}
	
	public void testOnContentUpdateTrigger()
	{
		NodeRef nodeRef = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT).getChildRef();
		
		TestRuleType ruleType = createTestRuleType(ON_CONTENT_UPDATE_TRIGGER);
		assertFalse(ruleType.rulesTriggered);
		
		// Try and trigger the type
		ContentWriter contentWriter = this.contentService.getUpdatingWriter(nodeRef);
		contentWriter.putContent("some content");
		
		// Check to see if the rule type has been triggered
        assertTrue(ruleType.rulesTriggered);		
	}
	
	private TestRuleType createTestRuleType(String ruleTriggerName)
	{
		RuleTrigger ruleTrigger = (RuleTrigger)this.applicationContext.getBean(ruleTriggerName);
		assertNotNull(ruleTrigger);
		TestRuleType ruleType = new TestRuleType();
		ruleTrigger.registerRuleType(ruleType);
		return ruleType;
	}
	
	private class TestRuleType implements RuleType
	{
		public boolean rulesTriggered = false;

		public String getName()
		{
			return "testRuleType";
		}

		public String getDisplayLabel()
		{
			return "displayLabel";
		}

		public void triggerRuleType(NodeRef nodeRef, NodeRef actionedUponNodeRef)
		{
			// Indicate that the rules have been triggered
			this.rulesTriggered = true;
		}
	}
}
