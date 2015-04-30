/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.HashMap;
import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.repo.descriptor.DescriptorServiceAvailableEvent;
import org.alfresco.service.cmr.admin.RepoAdminService;
import org.alfresco.service.descriptor.DescriptorService;

/**
 * Exports {@link RepositoryDescriptorMBean} and {@link LicenseDescriptorMBean} instances in response to
 * {@link DescriptorServiceAvailableEvent}s.
 * 
 * @author dward
 */
public class RepositoryDescriptorExporter extends AbstractManagedResourceExporter<DescriptorServiceAvailableEvent>
{
    private RepoAdminService repoAdminService;

    public RepositoryDescriptorExporter()
    {
        super(DescriptorServiceAvailableEvent.class);
    }
    
    public void setRepoAdminService(RepoAdminService repoAdminService)
    {
        this.repoAdminService = repoAdminService;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.alfresco.enterprise.repo.management.AbstractManagedResourceExporter#getObjectsToExport(org.springframework
     * .context. ApplicationEvent)
     */
    @Override
    public Map<ObjectName, ?> getObjectsToExport(DescriptorServiceAvailableEvent event)
            throws MalformedObjectNameException
    {
        DescriptorService source = event.getDescriptorService();
        Map<ObjectName, Object> objectMap = new HashMap<ObjectName, Object>(7);
        objectMap.put(new ObjectName("Alfresco:Name=RepositoryDescriptor,Type=Server"), new RepositoryDescriptor(source
                .getServerDescriptor()));
        objectMap.put(new ObjectName("Alfresco:Name=RepositoryDescriptor,Type=Initially Installed"), new RepositoryDescriptor(
                source.getInstalledRepositoryDescriptor()));
        objectMap.put(new ObjectName("Alfresco:Name=RepositoryDescriptor,Type=Current"), new RepositoryDescriptor(
                source.getCurrentRepositoryDescriptor()));
        
        /**
         * LicenseDescriptor is different from the above. Perhaps the License Component
         * should be exposed instead?
         */
        objectMap.put(new ObjectName("Alfresco:Name=License"), new LicenseDescriptor(source, repoAdminService));
        return objectMap;
    }
}
