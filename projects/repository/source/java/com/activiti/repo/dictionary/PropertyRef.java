package com.activiti.repo.dictionary;

import com.activiti.repo.ref.QName;

public class PropertyRef extends DictionaryRef
{

    private static final long serialVersionUID = 3256444698674935860L;
    
    private ClassRef classRef;
    private String propertyName;
    
    public PropertyRef(ClassRef classRef, String propertyName)
    {
        super(QName.createQName(classRef.getQName().getNamespaceURI(), classRef.getQName().getLocalName() + NAME_SEPARATOR + propertyName));
        this.classRef = classRef;
        this.propertyName = propertyName;
    }

    
    public ClassRef getClassRef()
    {
        return classRef;
    }
    
    public String getPropertyName()
    {
        return propertyName; 
    }
    
}
