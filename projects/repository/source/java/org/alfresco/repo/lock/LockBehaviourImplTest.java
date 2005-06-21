/**
 * Created on May 3, 2005
 */
package org.alfresco.repo.lock;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.lock.NodeLockedException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;

/**
 * LockBehaviourImpl Unit Test.
 * 
 * @author Roy Wetherall
 */
public class LockBehaviourImplTest extends BaseSpringTest
{
    /**
     * Data used in the tests
     */
    private static final String GOOD_USER = LockService.LOCK_USER;  // TODO this is the user applied to all lock checks since we can't determine the current user yet
    private static final String BAD_USER = "badUser";
    
    /**
     * The lock service
     */
    private LockService lockService;
	
	/**
	 * The version service
	 */
	private VersionService versionService;
    
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
        this.lockService = (LockService)applicationContext.getBean("lockService");
		this.versionService = (VersionService)applicationContext.getBean("versionService");
        
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
				ContentModel.ASSOC_CONTAINS, 
                QName.createQName("{}ParentNode"),
                ContentModel.TYPE_CONTAINER,
                nodeProperties).getChildRef();
        this.nodeService.addAspect(this.nodeRef, ContentModel.ASPECT_LOCKABLE, new HashMap<QName, Serializable>());
        assertNotNull(this.nodeRef);
        
        // Create a node with no lockAspect
        this.noAspectNode = this.nodeService.createNode(
                rootNodeRef, 
				ContentModel.ASSOC_CONTAINS, 
                QName.createQName("{}noAspectNode"),
                ContentModel.TYPE_CONTAINER,
                nodeProperties).getChildRef();
        assertNotNull(this.noAspectNode);        
    }

    /**
     * Test checkForLock (no user specified)
     */
    public void testCheckForLockNoUser()
    {
        // NOTE:  we are current presumed to be the GOOD_USER        	
		
        this.lockService.checkForLock(this.nodeRef);
        this.lockService.checkForLock(this.noAspectNode);
        
        // Give the node a write lock (as the good user)
        this.lockService.lock(this.nodeRef, GOOD_USER, LockType.WRITE_LOCK);    
        this.lockService.checkForLock(this.nodeRef);
        
        // Give the node a read only lock (as the good user)
        this.lockService.unlock(this.nodeRef, GOOD_USER);
        this.lockService.lock(this.nodeRef, GOOD_USER, LockType.READ_ONLY_LOCK);
        try
        {
            this.lockService.checkForLock(this.nodeRef);
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
            this.lockService.checkForLock(this.nodeRef);
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
            this.lockService.checkForLock(this.nodeRef);
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
        this.lockService.checkForLockWithUser(this.nodeRef, GOOD_USER);
        this.lockService.checkForLockWithUser(this.nodeRef, BAD_USER);
        this.lockService.checkForLockWithUser(this.noAspectNode, GOOD_USER);
        this.lockService.checkForLockWithUser(this.noAspectNode, BAD_USER);
        
        // Give the node a write lock
        this.lockService.lock(this.nodeRef, GOOD_USER, LockType.WRITE_LOCK);        
        this.lockService.checkForLockWithUser(this.nodeRef, GOOD_USER);
        
        try
        {
            this.lockService.checkForLockWithUser(this.nodeRef, BAD_USER);
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
            this.lockService.checkForLockWithUser(this.nodeRef, GOOD_USER);
            fail("The node locked exception should have been raised");
        }
        catch (NodeLockedException exception)
        {
            // Correct behaviour
        }
        try
        {
            this.lockService.checkForLockWithUser(this.nodeRef, BAD_USER);
            fail("The node locked exception should have been raised");
        }
        catch (NodeLockedException exception)
        {
            // Correct behaviour
        }
    }
	
	/**
	 * Test version service lock checking
	 */
	public void testVersionServiceLockBehaviour()
	{
		// Add the version aspect to the node
		this.nodeService.addAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE, null);
		
		try
		{
			this.versionService.createVersion(this.nodeRef, new HashMap<String, Serializable>());
		}
		catch (NodeLockedException exception)
		{
			fail("There is no lock so this should have worked.");
		}
		
		// Lock the node as the good user with a write lock
		this.lockService.lock(this.nodeRef, GOOD_USER, LockType.WRITE_LOCK);
		try
		{
			this.versionService.createVersion(this.nodeRef, new HashMap<String, Serializable>());
		}
		catch (NodeLockedException exception)
		{
			fail("Tried to version as the lock owner so should work.");
		}
		this.lockService.unlock(this.nodeRef, GOOD_USER);
		
		// Lock the node as the good user with a read only lock
		this.lockService.lock(this.nodeRef, GOOD_USER, LockType.READ_ONLY_LOCK);
		try
		{
			this.versionService.createVersion(this.nodeRef, new HashMap<String, Serializable>());
			fail("Should have failed since this node has been locked with a read only lock.");
		}
		catch (NodeLockedException exception)
		{
		}
		this.lockService.unlock(this.nodeRef, GOOD_USER);
		
		// Lock the node as the bad user with a write lock
		this.lockService.lock(this.nodeRef, BAD_USER, LockType.WRITE_LOCK);
		try
		{
			this.versionService.createVersion(this.nodeRef, new HashMap<String, Serializable>());
			fail("Shoudl have failed since this node has been locked by another user with a write lock.");
		}
		catch (NodeLockedException exception)
		{
		}
	}
	
	/**
	 * Test that the node service lock behaviour is as we expect
	 *
	 */
	public void testNodeServiceLockBehaviour()
	{
		// Check that we can create a new node and set of it properties when no lock is present
		ChildAssociationRef childAssocRef = this.nodeService.createNode(
				this.nodeRef, 
				ContentModel.ASSOC_CONTAINS,
				QName.createQName("{test}nodeServiceLockTest"),
				ContentModel.TYPE_CONTAINER);
		NodeRef nodeRef = childAssocRef.getChildRef();
		
		// Lets lock the parent node and check that whether we can still create a new node
		this.lockService.lock(this.nodeRef, GOOD_USER, LockType.WRITE_LOCK);
		ChildAssociationRef childAssocRef2 = this.nodeService.createNode(
				this.nodeRef, 
				ContentModel.ASSOC_CONTAINS,				
				QName.createQName("{test}nodeServiceLockTest"),
				ContentModel.TYPE_CONTAINER);
		NodeRef nodeRef2 = childAssocRef.getChildRef();
		
		// Lets check that we can do other stuff with the node since we have it locked
		this.nodeService.setProperty(this.nodeRef, QName.createQName("{test}prop1"), "value1");
		Map<QName, Serializable> propMap = new HashMap<QName, Serializable>();
		propMap.put(QName.createQName("{test}prop2"), "value2");
		this.nodeService.setProperties(this.nodeRef, propMap);
		this.nodeService.removeAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE);
		// TODO there are various other calls that could be more vigirously checked
		
		// Lock the node as the 'bad' user
		this.lockService.unlock(this.nodeRef,GOOD_USER);
		this.lockService.lock(this.nodeRef, BAD_USER, LockType.WRITE_LOCK);
		
		// Lets check that we can't create a new child 
		try
		{
			this.nodeService.createNode(
					this.nodeRef, 
					ContentModel.ASSOC_CONTAINS,
					QName.createQName("{test}nodeServiceLockTest"),
					ContentModel.TYPE_CONTAINER);
			fail("The parent is locked so a new child should not have been created.");
		}
		catch(NodeLockedException exception)
		{
		}
		
		// TODO various other tests along these lines ...
		
		// TODO check that delete is also working
	}
    
}
