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

import org.alfresco.web.framework.exception.PageRendererExecutionException;
import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.render.AbstractRenderer;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderFocus;
import org.alfresco.web.framework.render.RenderHelper;
import org.alfresco.web.framework.render.Renderer;
import org.alfresco.web.framework.render.RendererType;
import org.alfresco.web.site.Timer;

/**
 * The primary duty of this bean is to determine the appropriate
 * template to execute and the begin the processing of that template.
 * 
 * It must base this decision off of the given context which will usually
 * be the base render context that lightly wraps the request.
 * 
 * @author muzquiano
 */
public class PageRenderer extends AbstractRenderer
{
    /**
     * Renders the current page
     */
    public void body(RenderContext parentContext)
        throws RendererExecutionException
    {
        // get the page
        Page page = (Page) parentContext.getObject();
        if (page == null)
        {
            throw new PageRendererExecutionException("Unable to render page: null");
        }

        // look up the page template
        TemplateInstance template = page.getTemplate(parentContext);
        if (template == null)
        {
            throw new PageRendererExecutionException("Unable to locate template for page: " + page.getId());
        }

        // provision a template-bound render context
        // execute the renderer
        RenderContext context = RenderHelper.provideRenderContext(parentContext, template);
        try
        {
            // start a timer
            if (Timer.isTimerEnabled())
                Timer.start(context, "PageRendererBody-" + page.getId());

            // loads the "template renderer" bean and executes it
            Renderer renderer = RenderHelper.getRenderer(RendererType.TEMPLATE);
            renderer.render(context, RenderFocus.BODY);
        }
        finally
        {
            context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(context, "PageRendererBody-" + page.getId());
        }
    }

    /**
     * Renders the header for the page
     */
    public void header(RenderContext context)
        throws RendererExecutionException
    {
    }

    /**
     * Renders the footer for the page
     */
    public void footer(RenderContext context)
        throws RendererExecutionException
    {
    }
}