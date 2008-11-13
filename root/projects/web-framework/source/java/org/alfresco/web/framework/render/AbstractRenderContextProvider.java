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
package org.alfresco.web.framework.render;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.site.RequestContext;

/**
 * Provides a base class for use by developers who wish to build
 * custom render context providers.
 * 
 * @author muzquiano
 */
public abstract class AbstractRenderContextProvider implements RenderContextProvider
{        
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContextProvider#merge(org.alfresco.web.framework.render.RenderContext, org.alfresco.web.framework.ModelObject)
     */
    public abstract void merge(RenderContext renderContext, ModelObject modelObject);

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContextProvider#release(org.alfresco.web.framework.render.RenderContext)
     */
    public abstract void release(RenderContext renderContext);

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContextProvider#provide(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public abstract RenderContext provide(RequestContext context,  HttpServletRequest request, HttpServletResponse response);

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContextProvider#provide(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.alfresco.web.framework.render.RenderMode)
     */
    public RenderContext provide(RequestContext context,  HttpServletRequest request, HttpServletResponse response, RenderMode renderMode)
    {
        RenderContext renderContext = provide(context, request, response);
        renderContext.setRenderMode(renderMode);
        
        return renderContext;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContextProvider#provide(org.alfresco.web.framework.render.RenderContext)
     */
    public abstract RenderContext provide(RenderContext renderContext);
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContextProvider#provide(org.alfresco.web.framework.render.RenderContext, org.alfresco.web.framework.ModelObject)
     */
    public RenderContext provide(RenderContext renderContext, ModelObject modelObject)
    {
        RenderContext newRenderContext = provide(renderContext);        
        merge(newRenderContext, modelObject);
        
        return newRenderContext;
    }
}
