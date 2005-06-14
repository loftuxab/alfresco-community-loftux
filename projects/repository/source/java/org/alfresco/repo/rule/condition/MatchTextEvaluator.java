/**
 * Created on Jun 9, 2005
 */
package org.alfresco.repo.rule.condition;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.RuleCondition;
import org.springframework.context.ApplicationContext;

/**
 * Contains text evaluator
 * 
 * @author Roy Wetherall
 */
public class MatchTextEvaluator extends RuleConditionEvaluatorAbstractBase 
{
	public final static String NAME = "match-text";
	public final static String PARAM_TEXT = "text";
	public final static String PARAM_OPERATION = "operation";
    
    public enum Operation {CONTAINS, BEGINS, ENDS, EXACT};
    
    //private NodeService nodeService;
	
	/**
	 * Contructor 
	 * 
	 * @param ruleCondition		   the rule condition
	 * @param applicationContext   the application context
	 */
	public MatchTextEvaluator(
			RuleCondition ruleCondition,
            NodeService nodeService,
			ApplicationContext applicationContext) 
	{
		super(ruleCondition, nodeService, applicationContext);
        
        //this.nodeService = (NodeService)this.applicationContext.getBean("nodeService");
	}

	/**
     * @see org.alfresco.repo.rule.condition.RuleConditionEvaluatorAbstractBase#evaluateImpl(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
	 */
	public boolean evaluateImpl(
			NodeRef actionableNodeRef,
			NodeRef actionedUponNodeRef) 
	{
		boolean result = false;
		
		// Get the text to match against
		String matchText = (String)this.ruleCondition.getParameterValue(PARAM_TEXT);
		
		// Get the operation to be performed
		Operation operation = Operation.CONTAINS;
		String stringOperation = (String)this.ruleCondition.getParameterValue(PARAM_OPERATION);
		if (stringOperation != null)
		{
			operation = Operation.valueOf(stringOperation);
		}
		
		// Build the reg ex
		String regEx = buildRegEx(matchText, operation);
		
		// Get the name value of the node
		String name = (String)this.nodeService.getProperty(actionedUponNodeRef, DictionaryBootstrap.PROP_QNAME_NAME);
		
		// Do the match
		if (name != null)
		{
			result = name.matches(regEx);
		}
		
		return result;
	}

	private String buildRegEx(String matchText, Operation operation) 
	{
		// TODO the result of this could be cached to speed things up ...
		
		String result = escapeText(matchText);
		switch (operation) 
		{
			case CONTAINS:
				result = "^.*" + result + ".*$";
				break;
			case BEGINS:
				result = "^" + result + ".*$";
				break;
			case ENDS:
				result = "^.*" + result + "$";
				break;
			default:
				break;
		}
		return result;
	}

	private String escapeText(String matchText) 
	{
		StringBuilder builder = new StringBuilder(matchText.length());
		for (char charValue : matchText.toCharArray()) 
		{
			if (getEscapeCharList().contains(charValue) == true)
			{
				builder.append("\\");
			}
			builder.append(charValue);
		}
		
		return builder.toString();
	}

	private static List<Character> ESCAPE_CHAR_LIST = null;
	
	private List<Character> getEscapeCharList() 
	{
		if (ESCAPE_CHAR_LIST == null)
		{
			ESCAPE_CHAR_LIST = new ArrayList<Character>(4);
			ESCAPE_CHAR_LIST.add('.');
			ESCAPE_CHAR_LIST.add('^');
			ESCAPE_CHAR_LIST.add('*');
			ESCAPE_CHAR_LIST.add('$');
		}
		return ESCAPE_CHAR_LIST;
	}
}
