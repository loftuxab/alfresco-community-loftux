package org.alfresco.repo.service;

import java.util.Collection;

import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.lock.LockService;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.version.VersionService;


/**
 * Registry of Repository Services.
 * 
 * Provides access to the public services of the Repository as well
 * descriptions of those services.
 * 
 * @author David Caruana
 */
public interface ServiceRegistry
{
    /**
     * Get Services provided by Repository
     *
     * @return  list of provided Services
     */
    Collection<QName> getServices();
    

    /**
     * Is Service Provided?
     * 
     * @param serviceName  name of service to test provision of
     * @return  true => provided, false => not provided
     */
    boolean isServiceProvided(QName service);
    

    /**
     * Get Service Meta Data
     *
     * @param serviceName  name of service to retrieve meta data for
     * @return  the service meta data
     */
    ServiceDescriptor getServiceDescriptor(QName service);
    

    /** 
     * Get Service Interface
     *
     * @param serviceName  name of service to retrieve
     * @return  the service interface
     */  
    Object getService(QName service);
    

    /**
     * @return  the node service (or null, if one is not provided)
     */
    NodeService getNodeService();
    

    /**
     * @return  the content service (or null, if one is not provided)
     */
    ContentService getContentService();
    

    /**
     * @return  the search service (or null, if one is not provided)
     */
    //SearchService getSearchService();
    

    /**
     * @return  the version service (or null, if one is not provided)
     */
    VersionService getVersionService();
    

    /**
     * @return  the lock service (or null, if one is not provided)
     */
    LockService getLockService();


    /**
     * @return  the dictionary service (or null, if one is not provided)
     */
    DictionaryService getDictionaryService();
      
}
