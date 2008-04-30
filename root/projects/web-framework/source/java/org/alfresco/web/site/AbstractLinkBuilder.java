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
 * Abstract base class for the LinkBuilder.
 * 
 * The Link Builder defines methods that are used generically to
 * construct links to other pages, page types or objects within the
 * system.
 * 
 * In general, links are either to specific "known" pages or to
 * page placeholders that must be resolved when the link is clicked.
 * 
 * Example - a link to a page:
 * 
 *     String link = builder.page(context, "homepageInstance");
 * 
 * @author muzquiano
 */
public abstract class AbstractLinkBuilder implements LinkBuilder
{
    protected AbstractLinkBuilder()
    {
        super();
    }

    /**
     * Constructs a link to a given page instance.
     * This will automatically use the default format.
     * 
     * @param context The Request Context instance
     * @param pageId The id of the page instance
     */
    public String page(RequestContext context, String pageId)
    {
        String formatId = context.getConfig().getDefaultFormatId();
        return page(context, pageId, formatId);
    }

    /**
     * Constructs a link to a given page for a given format.
     * 
     * @param context The Request Context instance
     * @param pageId The id of the page instance
     * @param formatId The id of the format to render
     */
    public String page(RequestContext context, String pageId, 
            String formatId)
    {
        return page(context, pageId, formatId, null);
    }

    /**
     * Constructs a link to a given page for a given format.
     * The provided object is passed in as context.
     * 
     * @param context The Request Context instance
     * @param pageId The id of the page instance
     * @param formatId The id of the format to render
     * @param objectId The id of the object
     */
    public String page(RequestContext context, String pageId, 
            String formatId, String objectId)
    {
        return page(context, pageId, formatId, objectId, null);
    }
    

    /**
     * Constructs a link to a given page for a given format.
     * The provided object is passed in as context.
     * The provided parameters are appended to the URL.
     * 
     * @param context The Request Context instance
     * @param pageId The id of the page instance
     * @param formatId The id of the format to render
     * @param objectId The id of the object
     * @param params A map of name/value pairs to be appended to the URL
     */
    public abstract String page(RequestContext context, String pageId, 
            String formatId, String objectId, Map<String, String> params);
    
    
    

    /**
     * Constructs a link to a given page type.
     * This will automatically use the default format.
     * 
     * @param context The Request Context instance
     * @param pageTypeId The type of the page
     */
    public String pageType(RequestContext context, String pageTypeId)
    {
        String formatId = context.getConfig().getDefaultFormatId();
        return pageType(context, pageTypeId, formatId);
    }

    /**
     * Constructs a link to a given page type for a given format.
     * 
     * @param context The Request Context instance
     * @param pageTypeId The type of the page
     * @param formatId The id of the format to render
     */    
    public String pageType(RequestContext context, String pageTypeId, 
            String formatId)
    {
        return pageType(context, pageTypeId, formatId, null);
    }

    /**
     * Constructs a link to a given page type for a given format.
     * The provided object is passed in as context.
     * 
     * @param context The Request Context instance
     * @param pageTypeId The type of the page
     * @param formatId The id of the format to render
     * @param objectId The id of the object
     */        
    public String pageType(RequestContext context, String pageTypeId, 
            String formatId, String objectId)
    {
        return pageType(context, pageTypeId, formatId, objectId, null);
    }
    
    /**
     * Constructs a link to a given page type for a given format.
     * The provided object is passed in as context.
     * The provided parameters are appended to the URL.
     * 
     * @param context The Request Context instance
     * @param pageTypeId The type of the page
     * @param formatId The id of the format to render
     * @param objectId The id of the object
     * @param params A map of name/value pairs to be appended to the URL
     */
    public abstract String pageType(RequestContext context, String pageTypeId, 
            String formatId, String objectId, Map<String, String> params);

    
    
    
    
    
    /**
     * Constructs a link to a given object.
     * This will automatically use the default format.
     * 
     * @param context The Request Context instance
     * @param objectId The id of the object
     */    
    public String object(RequestContext context, String objectId)
    {
        String formatId = context.getConfig().getDefaultFormatId();
        return object(context, objectId, formatId);
    }

    /**
     * Constructs a link to a given object.
     * This will automatically use the default format.
     * 
     * @param context The Request Context instance
     * @param objectId The id of the object
     * @param formatId The id of the format to render
     */        
    public String object(RequestContext context, String objectId,
            String formatId)
    {
        return object(context, objectId, formatId, null);
    }

    /**
     * Constructs a link to a given object.
     * The provided object is passed in as context.
     * The provided parameters are appended to the URL.
     * 
     * @param context The Request Context instance
     * @param objectId The id of the object
     * @param formatId The id of the format to render
     * @param params A map of name/value pairs to be appended to the URL
     */    
    public abstract String object(RequestContext context, String objectId,
            String formatId, Map<String, String> params);


}
