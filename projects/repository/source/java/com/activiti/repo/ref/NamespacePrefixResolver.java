package com.activiti.repo.ref;


/**
 * The <code>NamespacePrefixResolver</code> provides a mapping between
 * QName prefixes and namespace URIs.
 * 
 * @author David Caruana
 */
public interface NamespacePrefixResolver
{

    /**
     * Gets the registered namespace URI for the given prefix
     * 
     * @param prefix  prefix to lookup
     * @return  the namespace
     * @throws NamespaceException  prefix has not been registered
     */
    public String getNamespaceURI(String prefix)
        throws NamespaceException;
    

    /**
     * Gets the registered prefix for the given namespace URI
     * 
     * @param namespaceURI  namespace URI to lookup
     * @return  the prefix
     * @throws NamespaceException  prefix has not been registered for URI
     */
    public String getPrefix(String namespaceURI)
        throws NamespaceException;
    
}
