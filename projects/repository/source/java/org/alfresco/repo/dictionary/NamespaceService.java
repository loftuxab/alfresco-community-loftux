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
     * Alfresco Namespace URI
     */
    public static final String ALFRESCO_URI = "http://www.alfresco.org/1.0";
    
    /**
     * Alfresco Namespace Prefix
     */
    public static final String ALFRESCO_PREFIX = "alf";

    /**
     * Alfresco Test Namespace URI
     */
    public static final String ALFRESCO_TEST_URI = "http://www.alfresco.org/test/1.0";
    
    /**
     * Alfresco Test Namespace Prefix
     */
    public static final String ALFRESCO_TEST_PREFIX = "alftest";

    
    /**
     * Gets all registered Namespace URIs
     * 
     * @return collection of all registered namespace URIs
     */
    Collection<String> getURIs();

  
    
}
