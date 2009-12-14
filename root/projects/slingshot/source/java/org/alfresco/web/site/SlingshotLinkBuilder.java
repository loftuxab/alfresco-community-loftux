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

import org.springframework.extensions.surf.util.URLEncoder;

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
 * @author kevinr
 */
public class SlingshotLinkBuilder extends AbstractLinkBuilder
{
    /**
     * Instantiates a new slingshot link builder. For reflection based construction.
     */
    public SlingshotLinkBuilder()
    {
        super();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.AbstractLinkBuilder#page(org.alfresco.web.site.RequestContext, java.lang.String, java.lang.String, java.lang.String, java.util.Map)
     */
    public String page(RequestContext context, String pageId, 
            String formatId, String objectId, Map<String, String> params)
    {
        if (pageId == null)
        {
            throw new IllegalArgumentException("PageId is mandatory.");
        }
        
        if (formatId == null)
        {
            formatId = FrameworkHelper.getConfig().getDefaultFormatId();
        }
        
        // TODO: how should we handle the format?
        
        // construct the url
        StringBuffer buffer = new StringBuffer(128);
        buffer.append("/page/").append(pageId);
        
        boolean first = true;        
        if (objectId != null)
        {
        	buffer.append("?doc=").append(objectId);
        	first = false;
        }
        if (params != null)
        {
        	Iterator it = params.keySet().iterator();
        	while(it.hasNext())
        	{
        		String key = (String) it.next();
        		String value = (String) params.get(key);
        		
        		if (first)
            	{
            		buffer.append('?');
                    first = false;
            	}
        		
                buffer.append(key).append('=').append(URLEncoder.encode(value));
                if(it.hasNext())
                {
                	buffer.append('&');
                }
            }
        }
        
        // set relative to servlet context
        return URLUtil.browser(context, buffer.toString());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.AbstractLinkBuilder#pageType(org.alfresco.web.site.RequestContext, java.lang.String, java.lang.String, java.lang.String, java.util.Map)
     */
    public String pageType(RequestContext context, String pageTypeId, 
            String formatId, String objectId, Map<String, String> params)
    {
        if (pageTypeId == null)
        {
            throw new IllegalArgumentException("PageTypeId is mandatory.");
        }
        
        if (formatId == null)
        {
            formatId = FrameworkHelper.getConfig().getDefaultFormatId();
        }

        StringBuilder buffer = new StringBuilder(128);
        buffer.append("?f=").append(formatId);
        buffer.append("&pt=").append(pageTypeId);
        if (objectId != null && objectId.length() != 0)
        {
              buffer.append("&o=").append(objectId);
        }
        if (params != null)
        {
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();
                buffer.append("&").append(key).append("=").append(URLEncoder.encode(value));
            }
        }

        return buffer.toString();
    }    
        
    /* (non-Javadoc)
     * @see org.alfresco.web.site.AbstractLinkBuilder#object(org.alfresco.web.site.RequestContext, java.lang.String, java.lang.String, java.util.Map)
     */
    public String object(RequestContext context, String objectId,
            String formatId, Map<String, String> params)
    {
        if(objectId == null)
        {
            throw new IllegalArgumentException("ObjectId is mandatory.");
        }
        
        if (formatId == null)
        {
            formatId = FrameworkHelper.getConfig().getDefaultFormatId();
        }

        StringBuffer buffer = new StringBuffer(128);
        buffer.append("?f=").append(formatId);
        buffer.append("&o=").append(objectId);
      
        if (params != null)
        {
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();                
                buffer.append("&").append(key).append("=").append(URLEncoder.encode(value));
            }
        }

        return buffer.toString();
    }
}
