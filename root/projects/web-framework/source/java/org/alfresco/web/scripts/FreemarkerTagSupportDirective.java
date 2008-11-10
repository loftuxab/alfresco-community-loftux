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

import javax.servlet.jsp.tagext.Tag;

import org.alfresco.tools.TagUtil;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderContextRequest;
import org.alfresco.web.site.exception.TagExecutionException;

import freemarker.template.TemplateDirectiveModel;

/**
 * Abstract class that defines a Freemarker directive which can process
 * JSP tags.
 * 
 * This class exists so that freemarker directive implementations can be
 * constructing using a single piece of code - a tag.  The directives then
 * provide another entry point into the same tag logic.
 * 
 * @author muzquiano
 */
public abstract class FreemarkerTagSupportDirective implements TemplateDirectiveModel
{
    /** The context. */
    private final RenderContext context;

    /**
     * Instantiates a new freemarker tag support directive.
     * 
     * @param context the context
     */
    public FreemarkerTagSupportDirective(RenderContext context)
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
    	// generate a request that packages up the render context
    	RenderContextRequest request = new RenderContextRequest(context);
            
        // execute the tag
        return TagUtil.execute(tag, request, bodyContent);
    }
}
