/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.action.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * Contains text evaluator
 * 
 * @author Roy Wetherall
 */
public class MatchTextEvaluator extends ActionConditionEvaluatorAbstractBase 
{
	/**
	 * Evaluator constants
	 */
	public final static String NAME = "match-text";
	public final static String PARAM_TEXT = "text";
	public final static String PARAM_OPERATION = "operation";
    
	/**
	 * Operations enum
	 */
    public enum Operation {CONTAINS, BEGINS, ENDS, EXACT};
    
	/**
	 * The node service
	 */
    private NodeService nodeService;
    
    /**
     * The dictionary service
     */
    private DictionaryService dictionaryService;
    
    /**
     * Special star string
     */
    private static final String STAR = "*";
	
    /**
     * Set node service
     * 
     * @param nodeService  the node service
     */
	public void setNodeService(NodeService nodeService) 
	{
		this.nodeService = nodeService;
	}
    
    /**
     * Set dictionary service
     * 
     * @param dictionaryService  the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService) 
    {
        this.dictionaryService = dictionaryService;
    }   
    
	
    /**
     * Add paremeter defintions
     */
	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		paramList.add(new ParameterDefinitionImpl(PARAM_TEXT, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_TEXT)));
		paramList.add(new ParameterDefinitionImpl(PARAM_OPERATION, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_OPERATION)));
	}

	/**
     * @see ActionConditionEvaluatorAbstractBase#evaluateImpl(ActionCondition, NodeRef)
	 */
	public boolean evaluateImpl(
			ActionCondition ruleCondition,
			NodeRef actionedUponNodeRef) 
	{
		boolean result = false;
		
		if (this.nodeService.exists(actionedUponNodeRef) == true)
		{
            // TODO: Move this type check into its own Class Evaluator
            QName nodeType = nodeService.getType(actionedUponNodeRef);
            if (dictionaryService.isSubClass(nodeType, ContentModel.TYPE_CONTENT))
            {
    			// Get the text to match against
    			String matchText = (String)ruleCondition.getParameterValue(PARAM_TEXT);
    			
    			// Get the operation to be performed
    			Operation operation = null;
    			String stringOperation = (String)ruleCondition.getParameterValue(PARAM_OPERATION);
    			if (stringOperation != null)
    			{
    				operation = Operation.valueOf(stringOperation);
    			}
                else
                {
                    // Check for a trailing or leading star since it implies special behaviour when no default operation is specified
                    if (matchText.startsWith(STAR) == true)
                    {
                        // Remove the star and set the operation to endsWith
                        operation = Operation.ENDS;
                        matchText = matchText.substring(1);
                    }
                    else if (matchText.endsWith(STAR) == true)
                    {
                        // Remove the star and set the operation to startsWith
                        operation = Operation.BEGINS;
                        matchText = matchText.substring(0, (matchText.length()-2));
                    }
                    else
                    {
                        operation = Operation.CONTAINS;
                    }
                }
    			
    			// Build the reg ex
    			String regEx = buildRegEx(matchText, operation);
    			
    			// Get the name value of the node
    			String name = (String)this.nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME);
    			
    			// Do the match
    			if (name != null)
    			{
    				result = name.matches(regEx);
    			}
            }
		}
		
		return result;
	}

	/**
	 * Builds the regular expressin that it used to make the match
	 * 
	 * @param matchText		the raw text to be matched
	 * @param operation		the operation
	 * @return				the regular expression string
	 */
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

	/**
	 * Escapes the text before it is turned into a regualr expression
	 * 
	 * @param matchText		the raw text
	 * @return				the escaped text
	 */
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

	/**
	 * List of escape characters
	 */
	private static List<Character> ESCAPE_CHAR_LIST = null;
	
	/**
	 * Get the list of escape chars
	 * 
	 * @return  list of excape chars
	 */
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
