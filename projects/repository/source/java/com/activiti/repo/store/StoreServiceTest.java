package com.activiti.repo.store;

import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.util.BaseSpringTest;

/**
 * Tests the default implementation of the <code>WorkspaceService</code>
 * 
 * @see com.activiti.repo.store.StoreService
 *
 * @author Derek Hulley
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
    
    /**
     * @return Returns a reference to the created store
     */
    private StoreRef createStore() throws Exception
    {
        StoreRef storeRef = storeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "my store");
        assertNotNull("No reference returned", storeRef);
        // done
        return storeRef;
    }
    
    public void testCreateStore() throws Exception
    {
        createStore();
    }
    
    public void testExists() throws Exception
    {
        StoreRef storeRef = createStore();
        boolean exists = storeService.exists(storeRef);
        assertEquals("Exists failed", true, exists);
        // create bogus ref
        StoreRef bogusRef = new StoreRef("What", "the");
        exists = storeService.exists(bogusRef);
        assertEquals("Exists failed", false, exists);
    }
    
    public void testGetRootNode() throws Exception
    {
        StoreRef storeRef = createStore();
        // get the root node
        NodeRef rootNodeRef = storeService.getRootNode(storeRef);
        assertNotNull("No root node reference returned", rootNodeRef);
    }
}
