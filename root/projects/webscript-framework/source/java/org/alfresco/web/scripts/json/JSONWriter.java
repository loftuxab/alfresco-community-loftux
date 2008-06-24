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
package org.alfresco.web.scripts.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

/**
 * Fast and simple JSON stream writer. Wraps a Writer to output a JSON object stream.
 * No intermediate objects are created - writes are immediate to the underlying stream.
 * Quoted and correct JSON encoding is performed on string values, - encoding is
 * not performed on key names - it is assumed they are simple strings. The developer must
 * call JSONWriter.encodeJSONString() on the key name if required.
 * 
 * @author Kevin Roast
 */
public final class JSONWriter
{
   private Writer out;
   private Stack<Boolean> stack = new Stack<Boolean>();
   
   public JSONWriter(Writer out)
   {
      this.out = out;
      stack.push(Boolean.FALSE);
   }
   
   public void startArray() throws IOException
   {
      if (stack.peek() == true) out.write(", ");
      out.write("[");
      stack.pop();
      stack.push(Boolean.TRUE);
      stack.push(Boolean.FALSE);
   }
   
   public void endArray() throws IOException
   {
      out.write("]");
      stack.pop();
   }
   
   public void startObject() throws IOException
   {
      if (stack.peek() == true) out.write(", ");
      out.write("{");
      stack.pop();
      stack.push(Boolean.TRUE);
      stack.push(Boolean.FALSE);
   }
   
   public void endObject() throws IOException
   {
      out.write("}");
      stack.pop();
   }
   
   public void startValue(String name) throws IOException
   {
      if (stack.peek() == true) out.write(", ");
      out.write('"');
      out.write(name);
      out.write("\": ");
      stack.pop();
      stack.push(Boolean.TRUE);
      stack.push(Boolean.FALSE);
   }
   
   public void endValue()
   {
      stack.pop();
   }
   
   public void writeValue(String name, String value) throws IOException
   {
      if (stack.peek() == true) out.write(", ");
      out.write('"');
      out.write(name);
      out.write("\": \"");
      out.write(encodeJSONString(value));
      out.write('"');
      stack.pop();
      stack.push(Boolean.TRUE);
   }
   
   public void writeValue(String name, int value) throws IOException
   {
      if (stack.peek() == true) out.write(", ");
      out.write('"');
      out.write(name);
      out.write("\": ");
      out.write(Integer.toString(value));
      stack.pop();
      stack.push(Boolean.TRUE);
   }
   
   public void writeValue(String name, boolean value) throws IOException
   {
      if (stack.peek() == true) out.write(", ");
      out.write('"');
      out.write(name);
      out.write("\": ");
      out.write(Boolean.toString(value));
      stack.pop();
      stack.push(Boolean.TRUE);
   }
   
   public void writeNullValue(String name) throws IOException
   {
      if (stack.peek() == true) out.write(", ");
      out.write('"');
      out.write(name);
      out.write("\": null");
      stack.pop();
      stack.push(Boolean.TRUE);
   }

   public static String encodeJSONString(final String s)
   {
       if (s == null || s.length() == 0)
       {
           return "";
       }
       
       StringBuilder sb = null;      // create on demand
       String enc;
       char c;
       int len = s.length();
       for (int i = 0; i < len; i++)
       {
           enc = null;
           c = s.charAt(i);
           switch (c)
           {
               case '\\':
                   enc = "\\\\";
                   break;
               case '"':
                   enc = "\\\"";
                   break;
               case '/':
                   enc = "\\/";
                   break;
               case '\b':
                   enc = "\\b";
                   break;
               case '\t':
                   enc = "\\t";
                   break;
               case '\n':
                   enc = "\\n";
                   break;
               case '\f':
                   enc = "\\f";
                   break;
               case '\r':
                   enc = "\\r";
                   break;

               default:
                   if (((int)c) >= 0x80)
                   {
                       //encode all non basic latin characters
                       String u = "000" + Integer.toHexString((int)c);
                       enc = "\\u" + u.substring(u.length() - 4);

                   }
               break;
           }

           if (enc != null)
           {
               if (sb == null)
               {
                   String soFar = s.substring(0, i);
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
           return s;
       }
       else
       {
           return sb.toString();
       }
   }
}