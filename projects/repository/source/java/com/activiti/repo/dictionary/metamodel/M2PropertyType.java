package com.activiti.repo.dictionary.metamodel;

import com.activiti.repo.dictionary.PropertyTypeDefinition;
import com.activiti.repo.ref.QName;


public interface M2PropertyType
{

    
    public QName getName();
    
    public void setName(QName name);    
    
    // TODO:  public List/*M2ValueConstraint*/ getValueConstraints();
 
    public PropertyTypeDefinition getPropertyTypeDefinition();
    
}
