/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

/**
 * A Management Interface exposing properties of the JVM runtime for monitoring.
 * 
 * @author dward
 */
public interface RuntimeMBean
{
    /**
     * Returns the amount of free memory in the Java Virtual Machine.
     * 
     * @return the amount of free memory in bytes
     */
    public long getFreeMemory();

    /**
     * Returns the maximum amount of memory that the Java virtual machine will attempt to use.
     * 
     * @return the maximum amount of memory in bytes
     */
    public long getMaxMemory();

    /**
     * Returns the total amount of memory in the Java virtual machine.
     * 
     * @return the total amount of memory in bytes
     */
    public long getTotalMemory();
    
    /**
     * Returns the number of processing units (CPUs/cores) available to the Java virtual machine.
     * 
     * @return number of available processing units
     */
    public int getAvailableProcessors();
}
