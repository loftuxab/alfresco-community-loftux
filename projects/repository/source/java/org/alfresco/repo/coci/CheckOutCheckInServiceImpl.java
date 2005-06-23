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
package org.alfresco.repo.coci;

import java.io.Serializable;
import java.util.Map;

import net.sf.acegisecurity.Authentication;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.repo.security.authentication.RepositoryUser;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.coci.CheckOutCheckInServiceException;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.lock.UnableToReleaseLockException;
import org.alfresco.service.cmr.repository.AspectMissingException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Version opertaions service implementation
 * 
 * @author Roy Wetherall
 */
public class CheckOutCheckInServiceImpl implements CheckOutCheckInService 
{
	/**
	 * Error messages
	 */
	private static final String ERR_BAD_COPY = "The original node can not be found.  Perhaps the copy has " +
											   "been corrupted or the origional has been deleted or moved.";
	private static final String ERR_NOT_WORKING_COPY = "The node provided is not a working copy.";
	
	/**
	 * The node service
	 */
	private NodeService nodeService;
	
	/**
	 * The version service
	 */
	private VersionService versionService;
	
	/**
	 * The lock service
	 */
	private LockService lockService;
	
	/**
	 * The copy service
	 */
	private CopyService copyService;
	
	/**
	 * Policy component
	 */
	private PolicyComponent policyComponent;
    
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
	 * Set the version service
	 * 
	 * @param versionService  the version service
	 */
	public void setVersionService(VersionService versionService) 
	{
		this.versionService = versionService;
	}
	
	/**
	 * Sets the lock service
	 * 
	 * @param lockService  the lock service
	 */
	public void setLockService(LockService lockService) 
	{
		this.lockService = lockService;
	}
	
	/**
     * Sets the copy service
     *  
     * @param copyService  the copy service
	 */
	public void setCopyService(
			CopyService copyService) 
	{
		this.copyService = copyService;
	}
	
	/**
	 * Sets the policy component
	 * 
	 * @param policyComponent  the policy component
	 */
	public void setPolicyComponent(PolicyComponent policyComponent) 
	{
		this.policyComponent = policyComponent;
	}
    
    /**
     * Sets the authenticatin service
     * 
     * @param authenticationService  the authentication service
     */
    public void setAuthenticationService(
            AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
	
	/**
	 * Initialise method
	 */
	public void init()
	{
		// Register copy behaviour for the working copy aspect
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCopyNode"),
				ContentModel.ASPECT_WORKING_COPY,
				new JavaBehaviour(this, "onCopy"));
	}
	
	/**
	 * onCopy policy behaviour
	 * 
	 * @see org.alfresco.repo.node.operations.CopyServicePolicies.OnCopyPolicy#onCopy(ClassRef, NodeRef, PolicyScope)
	 * 
	 * @param sourceClassRef  the source class reference
	 * @param sourceNodeRef	  the source node reference
	 * @param copyDetails	  the copy details
	 */
	public void onCopy(QName sourceClassRef, NodeRef sourceNodeRef, PolicyScope copyDetails)
	{
		// Do nothing to ensure that the working copy aspect does not appear on the copy
	}
	
	/**
	 * @see org.alfresco.service.cmr.coci.CheckOutCheckInService#checkout(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, org.alfresco.service.namespace.QName)
	 */
	public NodeRef checkout(
			NodeRef nodeRef, 
			NodeRef destinationParentNodeRef,
			QName destinationAssocTypeQName, 
			QName destinationAssocQName) 
	{
		// Make sure we are no checking out a working copy node
		if (this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY) == true)
		{
			throw new CheckOutCheckInServiceException("A working copy can not be checked out.");
		}
		
