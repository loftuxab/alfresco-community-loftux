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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.RuleItem;
import org.alfresco.service.cmr.rule.RuleItemDefinition;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleServiceException;

/**
 * Rule item abstract base.
 * <p>
 * Helper base class used by the action exector and condition evaluator implementations.
 * 
 * @author Roy Wetherall
 */
public abstract class RuleItemAbstractBase extends CommonResourceAbstractBase 
{
	/**
	 * Error messages
	 */
	private static final String ERR_MAND_PROP = "A value for the mandatory parameter {0} has not been set on the rule item {1}";
	
	/**
	 * Look-up constants
	 */
	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	private static final String DISPLAY_LABEL = "display-label";
	
	/**
	 * Rule service
	 */
	protected RuleService ruleService;
    
    /**
     * @return Return a short title and description string
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder(60);
        sb.append("RuleItem")
          .append("[ title='").append(getTitle()).append("'")
          .append(", description='").append(getDescription()).append("'")
          .append("]");
        return sb.toString();
    }
	
	/**
	 * Gets a list containing the parameter definitions for this rule item.
	 * 
	 * @return  the list of parameter definitions
	 */
	protected List<ParameterDefinition> getParameterDefintions() 
	{
		List<ParameterDefinition> result = new ArrayList<ParameterDefinition>();		
		addParameterDefintions(result);
		return result;
	}
	
	/**
	 * Adds the parameter definitions to the list
	 * 
	 * @param paramList		the parameter definitions list
	 */
	protected abstract void addParameterDefintions(List<ParameterDefinition> paramList);

	public void setRuleService(RuleService ruleService) 
	{
		this.ruleService = ruleService;
	}

	/**
	 * Gets the rule item title value from the properties file.
	 * 
	 * @return	the title of the rule item
	 */
	protected String getTitle() 
	{
		return this.properties.getProperty(this.name + "." + TITLE);
	}

	/**
	 * Gets the description of the rule item from the properties file.
	 * 
	 * @return	the description of the rule item
	 */
	protected String getDescription() 
	{
		return this.properties.getProperty(this.name + "." + DESCRIPTION);
	}	

	/**
	 * Gets the parameter definition display label from the properties file.
	 * 
	 * @param paramName  the name of the parameter
	 * @return			 the diaplay label of the parameter
	 */
	protected String getParamDisplayLabel(String paramName) 
	{
		return this.properties.getProperty(this.name + "." + paramName + "." + DISPLAY_LABEL);
	}
	
	/**
	 * Checked whether all the mandatory parameters for the rule item have been assigned.
	 * 
	 * @param ruleItem				the rule item
	 * @param ruleItemDefinition	the rule item definition
	 */
	protected void checkMandatoryProperties(RuleItem ruleItem, RuleItemDefinition ruleItemDefinition)
	{
        List<ParameterDefinition> definitions = ruleItemDefinition.getParameterDefinitions();
        for (ParameterDefinition definition : definitions)
        {
            if (definition.isMandatory() == true)
            {
                // Check that a value has been set for the mandatory parameter
                if (ruleItem.getParameterValue(definition.getName()) == null)
                {
                    // Error since a mandatory parameter has a null value
                   throw new RuleServiceException(
                          MessageFormat.format(ERR_MAND_PROP, new Object[]{definition.getName(), ruleItemDefinition.getName()}));
                }
            }
        }
        
	}
}
