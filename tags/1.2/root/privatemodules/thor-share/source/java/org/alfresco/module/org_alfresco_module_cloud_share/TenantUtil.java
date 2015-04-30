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
package org.alfresco.module.org_alfresco_module_cloud_share;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.alfresco.web.site.SlingshotUserFactory;
import org.alfresco.web.site.servlet.MTAuthenticationFilter;

/**
 * <p>Static constants and methods for working with tenants</p> 
 * 
 * @author David Draper
 */
public class TenantUtil
{
    /**
     * <p>This is the request attribute key that is used to store the id of the requested tenant. This attribute is set
     * by the URLRewrite filter which detects a tenant id and stores it before removing it from the request URL.</p>
     */
    public static final String TENANT_NAME_REQUEST_ATTRIBUTE = "org.alfresco.cloud.tenant.name";
    
    /**
     * <p>This is the name of the default tenant that is used to authenticate against if the request does not specify
     * a tenant.</p>
     */
    public static final String DEFAULT_TENANT_NAME = "-default-";
    
    /**
     * <p>This is the tenant used by the administrator</p>
     */
    public static final String SYSTEM_TENANT_NAME = "-system-";
    
    public static final String LOGIN_URL = "/" + DEFAULT_TENANT_NAME + TenantUserFactory.LOGIN_URL_SUFFIX;
    
    /**
     * <p>Retrieves the current tenant name. This will be searched for in a number of places, if it has been set as 
     * a request attribute then that value will be used unless it is set to the default tenant and the user has been
     * authenticated with a home tenant available (in which case the home tenant will be returned). If no request or
     * request attribute can be found then the default tenant value will be returned.</p>
     * 
     * @return The name of the current tenant to use for the current request.
     */
    public static String getTenantName()
    {
        String tenantName = null;
        
        HttpServletRequest request = MTAuthenticationFilter.getCurrentServletRequest();
        if (request == null)
        {
            tenantName = DEFAULT_TENANT_NAME;
        }
        else
        {
            tenantName = (String) request.getAttribute(TENANT_NAME_REQUEST_ATTRIBUTE);
            if (tenantName == null || tenantName.length() == 0)
            {
                tenantName = DEFAULT_TENANT_NAME;
                HttpSession session = request.getSession(false);
                if (session != null)
                {
                    TenantUser user = (TenantUser) session.getAttribute(SlingshotUserFactory.SESSION_ATTRIBUTE_KEY_USER_OBJECT);
                    if (user != null)
                    {
                        tenantName = user.getHomeTenant();
                    }
                }
                request.setAttribute(TENANT_NAME_REQUEST_ATTRIBUTE, tenantName);
            }
        }
        return tenantName;
    }
    
    /**
     * <p>Sets the supplied tenant name as a request attribute.</p>
     * @param tenantName
     */
    public static void setTenantName(String tenantName)
    {
        MTAuthenticationFilter.getCurrentServletRequest().setAttribute(TENANT_NAME_REQUEST_ATTRIBUTE, tenantName);
    }
}
