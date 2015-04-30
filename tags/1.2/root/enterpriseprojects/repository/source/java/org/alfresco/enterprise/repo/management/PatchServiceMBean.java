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
 * A simple management interface for monitoring the patch service.
 * 
 * @author dward
 */
public interface PatchServiceMBean
{
    /**
     * Gets an array of composite objects describing the patches that have been applied to the repository.
     * 
     * @return the applied patches
     */
    public CompositeData[] getAppliedPatches();
}
