/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.List;
import java.util.Map;

import javax.management.openmbean.CompositeData;

import org.alfresco.enterprise.util.BeanMap;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.module.ModuleDetails;

/**
 * An implementation of the {@link ModuleServiceMBean} management interface.
 * 
 * @author dward
 */
public class ModuleService extends MBeanSupport implements ModuleServiceMBean
{

    /** The module service. */
    private org.alfresco.service.cmr.module.ModuleService moduleService;

    /**
     * Sets the module service.
     * 
     * @param moduleService
     *            the new module service
     */
    public void setModuleService(org.alfresco.service.cmr.module.ModuleService moduleService)
    {
        this.moduleService = moduleService;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.ModuleServiceMBean#getAllModules()
     */
    public CompositeData[] getAllModules()
    {
        return doWork(new RetryingTransactionCallback<CompositeData[]>()
        {
            public CompositeData[] execute() throws Throwable
            {
                List<ModuleDetails> allModules = ModuleService.this.moduleService.getAllModules();
                CompositeData[] data = getModules(allModules);
                return data;
            }
        }, true);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.ModuleServiceMBean#getMissingModules()
     */
    @Override
    public CompositeData[] getMissingModules()
    {
        return doWork(new RetryingTransactionCallback<CompositeData[]>()
        {
            public CompositeData[] execute() throws Throwable
            {
                List<ModuleDetails> missingModules = ModuleService.this.moduleService.getMissingModules();
                CompositeData[] data = getModules(missingModules);
                return data;
            }
        }, true);
    }
    
    /**
     * Adds the given list of modules into a CompositeData array
     * 
     * @param modules The list of modules
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private CompositeData[] getModules(List<ModuleDetails> modules)
    {
        CompositeData[] data = new CompositeData[modules.size()];
        int i = 0;
        for (ModuleDetails module : modules)
        {
            data[i++] = BeanMap.getCompositeData((Map) module.getProperties());
        }
        return data;
    }
}
