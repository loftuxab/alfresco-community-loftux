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

import java.util.Date;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountEntity;
import org.alfresco.util.ParameterCheck;

/**
 * @author David Gildeh
 * @author Neil Mc Erlean
 * @since Thor
 */
public class AccountImpl implements Account
{
    AccountEntity entity;
    AccountType type;
    AccountUsages usageQuota;
    AccountRegistry registry;
    boolean enabled;
    
    public AccountImpl(AccountRegistry registry, AccountEntity entity, AccountUsages usageQuota, boolean enabled)
    {
        ParameterCheck.mandatory("registry", registry);
        ParameterCheck.mandatory("entity", entity);
        this.entity = entity;
        this.registry = registry;
        this.type = registry.getType(entity.getType());
        this.usageQuota = usageQuota;
        this.enabled = enabled;
    }
    
    @Override
    public long getId()
    {
        return (entity.getId() == null ? -1 : entity.getId());
    }
    
    @Override
    public String getName()
    {
        return entity.getName();
    }
    
    @Override
    public List<String> getDomains()
    {
        return entity.getDomains();
    }
    
    @Override
    public AccountType getType()
    {
        return type;
    }
    
    public void setType(int accountTypeId)
    {
        final AccountType requestedAccountType = registry.getType(accountTypeId);
        if (requestedAccountType == null)
        {
            throw new AlfrescoRuntimeException("Unrecognised accountTypeId: " + accountTypeId);
        }
        this.type = requestedAccountType;
    }
    
    @Override
    public Date getCreationDate()
    {
        return entity.getCreationDate();
    }
    
    @Override
    public AccountUsages getUsageQuota()
    {
        return usageQuota;
    }
    
    @Override
    public boolean isEnabled()
    {
        return enabled;
    }
    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    @Override
    public String getTenantId()
    {
        // TODO: persist tenant id
        List<String> domains = entity.getDomains();
        if (domains.size() > 0)
        {
            return domains.get(0);
        }
        return null;
    }
    
    @Override public String getAccountClassName()
    {
        return type.getAccountClass().getName().toString();
    }
    
    @Override public String getAccountClassDisplayName()
    {
        return type.getAccountClass().getDisplayName();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof AccountImpl))
        {
            return false;
        }
        AccountImpl other = (AccountImpl) obj;
        if (this.entity == null)
        {
            if (other.entity != null)
            {
                return false;
            }
        }
        else if (!this.entity.equals(other.entity))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.entity == null) ? 0 : this.entity.hashCode());
        return result;
    }   
}
