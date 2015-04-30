package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xaldon.officeservices.URLPathDecoder;

public class ServiceFilter implements Filter
{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        // nothing to destroy here
    }

    @Override
    public void destroy()
    {
        // nothing to destroy here
    }

    @Override
    public String toString()
    {
        return "Alfresco AOS ServiceFilter";
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        if (response instanceof HttpServletResponse)
        {
            appendResponseHeaders((HttpServletResponse) response);
        }
        if(request instanceof HttpServletRequest)
        {
            // get the requested path. we need to URLDecode the requested  URI to get the requested path
            String requestedPath = "";
            try
            {
                requestedPath = URLPathDecoder.decode(((HttpServletRequest) request).getRequestURI(), "UTF8");
                if (null == requestedPath)
                {
                    requestedPath = "";
                }
            }
            catch (UnsupportedEncodingException ex)
            {
                requestedPath = "";
            }
            // remove starting context path
            String contextPath = ((HttpServletRequest) request).getContextPath();
            if ((contextPath != null) && requestedPath.substring(0, contextPath.length()).equals(contextPath))
            {
                requestedPath = requestedPath.substring(contextPath.length());
            }
            // remove starting slashes
            while ((requestedPath.length() > 0) && (requestedPath.charAt(0) == '/'))
            {
                requestedPath = requestedPath.substring(1);
            }
            // AcrobatX: redirect requests to Webs.asmx, Lists.asmx and Copy.asmx to the root end-points of these services
            if (requestedPath.endsWith("/_vti_bin/Webs.asmx"))
            {
                // WARNING:
                // this is only possible since we removed the leading slash in the URL!
                // Otherwise you will create a redirection loop.
                RequestDispatcher rqDispatcher = request.getRequestDispatcher(Const.SERVICE_MAPPING_IN_CONTEXT + "/_vti_bin/webs.asmx");
                if (rqDispatcher != null)
                {
                    rqDispatcher.forward(request, response);
                }
                return;
            }
            if (requestedPath.endsWith("/_vti_bin/Lists.asmx"))
            {
                // WARNING:
                // this is only possible since we removed the leading slash in the URL!
                // Otherwise you will create a redirection loop.
                request.setAttribute("RequestedListPath", requestedPath.substring(0, requestedPath.length() - 20));
                RequestDispatcher rqDispatcher = request.getRequestDispatcher(Const.SERVICE_MAPPING_IN_CONTEXT + "/_vti_bin/lists.asmx");
                if (rqDispatcher != null)
                {
                    rqDispatcher.forward(request, response);
                }
                return;
            }
            if (requestedPath.endsWith("/_vti_bin/Copy.asmx"))
            {
                // WARNING:
                // this is only possible since we removed the leading slash in the URL!
                // Otherwise you will create a redirection loop.
                RequestDispatcher rqDispatcher = request.getRequestDispatcher(Const.SERVICE_MAPPING_IN_CONTEXT + "/_vti_bin/copy.asmx");
                if (rqDispatcher != null)
                {
                    rqDispatcher.forward(request, response);
                }
                return;
            }
        }
        chain.doFilter(request, response);
    }

    static void appendResponseHeaders(HttpServletResponse response)
    {
        // server type header is appended pre-authenticated in the AuthenticationFilter
        // response.addHeader("MicrosoftSharePointTeamServices", "14.0.0.4730");
        response.addHeader("MS-Author-Via", "MS-FP/4.0,DAV");
        response.addHeader("MicrosoftOfficeWebServer", "5.0_Collab");
        response.addHeader("DocumentManagementServer", "Properties Schema;Source Control;Version History;");
        response.addHeader("DAV", "1,2");
        response.addHeader("Allow", "GET, POST, OPTIONS, HEAD, MKCOL, PUT, PROPFIND, PROPPATCH, DELETE, MOVE, COPY, GETLIB, LOCK, UNLOCK");
        response.setHeader("Accept-Ranges", "none");
    }

}