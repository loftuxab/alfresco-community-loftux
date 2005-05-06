package org.alfresco.repo.dictionary;

import org.alfresco.repo.ref.QName;

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
     * @param propertyName  property name - this is not the fully qualified name as
     *      the property is qualified by its defining class/aspect
     */
    public PropertyRef(ClassRef classRef, String propertyName)
    {
        super(QName.createQName(
                classRef.getQName().getNamespaceURI(), propertyName));
        this.classRef = classRef;
        this.propertyName = propertyName;
    }

    /**
     * @return Returns the owning class reference
     */
    public ClassRef getClassRef()
    {
        return classRef;
    }

    /**
     * @return Returns the simple property name, i.e. not the fully qualified name
     * 
     * @see DictionaryRef#getQName()
     */
    public String getPropertyName()
    {
        return propertyName; 
    }    
}
