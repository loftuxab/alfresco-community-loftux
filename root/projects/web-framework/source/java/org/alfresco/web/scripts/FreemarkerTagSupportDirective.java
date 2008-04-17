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
package org.alfresco.web.scripts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.Tag;

import org.alfresco.tools.TagUtil;
import org.alfresco.web.site.HttpRequestContext;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.model.Page;

import freemarker.template.TemplateDirectiveModel;

/**
 * Custom @imports FreeMarker directive.
 * This places the imports into the page
 * 
 * @author Michael Uzquiano
 */
public abstract class FreemarkerTagSupportDirective implements
        TemplateDirectiveModel
{
    private RequestContext context;

    public FreemarkerTagSupportDirective(RequestContext context)
    {
        this(context, context.getCurrentPage());
    }

    public FreemarkerTagSupportDirective(RequestContext context, Page page)
    {
        this.context = context;
    }

    public String executeTag(Tag tag)
    {
        return executeTag(tag, null);
    }
    
    public String executeTag(Tag tag, String bodyContent)
    {
        // render the component into dummy objects
        // currently, we can only do this for HttpRequestContext instances
        if (context instanceof HttpRequestContext)
        {
            HttpServletRequest response = (HttpServletRequest) ((HttpRequestContext) context).getRequest();

            // execute the tag
            String output = TagUtil.execute(tag, response, bodyContent);
            return output;
        }
        return null;
    }
}
