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

/**
 * Describes a renderer implementation.
 * 
 * This class contains all of the information necessary to locate
 * a renderer and process it.
 * 
 * @author muzquiano
 */
public class RendererDescriptor
{
    /**
     * Instantiates a new renderer descriptor.
     * 
     * @param renderer the renderer
     * @param rendererType the renderer type
     */
    public RendererDescriptor(String renderer, String rendererType)
    {
        this.renderer = renderer;
        this.rendererType = rendererType;
    }
    
    /**
     * Sets the renderer.
     * 
     * @param renderer the new renderer
     */
    public void setRenderer(String renderer)
    {
        this.renderer = renderer;
    }
    
    /**
     * Gets the renderer.
     * 
     * @return the renderer
     */
    public String getRenderer()
    {
        return this.renderer;
    }
    
    /**
     * Sets the renderer string.
     * 
     * @param rendererType the new renderer string
     */
    public void setRendererString(String rendererType)
    {
        this.rendererType = rendererType;
    }
    
    /**
     * Gets the renderer type.
     * 
     * @return the renderer type
     */
    public String getRendererType()
    {
        return this.rendererType;
    }
    
    /** The renderer. */
    protected String renderer;
    
    /** The renderer type. */
    protected String rendererType;
}
