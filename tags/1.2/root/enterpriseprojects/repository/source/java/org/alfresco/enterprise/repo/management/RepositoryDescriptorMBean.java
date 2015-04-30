/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

/**
 * A Management Interface exposing properties of an Alfresco repository.
 * 
 * @author dward
 */
public interface RepositoryDescriptorMBean
{
    /**
     * Gets the id of the repository.
     * 
     * @return the repository identifier
     */
    public String getId();

    /**
     * Gets the name of the repository.
     * 
     * @return name
     */
    public String getName();

    /**
     * Gets the major version number, e.g. <u>1</u>.2.3
     * 
     * @return the major version number
     */
    public String getVersionMajor();

    /**
     * Gets the minor version number, e.g. 1.<u>2</u>.3
     * 
     * @return the minor version number
     */
    public String getVersionMinor();

    /**
     * Gets the version revision number, e.g. 1.2.<u>3</u>
     * 
     * @return the revision number
     */
    public String getVersionRevision();

    /**
     * Gets the version label.
     * 
     * @return the version label
     */
    public String getVersionLabel();

    /**
     * Gets the build number.
     * 
     * @return the build number i.e. build-1
     */
    public String getVersionBuild();

    /**
     * Gets the version number.
     * 
     * @return a string containing the major-minor-revision numbers
     */
    public String getVersionNumber();

    /**
     * Gets the full version number.
     * 
     * @return full version number as major.minor.revision (label)
     */
    public String getVersion();

    /**
     * Gets the edition.
     * 
     * @return the edition
     */
    public String getEdition();

    /**
     * Gets the schema number.
     * 
     * @return a positive integer
     */
    public int getSchema();
}
