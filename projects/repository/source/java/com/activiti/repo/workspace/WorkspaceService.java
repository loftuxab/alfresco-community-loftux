package com.activiti.repo.workspace;

import com.activiti.repo.ref.StoreRef;

/**
 * Public interface for <b>workspace</b> operations
 * 
 * @author derekh
 */
public interface WorkspaceService
{
    /**
     * Create a new workspace for the given protocol and identifier
     * 
     * @param protocol the implementation protocol
     * @param identifier the protocol-specific identifier
     * @return Returns a reference to the workspace store
     */
    public StoreRef createWorkspace(String protocol, String identifier);
}