		// Apply the lock aspect if required
		if (this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_LOCKABLE) == false)
		{
			this.nodeService.addAspect(nodeRef, ContentModel.ASPECT_LOCKABLE, null);
		}
		
		// Make the working copy
		NodeRef workingCopy = this.copyService.copy(
				nodeRef,
				destinationParentNodeRef,
				destinationAssocTypeQName,
				destinationAssocQName);
		
		// Apply the working copy aspect to the working copy
		this.nodeService.addAspect(workingCopy, ContentModel.ASPECT_WORKING_COPY, null);
		
		// Lock the origional node
		this.lockService.lock(nodeRef, getUserNodeRef(), LockType.READ_ONLY_LOCK);
		
		// Return the working copy
		return workingCopy;
	}
    
    /**
     * Gets the authenticated users node reference
     * 
     * @return  the users node reference
     */
    private NodeRef getUserNodeRef()
    {
        NodeRef result = null;
        Authentication auth = this.authenticationService.getCurrentAuthentication();
        if (auth != null)
        {
            RepositoryUser user = (RepositoryUser)auth.getPrincipal();
            if (user != null)
            {
                result = user.getUserNodeRef();
            }
        }
        
        if (result == null)
        {
            throw new CheckOutCheckInServiceException("Can not find the currently authenticated user details required by the CheckOutCheckIn service.");
        }
        
        return result;
    }

	/**
	 * @see org.alfresco.service.cmr.coci.CheckOutCheckInService#checkout(org.alfresco.service.cmr.repository.NodeRef)
	 */
	public NodeRef checkout(NodeRef nodeRef) 
	{
		// Find the primary parent in order to determine where to put the copy
		ChildAssociationRef childAssocRef = this.nodeService.getPrimaryParent(nodeRef);
		
		// Checkout the working copy to the same destination
		return checkout(nodeRef, childAssocRef.getParentRef(), childAssocRef.getTypeQName(), childAssocRef.getQName());		
	}

	/**
	 * @see org.alfresco.repo.version.operations.VersionOperationsService#checkin(org.alfresco.repo.ref.NodeRef, Map<String,Serializable>, java.lang.String, boolean)
	 */
	public NodeRef checkin(
			NodeRef workingCopyNodeRef,
			Map<String,Serializable> versionProperties, 
			String contentUrl,
			boolean keepCheckedOut) 
	{
		NodeRef nodeRef = null;
		
		// Check that we have been handed a working copy
		if (this.nodeService.hasAspect(workingCopyNodeRef, ContentModel.ASPECT_WORKING_COPY) == false)
		{
			// Error since we have not been passed a working copy
			throw new AspectMissingException(ContentModel.ASPECT_WORKING_COPY, workingCopyNodeRef);
		}
		
		// Check that the working node still has the copy aspect applied
		if (this.nodeService.hasAspect(workingCopyNodeRef, ContentModel.ASPECT_COPIEDFROM) == true)
		{
			// Try and get the origional node reference
			nodeRef = (NodeRef)this.nodeService.getProperty(workingCopyNodeRef, ContentModel.PROP_COPY_REFERENCE);
			if(nodeRef == null)
			{
				// Error since the origional node can not be found
				throw new CheckOutCheckInServiceException(ERR_BAD_COPY);							
			}
			
			try
			{
				// Release the lock
				this.lockService.unlock(nodeRef, getUserNodeRef());
			}
			catch (UnableToReleaseLockException exception)
			{
				throw new CheckOutCheckInServiceException("This user is not the owner of the working copy and can not check it in.", exception);
			}
			
			if (versionProperties != null && this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE) == true)
			{
				// Create the new version
				this.versionService.createVersion(nodeRef, versionProperties);
			}
			
			if (contentUrl != null)
			{
				// Set the content url value onto the working copy
				this.nodeService.setProperty(
						workingCopyNodeRef, 
						ContentModel.PROP_CONTENT_URL, 
						contentUrl);
			}
			
			// Copy the contents of the working copy onto the origional
			this.copyService.copy(workingCopyNodeRef, nodeRef);
			
			if (keepCheckedOut == false)
			{
				// Delete the working copy
				this.nodeService.deleteNode(workingCopyNodeRef);							
			}
			else
			{
				// Re-lock the origional node
				this.lockService.lock(nodeRef, getUserNodeRef(), LockType.READ_ONLY_LOCK);
			}
			
		}
		else
		{
			// Error since the copy aspect is missing
			throw new AspectMissingException(ContentModel.ASPECT_COPIEDFROM, workingCopyNodeRef);
		}
		
		return nodeRef;
	}

	/**
	 * @see org.alfresco.service.cmr.coci.CheckOutCheckInService#checkin(org.alfresco.service.cmr.repository.NodeRef, Map, java.lang.String)
	 */
	public NodeRef checkin(
			NodeRef workingCopyNodeRef,
			Map<String, Serializable> versionProperties, 
			String contentUrl) 
	{
		return checkin(workingCopyNodeRef, versionProperties, contentUrl, false);
	}

	/**
	 * @see org.alfresco.service.cmr.coci.CheckOutCheckInService#checkin(org.alfresco.service.cmr.repository.NodeRef, Map)
	 */
	public NodeRef checkin(
			NodeRef workingCopyNodeRef,
			Map<String, Serializable> versionProperties) 
	{
		return checkin(workingCopyNodeRef, versionProperties, null, false);
	}

	/**
	 * @see org.alfresco.service.cmr.coci.CheckOutCheckInService#cancelCheckout(org.alfresco.service.cmr.repository.NodeRef)
	 */
	public NodeRef cancelCheckout(NodeRef workingCopyNodeRef) 
	{
		NodeRef nodeRef = null;
		
		// Check that we have been handed a working copy
		if (this.nodeService.hasAspect(workingCopyNodeRef, ContentModel.ASPECT_WORKING_COPY) == false)
		{
			// Error since we have not been passed a working copy
			throw new AspectMissingException(ContentModel.ASPECT_WORKING_COPY, workingCopyNodeRef);
		}
		
		// Ensure that the node has the copy aspect
		if (this.nodeService.hasAspect(workingCopyNodeRef, ContentModel.ASPECT_COPIEDFROM) == true)
		{
			// Get the origional node
			nodeRef = (NodeRef)this.nodeService.getProperty(workingCopyNodeRef, ContentModel.PROP_COPY_REFERENCE);
			if (nodeRef == null)
			{
				// Error since the origional node can not be found
				throw new CheckOutCheckInServiceException(ERR_BAD_COPY);
			}			
			
			// Release the lock on the origional node
			this.lockService.unlock(nodeRef, getUserNodeRef());
			
			// Delete the working copy
			this.nodeService.deleteNode(workingCopyNodeRef);
		}
		else
		{
			// Error since the copy aspect is missing
			throw new AspectMissingException(ContentModel.ASPECT_COPIEDFROM, workingCopyNodeRef);
		}
		
		return nodeRef;
	}
}
