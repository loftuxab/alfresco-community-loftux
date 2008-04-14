/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.web.scripts;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.scripts.servlet.RegionDirective;
import org.alfresco.web.scripts.servlet.TemplateInstanceConfig;
import org.alfresco.web.scripts.servlet.PageRendererServlet.URLHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.config.RuntimeConfig;
import org.alfresco.web.site.exception.RendererExecutionException;
import org.alfresco.web.site.renderer.AbstractRenderer;

/**
 * @author muzquiano
 */
public class FreemarkerRenderer extends AbstractRenderer
{
    public void executeImpl(RequestContext context, HttpServletRequest request,
            HttpServletResponse response, RuntimeConfig modelConfig)
            throws RendererExecutionException
    {
        // pull values from the config
        // these are values that are stored on the component instance
        // or on the template instance
        String requestUri = (String) modelConfig.get("uri"); // i.e. /test/component1
        String requestPath = (String) modelConfig.get("requestPath"); // i.e. /test/component1
        if (requestPath == null)
            requestPath = "/service";

        /*
         // the format
         String formatId = context.getCurrentFormatId();
         
         // the template
         Template template = (Template) modelConfig.getObject();
         Page page = context.getCurrentPage();

         // the template type
         TemplateType templateType = (TemplateType) template.getTemplateType(context);
         
         // build template name by convention:
         // mytemplate[.format].ftl
         //String templateName = templateInstance.getTemplateType() + ((format != null && format.length() != 0) ? ("." + format + ".ftl") : ".ftl");
         
         long startTime = 0;
         if (context.getLogger().isDebugEnabled())
         {
         context.getLogger().debug("Executing 1st template pass, looking up components...");
         startTime = System.nanoTime();
         }
         
         Map<String, Object> templateModel = getModel(page, req, false);
         Map<String, Object> model = new HashMap<String, Object>(8);
         
         URLHelper urlHelper = new URLHelper(request);
         model.put("url", urlHelper);
         model.put("description", page.getDescription());
         model.put("title", page.getTitle());
         model.put("theme", page.getTheme());
         //model.put("head", page.getHeaderRenderer(webscriptsRegistry, templateProcessor, urlHelper));

         // add the custom 'region' directive implementation - one instance per model as we pass in template/page 
         //model.put("region", new RegionDirective(componentStore, componentCache, page, active));
         
         return model;
         
         
         // execute any attached javascript behaviour for this template
         // the behaviour plus the config is responsible for specialising the template
         String scriptPath = templateInstance.getTemplateType() + ".js";
         ScriptContent script = templateStore.getScriptLoader().getScript(scriptPath);
         if (script != null)
         {
         Map<String, Object> scriptModel = new HashMap<String, Object>(8, 1.0f);
         // add the template config properties to the script model
         scriptModel.putAll(templateInstance.getPropetries());
         // results from the script should be placed into the root 'model' object
         scriptModel.put("model", resultModel);
         
         scriptProcessor.executeScript(script, scriptModel);
         
         // merge script results model into the template model
         for (Map.Entry<String, Object> entry : resultModel.entrySet())
         {
         // retrieve script model value and unwrap each java object from script object
         Object value = entry.getValue();
         Object templateValue = scriptProcessor.unwrapValue(value);
         templateModel.put(entry.getKey(), templateValue);
         }
         }
         
         // therefore this is very fast as template pages themselves have very little content themselves
         // and the logic for the template is executed once only with the result stored for the 2nd pass
         // the critical performance path is in executing the webscript components - which is only
         // performed on the second pass of the template once component references are all resolved
         templateProcessor.process(template, templateModel,
         new Writer ()
         {
         public void write(char[] cbuf, int off, int len) throws IOException
         {
         }
         
         public void flush() throws IOException
         {
         }
         
         public void close() throws IOException
         {
         }
         });
         
         if (logger.isDebugEnabled())
         {
         long endTime = System.nanoTime();
         logger.debug("...1st pass processed in: " + (endTime - startTime)/1000000f + "ms");
         logger.debug("Executing 2nd template pass, rendering...");
         startTime = System.nanoTime();
         }
         
         // construct template model for 2nd pass
         templateModel = getModel(page, req, true);
         if (script != null)
         {
         // script already executed - so just merge script return model into the template model
         for (Map.Entry<String, Object> entry : resultModel.entrySet())
         {
         // retrieve script model value
         Object value = entry.getValue();
         Object templateValue = scriptProcessor.unwrapValue(value);
         templateModel.put(entry.getKey(), templateValue);
         }
         }
         templateProcessor.process(template, templateModel, res.getWriter());
         
         if (logger.isDebugEnabled())
         {
         long endTime = System.nanoTime();
         logger.debug("...2nd pass processed in: " + (endTime - startTime)/1000000f + "ms");
         }
         }
         else
         {
         throw new AlfrescoRuntimeException("Unable to find template config: " + templateConfig);
         }
         */
    }
}
