package com.activiti.repo.domain;

import com.activiti.repo.ref.StoreRef;

/**
 * Represents a workspace
 * 
 * @author derekh
 */
public interface Workspace
{
    public static final String QUERY_FIND_BY_PROTOCOL_AND_IDENTIFIER = "workspace.FindByProtocolAndIdentifier";
    
    /**
     * @return Returns the persistence-generated ID
     */
    public Long getId();

    /**
     * @param id set automatically by persistence
     */
    public void setId(Long id);
    
    /**
     * @return Returns the protocol applicable to this workspace
     */
    public String getProtocol();
    
    /**
     * @param protocol the specific protocol that the workspace is supporting
     */
    public void setProtocol(String protocol);
    
    /**
     * @return Returns the protocol unique identifier of the workspace
     */
    public String getIdentifier();
    
    /**
     * @param identifier manually assigned protocol-unique identifier
     */
    public void setIdentifier(String identifier);
    
    /**
     * @return Returns the root of the workspace
     */
    public RealNode getRootNode();
    
    /**
     * @param rootNode mandatory association to the root of the workspace
     */
    public void setRootNode(RealNode rootNode);
    
    /**
     * Convenience method to access the reference
     * @return Returns the reference to the workspace as a store
     */
    public StoreRef getStoreRef();
}
