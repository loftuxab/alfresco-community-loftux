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

import org.alfresco.repo.usage.UserUsageTrackingComponent;
import org.springframework.context.ApplicationEvent;

/**
 * Overridden to be a NOOP (see THOR-184) hence does not clear user usages on startup or when creating a new tenant)
 * 
 * note: should be used in conjunction with "system.usages.enabled=false" and disabling userUsageCollapseJob
 * 
 * @author janv
 * @since Thor
 */
public class CloudNOOPUserUsageTrackingComponent extends UserUsageTrackingComponent
{
    @Override
    public void execute()
    {
    }
    
    @Override
    protected void onBootstrap(ApplicationEvent event)
    {
    }
    
    public void bootstrapInternal()
    {
    }
    
    @Override
    protected void onShutdown(ApplicationEvent event)
    {
    }
}
