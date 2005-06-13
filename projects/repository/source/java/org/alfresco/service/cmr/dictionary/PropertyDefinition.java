package org.alfresco.service.cmr.dictionary;

import org.alfresco.service.namespace.QName;

/**
 * Read-only definition of a Property.
 * 
 * @author David Caruana
 */
public interface PropertyDefinition
{
    /**
     * @return the qualified name of the property
     */
    public QName getName();

    /**
     * @return the human-readable class title 
     */
    public String getTitle();
    
    /**
     * @return the human-readable class description 
     */
    public String getDescription();
    
    /**
     * @return the default value 
     */
    public String getDefaultValue();
    
    /**
     * @return the qualified name of the property type
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
     * All non atomic properties will be indexed at the same time.
     *
     * @return true => The attribute must be indexed in the commit of the transaction. 
     * false => the indexing will be done in the background and may be out of date.
     */
    public boolean isIndexedAtomically();
    
}
