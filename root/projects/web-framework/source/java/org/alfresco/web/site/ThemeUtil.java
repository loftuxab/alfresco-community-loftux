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
package org.alfresco.web.site;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.model.Theme;

/**
 * @author muzquiano
 */
public class ThemeUtil
{
    public static String getCurrentThemeId(RequestContext context)
    {
        return context.getThemeId();
    }

    public static Theme getCurrentTheme(RequestContext context)
    {
        String themeId = getCurrentThemeId(context);
        if(themeId != null)
        {
            return (Theme) context.getModel().loadTheme(context, themeId);
        }
        return null;
    }
    
    public static String getCurrentThemeId(HttpServletRequest request)
    {
        return (String) request.getSession().getAttribute("currentThemeId");
    }

    public static void setCurrentThemeId(HttpServletRequest request,
            String themeId)
    {
        if (themeId != null)
        {
            request.getSession().setAttribute("currentThemeId", themeId);
        }
    }

    public static void applyTheme(RequestContext context,
            HttpServletRequest request)
    {
        String themeId = getCurrentThemeId(request);
        if(themeId == null)
        {
            themeId = WebFrameworkConstants.DEFAULT_THEME_ID;
            setCurrentThemeId(request, themeId);
        }
        if(themeId != null)
        {
            context.setThemeId(themeId);
        }
    }
}
