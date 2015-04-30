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

import java.util.Map;

import org.alfresco.web.site.SlingshotUser;

/**
 * <p>The bean representation of a Tenant specific user.</p>
 * @author David Draper
 */
public class TenantUser extends SlingshotUser
{
    private static final long serialVersionUID = 6087870871162696223L;

    public TenantUser(String id, Map<String, Boolean> capabilities, Map<String, Boolean> immutability)
    {
        super(id, capabilities, immutability);
    }

    private String homeTenant = null;
    private String defaultTenant = null;
    private String[] secondaryTenants = null;

    public String getHomeTenant()
    {
        return homeTenant;
    }
    
    public void setHomeTenant(String homeTenant)
    {
        this.homeTenant = homeTenant;
    }
    
    public String getDefaultTenant()
    {
        return defaultTenant;
    }
    
    public void setDefaultTenant(String defaultTenant)
    {
        this.defaultTenant = defaultTenant;
    }

    public String[] getSecondaryTenants()
    {
        return secondaryTenants == null ? null : secondaryTenants.clone();
    }

    public void setSecondaryTenants(String[] secondaryTenants)
    {
        if (secondaryTenants == null)
        {
            this.secondaryTenants = new String[0];
        }
        else
        {
            this.secondaryTenants = secondaryTenants.clone();
        }
    }
}
