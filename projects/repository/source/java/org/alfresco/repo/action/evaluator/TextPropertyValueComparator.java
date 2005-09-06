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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roy Wetherall
 */
public class TextPropertyValueComparator implements PropertyValueComparator
{
    /**
     * Special star string
     */
    private static final String STAR = "*";
    
    /**
     * @see org.alfresco.repo.action.evaluator.PropertyValueComparator#compare(java.io.Serializable, java.io.Serializable, org.alfresco.repo.action.evaluator.ComparePropertyValueOperation)
     */
    public boolean compare(
            Serializable propertyValue,
            Serializable compareValue, 
            ComparePropertyValueOperation operation)
    {
        String compareText = (String)compareValue;
        
        boolean result = false;
        if (operation == null)
        {
            // Check for a trailing or leading star since it implies special behaviour when no default operation is specified
            if (compareText.startsWith(STAR) == true)
            {
                // Remove the star and set the operation to endsWith
                operation = ComparePropertyValueOperation.ENDS;
                compareText = compareText.substring(1);
            }
            else if (compareText.endsWith(STAR) == true)
            {
                // Remove the star and set the operation to startsWith
                operation = ComparePropertyValueOperation.BEGINS;
                compareText = compareText.substring(0, (compareText.length()-2));
            }
            else
            {
                operation = ComparePropertyValueOperation.CONTAINS;
            }
        }
            
        // Build the reg ex
        String regEx = buildRegEx(compareText, operation);
        
        // Do the match
        if (propertyValue != null)
        {
            result = ((String)propertyValue).matches(regEx);
        }
        
        return result;
    }
    
    /**
     * Builds the regular expressin that it used to make the match
     * 
     * @param matchText     the raw text to be matched
     * @param operation     the operation
     * @return              the regular expression string
     */
    private String buildRegEx(String matchText, ComparePropertyValueOperation operation) 
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
     * @param matchText     the raw text
     * @return              the escaped text
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
