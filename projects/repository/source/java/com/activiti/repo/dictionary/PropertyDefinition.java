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
     * @return  the qualified name of the property
     */
    public QName getQName();

    /**
     * @return  the property reference
     */   
    public PropertyRef getReference();

    /**
     * TODO: Replace this with public PropertyTypeDefinition getPropertyType();
     * 
     * @return  the qualified name of the property type
     */
    public QName getPropertyType();

    /**
     * @return Returns the owning class's defintion
     */    
    public ClassDefinition getContainerClass();
    
    /**
     * @return  true => multi-valued, false => single-valued  
     */
    public boolean isMultiValued();

    /**
     * @return  true => mandatory, false => optional
     */
    public boolean isMandatory();
    
    /**
     * @return  true => system maintained, false => client may maintain 
     */
    public boolean isProtected();

    /**
     * @return  true => indexed, false => not indexed
     */
    public boolean isIndexed();
    
    /**
     * @return  true => stored in index
     */
    public boolean isStoredInIndex();

    /**
     * @return  Returns the class name of the index tokeniser to use on this property
     */
    public String getIndexTokeniserClassName();
    
}
