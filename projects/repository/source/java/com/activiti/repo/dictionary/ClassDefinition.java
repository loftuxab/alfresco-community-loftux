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
     * @return the first basic bootstrap type, or null if the type doesn't derive
     *      (either directly or indirectly) from any of the bootstrap types
     * 
     * @see com.activiti.repo.dictionary.bootstrap.DictionaryBootstrap#TYPE_BASE
     */
    public ClassRef getBootstrapClass();

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
    public Map<PropertyRef, PropertyDefinition> getProperties();

    /**
     * Gets the associations of the Class
     * 
     * Note: Also includes inherited associations.
     * 
     * @return  associations including inherited
     */
    public Map<AssociationRef, AssociationDefinition> getAssociations();
    
}
