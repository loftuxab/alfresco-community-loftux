package com.activiti.repo.workspace;

import com.activiti.repo.ref.StoreRef;
import com.activiti.util.BaseSpringTest;

/**
 * Tests the default implementation of the <code>WorkspaceService</code>
 * 
 * @see com.activiti.repo.workspace.WorkspaceService
 *
 * @author derekh
 */
public class WorkspaceServiceTest extends BaseSpringTest
{
    private WorkspaceService workspaceService;

    public void setWorkspaceService(WorkspaceService workspaceService)
    {
        this.workspaceService = workspaceService;
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull("workspaceService not set", workspaceService);
    }
    
    public void testCreateWorkspace() throws Exception
    {
        StoreRef storeRef = workspaceService.createWorkspace(StoreRef.PROTOCOL_WORKSPACE, "my workspace");
        assertNotNull("No reference returned", storeRef);
    }
}
