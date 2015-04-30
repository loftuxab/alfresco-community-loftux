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
package org.alfresco.module.org_alfresco_module_cloud_share.scripts.forms;

import org.alfresco.web.scripts.forms.FormUIGet;
import org.springframework.extensions.surf.ServletUtil;

/**
 * Tenant Form UI Component web script implementation.
 *
 * Extends and works just like the standard FormUIGet component except it modifies the submission url to include
 * the tenant.
 *
 * @author Erik Winlof
 */
public class TenantFormUIGet extends FormUIGet
{
    /**
     * Returns the base path to the proxy to use (including the tenant name)
     *
     * @param context Contains the request and context path
     * @return The base path to the proxy to use
     */
    @Override
    protected String getProxyPath(FormUIGet.ModelContext context)
    {
        // This string is defined by the constant TENANT_NAME_REQUEST_ATTRIBUTE in TenantUtil which is not visible
        String tenantName = (String) ServletUtil.getRequest().getAttribute("org.alfresco.cloud.tenant.name");
        return context.getRequest().getContextPath() + "/" + tenantName + ALFRESCO_PROXY;
    }
}
