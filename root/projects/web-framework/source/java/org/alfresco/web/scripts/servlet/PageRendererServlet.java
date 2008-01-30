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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.URLEncoder;
import org.alfresco.web.scripts.AbstractRuntime;
import org.alfresco.web.scripts.Authenticator;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Match;
import org.alfresco.web.scripts.PresentationTemplateProcessor;
import org.alfresco.web.scripts.Runtime;
import org.alfresco.web.scripts.SearchPath;
import org.alfresco.web.scripts.Store;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptRequestURLImpl;
import org.alfresco.web.scripts.WebScriptResponse;
import org.alfresco.web.scripts.WebScriptResponseImpl;
import org.alfresco.web.scripts.Description.RequiredAuthentication;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import freemarker.cache.TemplateLoader;

/**
 * Servlet for rendering pages based a PageTemplate and WebScript based components.
 * 
 * GET: /<context>/<servlet>/[resource]...
 *  resource - app url resource
 *  url args - passed to all webscript component urls for the page
 * 
 * Read from page renderer web-app config:
 *  serverurl - url to Alfresco host repo server (i.e. http://servername:8080/alfresco)
 * 
 * Read from object model for spk:site - pass to webscript urls as "well known tokens"
 *  theme - default theme for the site (can be override in user prefs?) 
 * 
 * @author Kevin Roast
 */
public class PageRendererServlet extends WebScriptServlet
{
   private static Log logger = LogFactory.getLog(PageRendererServlet.class);
   
   private static final String MIMETYPE_HTML = "text/html;charset=utf-8";
   private static final String PARAM_COMPONENT_ID  = "_alfId";
   private static final String PARAM_COMPONENT_URL = "_alfUrl";
   
   private PresentationTemplateProcessor templateProcessor;
   private UIComponentTemplateLoader uicomponentTemplateLoader;
   private SearchPath searchPath;
   
   @Override
   public void init() throws ServletException
   {
      super.init();
      
      // init required beans - template processor and template loaders
      ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
      
      searchPath = (SearchPath)context.getBean("pagerenderer.searchpath");
      templateProcessor = (PresentationTemplateProcessor)context.getBean("webscripts.web.templateprocessor");
      
      // custom loader for resolved UI Component reference indirections
      uicomponentTemplateLoader = new UIComponentTemplateLoader();
      templateProcessor.addTemplateLoader(uicomponentTemplateLoader);
      
      // add template loader for locally stored templates
      for (Store store : searchPath.getStores())
      {
         templateProcessor.addTemplateLoader(store.getTemplateLoader());
      }
      
      // init the config for the template processor - caches and loaders etc. get resolved
      templateProcessor.initConfig();
      
      // we use a specific config service instance
      configService = (ConfigService)context.getBean("pagerenderer.config");
   }

