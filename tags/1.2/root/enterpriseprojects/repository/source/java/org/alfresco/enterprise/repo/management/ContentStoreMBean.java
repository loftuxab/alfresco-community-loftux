/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
 
package org.alfresco.enterprise.repo.management;

/**
 * A simple management interface for monitoring Content Stores.
 * 
 * @author dward
 */
public interface ContentStoreMBean
{
    /**
     * Checks if the store supports write operations, i.e. is not read-only.
     * 
     * @return true if the store is writeable
     */
    public boolean isWriteSupported();
    
    /**
     * Gets the total size (bytes) of the store's underlying storage.
     * 
     * @return      the total size in bytes
     */
    public long getSpaceTotal();
    
    /**
     * Gets the free size (bytes) of the store's underlying storage.
     * 
     * @return      the free size in bytes
     */
    public long getSpaceFree();
}
