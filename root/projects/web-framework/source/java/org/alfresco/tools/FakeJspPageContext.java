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

package org.alfresco.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

import org.alfresco.web.site.RequestUtil;

/**
 * Fake Jsp PageContext implementation which wraps predescribed HTTP objects
 *
 * @author muzquiano
 */
public class FakeJspPageContext
	extends PageContext
{
    protected Exception exception;
	protected Map<String, Object> values;
	protected ServletContext context;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected JspWriter out;
	
    public FakeJspPageContext(ServletContext context, HttpServletRequest request, HttpServletResponse response, JspWriter out)
    {
    	this.context = context;
    	this.request = request;
    	this.response = response;
    	this.out = out;
    }

    public ServletRequest getRequest()
    {
    	return this.request;
    }

    public ServletResponse getResponse()
    {
    	return this.response;
    }

    public ServletContext getServletContext()
    {
    	return this.context;
    }

    public ServletConfig getServletConfig()
    {
        return null;
    }

    public JspWriter getOut()
    {
        return this.out;
    }

    public HttpSession getSession()
    {
    	return this.request.getSession();
    }

    public Object findAttribute(String name)
    {
        Object ret = getAttribute(name, PAGE_SCOPE);
        if (ret != null)
            return ret;
        ret = getAttribute(name, REQUEST_SCOPE);
        if (ret != null)
            return ret;
        ret = getAttribute(name, SESSION_SCOPE);
        if (ret != null)
            return ret;
        ret = getAttribute(name, APPLICATION_SCOPE);
        if (ret != null)
            return ret;
        return null;
    }

    public Object getAttribute(String name)
    {
    	return findAttribute(name);
    }

    public Object getAttribute(String name, int scope)
    {
        switch (scope)
        {
            case APPLICATION_SCOPE:
                return getServletContext().getAttribute(name);
            case REQUEST_SCOPE:
                Object ret = getRequest().getAttribute(name);
                if (ret == null)
                    ret = getRequest().getParameter(name);
                return ret;
            case SESSION_SCOPE:
                if (getSession() != null)
                    return getSession().getAttribute(name);
                else
                    return null;
            case PAGE_SCOPE:
                return getValue(name);
        }
        return null;
    }

    public void setAttribute(String name, Object obj)
    {
    	setValue(name, obj);
    }

    public void setAttribute(String name, Object obj, int scope)
    {
        switch (scope)
        {
            case APPLICATION_SCOPE:
                getServletContext().setAttribute(name, obj);
                break;
            case REQUEST_SCOPE:
                getRequest().setAttribute(name, obj);
                break;
            case SESSION_SCOPE:
                if (getSession() != null)
                    getSession().setAttribute(name, obj);
                break;
            case PAGE_SCOPE:
                setValue(name, obj);
                break;
        }
    }

    public void removeAttribute(String name)
    {
        removeValue(name);
    }

    public void removeAttribute(String name, int scope)
    {
        switch (scope)
        {
            case APPLICATION_SCOPE:
                getServletContext().removeAttribute(name);
                break;
            case REQUEST_SCOPE:
            	getRequest().removeAttribute(name);
                break;
            case SESSION_SCOPE:
                if (getSession() != null)
                    getSession().removeAttribute(name);
                break;
            case PAGE_SCOPE:
                removeValue(name);
                break;
        }
    }

    public Enumeration getAttributeNamesInScope(int scope)
    {
        switch (scope)
        {
            case APPLICATION_SCOPE:
                return getServletContext().getAttributeNames();
            case REQUEST_SCOPE:
                return getRequest().getAttributeNames();
            case SESSION_SCOPE:
            	return getSession().getAttributeNames();
            case PAGE_SCOPE:
                return getValueNames();
        }
        return null;
    }

    public int getAttributesScope(String name)
    {
        if (getValue(name) != null)
            return PAGE_SCOPE;

        // allow request attributes to override request parameters
        if (getRequest().getAttribute(name) != null)
            return REQUEST_SCOPE;
        if (getRequest().getParameter(name) != null)
            return REQUEST_SCOPE;
        if (getSession().getAttribute(name) != null)
            return SESSION_SCOPE;
        if (getServletContext().getAttribute(name) != null)
            return APPLICATION_SCOPE;

        return 0;
    }

    public void forward(String url) throws ServletException, IOException
    {
    	RequestUtil.forward(getServletContext(), getRequest(), getResponse(), url);    	
    }

    public void include(String url) throws ServletException, IOException
    {
    	include(url, true);
    }

    public void include(String url, boolean b) throws ServletException,
            IOException
    {
    	RequestUtil.include(getServletContext(), getRequest(), getResponse(), url);
    	
        // make sure everything is flushed before doing an include -- important for included JSP files
        flushOut();
    }

    public void flushOut() throws java.io.IOException
    {
    	out.flush();
    }

    public void release()
    {
    }

    public ExpressionEvaluator getExpressionEvaluator()
    {
        return null;

    }

    public VariableResolver getVariableResolver()
    {
        return null;

    }

    public void handlePageException(Throwable t)
    {
    	// TODO?
    }

    public void handlePageException(Exception e)
    {
        exception = e;
    }

    public Exception getException()
    {
    	return exception;
    }

    public Object getPage()
    {
        return null;
    }

    public void initialize(Servlet srv, ServletRequest req,
            ServletResponse res, String s1, boolean b1, int i1, boolean b2)
    {
    }
    
    
    // local page context helper methods
    
    protected Object getValue(String key)
    {
    	if(values == null)
    		values = new HashMap<String, Object>();
    	return values.get(key);
    }
    
    protected void setValue(String key, Object value)
    {
    	if(values == null)
    		values = new HashMap<String, Object>();
    	values.put(key, value);
    }
    
    protected void removeValue(String key)
    {
    	if(values == null)
    		values = new HashMap<String, Object>();
    	values.remove(key);
    }
    
    protected Enumeration getValueNames()
    {
    	ArrayList<Object> array = new ArrayList<Object>();

    	Iterator it = values.keySet().iterator();
    	while(it.hasNext())
    	{
    		array.add(it.next());
    	}

    	return java.util.Collections.enumeration(array);
    }


}
