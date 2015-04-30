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


/**
 * Tenant Quota Service
 */
public interface TenantQuotaService
{
    public final static String FILE_UPLOAD_SIZE = "fileUploadSize";
    public final static String FILE_STORAGE = "fileStorage";
    public final static String SITE_COUNT = "siteCount";
    public final static String PERSON_COUNT = "personTotalCount";
    public final static String INTERNAL_PERSON_COUNT = "personInternalOnlyCount";
    public final static String NETWORK_ADMIN_COUNT = "personNetworkAdminCount";

    public final static long QUOTA_UNLIMITED = -1; // not enforced
    public final static long QUOTA_ZERO = 0;       // prevent further additions
    public final static long UNKNOWN = -2;         // not set or unknown (quota or usage)
    
    
    /**
     * Gets a quota
     * 
     * @param quotaName name of quota (see @TenantQuotaService)
     * @return quota
     */
    public long getQuota(String quotaName);
    
    /**
     * Sets a quota
     * 
     * @param quotaName name of quota (see @TenantQuotaService)
     * @param newQuota quota
     */
    public void setQuota(String quotaName, long newQuota);
    
    /**
     * Clears a quota for the specified tenant
     * 
     * @param quotaName name of quota (see @TenantQuotaService)
     */
    public void clearQuota(String quotaName);
    
    /**
     * Gets how much used for a quota within the specified tenant
     * 
     * @param quotaName name of quota (see @TenantQuotaService)
     * @return usage
     */
    public QuotaUsage getUsage(String quotaName);

    /**
     * Register a usage for a quota
     * 
     * @param quotaName name of quota (see @TenantQuotaService)
     * @param usage the usage for the quota
     */
    public void registerUsage(String quotaName, QuotaUsage usage);
}
