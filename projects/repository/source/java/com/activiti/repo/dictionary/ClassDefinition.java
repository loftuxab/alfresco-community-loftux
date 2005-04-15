package com.activiti.repo.dictionary;

import java.util.List;

import com.activiti.repo.ref.QName;

/**
 * Read-only definition of a Class.
 * 
 * @author David Caruana
 */
public interface ClassDefinition
{
    /**
     * @return  the class reference
     */
    public ClassRef getReference();

    /**
     * @return the qualified name of the class
     */
    public QName getQName();
    
    /**
     * @return  the super class (or null, if this is the root)
     */
    public ClassDefinition getSuperClass();
    
    /**
     * @return the first basic bootstrap type, or null if the type doesn't derive
     *      (either directly or indirectly) from any of the bootstrap types
     * 
     * @see com.activiti.repo.dictionary.bootstrap.DictionaryBootstrap#TYPE_BASE
     */
    public ClassRef getBootstrapClass();

    /**
     * @return Returns true => aspect, false => type
     */
    public boolean isAspect();

    /**
     * @return Returns the properties of the class, including inherited properties
     */
    public List<PropertyDefinition> getProperties();
    
    /**
     * @param name the simple name of the property, i.e. not the qualified name
     * @return Returns the property definition, or null if not found
     * 
     * @see PropertyRef#getPropertyName()
     */
    public PropertyDefinition getProperty(String name);

    /**
     * @return Returns the associations including inherited ones
     */
    public List<AssociationDefinition> getAssociations();
    
    /**
     * @param name the simple name of the association, i.e. not the qualified name
     * @return Returns the association definition, or null if not found
     * 
     * @see AssociationRef#getAssociationName()
     */
    public AssociationDefinition getAssociation(String name);
}
