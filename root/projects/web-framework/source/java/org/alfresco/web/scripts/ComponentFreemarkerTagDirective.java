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

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.taglib.ComponentTag;

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
public class ComponentFreemarkerTagDirective extends FreemarkerTagSupportDirective
{
    public ComponentFreemarkerTagDirective(RequestContext context)
    {
        super(context);
    }

    public void execute(Environment env, Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException, IOException
    {
        // instantiate the tag class
        ComponentTag tag = new ComponentTag();

        
        
        // Component ID
        TemplateModel idValue = (TemplateModel)params.get("componentId");
        if(idValue != null)
        {
            if (idValue instanceof TemplateScalarModel == false)
            {
               throw new TemplateModelException("The 'id' parameter to a region directive must be a string.");
            }
            String componentId = ((TemplateScalarModel)idValue).getAsString();
            tag.setComponent(componentId);
        }
        
        
        // Chrome ID
        TemplateModel chromeValue = (TemplateModel)params.get("chrome");
        if(chromeValue != null)
        {        
            if (chromeValue instanceof TemplateScalarModel == false)
            {
               throw new TemplateModelException("The 'chrome' parameter to a region directive must be a string.");
            }
            String chrome = ((TemplateScalarModel)chromeValue).getAsString();
            tag.setChrome(chrome);
        }
        
        
        // Chromeless attribute        
        boolean chromeless = false;
        TemplateModel chromelessValue = (TemplateModel)params.get("chromeless");
        if (chromelessValue != null)
        {
           if (chromelessValue instanceof TemplateBooleanModel == false)
           {
              throw new TemplateModelException("The 'chromeless' parameter to a region directive must be a boolean.");
           }
           chromeless = ((TemplateBooleanModel)chromelessValue).getAsBoolean();
           tag.setChromeless(chromeless);
        }


        // execute the tag
        String output = executeTag(tag);

        // commit the output
        try
        {
            env.getOut().write(output);
            env.getOut().flush();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
