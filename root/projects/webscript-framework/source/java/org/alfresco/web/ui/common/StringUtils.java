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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.ui.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class containing misc helper methods for managing Strings.
 * 
 * NOTE: Extracted from org.alfresco.web.ui.common.Utils;
 * 
 * @author Kevin Roast
 */
public final class StringUtils
{
   private static final Log logger = LogFactory.getLog(StringUtils.class);
   
   private static final Set<String> safeTags = new HashSet<String>();
   static
   {
      safeTags.add("p");
      safeTags.add("/p");
      safeTags.add("b");
      safeTags.add("/b");
      safeTags.add("i");
      safeTags.add("/i");
      safeTags.add("br");
      safeTags.add("ul");
      safeTags.add("/ul");
      safeTags.add("ol");
      safeTags.add("/ol");
      safeTags.add("li");
      safeTags.add("/li");
      safeTags.add("h1");
      safeTags.add("/h1");
      safeTags.add("h2");
      safeTags.add("/h2");
      safeTags.add("h3");
      safeTags.add("/h3");
      safeTags.add("h4");
      safeTags.add("/h4");
      safeTags.add("h5");
      safeTags.add("/h5");
      safeTags.add("h6");
      safeTags.add("/h6");
      safeTags.add("span");
      safeTags.add("/span");
      safeTags.add("a");
      safeTags.add("/a");
      safeTags.add("img");
      safeTags.add("font");
      safeTags.add("/font");
   }
   
   /**
    * Private constructor
    */
   private StringUtils()
   {
   }
   
   /**
    * Encodes the given string, so that it can be used within an HTML page.
    * 
    * @param string     the String to convert
    */
   public static String encode(String string)
   {
      if (string == null)
      {
         return "";
      }

      StringBuilder sb = null;      //create on demand
      String enc;
      char c;
      for (int i = 0; i < string.length(); i++)
      {
         enc = null;
         c = string.charAt(i);
         switch (c)
         {
            case '"': enc = "&quot;"; break;    //"
            case '&': enc = "&amp;"; break;     //&
            case '<': enc = "&lt;"; break;      //<
            case '>': enc = "&gt;"; break;      //>
             
            //german umlauts
            case '\u00E4' : enc = "&auml;";  break;
            case '\u00C4' : enc = "&Auml;";  break;
            case '\u00F6' : enc = "&ouml;";  break;
            case '\u00D6' : enc = "&Ouml;";  break;
            case '\u00FC' : enc = "&uuml;";  break;
            case '\u00DC' : enc = "&Uuml;";  break;
            case '\u00DF' : enc = "&szlig;"; break;
            
            //misc
            //case 0x80: enc = "&euro;"; break;  sometimes euro symbol is ascii 128, should we suport it?
            case '\u20AC': enc = "&euro;";  break;
            case '\u00AB': enc = "&laquo;"; break;
            case '\u00BB': enc = "&raquo;"; break;
            case '\u00A0': enc = "&nbsp;"; break;
            
            default:
               if (((int)c) >= 0x80)
               {
                  //encode all non basic latin characters
                  enc = "&#" + ((int)c) + ";";
               }
               break;
         }
         
         if (enc != null)
         {
            if (sb == null)
            {
               String soFar = string.substring(0, i);
               sb = new StringBuilder(i + 8);
               sb.append(soFar);
            }
            sb.append(enc);
         }
         else
         {
            if (sb != null)
            {
               sb.append(c);
            }
         }
      }
      
      if (sb == null)
      {
         return string;
      }
      else
      {
         return sb.toString();
      }
   }
   
   /**
    * Crop a label within a SPAN element, using ellipses '...' at the end of label and
    * and encode the result for HTML output. A SPAN will only be generated if the label
    * is beyond the default setting of 32 characters in length.
    * 
    * @param text       to crop and encode
    * 
    * @return encoded and cropped resulting label HTML
    */
   public static String cropEncode(String text)
   {
      return cropEncode(text, 32);
   }
   
   /**
    * Crop a label within a SPAN element, using ellipses '...' at the end of label and
    * and encode the result for HTML output. A SPAN will only be generated if the label
    * is beyond the specified number of characters in length.
    * 
    * @param text       to crop and encode
    * @param length     length of string to crop too
    * 
    * @return encoded and cropped resulting label HTML
    */
   public static String cropEncode(String text, int length)
   {
      if (text.length() > length)
      {
         String label = text.substring(0, length - 3) + "...";
         StringBuilder buf = new StringBuilder(length + 32 + text.length());
         buf.append("<span title=\"")
            .append(StringUtils.encode(text))
            .append("\">")
            .append(StringUtils.encode(label))
            .append("</span>");
         return buf.toString();
      }
      else
      {
         return StringUtils.encode(text);
      }
   }
   
   /**
    * Encode a string to the %AB hex style JavaScript compatible notation.
    * Used to encode a string to a value that can be safely inserted into an HTML page and
    * then decoded (and probably eval()ed) using the unescape() JavaScript method.
    * 
    * @param s      string to encode
    * 
    * @return %AB hex style encoded string
    */
   public static String encodeJavascript(String s)
   {
       StringBuilder buf = new StringBuilder(s.length() * 3);
       for (int i=0; i<s.length(); i++)
       {
           char c = s.charAt(i);
           int iChar = (int)c;
           buf.append('%');
           buf.append(Integer.toHexString(iChar));
       }
       return buf.toString();
   }
   
