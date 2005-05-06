/**
 * Created on Apr 29, 2005
 */
package org.alfresco.repo.lock.common;

import org.alfresco.repo.lock.LockService;
import org.alfresco.repo.lock.NodeLockedException;
import org.alfresco.repo.lock.LockService.LockStatus;
import org.alfresco.repo.lock.LockService.LockType;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.util.AspectMissingException;

/**
 * Abstract class used as a base for lock policy implementations
 * 
 * @author Roy Wetherall
 */
public abstract class AbstractPolicyImpl
{
    /**
     * The lock service
     */
    private LockService lockService = null;
    
    /**
     * Constructor
     */
    public AbstractPolicyImpl(LockService lockService)
    {
        this.lockService = lockService;
    }
    
    /**
     * Checks to see if the node is locked or not.  Gets the user reference from the current 
     * session.
     * <p>
     * Throws a NodeLockedException based on the lock status of the lock, the user ref and the
     * lock type.
     * 
     * @param nodeRef   the node reference
     * @throws NodeLockedException
     *                  thrown if the node is determined to be locked based on the user ref and lock 
     *                  type
     */
    protected void checkForLock(NodeRef nodeRef)
    {
        // TODO get the current user reference
        String userRef = "tempUser";
        
        // Check the lock
        checkForLock(nodeRef, userRef);
    }
    
    /**
     * Checks to see if the node is locked or not.
     * <p>
     * Throws a NodeLockedException based on the lock status of the lock, the user ref and the
     * lock type.
     * 
     * @param nodeRef   the node reference
     * @param userRef   the user reference
     * @throws NodeLockedException
     *                  thrown if the node is determined to be locked based on the user ref and lock 
     *                  type
     */
    protected void checkForLock(NodeRef nodeRef, String userRef)
        throws NodeLockedException
    {        
        // Ensure we have found a node reference
        if (nodeRef != null && userRef != null)
        {
            try
            {
                LockType lockType = this.lockService.getLockType(nodeRef);
                if (LockType.WRITE_LOCK.equals(lockType) == true)
                {
                    // Get the current lock status on the node ref
                    LockStatus currentLockStatus = this.lockService.getLockStatus(nodeRef, userRef);
                    
                    if (LockStatus.LOCKED.equals(currentLockStatus) == true)
                    {
                        // Error since we are trying to preform an operation on a locked
                        // node
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
