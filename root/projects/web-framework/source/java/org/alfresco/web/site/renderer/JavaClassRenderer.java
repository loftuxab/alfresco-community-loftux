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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.RenderData;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.exception.RenderableNotFoundException;
import org.alfresco.web.site.exception.RendererExecutionException;

/**
 * @author muzquiano
 */
public class JavaClassRenderer extends AbstractRenderer
{
    public void execute(RequestContext context, HttpServletRequest request,
            HttpServletResponse response, RenderData renderData)
            throws RendererExecutionException
    {
        String renderer = this.getRenderer();

        // go to cache for performance
        if (instances == null)
            instances = new HashMap();

        Renderable renderableImpl = (Renderable) instances.get(renderer);
        if (renderableImpl == null)
        {
            try
            {
                // instantiate the java class
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

        // execute
        try
        {
            renderableImpl.execute(context, request, response, renderData);
        }
        catch (Exception ex)
        {
            throw new RendererExecutionException(ex);
        }
    }

    protected static HashMap instances = null;
}
