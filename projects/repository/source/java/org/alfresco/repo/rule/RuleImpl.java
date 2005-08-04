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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.action.ActionConditionImpl;
import org.alfresco.repo.action.ActionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleServiceException;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.util.GUID;

/**
 * Rule implementation class.
 * <p>
 * Encapsulates all the information about a rule.  Can be creted or editied and
 * then passed to the rule service to create/update a rule instance.
 * 
 * @author Roy Wetherall
 */
public class RuleImpl implements Serializable, Rule 
{
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 3544385898889097524L;

    /**
     * The id
     */
    private String id;
    
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
     * The created date
     */
    private Date createdDate;
    
    /**
     * The modified date
     */
    private Date modifiedDate;
    
    /**
     * Node ref pointing to the rule content, will be null if this is a new
     * rule.
     */
    private NodeRef ruleContentNodeRef;
    
    /**
     * List of rule conditions
     */
    private List<ActionCondition> ruleConditions = new ArrayList<ActionCondition>();
    
    /**
     * List of rule actions
     */
    private List<Action> ruleActions = new ArrayList<Action>();

    /**
     * Indicates whether the rule is applied to all the children of the associated node
     * rather than just the node itself.
     */
    private boolean isAppliedToChildren = false;
    
	/**
	 * Constructor
	 * 
	 * @param ruleType	the rule type
	 */
	public RuleImpl(String id, RuleType ruleType)
	{
        if (id == null)
        {
            // Error since an id has not been specified
            throw new RuleServiceException("An id has not been specified for the rule.");
        }
        
		if (ruleType == null)
		{
			// Error since the passed rule type is null
			throw new RuleServiceException("A rule must have a rule type specified.");
		}
		
		// Set the id and rule type
        this.id = id;
		this.ruleType = ruleType;
	}
	
    /**
     * @see org.alfresco.service.cmr.rule.Rule#getId()
     */
    public String getId()
    {
        return this.id;
    }
    
    /**
     * @see org.alfresco.service.cmr.rule.Rule#isAppliedToChildren()
     */
    public boolean isAppliedToChildren()
    {
        return this.isAppliedToChildren;
    }
    
    /**
     *@see org.alfresco.service.cmr.rule.Rule#applyToChildren(boolean)
     */
    public void applyToChildren(boolean isAppliedToChildren)
    {
        this.isAppliedToChildren = isAppliedToChildren;
    }
    
	/**
     * @see org.alfresco.service.cmr.rule.Rule#getRuleType()
     */
	public RuleType getRuleType()
	{
		return this.ruleType;
	}
	
	/**
     * @see org.alfresco.service.cmr.rule.Rule#getTitle()
     */
	public String getTitle()
	{
		return this.title;
	}
	
	/**
     * @see org.alfresco.service.cmr.rule.Rule#setTitle(java.lang.String)
     */
	public void setTitle(String title)
	{
		this.title = title;
	}
	
    /**
     * @see org.alfresco.repo.rule.Rule#getCreatedDate()
     */
    public Date getCreatedDate()
    {
        return this.createdDate;
    }
    
    /**
     * Set the created date
     * 
     * @param createdDate  the created date
     */
    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    /**
     * @see org.alfresco.repo.rule.Rule#getModifiedDate()
     */
    public Date getModifiedDate()
    {
        return this.modifiedDate;
    }
    
    /**
     * Set the modified date
     * 
     * @param modifiedDate  the modified date
     */
    public void setModifiedDate(Date modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }
    
	/**
     * @see org.alfresco.service.cmr.rule.Rule#getDescription()
     */
	public String getDescription()
	{
		return this.description;
	}
	
	/**
     * @see org.alfresco.service.cmr.rule.Rule#setDescription(java.lang.String)
     */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
     * @see org.alfresco.service.cmr.rule.Rule#getActionConditions()
     */
	public List<ActionCondition> getActionConditions()
	{
		return this.ruleConditions;
	}	
    
    /**
     * @see org.alfresco.service.cmr.rule.Rule#addActionCondition(String, java.util.Map)
     */
    public ActionCondition addActionCondition(
            String actionConditionDefinitionName,
            Map<String, Serializable> parameterValues)
    {
        // TODO for now we only support a single rule condition
        if (this.ruleConditions.size() > 0)
        {
            throw new RuleServiceException("Currently only one condition per rule is supported.");
        }
        
        // Create the rule action and add to the list
        ActionCondition ruleCondition = new ActionConditionImpl(GUID.generate(), actionConditionDefinitionName, parameterValues);
        this.ruleConditions.add(ruleCondition);
        return ruleCondition;
    }
        
    /**
     * @see org.alfresco.service.cmr.rule.Rule#removeActionCondition(org.alfresco.service.cmr.action.ActionCondition)
     */
    public void removeActionCondition(ActionCondition ruleCondition)
    {
        // Remove the rule action from the list
        this.ruleConditions.remove(ruleCondition);
    }
    
    /**
     * @see org.alfresco.service.cmr.rule.Rule#removeAllActionConditions()
     */
    public void removeAllActionConditions()
    {
        this.ruleConditions.clear();
    }
    
    /**
     * @see org.alfresco.service.cmr.rule.Rule#getActions()
     */
    public List<Action> getActions()
    {
        return this.ruleActions;
    }
	
	/**
     * @see org.alfresco.service.cmr.rule.Rule#addAction(String, Map<String, Serializable>)
     */
	public Action addAction(
			String actionDefinitionName,
			Map<String, Serializable> parameterValues)
	{
        // TODO for now we only support a single rule action
        if (this.ruleActions.size() > 0)
        {
            throw new RuleServiceException("Currently only one action per rule is supported.");
        }
        
        // Create the action and add to the list
		Action ruleAction = new ActionImpl(GUID.generate(), actionDefinitionName, parameterValues);
        this.ruleActions.add(ruleAction);
        return ruleAction;
	}
		
	/**
     * @see org.alfresco.service.cmr.rule.Rule#removeAction(org.alfresco.service.cmr.action.Action)
     */
	public void removeAction(Action ruleAction)
	{
		// Remove the rule action from the list
        this.ruleActions.remove(ruleAction);
	}

    /**
     * @see org.alfresco.service.cmr.rule.Rule#removeAllActions()
     */
    public void removeAllActions()
    {
        this.ruleActions.clear();
    }
    
    /**
     * Sets the rule content node reference.
     * 
     * @param ruleContentNodeRef  the rule content node reference
     */
    public void setRuleContentNodeRef(NodeRef ruleContentNodeRef)
    {
        this.ruleContentNodeRef = ruleContentNodeRef;
    }
    
    /**
     * Gets the rule content node reference
     *
     * @param   the rule content node reference
     */
    public NodeRef getRuleContentNodeRef()
    {
        return this.ruleContentNodeRef;
    }    
	
	/**
	 * Hash code
	 */
	@Override
	public int hashCode() 
	{
		return this.id.hashCode();
	}
	
	/**
	 * Equals
	 */
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
        {
            return true;
        }
        if (obj instanceof RuleImpl)
        {
			RuleImpl that = (RuleImpl) obj;
            return (this.id.equals(that.id));
        }
        else
        {
            return false;
        }
	}
}

