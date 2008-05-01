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
package org.alfresco.web.scripts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.Tag;

import org.alfresco.tools.TagUtil;
import org.alfresco.web.site.HttpRequestContext;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.exception.TagExecutionException;
import org.alfresco.web.site.model.Page;

import freemarker.template.TemplateDirectiveModel;

/**
 * Abstract class that defines a Freemarker directive which can process
 * JSP tags.
 * 
 * This class exists so that freemarker directive implementations can be
 * constructing using a single piece of code - a tag.  The directives then
 * provide another entry point into the same tag logic.
 * 
 * @author Michael Uzquiano
 */
public abstract class FreemarkerTagSupportDirective implements
        TemplateDirectiveModel
{
    
    /** The context. */
    private RequestContext context;

    /**
     * Instantiates a new freemarker tag support directive.
     * 
     * @param context the context
     */
    public FreemarkerTagSupportDirective(RequestContext context)
    {
        this(context, context.getCurrentPage());
    }

    /**
     * Instantiates a new freemarker tag support directive.
     * 
     * @param context the context
     * @param page the page
     */
    public FreemarkerTagSupportDirective(RequestContext context, Page page)
    {
        this.context = context;
    }

    /**
     * Execute a tag and return the String output
     * 
     * @param tag the tag
     * 
     * @return the string
     */
    public String executeTag(Tag tag)
        throws TagExecutionException
    {
        return executeTag(tag, null);
    }
    
    /**
     * Execute tag.
     * 
     * @param tag the tag
     * @param bodyContent the body content
     * 
     * @return the string
     */
    public String executeTag(Tag tag, String bodyContent)
        throws TagExecutionException
    {
        // render the component into dummy objects
        // currently, we can only do this for HttpRequestContext instances
        String output = null;
        if (context instanceof HttpRequestContext)
        {
            HttpServletRequest response = (HttpServletRequest) ((HttpRequestContext) context).getRequest();

            // execute the tag
            output = TagUtil.execute(tag, response, bodyContent);
        }
        return output;
    }
}
