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
package org.alfresco.web.site.parser.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.tagext.BodyContent;

import org.alfresco.web.site.FilterContext;

/**
 * The PageContext implementation used when pages are being interpreted
 * by PageTokenizer.  It wraps a FilterContext, and provides all of the scoped
 * accessors, as well as an output stream and forward and include capabilities.
 * 
 * @author muzquiano
 */
public class JspPageContextImpl extends PageContext
{
    public JspPageContextImpl(FilterContext cxt, JspWriter out)
    {
        this.cxt = cxt;
        if (out instanceof JspWriterImpl)
            this.out = out;
        else
            this.out = new JspWriterImpl(out, out.getBufferSize(), true);
        session = cxt.getRequest().getSession(true);
    }

    public ServletRequest getRequest()
    {
        return cxt.getRequest();
    }

    public ServletResponse getResponse()
    {
        return cxt.getResponse();
    }

    public ServletContext getServletContext()
    {
        return cxt.getServletContext();
    }

    public ServletConfig getServletConfig()
    {
        return null;
    }

    /**
     * Get the current Writer for output.  May be a BodyContent object if pushBody has been called.
     */
    public JspWriter getOut()
    {
        return out;
    }

    public HttpSession getSession()
    {
        return session;
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
        return cxt.getValue(name);
    }

    public Object getAttribute(String name, int scope)
    {
        switch (scope)
        {
            case APPLICATION_SCOPE:
                return cxt.getServletContext().getAttribute(name);
            case REQUEST_SCOPE:
                Object ret = cxt.getRequest().getAttribute(name);
                if (ret == null)
                    ret = cxt.getRequest().getParameter(name);
                return ret;
            case SESSION_SCOPE:
                if (session != null)
                    return session.getValue(name);
                else
                    return null;
            case PAGE_SCOPE:
                return cxt.getValue(name);
        }
        return null;
    }

    public void setAttribute(String name, Object obj)
    {
        cxt.setValue(name, obj);
    }

    public void setAttribute(String name, Object obj, int scope)
    {
        switch (scope)
        {
            case APPLICATION_SCOPE:
                cxt.getServletContext().setAttribute(name, obj);
                break;
            case REQUEST_SCOPE:
                cxt.getRequest().setAttribute(name, obj);
                break;
            case SESSION_SCOPE:
                if (session != null)
                    session.putValue(name, obj);
                break;
            case PAGE_SCOPE:
                cxt.setValue(name, obj);
                break;
        }
    }

    public void removeAttribute(String name)
    {
        cxt.removeValue(name);
    }

    public void removeAttribute(String name, int scope)
    {
        switch (scope)
        {
            case APPLICATION_SCOPE:
                cxt.getServletContext().removeAttribute(name);
                break;
            case REQUEST_SCOPE:
                break;
            case SESSION_SCOPE:
                if (session != null)
                    session.removeValue(name);
                break;
            case PAGE_SCOPE:
                cxt.removeValue(name);
                break;
        }
    }

    public Enumeration getAttributeNamesInScope(int scope)
    {
        switch (scope)
        {
            case APPLICATION_SCOPE:
                return cxt.getServletContext().getAttributeNames();
            case REQUEST_SCOPE:
                return cxt.getRequest().getAttributeNames();
            case SESSION_SCOPE:
                if (session != null)
                {
                    String names[] = session.getValueNames();
                    ArrayList namearray = new ArrayList();
                    for (int i = 0; i < names.length; i++)
                        namearray.add(names[i]);
                    return java.util.Collections.enumeration(namearray);
                }
                else
                    return null;
            case PAGE_SCOPE:
                return cxt.getNames();
        }
        return null;
    }

    public int getAttributesScope(String name)
    {
        if (cxt.getValue(name) != null)
            return PAGE_SCOPE;

        // allow request attributes to override request parameters
        if (cxt.getRequest().getAttribute(name) != null)
            return REQUEST_SCOPE;
        if (cxt.getRequest().getParameter(name) != null)
            return REQUEST_SCOPE;

        if (session != null)
        {
            if (session.getValue(name) != null)
                return SESSION_SCOPE;
        }

        if (cxt.getServletContext().getAttribute(name) != null)
            return APPLICATION_SCOPE;

        return 0;
    }

