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
package org.alfresco.module.org_alfresco_module_cloud.accounts;

import org.alfresco.module.org_alfresco_module_cloud.usage.TenantQuotaService;

/**
 * Tenant Quotas Data Holder - note: account service currently delegates to repo
 * 
 * @author janv
 * @since Thor
 */
public class AccountUsages
{
    // note: -2 => not set (also used to not update)
    
    private long fileUploadQuota = TenantQuotaService.UNKNOWN;
    
    private long fileQuota = TenantQuotaService.UNKNOWN;
    private long fileUsage = TenantQuotaService.UNKNOWN;
    
    private long siteCountQuota = TenantQuotaService.UNKNOWN;
    private long siteCountUsage = TenantQuotaService.UNKNOWN;
    
    private long personCountQuota = TenantQuotaService.UNKNOWN;
    private long personCountUsage = TenantQuotaService.UNKNOWN;
    
    private long personIntOnlyCountQuota = TenantQuotaService.UNKNOWN;
    private long personIntOnlyCountUsage = TenantQuotaService.UNKNOWN;

    private long personNetworkAdminCountQuota = TenantQuotaService.UNKNOWN;
    private long personNetworkAdminCountUsage = TenantQuotaService.UNKNOWN;

    
    public long getFileUploadQuota()
    {
        return fileUploadQuota;
    }
    
    public void setFileUploadQuota(long fileUploadQuota)
    {
        this.fileUploadQuota = fileUploadQuota;
    }
    
    public long getFileQuota()
    {
        return fileQuota;
    }
    
    public void setFileQuota(long fileQuota)
    {
        this.fileQuota = fileQuota;
    }
    
    public long getFileUsage()
    {
        return fileUsage;
    }
    
    public void setFileUsage(long fileUsage)
    {
        this.fileUsage = fileUsage;
    }
    
    public long getSiteCountQuota()
    {
        return siteCountQuota;
    }
    
    public void setSiteCountQuota(long siteCountQuota)
    {
        this.siteCountQuota = siteCountQuota;
    }
    
    public long getSiteCountUsage()
    {
        return siteCountUsage;
    }
    
    public void setSiteCountUsage(long siteCountUsage)
    {
        this.siteCountUsage = siteCountUsage;
    }
    
    public long getPersonCountQuota()
    {
        return personCountQuota;
    }
    
    public void setPersonCountQuota(long personCountQuota)
    {
        this.personCountQuota = personCountQuota;
    }
    
    public long getPersonCountUsage()
    {
        return personCountUsage;
    }
    
    public void setPersonCountUsage(long personCountUsage)
    {
        this.personCountUsage = personCountUsage;
    }
    
    public long getPersonIntOnlyCountQuota()
    {
        return personIntOnlyCountQuota;
    }
    
    public void setPersonIntOnlyCountQuota(long personIntOnlyCountQuota)
    {
        this.personIntOnlyCountQuota = personIntOnlyCountQuota;
    }
    
    public long getPersonIntOnlyCountUsage()
    {
        return personIntOnlyCountUsage;
    }
    
    public void setPersonIntOnlyCountUsage(long personIntOnlyCountUsage)
    {
        this.personIntOnlyCountUsage = personIntOnlyCountUsage;
    }

    public long getPersonNetworkAdminCountQuota()
    {
        return personNetworkAdminCountQuota;
    }
    
    public void setPersonNetworkAdminCountQuota(long personNetworkAdminCountQuota)
    {
        this.personNetworkAdminCountQuota = personNetworkAdminCountQuota;
    }

    public long getPersonNetworkAdminCountUsage()
    {
        return personNetworkAdminCountUsage;
    }
    
    public void setPersonNetworkAdminCountUsage(long personNetworkAdminCountUsage)
    {
        this.personNetworkAdminCountUsage = personNetworkAdminCountUsage;
    }
}
