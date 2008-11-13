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

import java.io.IOException;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.render.AbstractProcessor;
import org.alfresco.web.framework.render.ProcessorContext;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderContextRequest;
import org.alfresco.web.site.RequestUtil;

/**
 * The JSP processor is a delegating processor in that it allows you to
 * pass control of render processing to a specific JSP page.  Thus, folks
 * can dynamically add and remove JSP processors to their heart's content.
 * 
 * @author muzquiano
 */
public class JSPProcessor extends AbstractProcessor
{
    private static final String JSP_FILE_URI = "jsp-file-uri";
    private static final String JSP_PATH_URI = "jsp-path-uri";

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractProcessor#executeHeader(org.alfresco.web.framework.render.ProcessorContext)
     */
    public void executeHeader(ProcessorContext pc)
        throws RendererExecutionException
    {
        // get render context and processor properties
        RenderContext context = pc.getRenderContext();
        String jspPath = this.getProperty(pc, "jsp-path");
                
        try
        {
            if (jspPath != null)
            {
                int x = jspPath.lastIndexOf('.');
                if (x != -1)
                {
                    jspPath = jspPath.substring(0,x) + ".head." + jspPath.substring(x+1, jspPath.length());
                }
    
                // check whether the file exists
                // TODO: this is a bit expensive to do - should we change this?
                ServletContext servletContext = context.getRequest().getSession().getServletContext();
                URL resource = servletContext.getResource(jspPath);
    
                // if it exists, execute it
                if (resource != null)
                {
                    RequestUtil.include(context.getRequest(), 
                            context.getResponse(), jspPath);
                }
            }
        }
        catch (Exception ex)
        {
            throw new RendererExecutionException("Unable to execute 'header' JSP Include: " + jspPath, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractProcessor#executeBody(org.alfresco.web.framework.render.ProcessorContext)
     */
    public void executeBody(ProcessorContext pc)
        throws RendererExecutionException
    {
        // get render context and processor properties
        RenderContext context = pc.getRenderContext();
        String jspPath = this.getProperty(pc, "jsp-path");
        
        try
        {
            // Place the JSP file path onto the render data.
            // This allows it to be retrieved within the JSP page.
            context.setValue(JSP_FILE_URI, jspPath);
            
            // Place the JSP file's parent folder path onto the render data.
            // This allows it to be retrieved within the JSP page.
            int x = jspPath.lastIndexOf('/');
            if (x != -1)
            {
                String pathUri = jspPath.substring(0, x);
                context.setValue(JSP_PATH_URI, pathUri);
            }
            else
            {
                context.setValue(JSP_PATH_URI, "/");
            }
            
            doInclude(context, jspPath);            
        }
        catch (Exception ex)
        {
            throw new RendererExecutionException("Unable to execute 'body' JSP include: " + jspPath, ex);
        }
    }
    
    protected void doInclude(RenderContext context, String jspPath)
        throws ServletException, IOException
    {
        RenderContextRequest request = new RenderContextRequest(context);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher(jspPath);
        
        // if we're dispatching a template, we'll do a forward
        if (context.getObject() != null && context.getObject() instanceof TemplateInstance)
        {
            dispatcher.forward(request, context.getResponse());
        }
        else
        {
            // otherwise, we'll do an include
            dispatcher.include(request, context.getResponse());
        }
    }
}
