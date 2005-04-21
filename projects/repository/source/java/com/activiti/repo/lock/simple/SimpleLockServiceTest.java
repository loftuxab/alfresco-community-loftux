/**
 * Created on Apr 19, 2005
 */
package com.activiti.repo.lock.simple;

import java.io.Serializable;
import java.util.HashMap;

import com.activiti.repo.dictionary.bootstrap.DictionaryBootstrap;
import com.activiti.repo.lock.InsufficientPrivelegesToRealeseLockException;
import com.activiti.repo.lock.LockService;
import com.activiti.repo.lock.NodeAlreadyLockedException;
import com.activiti.repo.lock.LockService.LockStatus;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;
import com.activiti.util.BaseSpringTest;

/**
 * Simple lock service test
 * 
 * @author Roy Wetherall
 */
public class SimpleLockServiceTest extends BaseSpringTest
{
    private static final String USER_REF1 = "userRef1";
    private static final String USER_REF2 = "userRef2";
    
    /**
     * Services used in tests
     */
    private NodeService nodeService = null;
    private LockService lockService = null;
    
    /**
     * Data used in tests
     */
    private NodeRef parentNode = null;
    private NodeRef childNode1 = null;
    private NodeRef childNode2 = null;    

    /**
     * Called during the transaction setup
     */
    protected void onSetUpInTransaction() throws Exception
    {
        this.nodeService = (NodeService)applicationContext.getBean("dbNodeService");
        this.lockService = (LockService)applicationContext.getBean("simpleLockService");
        
        // Create the node properties
        HashMap<QName, Serializable> nodeProperties = new HashMap<QName, Serializable>();
        nodeProperties.put(QName.createQName("{test}property1"), "value1");
        
        // Create a workspace that contains the 'live' nodes
        StoreRef storeRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        
        // Get a reference to the root node
        NodeRef rootNodeRef = this.nodeService.getRootNode(storeRef);
        
        // Create node 
        this.parentNode = this.nodeService.createNode(
                rootNodeRef, 
                QName.createQName("{}ParentNode"), 
                DictionaryBootstrap.TYPE_CONTAINER,
                nodeProperties).getChildRef();
        assertNotNull(this.parentNode);
        
        // Add some children to the node
        this.childNode1 = this.nodeService.createNode(
                this.parentNode,
                QName.createQName("{}ChildNode1"),
                DictionaryBootstrap.TYPE_CONTAINER,
                nodeProperties).getChildRef();
        assertNotNull(this.childNode1);
        this.childNode2 = this.nodeService.createNode(
                this.parentNode,
                QName.createQName("{}ChildNode2"),
                DictionaryBootstrap.TYPE_CONTAINER,
                nodeProperties).getChildRef();
        assertNotNull(this.childNode2);
    }
    
    /**
     * Test lock
     */
    public void testLock()
    {
        // Check that the node is not currently locked
        assertEquals(
                LockStatus.UNLOCKED, 
                this.lockService.getLockStatus(this.parentNode, USER_REF1));
        
        // Test valid lock
        this.lockService.lock(this.parentNode, USER_REF1);
        assertEquals(
                LockStatus.LOCK_OWNER, 
                this.lockService.getLockStatus(this.parentNode, USER_REF1));
        assertEquals(
                LockStatus.LOCKED,
                this.lockService.getLockStatus(this.parentNode, USER_REF2));
        
        // Test lock when already locked
        try
        {
            this.lockService.lock(this.parentNode, USER_REF2);
            fail("The user should not be able to lock the node since it is already locked by another user.");
        }
        catch (NodeAlreadyLockedException exception)
        {
        }
        
        // Test already locked by this user
        try
        {
            this.lockService.lock(this.parentNode, USER_REF1);
        }
        catch (Exception exception)
        {
            fail("No error should be thrown when a node is re-locked by the current lock owner.");
        }
    }

    /**
     * Test lock with lockChildren == true
     */
    public void testLockChildren()
    {
    }
    
    /**
     * Test lock with collection
     */
    public void testLockMany()
    {
    }
    
    /**
     * Test unlock node
     */
    public void testUnlock()
    {
        // Lock the parent node
        testLock();
        
        // Try and unlock a locked node
        try
        {
            this.lockService.unlock(this.parentNode, USER_REF2);
            fail("A user cannot unlock a node that is currently lock by another user.");
        }
        catch (InsufficientPrivelegesToRealeseLockException exception)
        {
        }
        
        // Unlock the node
        this.lockService.unlock(this.parentNode, USER_REF1);
        assertEquals(
                LockStatus.UNLOCKED,
                this.lockService.getLockStatus(this.parentNode, USER_REF1));
        assertEquals(
                LockStatus.UNLOCKED,
                this.lockService.getLockStatus(this.parentNode, USER_REF2));
        
        // Try and unlock node with no lock
        try
        {
            this.lockService.unlock(this.parentNode, USER_REF1);
        }
        catch (Exception exception)
        {
            fail("Unlocking an unlocked node should not result in an exception being raised.");
        }
    }
    
    public void testUnlockChildren()
    {
    }
    
    public void testUnlockMany()
    {
    }
    
    /**
     * Test getLockStatus
     */
    public void testGetLockStatus()
    {
        // TODO check that fails when called with a node without the lock aspect
        
        // Check an unlocked node
        LockStatus lockStatus1 = this.lockService.getLockStatus(this.parentNode, USER_REF1);
        assertEquals(LockStatus.UNLOCKED, lockStatus1);
        
        // Simulate the node being locked by user1
        this.nodeService.setProperty(this.parentNode, LockService.ATT_LOCK_OWNER, USER_REF1);
        
        // Check for locked status
        LockStatus lockStatus2 = this.lockService.getLockStatus(this.parentNode, USER_REF2);
        assertEquals(LockStatus.LOCKED, lockStatus2);
        
        // Check for lock owner status
        LockStatus lockStatus3 = this.lockService.getLockStatus(this.parentNode, USER_REF1);
        assertEquals(LockStatus.LOCK_OWNER, lockStatus3);
    }
}
