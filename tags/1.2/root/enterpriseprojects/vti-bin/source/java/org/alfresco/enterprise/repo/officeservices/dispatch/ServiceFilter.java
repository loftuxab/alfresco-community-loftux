package org.alfresco.enterprise.repo.officeservices.dispatch;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.xaldon.officeservices.Types;

public class ServiceFilter implements Filter
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
        return "xosdav ServiceFilter";
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        if (response instanceof HttpServletResponse)
        {
            appendResponseHeaders((HttpServletResponse) response);
        }
        chain.doFilter(request, response);
    }

    static void appendResponseHeaders(HttpServletResponse response)
    {
        response.addHeader("MicrosoftSharePointTeamServices", Types.WSS_VERSION_STRING);
    }

}