/**
 * 
 */
package org.alfresco.repo.rule.impl.condition;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.rule.ParameterDefinition;
import org.alfresco.repo.rule.RuleCondition;
import org.alfresco.repo.rule.RuleConditionDefinition;
import org.alfresco.repo.rule.RuleConditionEvaluator;
import org.alfresco.repo.rule.RuleServiceException;

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
	 * The node service
	 */
    protected NodeService nodeService;

	/**
	 * Error messages
	 */
	private static final String ERR_MAND_PROP = "A value for the mandatory property {0} has not been set on the rule condition {1}";

    /**
     * Constructor
     * 
     * @param ruleCondition		the rule condition
     * @param nodeService		the node service
     */
    public RuleConditionEvaluatorAbstractBase(
            RuleCondition ruleCondition, 
            NodeService nodeService)
    {
        this.ruleCondition = ruleCondition;
        this.nodeService = nodeService;
    }
	
	/**
	 * Checks that all the mandatory attribtues have been set.
	 * <p>
	 * Raises an exception if they are not present.
	 */
	protected void checkMandatoryProperties()
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
