package com.activiti.repo.dictionary.metamodel;

import com.activiti.repo.dictionary.PropertyTypeDefinition;
import com.activiti.repo.ref.QName;


/**
 * Property Type Definition
 * 
 * @author David Caruana
 */
public interface M2PropertyType
{

    public QName getQName();
    
    public void setQName(QName qname);    
    
    // TODO:  public List/*M2ValueConstraint*/ getValueConstraints();

    /**
     * Gets the read-only Property Type Definition
     * 
     * @return the read-only definition
     */
    public PropertyTypeDefinition getPropertyTypeDefinition();
    
}
