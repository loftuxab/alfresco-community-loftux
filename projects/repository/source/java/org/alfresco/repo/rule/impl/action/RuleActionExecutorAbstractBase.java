/**
 * 
 */
package org.alfresco.repo.rule.impl.action;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;

import org.alfresco.repo.rule.RuleActionExecuter;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleActionDefinition;
import org.alfresco.service.cmr.rule.RuleServiceException;

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
	 * The node service
	 */
    protected NodeService nodeService;

	/**
	 * Error messages
	 */
	private static final String ERR_MAND_PROP = "A value for the mandatory property {0} has not been set on the rule action {1}";

    /**
     * Constructor
     * 
     * @param ruleAction	the rule action
     * @param nodeService	the node service
     */
    public RuleActionExecutorAbstractBase(
            RuleAction ruleAction,
            NodeService nodeService)
    {
        this.ruleAction = ruleAction;
        this.nodeService = nodeService;
    }
	
	/**
	 * Checks that all the mandatory attribtues have been set.
	 * <p>
	 * Raises an exception if they are not present.
	 */
	protected void checkMandatoryProperties()
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
