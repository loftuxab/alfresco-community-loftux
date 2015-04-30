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
package org.alfresco.module.org_alfresco_module_cloud.webdav;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author janv
 * @since Thor
 */
public class CloudWebDAVServlet extends WebDAVServlet
{
    private static Log logger = LogFactory.getLog("org.alfresco.webdav.protocol");
    private static final long serialVersionUID = 848648814507840603L;
    private CommonRequestHandling common;
    
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        common = (CommonRequestHandling) context.getBean("commonRequestHandling");
        
        // Override the PUT method's implementation
        m_davMethods.put(WebDAV.METHOD_PUT, CloudPutMethod.class);
        m_davMethods.put(WebDAV.METHOD_POST, CloudPutMethod.class);
    }
    
    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        if (!common.preProcessRequest(request, response))
        {
            // Request processing should finish.
            return;
        }
        if (!common.checkPrerequisites(request, response))
        {
            // Request processing should finish.
            return;
        }
        
        String tenantDomain = (String) request.getAttribute(CommonRequestHandling.REQ_ATTR_TENANT_DOMAIN);
        processRequestAsTenant(request, response, tenantDomain);
    }

    
    protected void processRequestAsTenant(
                final HttpServletRequest request,
                final HttpServletResponse response,
                String tenantDomain)
    {
        try
        {
            // note: if no tenant specified (ie. "/") then run as system tenant (eg. to list networks)
            TenantUtil.runAsTenant(new TenantRunAsWork<Void>()
            {
                public Void doWork() throws ServletException, IOException
                {
                    serviceImpl(request, response);
                    return null;
                }
            }, tenantDomain);
        }
        finally
        {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
    }

    protected void serviceImpl(
                final HttpServletRequest request,
                final HttpServletResponse response) throws ServletException, IOException
    {
        super.service(request, response);
    }

    public void setCommon(CommonRequestHandling common)
    {
        this.common = common;
    }
}
