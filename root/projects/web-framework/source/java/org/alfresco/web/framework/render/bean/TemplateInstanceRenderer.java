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
package org.alfresco.web.framework.render.bean;

import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.model.Component;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.render.AbstractRenderer;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderFocus;
import org.alfresco.web.framework.render.RenderHelper;
import org.alfresco.web.framework.render.RenderUtil;
import org.alfresco.web.site.Timer;

/**
 * Bean responsible for rendering a template instance.
 * 
 * @author muzquiano
 */
public class TemplateInstanceRenderer extends AbstractRenderer
{
    private void calculateComponentDependencies(RenderContext parentContext)
        throws RendererExecutionException
    {
        TemplateInstance template = (TemplateInstance) parentContext.getObject();
        
        // We need to preprocess the template to calculate the component dependencies
        // - component dependencies are resolved only when they have all executed.
        // First pass is very fast as template pages themselves have very little implicit content and
        // any associated behaviour logic is executed only once, with the result stored for the 2nd pass.
        // The critical performance path is in executing the WebScript components - which is only
        // performed during the second pass of the template - once component references are all resolved.
        RenderContext preContext = RenderHelper.provideRenderContext(parentContext, template);
        try
        {
            if (Timer.isTimerEnabled())
                Timer.start(preContext, "TemplateInstanceRenderer1-" + template.getId());

            // wrap the render context
            preContext = RenderHelper.wrapRenderContext(preContext);
            
            // set the context into "passive" mode            
            preContext.setPassiveMode(true);
            
            // get the template processor and process it
            // this commits the template output to output stream
            RenderHelper.processTemplate(preContext, RenderFocus.BODY, template);
        }
        finally
        {
            if (Timer.isTimerEnabled())
                Timer.stop(preContext, "TemplateInstanceRenderer1-" + template.getId());
            
            // TODO: is this necessary?  think not
            // switch out of passive mode
            preContext.setPassiveMode(false);
            
            // release the render context
            preContext.release();
        }    
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractRenderer#header(org.alfresco.web.framework.render.RenderContext)
     */
    public void header(RenderContext parentContext)
        throws RendererExecutionException
    {
        TemplateInstance template = (TemplateInstance) parentContext.getObject();
        if (template != null)
        {        
            // FIRST PASS - calculate component dependencies
            calculateComponentDependencies(parentContext);
    
            // SECOND PASS - render output of components        
            Component component = null;
            Component[] components = parentContext.getRenderingComponents();
            if (components != null)
            {
                for (int i = 0; i < components.length; i++)
                {
                    component = components[i];
                    
                    RenderUtil.renderComponent(parentContext, RenderFocus.HEADER, component.getId());
                    print(parentContext, RenderUtil.NEWLINE);
                }
            }
        }
        
        postHeaderProcess(parentContext);
    }
    
    /**
     * Renders the current template
     */
    public void body(RenderContext parentContext)
        throws RendererExecutionException
    {
        TemplateInstance template = (TemplateInstance) parentContext.getObject();
        
        // FIRST PASS - calculate component dependencies
        calculateComponentDependencies(parentContext);
        
        // SECOND PASS - render output of template
        RenderContext context = RenderHelper.provideRenderContext(parentContext, template);
        try
        {
            if (Timer.isTimerEnabled())
                Timer.start(context, "TemplateInstanceRenderer2-" + template.getId());
            
            // get the template processor and process it
            // this commits the template output to output stream
            RenderHelper.processTemplate(context, RenderFocus.BODY, template);
        }
        finally
        {
            if (Timer.isTimerEnabled())
                Timer.stop(context, "TemplateInstanceRenderer2-" + template.getId());

            // release the render context
            context.release();            
        }
    }
    
    public void postHeaderProcess(RenderContext context)
        throws RendererExecutionException
    {
    }
}