/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.web.site;

import java.io.IOException;

import org.alfresco.web.site.exception.RendererExecutionException;
import org.alfresco.web.site.renderer.Renderable;
import org.alfresco.web.site.renderer.RendererContext;

/**
 * Java renderer class to render empty regions for Slingshot.
 * 
 * @author Kevin Roast
 */
public class EmptyRegionRenderer implements Renderable
{
    /* (non-Javadoc)
     * @see org.alfresco.web.site.renderer.Renderable#execute(org.alfresco.web.site.renderer.RendererContext)
     */
    public void execute(RendererContext rendererContext) throws RendererExecutionException
    {
        // null result - for slingshot empty regions are just that, empty.
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.renderer.Renderable#getRenderer()
     */
    public String getRenderer()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.renderer.Renderable#getRendererType()
     */
    public String getRendererType()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.renderer.Renderable#head(org.alfresco.web.site.renderer.RendererContext)
     */
    public String head(RendererContext rendererContext) throws RendererExecutionException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.renderer.Renderable#init(org.alfresco.web.site.renderer.RendererContext)
     */
    public void init(RendererContext rendererContext)
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.renderer.Renderable#setRenderer(java.lang.String)
     */
    public void setRenderer(String renderer)
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.renderer.Renderable#setRendererType(java.lang.String)
     */
    public void setRendererType(String rendererType)
    {
    }
}
