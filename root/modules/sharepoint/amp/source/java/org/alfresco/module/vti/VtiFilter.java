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
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.auth.VtiAuthService;
import org.alfresco.module.vti.handler.VtiMethodHandler;
import org.alfresco.module.vti.httpconnector.VtiServletContainer;
import org.alfresco.module.vti.httpconnector.VtiSessionManager;
import org.alfresco.module.vti.auth.VtiAuthException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
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

    public final static String AUTHENTICATION_USER = "_vtiAuthTicket";

    private VtiAuthService authService;
    
    private VtiAccessChecker accessChecker;
    
    private VtiMethodHandler vtiHandler;
    
    private VtiSessionManager sessionManager;
    
    /** Logger */
    private static Log logger = LogFactory.getLog(VtiFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException
    {        
    }

    public void destroy()
    {
        authService = null;
        accessChecker = null;
        vtiHandler = null;
        sessionManager = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        // Assume it's an HTTP request
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        Map<String, Object> session = sessionManager.getSession(httpRequest);
        if (session == null) 
        {
            if (!accessChecker.isRequestAcceptableForRoot(httpRequest)) 
            {
                chain.doFilter(request, response);
            } 
            else 
            {
                session = sessionManager.createSession(httpResponse);
            }
        }
                
        // Ajust headers
        String httpMethod = httpRequest.getMethod();
        if (METHOD_OPTIONS.equals(httpMethod))
        {
            httpResponse.setHeader("MS-Author-Via", "MS-FP/4.0,DAV");
            httpResponse.setHeader("MicrosoftOfficeWebServer", "5.0_Collab");
            httpResponse.setHeader("MicrosoftSharePointTeamServices", "6.0.2.8117");
            httpResponse.setHeader("DAV", "1,2");
            httpResponse.setHeader("Accept-Ranges", "none");
            httpResponse.setHeader("Cache-Control", "no-cache");
            httpResponse.setHeader("Allow", "GET, POST, OPTIONS, HEAD, MKCOL, PUT, PROPFIND, PROPPATCH, DELETE, MOVE, COPY, GETLIB, LOCK, UNLOCK");
        }
        else if (METHOD_HEAD.equals(httpMethod) || METHOD_GET.equals(httpMethod))
        {
            httpResponse.setHeader("Public-Extension", "http://schemas.microsoft.com/repl-2");
            httpResponse.setHeader("MicrosoftSharePointTeamServices", "6.0.2.8117");
            httpResponse.setHeader("Cache-Control", "no-cache");
            httpResponse.setContentType("text/html");
        }
        else if (METHOD_POST.equals(httpMethod))
        {
            httpResponse.setHeader("MicrosoftSharePointTeamServices", "6.0.2.8117");
            httpResponse.setContentType("application/x-vermeer-rpc");
            httpResponse.setHeader("Cache-Control", "no-cache");
            httpResponse.setHeader("Connection", "close");
        }
        
        String alfrescoContext = (String)request.getAttribute(VtiServletContainer.VTI_ALFRESCO_CONTEXT);
        //Check resource existence
        String uri = httpRequest.getRequestURI();        
        if (METHOD_GET.equals(httpMethod) && !uri.equals("/_vti_inf.html") && !uri.contains("_vti_bin") && !uri.startsWith(alfrescoContext + "/history/a") && !uri.startsWith(alfrescoContext + "/resources"))
        {
        	   String decodedUrl = URLDecoder.decode(httpRequest.getRequestURI(), "UTF-8");
            // remove '/' character
            if (decodedUrl.length() > alfrescoContext.length())
            {
                decodedUrl = decodedUrl.substring(alfrescoContext.length() + 1);
            }
            if (!vtiHandler.existResource(decodedUrl))
            {
                httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                httpResponse.getOutputStream().write("NOT FOUND".getBytes());
                httpResponse.getOutputStream().close();
                return;
            }
            else
            {
                return;
            }
        }        

        // Auth

        // Get the user details object from the session

        VtiUser user = (VtiUser) session.get(AUTHENTICATION_USER);

        if (user == null)
        {
            // Get the authorization header

            if (logger.isDebugEnabled())
                logger.debug("Session user is null. Authenticate user.");
            
            String authHdr = httpRequest.getHeader("Authorization");

            if ( authHdr != null && authHdr.length() > 5 && authHdr.substring(0,5).equalsIgnoreCase("BASIC"))
            {
                // Basic authentication details present

                String basicAuth = new String(Base64.decodeBase64(authHdr.substring(5).getBytes()));

                // Split the username and password

                String username = null;
                String password = null;

                int pos = basicAuth.indexOf(":");
                if ( pos != -1)
                {
                    username = basicAuth.substring(0, pos);
                    password = basicAuth.substring(pos + 1);
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
                    
                    // Authenticate the user
                    authService.authenticate(username, password.toCharArray());
                    user = new VtiUser(username, authService.getCurrentTicket());
                    session.put(AUTHENTICATION_USER, user);
                }
                catch (VtiAuthException ex)
                {
                    // Do nothing, user object will be null
                }
            }

            // Check if the user is authenticated, if not then prompt again

            if (user == null)
            {
                // No user/ticket, force the client to prompt for logon details

                if (logger.isDebugEnabled())
                    logger.debug("No user or ticket, force the client to prompt for logon details");
                
                httpResponse.setHeader("WWW-Authenticate", "BASIC realm=\"Alfresco Vti Server\"");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.flushBuffer();
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
            
            try
            {
                // Setup the authentication context
                authService.validate(user.getTicket());
                
                if (logger.isDebugEnabled())
                    logger.debug("Ticket was validated");
            }
            catch (Exception ex)
            {
               // No user/ticket, force the client to prompt for logon details
               
               if (logger.isDebugEnabled())
                    logger.debug("Invalid ticket, force the client to prompt for logon details");

               httpResponse.setHeader("WWW-Authenticate", "BASIC realm=\"Alfresco Vti Server\"");
               httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

               httpResponse.flushBuffer();
               return;
            }
        }

        // Chain other filters

        chain.doFilter(request, response);
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

    public VtiAuthService getAuthService()
    {
        return authService;
    }

    public void setAuthService(VtiAuthService authService)
    {
        this.authService = authService;
    }

    public VtiAccessChecker getAccessChecker()
    {
        return accessChecker;
    }

    public void setAccessChecker(VtiAccessChecker accessChecker)
    {
        this.accessChecker = accessChecker;
    }

    public VtiMethodHandler getVtiHandler()
    {
        return vtiHandler;
    }

    public void setVtiHandler(VtiMethodHandler vtiHandler)
    {
        this.vtiHandler = vtiHandler;
    }

    public VtiSessionManager getSessionManager()
    {
        return sessionManager;
    }

    public void setSessionManager(VtiSessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    };

}
