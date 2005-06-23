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

import org.alfresco.service.cmr.rule.RuleType;

/**
 * Rule type implementation class.
 * 
 * @author Roy Wetherall
 */
public class RuleTypeImpl implements RuleType
{
    /**
     * The name of the rule type
     */
    private String name;
    
    /**
     * The display label
     */
    private String displayLabel;
    
    /**
     * The ruleType adapter
     */
    private String ruleTypeAdapter;
    
    /**
     * 
     */
    public RuleTypeImpl(String name)
    {
        this.name = name;
    }

    /**
     * @see org.alfresco.service.cmr.rule.RuleType#getName()
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Set the display label
     * 
     * @param displayLabel  the displaylabel
     */
    public void setDisplayLabel(String displayLabel)
    {
        this.displayLabel = displayLabel;
    }

    /**
     * @see org.alfresco.service.cmr.rule.RuleType#getDisplayLabel()
     */
    public String getDisplayLabel()
    {
        return this.displayLabel;
    }
    
    /**
     * Sets the rule type adapter
     * 
     * @param ruleTypeAdapter  the rule type adapter
     */
    public void setRuleTypeAdapter(String ruleTypeAdapter)
    {
        this.ruleTypeAdapter = ruleTypeAdapter;
    }
    
    /**
     * Gets the rule type adapter
     * 
     * @return  the rule type adapter
     */
    public String getRuleTypeAdapter()
    {
        return ruleTypeAdapter;
    }
}
