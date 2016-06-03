package org.alfresco.module.vti.handler;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.repo.SessionUser;

/**
 * Web authentication fundamental API
 * 
 * @author PavelYur
 *
 */
public interface AuthenticationHandler
{

    /**
     * Authenticate user based on information in http request such as Authorization header or else.
     * 
     * @param context servlet context
     * @param request http request
     * @param response http response
     * @param alfrescoContext deployment context of alfresco application
     * @return SessionUser information about currently loged in user or null. 
     * @throws ServletException 
     * @throws IOException 
     */
    public SessionUser authenticateRequest(ServletContext context, HttpServletRequest request, HttpServletResponse response, String alfrescoContext) throws IOException, ServletException;
    
}
