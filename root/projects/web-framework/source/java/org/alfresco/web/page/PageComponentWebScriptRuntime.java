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

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.web.page.PageRendererServlet.PageRendererContext;
import org.alfresco.web.scripts.AbstractRuntime;
import org.alfresco.web.scripts.Authenticator;
import org.alfresco.web.scripts.Match;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;

/**
 * WebScript runtime for a Page Component included within the PageRenderer servlet context.
 * 
 * @author Kevin Roast
 */
class PageComponentWebScriptRuntime extends AbstractRuntime
{
   private PageRendererContext context;
   private PageComponent component;
   private String webScript;
   private String scriptUrl;
   private String encoding;
   private Writer out;

   /**
    * Constructor
    * 
    * @param out           The Writer for the component output
    * @param context       The context for the PageRenderer execution thread
    * @param component     The Page Component this runtime should execute
    * @param webScript     The component WebScript url
    * @param executeUrl    The full URL to execute including context path
    */
   PageComponentWebScriptRuntime(
         Writer out, PageRendererContext context, PageComponent component,
         String webScript, String executeUrl)
   {
      super(context.RuntimeContainer);
      this.out = out;
      this.component = component;
      this.context = context;
      this.webScript = webScript;
      this.scriptUrl = executeUrl;
      this.encoding = encoding;
      if (logger.isDebugEnabled())
         logger.debug("Constructing runtime for url: " + executeUrl);
   }

   public PageRendererContext getPageRendererContext()
   {
      return this.context;
   }
   
   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.Runtime#getName()
    */
   public String getName()
   {
      return "Page Renderer";
   }

   @Override
   protected String getScriptUrl()
   {
      return webScript;
   }

   @Override
   protected WebScriptRequest createRequest(Match match)
   {
      // create the model for the component request
      Map<String, String> properties = new HashMap<String, String>(8, 1.0f);
      
      // component ID is always available to the component
      properties.put("id", component.getId());
      // add/replace the "well known" context tokens in component properties
      for (String arg : component.getProperties().keySet())
      {
         properties.put(
               arg,
               PageRendererServlet.replaceContextTokens(component.getProperties().get(arg), context.Tokens));
      }
      
      // build the request to render this component
      return new WebScriptPageComponentRequest(this, scriptUrl, match, properties);
   }

   /**
    * Create the WebScriptResponse for a UI component.
    * 
    * Create a response object that we control to write to a temporary output buffer that
    * we later use that as the source for the UI component webscript include.
    */
   @Override
   protected WebScriptResponse createResponse()
   {
      return new WebScriptPageComponentResponse(this, context, component.getId(), out);
   }
   
   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.AbstractRuntime#getScriptParameters()
    */
   @Override
   public Map<String, Object> getScriptParameters()
   {
      return context.PageComponentModel;
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.AbstractRuntime#getTemplateParameters()
    */
   @Override
   public Map<String, Object> getTemplateParameters()
   {
      return context.PageComponentModel;
   }

   @Override
   protected String getScriptMethod()
   {
      return "GET";
   }

   @Override
   protected Authenticator createAuthenticator()
   {
      return null;
   }
}
