package org.alfresco.repo.dictionary.impl;

import java.util.Collection;

import org.alfresco.repo.ref.NamespacePrefixResolver;


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

    public void addURI(String uri);
    
    public void removeURI(String uri);

    public void addPrefix(String prefix, String uri);

    public void removePrefix(String prefix);
    
}
