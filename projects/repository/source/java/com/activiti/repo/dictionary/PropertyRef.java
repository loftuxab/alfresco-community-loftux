package com.activiti.repo.dictionary;

import com.activiti.repo.ref.QName;


/**
 * Property Reference
 * 
 * @author David Caruana
 */
public class PropertyRef extends DictionaryRef
{
    private static final long serialVersionUID = 3256444698674935860L;

    /**
     * Owning Class Reference
     */
    private ClassRef classRef;
    
    /**
     * Property Name
     */
    private String propertyName;


    /**
     * Construct Property Reference for Default Data Dictionary store
     * 
     * @param classRef  owning class reference
     * @param propertyName  property name
     */
    public PropertyRef(ClassRef classRef, String propertyName)
    {
        super(QName.createQName(classRef.getQName().getNamespaceURI(), classRef.getQName().getLocalName() + NAME_SEPARATOR + propertyName));
        this.classRef = classRef;
        this.propertyName = propertyName;
    }


    /**
     * Gets the owning class reference
     * 
     * @return  the class reference
     */
    public ClassRef getClassRef()
    {
        return classRef;
    }

    
    /**
     * Gets the property name
     * 
     * @return  the property name
     */
    public String getPropertyName()
    {
        return propertyName; 
    }
    
}
