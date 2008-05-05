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

import java.util.HashMap;

import org.alfresco.web.site.exception.RenderableNotFoundException;
import org.alfresco.web.site.exception.RendererExecutionException;

/**
 * The Java Class renderer is a delegating renderer that allows application
 * developers to wrap the execution of their custom Java beans.
 * 
 * To use the renderer, the renderer type should be set to "javaclass" 
 * and the renderer property should be set to the fully qualified class name
 * of the Java bean to execute.
 * 
 * The Java bean to be executed must also implement the Renderable interface.
 * 
 * You are also free to register your Java bean directly.  The advantage of
 * routing through the JavaClassRenderer as a proxy is basically to
 * take advantage of Exception trapping.
 * 
 * @author muzquiano
 */
public class JavaClassRenderer extends AbstractRenderer
{
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.renderer.Renderable#execute(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.alfresco.web.site.RenderData)
     */
    public void execute(RendererContext rendererContext)
            throws RendererExecutionException
    {
        /**
         * The fully qualified java class name to execute
         */
        String renderer = this.getRenderer();

        /**
         * Make sure that we only construct beans for a given class name
         * once by using a cache
         */
        if (instances == null)
        {
            instances = new HashMap(16, 1.0f);
        }

        /**
         * See if an instance for this class has already been constructed
         * If not, construct it and place it into the cache
         */
        Renderable renderableImpl = (Renderable) instances.get(renderer);
        if (renderableImpl == null)
        {
            try
            {
                renderableImpl = (Renderable) Class.forName(renderer).newInstance();
                renderableImpl.setRenderer(renderer);
                instances.put(renderer, renderableImpl);
            }
            catch (Exception ex)
            {
                throw new RenderableNotFoundException(
                        ex,
                        "Unable to find renderer implementation class: " + renderer);
            }
        }

        /**
         * Now execute the bean
         */
        try
        {
            renderableImpl.execute(rendererContext);
        }
        catch (Exception ex)
        {
            throw new RendererExecutionException(ex);
        }
    }

    /** The instances. */
    protected static HashMap instances = null;
}
