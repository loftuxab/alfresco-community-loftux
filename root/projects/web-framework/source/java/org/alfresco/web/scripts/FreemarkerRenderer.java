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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.config.RuntimeConfig;
import org.alfresco.web.site.exception.RendererExecutionException;
import org.alfresco.web.site.renderer.AbstractRenderer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author muzquiano
 */
public class FreemarkerRenderer extends AbstractRenderer
{
    public void execute(RequestContext context, HttpServletRequest request,
            HttpServletResponse response, RuntimeConfig modelConfig)
            throws RendererExecutionException
    {
        // get the renderer destination property
        String uri = this.getRenderer();
        
        // the current format
        String format = context.getCurrentFormatId();
        
        // The current template and page
        //Page page = context.getCurrentPage();
        
        // get the template processor
        String processorId = context.getConfig().getRendererProperty(getRendererType(), "processor-bean");
        if(processorId == null || "".equals(processorId))
        {
            processorId = "webscripts.web.templateprocessor";
        }
        ServletContext servletContext = request.getSession().getServletContext();
        ApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        TemplateProcessor templateProcessor = (PresentationTemplateProcessor)appContext.getBean(processorId);

        // build the model
        Map<String, Object> model = new HashMap<String, Object>(8);
        ModelHelper.populateTemplateModel(context, model);

        // path to the template (switches on format)
        String templateName = uri + ((format != null && format.length() != 0 && !context.getConfig().getDefaultFormatId().equals(format)) ? ("." + format + ".ftl") : ".ftl");
        
        // execute
        try
        {
            templateProcessor.process(templateName, model, response.getWriter());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
