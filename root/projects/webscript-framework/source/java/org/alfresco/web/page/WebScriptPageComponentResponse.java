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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.alfresco.util.URLEncoder;
import org.alfresco.web.page.PageRendererServlet.PageRendererContext;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Runtime;
import org.alfresco.web.scripts.WebScriptResponseImpl;

/**
 * Implementation of a WebScript Response object for PageRenderer servlet.
 * Mostly based on the existing WebScriptResponseImpl - just adds support for
 * encoding URLs to manage user click requests to any component on the page.
 * 
 * @author Kevin Roast
 */
class WebScriptPageComponentResponse extends WebScriptResponseImpl
{
   private Writer out;
   private PageRendererContext context;
   private String componentId;

   public WebScriptPageComponentResponse(
         Runtime runtime, PageRendererContext context, String componentId, Writer out)
   {
      super(runtime);
      this.context = context;
      this.componentId = componentId;
      this.out = out;
   }

   public String encodeScriptUrl(String url)
   {
      // encode to allow presentation tier webscripts to call themselves non this page
      // needs the servlet URL plus args to identify the webscript and it's new url
      return context.RequestPath + context.RequestURI + "?" + PageRendererServlet.PARAM_COMPONENT_URL + "=" +
      URLEncoder.encode(url) + "&" + PageRendererServlet.PARAM_COMPONENT_ID + "=" + componentId;
   }

   public String getEncodeScriptUrlFunction(String name)
   {
      // TODO: may be required?
      return null;
   }

   public OutputStream getOutputStream() throws IOException
   {
      return null;
   }

   public Writer getWriter() throws IOException
   {
      return this.out;
   }

   public void reset()
   {
      // not supported
   }

   public void setCache(Cache cache)
   {
      // not supported
   }

   public void setHeader(String name, String value)
   {
      // not supported
   }

   public void addHeader(String name, String value)
   {
      // not supported
   }

   public void setContentType(String contentType)
   {
      // not supported
   }

   public void setStatus(int status)
   {
      // not supported
   }
}
