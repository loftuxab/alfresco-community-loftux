package org.alfresco.repo.domain;

import org.alfresco.repo.domain.StoreKey;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * Represents a store entity
 * 
 * @author Derek Hulley
 */
public interface Store
{
    public static final String QUERY_FIND_BY_PROTOCOL_AND_IDENTIFIER = "store.FindByProtocolAndIdentifier";
    
    /**
     * @return Returns the key for the class
     */
    public StoreKey getKey();

    /**
     * @param key the key uniquely identifying this store
     */
    public void setKey(StoreKey key);
    
    /**
     * @return Returns the root of the store
     */
    public Node getRootNode();
    
    /**
     * @param rootNode mandatory association to the root of the store
     */
    public void setRootNode(Node rootNode);
    
    /**
     * Convenience method to access the reference
     * @return Returns the reference to the store
     */
    public StoreRef getStoreRef();
}
