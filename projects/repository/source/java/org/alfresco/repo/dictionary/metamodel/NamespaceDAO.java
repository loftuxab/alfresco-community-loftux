package org.alfresco.repo.dictionary.metamodel;

import java.util.Collection;


/**
 * Namespace DAO Interface.
 * 
 * This DAO is responsible for retrieving and creating Namespace definitions.
 * 
 * @author David Caruana
 */
public interface NamespaceDAO
{
    
    /**
     * Gets all registered URIs
     * 
     * @return  all registered URIs
     */
    public Collection<String> getURIs();

    /**
     * Gets all registered Prefixes
     * 
     * @return  all registered prefixes
     */
    public Collection<String> getPrefixes();
    
    /**
     * Gets the specified URI definition
     * 
     * @param uri  the uri
     * @return  the uri definition
     */
    public M2NamespaceURI getURI(String uri);
    
    /**
     * Gets the specified Prefix definition
     * 
     * @param prefix  the prefix
     * @return  the prefix definition
     */
    public M2NamespacePrefix getPrefix(String prefix);

    /**
     * Create URI definition
     * 
     * @param uri  uri
     * @return  the definition
     */
    public M2NamespaceURI createURI(String uri);
    
    /**
     * Create Prefix definition
     * 
     * @param prefix  prefix
     * @return  the definition
     */
    public M2NamespacePrefix createPrefix(String prefix);

    // TODO: Delete methods...
    
}
