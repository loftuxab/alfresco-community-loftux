package com.activiti.repo.dictionary.metamodel;

import java.util.List;

import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.ref.QName;



public interface M2Class
{
    public ClassRef getReference();
    
    public QName getName();

    public void setName(QName value);

    public M2Class getSuperClass();
    
    public void setSuperClass(M2Class superClass);
    
    public List getProperties();
    
    public M2Property createProperty(String propertyName);
    
    public List getInheritedProperties();
    
    public List getAssociations();
    
    public M2Association createAssociation(String associationName);
    
    public M2ChildAssociation createChildAssociation(String associationName);
    
    public List getInheritedAssociations();
    
    public ClassDefinition getClassDefinition();
    
}
