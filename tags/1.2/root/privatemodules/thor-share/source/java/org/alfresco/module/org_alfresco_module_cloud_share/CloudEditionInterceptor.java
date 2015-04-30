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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.web.site.EditionInterceptor;
import org.alfresco.web.site.EditionInfo;
import org.json.JSONException;
import org.springframework.extensions.config.ConfigBootstrap;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.WebFrameworkServiceException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.web.context.request.WebRequest;

/**
 * THOR-249: Cloud override (see 'custom-slingshot-cloud-context.xml')
 * 
 * @author janv
 * @since Thor
 */
public class CloudEditionInterceptor extends EditionInterceptor
{
    private static EditionInfo EDITIONINFO = null;
    private static final ReadWriteLock editionLock = new ReentrantReadWriteLock();
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#preHandle(org.springframework.web.context.request.WebRequest)
     */
    @Override
    public void preHandle(WebRequest request) throws Exception
    {
        editionLock.readLock().lock();
        try
        {
            if (EDITIONINFO == null)
            {
                editionLock.readLock().unlock();
                editionLock.writeLock().lock();
                try
                {
                    // check again, as more than one thread could have been waiting on the Write lock 
                    if (EDITIONINFO == null)
                    {
                        RequestContext rc = ThreadLocalRequestContext.getRequestContext();
                        
                        String response =
                               "{" +
                                  "licenseMode: \"ENTERPRISE\"," +
                                  "licenseHolder: \"Alfresco\"" +
                               "}";
                        EDITIONINFO = new EditionInfo(response);
                        
                        // apply runtime config overrides based on the repository edition
                        String runtimeConfig = "classpath:alfresco/enterprise-config.xml";
                        
                        // manually instantiate a ConfigBootstrap object that will
                        // register our override config with the main config source
                        List<String> configs = new ArrayList<String>(1);
                        configs.add(runtimeConfig);
                        
                        ConfigService configservice = rc.getServiceRegistry().getConfigService();
                        ConfigBootstrap cb = new ConfigBootstrap();
                        cb.setBeanName("share-edition-config");
                        cb.setConfigService(configservice);
                        cb.setConfigs(configs);
                        cb.register();
                        configservice.reset();
                    }
                }
                catch (JSONException err)
                {
                    throw new WebFrameworkServiceException("Unable to process response: " + err.getMessage(), err);
                }
                finally
                {
                    editionLock.readLock().lock();
                    editionLock.writeLock().unlock();
                }
            }
            if (EDITIONINFO != null)
            {
                ThreadLocalRequestContext.getRequestContext().setValue("editionInfo", EDITIONINFO);
            }
        }
        finally
        {
            editionLock.readLock().unlock();
        }
    }
}