   /**
    * Strip unsafe HTML tags from a string - only leaves most basic formatting tags
    * and encodes or strips the remaining characters.
    * 
    * @param s HTML string to strip tags from
    * 
    * @return safe string
    */
   public static String stripUnsafeHTMLTags(String s)
   {
      s = s.replace("onclick", "$");
      s = s.replace("onmouseover", "$");
      s = s.replace("onmouseout", "$");
      s = s.replace("onmousemove", "$");
      s = s.replace("onfocus", "$");
      s = s.replace("onblur", "$");
      StringBuilder buf = new StringBuilder(s.length());
      char[] chars = s.toCharArray();
      for (int i=0; i<chars.length; i++)
      {
         if (chars[i] == '<')
         {
            // found a tag?
            int endMatchIndex = -1;
            int endTagIndex = -1;
            if (i < chars.length - 2)
            {
               for (int x=(i + 1); x<chars.length; x++)
               {
                  if (chars[x] == ' ' && endMatchIndex == -1)
                  {
                     // keep track of the match point for comparing tags in the safeTags set
                     endMatchIndex = x;
                  }
                  else if (chars[x] == '>')
                  {
                     endTagIndex = x;
                     break;
                  }
                  else if (chars[x] == '<')
                  {
                     // found another angle bracket - not a tag def so we can safely output to here
                     break;
                  }
               }
            }
            if (endTagIndex != -1)
            {
               // found end of the tag to match
               String tag = s.substring(i + 1, endTagIndex).toLowerCase();
               String matchTag = tag;
               if (endMatchIndex != -1)
               {
                  matchTag = s.substring(i + 1, endMatchIndex).toLowerCase();
               }
               if (safeTags.contains(matchTag))
               {
                  // safe tag - append to buffer
                  buf.append('<').append(tag).append('>');
               }
               // inc counter to skip past whole tag
               i = endTagIndex;
               continue;
            }
         }
         String enc = null;
         switch (chars[i])
         {
            case '"': enc = "&quot;"; break;
            case '&': enc = "&amp;"; break;
            case '<': enc = "&lt;"; break;
            case '>': enc = "&gt;"; break;
            
            default:
               if (((int)chars[i]) >= 0x80)
               {
                  //encode all non basic latin characters
                  enc = "&#" + ((int)chars[i]) + ";";
               }
            break;
         }
         buf.append(enc == null ? chars[i] : enc);
      }
      return buf.toString();
   }
   
   /**
    * Replace one string instance with another within the specified string
    * 
    * @param str
    * @param repl
    * @param with
    * 
    * @return replaced string
    */
   public static String replace(String str, String repl, String with)
   {
       int lastindex = 0;
       int pos = str.indexOf(repl);

       // If no replacement needed, return the original string
       // and save StringBuffer allocation/char copying
       if (pos < 0)
       {
           return str;
       }
       
       int len = repl.length();
       int lendiff = with.length() - repl.length();
       StringBuilder out = new StringBuilder((lendiff <= 0) ? str.length() : (str.length() + (lendiff << 3)));
       for (; pos >= 0; pos = str.indexOf(repl, lastindex = pos + len))
       {
           out.append(str.substring(lastindex, pos)).append(with);
       }
       
       return out.append(str.substring(lastindex, str.length())).toString();
   }
   
   /**
    * Remove all occurances of a String from a String
    * 
    * @param str     String to remove occurances from
    * @param match   The string to remove
    * 
    * @return new String with occurances of the match removed
    */
   public static String remove(String str, String match)
   {
      int lastindex = 0;
      int pos = str.indexOf(match);

      // If no replacement needed, return the original string
      // and save StringBuffer allocation/char copying
      if (pos < 0)
      {
          return str;
      }
      
      int len = match.length();
      StringBuilder out = new StringBuilder(str.length());
      for (; pos >= 0; pos = str.indexOf(match, lastindex = pos + len))
      {
          out.append(str.substring(lastindex, pos));
      }
      
      return out.append(str.substring(lastindex, str.length())).toString();
   }
   
   /**
    * Replaces carriage returns and line breaks with the &lt;br&gt; tag.
    * 
    * @param str The string to be parsed
    * @return The string with line breaks removed
    */
   public static String replaceLineBreaks(String str)
   {
      String replaced = null;
      
      if (str != null)
      {
         try
         {
            StringBuilder parsedContent = new StringBuilder(str.length() + 32);
            BufferedReader reader = new BufferedReader(new StringReader(str));
            String line = reader.readLine();
            while (line != null)
            {
               parsedContent.append(line);
               line = reader.readLine();
               if (line != null)
               {
                  parsedContent.append("<br>");
               }
            }
            
            replaced = parsedContent.toString();
         }
         catch (IOException ioe)
         {
            if (logger.isWarnEnabled())
            {
               logger.warn("Failed to replace line breaks in string: " + str);
            }
         }
      }
      
      return replaced;
   }
   
}
