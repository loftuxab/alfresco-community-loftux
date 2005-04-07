package com.activiti.repo.dictionary;

import com.activiti.repo.ref.QName;


/**
 * Read-only definition of a Property Type
 * 
 * @author David Caruana
 */
public interface PropertyTypeDefinition
{

    //
    // Property Types
    //
    public QName STRING = QName.createQName(NamespaceService.ACTIVITI_URI, "string");
    public QName DATE = QName.createQName(NamespaceService.ACTIVITI_URI, "date");
    public QName BOOLEAN = QName.createQName(NamespaceService.ACTIVITI_URI, "boolean");
    public QName QNAME = QName.createQName(NamespaceService.ACTIVITI_URI, "name");
    public QName GUID = QName.createQName(NamespaceService.ACTIVITI_URI, "guid");
    // TODO: Complete rest of property types

    
    /**
     * Gets the name of the Property Type
     * 
     * @return the qualified name
     */
    public QName getName();
    
}
