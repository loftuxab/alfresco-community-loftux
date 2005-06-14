/**
 * 
 */
package org.alfresco.repo.rule.condition;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;

import org.alfresco.repo.rule.RuleConditionEvaluator;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.RuleCondition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;
import org.alfresco.service.cmr.rule.RuleServiceException;

/**
 * Rule condition evaluator abstract base implementation.
 * 
 * @author Roy Wetherall
 */
public abstract class RuleConditionEvaluatorAbstractBase implements RuleConditionEvaluator
{
	/**
	 * Rule condition
	 */
    protected RuleCondition ruleCondition;
    
    /**
     * Thread local used to prevent circular evaluation
     */
    private static ThreadLocal<Object> currentlyEvaluating = new ThreadLocal<Object>();

	/**
	 * Error messages
	 */
	private static final String ERR_MAND_PROP = "A value for the mandatory property {0} has not been set on the rule condition {1}";

    /**
     * Constructor
     * 
     * @param ruleCondition		   the rule condition
     * @param applicationContext   the application context
     */
    public RuleConditionEvaluatorAbstractBase(
            RuleCondition ruleCondition, 
            ServiceRegistry serviceRegistry)
    {
        this.ruleCondition = ruleCondition;
    }
    
    /**
     * @see org.alfresco.repo.rule.RuleConditionEvaluator#evaluate(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean evaluate(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        boolean result = false;
        if (currentlyEvaluating.get() == null)
        {
            currentlyEvaluating.set(new Object());
            try
            {
                checkMandatoryProperties();
                result = evaluateImpl(actionableNodeRef, actionedUponNodeRef);
            }
            finally
            {
                currentlyEvaluating.set(null);
            }
        }
        return result;
    }
	
    /**
     * Evaluation implementation
     * 
     * @param actionableNodeRef     the actionable node reference
     * @param actionedUponNodeRef   the actioned upon node reference
     */
	protected abstract boolean evaluateImpl(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef);

    /**
	 * Checks that all the mandatory attribtues have been set.
	 * <p>
	 * Raises an exception if they are not present.
	 */
	private void checkMandatoryProperties()
	{
		Map<String, Serializable> paramValues = ruleCondition.getParameterValues();
		RuleConditionDefinition condDefinition = ruleCondition.getRuleConditionDefinition();
		for (Map.Entry<String, Serializable> entry : paramValues.entrySet()) 
		{
			if (entry.getValue() == null)
			{
				ParameterDefinition paramDef = condDefinition.getParameterDefintion(entry.getKey());
				if (paramDef.isMandatory() == true)
				{
					// Error since a mandatory parameter has a null value
					throw new RuleServiceException(
							MessageFormat.format(ERR_MAND_PROP, new Object[]{entry.getKey(), condDefinition.getName()}));
				}
			}
		}
	}
}
