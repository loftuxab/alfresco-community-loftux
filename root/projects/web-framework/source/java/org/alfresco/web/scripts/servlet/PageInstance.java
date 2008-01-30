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
package org.alfresco.web.scripts.servlet;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.web.scripts.Description.RequiredAuthentication;

/**
 * Simple structure class representing the definition of a single page instance
 * 
 * @author Kevin Roast
 */
public class PageInstance
{
   public String PageTemplate;
   public String Title;
   public String Description;
   public String Theme;
   public RequiredAuthentication Authentication;
   public Map<String, PageComponent> Components = new HashMap<String, PageComponent>();

   PageInstance(String templateId)
   {
      this.PageTemplate = templateId;
   }

   @Override
   public String toString()
   {
      StringBuilder buf = new StringBuilder(256);
      buf.append("PageTemplate: ").append(PageTemplate);
      for (String id : Components.keySet())
      {
         buf.append("\r\n   ").append(Components.get(id).toString());
      }
      return buf.toString();
   }
}
