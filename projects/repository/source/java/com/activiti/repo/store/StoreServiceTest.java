package com.activiti.repo.store;

import com.activiti.repo.ref.StoreRef;
import com.activiti.util.BaseSpringTest;

/**
 * Tests the default implementation of the <code>WorkspaceService</code>
 * 
 * @see com.activiti.repo.store.StoreService
 *
 * @author derekh
 */
public class StoreServiceTest extends BaseSpringTest
{
    private StoreService storeService;

    public void setStoreService(StoreService storeService)
    {
        this.storeService = storeService;
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull("storeService not set", storeService);
    }
    
    public void testCreateWorkspace() throws Exception
    {
        StoreRef storeRef = storeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "my store");
        assertNotNull("No reference returned", storeRef);
    }
}
