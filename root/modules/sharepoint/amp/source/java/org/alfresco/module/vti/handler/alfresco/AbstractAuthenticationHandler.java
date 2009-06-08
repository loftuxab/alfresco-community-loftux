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
package org.alfresco.module.vti.handler.alfresco;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.handler.AuthenticationHandler;
import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.handler.UserGroupServiceHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Abstract implementation of web authentication.</p>
 * 
 * @author PavelYur
 *
 */
public abstract class AbstractAuthenticationHandler implements AuthenticationHandler
{

    private static Log logger = LogFactory.getLog(AbstractAuthenticationHandler.class);
    
    public final static String AUTHENTICATION_USER = "_vtiAuthTicket";
    
    public final static String NTLM_START = "NTLM";
    
    public final static String BASIC_START = "BASIC";
    
    public final static String HEADER_AUTHORIZATION = "Authorization";
    
    public final static String HEADER_WWW_AUTHENTICATE = "WWW-Authenticate";
    
    // NTLM authentication session object names    
    public static final String NTLM_AUTH_DETAILS = "_alfNTLMDetails";
    
    
    protected MethodHandler vtiHandler;
    protected UserGroupServiceHandler vtiUserGroupServiceHandler;    
    protected AuthenticationService authenticationService;
    protected PersonService personService;

    public void forceClientToPromptLogonDetails(HttpServletResponse response)
    {
        if (logger.isDebugEnabled())
            logger.debug("Force the client to prompt for logon details");

        response.setHeader(HEADER_WWW_AUTHENTICATE, getWWWAuthenticate());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);        
    }

    @SuppressWarnings("unchecked")
    public void checkUserTicket(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String alfrescoContext, SessionUser user)
    {
        try
        {
            authenticationService.validate(user.getTicket());

            if (!isSiteMember(httpRequest, alfrescoContext, user.getUserName()))
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
    
    public boolean isSiteMember(HttpServletRequest request, String alfrescoContext, final String username)
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
            
            final String buf = dwsName;
            
            RunAsWork<Boolean> isSiteMemberRunAsWork = new RunAsWork<Boolean>()            
            {

                public Boolean doWork() throws Exception
                {                    
                    return vtiUserGroupServiceHandler.isUserMember(buf, username);
                }
                
            };
            
            return AuthenticationUtil.runAs(isSiteMemberRunAsWork, AuthenticationUtil.SYSTEM_USER_NAME).booleanValue();
        }
        catch (Exception e)
        {
            if (dwsName == null)
                throw new VtiHandlerException(VtiHandlerException.DOESNOT_EXIST);
            else
                return false;
        }
    }
    
    /**
     * Returns the <i>value</i> of 'WWW-Authenticate' http header that determine what type of authentication to use by client.
     * 
     * @return value
     */
    public abstract String getWWWAuthenticate();
    
    public void setVtiHandler(MethodHandler vtiHandler)
    {
        this.vtiHandler = vtiHandler;
    }
    
    public void setVtiUserGroupServiceHandler(UserGroupServiceHandler vtiUserGroupServiceHandler)
    {
        this.vtiUserGroupServiceHandler = vtiUserGroupServiceHandler;
    }
    
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
    
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }
}
