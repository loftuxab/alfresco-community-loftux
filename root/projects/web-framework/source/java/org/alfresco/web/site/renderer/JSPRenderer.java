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

import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.exception.RendererExecutionException;

/**
 * The JSP renderer is a delegating renderer in that it allows you to
 * pass control of object rendering to a specific JSP page.  Thus, folks
 * can dynamically add and remove JSP renderers to their heart's content.
 * 
 * To use the JSP renderer, the renderer type should be set to "jsp" and
 * the renderer property should be set to the path to your JSP file.
 * 
 * @author muzquiano
 */
public class JSPRenderer extends AbstractRenderer
{
    
    private static final String JSP_FILE_URI = "jsp-file-uri";
    private static final String JSP_PATH_URI = "jsp-path-uri";

    /* (non-Javadoc)
     * @see org.alfresco.web.site.renderer.Renderable#execute(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.alfresco.web.site.RenderData)
     */
    public void execute(RendererContext rendererContext)
            throws RendererExecutionException
    {
        /**
         * Get the JSP file to be included
         */
        String renderer = this.getRenderer();
        try
        {
            /**
             * Place the JSP file path onto the render data.
             * This allows it to be retrieved within the JSP page.
             */
            rendererContext.put(JSP_FILE_URI, renderer);

            /**
             * Place the JSP file's parent folder path onto the render data.
             * This allows it to be retrieved within the JSP page.
             */
            int x = renderer.lastIndexOf("/");
            if(x > -1)
            {
                String pathUri = renderer.substring(0, x);
                rendererContext.put(JSP_PATH_URI, pathUri);
            }
            else
            {
                rendererContext.put(JSP_PATH_URI, "/");
            }

            /**
             * Do the include via the request dispatcher
             */
            RequestUtil.include(rendererContext.getRequest(), 
                    rendererContext.getResponse(), renderer);
        }
        catch (Exception ex)
        {
            throw new RendererExecutionException("Unable to execute JSP include: " + renderer, ex);
        }
    }
}
