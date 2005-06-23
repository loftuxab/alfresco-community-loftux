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
package org.alfresco.repo.rule.common;

import org.alfresco.service.cmr.rule.RuleActionDefinition;

/**
 * Rule action implementation class
 * 
 * @author Roy Wetherall
 */
public class RuleActionDefinitionImpl extends RuleItemDefinitionImpl
                            implements RuleActionDefinition
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4048797883396863026L;    
    
    /**
     * The rule action executor
     */
    private String ruleActionExecutor;

    /**
     * Constructor
     * 
     * @param name  the name
     */
    public RuleActionDefinitionImpl(String name)
    {
        super(name);
    }
        
    /**
     * Set the rule action executor
     * 
     * @param ruleActionExecutor    the rule action executor
     */
    public void setRuleActionExecutor(String ruleActionExecutor)
    {
        this.ruleActionExecutor = ruleActionExecutor;
    }
    
    /**
     * Get the rule aciton executor
     * 
     * @return  the rule action executor
     */
    public String getRuleActionExecutor()
    {
        return ruleActionExecutor;
    }
}
