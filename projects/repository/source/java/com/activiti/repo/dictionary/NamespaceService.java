package com.activiti.repo.dictionary;

import java.util.Collection;

import com.activiti.repo.ref.NamespacePrefixResolver;


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
     * Activiti Namespace URI
     * 
     * TODO: Adjust with company name
     */
    public static final String ACTIVITI_URI = "http://www.activiti.com/1.0";
    
    /**
     * Activiti Namespace Prefix
     * 
     * TODO: Adjust with company name
     */
    public static final String ACTIVITI_PREFIX = "act";

    /**
     * Activiti Test Namespace URI
     * 
     * TODO: Adjust with company name
     */
    public static final String ACTIVITI_TEST_URI = "http://www.activiti.com/test/1.0";
    
    /**
     * Activiti Test Namespace Prefix
     * 
     * TODO: Adjust with company name
     */
    public static final String ACTIVITI_TEST_PREFIX = "test";

    
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
