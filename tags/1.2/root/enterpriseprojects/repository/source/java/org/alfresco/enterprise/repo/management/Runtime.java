/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

/**
 * An implementation of the {@link RuntimeMBean} interface exposing JVM runtime information.
 * 
 * @author dward
 */
public class Runtime implements RuntimeMBean
{
    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RuntimeMBean#getFreeMemory()
     */
    public long getFreeMemory()
    {
        return java.lang.Runtime.getRuntime().freeMemory();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RuntimeMBean#getMaxMemory()
     */
    public long getMaxMemory()
    {
        return java.lang.Runtime.getRuntime().maxMemory();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RuntimeMBean#getTotalMemory()
     */
    public long getTotalMemory()
    {
        return java.lang.Runtime.getRuntime().totalMemory();
    }

    /* (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.RuntimeMBean#getAvailableProcessors()
     */
    public int getAvailableProcessors()
    {
        return java.lang.Runtime.getRuntime().availableProcessors();
    }
}
