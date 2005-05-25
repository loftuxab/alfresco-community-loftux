/**
 * Created on May 25, 2005
 */
package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.Map;

/**
 * Rule implementation class.
 * <p>
 * Encapsulates all the information about a rule.  Can be creted or editied and
 * then passed to the rule service to create/update a rule instance.
 * 
 * @author Roy Wetherall
 */
public class Rule implements Serializable 
{
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 3544385898889097524L;

	/**
	 * The rule type
	 */
	private RuleType ruleType;
	
	/**
	 * Constructor
	 * 
	 * @param ruleType	the rule type
	 */
	public Rule(RuleType ruleType)
	{
		this.ruleType = ruleType;
	}
	
	/**
	 * Get the rule type
	 * 
	 * @return  the rule type
	 */
	public RuleType getRuleType()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Get the title of the rule
	 * 
	 * @return	the title
	 */
	public String getTitle()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Set the title of the rule
	 * 
	 * @param title  the title
	 */
	public void setTitle(String title)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Get the description of the rule
	 * 
	 * @return	the description of the rule
	 */
	public String getDescription()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Set the description of the rule
	 * 
	 * @param description  the description of the rule
	 */
	public void setDescription(String description)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Get the condition for the rule
	 * 
	 * @return  the rule condition
	 */
	public RuleCondition getRuleCondition()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Set the condition for the rule
	 * 
	 * @param ruleCondition  the rule condition
	 */
	public void setRuleCondition(RuleCondition ruleCondition)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Get the condition parameter values
	 * <p>
	 * Returns a map containing the name of the parameter and the 
	 * value.
	 * 
	 * @return	the parameter values of the condition
	 */
	public Map<String, Serializable> getRuleConditionParameterValues()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Set the condition parameter values
	 * 
	 * @param parameterValues  the condition parameter values
	 */
	public void setRuleConditionParameterValues(Map<String, Serializable> parameterValues) 
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Get the action for the rule
	 * 
	 * @return  the rule action 
	 */
	public RuleAction getRuleAction()
	{
		throw new UnsupportedOperationException();	
	}
	
	/**
	 * Set the action for the rule.
	 * 
	 * @param ruleAction	the rule action 
	 */
	public void setRuleAction(RuleAction ruleAction)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Get the action parameter values.
	 * <p>
	 * Returns a map containing the parameter name and value.
	 * 
	 * @return	the action parameter values
	 */
	public Map<String, Serializable> getRuleActionParameterValues()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Set the action parameter values.
	 * 
	 * @param parameterValues	the action parameter values
	 */
	public void setRuleActionParameterValues(Map<String, Serializable> parameterValues) 
	{
		throw new UnsupportedOperationException();
	}
	
}
