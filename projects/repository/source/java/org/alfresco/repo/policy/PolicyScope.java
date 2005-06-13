package org.alfresco.repo.policy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.namespace.QName;

/**
 * Policy scope.  
 * <p>
 * Helper often used by policies which require information
 * about a node to be gathered, for example onCopy or onCreateVersion.
 * 
 * @author Roy Wetherall
 */
public class PolicyScope extends AspectDetails
{
	/**
	 * The aspects
	 */
	protected Map<QName, AspectDetails> aspectCopyDetails = new HashMap<QName, AspectDetails>();
	
	/**
	 * Constructor
	 * 
	 * @param classRef  the class reference
	 */
	public PolicyScope(QName classRef)
	{
		super(classRef);
	}
	
	/**
	 * Add a property 
	 * 
	 * @param classRef  the class reference
	 * @param qName		the qualified name of the property
	 * @param value		the value of the property
	 */
	public void addProperty(QName classRef, QName qName, Serializable value) 
	{
		if (classRef.equals(this.classRef) == true)
		{
			addProperty(qName, value);
		}
		else
		{
			AspectDetails aspectDetails = this.aspectCopyDetails.get(classRef);
			if (aspectDetails == null)
			{
				// Add the aspect
				aspectDetails = addAspect(classRef);
			}
			aspectDetails.addProperty(qName, value);
		}
	}
	
	/**
	 * Removes a property from the list
	 * 
	 * @param classRef	the class reference
	 * @param qName		the qualified name
	 */
	public void removeProperty(QName classRef, QName qName) 
	{
		if (classRef.equals(this.classRef) == true)
		{
			removeProperty(qName);
		}
		else
		{
			AspectDetails aspectDetails = this.aspectCopyDetails.get(classRef);
			if (aspectDetails != null)
			{
				aspectDetails.removeProperty(qName);
			}				
		}
	}
	
	/**
	 * Get the properties
	 * 
	 * @param classRef  the class ref
	 * @return			the properties that should be copied
	 */
	public Map<QName, Serializable> getProperties(QName classRef)
	{
		Map<QName, Serializable> result = null;
		if (classRef.equals(this.classRef) == true)
		{
			result = getProperties();
		}
		else
		{
			AspectDetails aspectDetails = this.aspectCopyDetails.get(classRef);
			if (aspectDetails != null)
			{
				result = aspectDetails.getProperties();
			}
		}
		
		return result;
	}
	
	/**
	 * Adds a child association
	 * 
	 * @param classRef
	 * @param qname
	 * @param childAssocRef
	 */
	public void addChildAssociation(QName classRef, QName qname, ChildAssociationRef childAssocRef) 
	{
		if (classRef.equals(this.classRef) == true)
		{
			addChildAssociation(qname, childAssocRef);
		}
		else
		{
			AspectDetails aspectDetails = this.aspectCopyDetails.get(classRef);
			if (aspectDetails == null)
			{
				// Add the aspect
				aspectDetails = addAspect(classRef);
			}
			aspectDetails.addChildAssociation(qname, childAssocRef);
		}
	}
	
	/**
	 * Remove a child association
	 * 
	 * @param classRef
	 * @param qname
	 */
	public void removeChildAssociation(QName classRef, QName qname) 
	{
		if (classRef.equals(this.classRef) == true)
		{
			removeChildAssociation(qname);
		}
		else
		{
			AspectDetails aspectDetails = this.aspectCopyDetails.get(classRef);
			if (aspectDetails != null)
			{
				aspectDetails.removeChildAssociation(qname);
			}				
		}
	}
	
	/**
	 * Get a child association
	 * 
	 * @param classRef
	 * @return
	 */
	public Map<QName, ChildAssociationRef> getChildAssociations(QName classRef) 
	{
		Map<QName, ChildAssociationRef> result = null;
		if (classRef.equals(this.classRef) == true)
		{
			result = getChildAssociations();
		}
		else
		{
			AspectDetails aspectDetails = this.aspectCopyDetails.get(classRef);
			if (aspectDetails != null)
			{
				result = aspectDetails.getChildAssociations();
			}
		}
		
		return result;
	}
	
