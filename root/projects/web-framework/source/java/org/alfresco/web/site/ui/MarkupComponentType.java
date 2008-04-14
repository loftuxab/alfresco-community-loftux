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
package org.alfresco.web.site.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.tools.EncodingUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.ThemeUtil;
import org.alfresco.web.site.URLUtil;
import org.alfresco.web.site.config.RuntimeConfig;
import org.alfresco.web.site.exception.RendererExecutionException;

/**
 * @author muzquiano
 */
public class MarkupComponentType extends AbstractRenderable
{
    public void execute(RequestContext context, HttpServletRequest request,
            HttpServletResponse response, RuntimeConfig config)
            throws RendererExecutionException
    {
        // config values
        String markupData = (String) config.get("markupData");

        // shimmy the data a bit
        if (markupData != null)
        {
            /**
             * Append one or more tags that we would like to appear in the
             * HEAD region of the page.  This is done just to show an
             * example.
             */
            this.appendHeadTags(context, "<!-- Appended to HEAD by MarkupComponentType -->");

            // clean up the data
            String data = EncodingUtil.decode(markupData);

            // print out to component body
            print(response, data);
        }
        else
        {
            String currentThemeId = ThemeUtil.getCurrentThemeId(context);
            String unconfiguredImageUrl = URLUtil.toBrowserUrl("/ui/themes/builder/images/" + currentThemeId + "/icons/unconfigured_component_large.gif");
            String renderString = "<img src='" + unconfiguredImageUrl + "' border='0' alt='Unconfigured Google Gadget Component'/>";   
            print(response, renderString);            
        }
    }
}
