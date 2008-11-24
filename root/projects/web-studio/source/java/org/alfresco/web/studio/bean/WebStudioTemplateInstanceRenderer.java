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

import java.util.Map;

import org.alfresco.tools.WebUtil;
import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderUtil;
import org.alfresco.web.framework.render.bean.TemplateInstanceRenderer;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.studio.WebStudioUtil;

/**
 * @author muzquiano
 */
public class WebStudioTemplateInstanceRenderer extends TemplateInstanceRenderer
{
    public void postHeaderProcess(RenderContext context)
            throws RendererExecutionException
    {
        // if web studio is enabled + overlays enabled, bind it in via
        // javascript
        if (FrameworkHelper.getConfig().isWebStudioEnabled()
                && WebStudioUtil.isOverlayEnabled(context.getRequest()))
        {
            String location = FrameworkHelper.getConfig()
                    .getWebStudioLocation();
            if (location != null)
            {
                // get the context path to the webapp (i.e. /alfwf)
                String contextPath = context.getRequest().getContextPath();

                // allow for replace of ${contextPath} variable in
                // location
                // string
                if (location.indexOf("${contextPath}") > -1)
                {
                    location = location.replace("${contextPath}", contextPath);
                }

                // get the current query string
                // add the contextPath parameter into it
                // convert back to string
                Map queryStringMap = WebUtil.getQueryStringMap(context
                        .getRequest());
                queryStringMap.put("contextPath", contextPath);
                String qs = WebUtil.getQueryStringForMap(queryStringMap);

                // append into the buffer a JS and CSS include
                print(context, RenderUtil.NEWLINE);
                print(context, WebFrameworkConstants.WEB_STUDIO_SIGNATURE);
                print(context, RenderUtil.NEWLINE);
                print(context, "<script type=\"text/javascript\" src=\""
                        + contextPath + "/js/web-framework.js.jsp?" + qs
                        + "\"></script>");
                print(context, RenderUtil.NEWLINE);
                print(context, "<script type=\"text/javascript\" src=\""
                        + location + "/_js/static?" + qs + "\"></script>");
                print(context, RenderUtil.NEWLINE);
                print(context, "<script type=\"text/javascript\" src=\""
                        + location + "/_js/dynamic?" + qs + "\"></script>");
                print(context, RenderUtil.NEWLINE);
                print(context,
                        "<link type=\"text/css\" rel=\"stylesheet\" href=\""
                                + location + "/_css/?" + qs + "\">");
                print(context, RenderUtil.NEWLINE);
            }
        }

    }

}
