package com.activiti.repo.workspace;

import com.activiti.repo.domain.Workspace;
import com.activiti.repo.ref.StoreRef;

public class WorkspaceServiceImpl implements WorkspaceService
{
    private TypedWorkspaceService typedWorkspaceService;
    
    public void setTypedWorkspaceService(TypedWorkspaceService typedWorkspaceService)
    {
        this.typedWorkspaceService = typedWorkspaceService;
    }

    /**
     * Defers to the typed service
     * @see TypedWorkspaceService#createWorkspace(String)
     */
    public StoreRef createWorkspace(String protocol, String identifier)
    {
        Workspace workspace = typedWorkspaceService.createWorkspace(protocol, identifier);
        // done
        StoreRef storeRef = workspace.getStoreRef();
        return storeRef;
    }
}
