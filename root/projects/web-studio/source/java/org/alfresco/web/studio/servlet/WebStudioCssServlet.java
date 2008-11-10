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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.studio.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.servlet.BaseServlet;
import org.alfresco.web.studio.OverlayUtil;
import org.alfresco.web.studio.WebStudio;

/**
 * Retrieves CSS, Javascript and other assets on behalf of the currently
 * logged in user so as to render the in-context displays.
 * 
 * @author muzquiano
 */
public class WebStudioCssServlet extends BaseServlet
{
    public void init() throws ServletException
    {
        super.init();
    }

    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
    	String cacheKey = request.getRequestURI() + request.getQueryString();
    	
		StringBuilder buffer = null;
		
		// load from cache (if configured)
		if(WebStudio.getConfig().isCSSCachingEnabled())
		{
			buffer = OverlayUtil.getCachedResource(request, cacheKey); 
		}
	
		if(buffer == null)
		{
			buffer = new StringBuilder(65536);
			
			try
			{
				// By default, include a JSP from disk so that we can at least be pretty flexible
				// about some of the core stuff that gets included
				OverlayUtil.include(request, buffer, "/overlay/default/core.css.jsp");
					
	    		// TODO: Include stuff from the application
		
				// General cleanup off CSS to help resolve variables
				String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio");
				String str = buffer.toString();
				str = str.replace("url(/yui", "url(" + rootPath + "/yui");
				buffer = new StringBuilder(32768);
				buffer.append(str);
				
				// Execute CSS compression (if configured)
	    		if(WebStudio.getConfig().isCSSCompressionEnabled())
	    		{
	    			String value = WebStudio.compressCSS(buffer);
	    			buffer = new StringBuilder(131072);
	    			buffer.append(value);
	    		}
				
	    		// cache back (if configured)
				if(WebStudio.getConfig().isJavascriptCachingEnabled())
				{
					OverlayUtil.setCachedResource(request, cacheKey, buffer);
				}				
			}
			catch(IOException ioe)
			{
				// this was likely thrown during the compression step
				ioe.printStackTrace();
			}
		}
		
		response.getWriter().println(buffer.toString());    	
    }
}
