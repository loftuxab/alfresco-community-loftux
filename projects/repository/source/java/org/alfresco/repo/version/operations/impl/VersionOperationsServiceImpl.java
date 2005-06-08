/**
 * Created on May 13, 2005
 */
package org.alfresco.repo.version.operations.impl;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.lock.LockService;
import org.alfresco.repo.lock.LockType;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.node.operations.NodeOperationsService;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.version.VersionService;
import org.alfresco.repo.version.operations.VersionOperationsService;
import org.alfresco.repo.version.operations.VersionOperationsServiceException;
import org.alfresco.util.AspectMissingException;

/**
 * Version opertaions service implementation
 * 
 * @author Roy Wetherall
 */
public class VersionOperationsServiceImpl implements VersionOperationsService 
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
	 * The node operations service
	 */
	private NodeOperationsService nodeOperationsService;
	
	/**
	 * Policy component
	 */
	private PolicyComponent policyComponent;
	
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
	 * Sets the node operations service
	 * 
	 * @param nodeOperationsService  the node operations service
	 */
	public void setNodeOperationsService(
			NodeOperationsService nodeOperationsService) 
	{
		this.nodeOperationsService = nodeOperationsService;
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
	 * Initialise method
	 */
	public void init()
	{
		// Register copy behaviour for the working copy aspect
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCopyNode"),
				DictionaryBootstrap.ASPECT_QNAME_WORKING_COPY,
				new JavaBehaviour(this, "onCopy"));
	}
	
	/**
	 * onCopy policy behaviour
	 * 
	 * @see org.alfresco.repo.node.operations.NodeOperationsServicePolicies.OnCopyPolicy#onCopy(ClassRef, NodeRef, PolicyScope)
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
	 * @see org.alfresco.repo.version.operations.VersionOperationsService#checkout(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.QName, org.alfresco.repo.ref.QName)
	 */
	public NodeRef checkout(
			NodeRef nodeRef, 
			NodeRef destinationParentNodeRef,
			QName destinationAssocTypeQName, 
			QName destinationAssocQName) 
	{
		// TODO should this be done here ??
		
		// Apply the lock aspect if required
		if (this.nodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_LOCKABLE) == false)
		{
			this.nodeService.addAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_LOCKABLE, null);
		}
		
		// Apply the version aspect if required
		if (this.nodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE) == false)
		{
			this.nodeService.addAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE, null);
		}
		
		// Make the working copy
		NodeRef workingCopy = this.nodeOperationsService.copy(
				nodeRef,
				destinationParentNodeRef,
				destinationAssocTypeQName,
				destinationAssocQName);
		
		// Apply the working copy aspect to the working copy
		this.nodeService.addAspect(workingCopy, DictionaryBootstrap.ASPECT_QNAME_WORKING_COPY, null);
		
		// Get the current user reference to use as the lock owner
		// TODO Need to be able to get the current user reference.
		
		// Lock the origional node
		this.lockService.lock(nodeRef, LockService.LOCK_USER, LockType.READ_ONLY_LOCK);
		
		// Return the working copy
		return workingCopy;
	}

	/**
	 * @see org.alfresco.repo.version.operations.VersionOperationsService#checkout(org.alfresco.repo.ref.NodeRef)
	 */
	public NodeRef checkout(NodeRef nodeRef) 
	{
		// Find the primary parent in order to determine where to put the copy
		ChildAssocRef childAssocRef = this.nodeService.getPrimaryParent(nodeRef);
		
		//TODO The destination assoc type qname should come from the child assoc ref.
		return checkout(nodeRef, childAssocRef.getParentRef(), null, childAssocRef.getQName());		
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
		if (this.nodeService.hasAspect(workingCopyNodeRef, DictionaryBootstrap.ASPECT_QNAME_WORKING_COPY) == false)
		{
			// Error since we have not been passed a working copy
			throw new AspectMissingException(DictionaryBootstrap.ASPECT_QNAME_WORKING_COPY, workingCopyNodeRef);
		}
		
		if (contentUrl != null)
		{
			// Set the content url value onto the working copy
			this.nodeService.setProperty(
					workingCopyNodeRef, 
					DictionaryBootstrap.PROP_QNAME_CONTENT_URL, 
					contentUrl);
		}
		
		// Check that the working node still has the copy aspect applied
		if (this.nodeService.hasAspect(workingCopyNodeRef, DictionaryBootstrap.ASPECT_QNAME_COPIEDFROM) == true)
		{
			// Try and get the origional node reference
			nodeRef = (NodeRef)this.nodeService.getProperty(workingCopyNodeRef, DictionaryBootstrap.PROP_QNAME_COPY_REFERENCE);
			if(nodeRef == null)
			{
				// Error since the origional node can not be found
				throw new VersionOperationsServiceException(ERR_BAD_COPY);							
			}
			
            // Release the lock
			this.lockService.unlock(nodeRef, LockService.LOCK_USER);
			
			// Copy the contents of the working copy onto the origional
			this.nodeOperationsService.copy(workingCopyNodeRef, nodeRef);
			
			if (versionProperties != null)
			{
				// Create the new version
				this.versionService.createVersion(nodeRef, versionProperties);
			}
			
			if (keepCheckedOut == false)
			{
				// Delete the working copy
				this.nodeService.deleteNode(workingCopyNodeRef);							
			}
			else
			{
				// Re-lock the origional node
				this.lockService.lock(nodeRef, LockService.LOCK_USER, LockType.READ_ONLY_LOCK);
			}
			
		}
		else
		{
			// Error since the copy aspect is missing
			throw new AspectMissingException(DictionaryBootstrap.ASPECT_QNAME_COPIEDFROM, workingCopyNodeRef);
		}
		
		return nodeRef;
	}

	/**
	 * @see org.alfresco.repo.version.operations.VersionOperationsService#checkin(org.alfresco.repo.ref.NodeRef, Map, java.lang.String)
	 */
	public NodeRef checkin(
			NodeRef workingCopyNodeRef,
			Map<String, Serializable> versionProperties, 
			String contentUrl) 
	{
		return checkin(workingCopyNodeRef, versionProperties, contentUrl, false);
	}

	/**
	 * @see org.alfresco.repo.version.operations.VersionOperationsService#checkin(org.alfresco.repo.ref.NodeRef, Map)
	 */
	public NodeRef checkin(
			NodeRef workingCopyNodeRef,
			Map<String, Serializable> versionProperties) 
	{
		return checkin(workingCopyNodeRef, versionProperties, null, false);
	}

	/**
	 * @see org.alfresco.repo.version.operations.VersionOperationsService#cancelCheckout(org.alfresco.repo.ref.NodeRef)
	 */
	public NodeRef cancelCheckout(NodeRef workingCopyNodeRef) 
	{
		NodeRef nodeRef = null;
		
		// Check that we have been handed a working copy
		if (this.nodeService.hasAspect(workingCopyNodeRef, DictionaryBootstrap.ASPECT_QNAME_WORKING_COPY) == false)
		{
			// Error since we have not been passed a working copy
			throw new AspectMissingException(DictionaryBootstrap.ASPECT_QNAME_WORKING_COPY, workingCopyNodeRef);
		}
		
		// Ensure that the node has the copy aspect
		if (this.nodeService.hasAspect(workingCopyNodeRef, DictionaryBootstrap.ASPECT_QNAME_COPIEDFROM) == true)
		{
			// Get the origional node
			nodeRef = (NodeRef)this.nodeService.getProperty(workingCopyNodeRef, DictionaryBootstrap.PROP_QNAME_COPY_REFERENCE);
			if (nodeRef == null)
			{
				// Error since the origional node can not be found
				throw new VersionOperationsServiceException(ERR_BAD_COPY);
			}
			
			// TODO Need to get the current user reference
			
			// Release the lock on the origional node
			this.lockService.unlock(nodeRef, LockService.LOCK_USER);
			
			// Delete the working copy
			this.nodeService.deleteNode(workingCopyNodeRef);
		}
		else
		{
			// Error since the copy aspect is missing
			throw new AspectMissingException(DictionaryBootstrap.ASPECT_QNAME_COPIEDFROM, workingCopyNodeRef);
		}
		
		return nodeRef;
	}
}
