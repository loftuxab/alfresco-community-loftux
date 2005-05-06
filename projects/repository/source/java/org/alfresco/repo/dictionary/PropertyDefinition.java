package org.alfresco.repo.dictionary;

import org.alfresco.repo.ref.QName;

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
    public PropertyTypeDefinition getPropertyType();

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
     * @return true => tokenised when it is indexed (the stored value will not be tokenised)
     */
    
    public boolean isTokenisedInIndex();
    
    /**
     * @return true => The attribute must be indexed in the commit of the transaction. 
     * false => the indexing will be done in the background and may be out of date.
     * All non atomic properties will be indexed at the same time.
     */
    public boolean isIndexedAtomically();
    
}
