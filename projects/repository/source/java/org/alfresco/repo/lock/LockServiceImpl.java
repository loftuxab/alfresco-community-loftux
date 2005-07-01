/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.lock;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.acegisecurity.Authentication;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.repo.security.authentication.RepositoryUser;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.lock.NodeLockedException;
import org.alfresco.service.cmr.lock.UnableToAquireLockException;
import org.alfresco.service.cmr.lock.UnableToReleaseLockException;
import org.alfresco.service.cmr.repository.AspectMissingException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

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
     * The authentication service
     */
    private AuthenticationService authenticationService;
    
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
     * Sets the authentication service
     * 
     * @param authenticationService  the authentication service
     */
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
    
    /**
     * Initialise methods called by Spring framework
     */
    public void initialise()
    {
        // Register the various class behaviours to enable lock checking
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "beforeCreateVersion"),
				ContentModel.ASPECT_LOCKABLE,
				new JavaBehaviour(this, "checkForLock"));	
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "beforeUpdateNode"),
				ContentModel.ASPECT_LOCKABLE,
				new JavaBehaviour(this, "checkForLock"));
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "beforeDeleteNode"),
				ContentModel.ASPECT_LOCKABLE,
				new JavaBehaviour(this, "checkForLock"));
		
		// Register onCopy class behaviour
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCopyNode"),
				ContentModel.ASPECT_LOCKABLE,
				new JavaBehaviour(this, "onCopy"));
		
		// Register the onCreateVersion behavior for the version aspect
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateVersion"),
				ContentModel.ASPECT_LOCKABLE,
				new JavaBehaviour(this, "onCreateVersion"));
    }
    
    /**
     * @see org.alfresco.service.cmr.lock.LockService#lock(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, LockType)
     */
    public synchronized void lock(NodeRef nodeRef, NodeRef userRef, LockType lockType)
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
	            this.nodeService.setProperty(nodeRef, ContentModel.PROP_LOCK_OWNER, userRef);
	            this.nodeService.setProperty(nodeRef, ContentModel.PROP_LOCK_TYPE, lockType.toString());
			}
			finally
			{
				this.ignoreNodeRefs.remove(nodeRef);
			}
        }        
    }

    /**
     * @see org.alfresco.service.cmr.lock.LockService#lock(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, LockType, boolean)
     */
    public synchronized void lock(NodeRef nodeRef, NodeRef userRef, LockType lockType, boolean lockChildren)
        throws UnableToAquireLockException, AspectMissingException
    {
        lock(nodeRef, userRef, LockType.WRITE_LOCK);
        
        if (lockChildren == true)
        {
            Collection<ChildAssociationRef> childAssocRefs = this.nodeService.getChildAssocs(nodeRef);
            for (ChildAssociationRef childAssocRef : childAssocRefs)
            {
                lock(childAssocRef.getChildRef(), userRef, lockType, lockChildren);
            }
        }       
    }

    /**
     * @see org.alfresco.service.cmr.lock.LockService#lock(java.util.Collection, java.lang.String, LockType)
     */
    public synchronized void lock(Collection<NodeRef> nodeRefs, NodeRef userRef, LockType lockType)
        throws UnableToAquireLockException, AspectMissingException
    {        
        // Lock each of the specifed nodes
        for (NodeRef nodeRef : nodeRefs)
        {
            lock(nodeRef, userRef, LockType.WRITE_LOCK);
        }        
    }

    /**
     * @see org.alfresco.service.cmr.lock.LockService#unlock(NodeRef, String)
     */
    public synchronized void unlock(NodeRef nodeRef, NodeRef userRef)
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
	            this.nodeService.setProperty(nodeRef, ContentModel.PROP_LOCK_OWNER, null);
	            this.nodeService.setProperty(nodeRef, ContentModel.PROP_LOCK_TYPE, null);
			}
			finally
			{
				this.ignoreNodeRefs.remove(nodeRef);
			}
        }      
    }

    /**
     * @see org.alfresco.service.cmr.lock.LockService#unlock(NodeRef, String, boolean)
     */
    public synchronized void unlock(NodeRef nodeRef, NodeRef userRef, boolean unlockChildren)
        throws UnableToReleaseLockException, AspectMissingException
    {
        // Unlock the parent
        unlock(nodeRef, userRef);
        
        if (unlockChildren == true)
        {
            // Get the children and unlock them
            Collection<ChildAssociationRef> childAssocRefs = this.nodeService.getChildAssocs(nodeRef);
            for (ChildAssociationRef childAssocRef : childAssocRefs)
            {
                unlock(childAssocRef.getChildRef(), userRef, unlockChildren);
            }
        }        
    }

    /**
     * @see org.alfresco.repo.lock.LockService#unlock(Collection<NodeRef>, String)
     */
    public synchronized void unlock(Collection<NodeRef> nodeRefs, NodeRef userRef)
        throws UnableToReleaseLockException, AspectMissingException
    {        
        for (NodeRef nodeRef : nodeRefs)
        {
            unlock(nodeRef, userRef);
        }         
    }
    
    /**
     * @see org.alfresco.service.cmr.lock.LockService#getLockStatus(NodeRef)
     */
    public LockStatus getLockStatus(NodeRef nodeRef) throws AspectMissingException
    {
        return getLockStatus(nodeRef, getUserRef());
    }
    
    /**
     * @see org.alfresco.service.cmr.lock.LockService#getLockStatus(NodeRef, String)
     */
    public LockStatus getLockStatus(NodeRef nodeRef, NodeRef userRef)
        throws AspectMissingException
    {
        // Check for lock aspect
        checkForLockApsect(nodeRef);
        
        LockStatus result = LockStatus.NO_LOCK;
        
        // Get the current lock owner
        NodeRef currentUserRef = (NodeRef)this.nodeService.getProperty(nodeRef, ContentModel.PROP_LOCK_OWNER);
        if (currentUserRef != null)
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
        LockType result = null;
        
        // Check for the lock aspect
        checkForLockApsect(nodeRef);
        
        String lockTypeString = (String)this.nodeService.getProperty(nodeRef, ContentModel.PROP_LOCK_TYPE);
        if (lockTypeString != null)
        {
            result = LockType.valueOf(lockTypeString);
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
        if (this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_LOCKABLE) == false)
        {
            throw new AspectMissingException(ContentModel.ASPECT_LOCKABLE, nodeRef);
        }
    }
	
	/**
     * @see LockService#checkForLock(NodeRef)
     */
    public void checkForLock(NodeRef nodeRef)
		throws NodeLockedException
    {
		// Check the lock
        checkForLockWithUser(nodeRef, getUserRef());
    }
    
    /**
     * @see LockService#checkForLockWithUser(NodeRef, String)
     */
    public void checkForLockWithUser(NodeRef nodeRef, NodeRef userRef)
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
	 * @see org.alfresco.repo.copy.CopyServicePolicies.OnCopyNodePolicy#onCopyNode(QName, NodeRef, StoreRef, boolean, PolicyScope)
	 */
	public void onCopy(
            QName sourceClassRef, 
            NodeRef sourceNodeRef, 
            StoreRef destinationStoreRef,
            boolean copyToNewNode,
            PolicyScope copyDetails)
	{
		// Add the lock aspect, but do not copy any of the properties
		copyDetails.addAspect(ContentModel.ASPECT_LOCKABLE);
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
		nodeDetails.addAspect(ContentModel.ASPECT_LOCKABLE);
	}
    
    /**
     * Get the current user reference
     * 
     * @return  the current user reference
     */
    private NodeRef getUserRef()
    {
        NodeRef userRef = null;
        Authentication auth = this.authenticationService.getCurrentAuthentication();
        if (auth != null)
        {
            RepositoryUser user = (RepositoryUser)auth.getPrincipal();
            if (user != null)
            {
                userRef = user.getUserNodeRef();
            }
        }
        return userRef;
    }
}
