package org.alfresco.repo.dictionary;

import java.util.Collection;

import org.alfresco.repo.ref.NamespacePrefixResolver;


/**
 * Namespace Service.
 * 
 * The Namespace Service provides access to and definition of namespace
 * URIs and Prefixes. 
 * 
 * @author David Caruana
 */
public interface NamespaceService extends NamespacePrefixResolver
{

    /**
     * Default Namespace URI
     */
    public static final String DEFAULT_URI = "";
    
    /**
     * Default Namespace Prefix
     */
    public static final String DEFAULT_PREFIX = "";
    
    /**
     * alfresco Namespace URI
     * 
     * TODO: Adjust with company name
     */
    public static final String alfresco_URI = "http://www.alfresco.com/1.0";
    
    /**
     * alfresco Namespace Prefix
     * 
     * TODO: Adjust with company name
     */
    public static final String alfresco_PREFIX = "act";

    /**
     * alfresco Test Namespace URI
     * 
     * TODO: Adjust with company name
     */
    public static final String alfresco_TEST_URI = "http://www.alfresco.com/test/1.0";
    
    /**
     * alfresco Test Namespace Prefix
     * 
     * TODO: Adjust with company name
     */
    public static final String alfresco_TEST_PREFIX = "test";

    
    /**
     * Gets all registered Namespace URIs
     * 
     * @return collection of all registered namespace URIs
     */
    Collection<String> getURIs();

    /**
     * Gets all registered Prefixes
     * 
     * @return collection of all registered namespace prefixes
     */
    Collection<String> getPrefixes();
    
}
