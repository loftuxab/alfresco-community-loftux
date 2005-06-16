/**
 * 
 */
package org.alfresco.repo.rule.condition;

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.RuleCondition;

/**
 * No condition evaluator implmentation.
 * 
 * @author Roy Wetherall
 */
public class NoConditionEvaluator extends RuleConditionEvaluatorAbstractBase
{
	/**
	 * Evaluator constants
	 */
	public static final String NAME = "no-condition";	

	
    public boolean evaluateImpl(RuleCondition ruleCondition, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        // Always return true
        return true;
    }

	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		// No parameters to add
	}

}
