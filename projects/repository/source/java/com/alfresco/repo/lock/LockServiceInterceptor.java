/**
 * Created on Apr 19, 2005
 */
package org.alfresco.repo.lock;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.alfresco.repo.lock.LockService.LockStatus;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.util.AspectMissingException;

/**
 * Lock service interceptor used to call the lock service and determine
 * whether the process can proceed based on the state of the lock associatied
 * with the node reference.
 * 
 * @author Roy Wetherall
 */
class LockServiceInterceptor implements MethodInterceptor
{
    /**
     * The lock service
     */
    private LockService lockService = null;
    
    /**
     * Sets the lock service
     * 
     * @param  lockService  the lock service
     */
    public void setLockService(LockService lockService)
    {
        this.lockService = lockService;
    }

    /**
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation arg0) throws Throwable
    {
        // Get the method name
        String methodName = arg0.getMethod().getName();
        
        // TODO need to get the user from somewhere ...
        String userRef = "userRef";
        
        // TODO this will not work if there are more that one node ref's in the
        //      method unless the first is the significant one!!
        
        // Get the node reference
        NodeRef nodeRef = null;
        for (Object argumentValue : arg0.getArguments())
        {
            if (argumentValue instanceof NodeRef)
            {
                nodeRef = (NodeRef)argumentValue;
                break;
            }
        }
        
        // Ensure we have found a node reference
        // TODO should throw an exception here?
        if (nodeRef != null && userRef != null)
        {
            try
            {
                // Get the current lock status on the node ref
                LockStatus currentLockStatus = this.lockService.getLockStatus(nodeRef, userRef);
                
                // TODO change logic if ensure locked flag set to true
                
                if (LockStatus.LOCKED.equals(currentLockStatus) == true)
                {
                    // Error since we are trying to preform an operation on a locked
                    // node
                    throw new NodeLockedException(nodeRef);
                }
            }
            catch (AspectMissingException exception)
            {
                // Ignore since this indicates that the node does not have the lock  
                // aspect applied
            }
        }
        
        return arg0.proceed(); 
    }

}
