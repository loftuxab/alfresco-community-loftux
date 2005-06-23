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

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.rule.RuleCondition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;

/**
 * @author Roy Wetherall
 */
public class RuleConditionImpl extends RuleItemImpl implements Serializable,
        RuleCondition
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3257288015402644020L;
    
    /**
     * Rule condition defintion
     */
    private RuleConditionDefinition ruleConditionDefinition;

    /**
     * Constructor
     */
    public RuleConditionImpl(RuleConditionDefinition ruleConditionDefinition)
    {
        this(ruleConditionDefinition, null);
    }

    /**
     * @param parameterValues
     */
    public RuleConditionImpl(
            RuleConditionDefinition ruleConditionDefinition, 
            Map<String, Serializable> parameterValues)
    {
        super(parameterValues);
        this.ruleConditionDefinition = ruleConditionDefinition;
    }

    /**
     * @see org.alfresco.service.cmr.rule.RuleCondition#getRuleConditionDefinition()
     */
    public RuleConditionDefinition getRuleConditionDefinition()
    {
        return this.ruleConditionDefinition;
    }

}
