/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.List;

import javax.management.openmbean.CompositeData;

import org.alfresco.enterprise.util.BeanMap;
import org.alfresco.repo.admin.patch.AppliedPatch;

/**
 * An implementation of the {@link PatchServiceMBean} management interface.
 * 
 * @author dward
 */
public class PatchService implements PatchServiceMBean
{

    /** The patch service. */
    private org.alfresco.repo.admin.patch.PatchService patchService;

    /**
     * Sets the patch service.
     * 
     * @param patchService
     *            the patchService to set
     */
    public void setPatchService(org.alfresco.repo.admin.patch.PatchService patchService)
    {
        this.patchService = patchService;
    }

    public CompositeData[] getAppliedPatches()
    {
        List<AppliedPatch> patchList = this.patchService.getPatches(null, null);
        CompositeData[] data = new CompositeData[patchList.size()];
        int i = 0;
        for (AppliedPatch patch : patchList)
        {
            data[i++] = new BeanMap(patch).toCompositeData();
        }
        return data;
    }
}
