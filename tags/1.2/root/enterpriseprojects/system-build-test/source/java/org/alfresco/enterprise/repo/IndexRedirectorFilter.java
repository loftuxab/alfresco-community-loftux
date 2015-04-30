package org.alfresco.enterprise.repo;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class IndexRedirectorFilter implements Filter
{

    public void init(FilterConfig filterConfig) throws ServletException
    {
        // nothing to destroy here
    }

    public void destroy()
    {
        // nothing to destroy here
    }

    public String toString()
    {
        return "Enterprise System Build Test Index Redirector Filter";
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        if(request instanceof HttpServletRequest)
        {
        	String encodedRequestURI = ((HttpServletRequest) request).getRequestURI();
            if ((encodedRequestURI.length()==0) || encodedRequestURI.equals("/"))
            {
                RequestDispatcher rqDispatcher = request.getRequestDispatcher("/index.jsp");
                if (rqDispatcher != null)
                {
                    rqDispatcher.forward(request, response);
                }
                return;
            }
        }
        chain.doFilter(request, response);
    }

}