/**
 * Created on Apr 14, 2005
 */
package org.alfresco.repo.lock.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.lock.LockService;
import org.alfresco.repo.lock.LockStatus;
import org.alfresco.repo.lock.LockType;
import org.alfresco.repo.lock.NodeLockedException;
import org.alfresco.repo.lock.UnableToAquireLockException;
import org.alfresco.repo.lock.UnableToReleaseLockException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.util.AspectMissingException;
import org.alfresco.util.debug.CodeMonkey;

/**
 * Simple Lock service implementation
 * 
 * @author Roy Wetherall
 */
public class LockServiceImpl implements LockService
{
    /**
     * The node service
     */
    private NodeService nodeService;
    
    /**
     * The policy component
     */
    private PolicyComponent policyComponent;
	
	/**
	 * List of node ref's to ignore when checking for locks
	 */
	private Set<NodeRef> ignoreNodeRefs = new HashSet<NodeRef>();
    
    /**
     * Set the node service
     * 
     * @param nodeService  the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
	/**
	 * Sets the policy component
	 * 
	 * @param policyComponent the policy componentO
	 */
    public void setPolicyComponent(PolicyComponent policyComponent) 
	{
		this.policyComponent = policyComponent;
	}
    
    /**
     * Initialise methods called by Spring framework
     */
    public void initialise()
    {
        // Register the various class behaviours to enable lock checking
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "beforeCreateVersion"),
				DictionaryBootstrap.ASPECT_QNAME_LOCKABLE,
				new JavaBehaviour(this, "checkForLock"));	
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "beforeUpdateNode"),
				DictionaryBootstrap.ASPECT_QNAME_LOCKABLE,
				new JavaBehaviour(this, "checkForLock"));
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "beforeDeleteNode"),
				DictionaryBootstrap.ASPECT_QNAME_LOCKABLE,
				new JavaBehaviour(this, "checkForLock"));
		
		// Register onCopy class behaviour
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCopyNode"),
				DictionaryBootstrap.ASPECT_QNAME_LOCKABLE,
				new JavaBehaviour(this, "onCopy"));
		
		// Register the onCreateVersion behavior for the version aspect
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateVersion"),
				DictionaryBootstrap.ASPECT_QNAME_LOCKABLE,
				new JavaBehaviour(this, "onCreateVersion"));
    }
    
    /**
     * @see org.alfresco.repo.lock.LockService#lock(org.alfresco.repo.ref.NodeRef, java.lang.String, LockType)
     */
    public synchronized void lock(NodeRef nodeRef, String userRef, LockType lockType)
        throws UnableToAquireLockException, AspectMissingException
    {
        // Check for lock aspect
        checkForLockApsect(nodeRef);
        
        // Set a default value
        if (lockType == null)
        {
            lockType = LockType.WRITE_LOCK;
        }
        
        LockStatus currentLockStatus = getLockStatus(nodeRef, userRef);
        if (LockStatus.LOCKED.equals(currentLockStatus) == true)
        {
            // Error since we are trying to lock a locked node
            throw new UnableToAquireLockException(nodeRef);
        }
        else if (LockStatus.NO_LOCK.equals(currentLockStatus) == true)
        {
			this.ignoreNodeRefs.add(nodeRef);
			try
			{
	            // Set the current user as the lock owner
	            this.nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_LOCK_OWNER, userRef);
	            this.nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_LOCK_TYPE, lockType);
			}
			finally
			{
				this.ignoreNodeRefs.remove(nodeRef);
			}
        }        
    }

    /**
     * @see org.alfresco.repo.lock.LockService#lock(org.alfresco.repo.ref.NodeRef, java.lang.String, LockType, boolean)
     */
    public synchronized void lock(NodeRef nodeRef, String userRef, LockType lockType, boolean lockChildren)
        throws UnableToAquireLockException, AspectMissingException
    {
        lock(nodeRef, userRef, LockType.WRITE_LOCK);
        
        if (lockChildren == true)
        {
            Collection<ChildAssocRef> childAssocRefs = this.nodeService.getChildAssocs(nodeRef);
            for (ChildAssocRef childAssocRef : childAssocRefs)
            {
                lock(childAssocRef.getChildRef(), userRef, lockType, lockChildren);
            }
        }       
    }

    /**
     * @see org.alfresco.repo.lock.LockService#lock(java.util.Collection, java.lang.String, LockType)
     */
    public synchronized void lock(Collection<NodeRef> nodeRefs, String userRef, LockType lockType)
        throws UnableToAquireLockException, AspectMissingException
    {        
        // Lock each of the specifed nodes
        for (NodeRef nodeRef : nodeRefs)
        {
            lock(nodeRef, userRef, LockType.WRITE_LOCK);
        }        
    }

    /**
     * @see org.alfresco.repo.lock.LockService#unlock(NodeRef, String)
     */
    public synchronized void unlock(NodeRef nodeRef, String userRef)
        throws UnableToReleaseLockException, AspectMissingException
    {
        // Check for lock aspect
        checkForLockApsect(nodeRef);
        
        LockStatus lockStatus = getLockStatus(nodeRef, userRef);
        if (LockStatus.LOCKED.equals(lockStatus) == true)
        {
            // Error since the lock can only be released by the lock owner
            throw new UnableToReleaseLockException(nodeRef);
        }
        else if (LockStatus.LOCK_OWNER.equals(lockStatus) == true)
        {
			this.ignoreNodeRefs.add(nodeRef);
			try
			{
	            // Clear the lock owner
	            this.nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_LOCK_OWNER, null);
	            this.nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_LOCK_TYPE, null);
			}
			finally
			{
				this.ignoreNodeRefs.remove(nodeRef);
			}
        }      
    }

    /**
     * @see org.alfresco.repo.lock.LockService#unlock(NodeRef, String, boolean)
     */
    public synchronized void unlock(NodeRef nodeRef, String userRef, boolean unlockChildren)
        throws UnableToReleaseLockException, AspectMissingException
    {
        // Unlock the parent
        unlock(nodeRef, userRef);
        
        if (unlockChildren == true)
        {
            // Get the children and unlock them
            Collection<ChildAssocRef> childAssocRefs = this.nodeService.getChildAssocs(nodeRef);
            for (ChildAssocRef childAssocRef : childAssocRefs)
            {
                unlock(childAssocRef.getChildRef(), userRef, unlockChildren);
            }
        }        
    }

    /**
     * @see org.alfresco.repo.lock.LockService#unlock(Collection<NodeRef>, String)
     */
    public synchronized void unlock(Collection<NodeRef> nodeRefs, String userRef)
        throws UnableToReleaseLockException, AspectMissingException
    {        
        for (NodeRef nodeRef : nodeRefs)
        {
            unlock(nodeRef, userRef);
        }         
    }
    
    /**
     * @see org.alfresco.repo.lock.LockService#getLockStatus(NodeRef, String)
     */
    public LockStatus getLockStatus(NodeRef nodeRef, String userRef)
        throws AspectMissingException
    {
        // Check for lock aspect
        checkForLockApsect(nodeRef);
        
        LockStatus result = LockStatus.NO_LOCK;
        
        // Get the current lock owner
        String currentUserRef = (String)this.nodeService.getProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_LOCK_OWNER);
        if (currentUserRef != null && currentUserRef.length() != 0)
        {
            if (currentUserRef.equals(userRef) == true)
            {
                result = LockStatus.LOCK_OWNER;
            }
            else
            {
                result = LockStatus.LOCKED;
            }
        }
        
        return result;
        
    }
    
    /**
     * @see LockService#getLockType(NodeRef)
     */
    public LockType getLockType(NodeRef nodeRef)
        throws AspectMissingException
    {
        // Check for the lock aspect
        checkForLockApsect(nodeRef);
        
        // Return the lock type enum
        return (LockType)this.nodeService.getProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_LOCK_TYPE);        
    }
    
    /**
     * Checks for the lock aspect.  Throws an expception if it is missing.
     * 
     * @param nodeRef   the node reference
     * @throws AspectMissingException
     *                  thrown if the lock aspect is missing
     */
    private void checkForLockApsect(NodeRef nodeRef)
        throws AspectMissingException
    {
        if (this.nodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_LOCKABLE) == false)
        {
            throw new AspectMissingException(DictionaryBootstrap.ASPECT_QNAME_LOCKABLE, nodeRef);
        }
    }
	
	/**
     * @see LockService#checkForLock(NodeRef)
     */
    public void checkForLock(NodeRef nodeRef)
		throws NodeLockedException
    {
		CodeMonkey.todo("We should be looking up the current user here.");
		
        // Check the lock
        checkForLockWithUser(nodeRef, LockService.LOCK_USER);
    }
    
    /**
     * @see LockService#checkForLockWithUser(NodeRef, String)
     */
    public void checkForLockWithUser(NodeRef nodeRef, String userRef)
        throws NodeLockedException
    {     
		// Ensure we have found a node reference
        if (nodeRef != null && userRef != null)
        {
			// Check to see if should just ignore this node
			if (this.ignoreNodeRefs.contains(nodeRef) == false)
			{
	            try
	            {
	                LockType lockType = getLockType(nodeRef);
	                if (LockType.WRITE_LOCK.equals(lockType) == true)
	                {
	                    // Get the current lock status on the node ref
	                    LockStatus currentLockStatus = getLockStatus(nodeRef, userRef);
	                    
	                    if (LockStatus.LOCKED.equals(currentLockStatus) == true)
	                    {
	                        // Error since we are trying to preform an operation on a locked node
	                        throw new NodeLockedException(nodeRef);
	                    }
	                }
	                else if (LockType.READ_ONLY_LOCK.equals(lockType) == true)
	                {
	                    // Error since there is a read only lock on this object and all 
	                    // modifications are prevented
	                    throw new NodeLockedException(nodeRef);
	                }
	            }
	            catch (AspectMissingException exception)
	            {
	                // Ignore since this indicates that the node does not have the lock  
	                // aspect applied
	            }
			}
        }
    }	
	
	/**
	 * OnCopy behaviour implementation for the lock aspect.
	 * <p>
	 * Ensures that the propety values of the lock aspect are not copied onto
	 * the destination node.
	 * 
	 * @param sourceClassRef  the source class reference
	 * @param sourceNodeRef	  the source node reference
	 * @param copyDetails	  the copy details
	 */
	public void onCopy(QName sourceClassRef, NodeRef sourceNodeRef, PolicyScope copyDetails)
	{
		// Add the lock aspect, but do not copy any of the properties
		copyDetails.addAspect(DictionaryBootstrap.ASPECT_QNAME_LOCKABLE);
	}
	
	/**
	 * OnCreateVersion behaviour for the lock aspect
	 * <p>
	 * Ensures that the property valies of the lock aspect are not
	 * 'frozen' in the version store.
	 * 
	 * @param classRef				the class reference
	 * @param versionableNode		the versionable node reference
	 * @param versionProperties		the version properties
	 * @param nodeDetails			the details of the node to be versioned
	 */
	public void onCreateVersion(
			QName classRef,
			NodeRef versionableNode, 
			Map<String, Serializable> versionProperties,
			PolicyScope nodeDetails)
	{
		// Add the lock aspect, but do not version the property values
		nodeDetails.addAspect(DictionaryBootstrap.ASPECT_QNAME_LOCKABLE);
	}
}
