package com.activiti.repo.dictionary;

import com.activiti.repo.ref.QName;


/**
 * Read-only definition of a Property Type
 * 
 * @author David Caruana
 */
public interface PropertyTypeDefinition
{

    // TODO: Default Namespaces
    

    //
    // Property Types
    //
    public QName STRING = QName.createQName("activiti", "string");
    public QName DATE = QName.createQName("activiti", "date");
    public QName BOOLEAN = QName.createQName("activiti", "boolean");
    public QName QNAME = QName.createQName("activiti", "name");
    public QName GUID = QName.createQName("activiti", "guid");
    // TODO: Complete rest of property types

    
    /**
     * Gets the name of the Property Type
     * 
     * @return the qualified name
     */
    public QName getName();
    
}
