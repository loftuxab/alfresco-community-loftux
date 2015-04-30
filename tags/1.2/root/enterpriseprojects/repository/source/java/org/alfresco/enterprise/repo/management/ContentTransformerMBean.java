/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

/**
 * A simple management interface for monitoring ContentTransformers.
 * 
 * @author dward
 */
public interface ContentTransformerMBean
{
    /**
     * Checks if this transformer is available.
     * 
     * @return true if it is available
     */
    public boolean isAvailable();

    /**
     * Gets a string returning product and version information.
     * 
     * @return the version string
     */
    public String getVersionString();
}
