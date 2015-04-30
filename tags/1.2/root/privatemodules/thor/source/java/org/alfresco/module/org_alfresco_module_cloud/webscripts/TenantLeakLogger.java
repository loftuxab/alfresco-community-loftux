/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.webscripts;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantContextHolder;
import org.alfresco.repo.web.filter.beans.DependencyInjectedFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Log requests which haven't cleared their tenant and/or security context by the end of the request 
 */
public class TenantLeakLogger implements DependencyInjectedFilter
{
    private static final Log logger = LogFactory.getLog(TenantLeakLogger.class);

    public TenantLeakLogger()
    {
        if (logger.isDebugEnabled())
            logger.debug("Logging leaked tenant/security context");
    }
    
    @Override
    public void doFilter(ServletContext context, ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        try
        {
            chain.doFilter(request, response);
        }
        finally
        {
            if (logger.isDebugEnabled())
            {
                String tenantNow = TenantContextHolder.getTenantDomain();
                if (tenantNow != null)
                {
                    HttpServletRequest r = (HttpServletRequest)request;
                    logger.debug(r.getRequestURI() + " - Leaked tenant context: \"" + tenantNow + "\"");
                }
                
                String userNow = AuthenticationUtil.getFullyAuthenticatedUser();
                if (userNow != null)
                {
                    HttpServletRequest r = (HttpServletRequest)request;
                    logger.debug(r.getRequestURI() + " - Leaked security context: \"" + userNow + "\"");
                }
            }
        }
    }
}