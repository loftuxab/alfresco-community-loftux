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
package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.rule.action.AddFeaturesActionExecutor;
import org.alfresco.repo.rule.common.RuleImpl;
import org.alfresco.repo.rule.condition.MatchTextEvaluator;
import org.alfresco.repo.rule.ruletype.InboundRuleTypeAdapter;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleActionDefinition;
import org.alfresco.service.cmr.rule.RuleCondition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;

/**
 * Base class for rule service test.
 * <p>
 * This file contains a number of helpers to reduce the duplication in tests.
 * 
 * @author Roy Wetherall
 */
public class BaseRuleTest extends BaseSpringTest
{
    /**
     * Data used in the tests
     */
    protected static final String RULE_TYPE_NAME = InboundRuleTypeAdapter.NAME;

    /**
     * Action used in tests
     */
    protected static final String ACTION_DEF_NAME = AddFeaturesActionExecutor.NAME;
    protected static final String ACTION_PROP_NAME_1 = AddFeaturesActionExecutor.PARAM_ASPECT_NAME;
    protected static final QName ACTION_PROP_VALUE_1 = ContentModel.ASPECT_LOCKABLE;

    /**
     * Condition used in tests
     */
    protected static final String CONDITION_DEF_NAME = MatchTextEvaluator.NAME;
    protected static final String COND_PROP_NAME_1 = MatchTextEvaluator.PARAM_TEXT;
    protected static final String COND_PROP_VALUE_1 = ".doc";

    /**
     * Rule values used in tests
     */
    protected static final String TITLE = "title";
    protected static final String DESCRIPTION = "description";

    /**
     * Services
     */
    protected NodeService nodeService;
    protected ContentService contentService;
    protected RuleService ruleService;

    /**
     * Rule type used in tests
     */
    protected RuleType ruleType;

    /**
     * Store and node references
     */
    protected StoreRef testStoreRef;
    protected NodeRef rootNodeRef;
    protected NodeRef nodeRef;
    protected NodeRef configFolder;

    /**
     * onSetUpInTransaction implementation
     */
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        
        
        // Get the services
        this.nodeService = (NodeService) this.applicationContext
                .getBean("nodeService");
        this.contentService = (ContentService) this.applicationContext
                .getBean("contentService");
        this.ruleService = (RuleService) this.applicationContext
                .getBean("ruleService");

        // Get the rule type
        this.ruleType = this.ruleService.getRuleType(RULE_TYPE_NAME);

        // Create the store and get the root node
        this.testStoreRef = this.nodeService.createStore(
                StoreRef.PROTOCOL_WORKSPACE, "Test_"
                        + System.currentTimeMillis());
        this.rootNodeRef = this.nodeService.getRootNode(this.testStoreRef);

        // Create the node used for tests
        this.nodeRef = this.nodeService.createNode(rootNodeRef,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName("{test}testnode"),
                ContentModel.TYPE_CONTAINER).getChildRef();

        // Create the config folder
        this.configFolder = this.nodeService.createNode(rootNodeRef,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName("{test}configfolder"),
                ContentModel.TYPE_CONFIGURATIONS).getChildRef();
    }

    protected void makeTestNodeActionable()
    {
        // Manually make the test node actionable
        this.nodeService.addAspect(this.nodeRef,
                ContentModel.ASPECT_ACTIONABLE, null);
        this.nodeService.createAssociation(this.nodeRef, configFolder,
                ContentModel.ASSOC_CONFIGURATIONS);
    }

    protected RuleImpl createTestRule(String id)
    {
        // Rule properties
        Map<String, Serializable> conditionProps = new HashMap<String, Serializable>();
        conditionProps.put(COND_PROP_NAME_1, COND_PROP_VALUE_1);

        Map<String, Serializable> actionProps = new HashMap<String, Serializable>();
        actionProps.put(ACTION_PROP_NAME_1, ACTION_PROP_VALUE_1);

        RuleConditionDefinition cond = this.ruleService
                .getConditionDefinition(CONDITION_DEF_NAME);
        RuleActionDefinition action = this.ruleService
                .getActionDefinition(ACTION_DEF_NAME);

        // Create the rule
        RuleImpl rule = new RuleImpl(id, this.ruleType);
        rule.setTitle(TITLE);
        rule.setDescription(DESCRIPTION);
        rule.addRuleCondition(cond, conditionProps);
        rule.addRuleAction(action, actionProps);

        return rule;
    }

    protected void checkRule(RuleImpl rule, String id)
    {
        // Check the basic details of the rule
        assertEquals(id, rule.getId());
        assertEquals(this.ruleType.getName(), rule.getRuleType().getName());
        assertEquals(TITLE, rule.getTitle());
        assertEquals(DESCRIPTION, rule.getDescription());

        // Check conditions
        List<RuleCondition> ruleConditions = rule.getRuleConditions();
        assertNotNull(ruleConditions);
        assertEquals(1, ruleConditions.size());
        assertEquals(CONDITION_DEF_NAME, ruleConditions.get(0)
                .getRuleConditionDefinition().getName());
        Map<String, Serializable> condParams = ruleConditions.get(0)
                .getParameterValues();
        assertNotNull(condParams);
        assertEquals(1, condParams.size());
        assertTrue(condParams.containsKey(COND_PROP_NAME_1));
        assertEquals(COND_PROP_VALUE_1, condParams.get(COND_PROP_NAME_1));

        // Check the actions
        List<RuleAction> ruleActions = rule.getRuleActions();
        assertNotNull(ruleActions);
        assertEquals(1, ruleActions.size());
        assertEquals(ACTION_DEF_NAME, ruleActions.get(0)
                .getRuleActionDefinition().getName());
        Map<String, Serializable> actionParams = ruleActions.get(0)
                .getParameterValues();
        assertNotNull(actionParams);
        assertEquals(1, actionParams.size());
        assertTrue(actionParams.containsKey(ACTION_PROP_NAME_1));
        assertEquals(ACTION_PROP_VALUE_1, actionParams.get(ACTION_PROP_NAME_1));
    }
}
