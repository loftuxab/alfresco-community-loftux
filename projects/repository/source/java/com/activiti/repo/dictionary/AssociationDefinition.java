package com.activiti.repo.dictionary;

import java.util.List;

import com.activiti.repo.ref.QName;


/**
 * Read-only definition of an Association.
 *  
 * @author David Caruana
 *
 */
public interface AssociationDefinition
{
    /**
     * Gets the qualified name of the association
     * 
     * @return  the qualified name
     */
    public QName getName();

    /**
     * Gets the association reference
     * 
     * @return  the association reference
     */
    public AssociationRef getReference();

    /**
     * Gets the class reference that owns this association
     * 
     * @return  the owning class reference
     */
    public ClassRef getContainerClass();

    /**
     * Gets the list of classes that this association must refer to
     * 
     * @return  the class list  
     */
    public List<ClassRef> getRequiredToClasses();
    
    /**
     * Is this a child association?
     * 
     * @return  true => child,  false => general relationship
     */
    public boolean isChild();

    /**
     * Gets whether this association supports a cardinality of greater than 1
     * 
     * @return  true => cardinality > 1, false => cardinality of 0 or 1
     */
    public boolean isMultiValued();

    /**
     * Gets whether this association supports a cardinality of zero
     *  
     * @return  true => cardinality > 0
     */
    public boolean isMandatory();

    /**
     * Is this association maintained by the Repository?
     * 
     * @return  true => system maintained, false => client may maintain 
     */
    public boolean isProtected();

}
