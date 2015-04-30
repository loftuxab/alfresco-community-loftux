/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import javax.management.openmbean.CompositeData;

/**
 * A simple management interface for monitoring the module service.
 * 
 * @author dward
 */
public interface ModuleServiceMBean
{
    /**
     * Gets an array of composite objects describing the currently installed modules.
     * 
     * @return details of the currently installed modules
     */
    public CompositeData[] getAllModules();
    
    /**
     * Gets an array of composite objects describing the previously installed modules.
     * 
     * @return details of previously installed modules
     */
    public CompositeData[] getMissingModules();
}
