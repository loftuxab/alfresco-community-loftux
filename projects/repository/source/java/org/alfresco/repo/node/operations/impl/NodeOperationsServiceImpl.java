/**
 * Created on May 5, 2005
 */
package org.alfresco.repo.node.operations.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.node.operations.NodeOperationsService;
import org.alfresco.repo.node.operations.NodeOperationsServiceException;
import org.alfresco.repo.node.operations.NodeOperationsServicePolicies;
import org.alfresco.repo.node.operations.NodeOperationsServicePolicies.OnCopyPolicy;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;

/**
 * Node operations service implmentation.
 * 
 * @author Roy Wetherall
 */
public class NodeOperationsServiceImpl implements NodeOperationsService
{
    /**
     * The node service
     */
    private NodeService nodeService;
	
	/**
	 * The dictionary service
	 */
	private DictionaryService dictionaryService; 	
	
	/**
	 * Policy component
	 */
	private PolicyComponent policyComponent;

	/**
	 * Policy delegates
	 */
	private ClassPolicyDelegate<OnCopyPolicy> onCopyDelegate;
    
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
	 * Sets the dictionary service
	 * 
	 * @param dictionaryService  the dictionary service
	 */
	public void setDictionaryService(DictionaryService dictionaryService) 
	{
		this.dictionaryService = dictionaryService;
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
		// Register the policies
		this.onCopyDelegate = this.policyComponent.registerClassPolicy(NodeOperationsServicePolicies.OnCopyPolicy.class);
		
		// Register policy behaviours
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCopy"),
				DictionaryBootstrap.ASPECT_QNAME_COPY,
				new JavaBehaviour(this, "copyAspectOnCopy"));
	}
	
    /**
     * @see com.activiti.repo.node.copy.NodeCopyService#copy(com.activiti.repo.ref.NodeRef, com.activiti.repo.ref.NodeRef, com.activiti.repo.ref.QName, QName, boolean)
     */
    public NodeRef copy(
            NodeRef sourceNodeRef,
            NodeRef destinationParent, 
            QName destinationAssocTypeQName,
            QName destinationQName, 
            boolean copyChildren)
    {
		NodeRef destinationNodeRef = null;
        
        // Check that all the passed values are not null
        if (sourceNodeRef != null && 
            destinationParent != null && 
			destinationQName != null)
        {       
			QName sourceClassRef = this.nodeService.getType(sourceNodeRef);
			PolicyScope copyDetails = getCopyDetails(sourceNodeRef);			
			
            if (sourceNodeRef.getStoreRef().equals(destinationParent.getStoreRef()) == true)
            {
				// Create the new node
                ChildAssocRef destinationChildAssocRef = this.nodeService.createNode(
                        destinationParent, 
                        destinationAssocTypeQName,
                        destinationQName,
                        sourceClassRef,
                        copyDetails.getProperties());
                destinationNodeRef = destinationChildAssocRef.getChildRef();
				
				//	Apply the copy aspect to the new node	
				Map<QName, Serializable> copyProperties = new HashMap<QName, Serializable>();
				copyProperties.put(DictionaryBootstrap.PROP_QNAME_COPY_REFERENCE, sourceNodeRef);
				this.nodeService.addAspect(destinationNodeRef, DictionaryBootstrap.ASPECT_QNAME_COPY, copyProperties);
            }
            else
            {
				// TODO We need to create a new node in the other store with the same id as the source

                // Error - since at the moment we do not support cross store copying
				throw new UnsupportedOperationException("Copying nodes across stores is not currently supported.");
            }
			
			// Copy the aspects 
			copyAspects(destinationNodeRef, copyDetails);
			
			// Copy the associations
			copyAssociations(destinationNodeRef, copyDetails, copyChildren);
        }
        
        return destinationNodeRef;
    }
	
	/**
	 * Gets the copy details.  This calls the appropriate policies that have been registered
	 * against the node and aspect types in order to pick-up any type specific copy behaviour.
	 * <p>
	 * If no policies for a type are registered then the default copy takes place which will 
	 * copy all properties and associations in the ususal manner.
	 * 
	 * @param sourceNodeRef		the source node reference
	 * @return					the copy details
	 */
	private PolicyScope getCopyDetails(NodeRef sourceNodeRef)
	{
		QName sourceClassRef = this.nodeService.getType(sourceNodeRef);		
		PolicyScope copyDetails = new PolicyScope(sourceClassRef);
		
		// Invoke the onCopy behaviour
		invokeOnCopy(sourceClassRef, sourceNodeRef, copyDetails);
		
		// TODO What do we do aboout props and assocs that are on the node node but not part of the type definition?
		
		// Get the source aspects
		Set<QName> sourceAspects = this.nodeService.getAspects(sourceNodeRef);
		for (QName sourceAspect : sourceAspects) 
		{
			// Invoke the onCopy behaviour
			invokeOnCopy(sourceAspect, sourceNodeRef, copyDetails);
		}
		
		return copyDetails;
	}
	
	/**
	 * Invoke the correct onCopy behaviour
	 * 
	 * @param sourceClassRef	source class reference
	 * @param sourceNodeRef		source node reference
	 * @param copyDetails		the copy details
	 */
	private void invokeOnCopy(QName sourceClassRef, NodeRef sourceNodeRef, PolicyScope copyDetails)
	{
		Collection<NodeOperationsServicePolicies.OnCopyPolicy> policies = this.onCopyDelegate.getList(sourceClassRef);
		if (policies.isEmpty() == true)
		{
			defaultOnCopy(sourceClassRef, sourceNodeRef, copyDetails);
		}
		else
		{
			for (NodeOperationsServicePolicies.OnCopyPolicy policy : policies) 
			{
				policy.onCopy(sourceClassRef, sourceNodeRef, copyDetails);
			}
		}
	}
	
	/**
	 * Default implementation of on copy, used when there is no policy specified for a class.
	 * 
	 * @param classRef			the class reference of the node being copied
	 * @param sourceNodeRef		the source node reference
	 * @param copyDetails		details of the state being copied
	 */
    private void defaultOnCopy(QName classRef, NodeRef sourceNodeRef, PolicyScope copyDetails) 
	{
		ClassDefinition classDefinition = this.dictionaryService.getClass(classRef);	
		if (classDefinition != null)
		{			
			// Copy the properties
			Map<QName,PropertyDefinition> propertyDefinitions = classDefinition.getProperties();
			for (QName propertyName : propertyDefinitions.keySet()) 
			{
				Serializable propValue = this.nodeService.getProperty(sourceNodeRef, propertyName);
				copyDetails.addProperty(classRef, propertyName, propValue);
			}			
			
			// Copy the associations (child and target)
			Map<QName,AssociationDefinition> assocDefs = classDefinition.getAssociations();
			for (AssociationDefinition assocDef : assocDefs.values()) 
			{
				if (assocDef.isChild() == true)
				{
					List<ChildAssocRef> childAssocRefs = this.nodeService.getChildAssocs(sourceNodeRef, assocDef.getName());
					for (ChildAssocRef childAssocRef : childAssocRefs) 
					{
						copyDetails.addChildAssociation(classRef, assocDef.getName(), childAssocRef);
					}
				}
				else
				{
					List<NodeAssocRef> nodeAssocRefs = this.nodeService.getTargetAssocs(sourceNodeRef, assocDef.getName());
					for (NodeAssocRef nodeAssocRef : nodeAssocRefs) 
					{
						copyDetails.addAssociation(classRef, assocDef.getName(), nodeAssocRef);
					}
				}
			}
		}
	}
	
	/**
	 * Copies the properties for the node type onto the destination node.
	 * 	
	 * @param destinationNodeRef	the destintaion node reference
	 * @param copyDetails			the copy details
	 */
	private void copyProperties(NodeRef destinationNodeRef, PolicyScope copyDetails)
	{
		Map<QName, Serializable> props = copyDetails.getProperties();
		if (props != null)
		{
			for (QName propName : props.keySet()) 
			{
				this.nodeService.setProperty(destinationNodeRef, propName, props.get(propName));
			}
		}
	}
	
	/**
	 * Applies the aspects (thus copying the associated properties) onto the destination node
	 * 
	 * @param destinationNodeRef	the destination node reference
	 * @param copyDetails			the copy details
	 */
	private void copyAspects(NodeRef destinationNodeRef, PolicyScope copyDetails)
	{
		Set<QName> apects = copyDetails.getAspects();
		for (QName aspect : apects) 
		{
			if (this.nodeService.hasAspect(destinationNodeRef, aspect) == false)
			{
				// Add the aspect to the node
				this.nodeService.addAspect(
						destinationNodeRef, 
						aspect, 
						copyDetails.getProperties(aspect));
			}
			else
			{
				// Set each property on the destination node since the aspect has already been applied
				Map<QName, Serializable> aspectProps = copyDetails.getProperties(aspect);
				if (aspectProps != null)
				{
					for (Map.Entry<QName, Serializable> entry : aspectProps.entrySet()) 
					{
						this.nodeService.setProperty(destinationNodeRef, entry.getKey(), entry.getValue());
					}
				}
			}
		}
	}	
	
	/**
	 * Copies the associations (child and target) for the node type and aspects onto the 
	 * destination node.
	 * <p>
	 * If copyChildren is true then all child nodes of primary child associations are copied
	 * before they are associatied with the destination node.
	 * 
	 * @param destinationNodeRef	the destination node reference
	 * @param copyDetails			the copy details
	 * @param copyChildren			indicates whether the primary children are copied or not
	 */
	private void copyAssociations(NodeRef destinationNodeRef, PolicyScope copyDetails, boolean copyChildren)
	{
		QName classRef = this.nodeService.getType(destinationNodeRef);
		copyChildAssociations(classRef, destinationNodeRef, copyDetails, copyChildren);
		copyTargetAssociations(classRef, destinationNodeRef, copyDetails);
		
		Set<QName> apects = copyDetails.getAspects();
		for (QName aspect : apects) 
		{
			if (this.nodeService.hasAspect(destinationNodeRef, aspect) == false)
			{
				// Error since the aspect has not been added to the destination node (should never happen)
				throw new NodeOperationsServiceException("The aspect has not been added to the destination node.");
			}
			
			copyChildAssociations(aspect, destinationNodeRef, copyDetails, copyChildren);
			copyTargetAssociations(aspect, destinationNodeRef, copyDetails);
		}
	}
	
	/**
	 * Copies the target associations onto the destination node reference.
	 * 
	 * @param classRef				the class reference
	 * @param destinationNodeRef	the destination node reference
	 * @param copyDetails			the copy details 
	 */
	private void copyTargetAssociations(QName classRef, NodeRef destinationNodeRef, PolicyScope copyDetails) 
	{
		Map<QName, NodeAssocRef> nodeAssocRefs = copyDetails.getAssociations(classRef);
		if (nodeAssocRefs != null)
		{
			for (Map.Entry<QName, NodeAssocRef> entry : nodeAssocRefs.entrySet()) 
			{
				// TODO Currently the order of child associations is not preserved during copy
				
				// Add the association
				NodeRef targetRef = entry.getValue().getTargetRef();
				this.nodeService.createAssociation(destinationNodeRef, targetRef, entry.getValue().getQName());
											
			}
		}
	}

	/**
	 * Copies the child associations onto the destiantion node reference.
	 * <p>
	 * If copyChildren is true then the nodes at the end of a primary assoc will be copied before they
	 * are associated.
	 * 
	 * @param classRef				the class reference
	 * @param destinationNodeRef	the destination node reference
	 * @param copyDetails			the copy details
	 * @param copyChildren			indicates whether to copy the primary children
	 */
	private void copyChildAssociations(QName classRef, NodeRef destinationNodeRef, PolicyScope copyDetails, boolean copyChildren)
	{
		Map<QName, ChildAssocRef> childAssocs = copyDetails.getChildAssociations(classRef);
		if (childAssocs != null)
		{
			for (Map.Entry<QName, ChildAssocRef> entry : childAssocs.entrySet()) 
			{
				// TODO Currently the order of child associations is not preserved during copy
				
				if (copyChildren == true)
				{
					if (entry.getValue().isPrimary() == true)
					{
						// Copy the child
						NodeRef childCopy = copy(
								entry.getValue().getChildRef(), 
								destinationNodeRef, 
								null, 
								entry.getKey(),
								copyChildren);
					}
					else
					{
						// Add the child 
						NodeRef childRef = entry.getValue().getChildRef();
						this.nodeService.addChild(destinationNodeRef, childRef, entry.getKey());
					}
				}
				else
				{
					// Add the child (will not be primary reguardless of its origional state)
					NodeRef childRef = entry.getValue().getChildRef();
					this.nodeService.addChild(destinationNodeRef, childRef, entry.getKey());
				}							
			}
		}
	}

	/**
	 * Defer to the standard implementation with copyChildren set to false
	 * 
     * @see com.activiti.repo.node.copy.NodeCopyService#copy(com.activiti.repo.ref.NodeRef, com.activiti.repo.ref.NodeRef, com.activiti.repo.ref.QName)
     */
    public NodeRef copy(
            NodeRef sourceNodeRef,
            NodeRef destinationParent, 
            QName destinationAssocTypeQName,
            QName destinationQName)
    {
        return copy(
				sourceNodeRef, 
				destinationParent, 
				destinationAssocTypeQName, 
				destinationQName, 
				false);
    }

    /**
     * @see com.activiti.repo.node.copy.NodeCopyService#copy(com.activiti.repo.ref.NodeRef, com.activiti.repo.ref.NodeRef)
     */
    public void copy(
            NodeRef sourceNodeRef, 
            NodeRef destinationNodeRef)
    {
		// Check that the source and destination node are the same type
		if (this.nodeService.getType(sourceNodeRef).equals(this.nodeService.getType(destinationNodeRef)) == false)
		{
			// Error - can not copy objects that are of different types
			throw new NodeOperationsServiceException("The source and destination node must be the same type.");
		}
		
		// Get the copy details
		PolicyScope copyDetails = getCopyDetails(sourceNodeRef);
		
		// Copy over the top of the destination node
		copyProperties(destinationNodeRef, copyDetails);
		copyAspects(destinationNodeRef, copyDetails);
		copyAssociations(destinationNodeRef, copyDetails, false);
    }
	
	/**
	 * OnCopy behaviour registered for the copy aspect.  
	 * <p>
	 * Doing nothing in this behaviour ensures that the copy aspect found on the source node does not get 
	 * copied onto the destination node.
	 * 
	 * @param sourceClassRef	the source class reference
	 * @param sourceNodeRef		the source node reference
	 * @param copyDetails	    the copy details
	 */
	public void copyAspectOnCopy(QName sourceClassRef, NodeRef sourceNodeRef, PolicyScope copyDetails)
	{
		// Do nothing.  This will ensure that copy aspect on the source node does not get copied onto
		// the destination node.
	}	
}
