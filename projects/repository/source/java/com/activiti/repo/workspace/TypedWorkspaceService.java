package com.activiti.repo.workspace;

import com.activiti.repo.domain.Workspace;

/**
 * Provides methods for accessing workspace entities directly
 * 
 * @author derekh
 */
public interface TypedWorkspaceService
{
    /**
     * Creates a unique workspace for the given protocol and identifier combination
     * 
     * @param protocol a protocol, e.g. {@link com.activiti.repo.ref.StoreRef#PROTOCOL_WORKSPACE}
     * @param identifier a protocol-specific identifier
     * @return Returns the new persistent entity
     */
    public Workspace createWorkspace(String protocol, String identifier);
    
    /**
     * @param protocol the protocol that the workspace serves
     * @param identifier the protocol-specific identifer
     * @return Returns a workspace with the given values or null if one doesn't exist
     */
    public Workspace findWorkspace(String protocol, String identifier);
}
