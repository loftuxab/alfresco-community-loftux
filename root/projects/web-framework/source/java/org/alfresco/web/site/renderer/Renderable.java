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
package org.alfresco.web.site.renderer;

import org.alfresco.web.site.exception.RendererExecutionException;

/**
 * The Renderable interface is to be implemented by rendering engines
 * that wish to be called upon by the Web Framework to rendition objects
 * to the output stream.
 * 
 * Custom rendering engines simply need to implement this interface and then
 * register themselves within the configuration.
 * 
 * @author muzquiano
 */
public interface Renderable
{
    /**
     * Initialisation hook point. Guarenteed to be call once only and on the
     * first creation of this renderer class. The RendererContext is provided
     * to allow access to say ServletContext and config etc.
     * 
     * @param rendererContext
     */
    public void init(RendererContext rendererContext);
    
    /**
     * Allows the renderer to provide text that should be placed into the
     * header portion of the returned markup.
     * 
     * If null is returned, no markup is placed into the markup header.
     * 
     * @param rendererContext
     * @return
     * @throws RendererExecutionException
     */
    public String head(RendererContext rendererContext)
        throws RendererExecutionException;
    
    /**
     * Executes the renderer.  The renderer, while running in the context
     * of this method, has full access to the original request context,
     * the servlet request and the servlet response.
     * 
     * The renderer is also provisioned with a RenderData object that
     * describes the context of the thing being rendered.  This will include
     * a reference to the exact object, the objects configuration and more.
     *
     * TODO:
     * In essence, the only thing that should be necessary for a renderer
     * to run is that it have access to this RenderData object.  Thus, this
     * interface may change as it is unclear whether the RequestContext
     * and Request itself need to be made available.
     * 
     * @param rendererContext The RendererContext instance
     * 
     * @throws RendererExecutionException the renderer execution exception
     */
    public void execute(RendererContext rendererContext)
            throws RendererExecutionException;

    /**
     * Sets the renderer file path.  This means different things to different
     * engines.  For a JSP renderer, this would be the path to the JSP file.
     * For the WebScript Renderer, this is the URI to the web script. 
     * 
     * @param renderer the new renderer
     */
    public void setRenderer(String renderer);

    /**
     * Gets the renderer
     * 
     * @return the renderer
     */
    public String getRenderer();
    
    /**
     * Sets the renderer type.  A renderer type is basically an ID for the
     * renderer engine.  This is the same ID as is used in the configuration
     * file to register the engine.
     * 
     * For example, for the JSP renderer, this is just "jsp".
     * 
     * @return the renderer type
     */
    public String getRendererType();
    
    /**
     * Sets the renderer type.
     * 
     * @param rendererType the new renderer type
     */
    public void setRendererType(String rendererType);    
}
