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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.scripts;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.tagext.BodyTagSupport;

import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.site.taglib.RegionTag;

import freemarker.core.Environment;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * A generic Freemarker Directive wrapper around the Java Tag classes
 * 
 * @author muzquiano
 */
public class RegionFreemarkerTagDirective extends FreemarkerTagSupportDirective
{
    public RegionFreemarkerTagDirective(RenderContext context)
    {
        super(context);
    }

    public void execute(Environment env, Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException, IOException
    {
        // instantiate the tag class
        RegionTag tag = new RegionTag();

        // Region name
        TemplateModel idValue = (TemplateModel)params.get("id");
        if (idValue instanceof TemplateScalarModel == false)
        {
           throw new TemplateModelException("The 'id' parameter to a region directive must be a string.");
        }
        String regionId = ((TemplateScalarModel)idValue).getAsString();
        tag.setName(regionId);
        
        // Region Scope
        TemplateModel scopeValue = (TemplateModel)params.get("scope");
        if (scopeValue instanceof TemplateScalarModel == false)
        {
           throw new TemplateModelException("The 'scope' parameter to a region directive must be a string.");
        }
        String scope = ((TemplateScalarModel)scopeValue).getAsString();
        tag.setScope(scope);
        
        // Region Access
        boolean protectedRegion = false;
        TemplateModel protValue = (TemplateModel)params.get("protected");
        if (protValue != null)
        {
           if (protValue instanceof TemplateBooleanModel == false)
           {
              throw new TemplateModelException("The 'protected' parameter to a region directive must be a boolean.");
           }
           protectedRegion = ((TemplateBooleanModel)protValue).getAsBoolean();
           if (protectedRegion)
           {
               tag.setAccess("protected");
           }
        }
        
        // Region chrome
        // Allows region directives to explicitly specify which chrome they want to use
        TemplateModel chromeValue = (TemplateModel)params.get("chrome");
        if (chromeValue != null)
        {
            if (chromeValue instanceof TemplateScalarModel == false)
            {
               throw new TemplateModelException("The 'chrome' parameter to a region directive must be a string.");
            }
            String chromeId = ((TemplateScalarModel)chromeValue).getAsString();
            tag.setChrome(chromeId);
        }
        

        // copy in body content (if there is any)
        String bodyContentString = null;
        if (body != null)
        {
            if (tag instanceof BodyTagSupport)
            {
                // dump out the Freemarker body content
                StringBuilderWriter bodyStringWriter = new StringBuilderWriter(256);
                body.render(bodyStringWriter);
                bodyContentString = bodyStringWriter.toString();
            }
        }
        
        /**
         * Execute the tag
         */
        try
        {
            String output = executeTag(tag, bodyContentString);
            env.getOut().write(output);
        }
        catch (Exception ex)
        {
            throw new TemplateException("Unable to process tag and commit output", ex, env);
        }        
    }
}
