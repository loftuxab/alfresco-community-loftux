package com.activiti.repo.domain;

import com.activiti.repo.ref.StoreRef;

/**
 * Represents a store entity
 * 
 * @author derekh
 */
public interface Store
{
    public static final String QUERY_FIND_BY_PROTOCOL_AND_IDENTIFIER = "store.FindByProtocolAndIdentifier";
    
    /**
     * @return Returns the persistence-generated ID
     */
    public Long getId();

    /**
     * @param id set automatically by persistence
     */
    public void setId(Long id);
    
    /**
     * @return Returns the protocol applicable to this store
     */
    public String getProtocol();
    
    /**
     * @param protocol the specific protocol that the store is supporting
     */
    public void setProtocol(String protocol);
    
    /**
     * @return Returns the protocol unique identifier of the store
     */
    public String getIdentifier();
    
    /**
     * @param identifier manually assigned protocol-unique identifier
     */
    public void setIdentifier(String identifier);
    
    /**
     * @return Returns the root of the store
     */
    public RealNode getRootNode();
    
    /**
     * @param rootNode mandatory association to the root of the store
     */
    public void setRootNode(RealNode rootNode);
    
    /**
     * Convenience method to access the reference
     * @return Returns the reference to the store
     */
    public StoreRef getStoreRef();
}
