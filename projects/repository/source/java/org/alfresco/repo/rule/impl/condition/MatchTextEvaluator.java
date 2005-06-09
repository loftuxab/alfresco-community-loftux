/**
 * Created on Jun 9, 2005
 */
package org.alfresco.repo.rule.impl.condition;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.rule.RuleCondition;

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
	
	/**
	 * Contructor 
	 * 
	 * @param ruleCondition		the rule condition
	 * @param nodeService		the node service
	 */
	public MatchTextEvaluator(
			RuleCondition ruleCondition,
			NodeService nodeService) 
	{
		super(ruleCondition, nodeService);
	}

	/**
	 * @see org.alfresco.repo.rule.RuleConditionEvaluator#evaluate(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
	 */
	public boolean evaluate(
			NodeRef actionableNodeRef,
			NodeRef actionedUponNodeRef) 
	{
		boolean result = false;
		
		// Check for the mandatory
		checkMandatoryProperties();
		
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
		// TODO the result of this could be cached to spped things up ...
		
		String result = escapeText(matchText);
		switch (operation) 
		{
			case CONTAINS:
				result = "^.*" + matchText + ".*$";
				break;
			case BEGINS:
				result = "^" + matchText + ".*$";
				break;
			case ENDS:
				result = "^.*" + matchText + "$";
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
