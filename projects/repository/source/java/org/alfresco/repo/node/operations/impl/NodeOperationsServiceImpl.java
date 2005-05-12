/**
 * Created on May 5, 2005
 */
package org.alfresco.repo.node.operations.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.node.operations.NodeOperationsService;
import org.alfresco.repo.node.operations.NodeOperationsServiceException;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.util.debug.CodeMonkey;

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
			ClassRef sourceClassRef = this.nodeService.getType(sourceNodeRef);
			CopyDetails copyDetails = getCopyDetails(sourceNodeRef);			
			
            if (sourceNodeRef.getStoreRef().equals(destinationParent.getStoreRef()) == true)
            {
				// Create the new node
                ChildAssocRef destinationChildAssocRef = this.nodeService.createNode(
                        destinationParent, 
                        destinationAssocTypeQName,
                        destinationQName,
                        sourceClassRef.getQName(),
                        copyDetails.getProperties());
                destinationNodeRef = destinationChildAssocRef.getChildRef();
				
				//	Apply the copy aspect to the new node	
				Map<QName, Serializable> copyProperties = new HashMap<QName, Serializable>();
				copyProperties.put(DictionaryBootstrap.PROP_QNAME_COPY_REFERENCE, sourceNodeRef);
				this.nodeService.addAspect(destinationNodeRef, DictionaryBootstrap.ASPECT_COPY, copyProperties);
            }
            else
            {
				// TODO
				CodeMonkey.todo("We need to create a new node in the other store with the same id as the source.");

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
	private CopyDetails getCopyDetails(NodeRef sourceNodeRef)
	{
		ClassRef sourceClassRef = this.nodeService.getType(sourceNodeRef);		
		CopyDetails copyDetails = new CopyDetails(sourceClassRef);
		
		// TODO call the appropriate policy
		defaultOnCopy(sourceClassRef, sourceNodeRef, copyDetails);
		
		// TODO 
		CodeMonkey.todo("What do we do aboout props and assocs that are on the node node but not part of the type definition?");
		
		// Get the source aspects
		Set<ClassRef> sourceAspects = this.nodeService.getAspects(sourceNodeRef);
		for (ClassRef sourceAspect : sourceAspects) 
		{
			// Call the onCopy policy for the aspect
			// TODO call the appropriate policy
			defaultOnCopy(sourceAspect, sourceNodeRef, copyDetails);
		}
		
		return copyDetails;
	}
	
	/**
	 * Default implementation of on copy, used when there is no policy specified for a class.
	 * 
	 * @param classRef			the class reference of the node being copied
	 * @param sourceNodeRef		the source node reference
	 * @param copyDetails		details of the state being copied
	 */
    private void defaultOnCopy(ClassRef classRef, NodeRef sourceNodeRef, CopyDetails copyDetails) 
	{
		ClassDefinition classDefinition = this.dictionaryService.getClass(classRef);	
		if (classDefinition != null)
		{			
			// Copy the properties
			List<PropertyDefinition> propertyDefinitions = classDefinition.getProperties();
			for (PropertyDefinition propertyDefinition : propertyDefinitions) 
			{
				QName propName = propertyDefinition.getQName();
				Serializable propValue = this.nodeService.getProperty(sourceNodeRef, propName);
				copyDetails.addProperty(classRef, propName, propValue);
			}			
			
			// Copy the associations (child and target)
			List<AssociationDefinition> assocDefs = classDefinition.getAssociations();
			for (AssociationDefinition assocDef : assocDefs) 
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
	private void copyProperties(NodeRef destinationNodeRef, CopyDetails copyDetails)
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
	private void copyAspects(NodeRef destinationNodeRef, CopyDetails copyDetails)
	{
		Set<ClassRef> apects = copyDetails.getAspects();
		for (ClassRef aspect : apects) 
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
	private void copyAssociations(NodeRef destinationNodeRef, CopyDetails copyDetails, boolean copyChildren)
	{
		ClassRef classRef = this.nodeService.getType(destinationNodeRef);
		copyChildAssociations(classRef, destinationNodeRef, copyDetails, copyChildren);
		copyTargetAssociations(classRef, destinationNodeRef, copyDetails);
		
		Set<ClassRef> apects = copyDetails.getAspects();
		for (ClassRef aspect : apects) 
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
	private void copyTargetAssociations(ClassRef classRef, NodeRef destinationNodeRef, CopyDetails copyDetails) 
	{
		Map<QName, NodeAssocRef> nodeAssocRefs = copyDetails.getAssociations(classRef);
		if (nodeAssocRefs != null)
		{
			for (Map.Entry<QName, NodeAssocRef> entry : nodeAssocRefs.entrySet()) 
			{
				// TODO need to use the nth value when adding the child node
				CodeMonkey.todo("Currently the order of child associations is not preserved during copy");
				
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
	private void copyChildAssociations(ClassRef classRef, NodeRef destinationNodeRef, CopyDetails copyDetails, boolean copyChildren)
	{
		Map<QName, ChildAssocRef> childAssocs = copyDetails.getChildAssociations(classRef);
		if (childAssocs != null)
		{
			for (Map.Entry<QName, ChildAssocRef> entry : childAssocs.entrySet()) 
			{
				// TODO need to use the nth value when adding the child node
				CodeMonkey.todo("Currently the order of child associations is not preserved during copy");
				
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
		CopyDetails copyDetails = getCopyDetails(sourceNodeRef);
		
		// Copy over the top of the destination node
		copyProperties(destinationNodeRef, copyDetails);
		copyAspects(destinationNodeRef, copyDetails);
		copyAssociations(destinationNodeRef, copyDetails, false);
    }
	
	/**
	 * Aspect copy details class.  Contains the details of an aspect that should be copied.
	 * 
	 * @author Roy Wetherall
	 */
	private class AspectCopyDetails
	{
		/**
		 * The properties that should be copied
		 */
		protected Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		
		/**
		 * The child associations that should be copied
		 */
		protected Map<QName, ChildAssocRef> childAssocs = new HashMap<QName, ChildAssocRef>();
		
		/**
		 * The target associations that should be copied
		 */
		protected Map<QName, NodeAssocRef> targetAssocs = new HashMap<QName, NodeAssocRef>();
		
		/**
		 * The class ref of the aspect
		 */
		protected ClassRef classRef;
	
		/**
		 * Constructor
		 * 
		 * @param classRef  the class ref
		 */
		public AspectCopyDetails(ClassRef classRef)
		{
			this.classRef = classRef;
		}
		
		/**
		 * Add a property to the list of those to be copied
		 * 
		 * @param qName		the qualified name of the property
		 * @param value		the value of the property
		 */
		protected void addProperty(QName qName, Serializable value) 
		{
			this.properties.put(qName, value);			
		}
		
		/**
		 * Remove a property from the list of thiose to be copied
		 * 
		 * @param qName		the qualified name of the property
		 */
		protected void removeProperty(QName qName) 
		{
			this.properties.remove(qName);			
		}
		
		/**
		 * Gets the map of properties to be copied
		 * 
		 * @return  map of property names and values
		 */
		public Map<QName, Serializable> getProperties() 
		{
			return properties;
		}
		
		/**
		 * Add a child association to copy
		 * 
		 * @param qname			the qualified name of the association
		 * @param childAssocRef the child association reference
		 */
		protected void addChildAssociation(QName qname, ChildAssocRef childAssocRef) 
		{
			this.childAssocs.put(qname, childAssocRef);
		}
		
		/**
		 * Remove a child association from the list to copy
		 * 
		 * @param qname  the qualified name of the association
		 */
		protected void removeChildAssociation(QName qname) 
		{
			this.childAssocs.remove(qname);
		}
		
		/**
		 * Gets the child associations to be copied
		 * 
		 * @return  map containing the child associations to be copied
		 */
		public Map<QName, ChildAssocRef> getChildAssociations() 
		{
			return this.childAssocs;
		}
		
		/**
		 * Adds an association to be copied
		 * 
		 * @param qname			the qualified name of the association
		 * @param nodeAssocRef	the association reference
		 */
		protected void addAssociation(QName qname, NodeAssocRef nodeAssocRef)
		{
			this.targetAssocs.put(qname, nodeAssocRef);
		}
		
		/**
		 * Remove an association from the list to be copied
		 * 
		 * @param qname  the qualified name of the association
		 */
		protected void removeAssociation(QName qname) 
		{
			this.targetAssocs.remove(qname);
		}
		
		/**
		 * Gets the map of associations to be copied
		 * 
		 * @return  a map conatining the associations to be copied
		 */
		public Map<QName, NodeAssocRef> getAssociations() 
		{
			return this.targetAssocs;
		}		
	}
	
	/**
	 * Copy details class.  Contains the details of the node to be copied.
	 * 
	 * @author Roy Wetherall
	 */
	public class CopyDetails extends AspectCopyDetails
	{
		/**
		 * The aspects to be copied
		 */
		protected Map<ClassRef, AspectCopyDetails> aspectCopyDetails = new HashMap<ClassRef, AspectCopyDetails>();
		
		/**
		 * Constructor
		 * 
		 * @param classRef  the class reference
		 */
		public CopyDetails(ClassRef classRef)
		{
			super(classRef);
		}
		
		/**
		 * Adda property to be copied
		 * 
		 * @param classRef  the class reference
		 * @param qName		the qualified name of the property
		 * @param value		the value of the property
		 */
		public void addProperty(ClassRef classRef, QName qName, Serializable value) 
		{
			if (classRef.equals(this.classRef) == true)
			{
				addProperty(qName, value);
			}
			else
			{
				AspectCopyDetails aspectDetails = this.aspectCopyDetails.get(classRef);
				if (aspectDetails == null)
				{
					// Add the aspect
					aspectDetails = addAspect(classRef);
				}
				aspectDetails.addProperty(qName, value);
			}
		}
		
		/**
		 * Removes a property from the list to be copied
		 * 
		 * @param classRef	the class reference
		 * @param qName		the qualified name
		 */
		public void removeProperty(ClassRef classRef, QName qName) 
		{
			if (classRef.equals(this.classRef) == true)
			{
				removeProperty(qName);
			}
			else
			{
				AspectCopyDetails aspectDetails = this.aspectCopyDetails.get(classRef);
				if (aspectDetails != null)
				{
					aspectDetails.removeProperty(qName);
				}				
			}
		}
		
		/**
		 * Get the properties that to be copied for the class/aspect specified.
		 * 
		 * @param classRef  the class ref
		 * @return			the properties that should be copied
		 */
		public Map<QName, Serializable> getProperties(ClassRef classRef)
		{
			Map<QName, Serializable> result = null;
			if (classRef.equals(this.classRef) == true)
			{
				result = getProperties();
			}
			else
			{
				AspectCopyDetails aspectDetails = this.aspectCopyDetails.get(classRef);
				if (aspectDetails != null)
				{
					result = aspectDetails.getProperties();
				}
			}
			
			return result;
		}
		
		/**
		 * 
		 * @param classRef
		 * @param qname
		 * @param childAssocRef
		 */
		public void addChildAssociation(ClassRef classRef, QName qname, ChildAssocRef childAssocRef) 
		{
			if (classRef.equals(this.classRef) == true)
			{
				addChildAssociation(qname, childAssocRef);
			}
			else
			{
				AspectCopyDetails aspectDetails = this.aspectCopyDetails.get(classRef);
				if (aspectDetails == null)
				{
					// Add the aspect
					aspectDetails = addAspect(classRef);
				}
				aspectDetails.addChildAssociation(qname, childAssocRef);
			}
		}
		
		/**
		 * 
		 * @param classRef
		 * @param qname
		 */
		public void removeChildAssociation(ClassRef classRef, QName qname) 
		{
			if (classRef.equals(this.classRef) == true)
			{
				removeChildAssociation(qname);
			}
			else
			{
				AspectCopyDetails aspectDetails = this.aspectCopyDetails.get(classRef);
				if (aspectDetails != null)
				{
					aspectDetails.removeChildAssociation(qname);
				}				
			}
		}
		
		/**
		 * 
		 * @param classRef
		 * @return
		 */
		public Map<QName, ChildAssocRef> getChildAssociations(ClassRef classRef) 
		{
			Map<QName, ChildAssocRef> result = null;
			if (classRef.equals(this.classRef) == true)
			{
				result = getChildAssociations();
			}
			else
			{
				AspectCopyDetails aspectDetails = this.aspectCopyDetails.get(classRef);
				if (aspectDetails != null)
				{
					result = aspectDetails.getChildAssociations();
				}
			}
			
			return result;
		}
		
		/**
		 * 
		 * @param classRef
		 * @param qname
		 * @param nodeAssocRef
		 */
		public void addAssociation(ClassRef classRef, QName qname, NodeAssocRef nodeAssocRef)
		{
			if (classRef.equals(this.classRef) == true)
			{
				addAssociation(qname, nodeAssocRef);
			}
			else
			{
				AspectCopyDetails aspectDetails = this.aspectCopyDetails.get(classRef);
				if (aspectDetails == null)
				{
					// Add the aspect
					aspectDetails = addAspect(classRef);
				}
				aspectDetails.addAssociation(qname, nodeAssocRef);
			}
		}
		
		/**
		 * 
		 * @param classRef
		 * @param qname
		 */
		public void removeAssociation(ClassRef classRef, QName qname) 
		{
			if (classRef.equals(this.classRef) == true)
			{
				removeAssociation(qname);
			}
			else
			{
				AspectCopyDetails aspectDetails = this.aspectCopyDetails.get(classRef);
				if (aspectDetails != null)
				{
					aspectDetails.removeAssociation(qname);
				}				
			}
		}
		
		/**
		 * 
		 * @param classRef
		 * @return
		 */
		public Map<QName, NodeAssocRef> getAssociations(ClassRef classRef) 
		{
			Map<QName, NodeAssocRef> result = null;
			if (classRef.equals(this.classRef) == true)
			{
				result = getAssociations();
			}
			else
			{
				AspectCopyDetails aspectDetails = this.aspectCopyDetails.get(classRef);
				if (aspectDetails != null)
				{
					result = aspectDetails.getAssociations();
				}
			}
			
			return result;
		}
		
		/**
		 * Add an aspect to be copied
		 * 
		 * @param aspect	the aspect class reference
		 * @return			the apsect copy details (returned as a helper)
		 */
		public AspectCopyDetails addAspect(ClassRef aspect) 
		{
			AspectCopyDetails result = new AspectCopyDetails(aspect);
			this.aspectCopyDetails.put(aspect, result);
			return result;
		}
		
		/**
		 * Removes an aspect from the list to be copied
		 * 
		 * @param aspect	the aspect class reference
		 */
		public void removeAspect(ClassRef aspect) 
		{
			this.aspectCopyDetails.remove(aspect);
		}
		
		/**
		 * Gets a list of the aspects to be copied
		 * 
		 * @return  a list of aspect to copy
		 */
		public Set<ClassRef> getAspects()
		{
			return this.aspectCopyDetails.keySet();
		}
			
	}
}
