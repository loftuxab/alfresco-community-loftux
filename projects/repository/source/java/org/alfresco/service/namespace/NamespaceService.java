package org.alfresco.service.namespace;

import java.util.Collection;



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
     * Alfresco Dictionary Namespace URI
     */
    public static final String ALFRESCO_DICTIONARY_URI = "http://www.alfresco.org/dictionary/0.1";
    
    /**
     * Alfresco Dictionary Namespace Prefix
     */
    public static final String ALFRESCO_DICTIONARY_PREFIX = "d";

    /**
     * Alfresco Namespace URI
     */
    public static final String ALFRESCO_URI = "http://www.alfresco.org/1.0";
    
    /**
     * Alfresco Namespace Prefix
     */
    public static final String ALFRESCO_PREFIX = "alf";

    
    /**
     * Gets all registered Namespace URIs
     * 
     * @return collection of all registered namespace URIs
     */
    Collection<String> getURIs();

  
    
}
