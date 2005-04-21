/**
 * Created on Apr 14, 2005
 */
package com.activiti.repo.lock.simple;

import java.util.Collection;

import com.activiti.repo.lock.InsufficientPrivelegesToRealeseLockException;
import com.activiti.repo.lock.LockService;
import com.activiti.repo.lock.NodeAlreadyLockedException;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;

/**
 * Simple Lock service implementation
 * 
 * @author Roy Wetherall
 */
public class SimpleLockService implements LockService
{
    /**
     * Static object used to make the lock operaions thread safe
     */
    private final static Object syncObject = new Object();
    
    /**
     * The node service
     */
    private NodeService nodeService = null;
    
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
     * TODO need to check whether I really need to make this thread safe
     * 
     * @see com.activiti.repo.lock.LockService#lock(com.activiti.repo.ref.NodeRef, java.lang.String)
     */
    public void lock(NodeRef nodeRef, String userRef)
        throws NodeAlreadyLockedException
    {
        synchronized (SimpleLockService.syncObject)
        {
            lockImpl(nodeRef, userRef);
        }
    }

    /**
     * @see com.activiti.repo.lock.LockService#lock(com.activiti.repo.ref.NodeRef, java.lang.String, boolean)
     */
    public void lock(NodeRef nodeRef, String userRef, boolean lockChildren)
        throws NodeAlreadyLockedException
    {
        synchronized (SimpleLockService.syncObject)
        {
            lockImpl(nodeRef, userRef, lockChildren);
        }
    }

    /**
     * @see com.activiti.repo.lock.LockService#lock(java.util.Collection, java.lang.String)
     */
    public void lock(Collection<NodeRef> nodeRefs, String userRef)
        throws NodeAlreadyLockedException
    {
        synchronized (SimpleLockService.syncObject)
        {
            // Lock each of the specifed nodes
            for (NodeRef nodeRef : nodeRefs)
            {
                lockImpl(nodeRef, userRef);
            }
        }
    }
    
    /**
     * Applies the lock to the node for the given user.
     * 
     * @param  nodeRef  the node reference
     * @param  userRef  the user reference
     * @throws NodeAlreadyLockedException
     *                  if the node is aleady locked by another user
     */
    private void lockImpl(NodeRef nodeRef, String userRef)
        throws NodeAlreadyLockedException
    {
        LockStatus currentLockStatus = getLockStatus(nodeRef, userRef);
        if (LockStatus.LOCKED.equals(currentLockStatus) == true)
        {
            // Error since we are trying to lock a locked node
            throw new NodeAlreadyLockedException(nodeRef);
        }
        else if (LockStatus.UNLOCKED.equals(currentLockStatus) == true)
        {
            // Set the current user as the lock owner
            this.nodeService.setProperty(nodeRef, ATT_LOCK_OWNER, userRef);
        }
    }
    
    /**
     * 
     * @param nodeRef
     * @param userRef
     * @param lockChildren
     * @throws NodeAlreadyLockedException
     */
    private void lockImpl(NodeRef nodeRef, String userRef, boolean lockChildren)
        throws NodeAlreadyLockedException
    {
        lockImpl(nodeRef, userRef);
        
        if (lockChildren == true)
        {
            Collection<ChildAssocRef> childAssocRefs = this.nodeService.getChildAssocs(nodeRef);
            for (ChildAssocRef childAssocRef : childAssocRefs)
            {
                lockImpl(childAssocRef.getChildRef(), userRef, lockChildren);
            }
        }
    }

    /**
     * @see com.activiti.repo.lock.LockService#unlock(NodeRef, String)
     */
    public void unlock(NodeRef nodeRef, String userRef)
        throws InsufficientPrivelegesToRealeseLockException
    {
        synchronized (SimpleLockService.syncObject)
        {
            unlockImpl(nodeRef, userRef);
        }
    }

    /**
     * @see com.activiti.repo.lock.LockService#unlock(NodeRef, String, boolean)
     */
    public void unlock(NodeRef nodeRef, String userRef, boolean unlockChildren)
        throws InsufficientPrivelegesToRealeseLockException
    {
        synchronized (SimpleLockService.syncObject)
        {
            unlockImpl(nodeRef, userRef, unlockChildren);
        }
    }

    /**
     * @see com.activiti.repo.lock.LockService#unlock(Collection<NodeRef>, String)
     */
    public void unlock(Collection<NodeRef> nodeRefs, String userRef)
        throws InsufficientPrivelegesToRealeseLockException
    {
        synchronized (SimpleLockService.syncObject)
        {
            for (NodeRef nodeRef : nodeRefs)
            {
                unlockImpl(nodeRef, userRef);
            }
        }  
    }
    
    /**
     * 
     * @param  nodeRef
     * @param  userRef
     * @throws InsufficientPrivelegesToRealeseLockException
     */
    private void unlockImpl(NodeRef nodeRef, String userRef)
        throws InsufficientPrivelegesToRealeseLockException
    {
        LockStatus lockStatus = getLockStatus(nodeRef, userRef);
        if (LockStatus.LOCKED.equals(lockStatus) == true)
        {
            // Error since the lock can only be released by the lock owner
            throw new InsufficientPrivelegesToRealeseLockException(nodeRef);
        }
        else if (LockStatus.LOCK_OWNER.equals(lockStatus) == true)
        {
            // Clear the lock owner
            this.nodeService.setProperty(nodeRef, ATT_LOCK_OWNER, "");
        }
    }

    /**
     * 
     * @param  nodeRef
     * @param  userRef
     * @param  unlockChildren
     * @throws InsufficientPrivelegesToRealeseLockException
     */
    private void unlockImpl(NodeRef nodeRef, String userRef, boolean unlockChildren)
        throws InsufficientPrivelegesToRealeseLockException
    {
        // Unlock the parent
        unlockImpl(nodeRef, userRef);
        
        if (unlockChildren == true)
        {
            // Get the children and unlock them
            Collection<ChildAssocRef> childAssocRefs = this.nodeService.getChildAssocs(nodeRef);
            for (ChildAssocRef childAssocRef : childAssocRefs)
            {
                unlockImpl(childAssocRef.getChildRef(), userRef, unlockChildren);
            }
        }
    }
    
    /**
     * @see com.activiti.repo.lock.LockService#getLockStatus(NodeRef, String)
     */
    public LockStatus getLockStatus(NodeRef nodeRef, String userRef)
    {
        // TODO first check that this node has the lock aspect applied
        
        LockStatus result = LockStatus.UNLOCKED;
        
        // Get the current lock owner
        String currentUserRef = (String)this.nodeService.getProperty(nodeRef, ATT_LOCK_OWNER);
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

}
