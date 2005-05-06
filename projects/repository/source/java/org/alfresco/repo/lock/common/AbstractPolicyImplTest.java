/**
 * Created on May 3, 2005
 */
package org.alfresco.repo.lock.common;

import java.io.Serializable;
import java.util.HashMap;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.lock.LockService;
import org.alfresco.repo.lock.NodeLockedException;
import org.alfresco.repo.lock.LockService.LockType;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.util.BaseSpringTest;

/**
 * AbstractPolicyImpl Unit Test.
 * 
 * @author Roy Wetherall
 */
public class AbstractPolicyImplTest extends BaseSpringTest
{
    /**
     * Data used in the tests
     */
    private static final String GOOD_USER = "tempUser";  // TODO this is the user applied to all lock checks since we can't determine the current user yet
    private static final String BAD_USER = "badUser";
    
    /**
     * The test policy implementation
     */
    private TestPolicyImpl policyImpl;
    
    /**
     * The lock service
     */
    private LockService lockService;
    
    /**
     * The node service
     */
    private NodeService nodeService;

    /**
     * Node references used in the tests
     */
    private NodeRef nodeRef;
    private NodeRef noAspectNode;
    
    /**
     * Set up in transaction implmentation
     */
    protected void onSetUpInTransaction() throws Exception
    {
        this.nodeService = (NodeService)applicationContext.getBean("dbNodeService");
        this.lockService = (LockService)applicationContext.getBean("simpleLockService");
        
        // Get the lock aspect class ref
        ClassRef lockAspect = new ClassRef(LockService.ASPECT_QNAME_LOCK);
        
        // Create the node properties
        HashMap<QName, Serializable> nodeProperties = new HashMap<QName, Serializable>();
        nodeProperties.put(QName.createQName("{test}property1"), "value1");
        
        // Create a workspace that contains the 'live' nodes
        StoreRef storeRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        
        // Get a reference to the root node
        NodeRef rootNodeRef = this.nodeService.getRootNode(storeRef);
        
        // Create node 
        this.nodeRef = this.nodeService.createNode(
                rootNodeRef, 
                QName.createQName("{}ParentNode"), 
                DictionaryBootstrap.TYPE_CONTAINER,
                nodeProperties).getChildRef();
        this.nodeService.addAspect(this.nodeRef, lockAspect, new HashMap<QName, Serializable>());
        assertNotNull(this.nodeRef);
        
        // Create a node with no lockAspect
        this.noAspectNode = this.nodeService.createNode(
                rootNodeRef, 
                QName.createQName("{}noAspectNode"), 
                DictionaryBootstrap.TYPE_CONTAINER,
                nodeProperties).getChildRef();
        assertNotNull(this.noAspectNode);
        
        // Create the policy implementation
        this.policyImpl = new TestPolicyImpl(this.lockService);
    }

    /**
     * Test checkForLock (no user specified)
     */
    public void testCheckForLockNoUser()
    {
        // NOTE:  we are current presumed to be the GOOD_USER
        
        this.policyImpl.checkForLock(this.nodeRef);
        this.policyImpl.checkForLock(this.noAspectNode);
        
        // Give the node a write lock (as the good user)
        this.lockService.lock(this.nodeRef, GOOD_USER, LockType.WRITE_LOCK);    
        this.policyImpl.checkForLock(this.nodeRef);
        
        // Give the node a read only lock (as the good user)
        this.lockService.unlock(this.nodeRef, GOOD_USER);
        this.lockService.lock(this.nodeRef, GOOD_USER, LockType.READ_ONLY_LOCK);
        try
        {
            this.policyImpl.checkForLock(this.nodeRef);
            fail("The node locked exception should have been raised");
        }
        catch (NodeLockedException exception)
        {
            // Correct behaviour
        }
        
        // Give the node a write lock (as the bad user)
        this.lockService.unlock(this.nodeRef, GOOD_USER);
        this.lockService.lock(this.nodeRef, BAD_USER, LockType.WRITE_LOCK);        
        try
        {
            this.policyImpl.checkForLock(this.nodeRef);
            fail("The node locked exception should have been raised");
        }
        catch (NodeLockedException exception)
        {
            // Correct behaviour
        }
        
        // Give the node a read only lock (as the bad user)
        this.lockService.unlock(this.nodeRef, BAD_USER);
        this.lockService.lock(this.nodeRef, BAD_USER, LockType.READ_ONLY_LOCK);        
        try
        {
            this.policyImpl.checkForLock(this.nodeRef);
            fail("The node locked exception should have been raised");
        }
        catch (NodeLockedException exception)
        {
            // Correct behaviour
        }
    }
    
    /**
     * Test checkForLock (user specified)
     */
    public void testCheckForLock()
    {
        this.policyImpl.checkForLock(this.nodeRef, GOOD_USER);
        this.policyImpl.checkForLock(this.nodeRef, BAD_USER);
        this.policyImpl.checkForLock(this.noAspectNode, GOOD_USER);
        this.policyImpl.checkForLock(this.noAspectNode, BAD_USER);
        
        // Give the node a write lock
        this.lockService.lock(this.nodeRef, GOOD_USER, LockType.WRITE_LOCK);        
        this.policyImpl.checkForLock(this.nodeRef, GOOD_USER);
        
        try
        {
            this.policyImpl.checkForLock(this.nodeRef, BAD_USER);
            fail("The node locked exception should have been raised");
        }
        catch (NodeLockedException exception)
        {
            // Correct behaviour
        }
        
        // Give the node a read only lock
        this.lockService.unlock(this.nodeRef, GOOD_USER);
        this.lockService.lock(this.nodeRef, GOOD_USER, LockType.READ_ONLY_LOCK);
        
        try
        {
            this.policyImpl.checkForLock(this.nodeRef, GOOD_USER);
            fail("The node locked exception should have been raised");
        }
        catch (NodeLockedException exception)
        {
            // Correct behaviour
        }
        try
        {
            this.policyImpl.checkForLock(this.nodeRef, BAD_USER);
            fail("The node locked exception should have been raised");
        }
        catch (NodeLockedException exception)
        {
            // Correct behaviour
        }
    }
    
    /**
     * Test implementation of the abstract policy class
     * 
     * @author Roy Wetherall
     */
    private class TestPolicyImpl extends AbstractPolicyImpl
    {
        /**
         * Constructor
         * 
         * @param lockService  the lock service
         */
        public TestPolicyImpl(LockService lockService)
        {
            super(lockService);
        }
    }
    
}