	/**
	 * Add an association
	 * 
	 * @param classRef
	 * @param qname
	 * @param nodeAssocRef
	 */
	public void addAssociation(QName classRef, QName qname, AssociationRef nodeAssocRef)
	{
		if (classRef.equals(this.classRef) == true)
		{
			addAssociation(qname, nodeAssocRef);
		}
		else
		{
			AspectDetails aspectDetails = this.aspectCopyDetails.get(classRef);
			if (aspectDetails == null)
			{
				// Add the aspect
				aspectDetails = addAspect(classRef);
			}
			aspectDetails.addAssociation(qname, nodeAssocRef);
		}
	}
	
	/**
	 * Remove an association
	 * 
	 * @param classRef
	 * @param qname
	 */
	public void removeAssociation(QName classRef, QName qname) 
	{
		if (classRef.equals(this.classRef) == true)
		{
			removeAssociation(qname);
		}
		else
		{
			AspectDetails aspectDetails = this.aspectCopyDetails.get(classRef);
			if (aspectDetails != null)
			{
				aspectDetails.removeAssociation(qname);
			}				
		}
	}
	
	/**
	 * Get associations
	 * 
	 * @param classRef
	 * @return
	 */
	public Map<QName, AssociationRef> getAssociations(QName classRef) 
	{
		Map<QName, AssociationRef> result = null;
		if (classRef.equals(this.classRef) == true)
		{
			result = getAssociations();
		}
		else
		{
			AspectDetails aspectDetails = this.aspectCopyDetails.get(classRef);
			if (aspectDetails != null)
			{
				result = aspectDetails.getAssociations();
			}
		}
		
		return result;
	}
	
	/**
	 * Add an aspect 
	 * 
	 * @param aspect	the aspect class reference
	 * @return			the apsect copy details (returned as a helper)
	 */
	public AspectDetails addAspect(QName aspect) 
	{
		AspectDetails result = new AspectDetails(aspect);
		this.aspectCopyDetails.put(aspect, result);
		return result;
	}
	
	/**
	 * Removes an aspect from the list 
	 * 
	 * @param aspect	the aspect class reference
	 */
	public void removeAspect(QName aspect) 
	{
		this.aspectCopyDetails.remove(aspect);
	}
	
	/**
	 * Gets a list of the aspects 
	 * 
	 * @return  a list of aspect to copy
	 */
	public Set<QName> getAspects()
	{
		return this.aspectCopyDetails.keySet();
	}		
}

/**
 * Aspect details class.  
 * <p>
 * Contains the details of an aspect this can be used for copying or versioning.
 * 
 * @author Roy Wetherall
 */
/*package*/ class AspectDetails
{
	/**
	 * The properties that should be copied
	 */
	protected Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
	
	/**
	 * The child associations that should be copied
	 */
	protected Map<QName, ChildAssociationRef> childAssocs = new HashMap<QName, ChildAssociationRef>();
	
	/**
	 * The target associations that should be copied
	 */
	protected Map<QName, AssociationRef> targetAssocs = new HashMap<QName, AssociationRef>();
	
	/**
	 * The class ref of the aspect
	 */
	protected QName classRef;

	/**
	 * Constructor
	 * 
	 * @param classRef  the class ref
	 */
	public AspectDetails(QName classRef)
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
	protected void addChildAssociation(QName qname, ChildAssociationRef childAssocRef) 
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
	public Map<QName, ChildAssociationRef> getChildAssociations() 
	{
		return this.childAssocs;
	}
	
	/**
	 * Adds an association to be copied
	 * 
	 * @param qname			the qualified name of the association
	 * @param nodeAssocRef	the association reference
	 */
	protected void addAssociation(QName qname, AssociationRef nodeAssocRef)
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
	public Map<QName, AssociationRef> getAssociations() 
	{
		return this.targetAssocs;
	}	
}