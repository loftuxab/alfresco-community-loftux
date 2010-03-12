/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.module.vti.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.alfresco.web.sharepoint.auth.SiteMemberMappingException;
import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.admin.SysAdminParams;
import org.springframework.extensions.surf.util.URLDecoder;
import org.alfresco.web.sharepoint.auth.AuthenticationHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiFilter filter is used as security filter for checking authentication, 
* resource existence, access to specific document workspace and writing
* specific protocol headers to response</p>
*
* @author Michael Shavnev
*   
*/
public class VtiFilter implements Filter
{
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_TRACE = "TRACE";
    public static final String METHOD_PROPFIND = "PROPFIND";

    private org.alfresco.module.vti.handler.AuthenticationHandler authenticationHandler;
    private MethodHandler vtiHandler;
    private SysAdminParams sysAdminParams;

    private static Log logger = LogFactory.getLog(VtiFilter.class);

    /**
     * <p>Process the specified HTTP request, check authentication, resource existence,
     * access to document workspace and write specific protocol headers to response.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param chain filter chain
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        validSiteUri(httpRequest);
        Object validSiteUrl = httpRequest.getAttribute("VALID_SITE_URL");
        if (logger.isDebugEnabled())
        {
            logger.debug("Checking request for VTI or not");
        }
        HttpSession session = httpRequest.getSession(false);
        if (session == null)
        {
            if (validSiteUrl == null && !httpRequest.getRequestURI().endsWith(".vti"))
            {
                session = httpRequest.getSession();
            }
            else
            {
                chain.doFilter(request, response);
            }
        }

        writeHeaders(httpRequest, httpResponse);

        if (!checkResourceExistence(httpRequest, httpResponse, chain))
        {
            return;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Check authentication");
        }

        SessionUser user = null;

        if (session != null)
            user = (SessionUser) session.getAttribute(AuthenticationHandler.USER_SESSION_ATTRIBUTE);

        String authHeader = httpRequest.getHeader(AuthenticationHandler.HEADER_AUTHORIZATION);
        
        if (user == null || (authHeader != null && authHeader.startsWith(AuthenticationHandler.NTLM_START)))
        {
            if (logger.isDebugEnabled())
                logger.debug("Session user is null. Authenticate user.");

            try
            {
                user = authenticationHandler.authenticateRequest(httpRequest, httpResponse, getAlfrescoContext());
            }
            catch (SiteMemberMappingException e)
            {
                if (e.getMessage().contains(VtiHandlerException.DOESNOT_EXIST))
                {
                    httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    httpResponse.getOutputStream().close();
                    return;
                }
            }

            if (user == null)
            {
                authenticationHandler.forceClientToPromptLogonDetails(httpResponse);
                return;
            }
            else
            {
                if (logger.isDebugEnabled())
                    logger.debug("User was authenticated successfully");
            }
        }
        else
        {
            if (logger.isDebugEnabled())
                logger.debug("Checking user ticket");
            authenticationHandler.checkUserTicket(httpRequest, httpResponse, getAlfrescoContext(), user);

        }

        chain.doFilter(request, response);
    }

    /**
     * <p>Filter initialization.</p> 
     *
     * @param filterConfig filter configuration
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    /**
     * <p>Filter destroy method.</p> 
     *
     */
    public void destroy()
    {
        authenticationHandler = null;
        vtiHandler = null;
    }

    @SuppressWarnings("unchecked")
    private boolean checkResourceExistence(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain) throws IOException, ServletException
    {
        String uri = httpRequest.getRequestURI();
        String httpMethod = httpRequest.getMethod();
        Object validSiteUrl = httpRequest.getAttribute("VALID_SITE_URL");
        String if_header = httpRequest.getHeader("If");

        if ((METHOD_GET.equals(httpMethod) || METHOD_HEAD.equals(httpMethod)) && !uri.equals("/_vti_inf.html") && !uri.contains("_vti_bin")
                && !uri.contains("/_vti_history") && !uri.startsWith(getAlfrescoContext() + "/resources") && if_header == null)
        {
            if (validSiteUrl != null || uri.endsWith(".vti"))
            {
                chain.doFilter(httpRequest, httpResponse);
                return false;
            }

            if (logger.isDebugEnabled())
            {
                logger.debug("Checking is resource exist");
            }
            String decodedUrl = URLDecoder.decode(httpRequest.getRequestURI());
            if (decodedUrl.length() > getAlfrescoContext().length())
            {
                decodedUrl = decodedUrl.substring(getAlfrescoContext().length() + 1);
            }

            try
            {
                SessionUser user = (SessionUser) httpRequest.getSession().getAttribute(AuthenticationHandler.USER_SESSION_ATTRIBUTE);

                user = authenticationHandler.authenticateRequest(httpRequest, httpResponse, getAlfrescoContext());

                if (!authenticationHandler.isSiteMember(httpRequest, getAlfrescoContext(), user.getUserName()))
                {
                    Enumeration<String> attributes = httpRequest.getSession().getAttributeNames();
                    while (attributes.hasMoreElements())
                    {
                        String name = (String) attributes.nextElement();
                        httpRequest.getSession().removeAttribute(name);
                    }
                    throw new Exception("Access denied. Coldn't open document.");
                }

                vtiHandler.existResource(decodedUrl, httpResponse);

                return false;

            }
            catch (FileNotFoundException e)
            {
                httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                httpResponse.getOutputStream().close();
                return false;
            }
            catch (Exception e)
            {
                authenticationHandler.forceClientToPromptLogonDetails(httpResponse);
                return false;
            }
        }
        return true;
    }

