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
    // Built-in Property Types
    //

    public QName ANY = QName.createQName(NamespaceService.ACTIVITI_URI, "any");
    public QName TEXT = QName.createQName(NamespaceService.ACTIVITI_URI, "text");
    public QName CONTENT = QName.createQName(NamespaceService.ACTIVITI_URI, "content");
    public QName INT = QName.createQName(NamespaceService.ACTIVITI_URI, "int");
    public QName LONG = QName.createQName(NamespaceService.ACTIVITI_URI, "long");
    public QName FLOAT = QName.createQName(NamespaceService.ACTIVITI_URI, "float");
    public QName DOUBLE = QName.createQName(NamespaceService.ACTIVITI_URI, "double");
    public QName DATE = QName.createQName(NamespaceService.ACTIVITI_URI, "date");
    public QName DATETIME = QName.createQName(NamespaceService.ACTIVITI_URI, "datetime");
    public QName BOOLEAN = QName.createQName(NamespaceService.ACTIVITI_URI, "boolean");
    public QName NAME = QName.createQName(NamespaceService.ACTIVITI_URI, "name");
    public QName GUID = QName.createQName(NamespaceService.ACTIVITI_URI, "guid");
    public QName CATEGORY = QName.createQName(NamespaceService.ACTIVITI_URI, "category");
    
    
    /**
     * Gets the name of the Property Type
     * 
     * @return the qualified name
     */
    public QName getName();
    
}
