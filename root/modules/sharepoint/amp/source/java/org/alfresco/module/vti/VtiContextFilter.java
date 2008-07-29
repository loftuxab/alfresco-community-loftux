/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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

package org.alfresco.module.vti;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.httpconnector.VtiServletContainer;
import org.alfresco.module.vti.httpconnector.VtiSessionManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.Filter;

/**
*
* @author Stas Sokolovsky
*
*/
public class VtiContextFilter implements Filter 
{
   
    private static final long serialVersionUID = -2588894598790048114L;

    private VtiServletContainer contextContainer;
    
    private VtiServletContainer container;
    
    private VtiAccessChecker accessChecker;
    
    private VtiSessionManager sessionManager;
    
    public void init(FilterConfig filterConfig) throws ServletException
    {        
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        accessChecker = (VtiAccessChecker) context.getBean("vtiAccessChecker");
        sessionManager = (VtiSessionManager) context.getBean("vtiSessionManager");
        contextContainer = (VtiServletContainer) context.getBean("vtiContextServletContainer");
        container = (VtiServletContainer) context.getBean("vtiServletContainer");
        ServletContext servletContext = filterConfig.getServletContext();
        contextContainer.setServletContext(servletContext);
        container.setServletContext(servletContext);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (sessionManager.getSession(httpRequest) == null) {
            if (!accessChecker.isRequestAcceptableForRoot(httpRequest)) {
                chain.doFilter(request, response);
                return;
            } else {
                sessionManager.createSession(httpResponse);
            }
        }
        contextContainer.service((HttpServletRequest)request, (HttpServletResponse)response);
    }
    
    public void destroy()
    {
        accessChecker = null;
        sessionManager = null;
    }

}
