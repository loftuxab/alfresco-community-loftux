/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.web.scripts.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple servlet filter to add an 'Expires' HTTP header to a response.
 * The expires header is set forward in time by a value configurable in
 * the 'expires' init parameters - values are in days.
 * 
 * WebScripts or other servlets that happen to match the response type
 * configured for the filter (e.g. "*.js") should override cache settings
 * as required.
 * 
 * @author Kevin Roast
 */
public class StaticAssetCacheFilter implements Filter
{
    private static final long DAY_MS = 1000L*60L*60L*24L;   // 1 day in milliseconds
    private static final long DEFAULT_30DAYS = 30L;         // default of 30 days if not configured
    
    private long expire = DAY_MS * DEFAULT_30DAYS;          // initially set to default value of 30 days
    
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException
    {
        String expireParam = config.getInitParameter("expires");
        if (expireParam != null)
        {
            this.expire = Long.parseLong(expireParam) * DAY_MS;
        }
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException
    {
        ((HttpServletResponse)res).setDateHeader("Expires", System.currentTimeMillis() + this.expire);
        ((HttpServletResponse)res).setHeader("Cache-Control", "public");
        chain.doFilter(req, res);
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy()
    {
        this.expire = DAY_MS * DEFAULT_30DAYS;
    }
}