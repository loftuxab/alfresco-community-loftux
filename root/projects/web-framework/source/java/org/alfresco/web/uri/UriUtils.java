/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.uri;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility functions for dealing with URI and token replacement.
 * 
 * @author Kevin Roast
 */
public final class UriUtils
{
   private static Log logger = LogFactory.getLog(UriUtils.class);
   
   /**
    * Private constructor
    */
   private UriUtils()
   {
   }
   
   /**
    * Helper to replace tokens in a string with values from a map of token->value.
    * Token names in the string are delimited by '{' and '}' - the entire token name
    * plus the delimiters are replaced by the value found in the supplied replacement map.
    * If no replacement value is found for the token name, it is replaced by the empty string.
    * 
    * @param s       String to work on - cannot be null
    * @param tokens  Map of token name -> token value for replacements
    * 
    * @return the replaced string or the original if no tokens found or a failure occurs
    */
   public static String replaceUriTokens(String s, Map<String, String> tokens)
   {
      String result = s;
      int preIndex = 0;
      int delimIndex = s.indexOf('{');
      if (delimIndex != -1)
      {
         StringBuilder buf = new StringBuilder(s.length() + 16);
         do
         {
            // copy up to token delimiter start
            buf.append(s.substring(preIndex, delimIndex));
            
            // extract token and replace
            if (s.length() < delimIndex + 2)
            {
               if (logger.isWarnEnabled())
                  logger.warn("Failed to replace context tokens - malformed input: " + s);
               return s;
            }
            int endDelimIndex = s.indexOf('}', delimIndex + 2);
            if (endDelimIndex == -1)
            {
               if (logger.isWarnEnabled())
                  logger.warn("Failed to replace context tokens - malformed input: " + s);
               return s;
            }
            String token = s.substring(delimIndex + 1, endDelimIndex);
            String replacement = tokens.get(token);
            buf.append(replacement != null ? replacement : "");
            
            // locate next delimiter and mark end of previous delimiter
            preIndex = endDelimIndex + 1; 
            delimIndex = s.indexOf('{', preIndex);
            if (delimIndex == -1 && s.length() > preIndex)
            {
               // append suffix of original string after the last delimiter found
               buf.append(s.substring(preIndex));
            }
         } while (delimIndex != -1);
         
         result = buf.toString();
      }
      return result;
   }
}
