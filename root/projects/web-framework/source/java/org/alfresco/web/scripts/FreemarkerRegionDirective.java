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
package org.alfresco.web.scripts;

import java.io.IOException;
import java.util.Map;

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.taglib.RegionTag;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
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
 * Optionally a boolean parameter 'protected' can be specified which declares a region as locked. Locked
 * regions are not editable at design time. The default if omitted is false.
 * 
 * Example:
 * <@region id="mycomponent" scope="template" protected=false>
 *    some optional body content - displayed if there is no component resolved
 * </@region>
 * 
 * @author Kevin Roast
 * @author Michael Uzquiano
 */
public class FreemarkerRegionDirective extends FreemarkerTagSupportDirective
{   
   private RequestContext context;
   
   public FreemarkerRegionDirective(RequestContext context)
   {
       super(context);
   }
      
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
      
      //boolean protectedRegion = false;
      String access = null;
      TemplateModel accessValue = (TemplateModel)params.get("access");
      if (accessValue != null)
      {
         if (accessValue instanceof TemplateScalarModel == false)
         {
            throw new TemplateModelException("The 'access' parameter to a region directive must be a string.");
         }
         access = ((TemplateScalarModel)accessValue).getAsString();
      }
      
      
      /*
      // resolve the source Id - relative to the component scope
      String sourceId;
      if (scope.equalsIgnoreCase(Constants.SCOPE_GLOBAL))
      {
         sourceId = Constants.SCOPE_GLOBAL;
      }
      else if (scope.equalsIgnoreCase(Constants.SCOPE_TEMPLATE))
      {
         sourceId = this.page.getTemplateId();
      }
      else if (scope.equalsIgnoreCase(Constants.SCOPE_PAGE))
      {
         sourceId = this.page.getId();
      }
      else
      {
         throw new AlfrescoRuntimeException("Unknown component scope: " + scope +
               ". Was expecting one of 'global', 'template' or 'page'.");
      }
      */
      
      
      // the tag we want to execute
      RegionTag tag = new RegionTag();
      tag.setAccess(access);
      tag.setName(regionId);
      tag.setScope(scope);
      
      // execute the tag
      String output = executeTag(tag);
      
      // commit the output
      try
      {
          env.getOut().write(output);
          env.getOut().flush();
      }
      catch(Exception ex)
      {
          ex.printStackTrace();
      }

      
      
      
/*      
      
      
      
      
      // find any components that match these bindings
      Component[] components = ModelUtil.findComponents(context, scope, sourceId, regionId, null);
      
      // do we have at least one?
      if(components != null && components.length > 0)
      {
          // just render the first one
          Component component = components[0];
          String componentId = component.getId();
          
          // render the component into dummy objects
          // currently, we can only do this for HttpRequestContext instances
          if(context instanceof HttpRequestContext)
          {
              HttpServletRequest r = (HttpServletRequest) ((HttpRequestContext)context).getRequest();
              
              // execute component with a wrapped request
              WrappedHttpServletRequest request = new WrappedHttpServletRequest(r);
              
              // execute component with a fake response
              FakeHttpServletResponse response = new FakeHttpServletResponse();
              try
              {
                  RenderUtil.renderComponent(context, request, response, componentId);

                  // render the output
                  String output = response.getContentAsString();
                  env.getOut().write(output);                  
              }
              catch(Exception ex)
              {
                  ex.printStackTrace();
              }    
          }
      }
*/          
   }
}
