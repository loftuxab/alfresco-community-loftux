/**
 * Created on May 25, 2005
 */
package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.rule.impl.RuleActionImpl;
import org.alfresco.repo.rule.impl.RuleConditionImpl;

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
     * List of rule conditions
     */
    private List<RuleCondition> ruleConditions = new ArrayList<RuleCondition>();
    
    /**
     * List of rule actions
     */
    private List<RuleAction> ruleActions = new ArrayList<RuleAction>();
    
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
	 * Get a list of rule conditions.
     * 
	 * @return     the list of rule conditions
	 */
	public List<RuleCondition> getRuleConditions()
	{
		return this.ruleConditions;
	}	
    
    /**
     */
    public RuleCondition addRuleCondition(
            RuleConditionDefinition ruleConditionDefinition,
            Map<String, Serializable> parameterValues)
    {
        // TODO for now we only support a single rule condition
        if (this.ruleConditions.size() > 0)
        {
            throw new RuleServiceException("Currently only one condition per rule is supported.");
        }
        
        // Create the rule action and add to the list
        RuleCondition ruleCondition = new RuleConditionImpl(ruleConditionDefinition, parameterValues);
        this.ruleConditions.add(ruleCondition);
        return ruleCondition;
    }
        
    /**
     * 
     */
    public void removeRuleCondition(RuleCondition ruleCondition)
    {
        // Remove the rule action from the list
        this.ruleConditions.remove(ruleCondition);
    }
    
    /**
     * Get a list of rule actions.
     * 
     * @return      the list of rule actions
     */
    public List<RuleAction> getRuleActions()
    {
        return this.ruleActions;
    }
	
	/**
	 */
	public RuleAction addRuleAction(
			RuleActionDefinition ruleActionDefinition,
			Map<String, Serializable> parameterValues)
	{
        // TODO for now we only support a single rule action
        if (this.ruleActions.size() > 0)
        {
            throw new RuleServiceException("Currently only one action per rule is supported.");
        }
        
        // Create the rule action and add to the list
		RuleAction ruleAction = new RuleActionImpl(ruleActionDefinition, parameterValues);
        this.ruleActions.add(ruleAction);
        return ruleAction;
	}
		
	/**
	 * 
	 */
	public void removeRuleAction(RuleAction ruleAction)
	{
		// Remove the rule action from the list
        this.ruleActions.remove(ruleAction);
	}
}

