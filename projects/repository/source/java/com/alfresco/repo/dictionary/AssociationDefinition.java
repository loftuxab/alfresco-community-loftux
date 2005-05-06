package org.alfresco.repo.dictionary;

import java.util.List;

import org.alfresco.repo.ref.QName;


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
     * @return  the owning class definition
     */
    public ClassDefinition getContainerClass();

    /**
     * @return  the list of classes that this association must refer to  
     */
    public List<ClassDefinition> getRequiredToClasses();
    
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
