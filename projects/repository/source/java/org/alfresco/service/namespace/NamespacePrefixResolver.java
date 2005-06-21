package org.alfresco.service.namespace;

import java.util.Collection;

/**
 * The <code>NamespacePrefixResolver</code> provides a mapping between
 * namespace prefixes and namespace URIs.
 * 
 * @author David Caruana
 */
public interface NamespacePrefixResolver
{
    /**
     * Gets the namespace URI registered for the given prefix
     * 
     * @param prefix  prefix to lookup
     * @return  the namespace
     * @throws NamespaceException  if prefix has not been registered  
     */
    public String getNamespaceURI(String prefix)
        throws NamespaceException;
    
    /**
     * Gets the registered prefixes for the given namespace URI
     * 
     * @param namespaceURI  namespace URI to lookup
     * @return  the prefixes (or empty collection, if no prefixes registered against URI)
     * @throws NamespaceException  if URI has not been registered 
     */
    public Collection<String> getPrefixes(String namespaceURI)
        throws NamespaceException;
    
    /**
     * Gets all registered Prefixes
     * 
     * @return collection of all registered namespace prefixes
     */
    Collection<String> getPrefixes();
}
