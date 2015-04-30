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
package org.alfresco.module.org_alfresco_module_cloud.usage;

import org.alfresco.repo.content.ContentLimitProvider;


/**
 * Account based content limit provider
 */
public class AccountContentLimitProvider implements ContentLimitProvider
{
    private TenantQuotaService tenantQuotaService;
    
    public void setTenantQuotaService(TenantQuotaService service)
    {
        tenantQuotaService = service;
    }
    
    @Override public long getSizeLimit()
    {
        long quota = tenantQuotaService.getQuota(TenantQuotaService.FILE_UPLOAD_SIZE);
        if (quota == TenantQuotaService.UNKNOWN)
        {
            quota = TenantQuotaService.QUOTA_UNLIMITED;
        }
        return quota;
    }
}
