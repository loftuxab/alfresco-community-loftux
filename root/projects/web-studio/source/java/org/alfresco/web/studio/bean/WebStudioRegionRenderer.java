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
package org.alfresco.web.studio.bean;

import java.io.IOException;
import java.io.PrintWriter;

import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.bean.RegionRenderer;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.studio.WebStudioUtil;

/**
 * Provides Web-Studio extensions to region rendering
 * 
 * Primarily, this enables the regions to output additional Web Studio
 * specific JavaScript to bind client-side DOM elements together.
 * 
 * @author muzquiano
 */
public class WebStudioRegionRenderer extends RegionRenderer
{
    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.render.bean.RegionRenderer#postProcess(org.alfresco.web.framework.render.RenderContext)
     */
    public void postProcess(RenderContext context) throws IOException
    {
        // if web studio is enabled +
        // not passive mode + overlays
        // enabled
        if (FrameworkHelper.getConfig().isWebStudioEnabled()
                && !context.isPassiveMode()
                && WebStudioUtil.isOverlayEnabled(context.getRequest()))
        {
            // html binding id
            String htmlId = (String) context
                    .getValue(WebFrameworkConstants.RENDER_DATA_HTMLID);

            // region properties
            String regionId = (String) context
                    .getValue(WebFrameworkConstants.RENDER_DATA_REGION_ID);
            String regionScopeId = (String) context
                    .getValue(WebFrameworkConstants.RENDER_DATA_REGION_SCOPE_ID);
            String regionSourceId = (String) context
                    .getValue(WebFrameworkConstants.RENDER_DATA_REGION_SOURCE_ID);

            // commit to output
            PrintWriter writer = context.getResponse().getWriter();
            writer
                    .println("<script language='Javascript' type='text/javascript'>");

            if (regionId != null && regionScopeId != null
                    && regionSourceId != null)
            {
                writer.println("if(typeof WebStudio != \"undefined\"){");
                writer.println("WebStudio.configureRegion('" + htmlId + "', '"
                        + regionId + "', '" + regionScopeId + "', '"
                        + regionSourceId + "');");
                writer.println("}");
            }

            writer.println("</script>");
            writer.flush();
        }
    }
}
