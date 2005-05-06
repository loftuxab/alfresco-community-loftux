package org.alfresco.repo.dictionary;

import org.alfresco.repo.ref.QName;


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

    public QName ANY = QName.createQName(NamespaceService.alfresco_URI, "any");
    public QName TEXT = QName.createQName(NamespaceService.alfresco_URI, "text");
    public QName CONTENT = QName.createQName(NamespaceService.alfresco_URI, "content");
    public QName INT = QName.createQName(NamespaceService.alfresco_URI, "int");
    public QName LONG = QName.createQName(NamespaceService.alfresco_URI, "long");
    public QName FLOAT = QName.createQName(NamespaceService.alfresco_URI, "float");
    public QName DOUBLE = QName.createQName(NamespaceService.alfresco_URI, "double");
    public QName DATE = QName.createQName(NamespaceService.alfresco_URI, "date");
    public QName DATETIME = QName.createQName(NamespaceService.alfresco_URI, "datetime");
    public QName BOOLEAN = QName.createQName(NamespaceService.alfresco_URI, "boolean");
    public QName NAME = QName.createQName(NamespaceService.alfresco_URI, "name");
    public QName GUID = QName.createQName(NamespaceService.alfresco_URI, "guid");
    public QName CATEGORY = QName.createQName(NamespaceService.alfresco_URI, "category");
    
    
    /**
     * Gets the name of the Property Type
     * 
     * @return the qualified name
     */
    public QName getName();
    
    public String getAnalyserClassName();
    
}