    public void forward(String url) throws ServletException, IOException
    {
        // use servlet RequestDispatcher mechanism
        RequestDispatcher disp = cxt.getServletContext().getRequestDispatcher(
                url);

        // get the cache URI (remove extensions for location url's)
        //String cacheURI = cxt.getServerInstance().getCache().getCacheURI(url);

        // store the original SCRIPT_ITEM and set ourselves as the new one so that all
        // references and subobjects are evaluated to us
        //IContent origContent = (IContent) cxt.getRequest().getAttribute("SCRIPT_ITEM");
        //IContent newContent = (IContent) cxt.getServerInstance().getCache().peekContent( cacheURI );
        //if(newContent != null)
        //{
        //cxt.getRequest().setAttribute("SCRIPT_ITEM", newContent);

        // invoke the request dispatcher

        // TODO: Is this right?
        //synchronized( this )
        //{
        disp.forward(cxt.getRequest(), cxt.getResponse());
        //}

        // restore the original script item
        // TODO: Is this right?
        //			if(origContent != null)
        //			cxt.getRequest().setAttribute("SCRIPT_ITEM", origContent);
        //}
    }

    public void include(String url) throws ServletException, IOException
    {
        include(url, false);
    }

    public void include(String url, boolean b) throws ServletException,
            IOException
    {
        // use servlet RequestDispatcher mechanism
        RequestDispatcher disp = cxt.getServletContext().getRequestDispatcher(
                url);
        // make sure everything is flushed before doing an include -- important for included JSP files
        flushOut();

        // get the cache URI (remove extensions for location url's)
        //String cacheURI = cxt.getServerInstance().getCache().getCacheURI(url);

        // store the original SCRIPT_ITEM and set ourselves as the new one so that all
        // references and subobjects are evaluated to us
        //IContent origContent = (IContent) cxt.getRequest().getAttribute("SCRIPT_ITEM");
        //IContent newContent = (IContent) cxt.getServerInstance().getCache().peekContent( cacheURI );
        //if(newContent != null)
        //{
        //	cxt.getRequest().setAttribute("SCRIPT_ITEM", newContent);

        // invoke the request dispatcher
        //			synchronized( newContent )
        //			{
        disp.include(cxt.getRequest(), cxt.getResponse());

        // if the content item changed, serialize it to disk
        //		if(newContent.getCacheInfo().getTouched())
        //			cxt.getServerInstance().getCache().serialize(newContent);
        //			}

        // restore the original script item
        //	if(origContent != null)
        //		cxt.getRequest().setAttribute("SCRIPT_ITEM", origContent);
        //}
    }

    public void flushOut() throws java.io.IOException
    {
        if (out instanceof JspWriterImpl)
        {
            // make sure ALL output is flushed
            ((JspWriterImpl) out).flushAll();
        }
        else
        {
            out.flush();
        }
    }

    public void release()
    {
        cxt = null;
        session = null;
        out = null;
        error = null;
    }

    public BodyContent pushBody()
    {
        // the push is just passed through to the JspWriter
        return ((JspWriterImpl) out).pushWriter();
    }

    public JspWriter popBody()
    {
        // the pop is just passed through to the JspWriter
        return ((JspWriterImpl) out).popWriter();
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
        // TODO: Work it
    }

    public void handlePageException(Exception e)
    {
        error = e;
    }

    public Exception getException()
    {
        Exception e = error;
        error = null;
        return e;
    }

    // dummy methods

    public Object getPage()
    {
        return null;
    }

    public void initialize(Servlet srv, ServletRequest req,
            ServletResponse res, String s1, boolean b1, int i1, boolean b2)
    {
    }

    private FilterContext cxt;
    private HttpSession session;
    private JspWriter out;
    private Exception error;
}
