/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.version;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.evaluator.HasVersionHistoryEvaluator;
import org.alfresco.repo.action.executer.CreateVersionActionExecuter;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.rule.RuntimeRuleService;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Class containing behaviour for the versionable aspect
 * 
 * @author Roy Wetherall
 */
public class VersionableAspect
{
	private Behaviour onAddAspectBehaviour;
	
	private PolicyComponent policyComponent;
    
    private RuleService ruleService;
    
    private ActionService actionService;
	
	public void setPolicyComponent(PolicyComponent policyComponent)
	{
		this.policyComponent = policyComponent;
	}
    
    public void setRuleService(RuleService ruleService)
    {
        this.ruleService = ruleService;
    }
    
    public void setActionService(ActionService actionService)
    {
        this.actionService = actionService;
    }
    
	public void init()
	{
		this.onAddAspectBehaviour = new JavaBehaviour(this, "onAddAspect");
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"), 
				ContentModel.ASPECT_VERSIONABLE, 
				onAddAspectBehaviour);
	}
 
	
	/**
	 * On add aspect policy behaviour
	 * @param nodeRef
	 * @param aspectTypeQName
	 */
	@SuppressWarnings("unchecked")
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
	{
	    if (aspectTypeQName.equals(ContentModel.ASPECT_VERSIONABLE) == true)
        {
            Rule rule = this.ruleService.createRule("inbound");
            Action action = this.actionService.createAction(CreateVersionActionExecuter.NAME);
            ActionCondition condition = this.actionService.createActionCondition(HasVersionHistoryEvaluator.NAME);
            condition.setInvertCondition(true);
            action.addActionCondition(condition);
            rule.addAction(action);
            
            ((RuntimeRuleService)this.ruleService).addRulePendingExecution(nodeRef, nodeRef, rule, true);
        }
	}
}
