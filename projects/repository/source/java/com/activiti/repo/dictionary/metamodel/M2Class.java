package com.activiti.repo.dictionary.metamodel;

import java.util.List;

import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.ref.QName;


/**
 * Class Definition
 * 
 * @author David Caruana
 */
public interface M2Class
{
    public ClassRef getReference();
    
    public QName getName();

    public void setName(QName value);

    public M2Class getSuperClass();
    
    public void setSuperClass(M2Class superClass);
    
    public List<M2Property> getProperties();
    
    public M2Property createProperty(String propertyName);
    
    public List<M2Property> getInheritedProperties();
    
    public List<M2Association> getAssociations();
    
    public M2Association createAssociation(String associationName);
    
    public M2ChildAssociation createChildAssociation(String associationName);
    
    public List<M2Association> getInheritedAssociations();

    /**
     * Gets the read-only class definition
     * 
     * @return  read-only class definition
     */
    public ClassDefinition getClassDefinition();
    
}
