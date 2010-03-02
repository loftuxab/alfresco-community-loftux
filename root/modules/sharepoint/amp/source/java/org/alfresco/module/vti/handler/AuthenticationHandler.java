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
package org.alfresco.module.vti.handler;

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
     * @param request http request
     * @param response http response
     * @param alfrescoContext deployment context of alfresco application
     * @return SessionUser information about currently loged in user or null. 
     */
    public SessionUser authenticateRequest(HttpServletRequest request, HttpServletResponse httpResponse, String alfrescoContext);
    
    /**
     * Validate user ticket
     * 
     * @param httpRequest http request
     * @param httpResponse http response
     * @param alfrescoContext deployment context of alfresco application
     * @param user current user
     */
    public void checkUserTicket(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String alfrescoContext, SessionUser user);
    
    /**
     * Check if the given user is a member of the requested site
     * 
     * @param request http request to the site
     * @param alfrescoContext deployment context of alfresco application
     * @param username name of the user
     * @return <i>true</i> if user is member of the requested site, otherwise <i>false</i>
     */
    public boolean isSiteMember(HttpServletRequest request, String alfrescoContext, String username);
    
    /**
     * Send to user response with http status 401
     * @param response http response
     */
    public void forceClientToPromptLogonDetails(HttpServletResponse response);
}
