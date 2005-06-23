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

import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleActionDefinition;

/**
 * @author Roy Wetherall
 */
public class RuleActionImpl extends RuleItemImpl implements Serializable,
        RuleAction
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3258135760426186548L;
    
    /**
     * Rule action definition
     */
    private RuleActionDefinition ruleActionDefinition;

    /**
     * Constructor
     * 
     * @param ruleActionDefinition  the rule action definition
     */
    public RuleActionImpl(RuleActionDefinition ruleActionDefinition)
    {
        this(ruleActionDefinition, null);
    }

    /**
     * Constructor 
     * 
     * @param ruleActionDefinition  the rule action definition
     * @param parameterValues       the parameter values
     */
    public RuleActionImpl(
            RuleActionDefinition ruleActionDefinition, 
            Map<String, Serializable> parameterValues)
    {
        super(parameterValues);
        this.ruleActionDefinition = ruleActionDefinition;
    }

    /**
     * @see org.alfresco.service.cmr.rule.RuleAction#getRuleActionDefinition()
     */
    public RuleActionDefinition getRuleActionDefinition()
    {
        return this.ruleActionDefinition;
    }
}
