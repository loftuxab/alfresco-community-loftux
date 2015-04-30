/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import org.alfresco.service.descriptor.Descriptor;

/**
 * An implementation of the {@link RepositoryDescriptorMBean} interface that exposes information from a repository
 * descriptor.
 * 
 * @author dward
 */
public class RepositoryDescriptor implements RepositoryDescriptorMBean
{

    /** The internal repository descriptor. */
    private final Descriptor descriptor;

    /**
     * The Constructor.
     * 
     * @param descriptor
     *            the internal repository descriptor
     */
    public RepositoryDescriptor(Descriptor descriptor)
    {
        this.descriptor = descriptor;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RepositoryDescriptorMBean#getEdition()
     */
    public String getEdition()
    {
        return this.descriptor.getEdition();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RepositoryDescriptorMBean#getId()
     */
    public String getId()
    {
        return this.descriptor.getId();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RepositoryDescriptorMBean#getName()
     */
    public String getName()
    {
        return this.descriptor.getName();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RepositoryDescriptorMBean#getSchema()
     */
    public int getSchema()
    {
        return this.descriptor.getSchema();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RepositoryDescriptorMBean#getVersion()
     */
    public String getVersion()
    {
        return this.descriptor.getVersion();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RepositoryDescriptorMBean#getVersionBuild()
     */
    public String getVersionBuild()
    {
        return this.descriptor.getVersionBuild();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RepositoryDescriptorMBean#getVersionLabel()
     */
    public String getVersionLabel()
    {
        return this.descriptor.getVersionLabel();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RepositoryDescriptorMBean#getVersionMajor()
     */
    public String getVersionMajor()
    {
        return this.descriptor.getVersionMajor();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RepositoryDescriptorMBean#getVersionMinor()
     */
    public String getVersionMinor()
    {
        return this.descriptor.getVersionMinor();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RepositoryDescriptorMBean#getVersionNumber()
     */
    public String getVersionNumber()
    {
        return this.descriptor.getVersionNumber().toString();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RepositoryDescriptorMBean#getVersionRevision()
     */
    public String getVersionRevision()
    {
        return this.descriptor.getVersionRevision();
    }
}
