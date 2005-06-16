package org.alfresco.service.cmr.dictionary;

import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;


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
    public QName ANY = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "any");
    public QName TEXT = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "text");
    public QName CONTENT = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "content");
    public QName INT = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "int");
    public QName LONG = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "long");
    public QName FLOAT = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "float");
    public QName DOUBLE = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "double");
    public QName DATE = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "date");
    public QName DATETIME = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "datetime");
    public QName BOOLEAN = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "boolean");
    public QName QNAME = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "qname");
    public QName GUID = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "guid");
    public QName CATEGORY = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "category");
    public QName NODE_REF = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "noderef");
    public QName PATH = QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "path");
    
    
    /**
     * @return the qualified name of the property type
     */
    public QName getName();
    
    /**
     * @return the human-readable class title 
     */
    public String getTitle();
    
    /**
     * @return the human-readable class description 
     */
    public String getDescription();

    /**
     * @return the indexing analyser class
     */
    public String getAnalyserClassName();
    
    /**
     * @return the equivalent java class name (or null, if not mapped) 
     */
    public String getJavaClassName();
    
}
