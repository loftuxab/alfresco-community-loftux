package com.activiti.repo.dictionary;

import java.util.Map;

import com.activiti.repo.ref.QName;


/**
 * Read-only definition of a Class.
 * 
 * @author David Caruana
 */
public interface ClassDefinition
{
    /**
     * Gets the Class Reference
     * 
     * @return  the class reference
     */
    public ClassRef getReference();

    /**
     * Gets the class qualified name
     * 
     * @return  qualified name
     */
    public QName getName();
    
    /**
     * Gets the super class
     * 
     * @return  the super class (or null, if this is the root)
     */
    public ClassRef getSuperClass();

    /**
     * Is this an Aspect definition?
     * 
     * @return  true => aspect, false => type
     */
    public boolean isAspect();

    /**
     * Gets the properties of the Class
     * 
     * Note: Also includes inherited properties.
     * 
     * @return  properties including inherited
     */
    public Map/*<PropertyRef, PropertyDefinition>*/ getProperties();

    /**
     * Gets the associations of the Class
     * 
     * Note: Also includes inherited associations.
     * 
     * @return  associations including inherited
     */
    public Map/*<AssocRef, AssociationDefinition>*/ getAssociations();
    
}
