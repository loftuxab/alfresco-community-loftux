package com.activiti.repo.dictionary;

import java.util.Map;

import com.activiti.repo.ref.QName;


public interface ClassDefinition
{
    public ClassRef getReference();

    public QName getName();
    
    public ClassRef getSuperClass();
    
    public boolean isAspect();
    
    public Map/*<PropertyRef, PropertyDefinition>*/ getProperties();
    
    public Map/*<AssocRef, AssociationDefinition>*/ getAssociations();
    
}
