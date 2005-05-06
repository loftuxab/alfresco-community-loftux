/**
 * Created on Apr 14, 2005
 */
package org.alfresco.repo.lock.simple;

import java.util.Collection;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.lock.LockService;
import org.alfresco.repo.lock.UnableToAquireLockException;
import org.alfresco.repo.lock.UnableToReleaseLockException;
import org.alfresco.repo.lock.LockService.LockType;
import org.alfresco.repo.lock.common.VersionServicePolicyImpl;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.policy.PolicyRuntimeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.version.policy.OnBeforeCreateVersionPolicy;
import org.alfresco.util.AspectMissingException;

/**
 * Simple Lock service implementation
 * 
 * @author Roy Wetherall
 */
public class SimpleLockService implements LockService
{
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
                new VersionServicePolicyImpl(this),
                LockService.ASPECT_QNAME_LOCK);
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
            // Set the current user as the lock owner
            this.nodeService.setProperty(nodeRef, PROP_QNAME_LOCK_OWNER, userRef);
            this.nodeService.setProperty(nodeRef, PROP_QNAME_LOCK_TYPE, lockType);
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
            // Clear the lock owner
            this.nodeService.setProperty(nodeRef, PROP_QNAME_LOCK_OWNER, null);
            this.nodeService.setProperty(nodeRef, PROP_QNAME_LOCK_TYPE, null);
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
     * @see LockService#getLockType(NodeRef)
     */
    public LockType getLockType(NodeRef nodeRef)
        throws AspectMissingException
    {
        // Check for the lock aspect
        checkForLockApsect(nodeRef);
        
        // Return the lock type enum
        return (LockType)this.nodeService.getProperty(nodeRef, PROP_QNAME_LOCK_TYPE);        
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
}
