package org.alfresco.repo.dictionary.impl;

import java.util.Collection;

import org.alfresco.service.namespace.NamespacePrefixResolver;


/**
 * Namespace DAO Interface.
 * 
 * This DAO is responsible for retrieving and creating Namespace definitions.
 * 
 * @author David Caruana
 */
public interface NamespaceDAO extends NamespacePrefixResolver
{
    
    /**
     * @return all registered URIs
     */
    public Collection<String> getURIs();

    /**
     * @return  all registered prefixes
     */
    public Collection<String> getPrefixes();

    /**
     * Add a namespace URI
     * 
     * @param uri the namespace uri to add
     */
    public void addURI(String uri);

    /**
     * Remove the specified URI
     * 
     * @param uri the uri to remove
     */
    public void removeURI(String uri);

    /**
     * Add a namespace prefix
     * 
     * @param prefix the prefix
     * @param uri the uri to prefix
     */    
    public void addPrefix(String prefix, String uri);

    /**
     * Remove a namspace prefix
     * 
     * @param prefix the prefix to remove
     */
    public void removePrefix(String prefix);
    
}
