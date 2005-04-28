/**
 * Created on Apr 14, 2005
 */
package com.activiti.repo.lock.simple;

import java.util.Collection;

import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.lock.LockService;
import com.activiti.repo.lock.exception.UnableToReleaseLockException;
import com.activiti.repo.lock.exception.UnableToAquireLockException;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.policy.PolicyRuntimeService;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.version.policy.OnBeforeCreateVersionPolicy;
import com.activiti.util.AspectMissingException;

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
     * The policy runtime service
     */
    private PolicyRuntimeService policyRuntimeService = null;
    
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
     * Set the policy runtime service
     * 
     * @param policyRuntimeService  the policy runtime service
     */
    public void setPolicyRuntimeService(
            PolicyRuntimeService policyRuntimeService)
    {
        this.policyRuntimeService = policyRuntimeService;
    }
    
    /**
     * Initialise methods called by Spring framework
     */
    public void initialise()
    {
        // Register the behaviours
        this.policyRuntimeService.registerBehaviour(
                OnBeforeCreateVersionPolicy.class, 
                new OnBeforeCreateVersionPolicyImpl(),
                LockService.ASPECT_QNAME_LOCK);
    }
    
    /**
     * TODO need to check whether I really need to make this thread safe
     * 
     * @see com.activiti.repo.lock.LockService#lock(com.activiti.repo.ref.NodeRef, java.lang.String)
     */
    public void lock(NodeRef nodeRef, String userRef)
        throws UnableToAquireLockException, AspectMissingException
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
        throws UnableToAquireLockException, AspectMissingException
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
        throws UnableToAquireLockException, AspectMissingException
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
     * @throws UnableToAquireLockException
     *                  thrown if the lock cannot be obtained
     * @throws AspectMissingException
     *                  thrown if the lock aspect is missing                  
     */
    private void lockImpl(NodeRef nodeRef, String userRef)
        throws UnableToAquireLockException, AspectMissingException
    {
        // Check for lock aspect
        checkForLockApsect(nodeRef);
        
        LockStatus currentLockStatus = getLockStatus(nodeRef, userRef);
        if (LockStatus.LOCKED.equals(currentLockStatus) == true)
        {
            // Error since we are trying to lock a locked node
            throw new UnableToAquireLockException(nodeRef);
        }
        else if (LockStatus.UNLOCKED.equals(currentLockStatus) == true)
        {
            // Set the current user as the lock owner
            this.nodeService.setProperty(nodeRef, PROP_QNAME_LOCK_OWNER, userRef);
        }
    }
    
    /**
     * Applies the lock to node and optionally the children of the node.
     * 
     * @param  nodeRef       the node reference
     * @param  userRef       the user reference
     * @param  lockChildren  indicates whether to lock the children of the node
     * @throws UnableToAquireLockException
     *                       thrown if the lock cannot be obtained
     * @throws LockAspectMissing
     *                       thrown if the lock aspect is missing
     */
    private void lockImpl(NodeRef nodeRef, String userRef, boolean lockChildren)
        throws UnableToAquireLockException, AspectMissingException
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
        throws UnableToReleaseLockException, AspectMissingException
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
        throws UnableToReleaseLockException, AspectMissingException
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
        throws UnableToReleaseLockException, AspectMissingException
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
     * Releases the lock held on the node.
     * 
     * @param  nodeRef  the node reference
     * @param  userRef  the user reference
     * @throws UnableToReleaseLockException
     *                  thrown if the lock cannot be released
     * @throws AspectMissingException
     *                  thrown is the lock aspect is missing
     */
    private void unlockImpl(NodeRef nodeRef, String userRef)
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
            // Clear the lock owner
            this.nodeService.setProperty(nodeRef, PROP_QNAME_LOCK_OWNER, "");
        }
    }

    /**
     * 
     * @param  nodeRef          the node reference
     * @param  userRef          the user reference
     * @param  unlockChildren   indicates whether the children should also be unlocked
     * @throws UnableToReleaseLockException
     *                  thrown if the lock cannot be released
     * @throws AspectMissingException
     *                  thrown is the lock aspect is missing
     */
    private void unlockImpl(NodeRef nodeRef, String userRef, boolean unlockChildren)
        throws UnableToReleaseLockException, AspectMissingException
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
        throws AspectMissingException
    {
        // Check for lock aspect
        checkForLockApsect(nodeRef);
        
        LockStatus result = LockStatus.UNLOCKED;
        
        // Get the current lock owner
        String currentUserRef = (String)this.nodeService.getProperty(nodeRef, PROP_QNAME_LOCK_OWNER);
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
     * Checks for the lock aspect.  Throws an expception if it is missing.
     * 
     * @param nodeRef   the node reference
     * @throws AspectMissingException
     *                  thrown if the lock aspect is missing
     */
    private void checkForLockApsect(NodeRef nodeRef)
        throws AspectMissingException
    {
        // Get the class ref for the lock aspect
        ClassRef lockAspect = new ClassRef(ASPECT_QNAME_LOCK);
        
        if (this.nodeService.hasAspect(nodeRef, lockAspect) == false)
        {
            throw new AspectMissingException(lockAspect, nodeRef);
        }
    }
    
    /**
     * 
     * @author Roy Wetherall
     */
    public class OnBeforeCreateVersionPolicyImpl implements OnBeforeCreateVersionPolicy
    {
        /**
         * 
         */
        public void OnBeforeCreateVersion(NodeRef versionableNode)
        {
            System.out.println("Checking the lock here ...");
        }
    }

}
