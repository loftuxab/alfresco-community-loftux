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

import javax.servlet.http.HttpServletRequest;

import org.alfresco.repo.web.scripts.TenantWebScriptServletRequest;
import org.springframework.extensions.config.ServerProperties;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Runtime;

public class CloudTenantWebScriptServletRequest extends TenantWebScriptServletRequest
{
	public CloudTenantWebScriptServletRequest(Runtime container, HttpServletRequest req, Match serviceMatch, ServerProperties serverProperties)
	{
		super(container, req, serviceMatch, serverProperties);
	}

	@Override
    protected void parse()
    {
        String realPathInfo = getRealPathInfo();

        int idx = realPathInfo.indexOf('/', 1);

        // remove tenant
    	tenant = realPathInfo.substring(1, idx == -1 ? realPathInfo.length() : idx);
        pathInfo = realPathInfo.substring(tenant.length() + 1);
    }
}
