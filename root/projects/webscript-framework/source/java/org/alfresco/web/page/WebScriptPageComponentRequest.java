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
package org.alfresco.web.page;

import java.util.Map;

import org.alfresco.util.Content;
import org.alfresco.web.scripts.Match;
import org.alfresco.web.scripts.Runtime;
import org.alfresco.web.scripts.WebScriptRequestURLImpl;

/**
 * Simple implementation of a WebScript URL Request for a webscript component on the page.
 * Mostly based on the existing WebScriptRequestURLImpl - just adds support for additional
 * page level context parameters available to the component as args.
 * 
 * @author Kevin Roast
 */
class WebScriptPageComponentRequest extends WebScriptRequestURLImpl
{
   private Map<String, String> parameters;

   WebScriptPageComponentRequest(Runtime runtime, String scriptUrl, Match match, Map<String, String> parameters)
   {
      super(runtime, scriptUrl, match);
      this.parameters = parameters;
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getParameterNames()
    */
   public String[] getParameterNames()
   {
      return this.parameters.keySet().toArray(new String[this.parameters.size()]);
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getParameter(java.lang.String)
    */
   public String getParameter(String name)
   {
      return this.parameters.get(name);
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getParameterValues(java.lang.String)
    */
   public String[] getParameterValues(String name)
   {
      return this.parameters.values().toArray(new String[this.parameters.size()]);
   }

   public String getAgent()
   {
      return null;
   }

   public String getServerPath()
   {
      return null;
   }

   public String[] getHeaderNames()
   {
      return new String[] {};
   }

   public String getHeader(String name)
   {
      return null;
   }

   public String[] getHeaderValues(String name)
   {
      return null;
   }

   public Content getContent()
   {
      return null;
   }
}
