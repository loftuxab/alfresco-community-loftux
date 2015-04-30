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
package org.alfresco.module.org_alfresco_module_cloud.tenant;

import org.alfresco.repo.tenant.TenantAdminService;

/*
 * Cloud specific MT Admin Service interface
 */
public interface CloudTenantAdminService extends TenantAdminService
{
    /**
     * Gets a quota for the specified tenant
     * 
     * @param tenantDomain tenant
     * @param quotaName name of quota (see @TenantQuotaService)
     * @return quota
     */
    public long getQuota(String tenantDomain, String quotaName);
    
    /**
     * Sets a quota for the specified tenant
     * 
     * @param tenantDomain tenant
     * @param quotaName name of quota (see @TenantQuotaService)
     * @param newQuota quota
     */
    public void setQuota(String tenantDomain, String quotaName, long newQuota);
    
    /**
     * Clears a quota for the specified tenant
     * 
     * @param tenantDomain tenant
     * @param quotaName name of quota (see @TenantQuotaService)
     */
    public void clearQuota(String tenantDomain, String quotaName);

    /**
     * Gets how much used for a quota within the specified tenant
     * 
     * @param tenantDomain tenant
     * @param quotaName name of quota (see @TenantQuotaService)
     * @return usage
     */
    public long getUsage(String tenantDomain, String quotaName);
}
