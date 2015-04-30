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
package org.alfresco.module.org_alfresco_module_cloud_share;

import org.springframework.extensions.webscripts.DefaultURLModel;
import org.springframework.extensions.webscripts.URLModel;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * <p>A tenant specific implementation of {@link URLModel} that returns a context
 * that includes the tenant.<p>
 * 
 * @author David Draper
 */
public class TenantURLModel extends DefaultURLModel
{
    /**
     * <p>Instantiates a new {@link TenantURLModel} setting the current tenant from the request</p>
     * @param req
     */
    public TenantURLModel(WebScriptRequest req)
    {
        super(req);
        this.tenant = TenantUtil.getTenantName();
    }
    
    /**
     * <p>The name of the tenant to use for the request associated with this instance.</p>
     */
    private final String tenant;
    
    /**
     * <p>Overrides the default implementation to include the tenant name on the context.</p>
     */
    @Override
    public String getContext()
    {
        return super.getContext() + "/" + this.tenant;
    }
    
    @Override
    public String getServiceContext()
    {
        String serviceContext = getContext() + super.getServiceContext().substring(super.getContext().length());
        return serviceContext;
    }
    
    /**
     * <p>Returns the application context minus the tenant.</p>
     * @return
     */
    public String getAppContext()
    {
        return super.getContext();
    }
}
