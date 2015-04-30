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
package org.alfresco.module.org_alfresco_module_cloud.repo.thumbnail;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.thumbnail.ThumbnailRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * THOR-475: for now provide single ('super') static registry (to avoid re-init of same defs for each tenant).
 * 
 * In the future, we should refactor registries (including thumbnail registry) to support 
 * static/shared defs (across tenants) + dynamic/custom defs.
 * 
 * @author janv
 * @author Thor
 */
public class CloudThumbnailRegistry extends ThumbnailRegistry
{
    private static Log logger = LogFactory.getLog(CloudThumbnailRegistry.class);
    
    @Override
    public void initThumbnailDefinitions()
    {
        if (tenantAdminService.getCurrentUserDomain().equals(TenantService.DEFAULT_DOMAIN))
        {
            super.initThumbnailDefinitions();
        }
    }
    
    /**
     * This class hooks in to the spring application lifecycle and ensures that any
     * ThumbnailDefinitions injected by spring are converted to RenditionDefinitions
     * and saved.
     */
    // Override
    protected class RegistryLifecycle extends AbstractLifecycleBean
    {
        @Override
        protected void onBootstrap(ApplicationEvent event)
        {
            if (redeploy())
            {
                long start = System.currentTimeMillis();
                
                // If the database is in read-only mode, then do not persist the thumbnail definitions.
                if (transactionService.isReadOnly())
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("TransactionService is in read-only mode. Therefore no thumbnail definitions have been initialised.");
                    }
                    return;
                }
                
                AuthenticationUtil.runAs(new RunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        initThumbnailDefinitions();
                        return null;
                    }
                }, AuthenticationUtil.getSystemUserName());
                
                // note: do not re-deploy tenant thumbnail definitions since these are shared (refer to 'cloud-mt-context.xml')
                
                if (logger.isInfoEnabled())
                {
                    logger.info("Init'ed thumbnail defs in "+(System.currentTimeMillis()-start)+" ms");
                }
            }
        }
        
        @Override
        protected void onShutdown(ApplicationEvent event)
        {
            // Intentionally empty
        }
    }
}
