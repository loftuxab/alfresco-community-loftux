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
import java.io.Writer;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.page.PageRendererServlet.PageRendererContext;
import org.alfresco.web.scripts.Store;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.core.Environment;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * Custom @region FreeMarker directive.
 * 
 * A 'region' defines an area in a template that is replaced by a component implementation at runtime.
 * 
 * Regions are keyed by 'id' and 'scope' - scope can be one of the following:
 *  global - a site wide component, all templates containing a region with the same id see this component 
 *  template - a template specific component, all pages sharing this template see this component
 *  page - a page specific component, visible on this page only
 * 
 * A boolean parameter 'protected' can be optionally specified which declares a region as locked. Locked
 * regions are not editable at design time. The default is false if omitted.
 * 
 * Example:
 * <@region id="mycomponent" scope="template" protected=false>
 *    some optional body content - displayed if there is no component resolved
 * </@region>
 * 
 * @author Kevin Roast
 */
public class RegionDirective implements TemplateDirectiveModel
{
   private static final String SCOPE_GLOBAL   = "global";
   private static final String SCOPE_TEMPLATE = "template";
   private static final String SCOPE_PAGE     = "page";
   
   private static Log logger = LogFactory.getLog(RegionDirective.class);
   
   private PageRendererContext context;
   
   /** the Store to lookup components in */
   private Store store;
   
   /** the PageInstance this directive is contained in */
   private PageInstance page;
   
   /** template rendering mode */
   private boolean active;
   
   private Map<String, CacheValue<PageComponent>> cache;
   
   
   /**
    * Construction.
    * 
    * @param context    PageRenderer context
    * @param store      To lookup components in
    * @param cache      Cache for PageComponents
    * @param page       The containing page instance
    * @param active     The template rendering mode:
    *   active=false (passive mode) - first pass with component lookup but no component rendering 
    *   active=true (active mode) - second pass with component renderering
    */
   public RegionDirective(
         PageRendererContext context, Store store, Map<String, CacheValue<PageComponent>> cache,
         PageInstance page, boolean active)
   {
      if (context == null)
      {
         throw new IllegalArgumentException("PageRendererContext is mandatory.");
      }
      this.context = context;
      if (store == null)
      {
         throw new IllegalArgumentException("RegionDirective component Store is mandatory.");
      }
      this.store = store;
      if (page == null)
      {
         throw new IllegalArgumentException("RegionDirective page instance is mandatory."); 
      }
      this.page = page;
      this.active = active;
      this.cache = cache;
   }
   
   /* (non-Javadoc)
    * @see freemarker.template.TemplateDirectiveModel#execute(freemarker.core.Environment, java.util.Map, freemarker.template.TemplateModel[], freemarker.template.TemplateDirectiveBody)
    */
   public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
         throws TemplateException, IOException
   {
      // parse and validate the parameters to the region directive
      TemplateModel idValue = (TemplateModel)params.get("id");
      if (idValue instanceof TemplateScalarModel == false)
      {
         throw new TemplateModelException("The 'id' parameter to a region directive must be a string.");
      }
      String regionId = ((TemplateScalarModel)idValue).getAsString();
      
      TemplateModel scopeValue = (TemplateModel)params.get("scope");
      if (scopeValue instanceof TemplateScalarModel == false)
      {
         throw new TemplateModelException("The 'scope' parameter to a region directive must be a string.");
      }
      String scope = ((TemplateScalarModel)scopeValue).getAsString();
      
      boolean protectedRegion = false;
      TemplateModel protValue = (TemplateModel)params.get("protected");
      if (protValue != null)
      {
         if (protValue instanceof TemplateBooleanModel == false)
         {
            throw new TemplateModelException("The 'protected' parameter to a region directive must be a boolean.");
         }
         protectedRegion = ((TemplateBooleanModel)protValue).getAsBoolean();
      }
      
      // resolve the source Id - relative to the component scope
      String sourceId;
      if (scope.equalsIgnoreCase(SCOPE_GLOBAL))
      {
         sourceId = SCOPE_GLOBAL;
      }
      else if (scope.equalsIgnoreCase(SCOPE_TEMPLATE))
      {
         sourceId = this.page.getTemplate();
      }
      else if (scope.equalsIgnoreCase(SCOPE_PAGE))
      {
         sourceId = this.page.getPageId();
      }
      else
      {
         throw new AlfrescoRuntimeException("Unknown component scope: " + scope +
               ". Was expecting one of 'global', 'template' or 'page'.");
      }
      
      // the directive runs in one of two modes:
      //    'passive' mode the region will lookup the component instance and add it to the containing page object 
      //    'active' mode the region will render component chrome and include the component result
      
      PageComponent component;
      if (!active)
      {
         if (logger.isDebugEnabled())
            logger.debug(" Looking up component: scope=" + scope + " region=" + regionId + " source-id=" + sourceId);
         
         // query component definition keyed by "scope", "region" and "source-id" 
         component = queryComponentInstance(scope, regionId, sourceId);
         
         // if component has been found, save the reference against the PageInstance ready for the rendering pass
         if (component != null)
         {
            this.page.setComponent(component);
         }
      }
      else
      {
         component = this.page.getComponent(PageComponent.buildComponentId(scope, regionId, sourceId));
         
         // if component has been found, include it, otherwise render the default body content
         if (component != null)
         {
            if (logger.isDebugEnabled())
               logger.debug(" Rendering component: scope=" + scope + " region=" + regionId + " source-id=" + sourceId);
            
            // Apply component "chrome" pre/post component render - as per frame config element
            // - output surrounding DIV (with ID) or IFRAME (with URL) structure
            switch (component.getFrameType())
            {
               case DIV:
                  // TODO: style the chrome?
                  // write an ID into the div element - this can be used by the component writer
                  env.getOut().write("<div id='" + component.getId() + "'>");
                  break;
               case IFRAME:
                  // TODO: token context replacement on the URL here?
                  // TODO: width and height for the iframe from well known component config properties
                  env.getOut().write("<iframe src='/service" + component.getUrl() + "'>"); 
                  break;
            }
            
            // inform the component writer of the element id via 'htmlid' component properties
            component.getProperties().put(PageComponent.PROP_HTMLID, component.getId());
            
            renderComponent(env.getOut(), component);
            
            switch (component.getFrameType())
            {
               case DIV:
                  env.getOut().write("</div>");
                  break;
               case IFRAME:
                  env.getOut().write("</iframe>"); 
                  break;
            }
         }
         else
         {
            if (logger.isDebugEnabled())
            {
               logger.debug(" ***Failed to resolve component: " +
                     PageComponent.buildComponentId(scope, regionId, sourceId));
            }
            if (body != null)
            {
               body.render(env.getOut());
            }
         }
      }
   }
   
