package com.activiti.repo.store;

import com.activiti.repo.ref.StoreRef;

/**
 * Public interface for <b>store</b> operations
 * 
 * @author derekh
 */
public interface StoreService
{
    /**
     * Create a new store for the given protocol and identifier.  The implementation
     * may create the store in any number of locations, including a database or
     * subversion.
     * 
     * @param protocol the implementation protocol
     * @param identifier the protocol-specific identifier
     * @return Returns a reference to the store
     */
    public StoreRef createStore(String protocol, String identifier);
}
