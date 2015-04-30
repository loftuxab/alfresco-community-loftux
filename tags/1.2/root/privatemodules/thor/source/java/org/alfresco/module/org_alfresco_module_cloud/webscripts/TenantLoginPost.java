/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.org_alfresco_module_cloud.analytics.Analytics;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.web.scripts.TenantWebScriptServletRequest;
import org.alfresco.repo.web.scripts.bean.LoginPost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Post based login script
 * 
 * @author Neil Mc Erlean
 * @since Thor
 */
public class TenantLoginPost extends LoginPost
{
    private static final Log log = LogFactory.getLog(TenantLoginPost.class);
    
    private CloudTenantAuthentication tenantAuthentication;
    
    public void setTenantAuthentication(CloudTenantAuthentication service)
    {
        this.tenantAuthentication = service;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        if (!(req instanceof TenantWebScriptServletRequest))
        {
            throw new WebScriptException("Request is not a tenant aware request");
        }
        
        Map<String, Object> model = super.executeImpl(req, status);
        if (status.getCode() != Status.STATUS_OK)
        {
            return model;
        }
        
        String tenant = ((TenantWebScriptServletRequest)req).getTenant();
        String email = (String)model.get("username");
        
        boolean authorized = tenantAuthentication.authenticateTenant(email, tenant);
        if (!authorized)
        {
            status.setCode(HttpServletResponse.SC_FORBIDDEN);
            status.setMessage("Login Failed");
            status.setRedirect(true);
            return model;
        }

        AuthenticationUtil.setFullyAuthenticatedUser(email);
        try
        {
            if (log.isDebugEnabled())
                log.debug("Handling login analytic event.");
            Analytics.record_login(email);
        }
        finally
        {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
        
        return model;
    }
    
    @Override
    protected Map<String, Object> login(String username, String password)
    {
        username = username.toLowerCase();
        if (!tenantAuthentication.emailTenantExists(username))
        {
            throw new WebScriptException(HttpServletResponse.SC_FORBIDDEN, "Login Failed");
        }
        
        return super.login(username, password);
    }
    
}