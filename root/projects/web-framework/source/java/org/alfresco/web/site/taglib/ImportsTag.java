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
package org.alfresco.web.site.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.alfresco.web.site.RenderUtil;
import org.alfresco.web.site.ThemeUtil;

/**
 * @author muzquiano
 */
public class ImportsTag extends TagBase
{
    public int doStartTag() throws JspException
    {
        // CSS
        importLink("/extjs/resources/css/ext-all.css");
        importLink("ui/themes/builder/css/builder-default.css");

        // Theme CSS
        String currentThemeId = ThemeUtil.getCurrentThemeId(getRequestContext());
        importLink("ui/themes/extjs/css/xtheme-" + currentThemeId + ".css",
                "extjs-theme-link");
        importLink("ui/themes/builder/css/builder-" + currentThemeId + ".css",
                "builder-theme-link");

        // ExtJS things
        //importScript("ui/extjs/adapter/yui/yui-utilities.js");
        //importScript("ui/extjs/adapter/yui/ext-yui-adapter.js");
        importScript("/extjs/adapter/ext/ext-base.js", false);
        importScript("/extjs/ext-all.js", false);

        // Custom JS things
        importScript("ui/builder/utils/miframe-min.js", false);
        importScript("ui/builder/utils/json.js", false);
        importScript("ui/builder/dynamic.js.jsp", true);
        importScript("ui/builder/incontext.js.jsp", true);

        // Break out
        importScript("ui/builder/wizard-core.js", false);
        importScript("ui/builder/wizard-adapter-extjs.js", false);
        importScript("ui/builder/application.js", false);
        importScript("ui/builder/builder.js", false);

        // YUI things (for menus)
        //importScript("ui/yui/build/yahoo/yahoo.js");
        importScript("/yui/build/yahoo-dom-event/yahoo-dom-event.js", false);
        importScript("/yui/build/animation/animation-min.js", false);
        importScript("/yui/build/container/container-min.js", false);
        importScript("/yui/build/menu/menu.js", false);

        return SKIP_BODY;
    }

    public void importScript(String src, boolean includeQueryString)
    {
        HttpServletRequest request = (HttpServletRequest) getPageContext().getRequest();
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 4)
            src = src + "?" + queryString;

        // make sure references resolve to the configured servlet
        src = RenderUtil.toBrowserUrl(src);

        print("<script type=\"text/javascript\" src=\"" + src + "\"></script>");
        print("\r\n");
    }

    public void importLink(String href)
    {
        importLink(href, null);
    }

    public void importLink(String href, String id)
    {
        // make sure references resolve to the configured servlet
        href = RenderUtil.toBrowserUrl(href);

        print("<link ");
        if (id != null)
            print("id=\"" + id + "\" ");
        print("rel=\"stylesheet\" type=\"text/css\" href=\"" + href + "\"></link>");
        print("\r\n");
    }
}
