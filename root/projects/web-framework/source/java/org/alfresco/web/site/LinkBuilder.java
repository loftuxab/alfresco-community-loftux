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

import java.util.Map;

/**
 * @author muzquiano
 */
public abstract class LinkBuilder
{
    protected LinkBuilder()
    {
    }

    /**
     * Constructs a link to a given page.
     * This will automatically use the default format.
     */
    public abstract String page(RequestContext context, String pageId);

    /**
     * Constructs a link to a given page for a given format.
     */    
    public abstract String page(RequestContext context, String pageId, 
            String formatId);

    /**
     * Constructs a link to a given page for a given format.
     * The provided object is passed in as context.
     */    
    public abstract String page(RequestContext context, String pageId, 
            String formatId, String objectId);    

    /**
     * Constructs a link to a given page for a given format.
     * The provided object is passed in as context.
     * The provided parameters are appended to the URL.
     */    
    public abstract String page(RequestContext context, String pageId, 
            String formatId, String objectId, Map<String, String> params);
    
    /**
     * Constructs a link to a given content item.
     * This will automatically use the default format.
     * 
     * This method allows the dispatcher servlet to perform a late
     * lookup of the appropriate page to render for the given item.
     */    
    public abstract String content(RequestContext context, String objectId);

    /**
     * Constructs a link to a given content item for a given format.
     * 
     * This method allows the dispatcher servlet to perform a late
     * lookup of the appropriate page to render for the given item.
     */    
    public abstract String content(RequestContext context, String objectId,
            String formatId);

    /**
     * Constructs a link to a given content item for a given format.
     * The provided parameters are appended to the generated URL.
     * 
     * This method allows the dispatcher servlet to perform a late
     * lookup of the appropriate page to render for the given item.
     */    
    public abstract String content(RequestContext context, String objectId,
            String formatId, Map<String, String> params);
}
