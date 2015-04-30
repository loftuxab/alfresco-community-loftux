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

import org.alfresco.repo.web.scripts.TenantWebScriptServletRequest;
import org.alfresco.util.UrlUtil;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Cloud Specific override of the Tenancy Information Get WebScript,
 *  which also reports on Secondary Domains 
 *  
 * @author Nick Burch
 * @since Thor.
 */
public class SiteShareViewUrlGet extends org.alfresco.repo.web.scripts.site.SiteShareViewUrlGet
{
    /**
     * Supply the Share URL, including the network
     *  eg https://my.alfresco.com/share/alfresco.com/
     */
    @Override
    protected String getShareRootUrl(WebScriptRequest req)
    {
        String regular = UrlUtil.getShareUrl(sysAdminParams);
        String tenant = ((TenantWebScriptServletRequest)req).getTenant();
        return regular + "/" + tenant + "/";
    }
}