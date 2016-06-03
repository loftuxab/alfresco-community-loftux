package org.alfresco.service.namespace;

import java.io.Serializable;
import java.util.Collection;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.Auditable;
import org.alfresco.service.PublicService;

/**
 * The <code>NamespacePrefixResolver</code> provides a mapping between
 * namespace prefixes and namespace URIs.
 * 
 * @author David Caruana
 */
@AlfrescoPublicApi
public interface NamespacePrefixResolver 
{
    /**
     * Gets the namespace URI registered for the given prefix
     * 
     * @param prefix  prefix to lookup
     * @return  the namespace
     * @throws NamespaceException  if prefix has not been registered  
     */
    @Auditable(parameters = {"prefix"})
    public String getNamespaceURI(String prefix)
        throws NamespaceException;
    
    /**
     * Gets the registered prefixes for the given namespace URI
     * 
     * @param namespaceURI  namespace URI to lookup
     * @return  the prefixes (or empty collection, if no prefixes registered against URI)
     * @throws NamespaceException  if URI has not been registered 
     */
    @Auditable(parameters = {"namespaceURI"})
    public Collection<String> getPrefixes(String namespaceURI)
        throws NamespaceException;
    
    /**
     * Gets all registered Prefixes
     * 
     * @return collection of all registered namespace prefixes
     */
    @Auditable
    Collection<String> getPrefixes();

    /**
     * Gets all registered Uris
     * 
     * @return collection of all registered namespace uris
     */
    @Auditable
    Collection<String> getURIs();
}
