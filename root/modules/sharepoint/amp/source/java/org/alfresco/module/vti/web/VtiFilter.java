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
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.module.vti.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
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

import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.handler.UserGroupServiceHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.util.URLDecoder;
import org.apache.commons.codec.binary.Base64;
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

    public final static String AUTHENTICATION_USER = "_vtiAuthTicket";
    public final static String AUTHENTICATION_USERNAME = "_vtiUserName";
    public final static String AUTHENTICATION_PASSWORD = "_vtiPassword";

    private MethodHandler vtiHandler;

    private UserGroupServiceHandler vtiUserGroupServiceHandler;

    private AuthenticationService authenticationService;

    private String alfrescoContext;

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

        VtiUser user = null;

        if (session != null)
            user = (VtiUser) session.getAttribute(AUTHENTICATION_USER);

        if (user == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Session user is null. Authenticate user.");

            try
            {
                user = checkAuthorizationHeader(httpRequest, session, user);
            }
            catch (VtiHandlerException e)
            {
                if (e.getMessage().equals(VtiHandlerException.DOESNOT_EXIST))
                {
                    httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    httpResponse.getOutputStream().close();
                    return;
                }
            }

            if (user == null)
            {
                forceClientToPromptLogonDetails(httpResponse);
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
            checkUserTicket(httpRequest, httpResponse, user);

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
        vtiHandler = null;
    }

    private VtiUser checkAuthorizationHeader(HttpServletRequest httpRequest, HttpSession session, VtiUser user) throws FileNotFoundException
    {
        String authHdr = httpRequest.getHeader("Authorization");

        if (authHdr != null && authHdr.length() > 5 && authHdr.substring(0, 5).equalsIgnoreCase("BASIC"))
        {
            String basicAuth = new String(Base64.decodeBase64(authHdr.substring(5).getBytes()));
            String username = null;
            String password = null;

            int pos = basicAuth.indexOf(":");
            if (pos != -1)
            {
                username = basicAuth.substring(0, pos);
                password = basicAuth.substring(pos + 1);
                if (session != null)
                {
                    session.setAttribute(AUTHENTICATION_USERNAME, username);
                    session.setAttribute(AUTHENTICATION_PASSWORD, password);
                }
            }
            else
            {
                username = basicAuth;
                password = "";
            }

            try
            {
                if (logger.isDebugEnabled())
                    logger.debug("Authenticate the user '" + username + "'");
                if (isSiteMember(httpRequest, username))
                {
                    authenticationService.authenticate(username, password.toCharArray());
                    user = new VtiUser(username, authenticationService.getCurrentTicket());
                    if (session != null)
                        session.setAttribute(AUTHENTICATION_USER, user);
                }
            }
            catch (AuthenticationException ex)
            {
                // Do nothing, user object will be null
            }
        }

        return user;
    }

    @SuppressWarnings("unchecked")
    private void checkUserTicket(HttpServletRequest httpRequest, HttpServletResponse httpResponse, VtiUser vtiUser)
    {
        try
        {
            authenticationService.validate(vtiUser.getTicket());

            if (!isSiteMember(httpRequest, vtiUser.getName()))
            {
                Enumeration<String> attributes = httpRequest.getSession().getAttributeNames();
                while (attributes.hasMoreElements())
                {
                    String name = (String) attributes.nextElement();
                    httpRequest.getSession().removeAttribute(name);
                }
                throw new Exception("Not a member!!!!");
            }

            if (logger.isDebugEnabled())
                logger.debug("Ticket was validated");
        }
        catch (Exception ex)
        {
            forceClientToPromptLogonDetails(httpResponse);
            return;
        }
    }

    private void forceClientToPromptLogonDetails(HttpServletResponse httpResponse)
    {
        if (logger.isDebugEnabled())
            logger.debug("Force the client to prompt for logon details");

        httpResponse.setHeader("WWW-Authenticate", "BASIC realm=\"Alfresco Server\"");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @SuppressWarnings("unchecked")
    private boolean checkResourceExistence(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain) throws IOException, ServletException
    {
        String uri = httpRequest.getRequestURI();
        String httpMethod = httpRequest.getMethod();
        Object validSiteUrl = httpRequest.getAttribute("VALID_SITE_URL");
        String if_header = httpRequest.getHeader("If");

        if ((METHOD_GET.equals(httpMethod) || METHOD_HEAD.equals(httpMethod)) && !uri.equals("/_vti_inf.html") && !uri.contains("_vti_bin")
                && !uri.contains("/_vti_history") && !uri.startsWith(alfrescoContext + "/resources") && if_header == null)
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
            if (decodedUrl.length() > alfrescoContext.length())
            {
                decodedUrl = decodedUrl.substring(alfrescoContext.length() + 1);
            }

            try
            {
                VtiUser user = (VtiUser) httpRequest.getSession().getAttribute(AUTHENTICATION_USER);

                user = checkAuthorizationHeader(httpRequest, httpRequest.getSession(), user);

                if (!isSiteMember(httpRequest, user.getName()))
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
                httpResponse.setHeader("WWW-Authenticate", "BASIC realm=\"Alfresco Server\"");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getOutputStream().close();
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
                if (httpRequest.getRequestURI().startsWith(alfrescoContext + "/resources"))
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
            httpResponse.setContentType("application/x-vermeer-rpc");
            httpResponse.setHeader("Cache-Control", "no-cache");
            httpResponse.setHeader("Connection", "close");
        }
    }

    private boolean isSiteMember(HttpServletRequest request, String userName) throws FileNotFoundException
    {
        String uri = request.getRequestURI();

        if (request.getMethod().equalsIgnoreCase("OPTIONS"))
            return true;

        String targetUri = uri.startsWith(alfrescoContext) ? uri.substring(alfrescoContext.length()) : uri;

        if (targetUri.startsWith("/_vti_inf.html") || targetUri.startsWith("/_vti_bin/") || targetUri.startsWith("/resources/"))
            return true;

        String dwsName = null;

        try
        {
            String[] decompsedUrls = vtiHandler.decomposeURL(uri, alfrescoContext);
            dwsName = decompsedUrls[0].substring(decompsedUrls[0].lastIndexOf("/") + 1);
            return vtiUserGroupServiceHandler.isUserMember(dwsName, userName);
        }
        catch (Exception e)
        {
            if (dwsName == null)
                throw new VtiHandlerException(VtiHandlerException.DOESNOT_EXIST);
            else
                return false;
        }
    }

    private boolean validSiteUri(HttpServletRequest request)
    {
        if (!request.getMethod().equals("GET"))
            return false;

        String[] result;
        String uri = request.getRequestURI();
        String context = alfrescoContext;

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

    private static class VtiUser implements Serializable
    {
        private static final long serialVersionUID = -6556393995426663549L;

        private String name;
        private String ticket;

        public VtiUser(String name, String ticket)
        {
            this.name = name;
            this.ticket = ticket;
        }

        public String getName()
        {
            return name;
        }

        public String getTicket()
        {
            return ticket;
        }
    }

    public MethodHandler getVtiHandler()
    {
        return vtiHandler;
    }

    public void setVtiHandler(MethodHandler vtiHandler)
    {
        this.vtiHandler = vtiHandler;
    }

    public AuthenticationService getAuthenticationService()
    {
        return authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    public String getAlfrescoContext()
    {
        return alfrescoContext;
    }

    public void setAlfrescoContext(String alfrescoContext)
    {
        this.alfrescoContext = alfrescoContext;
    }

    public void setVtiUserGroupServiceHandler(UserGroupServiceHandler vtiUserGroupServiceHandler)
    {
        this.vtiUserGroupServiceHandler = vtiUserGroupServiceHandler;
    }

}
