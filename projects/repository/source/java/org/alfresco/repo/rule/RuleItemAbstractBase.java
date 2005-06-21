/**
 * Created on Jun 17, 2005
 */
package org.alfresco.repo.rule;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
