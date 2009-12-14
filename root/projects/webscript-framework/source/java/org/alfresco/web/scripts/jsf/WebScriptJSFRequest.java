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
package org.alfresco.web.scripts.jsf;

import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.surf.util.URLDecoder;
import org.alfresco.web.scripts.Match;
import org.alfresco.web.scripts.Runtime;
import org.alfresco.web.scripts.WebScriptRequestURLImpl;

/**
 * Implementation of a WebScript Request for the JSF environment.
 * 
 * @author Kevin Roast
 */
public class WebScriptJSFRequest extends WebScriptRequestURLImpl
{
   /**
    * Construct
    * 
    * @param container
    * @param scriptUrlParts
    * @param match
    */
   public WebScriptJSFRequest(Runtime container, String[] scriptUrlParts, Match match)
   {
      super(container, scriptUrlParts, match);
      // decode url args (as they would be if this was a servlet)
      for (String name : this.queryArgs.keySet())
      {
         this.queryArgs.put(name, URLDecoder.decode(this.queryArgs.get(name)));
      }
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getServerPath()
    */
   public String getServerPath()
   {
      // NOTE: not accessable from JSF context - cannot create absolute external urls...
      return "";
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getAgent()
    */
   public String getAgent()
   {
      // NOTE: unknown in the JSF environment
      return null;
   }
      
   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderNames()
    */
   public String[] getHeaderNames()
   {
       return new String[] {};
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getHeader(java.lang.String)
    */
   public String getHeader(String name)
   {
       return null;
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderValues(java.lang.String)
    */
   public String[] getHeaderValues(String name)
   {
       return null;
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getContent()
    */
   public Content getContent()
   {
       return null;
   }
   
}