   /**
    * Return the reader for the custom WebScript runtme using the PageRenderer as the outer context.
    * The output of the UI component is buffered and a reader to it is returned to the
    * template engine. The stream is then output as the result of the <@region> directive.
    */
   public void renderComponent(Writer out, PageComponent component) throws IOException
   {
      // NOTE: UI component URIs in page instance config files should not include /service prefix
      // Replace context/well known tokens in the component url
      String componentUrl = UriUtils.replaceUriTokens(component.getUrl(), context.Tokens);
      String webscript = componentUrl;
      if (webscript.lastIndexOf('?') != -1)
      {
         webscript = webscript.substring(0, webscript.lastIndexOf('?'));
      }

      // Execute the webscript and return a Reader to the textual content
      String executeUrl;
      if (component.getId().equals(context.ComponentId) == false)
      {
         executeUrl = context.RequestPath + componentUrl;
      }
      else
      {
         // else we found a clicked component url that was passed in on the servlet request
         executeUrl = context.ComponentUrl;
      }

      // Generate a runtime to execute the webscript ui component
      PageComponentWebScriptRuntime runtime = new PageComponentWebScriptRuntime(
            out, context, component, webscript, executeUrl);
      runtime.executeScript();
   }
   
   /**
    * Retrieve the component instance given the scope, region and source key attributes.
    * 
    * @param scope      Component scope, one of 'global', 'template' or 'page'.
    * @param regionId   Id of the component region
    * @param sourceId   SourceId - relative to scope, this will either be 'global', the template instance
    *                   id or the page instance id.
    * 
    * @return the PageComponent if found or null if failed to resolve
    */
   private PageComponent queryComponentInstance(String scope, String regionId, String sourceId)
      throws IOException
   {
      // generate component ID as key for lookup when including via custom template loader
      String key = PageComponent.buildComponentId(scope, regionId, sourceId);
      
      // retrieve component binding based on key
      // generate the component XML file name from the key - we load the file by this convention
      String path = key + ".xml";
      PageComponent component = null;
      if (store.hasDocument(path))
      {
         if (cache != null)
         {
            // obtain component from cache
            long lastModified = store.lastModified(path);
            CacheValue<PageComponent> v = cache.get(key);
            if (v != null)
            {
               // found component - has it been modified in the Store since caching?
               if (lastModified <= v.LastModified)
               {
                  // has not changed, we can use the cached version
                  component = v.Value;
               }
            }
            if (component == null)
            {
               // either the component has been modified since caching, or was never added
               // so - create new from store and update cache based on last modified timestamp
               component = new PageComponent(store, path);
               v = new CacheValue(component, lastModified);
               cache.put(key, v);
            }
         }
         else
         {
            component = new PageComponent(store, path);
         }
      }
      return component;
   }
}
