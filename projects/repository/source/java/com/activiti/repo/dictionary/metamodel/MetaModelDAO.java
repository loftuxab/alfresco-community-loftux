package com.activiti.repo.dictionary.metamodel;

import java.util.Collection;

import com.activiti.repo.ref.QName;

public interface MetaModelDAO
{
    
    public Collection/*<QName>*/ getTypes();

    public M2Class getClass(QName className);

    public M2Type getType(QName typeName);

    public M2Aspect getAspect(QName aspectName);
    
    public M2Property getProperty(QName className, String propertyName);
    
    public M2PropertyType getPropertyType(QName propertyType);
        
    
    public M2Type createType(QName typeName);
    
    public M2Aspect createAspect(QName aspectName);
    
    public M2PropertyType createPropertyType(QName typeName);
    
    public void save();
    
}
