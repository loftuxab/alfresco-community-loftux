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

    public QName ANY = QName.createQName(NamespaceService.ALFRESCO_URI, "any");
    public QName TEXT = QName.createQName(NamespaceService.ALFRESCO_URI, "text");
    public QName CONTENT = QName.createQName(NamespaceService.ALFRESCO_URI, "content");
    public QName INT = QName.createQName(NamespaceService.ALFRESCO_URI, "int");
    public QName LONG = QName.createQName(NamespaceService.ALFRESCO_URI, "long");
    public QName FLOAT = QName.createQName(NamespaceService.ALFRESCO_URI, "float");
    public QName DOUBLE = QName.createQName(NamespaceService.ALFRESCO_URI, "double");
    public QName DATE = QName.createQName(NamespaceService.ALFRESCO_URI, "date");
    public QName DATETIME = QName.createQName(NamespaceService.ALFRESCO_URI, "datetime");
    public QName BOOLEAN = QName.createQName(NamespaceService.ALFRESCO_URI, "boolean");
    public QName NAME = QName.createQName(NamespaceService.ALFRESCO_URI, "name");
    public QName GUID = QName.createQName(NamespaceService.ALFRESCO_URI, "guid");
    public QName CATEGORY = QName.createQName(NamespaceService.ALFRESCO_URI, "category");
    
    
    /**
     * Gets the name of the Property Type
     * 
     * @return the qualified name
     */
    public QName getName();
    
    public String getAnalyserClassName();
    
}