   @Override
   protected void service(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
   {
      String uri = req.getRequestURI();
      
      long startTime = 0;
      if (logger.isDebugEnabled())
      {
         String qs = req.getQueryString();
         logger.debug("Processing Page Renderer URL: ("  + req.getMethod() + ") " + uri + 
               ((qs != null && qs.length() != 0) ? ("?" + qs) : ""));
         startTime = System.currentTimeMillis();
      }
      
      uri = uri.substring(req.getContextPath().length());   // skip server context path
      StringTokenizer t = new StringTokenizer(uri, "/");
      t.nextToken();    // skip servlet name
      if (!t.hasMoreTokens())
      {
         throw new IllegalArgumentException("Invalid URL to PageRendererServlet: " + uri);
      }
      
      // get the remaining elements of the url ready for AppUrl lookup 
      StringBuilder buf = new StringBuilder(64);
      while (t.hasMoreTokens())
      {
         buf.append(t.nextToken());
         if (t.hasMoreTokens())
         {
            buf.append('/');
         }
      }
      String resource = buf.toString();
      
      // get URL arguments as a map ready for AppUrl lookup
      Map<String, String> args = new HashMap<String, String>(req.getParameterMap().size(), 1.0f);
      Enumeration names = req.getParameterNames();
      while (names.hasMoreElements())
      {
         String name = (String)names.nextElement();
         args.put(name, req.getParameter(name));
      }
      
      // resolve app url object to process this resource
      if (logger.isDebugEnabled())
         logger.debug("Matching resource URL: " + resource + " args: " + args.toString());
      ApplicationUrl appUrl = matchAppUrl(resource, args);
      if (appUrl == null)
      {
         logger.warn("No Application URL mapping found for resource: " + resource);
         res.setStatus(HttpServletResponse.SC_NOT_FOUND);
         return;
      }
      
      // TODO: what caching here...?
      setNoCacheHeaders(res);
      
      try
      {
         // retrieve the page instance via the application url - will throw a runtime exception if
         // the page cannot be found or fails to retrieve from the local store or repository
         PageInstance page = appUrl.getPageInstance(resource);
         if (logger.isDebugEnabled())
            logger.debug("PageInstance: " + page.toString());
         
         // set response content type and charset
         res.setContentType(MIMETYPE_HTML);
         
         // TODO: authenticate - or redirect to login page etc...
         if (authenticate(getServletContext(), page.Authentication))
         {
            // setup the webapp context path for the webscript runtime template loader to use when rebuilding urls
            PageRendererContext context = new PageRendererContext();
            context.RequestURI = uri;
            context.RequestPath = req.getContextPath();
            context.PageInstance = page;
            
            // handle a clicked UI component link - look for id+url
            // TODO: keep state of page? i.e. multiple webscripts can be hosted and clicked...
            String compId = req.getParameter(PARAM_COMPONENT_ID);
            if (compId != null)
            {
               String compUrl = req.getParameter(PARAM_COMPONENT_URL);
               if (logger.isDebugEnabled())
                  logger.debug("Clicked component found: " + compId + " URL: " + compUrl);
               context.ComponentId = compId;
               context.ComponentUrl = compUrl;
            }
            this.uicomponentTemplateLoader.setContext(context);
            
            // Process the page template using our custom loader - the loader will find and buffer
            // individual included webscript output into the Writer out for the servlet page.
            // TODO: find the page template path
            if (logger.isDebugEnabled())
               logger.debug("Page template resolved as: " + page.PageTemplate);
            
            // execute the templte to render the page - based on the current page definition
            processTemplatePage(page.PageTemplate, page, req, res);
         }
         if (logger.isDebugEnabled())
         {
            long endTime = System.currentTimeMillis();
            logger.debug("Time to render page: " + (endTime - startTime) + "ms");
         }
      }
      catch (Throwable err)
      {
         throw new AlfrescoRuntimeException("Error occurred during page rendering. App Url: " +
               appUrl.toString() + " with error: " + err.getMessage(), err);
      }
   }
   
   /**
    * Match the specified resource url against an app url object, which in turn is responsible
    * for locating the correct Page. 
    *  
    * @param resource   
    * @param args       
    */
   private ApplicationUrl matchAppUrl(String resource, Map<String, String> args)
   {
      // TODO: match against app urls loaded from app registry
      //       retrieve the application urls from a Store that may reference the repo and/or a local 
      ApplicationUrl testUrl = new ApplicationUrl(this.searchPath);
      return testUrl;
   }
   
   /**
    * Execute the template to render the main page - based on the specified page definition config
    * @throws IOException
    */
   private void processTemplatePage(
         String templatePath, PageInstance page, HttpServletRequest req, HttpServletResponse res)
      throws IOException
   {
      // TODO: retrieve the template from the remote repo - or from cache
      templateProcessor.process(templatePath, getModel(page, req), res.getWriter());
   }
   
   /**
    * @return model to use for UI Component template page execution
    */
   private Object getModel(PageInstance page, HttpServletRequest req)
   {
      Map<String, Object> model = new HashMap<String, Object>(8);
      model.put("url", new URLHelper(req.getContextPath()));
      model.put("description", page.Description);
      model.put("title", page.Title);
      model.put("theme", page.Theme);
      return model;
   }
   
   private Config getConfig()
   {
      return this.configService.getConfig("PageRenderer");
   }
   
   private static boolean authenticate(ServletContext sc, RequiredAuthentication auth)
   {
      // TODO: authenticate via call to Alfresco server - using web-app config?
      return true;
   }
   
   /**
    * Apply the headers required to disallow caching of the response in the browser
    */
   private static void setNoCacheHeaders(HttpServletResponse res)
   {
      res.setHeader("Cache-Control", "no-cache");
      res.setHeader("Pragma", "no-cache");
   }
   
   
   /**
    * WebScript runtime for the PageRenderer servlet.
    */
   private class PageRendererWebScriptRuntime extends AbstractRuntime
   {
      private PageComponent component;
      private PageRendererContext context;
      private String webScript;
      private String scriptUrl;
      private String encoding;
      private ByteArrayOutputStream baOut = null;
      
      PageRendererWebScriptRuntime(
            PageComponent component, PageRendererContext context, String webScript, String executeUrl, String encoding)
      {
         super(PageRendererServlet.this.container);
         this.component = component;
         this.context = context;
         this.webScript = webScript;
         this.scriptUrl = executeUrl;
         this.encoding = encoding;
         if (logger.isDebugEnabled())
            logger.debug("Constructing runtime for url: " + executeUrl);
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
         // set the component properties as the additional request parameters
         Map<String, String> properties = new HashMap<String, String>();
         properties.putAll(component.Properties);
         
         //
         // TODO: add/replace the "well known" context token attributes
         //
         
         return new WebScriptPageRendererRequest(this, scriptUrl, match, properties);
      }

      @Override
      protected WebScriptResponse createResponse()
      {
         // create a response object that we control to write to a temporary output
         // we later use that as the source for the webscript "template"
         try
         {
            this.baOut = new ByteArrayOutputStream(4096);
            BufferedWriter wrOut = new BufferedWriter(
                  encoding == null ? new OutputStreamWriter(baOut) : new OutputStreamWriter(baOut, encoding));
            return new WebScriptPageRendererResponse(this, context, component.Id, wrOut, baOut);
         }
         catch (UnsupportedEncodingException err)
         {
            throw new AlfrescoRuntimeException("Unsupported encoding.", err);
         }
      }

      @Override
      protected String getScriptMethod()
      {
         return "GET";
      }
      
      public Reader getResponseReader()
      {
         try
         {
            if (baOut == null)
            {
               return null;
            }
            else
            {
               return new BufferedReader(new InputStreamReader(
                     encoding == null ? new ByteArrayInputStream(baOut.toByteArray()) :
                        new ByteArrayInputStream(baOut.toByteArray()), encoding));
            }
         }
         catch (UnsupportedEncodingException err)
         {
            throw new AlfrescoRuntimeException("Unsupported encoding.", err);
         }
      }

      @Override
      protected Authenticator createAuthenticator()
      {
         return null;
      }
   }
   
   /**
    * Simple implementation of a WebScript URL Request for a webscript on the page
    */
   private class WebScriptPageRendererRequest extends WebScriptRequestURLImpl
   {
      private Map<String, String> parameters;
      
      WebScriptPageRendererRequest(Runtime runtime, String scriptUrl, Match match, Map<String, String> attributes)
      {
         super(runtime, scriptUrl, match);
         this.parameters = attributes;
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
   }
   
   /**
    * Implementation of a WebScript Response object for PageRenderer servlet
    */
   private class WebScriptPageRendererResponse extends WebScriptResponseImpl
   {
      private Writer outWriter;
      private OutputStream outStream;
      private PageRendererContext context;
      private String componentId;
      
      public WebScriptPageRendererResponse(
            Runtime runtime, PageRendererContext context, String componentId, Writer outWriter, OutputStream outStream)
      {
         super(runtime);
         this.context = context;
         this.componentId = componentId;
         this.outWriter = outWriter;
         this.outStream = outStream;
      }
      
      public String encodeScriptUrl(String url)
      {
         // encode to allow presentation tier webscripts to call themselves non this page
         // needs the servlet URL plus args to identify the webscript and it's new url
         return context.RequestPath + context.RequestURI + "?" + PARAM_COMPONENT_URL + "=" +
                URLEncoder.encode(url) + "&" + PARAM_COMPONENT_ID + "=" + componentId;
      }

      public String getEncodeScriptUrlFunction(String name)
      {
         // TODO: may be required?
         return null;
      }

      public OutputStream getOutputStream() throws IOException
      {
         return this.outStream;
      }

      public Writer getWriter() throws IOException
      {
         return this.outWriter;
      }

      public void reset()
      {
         // not supported
      }

      public void setCache(Cache cache)
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
   
   /**
    * Template loader that resolves and executes UI WebScript components by looking up layout keys
    * in the template against the component definition service URLs for the page.
    */
   private class UIComponentTemplateLoader implements TemplateLoader
   {
      private ThreadLocal<PageRendererContext> context = new ThreadLocal<PageRendererContext>();
      private long last = 0L;
      
      public void closeTemplateSource(Object templateSource) throws IOException
      {
         // nothing to do
      }

      public Object findTemplateSource(String name) throws IOException
      {
         // The webscript is looked up based on the key in the #include directive - it must
         // be of the form [somekey] so that it can be recognised by the loader
         
         // most templates included by this loader will be children of other templates
         // unfortunately FreeMarker attempts to build paths for you to child templates - they are not
         // really children - so this information must be discarded
         //if (name.startsWith("avm://"))
         //{
         //   name = name.substring(name.indexOf("/[") + 1);
         //}
         
         if (name.startsWith("[") && name.endsWith("]"))
         {
            String key = name.substring(1, name.length() - 1);
            
            if (logger.isDebugEnabled())
               logger.debug("Found webscript component key: " + key);
            
            return key;
         }
         else
         {
            return null;
         }
      }

      public long getLastModified(Object templateSource)
      {
         return last++;
      }

      public Reader getReader(Object templateSource, String encoding) throws IOException
      {
         String key = templateSource.toString();
         
         // lookup against component def config
         PageRendererContext context = this.context.get();
         PageComponent component = context.PageInstance.Components.get(key);
         if (component == null)
         {
            // TODO: if the lookup fails, throw exception or just ignore the render and log...?
            return new StringReader("ERROR: Failed to find component identified by key '" + key +
                  "' found in template: " + context.PageInstance.PageTemplate);
         }
         
         // NOTE: UI component URLs in config files for page instances should not include /service prefix
         String webscript = component.Url;
         if (webscript.lastIndexOf('?') != -1)
         {
            webscript = webscript.substring(0, webscript.lastIndexOf('?'));
         }
         
         // Execute the webscript and return a Reader to the textual content
         String executeUrl;
         if (component.Id.equals(context.ComponentId) == false)
         {
            executeUrl = context.RequestPath + component.Url;
         }
         else
         {
            // found the component url that was passed in on the servlet request
            executeUrl = context.ComponentUrl;
         }
         PageRendererWebScriptRuntime runtime = new PageRendererWebScriptRuntime(
               component, context, webscript, executeUrl, encoding);
         runtime.executeScript();
         
         // Return a reader from the runtime that executed the webscript - this effectively
         // returns the result as a "template" source to freemarker. Generally this will not itself
         // be a template but it can contain additional freemarker syntax if required - it is up to
         // the template writer to add the parse=[true|false] attribute as appropriate to the #include
         return runtime.getResponseReader();
      }
      
      /**
       * Setter to apply the current context for this template execution. A ThreadLocal wrapper is used
       * to allow multiple servlet threads to run using the same TemplateLoader (there can only be one)
       * but with a different context for each execution thread.
       */
      public void setContext(PageRendererContext context)
      {
         this.context.set(context);
      }
   }
   
   /**
    * Simple structure class representing the current page request context
    */
   private static class PageRendererContext
   {
      PageInstance PageInstance;
      String RequestURI;
      String RequestPath;
      String ComponentId;
      String ComponentUrl;
   }
   
   /**
    * Helper to return context path for generating urls
    */
   public static class URLHelper
   {
      String context;

      public URLHelper(String context)
      {
         this.context = context;
      }

      public String getContext()
      {
         return context;
      }
   }
}
