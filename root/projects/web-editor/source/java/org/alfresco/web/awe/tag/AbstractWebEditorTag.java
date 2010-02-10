/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.web.awe.tag;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.tagext.TagSupport;

import org.alfresco.web.awe.filter.WebEditorFilter;

/**
 * Base class for all Web Editor tag implementations.
 * 
 * @author gavinc
 */
public class AbstractWebEditorTag extends TagSupport
{
   private static final long serialVersionUID = 3251970922970982753L;

   private String urlPrefix = null;
   private Boolean editingEnabled = null;
   private Boolean debugEnabled = null;
    
   protected static final String KEY_TOOLBAR_LOCATION = "awe_toolbar_location";
   protected static final String KEY_MARKER_ID_PREFIX = "awe_marker_id_prefix";
   protected static final String KEY_EDITABLE_CONTENT = "awe_editable_content";

   protected boolean isEditingEnabled()
   {
      if (this.editingEnabled == null)
      {
         Object enabledKey = this.pageContext.getRequest().getAttribute(WebEditorFilter.KEY_ENABLED);
         if (enabledKey != null && enabledKey instanceof Boolean)
         {
            this.editingEnabled = (Boolean)enabledKey;
         }
         else
         {
            this.editingEnabled = Boolean.FALSE;
         }
      }
      
      return this.editingEnabled;
   }
   
   /**
    * Returns the URL prefix for the web editor application.
    * 
    * @return The AWE prefix URL
    */
   protected String getWebEditorUrlPrefix()
   {
      if (this.urlPrefix == null)
      {
         String prefix = (String)this.pageContext.getRequest().getAttribute(WebEditorFilter.KEY_URL_PREFIX);
         if (prefix != null && prefix.length() > 0)
         {
            this.urlPrefix = prefix;
         }
         else
         {
            this.urlPrefix = WebEditorFilter.DEFAULT_CONTEXT_PATH;
         }
      }
       
      return this.urlPrefix;
   }

   /**
    * Determines whether debug is enabled for the web editor application
    * <p>
    * This is the value of the <code>debug</code> init
    * parameter of the Web Editor filter definition in web.xml.
    * If the init parameter is not present false will be returned.
    * </p>
    * 
    * @return true if debug is enabled
    */
   protected boolean isDebugEnabled()
   {
      if (this.debugEnabled == null)
      {
         Object debug = this.pageContext.getRequest().getAttribute(WebEditorFilter.KEY_DEBUG);
         if (debug != null && debug instanceof Boolean)
         {
            this.debugEnabled = (Boolean)debug;
         }
         else
         {
            this.debugEnabled = Boolean.FALSE;
         }
         
      }
      
      return this.debugEnabled.booleanValue(); 
   }

   /**
    * Returns the list of marked content that has been discovered.
    * <p>
    * This list is built up as each markContent tag is encountered.
    * </p>
    * 
    * @return List of MarkedContent objects
    */
   @SuppressWarnings("unchecked")
   protected List<MarkedContent> getMarkedContent()
   {
      List<MarkedContent> markedContent = (List<MarkedContent>)this.pageContext.getRequest().getAttribute(KEY_EDITABLE_CONTENT);
    
      if (markedContent == null)
      {
         markedContent = new ArrayList<MarkedContent>();
         this.pageContext.getRequest().setAttribute(KEY_EDITABLE_CONTENT, markedContent);
      }
    
      return markedContent;
   }
   
   /**
    * Encodes the given string, so that it can be used within an HTML page.
    * 
    * @param string     the String to convert
    */
   protected String encode(String string)
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

   @Override
   public void release()
   {
      super.release();
       
      this.urlPrefix = null;
      this.debugEnabled = null;
   }
}
