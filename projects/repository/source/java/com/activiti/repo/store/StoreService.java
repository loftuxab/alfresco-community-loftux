package com.activiti.repo.store;

import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;

/**
 * Public interface for <b>store</b> operations
 * 
 * @author Derek Hulley
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
     * @throws StoreExistsException
     */
    public StoreRef createStore(String protocol, String identifier) throws StoreExistsException;
    
    /**
     * @param storeRef a reference to the store to look for
     * @return Returns true if the store exists, otherwise false
     */
    public boolean exists(StoreRef storeRef);
    
    /**
     * @param storeRef a reference to an existing store
     * @return Returns a reference to the root node of the store
     * @throws InvalidStoreRefException if the store could not be found
     */
    public NodeRef getRootNode(StoreRef storeRef) throws InvalidStoreRefException;
}
