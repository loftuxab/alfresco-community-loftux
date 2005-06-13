/**
 * Created on Mar 31, 2005
 */
package org.alfresco.repo.version.common.counter;

import org.alfresco.service.cmr.repository.StoreRef;

/**
 * Version counter DAO service interface.
 * 
 * @author Roy Wetherall
 */
public interface VersionCounterDaoService
{
    /**
     * Get the next available version number for the specified store.
     * 
     * @param storeRef  the store reference
     * @return          the next version number
     */
    public int nextVersionNumber(StoreRef storeRef);   
    
    /**
     * Gets the current version number for the specified store.
     * 
     * @param storeRef  the store reference
     * @return          the current versio number
     */
    public int currentVersionNumber(StoreRef storeRef);
    
    /**
     * Resets the version number for a the specified store.
     * 
     * WARNING: calling this method will completely reset the current 
     * version count for the specified store and cannot be undone.  
     *
     * @param storeRef  the store reference
     */
    public void resetVersionNumber(StoreRef storeRef);
}
