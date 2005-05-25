/**
 * Created on May 25, 2005
 */
package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.List;
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
	 * The title
	 */
	private String title;

	/**
	 * The description
	 */
	private String description;
	
	/**
	 * Constructor
	 * 
	 * @param ruleType	the rule type
	 */
	public Rule(RuleType ruleType)
	{
		if (ruleType == null)
		{
			// Error since the passed rule type is null
			throw new RuleServiceException("A rule must have a rule type specified.");
		}
		
		// Set the rule type
		this.ruleType = ruleType;
	}
	
	/**
	 * Get the rule type
	 * 
	 * @return  the rule type
	 */
	public RuleType getRuleType()
	{
		return this.ruleType;
	}
	
	/**
	 * Get the title of the rule
	 * 
	 * @return	the title
	 */
	public String getTitle()
	{
		return this.title;
	}
	
	/**
	 * Set the title of the rule
	 * 
	 * @param title  the title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	/**
	 * Get the description of the rule
	 * 
	 * @return	the description of the rule
	 */
	public String getDescription()
	{
		return this.description;
	}
	
	/**
	 * Set the description of the rule
	 * 
	 * @param description  the description of the rule
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * 
	 * @param ruleCondition
	 * @param parameterValues
	 * @return
	 */
	public RuleItemInstance<RuleCondition> addRuleCondition(
			RuleCondition ruleCondition, 
			Map<String, Serializable> parameterValues) 
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 * @return
	 */
	public List<RuleItemInstance<RuleCondition>> getRuleConditions()
	{
		throw new UnsupportedOperationException();
	}		
	
	/**
	 * 
	 * @param ruleItemInstance
	 */
	public void removeRuleCondition(RuleItemInstance<RuleCondition> ruleItemInstance)
	{
		throw new UnsupportedOperationException();
	}	
	
	/**
	 * 
	 * @param ruleAction
	 * @param parameterValues
	 * @return
	 */
	public RuleItemInstance<RuleAction> addRuleAction(
			RuleAction ruleAction,
			Map<String, Serializable> parameterValues)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 * @return
	 */
	public List<RuleItemInstance<RuleAction>> getRuleActions()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 * @param ruleItemInstance
	 */
	public void removeRuleAction(RuleItemInstance<RuleAction> ruleItemInstance)
	{
		throw new UnsupportedOperationException();
	}
}

