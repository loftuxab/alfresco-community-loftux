package com.activiti.repo.dictionary;

import com.activiti.repo.ref.QName;


/**
 * Read-only definition of a Property.
 * 
 * @author David Caruana
 */
public interface PropertyDefinition
{
    /**
     * Gets the qualified name of the property
     * 
     * @return  the qualified name
     */
    public QName getName();

    /**
     * Gets the property reference
     * 
     * @return  the reference
     */   
    public PropertyRef getReference();

    /**
     * Gets the property type
     *
     * TODO: Replace this with public PropertyTypeDefinition getPropertyType();
     * 
     * @return  the qualified name of the property type
     */
    public QName getPropertyType();

    /**
     * Gets the owing class of this property
     * 
     * @return  owning class reference
     */    
    public ClassRef getContainerClass();
    
    /**
     * Is this a multi-valued property?
     * 
     * @return  true => multi-valued, false => single-valued  
     */
    public boolean isMultiValued();

    /**
     * Is this property mandatory?
     * 
     * @return  true => mandatory, false => optional
     */
    public boolean isMandatory();
    
    /**
     * Is this association maintained by the Repository?
     * 
     * @return  true => system maintained, false => client may maintain 
     */
    public boolean isProtected();

    /**
     * Is this property indexed?
     * 
     * @return  true => indexed, false => not indexed
     */
    public boolean isIndexed();
    
    /**
     * Is this property stored in the index?
     * 
     * @return  true => stored in index
     */
    public boolean isStoredInIndex();

    /**
     * Gets the class name of the index tokeniser to use on this property
     *  
     * @return  tokeniser class name
     */
    public String getIndexTokeniserClassName();
    
}
