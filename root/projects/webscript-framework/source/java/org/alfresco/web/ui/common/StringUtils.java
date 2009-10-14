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
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;

/**
 * Class containing misc helper methods for managing Strings.
 * 
 * NOTE: Extracted from org.alfresco.web.ui.common.Utils;
 * 
 * @author Kevin Roast
 */
public class StringUtils
{
    private static final Log logger = LogFactory.getLog(StringUtils.class);
    
    private static final String ATTR_STYLE = "STYLE";
    private static final String ATTR_ON_PREFIX = "ON";
    
    private static final Set<String> safeTags = new HashSet<String>();
    static
    {
        safeTags.add("EM");
        safeTags.add("STRONG");
        safeTags.add("SUP");
        safeTags.add("SUB");
        safeTags.add("P");
        safeTags.add("B");
        safeTags.add("I");
        safeTags.add("BR");
        safeTags.add("UL");
        safeTags.add("OL");
        safeTags.add("LI");
        safeTags.add("H1");
        safeTags.add("H2");
        safeTags.add("H3");
        safeTags.add("H4");
        safeTags.add("H5");
        safeTags.add("H6");
        safeTags.add("SPAN");
        safeTags.add("DIV");
        safeTags.add("A");
        safeTags.add("IMG");
        safeTags.add("FONT");
        safeTags.add("TABLE");
        safeTags.add("THEAD");
        safeTags.add("TBODY");
        safeTags.add("TR");
        safeTags.add("TH");
        safeTags.add("TD");
        safeTags.add("HR");
        safeTags.add("DT");
        safeTags.add("DL");
        safeTags.add("DT");
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
                    sb = new StringBuilder(i + 16);
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
     * and encodes the remaining characters.
     * 
     * @param s HTML string to strip tags from
     * 
     * @return safe string
     */
    public static String stripUnsafeHTMLTags(String s)
    {
        return stripUnsafeHTMLTags(s, true);
    }
    
    /**
     * Strip unsafe HTML tags from a string - only leaves most basic formatting tags
     * and optionally encodes or strips the remaining characters.
     * 
     * @param s         HTML string to strip tags from
     * @param encode    if true then encode remaining html data
     * 
     * @return safe string
     */
    public static String stripUnsafeHTMLTags(String s, boolean encode)
    {
        StringBuilder buf = new StringBuilder(s.length());
        
        Parser parser = Parser.createParser(s, "UTF-8");
        PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
        parser.setNodeFactory(factory);
        try
        {
            NodeIterator itr = parser.elements();
            processNodes(buf, itr, encode);
        }
        catch (ParserException e)
        {
            // return the only safe value if this occurs
            return "";
        }
        
        return buf.toString();
    }

    /**
     * Recursively process HTML nodes to strip unsafe HTML.
     * 
     * @param buf       Buffer to write to
     * @param itr       Node iterator to process
     * @param encode    True to HTML encode characters within text nodes
     * 
     * @throws ParserException
     */
    private static void processNodes(StringBuilder buf, NodeIterator itr, boolean encode) throws ParserException
    {
        while (itr.hasMoreNodes())
        {
            Node node = itr.nextNode();
            if (node instanceof Tag)
            {
                // get the tag and process it and its attributes
                Tag tag = (Tag)node;
                
                // get the tag name - automatically converted to upper case
                String tagname = tag.getTagName();
                
                // only allow a whitelist of safe tags i.e. remove SCRIPT etc.
                if (safeTags.contains(tagname))
                {
                    // process each attribute name - removing:
                    // all "on*" javascript event handlers
                    // all "style" attributes - as could contain 'expression' javascript for IE
                    Vector<Attribute> attrs = tag.getAttributesEx();
                    
                    // tag attributes contain the tag name at a minimum
                    if (attrs.size() > 1)
                    {
                        buf.append('<').append(tagname);
                        for (Attribute attr : attrs)
                        {
                            String name = attr.getName();
                            if (name != null)
                            {
                                String nameUpper = name.toUpperCase();
                                if (!tagname.equals(nameUpper))
                                {
                                    // found a tag attribute for output
                                    // test for known attributes to remove
                                    if (!nameUpper.startsWith(ATTR_ON_PREFIX) && !nameUpper.equals(ATTR_STYLE))
                                    {
                                        buf.append(' ').append(name).append('=')
                                           .append(attr.getRawValue());
                                    }
                                }
                            }
                        }
                        
                        // close the tag after attribute output and before child output
                        buf.append('>');
                        
                        // process children if they exist, else end tag will be processed in next iteration
                        if (tag.getChildren() != null)
                        {
                            processNodes(buf, tag.getChildren().elements(), encode);
                            buf.append(tag.getEndTag().toHtml());
                        }
                    }
                    else
                    {
                        // process children if they exist - or output end tag if not empty
                        if (tag.getChildren() != null)
                        {
                            buf.append('<').append(tagname).append('>');
                            processNodes(buf, tag.getChildren().elements(), encode);
                            buf.append(tag.getEndTag().toHtml());
                        }
                        else
                        {
                            buf.append(tag.toHtml());
                        }
                    }
                }
            }
            else if (node instanceof Text)
            {
                String txt = ((Text)node).toPlainTextString();
                buf.append(encode ? encode(txt): txt);
            }
        }
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
        if (str == null)
        {
            return null;
        }
        
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
    public static String replaceLineBreaks(String str, boolean xhtml)
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
                        parsedContent.append(xhtml ? "<br/>" : "<br>");
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
