package com.activiti.repo.store.db;

import com.activiti.repo.domain.Store;

/**
 * Provides methods for accessing persistent store entities directly
 * 
 * @author derekh
 */
public interface StoreDaoService
{
    /**
     * Creates a unique store for the given protocol and identifier combination
     * 
     * @param protocol a protocol, e.g. {@link com.activiti.repo.ref.StoreRef#PROTOCOL_WORKSPACE}
     * @param identifier a protocol-specific identifier
     * @return Returns the new persistent entity
     */
    public Store createStore(String protocol, String identifier);
    
    /**
     * @param protocol the protocol that the workspace serves
     * @param identifier the protocol-specific identifer
     * @return Returns a store with the given values or null if one doesn't exist
     */
    public Store findStore(String protocol, String identifier);
}
