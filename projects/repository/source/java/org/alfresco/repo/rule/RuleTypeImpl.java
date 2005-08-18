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

import java.util.List;

import org.alfresco.repo.action.CommonResourceAbstractBase;
import org.alfresco.repo.rule.ruletrigger.RuleTrigger;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleType;

/**
 * Rule type implementation class.
 * 
 * @author Roy Wetherall
 */
public class RuleTypeImpl extends CommonResourceAbstractBase implements RuleType
{
	/**
	 * The rule service
	 */
	private RuleService ruleService;
    
    /**
     * Constructor
     * 
     * @param ruleTriggers	the rule triggers
     */
    public RuleTypeImpl(List<RuleTrigger> ruleTriggers)
    {
    	if (ruleTriggers != null)
    	{
	    	for (RuleTrigger trigger : ruleTriggers)
			{
				trigger.registerRuleType(this);
			}
    	}
    }
    
    /**
     * Set the rule service
     * 
     * @param ruleService  the rule service
     */
    public void setRuleService(RuleService ruleService)
	{
		this.ruleService = ruleService;
	}

    /**
     * Rule type initialise method
     */
    public void init()
    {
    	((RuntimeRuleService)this.ruleService).registerRuleType(this);
    }
    
    /**
     * @see org.alfresco.service.cmr.rule.RuleType#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see org.alfresco.service.cmr.rule.RuleType#getDisplayLabel()
     */
    public String getDisplayLabel()
    {
        return this.properties.getProperty(this.name + "." + "display-label");
    }

    /**
     * @see org.alfresco.service.cmr.rule.RuleType#triggerRuleType(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
     */
	public void triggerRuleType(NodeRef nodeRef, NodeRef actionedUponNodeRef)
	{
		if (this.ruleService.rulesEnabled(nodeRef) == true && this.ruleService.hasRules(nodeRef) == true)
        {
            List<Rule> rules = this.ruleService.getRules(
            		nodeRef, 
                    true,
                    this.name);
			
            for (Rule rule : rules)
            {   
				((RuntimeRuleService)this.ruleService).addRulePendingExecution(nodeRef, actionedUponNodeRef, rule);
            }
        }
	}
	
	/**
	 * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
	 */
	public void setBeanName(String name)
	{
		this.name = name;	
	}
}
