package com.activiti.repo.store;

import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.util.BaseSpringTest;

/**
 * Provides basic tests for implementations of <code>StoreService</code>.
 * <p>
 * Derived classes can work against any implementation of the <code>StoreService</code>
 * and be sure that the comprehensive set of tests is being run.
 * 
 * @see #storeService
 * @see #getStoreService()
 * @see com.activiti.repo.store.StoreService
 *
 * @author Derek Hulley
 */
public abstract class BaseStoreServiceTest extends BaseSpringTest
{
    private StoreService storeService;

    protected void onSetUpInTransaction() throws Exception
    {
        storeService = getStoreService();
    }

    /**
     * Usually just implemented by fetching the bean directly from the bean factory,
     * for example:
     * <p>
     * <pre>
     *      return (StoreService) applicationContext.getBean("dbStoreService");
     * </pre>
     * 
     * @return Returns the implementation of <code>StoreService</code> to be
     *      used for this test
     */
    protected abstract StoreService getStoreService();
    
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
