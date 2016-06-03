package org.alfresco.service.namespace;

import java.io.Serializable;

/**
 * Provides support for serializable objects such as the QNameMap that require a
 * NamespacePrefixResolver to be available. Ensures that the objects can remain
 * serializable themselves and still maintain a valid NamespacePrefixResolver.
 * 
 * @author Kevin Roast
 */
public interface NamespacePrefixResolverProvider extends Serializable
{
    /**
     * Get an object that implements the NamespacePrefixResolver interface
     * 
     * @return NamespacePrefixResolver
     */
    NamespacePrefixResolver getNamespacePrefixResolver();
}