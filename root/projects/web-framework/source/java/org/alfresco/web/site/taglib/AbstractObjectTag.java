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

import javax.servlet.jsp.PageContext;

import org.alfresco.web.site.RequestContext;

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
    private String pageId = null;
    private String pageTypeId = null;
    private String objectId = null;

    public void setPage(String pageId)
    {
        this.pageId = pageId;
    }

    public String getPage()
    {
        return this.pageId;
    }
    
    public void setPageType(String pageTypeId)
    {
        this.pageTypeId = pageTypeId;
    }
    
    public String getPageType()
    {
        return this.pageTypeId;
    }
    
    public void setObject(String objectId)
    {
        this.objectId = objectId;
    }
    
    public String getObject()
    {
        if (this.objectId == null)
        {
            PageContext pc = (PageContext) getPageContext();

            // TODO: Fix this behavior
            String newId = (String) pc.getAttribute("content.item.id");
            if (newId != null)
            {
                this.id = newId;
            }
        }
        return this.objectId;
    }
            
    protected String link(RequestContext context, String formatId)
    {
        return link(context, getPageType(), getPage(), getObject(), formatId);
    }
    
    protected String link(RequestContext context, String pageTypeId, String pageId, String objectId, String formatId)
    {
        // page types
        if(pageTypeId != null)
        {
            if(objectId != null)
            {
                return context.getLinkBuilder().pageType(context, pageTypeId, formatId, objectId);
            }
            else
            {
                return context.getLinkBuilder().pageType(context, pageTypeId, formatId);
            }
        }
        
        // pages
        if(pageId != null)
        {
            if(objectId != null)
            {
                return context.getLinkBuilder().page(context, pageId, formatId, objectId);
            }
            else
            {
                return context.getLinkBuilder().page(context, pageId, formatId);
            }
        }
        
        // objects
        if(objectId != null)
        {
            return context.getLinkBuilder().object(context, objectId, formatId);
        }
        
        return null;
    }
        
}
