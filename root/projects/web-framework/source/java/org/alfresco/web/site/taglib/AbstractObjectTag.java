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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

import org.alfresco.web.site.RenderUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.model.Page;

/**
 * A tag that works specifically with bound objects, either explicitly
 * or implicitly.  These objects are things like:
 * 
 * Pages
 * Content Items
 * External Object Types
 * 
 * @author muzquiano
 */
public abstract class AbstractObjectTag extends TagBase
{
    private String id = null;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        if (this.id == null)
        {
            PageContext pc = (PageContext) getPageContext();

            // TODO: Fix this behavior
            String newId = (String) pc.getAttribute("content.item.id");
            if (newId != null)
            {
                this.id = newId;
            }
        }
        return this.id;
    }
    
    /**
     * TODO: I consider this a pretty weak way to do this
     */
    protected boolean isPageId()
    {
        if(this.id == null)
            return false;
        
        // TODO: This can be expensive if not in cache
        Page page = getRequestContext().getModel().loadPage(getRequestContext(), getId());
        return(page != null);
    }
    
    protected boolean isContentId()
    {
        if(this.id == null)
            return false;
        
        return(!isPageId());
    }
    
    protected String render(RequestContext context, String id, String format)
    {
        boolean isContent = isContentId();
        if(isContent)
        {
            return context.getLinkBuilder().content(context, id, format);
        }
        else
        {
            return context.getLinkBuilder().page(context, id, format);
        }
    }
    
    protected void render(RequestContext context, HttpServletRequest request, HttpServletResponse response, String id, String format)
    {
        boolean isContent = isContentId();
        if(isContent)
        {
            RenderUtil.content(context, request, response, id, format);
        }
        else
        {
            RenderUtil.page(context, request, response, id, format);
        }
    }
    
    
}
