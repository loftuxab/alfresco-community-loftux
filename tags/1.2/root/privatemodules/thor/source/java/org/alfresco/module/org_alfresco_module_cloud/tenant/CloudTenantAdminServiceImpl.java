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

import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.usage.QuotaUsage;
import org.alfresco.module.org_alfresco_module_cloud.usage.TenantQuotaService;
import org.alfresco.repo.tenant.MultiTAdminServiceImpl;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.springframework.extensions.surf.util.ParameterCheck;

/**
 * Cloud specific MT Admin Service implementation
 * 
 * @author janv
 * @since Thor
 */
public class CloudTenantAdminServiceImpl  extends MultiTAdminServiceImpl implements CloudTenantAdminService
{
    private TenantQuotaService tenantQuotaService;
    private EmailAddressService emailAddressService;

    
    public void setTenantQuotaService(TenantQuotaService service)
    {
        this.tenantQuotaService = service;
    }

    public void setEmailAddressService(EmailAddressService service)
    {
        this.emailAddressService = service;
    }
    
    @Override
    protected void notifyAfterEnableTenant(String tenantDomain)
    {
        // NOOP - note: assume for now that all tenant deployers can lazily init
    }

    @Override
    public void setQuota(String tenantDomain, final String quotaName, final long newQuota)
    {
        tenantDomain = getTenantDomain(tenantDomain);
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork()
            {
                tenantQuotaService.setQuota(quotaName, newQuota);
                return null;
            }
        }, tenantDomain);
    }

    @Override
    public void clearQuota(String tenantDomain, final String quotaName)
    {
        tenantDomain = getTenantDomain(tenantDomain);
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork()
            {
                tenantQuotaService.clearQuota(quotaName);
                return null;
            }
        }, tenantDomain);
    }

    @Override
    public long getQuota(String tenantDomain, final String quotaName)
    {
        tenantDomain = getTenantDomain(tenantDomain);
        return TenantUtil.runAsSystemTenant(new TenantRunAsWork<Long>()
        {
            public Long doWork()
            {
                return tenantQuotaService.getQuota(quotaName);
            }
        }, tenantDomain);
    }

    @Override
    public long getUsage(String tenantDomain, final String quotaName)
    {
        tenantDomain = getTenantDomain(tenantDomain);
        return TenantUtil.runAsSystemTenant(new TenantRunAsWork<Long>()
        {
            public Long doWork()
            {
                QuotaUsage usage = tenantQuotaService.getUsage(quotaName);
                return usage == null ? TenantQuotaService.UNKNOWN : usage.getUsage();
            }
        }, tenantDomain);
    }
    
    @Override
    protected void validateTenantName(String tenantDomain)
    {
        ParameterCheck.mandatory("tenantDomain", tenantDomain);
        
        if (!emailAddressService.isValidDomainName(tenantDomain))
        {
            throw new IllegalArgumentException(tenantDomain + " is not a valid tenant name");
        }
    }
}
