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
package org.alfresco.module.vti.httpconnector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
*
* @author Stas Sokolovsky
*
*/
public class VtiServletContainer 
{
    private static final long serialVersionUID = 2257788564135460595L;

    private List<ServletPattern> exactMatchServlets;

    private List<ServletPattern> prefixServlets;

    private List<ServletPattern> postfixServlets;

    private List<FilterPattern> filters;
    
    private Set<Servlet> servlets;
    
    private String context = "";

    private static final String MULTIPLIER_PATTERN = "*";
    
    private static final String VTI_SERVLET_NAME = "VtiServlet";
    
    public static final String VTI_ALFRESCO_CONTEXT = "ALFRESCO-DEPLOYMENT-CONTEXT";

    public VtiServletContainer(List<ServletPattern> servletList, List<FilterPattern> filterList) {
        if (filterList != null) {
            filters = filterList;
        } else {
            filterList = new ArrayList<FilterPattern>();
        }
        exactMatchServlets = new ArrayList<ServletPattern>(); 
        prefixServlets = new ArrayList<ServletPattern>();
        postfixServlets = new ArrayList<ServletPattern>();
        servlets = new HashSet<Servlet>();
        if (servletList != null) {
            for (ServletPattern servletPattern : servletList) {
                servlets.add(servletPattern.getServlet());
                String pattern = servletPattern.getPattern();
                if (pattern.endsWith(MULTIPLIER_PATTERN))
                {
                    prefixServlets.add(servletPattern);
                }
                else if (pattern.startsWith(MULTIPLIER_PATTERN))
                {
                    postfixServlets.add(servletPattern);
                }
                else
                {
                    exactMatchServlets.add(servletPattern);
                }
            }
        } 
    }
    
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(VtiServletContainer.class.getClassLoader());
        try {    
            request.setAttribute(VTI_ALFRESCO_CONTEXT, context);
            new HttpFilterChain(request).doFilter(request, response);
        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }
    
    public void setServletContext(ServletContext servletContext) {
        if (servletContext != null) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(VtiServletContainer.class.getClassLoader());
            try {
                int index = 1;
                for (Servlet servlet : servlets) {
                    ServletConfig config = new ServletConfigStub(servletContext, VTI_SERVLET_NAME + index);
                    servlet.init(config);
                    index++;
                }
                Thread.currentThread().setContextClassLoader(classLoader);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                Thread.currentThread().setContextClassLoader(classLoader);
            }            
        }
    }
    
    public void setContext(String context)
    {
        this.context = context;        
    }

    public void doServlets(ServletRequest request, ServletResponse response) throws IOException, ServletException
    {
        String uri = getUri((HttpServletRequest)request);
        HttpServlet targetServlet = null;
        for (ServletPattern servletPattern : exactMatchServlets) {
            if (isMatch(uri, servletPattern.getPattern())) {
                targetServlet = servletPattern.getServlet();
                break;
            }
        }
        if (targetServlet == null) {
            int maxPatternLength = 0;
            for (ServletPattern servletPattern : prefixServlets) {
                String pattern = servletPattern.getPattern();
                if (isMatch(uri, pattern)) {
                    if (pattern.length() > maxPatternLength) {
                        targetServlet = servletPattern.getServlet();
                        maxPatternLength = pattern.length();
                    }                
                }
            }
        }
        if (targetServlet == null) {
            int maxPatternLength = 0;
            for (ServletPattern servletPattern : postfixServlets) {
                String pattern = servletPattern.getPattern();
                if (isMatch(uri, pattern)) {
                    if (pattern.length() > maxPatternLength) {
                        targetServlet = servletPattern.getServlet();
                        maxPatternLength = pattern.length();
                    }                
                }
            }
        }
        if (targetServlet != null) {
            targetServlet.service(request, response);
        }
    }

    class HttpFilterChain implements FilterChain
    {

        private Iterator<FilterPattern> iterator;

        private String uri;

        public HttpFilterChain(HttpServletRequest request)
        {
            iterator = filters.iterator();
            uri = getUri(request);
        }

        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException
        {
            if (uri == null)
            {
                return;
            }
            while (iterator.hasNext())
            {
                FilterPattern filterPattern = iterator.next();
                if (isMatch(uri, filterPattern.getPattern()))
                {
                    Filter filter = filterPattern.getFilter();
                    filter.doFilter(request, response, this);
                    return;
                }
            }
            doServlets(request, response);
        }

    }

    private String getUri(HttpServletRequest request)
    {
        String uri = request.getRequestURI();
        if (context != null && uri.startsWith(context)) {
            uri = uri.substring(context.length());
        }
        return uri;
    }

    private boolean isMatch(String uri, String pattern)
    {
        boolean result = false;
        if (uri != null && pattern != null && pattern.length() > 1)
        {
            if (pattern.endsWith(MULTIPLIER_PATTERN))
            {
                if (uri.startsWith(pattern.substring(0, pattern.length() - 1)))
                {
                    result = true;
                }
            }
            else if (pattern.startsWith(MULTIPLIER_PATTERN))
            {
                if (uri.endsWith(pattern.substring(1, pattern.length())))
                {
                    result = true;
                }
            }
            else
            {
                if (uri.equals(pattern))
                {
                    result = true;
                }
            }
        }
        return result;
    }
    

}
