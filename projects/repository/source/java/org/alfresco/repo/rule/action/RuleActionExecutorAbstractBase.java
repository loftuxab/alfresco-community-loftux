/**
 * 
 */
package org.alfresco.repo.rule.action;

import org.alfresco.repo.rule.RuleItemAbstractBase;
import org.alfresco.repo.rule.RuleRegistration;
import org.alfresco.repo.rule.common.RuleActionDefinitionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleActionDefinition;

/**
 * Rule action executor abstract base.
 * 
 * @author Roy Wetherall
 */
public abstract class RuleActionExecutorAbstractBase extends RuleItemAbstractBase implements RuleActionExecuter
{
	protected RuleActionDefinition ruleActionDefinition;
	
	public void init()
	{
		((RuleRegistration)this.ruleService).registerRuleActionExecutor(this);
	}
	
	public RuleActionDefinition getRuleActionDefinition() 
	{
		if (this.ruleActionDefinition == null)
		{
			this.ruleActionDefinition = new RuleActionDefinitionImpl(this.name);
			((RuleActionDefinitionImpl)this.ruleActionDefinition).setTitle(getTitle());
			((RuleActionDefinitionImpl)this.ruleActionDefinition).setDescription(getDescription());
			((RuleActionDefinitionImpl)this.ruleActionDefinition).setRuleActionExecutor(this.name);
			((RuleActionDefinitionImpl)this.ruleActionDefinition).setParameterDefinitions(getParameterDefintions());
		}
		return this.ruleActionDefinition;
	}
	
	/**
     * @see org.alfresco.repo.rule.action.RuleActionExecuter#execute(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
     */
    public void execute(RuleAction ruleAction, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {        
        // Check the mandatory properties
        checkMandatoryProperties(ruleAction, this.ruleActionDefinition);
        
        // Execute the implementation
        executeImpl(ruleAction, actionableNodeRef, actionedUponNodeRef);        
    }
	
    /**
     * Execute the action implementation
     * 
     * @param actionableNodeRef     the actionable node
     * @param actionedUponNodeRef   the actioned upon node
     */
	protected abstract void executeImpl(RuleAction ruleAction, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef);

     
}
