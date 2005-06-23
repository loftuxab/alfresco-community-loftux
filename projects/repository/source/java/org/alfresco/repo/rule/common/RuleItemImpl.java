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
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.rule.RuleItem;

/**
 * Rule item instance implementation class.
 * 
 * @author Roy Wetherall
 */
public abstract class RuleItemImpl implements RuleItem, Serializable
{
    /**
     * The parameter values
     */
    private Map<String, Serializable> parameterValues = new HashMap<String, Serializable>();

    /**
     * Constructor
     * 
     * @param ruleItem  the rule item
     */
    public RuleItemImpl()
    {
        this(null);
    }
    
    /**
     * Constructor
     * 
     * @param ruleItem          the rule item
     * @param parameterValues   the parameter values
     */
    public RuleItemImpl(Map<String, Serializable> parameterValues)
    {     
        if (parameterValues != null)
		{
	        // TODO need to check that the parameter values being set correspond
	        // correctly to the parameter definions on the rule item defintion
	        this.parameterValues = parameterValues;
		}
    }

    /**
     * @see org.alfresco.service.cmr.rule.RuleItem#getParameterValues()
     */
    public Map<String, Serializable> getParameterValues()
    {
        Map<String, Serializable> result = this.parameterValues;
        if (result == null)
        {
            result = new HashMap<String, Serializable>();
        }
        return result;
    }
	
	/**
	 * @see org.alfresco.service.cmr.rule.RuleItem#getParameterValue(String)
	 */
	public Serializable getParameterValue(String name)
	{
		return this.parameterValues.get(name);
	}
	
	/**
     * @see org.alfresco.service.cmr.rule.RuleItem#setParameterValues(java.util.Map)
     */
    public void setParameterValues(Map<String, Serializable> parameterValues)
    {
		if (parameterValues != null)
		{
			// TODO need to check that the parameter values being set correspond
			//      correctly to the parameter definions on the rule item defintion
			this.parameterValues = parameterValues;
		}
    }
	
	/**
	 * @see org.alfresco.service.cmr.rule.RuleItem#setParameterValue(String, Serializable)
	 */
	public void setParameterValue(String name, Serializable value)
	{
		this.parameterValues.put(name, value);
	}
}