    private void writeHeaders(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        String httpMethod = httpRequest.getMethod();
        if (METHOD_OPTIONS.equals(httpMethod))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Return VTI answer for OPTIONS request");
            }
            httpResponse.setHeader("MS-Author-Via", "MS-FP/4.0,DAV");
            httpResponse.setHeader("MicrosoftOfficeWebServer", "5.0_Collab");
            httpResponse.setHeader("MicrosoftSharePointTeamServices", "6.0.2.8117");
            httpResponse.setHeader("DAV", "1,2");
            httpResponse.setHeader("Accept-Ranges", "none");
            httpResponse.setHeader("Cache-Control", "no-cache");
            httpResponse.setHeader("Allow", "GET, POST, OPTIONS, HEAD, MKCOL, PUT, PROPFIND, PROPPATCH, DELETE, MOVE, COPY, GETLIB, LOCK, UNLOCK");
        }
        else if (METHOD_HEAD.equals(httpMethod) || METHOD_GET.equals(httpMethod) || METHOD_PUT.equals(httpMethod))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Return VTI answer for HEAD request");
            }
            httpResponse.setHeader("Public-Extension", "http://schemas.microsoft.com/repl-2");
            httpResponse.setHeader("MicrosoftSharePointTeamServices", "6.0.2.8117");
            if (METHOD_GET.equals(httpMethod))
            {
                if (httpRequest.getRequestURI().startsWith(getAlfrescoContext() + "/resources"))
                {
                    httpResponse.setHeader("Cache-Control", "public");
                }
                else
                {
                    httpResponse.setHeader("Cache-Control", "private");
                }
            }
            else
            {
                httpResponse.setHeader("Cache-Control", "no-cache");
            }
            httpResponse.setContentType("text/html");
        }
        else if (METHOD_PROPFIND.equals(httpMethod))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Return VTI answer for PROPFIND request");
            }
            httpResponse.setHeader("Public-Extension", "http://schemas.microsoft.com/repl-2");
            httpResponse.setHeader("MicrosoftSharePointTeamServices", "6.0.2.8117");
            httpResponse.setHeader("Cache-Control", "no-cache");
        }
        else if (METHOD_POST.equals(httpMethod))
        {
            httpResponse.setHeader("MicrosoftSharePointTeamServices", "6.0.2.8117");
            httpResponse.setHeader("Cache-Control", "no-cache");
            String auth = httpRequest.getHeader(AuthenticationHandler.HEADER_AUTHORIZATION);
            if (auth == null || !auth.startsWith(AuthenticationHandler.NTLM_START))
            {
            httpResponse.setHeader("Connection", "close");
                httpResponse.setContentType("application/x-vermeer-rpc");
            }
            else
            {
                httpResponse.setContentType("text/html");
            }
        }
    }

    private boolean validSiteUri(HttpServletRequest request)
    {
        if (!request.getMethod().equals("GET"))
            return false;

        String[] result;
        String uri = request.getRequestURI();
        String context = getAlfrescoContext();

        String[] parts = VtiPathHelper.removeSlashes(uri).split("/");

        if (parts[parts.length - 1].indexOf('.') != -1)
            return false;

        try
        {
            result = vtiHandler.decomposeURL(uri, context);
            if (result[0].length() > context.length())
            {
                request.setAttribute("VALID_SITE_URL", "true");
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    public void setSysAdminParams(SysAdminParams sysAdminParams)
    {
        this.sysAdminParams = sysAdminParams;
    }

    public MethodHandler getVtiHandler()
    {
        return vtiHandler;
    }

    public void setVtiHandler(MethodHandler vtiHandler)
    {
        this.vtiHandler = vtiHandler;
    }

    public String getAlfrescoContext()
    {
        return "/" + sysAdminParams.getAlfrescoContext();
    }

    public void setAuthenticationHandler(org.alfresco.module.vti.handler.AuthenticationHandler authenticationHandler)
    {
        this.authenticationHandler = authenticationHandler;
    }
    
    public org.alfresco.module.vti.handler.AuthenticationHandler getAuthenticationHandler()
    {
        return authenticationHandler;
    }

}
