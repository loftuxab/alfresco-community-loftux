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

import java.util.Iterator;
import java.util.Map;

/**
 * Link Construction class for Slingshot
 * 
 * This class is automatically provisioned by the framework based on the
 * Slingshot configuration files.  When links are built by the framework,
 * the APIs on this class are automatically consulted to build the link
 * according to the wishes of the application.
 * 
 * In this fashion, the Web Framework is able to provide link facilities
 * in the style that had been expected by the PageRendererServlet.
 * 
 * This also allows developers to avoid hard-coding links and provides them
 * with a clean API for creating links and allowing the framework to
 * resolve how the links should render at execution time.
 * 
 * Thus, linking behaviors can be changed without a huge rewrite to your
 * web site.
 * 
 * @author muzquiano
 */
public class SlingshotLinkBuilder extends LinkBuilder
{
    public SlingshotLinkBuilder()
    {
        super();
    }

    /**
     * Constructs a link to a given page.
     * This will automatically use the default format.
     */
    public String page(RequestContext context, String pageId)
    {
        String formatId = context.getConfig().getDefaultFormatId();
        return page(context, pageId, formatId);
    }

    /**
     * Constructs a link to a given page for a given format.
     */
    public String page(RequestContext context, String pageId, 
            String formatId)
    {
        return page(context, pageId, formatId, null);
    }

    /**
     * Constructs a link to a given page for a given format.
     * The provided object is passed in as context.
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
     */
    public String page(RequestContext context, String pageId, 
            String formatId, String objectId, Map<String, String> params)
    {
        if (pageId == null)
        {
            return null;
        }
        if (formatId == null)
        {
            formatId = context.getConfig().getDefaultFormatId();
        }
        
        // TODO: how should we handle the format?

        // construct the url
        StringBuffer buffer = new StringBuffer();
        buffer.append("/page/" + pageId);
        
        boolean first = true;        
        if(objectId != null)
        {
        	buffer.append("?doc=" + objectId);
        	first = false;
        }
        if(params != null)
        {
        	Iterator it = params.keySet().iterator();
        	while(it.hasNext())
        	{
        		String key = (String) it.next();
        		String value = (String) params.get(key);

        		if(first)
            	{
            		buffer.append("?");
            	}
        		
                buffer.append(key + "=" + value);
                if(it.hasNext())
                {
                	buffer.append("&");
                }
            }
        }
        
        // set relative to servlet context
        return URLUtil.browser(context, buffer.toString());
    }
    
    
    
    
    
    
    
    
    

    /**
     * Constructs a link to a given content item.
     * This will automatically use the default format.
     * 
     * This method allows the dispatcher servlet to perform a late
     * lookup of the appropriate page to render for the given item.
     */
    public String content(RequestContext context, String objectId)
    {
        String formatId = context.getConfig().getDefaultFormatId();
        return content(context, objectId, formatId);
    }

    /**
     * Constructs a link to a given content item for a given format.
     * 
     * This method allows the dispatcher servlet to perform a late
     * lookup of the appropriate page to render for the given item.
     */
    public String content(RequestContext context, String objectId,
            String formatId)
    {
        return content(context, objectId, formatId, null);
    }

    /**
     * Constructs a link to a given content item for a given format.
     * The provided parameters are appended to the generated URL.
     * 
     * This method allows the dispatcher servlet to perform a late
     * lookup of the appropriate page to render for the given item.
     */
    public String content(RequestContext context, String objectId,
            String formatId, Map<String, String> params)
    {
        if(objectId == null)
        {
            return null;
        }
        if (formatId == null)
        {
            formatId = context.getConfig().getDefaultFormatId();
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append("?f=" + formatId);
        buffer.append("&o=" + objectId);
      
        if(params != null)
        {
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();                
                buffer.append("&" + key + "=" + value);
            }
        }

        return buffer.toString();
    }

}
