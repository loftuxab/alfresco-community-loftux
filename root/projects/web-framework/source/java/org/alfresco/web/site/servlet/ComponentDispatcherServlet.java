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
package org.alfresco.web.site.servlet;

import java.util.StringTokenizer;

import javax.servlet.ServletException;

import org.alfresco.web.framework.render.PresentationUtil;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderFocus;
import org.alfresco.web.framework.render.RenderMode;
import org.alfresco.web.site.exception.RequestDispatchException;

/**
 * Responsible for dispatching a single component.
 * 
 * Constructs the request context as per usual construction pattern
 * so as to provide context during component execution.
 * 
 * A component can be any kind of Web Framework component
 * (Web Script, JSP, JavaBean, etc)
 * 
 * URLs are expected to be invoked as shown:
 * 
 * /c/<componentId>                    -> runs the view mode for component
 * /c/view/<componentId>            -> runs the view mode for component
 * /c/view/header/<componentId>        -> runs the view mode for component (and processes the 'header')
 * /c/edit/<componentId>               -> runs the edit mode for component
 * /c/edit/header/<componentId>     -> runs the edit mode for component (and processes the 'header')
 * 
 * The component is then executed and its output streamed back.
 * 
 * @author muzquiano
 */
public class ComponentDispatcherServlet extends DispatcherServlet
{
    public void init() throws ServletException
    {
        super.init();
    }

    /**
     * Dispatch component
     * 
     * @throws RequestDispatchException
     */
    protected void dispatch(RenderContext context)
        throws RequestDispatchException
    {
        String uri = context.getRequest().getRequestURI();
        
        // skip server context path and build the path to the resource we are looking for
        uri = uri.substring(context.getRequest().getContextPath().length());
        
        // validate and return the resource path - stripping the servlet context
        StringTokenizer t = new StringTokenizer(uri, "/");
        String servletName = t.nextToken();
        if (!t.hasMoreTokens())
        {
            throw new RequestDispatchException("Invalid URL: " + uri);
        }

        // render mode and render focus
        RenderMode renderMode = null;
        RenderFocus renderFocus = null;
                
        String componentId = t.nextToken();
        
        // was this the render mode?
        try
        {
            renderMode = RenderMode.fromString(componentId);
        }
        catch (IllegalArgumentException iae) 
        { 
            // this means it wasn't an enum type
        }
        
        // if we received the render mode, advance the token
        if (renderMode != null)
        {
            componentId = t.nextToken();
            
            try
            {
                renderFocus = RenderFocus.fromString(componentId);
            }
            catch (IllegalArgumentException iae)
            {
                // this means it wasn't an enum type
            }
            
            // advance the token if we found a render focus
            if (renderFocus != null)
            {
                componentId = t.nextToken();
            }
        }
        
        // some defaults
        if (renderMode == null)
        {
            renderMode = RenderMode.VIEW;
        }
        if (renderFocus == null)
        {
            renderFocus = RenderFocus.BODY;
        }
        
        // set the render mode
        context.setRenderMode(renderMode);

        // do the render of the component
        PresentationUtil.renderComponent(context, renderFocus, componentId);        
    }
}
