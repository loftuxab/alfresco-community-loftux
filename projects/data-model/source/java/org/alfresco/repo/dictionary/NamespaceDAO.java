package org.alfresco.repo.dictionary;

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
    
    /**
     * Initialise Namespaces
     */
//    public void init();
    
//    public void afterDictionaryInit();
    
    /**
     * Destroy Namespaces
     */
//    public void destroy();
    
    /**
     * Register with the Dictionary
     */
//    public void registerDictionary(DictionaryDAO dictionaryDAO);

    /**
     * Clear NamespaceLocal
     */
//    public void clearNamespaceLocal();
}
