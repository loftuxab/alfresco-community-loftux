/**
 * 
 */
package org.alfresco.repo.rule.action;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;

import org.alfresco.repo.rule.RuleActionExecuter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleActionDefinition;
import org.alfresco.service.cmr.rule.RuleServiceException;
import org.springframework.context.ApplicationContext;

/**
 * Rule action executor abstract base.
 * 
 * @author Roy Wetherall
 */
public abstract class RuleActionExecutorAbstractBase implements RuleActionExecuter
{
	/**
	 * The rule action
	 */
    protected RuleAction ruleAction;
    
    /**
     * The application context
     */
    protected ApplicationContext applicationContext;
    
    /**
     * Thread local used to prevent circular execution
     */
    private static ThreadLocal<Object> currentlyExecuting = new ThreadLocal<Object>();

	/**
	 * Error messages
	 */
	private static final String ERR_MAND_PROP = "A value for the mandatory property {0} has not been set on the rule action {1}";

    /**
     * Constructor
     * 
     * @param ruleAction	       the rule action
     * @param applicationContext   the application context 
     */
    public RuleActionExecutorAbstractBase(
            RuleAction ruleAction,
            ApplicationContext applicationContext)
    {
        this.ruleAction = ruleAction;
        this.applicationContext = applicationContext;
    }    
    
    /**
     * @see org.alfresco.repo.rule.RuleActionExecuter#execute(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
     */
    public void execute(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        if (currentlyExecuting.get() == null)
        {
            currentlyExecuting.set(new Object());
            try
            {
                // Check the mandatory properties
                checkMandatoryProperties();
                
                // Execute the implementation
                executeImpl(actionableNodeRef, actionedUponNodeRef);
            }
            finally
            {
                currentlyExecuting.set(null);
            }
        }
        
    }
	
    /**
     * Execute the action implementation
     * 
     * @param actionableNodeRef     the actionable node
     * @param actionedUponNodeRef   the actioned upon node
     */
	protected abstract void executeImpl(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef);

    /**
	 * Checks that all the mandatory attribtues have been set.
	 * <p>
	 * Raises an exception if they are not present.
	 */
	private void checkMandatoryProperties()
	{
		Map<String, Serializable> paramValues = ruleAction.getParameterValues();
		RuleActionDefinition actionDefinition = ruleAction.getRuleActionDefinition();
		for (Map.Entry<String, Serializable> entry : paramValues.entrySet()) 
		{
			if (entry.getValue() == null)
			{
				ParameterDefinition paramDef = actionDefinition.getParameterDefintion(entry.getKey());
				if (paramDef.isMandatory() == true)
				{
					// Error since a mandatory parameter has a null value
					throw new RuleServiceException(
							MessageFormat.format(ERR_MAND_PROP, new Object[]{entry.getKey(), actionDefinition.getName()}));
				}
			}
		}
	}    
}
